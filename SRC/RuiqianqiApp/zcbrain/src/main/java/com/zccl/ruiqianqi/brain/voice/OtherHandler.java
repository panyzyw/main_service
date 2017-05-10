package com.zccl.ruiqianqi.brain.voice;

import android.content.Context;
import android.os.Bundle;

import com.zccl.ruiqianqi.brain.R;
import com.zccl.ruiqianqi.presentation.presenter.StatePresenter;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.MyAppUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.zccl.ruiqianqi.config.MyConfig.INTENT_ACTION_OVERALL;
import static com.zccl.ruiqianqi.config.MyConfig.KEY_OVERALL_RESULT;
import static com.zccl.ruiqianqi.config.MyConfig.KEY_RESULT;
import static com.zccl.ruiqianqi.config.MyConfig.KEY_VIDEO;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_ARITHMETIC;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_BATTERY;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_CALL;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_CAMERA;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_CHAT;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_COOKBOOK;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_DANCE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_ENCYCLOPEDIAS;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_FACE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_HABIT;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_HEALTH;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_JOKE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_MAP;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_MOVIE_INFO;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_NEWS;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_OPERA;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_PHOTO;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_QUESTION;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_SCHEDULE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_SHUTDOWN;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_SINOLOGY;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_SMART_HOME;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_SOUND;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_SQUARE_DANCE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_STOCK;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_STORY;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_STUDY;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_TRANSLATION;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_WEATHER;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_YYDCHAT;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_ARITHMETIC;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_BAI_KE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_BATTERY;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_CALL;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_CAMERA;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_CHAT;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_COOKBOOK;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_DANCE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_DATETIME;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_FACE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_FAQ;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_HABIT;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_HEALTH;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_JOKE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_MAP;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_MOVIE_INFO;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_NEWS;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_OPEN_QA;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_OPERA;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_PHOTO;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_QUSETION;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_SCHEDULE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_SHUTDOWN;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_SINOLOGY;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_SMART_HOME;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_SOUND;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_SQUARE_DANCE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_STOCK;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_STORY;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_STUDY;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_TRANSLATE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_TRANSLATE_;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_WEATHER;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_YYD_CHAT;

/**
 * Created by ruiqianqi on 2017/3/21 0021.
 */

public class OtherHandler extends BaseHandler {

    // 类标志
    private static String TAG = OtherHandler.class.getSimpleName();
    // 服务对应的广播
    private Map<String, String> intentMap;

    public OtherHandler(Context context, RobotVoice robotVoice){
        super(context, robotVoice);
        intentMap = new HashMap<>();
        init();
    }

    @Override
    public boolean handlerScene(String json, int type) {
        return false;
    }

