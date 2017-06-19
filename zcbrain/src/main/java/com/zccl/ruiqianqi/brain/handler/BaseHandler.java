package com.zccl.ruiqianqi.brain.handler;

import android.content.Context;

import com.zccl.ruiqianqi.brain.voice.RobotVoice;

/**
 * Created by ruiqianqi on 2017/2/24 0024.
 */

public abstract class BaseHandler {

    // 播放多媒体的ACTION
    public static final String ACTION_PLAYER = "com.yongyida.robot.PLAYER";
    // 播放类型的KEY
    public static final String PLAYER_CATEGORY_KEY = "category";
    // 类型为播放音乐
    public static final String MUSIC_PLAY = "music_play";
    // 类型为搜索音乐
    public static final String MUSIC_SEARCH = "music_search";
    // 类型为控制音乐
    public static final String MUSIC_CONTROL = "music_ctrl";
    // 类型为播放视频
    public static final String VIDEO_PLAY = "video";
    // 播放数据的KEY
    public static final String PLAYER_RESULT_KEY = "result";

    // 讯飞音乐场景
    public static final String SCENE_XF_MUSIC = "scene_xf_music";
    // 讯飞视频场景
    public static final String SCENE_XF_VIDEO = "scene_xf_video";
    // 音乐场景
    public static final String SCENE_MY_MUSIC = "scene_my_music";
    // 翻译场景
    public static final String SCENE_MY_TRANS = "scene_my_trans";


    // 全局上下文
    protected Context mContext;
    // 音频处理类
    protected RobotVoice mRobotVoice;
    // 持有下一个处理请求的对象
    protected BaseHandler mSuccessor;

    public BaseHandler(Context context, RobotVoice robotVoice){
        this.mContext = context;
        this.mRobotVoice = robotVoice;
    }

    /**
     * 取值方法
     */
    public BaseHandler getSuccessor() {
        return mSuccessor;
    }

    /**
     * 设置下一个处理请求的对象
     */
    public void setSuccessor(BaseHandler successor) {
        this.mSuccessor = successor;
    }

    /**
     * 优先处理场景
     * @param json  当前理解返回的数据
     * @param type  理解成功与失败
     * {@link com.zccl.ruiqianqi.plugin.voice.AbstractVoice.UnderstandCallback#UNDERSTAND_FAILURE}
     * {@link com.zccl.ruiqianqi.plugin.voice.AbstractVoice.UnderstandCallback#UNDERSTAND_SUCCESS}
     *
     * @return
     */
    public abstract boolean handlerScene(String json, int type);

    /**
     * 处理语义理解
     * @param json        科大讯飞返回的完整数据
     * @param funcType    功能类型
     * @return
     */
    public abstract boolean handleSemantic(String json, String funcType);

    /**
     * 处理语音识别
     * @param asr    语音识别的结果
     * @param type   语音识别的类型
     * {@link com.zccl.ruiqianqi.plugin.voice.AbstractVoice.RecognizerCallback#LISTEN_ERROR}
     * {@link com.zccl.ruiqianqi.plugin.voice.AbstractVoice.RecognizerCallback#LISTEN}
     * {@link com.zccl.ruiqianqi.plugin.voice.AbstractVoice.RecognizerCallback#ONLINE_WORD}
     * {@link com.zccl.ruiqianqi.plugin.voice.AbstractVoice.RecognizerCallback#OFFLINE_WORD}
     * {@link com.zccl.ruiqianqi.plugin.voice.AbstractVoice.RecognizerCallback#LISTEN_UNDERSTAND}
     */
    public abstract boolean handleAsr(String asr, int type);

    /**
     * 根据传入的数据，直接处理相应的功能
     * @param func 功能的描述
     */
    public abstract boolean handlerFunc(String func);
}
