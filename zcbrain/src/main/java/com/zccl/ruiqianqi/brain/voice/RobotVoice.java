package com.zccl.ruiqianqi.brain.voice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;

import com.google.gson.Gson;
import com.iflytek.cloud.SpeechError;
import com.zccl.ruiqianqi.brain.R;
import com.zccl.ruiqianqi.brain.handler.SDKHandler;
import com.zccl.ruiqianqi.brain.system.ISDKCallback;
import com.zccl.ruiqianqi.mind.voice.impl.VoiceManager;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.plugin.voice.WakeInfo;
import com.zccl.ruiqianqi.presentation.presenter.PersistPresenter;
import com.zccl.ruiqianqi.presentation.presenter.StatePresenter;
import com.zccl.ruiqianqi.presentation.presenter.SystemPresenter;
import com.zccl.ruiqianqi.tools.CheckUtils;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.MyAppUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.tools.SystemUtils;
import com.zccl.ruiqianqi.tools.executor.rxutils.MyRxUtils;
import com.zccl.ruiqianqi.utils.AppUtils;
import com.zccl.ruiqianqi.utils.LedUtils;

import java.util.concurrent.LinkedBlockingQueue;

import rx.Subscription;

import static com.zccl.ruiqianqi.config.MyConfig.INTENT_ACTION_STOP;
import static com.zccl.ruiqianqi.config.MyConfig.KEY_STOP_FROM;
import static com.zccl.ruiqianqi.config.MyConfig.TTS_NOT_DEAL_RESPONSE;
import static com.zccl.ruiqianqi.plugin.voice.AbstractVoice.UnderstandCallback.UNDERSTAND_FAILURE;
import static com.zccl.ruiqianqi.plugin.voice.AbstractVoice.UnderstandCallback.UNDERSTAND_SUCCESS;

/**
 * Created by ruiqianqi on 2017/3/6 0006.
 */

public class RobotVoice extends VoiceManager {

    // 类标志
    private static String TAG = RobotVoice.class.getSimpleName();


    // 监听入口处理类
    private ListenCheck mListenCheck;
    // 声源定位
    private Localization mLocalization;
    // 处理类
    private MindHandler mMindHandler;
    // SDK处理类
    private SDKHandler sdkHandler;

    // 端点检测开始时间
    private long beginTime = 0;
    // 端点检测结束时间
    private long endTime = 0;

    // 原始音频数据集合
    // offer方法在添加元素时，如果发现队列已满无法添加的话，会直接返回false。
    // add方法在添加元素的时候，若超出了度列的长度会直接抛出异常：
    // put方法，若向队尾添加元素的时候发现队列已经满了会发生阻塞一直等待空间，以加入元素。
    // poll: 若队列为空，返回null。
    // remove:若队列为空，抛出NoSuchElementException异常。
    // take:若队列为空，发生阻塞，等待有元素。
    private LinkedBlockingQueue<byte[]> rawDataS = null;
    // 会话是不是在进行中
    private boolean isSessionGoing = false;
    // 线程同步用的类
    private Object syncObj = new Object();

    // 端点提前结束了
    private boolean isFinishInAdvance = false;
    // 是否是生产者与消费者，默认不是
    private boolean isProducerConsumer = false;
    // 错误回调计数
    private int mErrorCount = 0;
    // 录音超时处理线程
    private Subscription timeSubscription;
    // 录音超时时间为10秒
    private final long RECORD_TIME_OUT = 10000;
    // 前端点提前结束时间限制
    private final long ADVANCE_TIME = 3000;

    // 是不是触摸唤醒
    private boolean isTouchWake = false;
    // 是不是显示大表情
    private boolean isUseExpression = false;

    // 用户没说话的时候，机器人的反应语
    private String[] noVoices = null;
    // VIP通道测试
    private TestVipChannel mTestVipChannel;
    // 是不是用语义理解
    private boolean isUseUnderstand = false;

    // 是不是小勇的APPID
    private String isAppIdXiaoYong;

