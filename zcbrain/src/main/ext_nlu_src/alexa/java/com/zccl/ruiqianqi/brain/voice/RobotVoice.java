package com.zccl.ruiqianqi.brain.voice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.zccl.ruiqianqi.brain.R;
import com.zccl.ruiqianqi.brain.handler.SDKHandler;
import com.zccl.ruiqianqi.brain.system.ISDKCallback;
import com.zccl.ruiqianqi.mind.voice.impl.VoiceManager;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.plugin.voice.WakeInfo;
import com.zccl.ruiqianqi.presentation.presenter.PersistPresenter;
import com.zccl.ruiqianqi.presentation.presenter.SystemPresenter;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.MYUIUtils;
import com.zccl.ruiqianqi.tools.MyAppUtils;
import com.zccl.ruiqianqi.tools.SystemUtils;
import com.zccl.ruiqianqi.tools.config.MyConfigure;

import static com.zccl.ruiqianqi.config.MyConfig.INTENT_ACTION_STOP;
import static com.zccl.ruiqianqi.config.MyConfig.KEY_STOP_FROM;
import static com.zccl.ruiqianqi.config.MyConfig.TTS_NOT_DEAL_RESPONSE;

/**
 * Created by ruiqianqi on 2017/3/6 0006.
 */

public class RobotVoice extends VoiceManager {

    // 类标志
    private static String TAG = RobotVoice.class.getSimpleName();
    // 监听入口处理类
    private ListenCheck mListenCheck;
    // 处理类
    private MindHandler mMindHandler;
    // SDK处理类
    private SDKHandler sdkHandler;
    // 是不是触摸唤醒
    private boolean isTouchWake = false;



    public RobotVoice(Context context) {
        super(context);
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        mListenCheck = new ListenCheck(mContext, this);
        mMindHandler = new MindHandler(mContext, this);
        sdkHandler = new SDKHandler(mContext, this);

    }

    /**
     * 初始化语音服务
     */
    @Override
    public void initSpeech() {
        // 初始化语音服务对象
        super.initSpeech();

        // 设置唤醒监听回调接口
        setWakeupCallback(new WakeupListener());
    }


    /**********************************************************************************************/
    /*********************************【状态变化通知处理】*****************************************/
    /**********************************************************************************************/
    /**
     * 机器人状态改变的通知
     * @param flag
     * {@link AbstractVoice#NET_CHANGE}
     * {@link AbstractVoice#PHONE_CHANGE}
     * {@link AbstractVoice#SENSOR_CHANGE}
     * {@link AbstractVoice#BATTERY_CHANGE}
     * {@link AbstractVoice#APP_STATUS_CHANGE}
     * {@link AbstractVoice#RECYCLE_LISTEN_CHANGE}
     * {@link AbstractVoice#STOP_LISTEN_CHANGE}
     * {@link AbstractVoice#HDMI_CHANGE}
     *
     * @param obj
     */
    @Override
    public void notifyChange(int flag, Object obj) {
        mMindHandler.notifyChange(flag, obj);

    }

    /**********************************************************************************************/
    /************************************【开始语义处理】******************************************/
    /**********************************************************************************************/
    /**
     * 触摸监听
     * 唤醒监听
     * 循环监听
     *
     * @param fromWhere  来自哪里
     *              touch_head_wake
     *              voice_wake
     *              somewhere_else
     */
    public void handlerVoiceEntry(String fromWhere){

        LogUtils.e(TAG, "fromWhere = " + fromWhere + " - " + sdkHandler.getSDKCallback());

        boolean isShow = mListenCheck.isShowExpression();
        if(!isShow){
            mListenCheck.setUseExpression(false);
        }

        // 发STOP广播
        stopOtherAppFunc(fromWhere);

        // 唤醒之后立即取消当前会话
        cancelListen();

        // 完美结束
        prettyEnd();

        // 结束发音
        stopTTS();

        // 显示悬浮小表情
        if(mListenCheck.isUseVoiceFloat()) {
            // 触摸唤醒，第一次，需要说话
            if (mContext.getString(R.string.sensor_touch).equals(fromWhere)) {
                mListenCheck.startCheckAndListen(true, true, mListenCheck.isUseExpression());
            }
            // 语音唤醒，第一次，需要说话
            else if (mContext.getString(R.string.sensor_voice).equals(fromWhere)) {
                mListenCheck.startCheckAndListen(true, true, mListenCheck.isUseExpression());
            }
            // 循环监听，不需要说话
            else {
                mListenCheck.startCheckAndListen(false, false, mListenCheck.isUseExpression());
            }
        }
        // 不显示悬浮表情
        else {
            continueListen();
        }
    }

