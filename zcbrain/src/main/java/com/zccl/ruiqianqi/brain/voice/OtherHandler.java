package com.zccl.ruiqianqi.brain.voice;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;

import com.zccl.ruiqianqi.brain.R;
import com.zccl.ruiqianqi.mind.voice.iflytek.beans.ExpressionBean;
import com.zccl.ruiqianqi.presentation.presenter.GenericPresenter;
import com.zccl.ruiqianqi.presentation.presenter.StatePresenter;
import com.zccl.ruiqianqi.tools.JsonUtils;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.MyAppUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.tools.VoiceUtils;
import com.zccl.ruiqianqi.utils.AppUtils;

import java.util.HashMap;
import java.util.Map;

import static com.zccl.ruiqianqi.config.MyConfig.INTENT_ACTION_OVERALL;
import static com.zccl.ruiqianqi.config.MyConfig.KEY_OVERALL_RESULT;
import static com.zccl.ruiqianqi.config.MyConfig.KEY_RESULT;
import static com.zccl.ruiqianqi.config.MyConfig.KEY_VIDEO;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_CAMERA;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_GAME;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_HOME;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_LAUNCHER;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_MUSIC;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_PHOTOS;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_POLE;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_QRCODE_SCAN;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_SETTINGS;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_SHUTDOWN;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_TOOLS;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_UPDATE_WORDS;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_VIDEO;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_VOICE_DOWN;
import static com.zccl.ruiqianqi.config.MyConfig.OFF_LINE_VOICE_UP;
import static com.zccl.ruiqianqi.config.MyConfig.SEMANTIC_BIND_QR;
import static com.zccl.ruiqianqi.config.MyConfig.SEMANTIC_CAMERA;
import static com.zccl.ruiqianqi.config.MyConfig.SEMANTIC_DOWNLOAD_QR;
import static com.zccl.ruiqianqi.config.MyConfig.SEMANTIC_PHOTO;
import static com.zccl.ruiqianqi.config.MyConfig.SEMANTIC_SHUTDOWN;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_ARITHMETIC;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_BATTERY;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_CALL;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_CAMERA;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_CHAT;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_COOKBOOK;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_DANCE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_EMOTION_CHAT;
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
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_EMOTION_CHAT;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_FACE;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_FAQ;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_GENERIC;
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
    // 离线回答
    private String[] mAnswers;

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

        // 离线答案列表
        mAnswers = mContext.getResources().getStringArray(R.array.answers);

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

        // 表情聊天
        intentMap.put(FUNC_EMOTION_CHAT, INTENT_EMOTION_CHAT);
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


        // 如果是聊天
        if(FUNC_CHAT.equals(funcType)) {
            // 如果是表情聊天
            ExpressionBean expressionBean = JsonUtils.parseJson(json, ExpressionBean.class);
            if (null != expressionBean) {
                if ("emotionchat".equals(expressionBean.getOperation())) {
                    funcType = FUNC_EMOTION_CHAT;
                }
            }
        }

        doRemoteFunc(funcType, json);
    }

    /**
     * 执行远程任务
     * @param funcType
     * @param json
     */
    private void doRemoteFunc(String funcType, String json){

        // 不唱歌
        /*
        if(FUNC_DANCE.equals(funcType)){
            json = "-1";
        }
        */

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

        GenericPresenter genericPresenter = new GenericPresenter();
        if(OFF_LINE_MUSIC.equals(asr)){
            Bundle bundle = new Bundle();
            bundle.putInt("type", 1);
            bundle.putString("offline", "offline");
            MyAppUtils.startApp(mContext,
                    "com.yongyida.robot.resourcemanager",
                    "com.yongyida.robot.resourcemanager.activity.MusicAndImageActivity",
                    bundle);
        }
        else if(OFF_LINE_VIDEO.equals(asr)){
            Bundle bundle = new Bundle();
            bundle.putInt("type", 0);
            bundle.putString("offline", "offline");
            MyAppUtils.startApp(mContext,
                    "com.yongyida.robot.resourcemanager",
                    "com.yongyida.robot.resourcemanager.activity.VideoActivity",
                    bundle);
        }
        else if(OFF_LINE_CAMERA.equals(asr)){
            doRemoteFunc(FUNC_CAMERA, SEMANTIC_CAMERA);
        }
        else if(OFF_LINE_SHUTDOWN.equals(asr)){
            doRemoteFunc(FUNC_SHUTDOWN, SEMANTIC_SHUTDOWN);
        }
        else if(OFF_LINE_PHOTOS.equals(asr)){
            doRemoteFunc(FUNC_PHOTO, SEMANTIC_PHOTO);
        }
        else if(OFF_LINE_GAME.equals(asr)){
            genericPresenter.genericOperator(GenericPresenter.GAME);
        }
        else if(OFF_LINE_QRCODE_SCAN.equals(asr)){
            if(1 == type){
                doRemoteFunc(FUNC_YYD_CHAT, SEMANTIC_DOWNLOAD_QR);
            }else {
                doRemoteFunc(FUNC_YYD_CHAT, SEMANTIC_BIND_QR);
            }
        }
        else if(OFF_LINE_SETTINGS.equals(asr)){
            switch (type){
                case 1: // 设置
                    genericPresenter.genericOperator(GenericPresenter.SETTINGS);
                    break;
                case 2: // 网络设置
                    genericPresenter.genericOperator(GenericPresenter.NET_SETTINGS);
                    break;
                case 3: // WIFI
                    genericPresenter.genericOperator(GenericPresenter.WIFI);
                    break;
                case 4: // 蓝牙
                    genericPresenter.genericOperator(GenericPresenter.BLUE_TOOTH);
                    break;
                case 5: // 关于
                    genericPresenter.genericOperator(GenericPresenter.ABOUT);
                    break;
                case 6: // 飞行模式
                    genericPresenter.genericOperator(GenericPresenter.FLYING);
                    break;
                case 7: // 网络热点
                    genericPresenter.genericOperator(GenericPresenter.WIFI_HOT);
                    break;
                case 8: // 显示
                    genericPresenter.genericOperator(GenericPresenter.DISPLAY);
                    break;
                case 9: // 提示音
                    genericPresenter.genericOperator(GenericPresenter.SOUND);
                    break;
                case 10: // 位置信息
                    genericPresenter.genericOperator(GenericPresenter.LOCATION);
                    break;
                case 11: // 语言
                    genericPresenter.genericOperator(GenericPresenter.LANGUAGE);
                    break;
                case 12: // 日期
                    genericPresenter.genericOperator(GenericPresenter.DATE_TIME);
                    break;
                case 13: // 出厂设置
                    genericPresenter.genericOperator(GenericPresenter.BACK_TO_FACTORY);
                    break;
                case 14: // 存储状况
                    genericPresenter.genericOperator(GenericPresenter.STORAGE);
                    break;
                case 15: // 电池电量
                    genericPresenter.genericOperator(GenericPresenter.BATTERY);
                    break;
                case 16: // 耗电信息
                    genericPresenter.genericOperator(GenericPresenter.BATTERY_USAGE);
                    break;
                case 17: // 应用列表
                    genericPresenter.genericOperator(GenericPresenter.APP_LIST);
                    break;

                default:
                    break;
            }
        }
        else if(OFF_LINE_TOOLS.equals(asr)){
            switch (type){
                case 1: // 下载
                    genericPresenter.genericOperator(GenericPresenter.DOWNLOAD);
                    break;
                case 2: // 信息
                    genericPresenter.genericOperator(GenericPresenter.MMS);
                    break;
                case 3: // 录音机
                    genericPresenter.genericOperator(GenericPresenter.RECORDER);
                    break;
                case 4: // 文件管理
                    genericPresenter.genericOperator(GenericPresenter.FILE_MANAGER);
                    break;
                case 5: // 日历
                    genericPresenter.genericOperator(GenericPresenter.CALENDAR);
                    break;
                case 6: // 时钟
                    genericPresenter.genericOperator(GenericPresenter.DESK_CLOCK);
                    break;
                case 7: // 浏览器
                    genericPresenter.genericOperator(GenericPresenter.BROWSER);
                    break;
                case 8: // 通讯录
                    genericPresenter.genericOperator(GenericPresenter.CONTACTS);
                    break;
                default:
                    break;
            }
        }
        else if(OFF_LINE_LAUNCHER.equals(asr)){
            switch (type){
                case 1: // 应用
                    genericPresenter.genericOperator(GenericPresenter.MY_APP);
                    break;
                case 2: // 影视
                    genericPresenter.genericOperator(GenericPresenter.MOVIE_MUSEUM);
                    break;
                case 3: // 文艺馆
                    genericPresenter.genericOperator(GenericPresenter.ART_MUSEUM);
                    break;
                case 4: // 工具
                    genericPresenter.genericOperator(GenericPresenter.TOOL);
                    break;
                case 5: // 投影设置
                    genericPresenter.genericOperator(GenericPresenter.PROJECTION);
                    break;
                case 6: // 学习
                    genericPresenter.genericOperator(GenericPresenter.STUDY);
                    break;
                case 7: // 遥控器
                    genericPresenter.genericOperator(GenericPresenter.REMOTE_CONTROL);
                    break;
                case 8: // 文档管理
                    genericPresenter.genericOperator(GenericPresenter.DOC_MANAGE);
                    break;
                default:
                    break;
            }
        }
        else if(OFF_LINE_HOME.equals(asr)){
            genericPresenter.genericOperator(GenericPresenter.HOME_PAGE);
        }
        else if(OFF_LINE_POLE.equals(asr)){
            if(1 == type){
                VoiceUtils.setMaxVolume(mContext, AudioManager.STREAM_MUSIC);
            }else if(2 == type){
                VoiceUtils.setMinVolume(mContext, AudioManager.STREAM_MUSIC);
            }
        }
        else if(OFF_LINE_VOICE_UP.equals(asr)){
            VoiceUtils.volumeUp(mContext, AudioManager.STREAM_MUSIC);

        }
        else if(OFF_LINE_VOICE_DOWN.equals(asr)){
            VoiceUtils.volumeDown(mContext, AudioManager.STREAM_MUSIC);

        }
        else if(OFF_LINE_UPDATE_WORDS.equals(asr)){
            if(type - 1 >= 0 && type - 1 < mAnswers.length) {
                String[] answers = mAnswers[type - 1].split("\\|");
                int index = (int) (Math.random() * answers.length);
                mRobotVoice.startTTS(answers[index], new Runnable() {
                    @Override
                    public void run() {
                        AppUtils.sendStartListenEvent("OFF_LINE", true, true);
                    }
                });
            }
        }
    }

    /**
     * 直接功能的处理
     * @param func
     */
    @Override
    public void handlerFunc(String func) {
        // 摸肩跳舞
        LogUtils.e(TAG, "func = " + func);
        if(mContext.getString(R.string.sensor_dance).equals(func)){
            // 功能发送【发给master进行处理】
            String action = intentMap.get(func);
            LogUtils.e(TAG, "action = " + action);
            if(!StringUtils.isEmpty(action)) {
                Bundle args = new Bundle();
                // 传-1的话，只跳舞，不唱歌
                args.putString(KEY_RESULT, "-1");
                if(StatePresenter.getInstance().isVideoing()){
                    args.putString(KEY_VIDEO, "open");
                }else {
                    args.putString(KEY_VIDEO, "close");
                }
                MyAppUtils.sendBroadcast(mContext, action, args);
            }else {

            }
        }
    }

}
