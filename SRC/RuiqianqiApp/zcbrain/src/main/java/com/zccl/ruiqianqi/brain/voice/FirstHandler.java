package com.zccl.ruiqianqi.brain.voice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zccl.ruiqianqi.brain.R;
import com.zccl.ruiqianqi.brain.eventbus.MindBusEvent;
import com.zccl.ruiqianqi.domain.model.Robot;
import com.zccl.ruiqianqi.mind.voice.iflytek.beans.BaseInfo;
import com.zccl.ruiqianqi.mind.voice.iflytek.beans.DisplayBean;
import com.zccl.ruiqianqi.mind.voice.iflytek.beans.MoveBean;
import com.zccl.ruiqianqi.mind.voice.iflytek.beans.SwitchBean;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.presentation.presenter.HttpReqPresenter;
import com.zccl.ruiqianqi.presentation.presenter.PersistPresenter;
import com.zccl.ruiqianqi.presentation.presenter.MovePresenter;
import com.zccl.ruiqianqi.presentation.presenter.ReportPresenter;
import com.zccl.ruiqianqi.presentation.presenter.SocketPresenter;
import com.zccl.ruiqianqi.presentation.presenter.StatePresenter;
import com.zccl.ruiqianqi.presentation.view.translation.TranslateActivity;
import com.zccl.ruiqianqi.tools.CheckUtils;
import com.zccl.ruiqianqi.tools.JsonUtils;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.MYUIUtils;
import com.zccl.ruiqianqi.tools.MyAppUtils;

import org.eventbus.zccl.Event;
import org.greenrobot.eventbus.EventBus;

import static com.zccl.ruiqianqi.brain.eventbus.MindBusEvent.TransEvent.TRANS_FAILURE;
import static com.zccl.ruiqianqi.brain.eventbus.MindBusEvent.TransEvent.TRANS_SUCCESS;
import static com.zccl.ruiqianqi.config.MyConfig.INTENT_ACTION_STOP;
import static com.zccl.ruiqianqi.config.MyConfig.KEY_STOP_FROM;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_APP;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_DICT;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_DISPLAY;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_GAME;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_HABIT;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_HEALTH;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_MOVE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_MUSIC;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_MUSIC1;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_MUSIC_;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_MUTE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_OPERA;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_SMS;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_SQUARE_DANCE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_STORY;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_SWITCH;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_TRANSLATE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_TRANSLATE_;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_WATCH_TV;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_YYD_CAHT;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_YYD_CHAT;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.OP_BIND_QRCODE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.OP_CLOSE_SENSE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.OP_DOWNLOAD_QRCODE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.OP_OPEN_SENSE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.RESULT_ZERO;
import static com.zccl.ruiqianqi.plugin.voice.AbstractVoice.RecognizerCallback.LISTEN_ERROR;
import static com.zccl.ruiqianqi.plugin.voice.AbstractVoice.UnderstandCallback.UNDERSTAND_FAILURE;

/**
 * Created by ruiqianqi on 2017/3/21 0021.
 */

public class FirstHandler extends BaseHandler {

    // 类标志
    private static String TAG = FirstHandler.class.getSimpleName();
    // 安静语
    private String[] muteWords;

    public FirstHandler(Context context, RobotVoice robotVoice){
        super(context, robotVoice);
        muteWords = mContext.getResources().getStringArray(R.array.mute_words);
    }


