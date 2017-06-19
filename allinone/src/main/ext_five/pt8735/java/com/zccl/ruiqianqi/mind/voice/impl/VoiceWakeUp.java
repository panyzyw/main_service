package com.zccl.ruiqianqi.mind.voice.impl;

import android.content.Context;
import android.os.Handler;

import com.iflytek.alsa.AlsaRecorder;
import com.iflytek.cae.CAEEngine;
import com.iflytek.cae.CAEError;
import com.iflytek.cae.CAEListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.zccl.ruiqianqi.control.Control;
import com.zccl.ruiqianqi.mind.voice.allinone.R;
import com.zccl.ruiqianqi.mind.voice.impl.beans.FiveWakeUpInfo;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.plugin.voice.WakeInfo;
import com.zccl.ruiqianqi.presentation.mictest.SavePcmAudio;
import com.zccl.ruiqianqi.tools.JsonUtils;
import com.zccl.ruiqianqi.tools.LogUtils;

/**
 * Created by ruiqianqi on 2016/7/25 0025.
 */
public class VoiceWakeUp {

    /** 类日志标志 */
    private static String TAG = VoiceWakeUp.class.getSimpleName();
    /** 单例引用 */
    private static VoiceWakeUp instance;
    /** 录音设备 */
    public static final int RECORD_MODE = 5;
    /**
     * 唤醒文件的名字
     */
    private static String WAKE_UP_NAME = "wakeup";

    /** 生命周期最长的上下文 */
    protected Context mContext;
    /** 唤醒资源路径 */
    private String mResPath = null;

    /** 当前准备使用的唤醒阀值 */
    //private int curThreshUse = 10;

    /** 最开始的录音类 */
    private AlsaRecorder mRecorder = null;
    /** 最开始的录音类回调接口 */
    private AlsaRecorder.PcmListener mPcmListener = null;
    /** 唤醒引擎 */
    private CAEEngine mCaeEngine = null;
    /** 唤醒后的回调接口 */
    private CAEListener mCaeListener = null;
    // 引擎是否正在重置标志
    private boolean isCaeReset = false;

    /** 唤醒回调接口 */
    private AbstractVoice.WakeupCallback mWakeupCallback;
    // 需要重启吗
    private volatile boolean needReboot = false;

    // 保存录音数据，只是在测试五麦的时候用
    private SavePcmAudio mSavePcmAudio;

    /**
     * 用这个用话，instance不需要用volatile修饰
     * @return
     */
    public static VoiceWakeUp getInstance(Context context) {
        if(instance == null) {
            synchronized(VoiceWakeUp.class) {
                VoiceWakeUp temp = instance;
                if(temp == null) {
                    temp = new VoiceWakeUp(context);
                    instance = temp;
                }
            }
        }
        return instance;
    }

    private VoiceWakeUp(Context context) {
        this.mContext = context.getApplicationContext();
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        // 加载唤醒资源
        mResPath = ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "ivw/" + WAKE_UP_NAME + ".jet");

        // 原始音频数据
        mRecorder = AlsaRecorder.createInstance(0);
        mPcmListener = new MyPcmListener();

        // 引擎数据分析, 分析唤醒用的
        mCaeEngine = CAEEngine.createInstance(mResPath);
        mCaeListener = new MyCAEListener();
        mCaeEngine.setCAEListener(mCaeListener);

