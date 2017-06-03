package com.zccl.ruiqianqi.brain.voice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.text.TextUtils;

import com.zccl.ruiqianqi.brain.R;
import com.zccl.ruiqianqi.brain.eventbus.MindBusEvent;
import com.zccl.ruiqianqi.domain.model.Robot;
import com.zccl.ruiqianqi.mind.voice.iflytek.beans.BaseInfo;
import com.zccl.ruiqianqi.mind.voice.iflytek.beans.DisplayBean;
import com.zccl.ruiqianqi.mind.voice.iflytek.beans.GenericBean;
import com.zccl.ruiqianqi.mind.voice.iflytek.beans.MoveBean;
import com.zccl.ruiqianqi.mind.voice.iflytek.beans.SwitchBean;
import com.zccl.ruiqianqi.presentation.presenter.GenericPresenter;
import com.zccl.ruiqianqi.presentation.presenter.HttpReqPresenter;
import com.zccl.ruiqianqi.presentation.presenter.MovePresenter;
import com.zccl.ruiqianqi.presentation.presenter.PersistPresenter;
import com.zccl.ruiqianqi.presentation.presenter.ReportPresenter;
import com.zccl.ruiqianqi.presentation.presenter.SocketPresenter;
import com.zccl.ruiqianqi.presentation.presenter.StatePresenter;
import com.zccl.ruiqianqi.presentation.presenter.MusicPresenter;
import com.zccl.ruiqianqi.presentation.view.translation.TranslateActivity;
import com.zccl.ruiqianqi.tools.CheckUtils;
import com.zccl.ruiqianqi.tools.JsonUtils;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.MYUIUtils;
import com.zccl.ruiqianqi.tools.MyAppUtils;
import com.zccl.ruiqianqi.tools.SystemUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import static com.zccl.ruiqianqi.brain.eventbus.MindBusEvent.TransEvent.TRANS_FAILURE;
import static com.zccl.ruiqianqi.brain.eventbus.MindBusEvent.TransEvent.TRANS_SUCCESS;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_CAMERA;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_GAME;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_HOME;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_LAUNCHER;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_MUSIC;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_PHOTOS;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_PICTURE;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_POLE;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_QRCODE_SCAN;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_SETTINGS;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_SHUTDOWN;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_TOOLS;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_UPDATE_WORDS;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_VIDEO;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_VOICE_DOWN;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_VOICE_UP;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_APP;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_DICT;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_DISPLAY;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_GAME;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_GENERIC;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_HABIT;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_HEALTH;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_MOVE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_MUSIC;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_MUSIC_;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_MUTE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_OPERA;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_SMART_HOME;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_SMS;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_SQUARE_DANCE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_STORY;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_SWITCH;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_TRANSLATE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_TRANSLATE_;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_TV_CONTROL;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_VIDEO;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_WATCH_TV;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_YYD_CAHT;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.RESULT_ZERO;
import static com.zccl.ruiqianqi.plugin.voice.AbstractVoice.RecognizerCallback.OFFLINE_WORD;
import static com.zccl.ruiqianqi.plugin.voice.AbstractVoice.UnderstandCallback.UNDERSTAND_SUCCESS;

/**
 * Created by ruiqianqi on 2017/3/21 0021.
 */

public class FirstHandler extends BaseHandler {

    // 类标志
    private static String TAG = FirstHandler.class.getSimpleName();
    // 安静语
    private String[] muteWords;
    // 用不用讯飞音乐
    private String isUseXiriKey;