    /**
     * 处理场景【场景循环监听，就意味着所有的结果都要发给场景，由场景处理之后发循环监听广播】
     * @param json     返回的理解结果
     * @param type     理解成功与失败
     * {@link com.zccl.ruiqianqi.plugin.voice.AbstractVoice.UnderstandCallback#UNDERSTAND_FAILURE}
     * {@link com.zccl.ruiqianqi.plugin.voice.AbstractVoice.UnderstandCallback#UNDERSTAND_SUCCESS}
     *
     * @return true代表处理了，false代表没处理，继续走流程
     */
    @Override
    public boolean handlerScene(String json, int type){

        BaseInfo baseInfo = JsonUtils.parseJson(json, BaseInfo.class);

        StatePresenter sp = StatePresenter.getInstance();
        String scene = sp.getScene();

        LogUtils.e(TAG, "handlerScene = " + scene);

        // 如果当前是音乐场景
        if(SCENE_MUSIC.equals(scene)){

            // 理解出错
            if(UNDERSTAND_FAILURE == type){

            }else {
                if(null != baseInfo){
                    String funcType = baseInfo.getServiceType();
                    String words = baseInfo.getText();
                    Bundle bundle = new Bundle();

                    // 再次搜索音乐、儿歌、
                    // 戏曲、
                    // 故事、
                    // 广场舞、
                    // 健康养生、
                    // 习惯养成
                    if(/*FUNC_MUSIC.equals(funcType) || FUNC_MUSIC_.equals(funcType) ||*/
                            FUNC_OPERA.equals(funcType) ||
                            FUNC_STORY.equals(funcType) ||
                            FUNC_SQUARE_DANCE.equals(funcType) ||
                            FUNC_HEALTH.equals(funcType)||
                            FUNC_HABIT.equals(funcType)){
                        bundle.putString(PLAYER_CATEGORY_KEY, PLAYER_CATEGORY_MUSIC_SEARCH);
                        bundle.putString(PLAYER_RESULT_KEY, json);
                    }
                    // 音乐控制
                    else {
                        bundle.putString(PLAYER_CATEGORY_KEY, PLAYER_CATEGORY_MUSIC_CTRL);
                        bundle.putString(PLAYER_RESULT_KEY, words);
                    }
                    MyAppUtils.sendBroadcast(mContext, ACTION_PLAYER, bundle);
                }
                // 返回的数据解析出错，这个可能性不大，一旦出错就意味着讯飞服务器返回出错了
                else {

                }
            }

            return true;

        }

        // 如果当前是翻译场景
        else if(SCENE_TRANS.equals(scene)){

            MindBusEvent.TransEvent transEvent = new MindBusEvent.TransEvent();
            // 理解出错
            if(UNDERSTAND_FAILURE == type){
                transEvent.setType(TRANS_FAILURE);
                transEvent.setText(json);
            }else {
                if(null != baseInfo){
                    transEvent.setType(TRANS_SUCCESS);
                    transEvent.setText(baseInfo.getText());
                }
                // 返回的数据解析出错，这个可能性不大，一旦出错就意味着讯飞服务器返回出错了
                else {
                    transEvent.setType(TRANS_FAILURE);
                }
            }
            EventBus.getDefault().post(transEvent);
            return true;

        }

        if(null != getSuccessor()){
            return getSuccessor().handlerScene(json, type);
        }
        return false;
    }

