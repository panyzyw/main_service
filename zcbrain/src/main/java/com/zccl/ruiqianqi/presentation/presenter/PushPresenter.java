package com.zccl.ruiqianqi.presentation.presenter;

import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.presenter.base.BasePresenter;
import com.zccl.ruiqianqi.presenter.impl.MindPresenter;
import com.zccl.ruiqianqi.tools.StringUtils;

/**
 * Created by ruiqianqi on 2017/3/24 0024.
 */

public class PushPresenter extends BasePresenter {

    // 语音处理类
    private AbstractVoice voice;

    public PushPresenter(){
        voice = MindPresenter.getInstance().getVoiceDevice();
    }

    /**
     * 手机控制机器人聊天
     * @param question
     */
    public void chat(String question){
        if(StringUtils.isEmpty(question))
            return;
        if(null != voice){
            voice.startText(question);
        }
    }

    /**
     * 机器人同步手机说话文字
     * @param tts
     */
    public void talk(String tts){
        if(StringUtils.isEmpty(tts))
            return;
        if(null != voice){
            voice.startTTS(tts, null, null);
        }
    }

    /**
     * 推送音乐
     * @param name
     * @param url
     */
    public void pushMusic(String name, String url){

    }

    /**
     * 推送视频
     * @param name
     * @param url
     */
    public void pushVideo(String name, String url){

    }

    /**
     * 推送文本
     * @param text
     */
    public void pushText(String text){

    }

}