    public FirstHandler(Context context, RobotVoice robotVoice){
        super(context, robotVoice);
        muteWords = mContext.getResources().getStringArray(R.array.mute_words);

        isUseXiriKey = mContext.getString(R.string.is_use_xiri);

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

        StatePresenter sp = StatePresenter.getInstance();
        String scene = sp.getScene();
        LogUtils.e(TAG, "handlerSceneA = " + scene);

        /*
        //【音乐场景】
        if(SCENE_MUSIC.equals(scene)){
            if(!SystemUtils.isAppForeRunning(mContext, "com.yongyida.robot.player")){
                sp.handleScene(SCENE_MUSIC, false);
                scene = sp.getScene();
            }
        }
        LogUtils.e(TAG, "handlerSceneB = " + scene);
        */


        if(UNDERSTAND_SUCCESS == type){
            BaseInfo baseInfo = JsonUtils.parseJson(json, BaseInfo.class);
            if (null == baseInfo) {
                return false;
            }

            //【显示录音文字】
            if(PersistPresenter.getInstance().isShowWords()){
                MYUIUtils.showToast(mContext, baseInfo.getText());
            }

            //【音乐场景】
            if(SCENE_MUSIC.equals(scene)){
                if(null != baseInfo){

                    String funcType = baseInfo.getServiceType();

                    // 用讯飞音乐，我就不处理音乐了
                    boolean isUseXiri = SystemProperties.getBoolean(isUseXiriKey, false);

                    // 自有播放器，过滤条件
                    boolean isFilter;

                    if(isUseXiri){
                        // 自有播放器界面时，音乐转给讯飞处理【音乐、儿歌】
                        if(FUNC_MUSIC.equals(funcType) || FUNC_MUSIC_.equals(funcType)){
                            // 传给讯飞语点
                            MusicPresenter xiriPresenter = new MusicPresenter();
                            xiriPresenter.flyTekYuDian(json);

                            // 还有关掉自有播放器
                            Bundle bundle = new Bundle();
                            bundle.putString(PLAYER_CATEGORY_KEY, MUSIC_CONTROL);
                            bundle.putString(PLAYER_RESULT_KEY, "退出");
                            MyAppUtils.sendBroadcast(mContext, ACTION_PLAYER, bundle);
                            return true;
                        }

                        //【戏曲、故事、广场舞、健康养生、习惯养成】
                        isFilter = FUNC_OPERA.equals(funcType) || FUNC_STORY.equals(funcType) ||
                                   FUNC_SQUARE_DANCE.equals(funcType) || FUNC_HEALTH.equals(funcType)||
                                   FUNC_HABIT.equals(funcType);
                    }else {

                        isFilter = FUNC_MUSIC.equals(funcType) || FUNC_MUSIC_.equals(funcType) ||
                                   FUNC_OPERA.equals(funcType) || FUNC_STORY.equals(funcType) ||
                                   FUNC_SQUARE_DANCE.equals(funcType) || FUNC_HEALTH.equals(funcType)||
                                   FUNC_HABIT.equals(funcType);
                    }

                    String words = baseInfo.getText();
                    Bundle bundle = new Bundle();

                    // 音乐搜索，【GSON解析】
                    if(isFilter){
                        bundle.putString(PLAYER_CATEGORY_KEY, MUSIC_SEARCH);
                        bundle.putString(PLAYER_RESULT_KEY, json);
                    }
                    // 音乐控制，【正则匹配】
                    else {
                        bundle.putString(PLAYER_CATEGORY_KEY, MUSIC_CONTROL);
                        bundle.putString(PLAYER_RESULT_KEY, words);
                    }
                    MyAppUtils.sendBroadcast(mContext, ACTION_PLAYER, bundle);
                }
                // 返回的数据解析出错，这个可能性不大，一旦出错就意味着讯飞服务器返回出错了
                else {

                }
                return true;
            }
            // 【翻译场景】
            else if(SCENE_TRANS.equals(scene)){
                MindBusEvent.TransEvent transEvent = new MindBusEvent.TransEvent();
                if(null != baseInfo){
                    transEvent.setType(TRANS_SUCCESS);
                    transEvent.setText(baseInfo.getText());
                }
                // 返回的数据解析出错，这个可能性不大，一旦出错就意味着讯飞服务器返回出错了
                else {
                    transEvent.setType(TRANS_FAILURE);
                }
                EventBus.getDefault().post(transEvent);
                return true;
            }

        }else {
            // 如果当前是音乐场景
            if(SCENE_MUSIC.equals(scene)){
                return true;
            }
            // 如果当前是翻译场景
            else if(SCENE_TRANS.equals(scene)){
                MindBusEvent.TransEvent transEvent = new MindBusEvent.TransEvent();
                transEvent.setType(TRANS_FAILURE);
                transEvent.setText(json);
                EventBus.getDefault().post(transEvent);
                return true;
            }
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

        boolean isUseXiri = SystemProperties.getBoolean(isUseXiriKey, false);
        boolean testYuDian = false;
        if(isUseXiri && (testYuDian ||
                FUNC_MUSIC.equals(funcType) || FUNC_MUSIC_.equals(funcType) ||
                FUNC_VIDEO.equals(funcType) || FUNC_SMART_HOME.equals(funcType) ||
                FUNC_TV_CONTROL.equals(funcType))){
            // 传给讯飞语点
            MusicPresenter xiriPresenter = new MusicPresenter();
            xiriPresenter.flyTekYuDian(json);
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
        boolean filter = FUNC_MUSIC.equals(funcType) || FUNC_MUSIC_.equals(funcType) ||
                FUNC_OPERA.equals(funcType) || FUNC_STORY.equals(funcType) ||
                FUNC_SQUARE_DANCE.equals(funcType) || FUNC_HEALTH.equals(funcType)||
                FUNC_HABIT.equals(funcType);

        // 用讯飞音乐，我就不处理音乐了
        if(isUseXiri){
            filter = FUNC_OPERA.equals(funcType) || FUNC_STORY.equals(funcType) ||
                    FUNC_SQUARE_DANCE.equals(funcType) || FUNC_HEALTH.equals(funcType)||
                    FUNC_HABIT.equals(funcType);
        }

        if(filter){
            Bundle bundle = new Bundle();
            bundle.putString(PLAYER_CATEGORY_KEY, MUSIC_PLAY);
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
            mRobotVoice.stopOtherAppFunc(FUNC_MUTE);

            // 让自己安静
            mRobotVoice.cancelUnderstand();
            mRobotVoice.cancelRecognizer();
            String word = muteWords[CheckUtils.getRandom(muteWords.length)];
            mRobotVoice.startTTS(word, new Runnable() {
                @Override
                public void run() {
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

        // 如果是通用功能
        else if(FUNC_GENERIC.equals(funcType)){
            GenericBean genericBean = JsonUtils.parseJson(json, GenericBean.class);
            if(null != genericBean && null != genericBean.semantic && null != genericBean.semantic.slots){
                GenericPresenter genericPresenter = new GenericPresenter();
                genericPresenter.genericOperator(genericBean);
            }else {
                ReportPresenter.report("");
            }
            return;
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

        LogUtils.e(TAG, asr + "");
        if(type == OFFLINE_WORD){

            Map<String, String> offLineMap = JsonUtils.parseJson(asr, Map.class);
            if(null != offLineMap){

                String text = offLineMap.get("text");
                if(!TextUtils.isEmpty(text)){

                    if(text.contains("歪发")){
                        text = text.replace("歪发", "WIFI");
                    }

                    // 显示录音文字
                    if(PersistPresenter.getInstance().isShowWords()){
                        MYUIUtils.showToast(mContext, text);
                    }
                }

                String funcStr = null;
                String funcIndex = "0";

                if(offLineMap.containsKey(OFF_LINE_MUSIC)){
                    funcStr = OFF_LINE_MUSIC;

                }else if(offLineMap.containsKey(OFF_LINE_VIDEO)){
                    funcStr = OFF_LINE_VIDEO;

                }else if(offLineMap.containsKey(OFF_LINE_PICTURE) || offLineMap.containsKey(OFF_LINE_CAMERA)){
                    funcStr = OFF_LINE_CAMERA;

                }else if(offLineMap.containsKey(OFF_LINE_SHUTDOWN)){
                    funcStr = OFF_LINE_SHUTDOWN;

                }else if(offLineMap.containsKey(OFF_LINE_PHOTOS)){
                    funcStr = OFF_LINE_PHOTOS;

                }else if(offLineMap.containsKey(OFF_LINE_GAME)){
                    funcStr = OFF_LINE_GAME;

                }else if(offLineMap.containsKey(OFF_LINE_QRCODE_SCAN)){
                    funcStr = OFF_LINE_QRCODE_SCAN;
                    funcIndex = offLineMap.get(OFF_LINE_QRCODE_SCAN);

                }else if(offLineMap.containsKey(OFF_LINE_SETTINGS)) {
                    funcStr = OFF_LINE_SETTINGS;
                    funcIndex = offLineMap.get(OFF_LINE_SETTINGS);

                }else if(offLineMap.containsKey(OFF_LINE_TOOLS)){
                    funcStr = OFF_LINE_TOOLS;
                    funcIndex = offLineMap.get(OFF_LINE_TOOLS);

                }else if(offLineMap.containsKey(OFF_LINE_LAUNCHER)){
                    funcStr = OFF_LINE_LAUNCHER;
                    funcIndex = offLineMap.get(OFF_LINE_LAUNCHER);

                }else if(offLineMap.containsKey(OFF_LINE_HOME)){
                    funcStr = OFF_LINE_HOME;
                    funcIndex = offLineMap.get(OFF_LINE_HOME);

                }else if(offLineMap.containsKey(OFF_LINE_POLE)){
                    funcStr = OFF_LINE_POLE;
                    funcIndex = offLineMap.get(OFF_LINE_POLE);

                }else if(offLineMap.containsKey(OFF_LINE_VOICE_UP)){
                    funcStr = OFF_LINE_VOICE_UP;

                }else if(offLineMap.containsKey(OFF_LINE_VOICE_DOWN)){
                    funcStr = OFF_LINE_VOICE_DOWN;

                }else if(offLineMap.containsKey(OFF_LINE_UPDATE_WORDS)){
                    funcStr = OFF_LINE_UPDATE_WORDS;
                    funcIndex = offLineMap.get(OFF_LINE_UPDATE_WORDS);

                }

                try {
                    type = Integer.parseInt(funcIndex);
                }catch (NumberFormatException e){
                    type = 0;
                }
                if(null != getSuccessor()){
                    getSuccessor().handleAsr(funcStr, type);
                }
            }

        }else {

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



}