    /**
     * 在线调用的监听方法
     */
    protected void startListenOnline(){
        startRecognizer();
    }

    /**
     * 离线调用的监听方法
     */
    protected void startListenOffline(){

    }

    /**
     * 再次开始监听，仅仅是再次开启监听
     */
    private void continueListen(){
        startRecognizer();
    }

    /**
     * 取消监听
     */
    protected void cancelListen(){
        cancelRecognizer();
    }

    /**
     * 开始写数据
     * @param audio
     */
    protected void writeData(byte[] audio){
        writeRecognizer(audio);
    }

    /**
     * 正常的结束路子
     */
    protected void prettyEnd(){
        // 结束监听悬浮
        mListenCheck.endListenFace();
    }

    /**
     * 取消监听
     * 隐藏大表情
     * @param destroyExpression 是否结束大表情
     */
    protected void cancelExpression(boolean destroyExpression){
        LogUtils.e(TAG, "cancelExpression");

        // 结束发音
        stopTTS();

        // 结束监听
        cancelUnderstand();
        cancelRecognizer();

        // 结束监听悬浮
        mListenCheck.endListenFace();

        // 结束大表情
        if(destroyExpression) {
            mListenCheck.endListenExpression();
        }
    }

    /**
     * 讯飞音乐及影视
     * 自有播放器
     * 安静
     * 锁屏
     * 循环监听
     *
     * 监听，调用
     * 安静，调用
     * 灭屏，调用
     *
     * 停止其他应用的功能
     * @param fromWhere
     */
    public void stopOtherAppFunc(String fromWhere){
        Bundle args = new Bundle();
        args.putString(KEY_STOP_FROM, fromWhere);
        // 关闭所有，但是launcher接收之后，会点亮屏幕
        MyAppUtils.sendBroadcast(mContext, INTENT_ACTION_STOP, args);

    }

    /**
     * 进行某项功能
     * @param cmd
     * @param obj
     */
    public void sendCommand(int cmd, Object obj){
        mVoiceWakeUp.sendCommand(cmd, obj);
    }

    /**
     * 设置拾音波束
     * @param beam
     */
    protected void setRealBeam(int beam) {
        mVoiceWakeUp.setRealBeam(beam);
    }

    /**********************************************************************************************/
    /*********************************【生产者、消费者】*******************************************/
    /**********************************************************************************************/
    /**
     * 【自行处理】唤醒接口的回调
     */
    private class WakeupListener implements AbstractVoice.WakeupCallback {
        /**
         * 单唤醒模式
         * @param msg
         */
        @Override
        public void oneShot(String msg) {
        }

        /**
         * 唤醒失败
         * @param e
         */
        @Override
        public void wakeFailure(Throwable e) {
            LogUtils.e(TAG, "wakeFailure");
        }

        /**
         * 语音唤醒成功
         */
        @Override
        public void wakeSuccess(WakeInfo wakeInfo) {
            if(null == wakeInfo)
                return;

            boolean isShowDebug = Boolean.parseBoolean(MyConfigure.getValue("show_debug"));
            if(isShowDebug) {
                MYUIUtils.showToast(mContext, "angle=" + wakeInfo.getAngle() + ", score=" + wakeInfo.getScore());
            }

            PersistPresenter cp = PersistPresenter.getInstance();
            boolean isLocalizationOn = Boolean.parseBoolean(MyConfigure.getValue("localization"));
            if(isLocalizationOn){
                cp.setLocalization(true);
            }

            if(wakeInfo.getScore() >= cp.getThreshold()){

                // 唤醒亮屏，对手机好像没什么用
                SystemUtils.wakeUp(mContext);

                boolean isFaceDetect = Boolean.parseBoolean(MyConfigure.getValue("face_detect"));
                if(isFaceDetect) {
                    // 唤醒之后，进行人脸识别
                    Intent intent = new Intent("com.yongyida.robot.VoiceLocalization");
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    intent.putExtra("angle", wakeInfo.getAngle());
                    mContext.sendBroadcast(intent);
                }

                // 非触摸唤醒、有表情
                setTouchWake(false);
                // 有小表情，有大表情
                setExpressionState(true, true);

                // 有SDK，就进行SDK处理，下面就不处理了
                if(null != sdkHandler.getSDKCallback()){
                    // 声源定位相关检测
                    mListenCheck.localization(wakeInfo.getAngle());
                    // 唤醒后的SDK回调
                    sdkHandler.onReceive(SDKHandler.RECV_VOICE_WAKE_UP, new Gson().toJson(wakeInfo));

                }else {
                    // 声源定位相关检测
                    if(mListenCheck.localization(wakeInfo.getAngle())) {
                        // 开始自有逻辑处理
                        handlerVoiceEntry(mContext.getString(R.string.sensor_voice));
                    }

                }

            }else {
                wakeFailure(new Throwable(mContext.getString(R.string.wakeup_invalid)));
            }
        }

