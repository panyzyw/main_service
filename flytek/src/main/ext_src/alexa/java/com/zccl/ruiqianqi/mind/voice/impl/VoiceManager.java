package com.zccl.ruiqianqi.mind.voice.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.zccl.ruiqianqi.mind.voice.alexa.Configuration;
import com.zccl.ruiqianqi.mind.voice.alexa.VoiceRecognizer;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;

/**
 * Created by ruiqianqi on 2017/2/10 0010.
 */

public class VoiceManager extends AbstractVoice {

    // 默认发音人
    public static final String DEFAULT_SPEAKER = Configuration.SPEAKER_NAME;
    // 全局上下文
    protected Context mContext;
    // 主线程的Handler
    protected Handler mHandler;
    // 语音唤醒
    protected VoiceWakeUp voiceWakeUp;
    // 语音识别
    private VoiceRecognizer mVoiceRecognizer;

    public VoiceManager(Context context){
        this.mContext = context.getApplicationContext();
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void initSpeech() {
        voiceWakeUp = VoiceWakeUp.getInstance(mContext);
        mVoiceRecognizer = new VoiceRecognizer(mContext);
    }

    @Override
    public void switchLanguage(String language) {

    }

    @Override
    public void startRecognizer() {
        mVoiceRecognizer.start();
    }

    /**
     * 直接加载语法识别语音数据
     * @param dataS
     */
    public void writeRecognizer(byte[] dataS){
        mVoiceRecognizer.writeRecognizer(dataS);
    }

    @Override
    public void stopRecognizer() {
        mVoiceRecognizer.stop();
    }

    @Override
    public void cancelRecognizer() {
        mVoiceRecognizer.stop();
    }

}