    /**
     * 初始化
     */
    private void init(){

        // 打电话
        intentMap.put(FUNC_CALL, INTENT_CALL);
        // 提醒
        intentMap.put(FUNC_SCHEDULE, INTENT_SCHEDULE);
        // 地图
        intentMap.put(FUNC_MAP, INTENT_MAP);
        // 股票
        intentMap.put(FUNC_STOCK, INTENT_STOCK);
        // 天气
        intentMap.put(FUNC_WEATHER, INTENT_WEATHER);
        // 音乐
        //intentMap.put(FUNC_MUSIC, INTENT_MUSIC);
        // 菜谱
        intentMap.put(FUNC_COOKBOOK, INTENT_COOKBOOK);

        // 广场舞
        intentMap.put(FUNC_SQUARE_DANCE, INTENT_SQUARE_DANCE);
        // 诗词学习
        intentMap.put(FUNC_STUDY, INTENT_STUDY);
        // 国学知识
        intentMap.put(FUNC_SINOLOGY, INTENT_SINOLOGY);
        // 影视资讯
        intentMap.put(FUNC_MOVIE_INFO, INTENT_MOVIE_INFO);
        // 戏曲
        intentMap.put(FUNC_OPERA, INTENT_OPERA);
        // 健康养生
        intentMap.put(FUNC_HEALTH, INTENT_HEALTH);
        // 故事
        intentMap.put(FUNC_STORY, INTENT_STORY);
        // 跳舞
        intentMap.put(FUNC_DANCE, INTENT_DANCE);
        // 拍照
        intentMap.put(FUNC_CAMERA, INTENT_CAMERA);
        // 笑话
        intentMap.put(FUNC_JOKE, INTENT_JOKE);
        // 新闻
        intentMap.put(FUNC_NEWS, INTENT_NEWS);
        // 习惯养成
        intentMap.put(FUNC_HABIT, INTENT_HABIT);
        // 算术
        intentMap.put(FUNC_ARITHMETIC, INTENT_ARITHMETIC);
        // 百科
        intentMap.put(FUNC_BAI_KE, INTENT_ENCYCLOPEDIAS);
        // 日期
        intentMap.put(FUNC_DATETIME, INTENT_CHAT);
        // 社区问答
        intentMap.put(FUNC_FAQ, INTENT_CHAT);
        // 开放语义
        intentMap.put(FUNC_OPEN_QA, INTENT_CHAT);
        // 聊天
        intentMap.put(FUNC_CHAT, INTENT_CHAT);

        // 智能家居、电视、机顶盒、影碟机、电视盒子、功放音响、投影仪
        intentMap.put(FUNC_SMART_HOME, INTENT_SMART_HOME);
        // 跟机器人说学习算术
        intentMap.put(FUNC_QUSETION, INTENT_QUESTION);

        // 人脸识别
        intentMap.put(FUNC_FACE, INTENT_FACE);
        // 音量控制
        intentMap.put(FUNC_SOUND, INTENT_SOUND);
        // 电量
        intentMap.put(FUNC_BATTERY, INTENT_BATTERY);
        // 关机、重启
        intentMap.put(FUNC_SHUTDOWN, INTENT_SHUTDOWN);
        // 相册
        intentMap.put(FUNC_PHOTO, INTENT_PHOTO);

        // 翻译
        intentMap.put(FUNC_TRANSLATE_, INTENT_TRANSLATION);
        // 翻译
        intentMap.put(FUNC_TRANSLATE, INTENT_TRANSLATION);

        // 二维码
        intentMap.put(FUNC_YYD_CHAT, INTENT_YYDCHAT);
    }

    /**
     * 语义理解的处理
     * @param funcType    功能类型
     * @param json         科大讯飞返回的完整数据
     */
    @Override
    public void handleSemantic(String funcType, String json) {

        if(StringUtils.isEmpty(funcType))
            return;

        if(StringUtils.isEmpty(json))
            return;


        // 全局发送【主要是为了调试查看】
        Bundle bundle = new Bundle();
        bundle.putString(KEY_OVERALL_RESULT, json);
        MyAppUtils.sendBroadcast(mContext, INTENT_ACTION_OVERALL, bundle);


        StatePresenter sp = StatePresenter.getInstance();
        // 功能发送【发给master进行处理】
        String action = intentMap.get(funcType);
        if(!StringUtils.isEmpty(action)) {
            Bundle args = new Bundle();
            args.putString(KEY_RESULT, json);
            if(sp.isVideoing()){
                args.putString(KEY_VIDEO, "open");
            }else {
                args.putString(KEY_VIDEO, "close");
            }
            MyAppUtils.sendBroadcast(mContext, action, args);

            LogUtils.e(TAG, KEY_VIDEO + " = " + sp.isVideoing());
        }else {

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

    }

    /**
     * 直接功能的处理
     * @param func
     */
    @Override
    public void handlerFunc(String func) {
        // 摸肩跳舞
        if(mContext.getString(R.string.sensor_dance).equals(func)){
            // 功能发送【发给master进行处理】
            String action = intentMap.get(func);
            if(!StringUtils.isEmpty(action)) {
                Bundle args = new Bundle();
                args.putString(KEY_RESULT, "-1");
                args.putString(KEY_VIDEO, StatePresenter.getInstance().isVideoing() + "");
                MyAppUtils.sendBroadcast(mContext, action, args);
            }else {

            }
        }
    }

}