        /**
         * 五麦回调的数据【原始语音音频生产者】
         *
         * 五麦一直返回音频数据，对于单麦来讲，这个是没有用的
         * 因为单麦根本就没有回调这个接口
         * @param audio
         * @param audioLen
         */
        @Override
        public void onAudio(byte[] audio, int audioLen) {

            // SDK处理了音频数据
            if(sdkHandler.onAudio(audio, audioLen)){

            }
            // 自有逻辑处理了音频数据
            else {
                writeData(audio);
            }
        }

        @Override
        public boolean isTouchWake() {
            return isTouchWake;
        }

    }


    /**********************************************************************************************/
    /**
     * 发音方法重载
     * @param words ---------------- 发音要读的文字
     * @param from ---------------- 携带的标志
     * @param synthesizerCallback
     */
    @Override
    public void startTTS(String words, String from, final SynthesizerCallback synthesizerCallback) {
        SystemPresenter.getInstance().startTTS(words, from, synthesizerCallback);
    }

    /**
     * 发音方法重载
     * @param words  -------------------- 发音要读的文字
     * @param from ---------------- 携带的标志
     * @param runnable
     */
    @Override
    public void startTTS(String words, String from, Runnable runnable) {
        SystemPresenter.getInstance().startTTS(words, from, runnable);
    }

    /**
     * 发音方法重载【默认不处理，语音的回调逻辑】
     * @param words  -------------------- 发音要读的文字
     * @param runnable
     */
    @Override
    public void startTTS(String words, Runnable runnable) {
        SystemPresenter.getInstance().startTTS(words, TTS_NOT_DEAL_RESPONSE, runnable);
    }

    @Override
    public void pauseTTS()  {
        SystemPresenter.getInstance().pauseTTS();
    }

    @Override
    public void resumeTTS() {
        SystemPresenter.getInstance().resumeTTS();
    }

    /**
     * 现在结束发音，统一由这里处理
     *
     * 安静
     * 取消监听
     * 开始监听
     * 语音推送
     *
     */
    @Override
    public void stopTTS() {
        SystemPresenter.getInstance().stopTTS();
    }

    @Override
    public boolean isSpeaking() {
        return SystemPresenter.getInstance().isSpeaking();
    }


    /*****************************【自身成员变量的读写】*******************************************/
    /**
     * 获取是否是触摸唤醒
     * @return
     */
    public boolean isTouchWake() {
        return isTouchWake;
    }

    /**
     * 设置是否是触摸唤醒
     * @param touchWake
     */
    public void setTouchWake(boolean touchWake) {
        isTouchWake = touchWake;
    }

    /**
     * 设置是否显示表情
     * @param useVoiceFloat 小表情
     * @param useExpression 大表情
     */
    public void setExpressionState(boolean useVoiceFloat, boolean useExpression) {
        mListenCheck.setUseVoiceFloat(useVoiceFloat);
        mListenCheck.setUseExpression(useExpression);
    }

    /**
     * 是不是继续循环监听
     * @param from
     * @return
     */
    public boolean isContinueListen(String from){
        return mListenCheck.isContinueListen(from);
    }

    /**********************************【SDK相关方法】*********************************************/
    /**
     * 设置SDK的回调接口
     * @param SDKCallback
     */
    public void setSDKCallback(ISDKCallback SDKCallback) {
        this.sdkHandler.setSDKCallback(SDKCallback);
    }

    /**
     * 相关动作节点，回调SDK客户端
     * @param cmd
     * @param msg
     */
    public void onSDKReceive(int cmd, String msg){
        sdkHandler.onReceive(cmd, msg);
    }

}