    /**
     * 语义理解的处理
     * @param funcType    功能类型
     * @param json         科大讯飞返回的完整数据
     */
    @Override
    public void handleSemantic(String funcType, String json) {

        BaseInfo baseInfo = JsonUtils.parseJson(json, BaseInfo.class);
        if (null == baseInfo) {
            return;
        }

        // 显示录音文字
        if(PersistPresenter.getInstance().isShowWords()){
            MYUIUtils.showToast(mContext, baseInfo.getText());
        }

        boolean testYuDian = true;
        if(FUNC_MUSIC.equals(funcType) || FUNC_MUSIC_.equals(funcType)){
            // 传给讯飞语点
            flyTekYuDian(baseInfo.getText());
            return;
        }

        // 没有找到对应的语义
        if(RESULT_ZERO != baseInfo.getSuccess()){
            // 在这里获取薄言豆豆的对话语义
            StatePresenter sp = StatePresenter.getInstance();
            Robot robot = sp.getRobot();
            if(null != robot){
                HttpReqPresenter htp = new HttpReqPresenter(mContext);
                htp.queryBoYan(baseInfo.getText(), robot.getRid(), robot.getRname());
            }
            return;
        }

        // 播放音乐、儿歌、
        // 戏曲、
        // 故事、
        // 广场舞、
        // 健康养生、
        // 习惯养成
        if(/*FUNC_MUSIC.equals(funcType) || FUNC_MUSIC_.equals(funcType) || */
                FUNC_OPERA.equals(funcType) ||
                FUNC_STORY.equals(funcType) ||
                FUNC_SQUARE_DANCE.equals(funcType) ||
                FUNC_HEALTH.equals(funcType)||
                FUNC_HABIT.equals(funcType)){
            Bundle bundle = new Bundle();
            bundle.putString(PLAYER_CATEGORY_KEY, PLAYER_CATEGORY_MUSIC);
            bundle.putString(PLAYER_RESULT_KEY, json);
            MyAppUtils.sendBroadcast(mContext, ACTION_PLAYER, bundle);
        }

        // 机器人移动
        else if(FUNC_MOVE.equals(funcType)){
            MoveBean moveBean = JsonUtils.parseJson(json, MoveBean.class);
            if(null != moveBean && null != moveBean.semantic && null != moveBean.semantic.slots){
                MovePresenter.getInstance().parseFlytekData(moveBean.semantic.slots.direct);
            }else {
                ReportPresenter.report("");
            }
        }

        // 切换版本
        else if(FUNC_SWITCH.equals(funcType)){
            SwitchBean switchBean = JsonUtils.parseJson(json, SwitchBean.class);
            if(null != switchBean && null != switchBean.semantic && null != switchBean.semantic.slots){
                SocketPresenter.getInstance().switchToServer(switchBean.semantic.slots.server_type);
            }else {
                ReportPresenter.report("");
            }
        }

        // 是否显示录音文本
        else if(FUNC_DISPLAY.equals(funcType)){
            DisplayBean displayBean = JsonUtils.parseJson(json, DisplayBean.class);
            if(null != displayBean && null != displayBean.semantic && null != displayBean.semantic.slots){
                if(DisplayBean.DISPLAY.equals(displayBean.semantic.slots.display)){
                    PersistPresenter.getInstance().setShowWords(true);
                }else {
                    PersistPresenter.getInstance().setShowWords(false);
                }
            }else {
                ReportPresenter.report("");
            }
        }

        // 安静
        else if(FUNC_MUTE.equals(funcType)){
            // 让其他应用安静
            Bundle args = new Bundle();
            args.putString(KEY_STOP_FROM, FUNC_MUTE);
            MyAppUtils.sendBroadcast(mContext, INTENT_ACTION_STOP, args);

            // 让自己安静
            mRobotVoice.cancelUnderstand();
            mRobotVoice.cancelRecognizer();
            String word = muteWords[CheckUtils.getRandom(muteWords.length)];
            mRobotVoice.startTTS(word, "", new AbstractVoice.SynthesizerCallback() {
                @Override
                public void OnBegin() {

                }

                @Override
                public void OnPause() {

                }

                @Override
                public void OnResume() {

                }

                @Override
                public void OnComplete(Throwable throwable, String tag) {
                    mRobotVoice.stopTTS();
                }
            });
        }

        // 打开未读短信
        else if(FUNC_SMS.equals(funcType)){
            MyAppUtils.openDialRecord(mContext);

        }

        // 打开多媒体
        else if(FUNC_WATCH_TV.equals(funcType)){

        }

        // 打开各类界面
        else if(FUNC_APP.equals(funcType)){

        }

        // 打开游戏
        else if(FUNC_GAME.equals(funcType)){

        }

        // 红外感应的开关、二维码、打开测试界面
        /*
        else if(FUNC_YYD_CHAT.equals(funcType)){
            String op = baseInfo.getOperation();
            if(OP_OPEN_SENSE.equals(op)){

            }
            else if(OP_CLOSE_SENSE.equals(op)){

            }
            else if(OP_BIND_QRCODE.equals(op)){

            }
            else if(OP_DOWNLOAD_QRCODE.equals(op)){

            }
        }
        */

        // 打开、关闭投影
        else if(FUNC_YYD_CAHT.equals(funcType)){

        }

        // 翻译【直接打开翻译界面】
        else if(FUNC_TRANSLATE.equals(funcType) || FUNC_TRANSLATE_.equals(funcType)){
            Intent intent = new Intent(mContext, TranslateActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            return;
        }

        // 开启英业达字典
        else if(FUNC_DICT.equals(funcType)){
            MyAppUtils.openApp(mContext, mContext.getString(R.string.yingyeda));
        }

        // 其他发送出去
        else {
            if(null != getSuccessor()){
                getSuccessor().handleSemantic(funcType, json);
            }
        }
    }

    /**
     * 语音识别的处理
     * @param asr
     * @param type
     * {@link com.zccl.ruiqianqi.plugin.voice.AbstractVoice.RecognizerCallback#LISTEN_ERROR}
     * {@link com.zccl.ruiqianqi.plugin.voice.AbstractVoice.RecognizerCallback#LISTEN}
     * {@link com.zccl.ruiqianqi.plugin.voice.AbstractVoice.RecognizerCallback#ONLINE_WORD}
     * {@link com.zccl.ruiqianqi.plugin.voice.AbstractVoice.RecognizerCallback#OFFLINE_WORD}
     * {@link com.zccl.ruiqianqi.plugin.voice.AbstractVoice.RecognizerCallback#LISTEN_UNDERSTAND}
     */
    @Override
    public void handleAsr(String asr, int type) {

        StatePresenter sp = StatePresenter.getInstance();
        // 翻译中
        if(SCENE_TRANS.equals(sp.getScene())){
            MindBusEvent.TransEvent transEvent = new MindBusEvent.TransEvent();
            if(LISTEN_ERROR == type){
                type = TRANS_FAILURE;
            }else {
                type = TRANS_SUCCESS;
            }
            transEvent.setType(type);
            transEvent.setText(asr);
            EventBus.getDefault().post(transEvent);
        }

    }

    /**
     * 直接功能的处理
     * @param func
     */
    @Override
    public void handlerFunc(String func) {
        if(null != getSuccessor()){
            getSuccessor().handlerFunc(func);
        }
    }


    /**
     * 识别以文字的形式传给语点
     * @param text
     */
    private void flyTekYuDian(String text){
        // 把语义识别原文传给讯飞语点
        // 然后再按讯飞语点的逻辑走流程
        Intent intent = new Intent("com.iflytek.xiri2.START");
        // 讯飞语点包名
        intent.setPackage("com.iflytek.xiri");
        intent.putExtra("text", text);
        intent.putExtra("startmode", "text");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startService(intent);
    }

}