    public RobotVoice(Context context) {
        super(context);
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        mListenCheck = new ListenCheck(mContext, this);
        mLocalization = new Localization(mContext);
        mMindHandler = new MindHandler(mContext, this);
        sdkHandler = new SDKHandler(mContext, this);

        rawDataS = new LinkedBlockingQueue<>();
        noVoices = mContext.getResources().getStringArray(R.array.no_audio_input);

        // 开启音频输入线程
        MyRxUtils.doNewThreadRun(new Consumer());

        isAppIdXiaoYong = mContext.getString(R.string.is_xiaoyong);
        mTestVipChannel = new TestVipChannel(mContext, this);
        mMindHandler.setVipTest(mTestVipChannel);

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
        // 设置语义理解回调接口
        addUnderstandCallback(TAG, new UnderstandListener());
        // 设置文字理解回调接口
        setTextUnderCallback(new TextUnderListener());
        // 设置语音识别回调接口
        addRecognizerCallback(TAG, new RecognizerListener());
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
     * @param isUseVoiceFloat true显示悬浮表情，false不显示悬浮表情
     * @param isUseExpression true显示大表情，  false不显示大表情
     */
    public void handlerVoiceEntry(String fromWhere, boolean isUseVoiceFloat, boolean isUseExpression){

        LogUtils.e(TAG, "fromWhere = " + fromWhere + " - " + sdkHandler.getSDKCallback());

        // 有SDK，就进行SDK处理，下面就不处理了
        if(null != sdkHandler.getSDKCallback()){
            return;
        }

        boolean isShow = mListenCheck.isShowExpression();
        isUseExpression = isShow == false ? isShow : isUseExpression;

        // 发STOP广播
        stopOtherAppFunc(fromWhere);

        // 唤醒之后立即取消当前会话
        cancelListen();

        // 完美结束
        prettyEnd();

        // 结束发音
        stopTTS();

        mErrorCount = 0;
        // 一次正常的会话开始
        isSessionGoing = true;
        // 是正常结束
        isFinishInAdvance = false;

        // 设置是否显示悬浮表情
        mListenCheck.setUseVoiceFloat(isUseVoiceFloat);

        // 显示悬浮表情
        if(isUseVoiceFloat) {
            // 触摸唤醒，第一次，需要说话
            if (mContext.getString(R.string.sensor_touch).equals(fromWhere)) {
                mListenCheck.startCheckAndListen(true, true, isUseExpression);
            }
            // 语音唤醒，第一次，需要说话
            else if (mContext.getString(R.string.sensor_voice).equals(fromWhere)) {
                mListenCheck.startCheckAndListen(true, true, isUseExpression);
            }
            // 循环监听，不需要说话
            else {
                mListenCheck.startCheckAndListen(false, false, isUseExpression);
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
        if(IS_USE_CHINESE){
            isUseUnderstand = true;
            startUnderstand();
        }else {
            startRecognizer();
        }
    }

    /**
     * 离线调用的监听方法
     */
    protected void startListenOffline(){
        if(IS_USE_CHINESE){
            isUseUnderstand = false;
            startRecognizer(2);
        }else {
            // 提示需要联网
        }
    }

    /**
     * 再次开始监听，仅仅是再次开启监听
     */
    private void continueListen(){
        // 一次正常的会话开始
        isSessionGoing = true;

        if(IS_USE_CHINESE) {
            if (isUseUnderstand) {
                startUnderstand();
            } else {
                startRecognizer();
            }
        }else {
            startRecognizer();
        }
    }

    /**
     * 取消监听
     */
    protected void cancelListen(){
        if(IS_USE_CHINESE){
            if (isUseUnderstand) {
                cancelUnderstand();
            } else {
                cancelRecognizer();
            }
        }else {
            cancelRecognizer();
        }
    }

    /**
     * 开始写数据
     * @param audio
     */
    protected void writeData(byte[] audio){
        if(IS_USE_CHINESE){
            if (isUseUnderstand) {
                writeUnderstand(audio);
            } else {
                writeRecognizer(audio);
            }
        }else {
            writeRecognizer(audio);
        }
    }

    /**
     * 正常的结束路子
     */
    protected void prettyEnd(){
        // 没有超时，就要取消超时处理
        if(null != timeSubscription) {
            timeSubscription.unsubscribe();
            timeSubscription = null;
        }
        // 关闭生产者与消费者模式
        isProducerConsumer = false;
        // 是正常结束
        isFinishInAdvance = false;
        // 已结束
        isSessionGoing = false;
        // 清除缓存音频对对象
        rawDataS.clear();

        // 结束监听悬浮
        mListenCheck.endListenFace();

    }

    /**
     * 取消监听
     * 隐藏大表情
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
            AppUtils.endEmotion(mContext);
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


    /**********************************************************************************************/
    /*********************************【生产者、消费者】*******************************************/
    /**********************************************************************************************/
    /**
     * 设置拾音波束
     * @param beam
     */
    protected void setRealBeam(int beam) {
        voiceWakeUp.setRealBeam(beam);
    }

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

            PersistPresenter cp = PersistPresenter.getInstance();
            if(wakeInfo.getScore() >= cp.getThreshold()){

                // 唤醒亮屏，对手机好像没什么用
                SystemUtils.wakeUp(mContext);

                if(0 != wakeInfo.getAngle()){
                    StatePresenter sp = StatePresenter.getInstance();
                    boolean canLocal = true;

                    /*
                    // 机器人在电话中
                    if(sp.isCalling()){
                        canLocal = false;
                    }
                    // 机器人正在视频中
                    else if(sp.isVideoing()){
                        canLocal = false;
                    }
                    // 机器人正在工厂模式中
                    else
                    */
                    if(sp.isFactory()){
                        canLocal = false;
                        LogUtils.e(TAG, "isFactory");
                    }
                    // 机器人在控制中
                    else if(sp.isInControl()){
                        canLocal = false;
                        LogUtils.e(TAG, "isInControl");
                    }

                    LogUtils.e(TAG, canLocal + " - localization - " + cp.isLocalization());

                    cp.setLocalization(true);
                    // 声源定位在哪唤醒，拾音波束就在哪，如果进行声源定位了，就要进行波束重置
                    // 如果没有进行声源定位，拾音波束就是上一次唤醒的波束
                    if(canLocal && cp.isLocalization()){
                        mLocalization.rotate(wakeInfo.getAngle());
                        setRealBeam(0);
                    }

                }

                // 唤醒之后，进行人脸识别
                Intent intent = new Intent("com.yongyida.robot.VoiceLocalization");
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                intent.putExtra("angle", wakeInfo.getAngle());
                mContext.sendBroadcast(intent);

                // 非触摸、有表情、唤醒
                setTouchWake(false);
                setUseExpression(true);
                handlerVoiceEntry(mContext.getString(R.string.sensor_voice), true, isUseExpression());

                // 唤醒后的SDK回调
                sdkHandler.onReceive(SDKHandler.RECV_VOICE_WAKE_UP, new Gson().toJson(wakeInfo));

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

            if(sdkHandler.onAudio(audio, audioLen)){
                // 处理了SDK音频数据

            }else {
                // 原始语音音频生产者，监听期间一直BAK
                if(isSessionGoing){
                    rawDataS.offer(audio);
                }

                // 如果是正常模式，则直接消费音频数据
                if(!isProducerConsumer){
                    writeData(audio);
                }
            }
        }

        @Override
        public boolean isTouchWake() {
            return isTouchWake && voiceUnderstander.isListening();
        }

    }

    /**
     * 【原始语音音频消费者】
     */
    private class Consumer implements Runnable{
        @Override
        public void run() {
            try {
                // 在调用sleep()方法的过程中，线程不会释放对象锁。
                // 而当调用wait()方法的时候，线程会放弃对象锁，进入等待此对象的等待锁定池，
                // 只有针对此对象调用notify()方法后本线程才进入对象锁定池准备
                while (true) {

                    // 等待被唤醒使用
                    if(!isProducerConsumer) {
                        // 不打算锁在while循环外边,因为这样的话,循环一旦运行起来,就一直持有锁了
                        // 那样的话调用notify()的线程就会阻塞,因为它拿不到锁
                        synchronized (syncObj) {
                            // 等在这里，直到唤醒
                            syncObj.wait();
                        }
                        isProducerConsumer = true;
                    }

                    // 使用生产者与消费者后，数据从队列取，直到正常结束
                    byte [] audio = rawDataS.take();
                    writeData(audio);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**********************************************************************************************/
    /************************************【语音回调处理】******************************************/
    /**********************************************************************************************/
    /**
     * 开始说话
     */
    private void onBegin_(){
        LogUtils.e(TAG, "onBeginOfSpeech");
        // 开始说话时间
        beginTime = System.currentTimeMillis();

        // 取消之前的订阅，如果有的话
        if(null != timeSubscription) {
            timeSubscription.unsubscribe();
        }
        // 录音超时处理线程
        timeSubscription = MyRxUtils.doAsyncRun(new Runnable() {
            @Override
            public void run() {

                // 超时之后立即停止录音，然后上传
                LogUtils.e(TAG, "stopUnderstand");
                stopUnderstand();

                LogUtils.e(TAG, "stopRecognizer");
                stopRecognizer();

                onEnd_();
            }
        }, RECORD_TIME_OUT);
    }

    /**
     * 结束说话
     */
    private void onEnd_(){

        mTestVipChannel.onEndOfSpeech();

        // 结束说话时间
        endTime = System.currentTimeMillis();
        long delta = endTime - beginTime;
        if(delta > RECORD_TIME_OUT){
            LogUtils.e(TAG, "onEndOfSpeech timeout -- " + rawDataS.size() + " need clear");
            prettyEnd();
        }else {
            // 当作正常处理
            if (delta > ADVANCE_TIME) {
                LogUtils.e(TAG, "onEndOfSpeech normally -- " + rawDataS.size() + " need clear");
                prettyEnd();
            }
            // 端点异常，过早返回【现在设计是3秒内返回就算异常】
            // 如果这个回调onError
            // 如果这个回调onResult
            else {
                LogUtils.e(TAG, "onEndOfSpeech inAdvance -- " + rawDataS.size() + " in use");
                isFinishInAdvance = true;
            }
        }
    }


    /**
     * 说话异常
     * @param error
     */
    private void onError_(Throwable error){

        // 只是打印下错误日志
        SpeechError speechError = (SpeechError) error;

        mTestVipChannel.onError(speechError.getPlainDescription(false));

        // 【前端点异常，提前结束】
        if(isFinishInAdvance) {
            LogUtils.e(TAG, "onError inAdvance", error);

            // 队列里面有未消费的数据，才使用此模式
            if(rawDataS.size() > 0) {
                // 使用生产者与消费者模式
                synchronized (syncObj) {
                    syncObj.notify();
                }
            }

            // 前端点异常，继续输入
            continueListen();
        }

        // 【正常结束，则做理解次数记录】
        else {
            ++mErrorCount;
            LogUtils.e(TAG, "onError normally -- ErrorCount = " + mErrorCount);

            try {
                String errMsg = mContext.getString(R.string.who_is_speaking);
                if(null != speechError) {
                    if (NO_VOICE == speechError.getErrorCode()) {
                        errMsg = noVoices[CheckUtils.getRandom(noVoices.length)];
                        LogUtils.e(TAG, errMsg, speechError);
                    }
                    else if (NO_NET == speechError.getErrorCode()) {
                        errMsg = mContext.getString(R.string.no_net_use);
                        LogUtils.e(TAG, errMsg, speechError);
                    }
                    else {
                        // 获取结果超时.    (错误码:20002)
                        // 网络连接发生异常.(错误码:10114)
                        LogUtils.e(TAG, "", speechError);
                    }
                }

                // 一次没说话，继续监听
                if(1 == mErrorCount) {
                    // 显示悬浮表情
                    if (mListenCheck.isUseVoiceFloat()) {
                        startTTS(errMsg, new Runnable() {
                            @Override
                            public void run() {
                                mListenCheck.endListenFace();
                                continueListen();
                                mListenCheck.startListenFace();
                            }
                        });
                    }
                    // 不显示悬浮表情【翻译的时候就不显示】
                    else {
                        mMindHandler.parseMindData(speechError.getPlainDescription(false), UNDERSTAND_FAILURE);
                    }
                }

                // 二次没说话，结束监听
                else if(mErrorCount > 1){
                    mMindHandler.parseMindData(speechError.getPlainDescription(false), UNDERSTAND_FAILURE);
                }

            }catch (Exception e){

            }
        }
    }

    /**********************************************************************************************/
    /*************************************【语义理解的回调】***************************************/
    /**********************************************************************************************/
    /**
     * 【自行处理】语义理解的回调
     */
    private class UnderstandListener implements UnderstandCallback{

        @Override
        public void onVolumeChanged(int volume) {
            mListenCheck.sendVolume(volume);
        }

        // 这个五麦没有回调
        @Override
        public void onBeginOfSpeech() {
            onBegin_();
        }

        @Override
        public void onEndOfSpeech() {
            onEnd_();
        }

        @Override
        public void onResult(String result) {
            LogUtils.e(TAG, "Understand: onResult = " + result);
            // 【前端点异常，却正常结束】
            if(isFinishInAdvance){
                prettyEnd();
            }
            mMindHandler.parseMindData(result, UNDERSTAND_SUCCESS);
        }

        @Override
        public void onError(Throwable error) {
            onError_(error);
        }
    }



    /**********************************************************************************************/
    /*********************************【语音识别的回调】*******************************************/
    /**********************************************************************************************/
    /**
     * 【自行处理】语音识别的回调
     */
    private class RecognizerListener implements RecognizerCallback{

        @Override
        public void onVolumeChanged(int volume) {
            mListenCheck.sendVolume(volume);
        }

        @Override
        public void onBeginOfSpeech() {
            onBegin_();
        }

        @Override
        public void onEndOfSpeech() {
            onEnd_();

            if(!IS_USE_CHINESE){
                stopRecognizer();
            }
        }

        @Override
        public void onResult(String result, int flag) {
            LogUtils.e(TAG, "Recognizer: onResult = " + result);

            // 【前端点异常，却正常结束】
            if(isFinishInAdvance){
                prettyEnd();
            }

            if(IS_USE_CHINESE) {
                if (LISTEN_UNDERSTAND == flag) {
                    mMindHandler.parseMindData(result, UNDERSTAND_SUCCESS);
                } else {
                    mMindHandler.parseAsrData(result, flag);
                }
            }else {
                // 英文的话，这个是用来断句的
            }
        }

        @Override
        public void onError(Throwable error) {
            // 【前端点异常，提前结束】
            if(isFinishInAdvance) {
                LogUtils.e(TAG, "onError inAdvance", error);

                // 使用生产者与消费者模式
                synchronized (syncObj) {
                    syncObj.notify();
                }

                // 前端点异常，继续输入
                continueListen();
            }
            // 【正常结束】
            else {
                if(IS_USE_CHINESE) {
                    mMindHandler.parseAsrData(error.getMessage(), LISTEN_ERROR);
                }else {
                    // 英文的话，这个是用来断句的
                }
            }

        }
    }


    /**********************************************************************************************/
    /*******************************【文字理解的回调】*********************************************/
    /**********************************************************************************************/
    /**
     * 【自行处理】文字理解的回调
     */
    private class TextUnderListener implements TextUnderCallback{

        @Override
        public void onResult(String result) {
            LogUtils.e(TAG, "TextUnder: onResult = " + result);
            mMindHandler.parseMindData(result, UNDERSTAND_SUCCESS);
        }

        @Override
        public void onError(Throwable e) {
            LogUtils.e(TAG, "TextUnder: onError", e);
        }
    }

    /******************【重载发音方法，目地就是说话的时候闪蓝色呼吸灯】****************************/
    /**
     * 发音方法重载
     * @param words ---------------- 发音要读的文字
     * @param from  ---------------- 携带的标志
     * @param synthesizerCallback
     */
    public void startTTS_(String words, String from, final SynthesizerCallback synthesizerCallback) {
        super.startTTS(words, from, new SynthesizerCallback() {
            @Override
            public void OnBegin() {
                LedUtils.startSpeakLed(mContext);
                if(null != synthesizerCallback){
                    synthesizerCallback.OnBegin();
                }
            }

            @Override
            public void OnPause() {
                if(null != synthesizerCallback){
                    synthesizerCallback.OnPause();
                }
            }

            @Override
            public void OnResume() {
                if(null != synthesizerCallback){
                    synthesizerCallback.OnResume();
                }
            }

            @Override
            public void OnComplete(Throwable throwable, String tag) {
                LedUtils.endSpeakLed(mContext);
                if(null != synthesizerCallback){
                    synthesizerCallback.OnComplete(throwable, tag);
                }
            }
        });
    }

    /**
     * 发音方法重载
     * @param words  -------------------- 发音要读的文字
     * @param runnable ------------------ 操作完之后的回调
     */
    public void startTTS_(final String words, String from, final Runnable runnable) {
        super.startTTS(words, from, new SynthesizerCallback() {
            @Override
            public void OnBegin() {
                if(!StringUtils.isEmpty(words)) {
                    LedUtils.startSpeakLed(mContext);
                }

            }

            @Override
            public void OnPause() {

            }

            @Override
            public void OnResume() {

            }

            @Override
            public void OnComplete(Throwable throwable, String tag) {
                if(!StringUtils.isEmpty(words)) {
                    LedUtils.endSpeakLed(mContext);
                }

                if(null != runnable){
                    runnable.run();
                }
            }
        });
    }

    /**
     * 发音方法重载
     * @param words  -------------------- 发音要读的文字
     * @param runnable ------------------ 操作完之后的回调
     */
    public void startTTS_(String words, Runnable runnable) {
        startTTS_(words, null, runnable);
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
        /*
        boolean isUseAppIdXiaoYong = SystemProperties.getBoolean(isAppIdXiaoYong, false);
        if(isUseAppIdXiaoYong){
            startTTS_(words, from, synthesizerCallback);
        }else {
        }
        */
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
        /*
        boolean isUseAppIdXiaoYong = SystemProperties.getBoolean(isAppIdXiaoYong, false);
        if(isUseAppIdXiaoYong){
            startTTS_(words, from, runnable);
        }else {
        }
        */
        SystemPresenter.getInstance().startTTS(words, from, runnable);
    }

    /**
     * 发音方法重载【默认不处理，语音的回调逻辑】
     * @param words  -------------------- 发音要读的文字
     * @param runnable
     */
    @Override
    public void startTTS(String words, Runnable runnable) {
        /*
        boolean isUseAppIdXiaoYong = SystemProperties.getBoolean(isAppIdXiaoYong, false);
        if(isUseAppIdXiaoYong){
            startTTS_(words, runnable);
        }else {
        }
        */
        SystemPresenter.getInstance().startTTS(words, TTS_NOT_DEAL_RESPONSE, runnable);
    }

    @Override
    public void pauseTTS()  {
        /*
        boolean isUseAppIdXiaoYong = SystemProperties.getBoolean(isAppIdXiaoYong, false);
        if(isUseAppIdXiaoYong){
            super.pauseTTS();
        }else {
        }
        */
        SystemPresenter.getInstance().pauseTTS();
    }

    @Override
    public void resumeTTS() {
        /*
        boolean isUseAppIdXiaoYong = SystemProperties.getBoolean(isAppIdXiaoYong, false);
        if(isUseAppIdXiaoYong){
            super.resumeTTS();
        }else {
        }
        */
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
        /*
        boolean isUseAppIdXiaoYong = SystemProperties.getBoolean(isAppIdXiaoYong, false);
        if(isUseAppIdXiaoYong){
            super.stopTTS();
        }else {
        }
        */
        SystemPresenter.getInstance().stopTTS();
    }

    @Override
    public boolean isSpeaking() {
        /*
        boolean isUseAppIdXiaoYong = SystemProperties.getBoolean(isAppIdXiaoYong, false);
        if(isUseAppIdXiaoYong){
            return super.isSpeaking();
        }else {
        }
        */
        return SystemPresenter.getInstance().isSpeaking();
    }


    /*****************************【自身成员变量的读写】*******************************************/
    public boolean isTouchWake() {
        return isTouchWake;
    }

    public void setTouchWake(boolean touchWake) {
        isTouchWake = touchWake;
    }

    public boolean isUseExpression() {
        return isUseExpression;
    }

    public void setUseExpression(boolean useExpression) {
        isUseExpression = useExpression;
    }

    /**
     * 是不是继续循环监听
     * @return
     */
    public boolean isContinueListen(){
        return mListenCheck.isContinueListen();
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
