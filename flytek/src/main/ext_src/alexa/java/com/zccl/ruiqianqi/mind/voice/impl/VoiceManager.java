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
    protected VoiceWakeUp mVoiceWakeUp;
    // 语音识别
    private VoiceRecognizer mVoiceRecognizer;

    public VoiceManager(Context context){
        this.mContext = context.getApplicationContext();
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void initSpeech() {
        mVoiceWakeUp = VoiceWakeUp.getInstance(mContext);
        mVoiceRecognizer = new VoiceRecognizer(mContext);
    }

    /************************************【唤醒相关接口】******************************************/
    /**
     * 设置唤醒回调接口
     * @param wakeupCallback
     */
    @Override
    public void setWakeupCallback(WakeupCallback wakeupCallback) {
        mVoiceWakeUp.setWakeupCallback(wakeupCallback);
    }

    /**
     * 开始唤醒监听
     */
    @Override
    public void startWakeup() {
        mVoiceWakeUp.startWakeUp();
    }

    /**
     * 停止唤醒监听
     */
    @Override
    public void stopWakeup() {
        mVoiceWakeUp.stopWakeUp();
    }

    /**
     * 重启唤醒监听
     * 返回值：是否需要检测，并重启唤醒引擎
     */
    @Override
    public boolean reboot() {
        return mVoiceWakeUp.reboot();
    }

    /**
     * 得到看门狗的值
     * @return
     */
    @Override
    public boolean isReboot() {
        return mVoiceWakeUp.isReboot();
    }

    /**
     * 设置看门狗的值
     * @param reboot
     */
    @Override
    public void setReboot(boolean reboot) {
        mVoiceWakeUp.setReboot(reboot);
    }

    /*********************************【语法识别相关接口】*****************************************/
    /**
     *
     */
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