        mSavePcmAudio = new SavePcmAudio();
    }

    /**
     * 开始底层录音，循环录音
     */
    public void startWakeUp(){
        // 先启动ALSA
        mRecorder.startRecording(mPcmListener);
        LogUtils.e(TAG, "startWakeUp");
    }

    /**
     * 停止底层录音
     */
    public void stopWakeUp(){
        if (mRecorder != null) {
            if (mRecorder.isRecording()) {
                mRecorder.stopRecording();
            }
            //mRecorder = null;
        }
    }

    /**
     * 设置拾音麦口【五麦】
     *   2
     * 3   1
     *   0
     * @param beam
     */
    public void setRealBeam(int beam){
        mCaeEngine.setRealBeam(beam);
    }

    /*************************************【五麦回调】*********************************************/
    /**
     * 原始录音回调接口，非静态内部类持有外部类的引用
     */
    private class MyPcmListener implements AlsaRecorder.PcmListener{

        @Override
        public void onPcmData(byte[] bytes, int length) {
            //建议不要在读音频线程中做耗时的同步操作，否则会导致音频数据读出不及时造成AudioRecord中的缓存溢出。
            //Log.d("MSC_LOG", "onPcmData: "+length);

            // 重置看门狗，有数据，不需要重启
            setReboot(false);

            if(null != mCaeEngine) {

                // 将从阵列读取的96K采样的音频写入CAE引擎
                mCaeEngine.writeAudio(bytes, length);

                // 保存录音数据，只是在测试五麦的时候用
                mSavePcmAudio.writeAudio(bytes, length);

                if(null != mWakeupCallback){
                    // 触摸唤醒走单mic模式，此模式无降噪，只能近距离识别
                    if(mWakeupCallback.isTouchWake()){
                        byte[] sendData = new byte[length / 24];
                        length = mCaeEngine.extract16K(bytes, length, 1, sendData);
                        mWakeupCallback.onAudio(sendData, length);
                    }

                }

                // 如果没有语音唤醒，就是默认拾音波束
                if (!mCaeEngine.isWakeup()) {
                    setRealBeam(0);
                }
            }

        }
    }

    /**
     * 唤醒后的回调接口，非静态内部类持有外部类的引用
     */
    private class MyCAEListener implements CAEListener {

        /**
         * 被唤醒了，可以进行语义理解了
         * @param jsonResult {"angle":322, "channel":2, "power":313967968256, "CMScore":12, "beam":3}
         */
        @Override
        public void onWakeup(String jsonResult) {
            LogUtils.e(TAG, "onWakeup: " + jsonResult);
            FiveWakeUpInfo fiveWakeUpInfo = JsonUtils.parseJson(jsonResult, FiveWakeUpInfo.class);
            if(null != fiveWakeUpInfo){
                WakeInfo wakeInfo = new WakeInfo();
                wakeInfo.setAngle(fiveWakeUpInfo.getAngle());
                wakeInfo.setScore(fiveWakeUpInfo.getScore());
                wakeInfo.setBeam(fiveWakeUpInfo.getBeam());
                if(null != mWakeupCallback){
                    mWakeupCallback.wakeSuccess(wakeInfo);
                }

            }else {
                if(null != mWakeupCallback){
                    mWakeupCallback.wakeFailure(new Throwable(mContext.getString(R.string.wakeup_parse_error)));
                }
            }
        }

        /**
         * 一旦被唤醒，就会不断的有音频资源输出
         * 只有被唤醒，才会有不断的音频资源输出
         * 这个回调方法，在不同的线程中，底层可能是线程池
         * @param audio
         * @param audioLen
         * @param param1
         * @param param2
         */
        @Override
        public void onAudio(byte[] audio, int audioLen, int param1, int param2) {
            //LogUtils.e(TAG, "onAudio: "+audioLen);
            if (null != mWakeupCallback) {
                if(!mWakeupCallback.isTouchWake()) {
                    mWakeupCallback.onAudio(audio, audioLen);
                }
            }
        }

        @Override
        public void onError(CAEError caeError) {
            if(caeError != null) {

                if(null != mWakeupCallback){
                    mWakeupCallback.wakeFailure(caeError);
                }

                if(10110 == caeError.getErrorCode()){
                    if(!isCaeReset){
                        isCaeReset = true;

                        if(1 == Control.Reset5Mic()){
                            LogUtils.e(TAG, "10110错误，引擎重置");
                            mCaeEngine.reset();
                            mCaeEngine.destroy();
                            mCaeEngine = null;
                            mCaeEngine = CAEEngine.createInstance(mResPath);
                            mCaeEngine.setCAEListener(mCaeListener);
                        }else{
                            LogUtils.e(TAG, "5mic驱动重置失败");
                        }

                        // 3秒后重置标志位
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isCaeReset = false;
                            }
                        }, 3000);

                    }
                }
            }
        }

    }

    /**
     * 重启唤醒
     * @param context
     * 返回值：true  表示需要检测
     *         false 表示不需要检测
     */
    public static boolean reboot(Context context){
        // 唤醒监听开启
        VoiceWakeUp.getInstance(context).startWakeUp();
        return true;
    }

    /**************************************自身参数设置********************************************/
    /**
     * 设置唤醒回调接口
     * @param wakeupCallback
     */
    public void setWakeupCallback(AbstractVoice.WakeupCallback wakeupCallback) {
        this.mWakeupCallback = wakeupCallback;
    }

    /**
     * 得到看门狗的值
     * @return
     */
    public boolean isReboot() {
        return needReboot;
    }

    /**
     * 设置看门狗的值
     * @param needReboot
     */
    public void setReboot(boolean needReboot) {
        this.needReboot = needReboot;
    }
}
