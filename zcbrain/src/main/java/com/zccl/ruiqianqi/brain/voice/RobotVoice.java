package com.zccl.ruiqianqi.brain.voice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.SpeechError;
import com.zccl.ruiqianqi.beans.ReportBean;
import com.zccl.ruiqianqi.brain.R;
import com.zccl.ruiqianqi.brain.eventbus.MindBusEvent;
import com.zccl.ruiqianqi.brain.service.FloatListen;
import com.zccl.ruiqianqi.mind.eventbus.MainBusEvent;
import com.zccl.ruiqianqi.mind.voice.iflytek.Configuration;
import com.zccl.ruiqianqi.mind.voice.iflytek.FlyTekVoice;
import com.zccl.ruiqianqi.mind.voice.iflytek.beans.BaseInfo;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.plugin.voice.WakeInfo;
import com.zccl.ruiqianqi.presentation.presenter.BatteryPresenter;
import com.zccl.ruiqianqi.presentation.presenter.PersistPresenter;
import com.zccl.ruiqianqi.presentation.presenter.ReportPresenter;
import com.zccl.ruiqianqi.presentation.presenter.StatePresenter;
import com.zccl.ruiqianqi.tools.CheckUtils;
import com.zccl.ruiqianqi.tools.JsonUtils;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.MYUIUtils;
import com.zccl.ruiqianqi.tools.MyAppUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.tools.SystemUtils;
import com.zccl.ruiqianqi.tools.executor.rxutils.MyRxUtils;
import com.zccl.ruiqianqi.utils.AppUtils;
import com.zccl.ruiqianqi.utils.LedUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.LinkedBlockingQueue;

import rx.Subscription;

import static com.zccl.ruiqianqi.config.MyConfig.INTENT_ACTION_STOP;
import static com.zccl.ruiqianqi.config.MyConfig.KEY_RESULT;
import static com.zccl.ruiqianqi.config.MyConfig.KEY_STOP_FROM;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_EMOTION_CHAT;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncIntent.INTENT_STOP_OTHER;
import static com.zccl.ruiqianqi.plugin.voice.AbstractVoice.RecognizerCallback.LISTEN;
import static com.zccl.ruiqianqi.plugin.voice.AbstractVoice.RecognizerCallback.OFFLINE_WORD;
import static com.zccl.ruiqianqi.plugin.voice.AbstractVoice.RecognizerCallback.ONLINE_WORD;
import static com.zccl.ruiqianqi.plugin.voice.AbstractVoice.UnderstandCallback.UNDERSTAND_FAILURE;
import static com.zccl.ruiqianqi.plugin.voice.AbstractVoice.UnderstandCallback.UNDERSTAND_SUCCESS;

/**
 * Created by ruiqianqi on 2017/3/6 0006.
 */

public class RobotVoice extends FlyTekVoice {

    // 类标志
    private static String TAG = RobotVoice.class.getSimpleName();

    // 处理链
    private BaseHandler mFirstHandler;
    // 监听入口处理类
    private ListenCheck mListenCheck;
    // 声源定位
    private Localization mLocalization;

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
    private VipTest mVipTest;

    public RobotVoice(Context context) {
        super(context);
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        mFirstHandler = new FirstHandler(mContext, this);
        BaseHandler otherHandler = new OtherHandler(mContext, this);
        mFirstHandler.setSuccessor(otherHandler);
        mListenCheck = new ListenCheck(mContext, this);
        mLocalization = new Localization(mContext);

        rawDataS = new LinkedBlockingQueue<>();

        noVoices = new String[]{
                mContext.getString(R.string.no_audio_input),
                mContext.getString(R.string.no_audio_input1),
                mContext.getString(R.string.no_audio_input2),
                mContext.getString(R.string.no_audio_input3)};

        // 开启音频输入线程
        MyRxUtils.doNewThreadRun(new Consumer());

        mVipTest = new VipTest(mContext, this);
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

    /**
     * 切换语言
     * @param language
     */
    @Override
    public void switchLanguage(String language) {
        Configuration.Language = language;
        voiceUnderstander.setLanguage(language);
        voiceRecognizer.setLanguage(language);
    }

    /**********************************************************************************************/
    /***********************************【结果解析】***********************************************/
    /**********************************************************************************************/
    /**
     * 语义理解解析
     * @param json
     */
    private void parseMindData(String json, int type){

        // VIP通道测试
        BaseInfo testBaseInfo = JsonUtils.parseJson(json, BaseInfo.class);
        if (null != testBaseInfo) {
            if(testBaseInfo.getText().contains(mVipTest.startVip)){
                mVipTest.create();
                return;
            }else if(testBaseInfo.getText().contains(mVipTest.endVip)){
                mVipTest.close();
                return;
            }
            mVipTest.onResult(testBaseInfo.getText());
        }

        // 【第一次场景，处理】
        if(!mFirstHandler.handlerScene(json, type)){
            if(UNDERSTAND_FAILURE == type){
                LogUtils.e(TAG, "parseMindData = " + mContext.getString(R.string.under_error));

            }else {
                BaseInfo baseInfo = JsonUtils.parseJson(json, BaseInfo.class);
                if (null != baseInfo) {
                    // 【第二次语义，处理】
                    mFirstHandler.handleSemantic(baseInfo.getServiceType(), json);

                } else {
                    LogUtils.e(TAG, "parseMindData = " +  mContext.getString(R.string.parse_under_error));
                }
            }
        }
    }

    /**
     * 语音识别解析
     * @param result
     */
    private void parseAsrData(String result, int type){
        if(LISTEN == type){
            String word = result;
            mFirstHandler.handleAsr(word, type);

        }else if(ONLINE_WORD == type){
            String word = result;
            mFirstHandler.handleAsr(word, type);

        }else if(OFFLINE_WORD == type){
            /*
            String res[] = result.split("\\|");
            if(3 == res.length) {
                String word = res[0];
                String slot[] = res[1].split("\\*");
                String id[] = res[2].split("\\*");
                if(slot.length == id.length){
                }
                mFirstHandler.handleAsr(word, type);
            }
            */
            mFirstHandler.handleAsr(result, type);

        }else {
            mFirstHandler.handleAsr(result, type);
        }

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
     *
     * @param obj
     */
    @Override
    public void notifyChange(int flag, Object obj) {
        if(null == obj)
            return;
        try {
            // 网络状态改变了
            if(NET_CHANGE == flag){
                MainBusEvent.NetEvent event = (MainBusEvent.NetEvent) obj;
                StatePresenter sp = StatePresenter.getInstance();
                sp.setNetConnected(event.isConn());

                ReportBean reportBean;
                if(event.isConn()){
                    // 网络已连接，当前网络为
                    reportBean = ReportBean.obtain(ReportBean.CODE_TTS, mContext.getString(R.string.have_net) + event.getText());
                    //reportBean = null;
                }else {
                    // 网络已断开
                    reportBean = ReportBean.obtain(ReportBean.CODE_TTS, mContext.getString(R.string.no_net) /*+ event.getText()*/);
                    /*
                    sp.setRobotState(STATE_CONNECT_OFF);
                    sp.setInControl(false);
                    sp.setControlId(null);
                    */
                }
                ReportPresenter.report(reportBean);
            }
            // 电话状态改变了
            else if(PHONE_CHANGE == flag){
                MainBusEvent.PhoneEvent phoneEvent = (MainBusEvent.PhoneEvent) obj;
                String status = phoneEvent.getStatus();
                if(StringUtils.isEmpty(status)) {
                    return;
                }
                StatePresenter sp = StatePresenter.getInstance();
                if(status.equals(mContext.getString(R.string.phone_idle))){
                    sp.setCalling(false);

                }
                // 摘机
                else if(status.equals(mContext.getString(R.string.phone_off_hook))){
                    sp.setCalling(true);
                    cancelExpression();

                }
                // 响铃
                else if(status.equals(mContext.getString(R.string.phone_ringing))){
                    sp.setCalling(true);
                    cancelExpression();

                }
                // 拨打
                else if(status.equals(mContext.getString(R.string.phone_dialing))){
                    sp.setCalling(true);
                    cancelExpression();

                }
            }
            // 传感器状态改变了
            else if(SENSOR_CHANGE == flag){
                MainBusEvent.SensorEvent sensorEvent = (MainBusEvent.SensorEvent) obj;
                String text = sensorEvent.getText();

                LogUtils.e(TAG,  text + "");

                if(StringUtils.isEmpty(text)) {
                    return;
                }

                // 触摸唤醒，一切从头开始
                if(text.equals(mContext.getString(R.string.sensor_touch))){

                    StatePresenter sp = StatePresenter.getInstance();
                    // 屏幕灭了，触摸就不响应
                    if(sp.isScreenOff()){
                        return;
                    }

                    isTouchWake = true;
                    isUseExpression = true;
                    firstUnderstand(mContext.getString(R.string.sensor_touch), true, isUseExpression);

                }
                // 语音唤醒，一切从头开始
                /*
                else if(text.equals(mContext.getString(R.string.sensor_voice))){
                    isTouchWake = false;
                    firstUnderstand(mContext.getString(R.string.sensor_voice), true, isTouchWake);
                }
                */
                else if(text.equals(mContext.getString(R.string.sensor_left_arm))){

                }
                else if(text.equals(mContext.getString(R.string.sensor_right_arm))){

                }
                else if(text.equals(mContext.getString(R.string.sensor_dance))){
                    mFirstHandler.handlerFunc(mContext.getString(R.string.sensor_dance));
                }

                // 打开五麦
                else if(text.equals("5micon")){
                    startWakeup();
                    StatePresenter sp = StatePresenter.getInstance();
                    sp.setVideoing(false);
                    LogUtils.f("5micon", System.currentTimeMillis() + "：5micon\n");

                }
                // 关闭五麦
                else if(text.equals("5micoff")){
                    stopWakeup();
                    StatePresenter sp = StatePresenter.getInstance();
                    sp.setVideoing(true);
                    cancelExpression();
                    LogUtils.f("5micoff", System.currentTimeMillis() + "：5micoff\n");

                }

                else if(text.equals(mContext.getString(R.string.hdmi_long_press))){

                }
                else if(text.equals(mContext.getString(R.string.hdmi_short_press))){

                }
                // 开屏
                else if(text.equals(mContext.getString(R.string.screen_on))){
                    LedUtils.endScreenOnLed(mContext);

                    StatePresenter sp = StatePresenter.getInstance();
                    sp.setScreenOff(false);

                }
                // 锁屏
                else if(text.equals(mContext.getString(R.string.screen_off))){
                    LedUtils.startScreenOffLed(mContext);

                    StatePresenter sp = StatePresenter.getInstance();
                    sp.setScreenOff(true);

                    // 停止主服务的功能
                    cancelExpression();

                    // 停止其他应用的功能
                    stopOtherAppFunc(text);
                }

                else if(text.equals(mContext.getString(R.string.user_present))){

                }
                else if(text.equals(mContext.getString(R.string.shutdown))){

                }
            }

            // 电池状态变化了
            else if(BATTERY_CHANGE == flag){
                MainBusEvent.BatteryEvent batteryEvent = (MainBusEvent.BatteryEvent) obj;
                BatteryPresenter.getInstance().dealWithBattery(batteryEvent);
            }

            // APP状态改变了
            else if(APP_STATUS_CHANGE == flag){
                MainBusEvent.AppStatusEvent appStatusEvent = (MainBusEvent.AppStatusEvent) obj;
                String action = appStatusEvent.getAction();
                if(StringUtils.isEmpty(action)) {
                    return;
                }
                StatePresenter sp = StatePresenter.getInstance();
                if(action.equals(mContext.getString(R.string.entry_video_monitor))){
                    sp.setVideoing(true);
                    //stopWakeup();
                    cancelExpression();

                }else if(action.equals(mContext.getString(R.string.exit_video_monitor))){
                    sp.setVideoing(false);
                    //startWakeup();

                }else if(action.equals(mContext.getString(R.string.entry_video_comm))){
                    sp.setVideoing(true);
                    //stopWakeup();
                    cancelExpression();

                }else if(action.equals(mContext.getString(R.string.exit_video_comm))){
                    sp.setVideoing(false);
                    //startWakeup();
                }
                // 进入工厂模式
                else if(action.equals(mContext.getString(R.string.factory_start))){
                    sp.setFactory(true);
                    cancelExpression();

                }
                // 退出工厂模式
                else if(action.equals(mContext.getString(R.string.factory_close))){
                    sp.setFactory(false);

                }
            }

            // 任务完成后的循环监听来了
            else if(RECYCLE_LISTEN == flag){

                if(mVipTest.isTestVip())
                    return;

                StatePresenter sp = StatePresenter.getInstance();
                // 屏幕灭了， 循环监听就不响应
                if(sp.isScreenOff()){
                    return;
                }

                MainBusEvent.ListenEvent listenEvent = (MainBusEvent.ListenEvent) obj;
                LogUtils.e(TAG, "RECYCLE_LISTEN from = " + listenEvent.getText());
                if(StringUtils.isEmpty(listenEvent.getText())){

                }else {
                    LogUtils.e(TAG, "VoiceFloat = " + listenEvent.isUseVoiceFloat());
                    LogUtils.e(TAG, "Expression = " + listenEvent.isUseExpression());
                    firstUnderstand(listenEvent.getText(), listenEvent.isUseVoiceFloat(), listenEvent.isUseExpression());
                }
            }

            // 停止监听
            else if(STOP_LISTEN == flag){
                MainBusEvent.ListenEvent listenEvent = (MainBusEvent.ListenEvent) obj;

                LogUtils.e(TAG, "STOP_LISTEN from = " + listenEvent.getText());
                if(StringUtils.isEmpty(listenEvent.getText())){

                }else {

                    // 单击悬浮按钮结束监听
                    if(FloatListen.TAG.equals(listenEvent.getText())){
                        // 结束监听
                        cancelUnderstand();
                        // 结束监听悬浮
                        mListenCheck.endListenFace();
                    }
                    // 其他情况结束监听
                    else {
                        cancelExpression();
                    }

                }
            }

        }catch (Exception e){
            LogUtils.e(TAG, "notifyChange", e);
        }

    }

    /**********************************************************************************************/
    /************************************【开始语义理解】******************************************/
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
    public void firstUnderstand(String fromWhere, boolean isUseVoiceFloat, boolean isUseExpression){

        LogUtils.e(TAG, "fromWhere = " + fromWhere);

        boolean isShow = mListenCheck.isShowExpression();
        isUseExpression = isShow == false ? isShow : isUseExpression;

        // 发STOP广播
        stopOtherAppFunc(fromWhere);

        // 唤醒之后立即结束监听
        cancelUnderstand();
        prettyEnd();
        stopTTS();

        mErrorCount = 0;
        // 一次正常的会话开始
        isSessionGoing = true;
        // 是正常结束
        isFinishInAdvance = false;

        // 设置是否显示悬浮框
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
            continueListen(true);
        }
    }

    /**
     * 再次开始监听，仅仅是再次开启监听
     * @param isUnderStand true语义理解 false语音识别
     */
    private void continueListen(boolean isUnderStand){
        // 一次正常的会话开始
        isSessionGoing = true;

        if(isUnderStand) {
            startUnderstand();
        }else {
            startRecognizer();
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
    protected void cancelExpression(){
        LogUtils.e(TAG, "cancelExpression");

        // 结束发音
        stopTTS();

        // 结束监听
        cancelUnderstand();

        // 结束监听悬浮
        mListenCheck.endListenFace();

        // 结束大表情
        /*
        MindBusEvent.ExpressionEvent expressionEvent = new MindBusEvent.ExpressionEvent();
        EventBus.getDefault().post(expressionEvent);
        */

        // 结束大表情
        AppUtils.endEmotion(mContext);

    }

    /**
     *
     * 监听，调用
     * 安静，调用
     * 灭屏，调用
     *
     * 停止其他应用的功能
     * @param fromWhere
     */
    protected void stopOtherAppFunc(String fromWhere){
        Bundle args = new Bundle();
        args.putString(KEY_STOP_FROM, fromWhere);

        // 只关闭master
        //MyAppUtils.sendBroadcast(mContext, INTENT_STOP_OTHER, args);

        // 关闭所有，但是launcher接收之后，会点亮屏幕
        MyAppUtils.sendBroadcast(mContext, INTENT_ACTION_STOP, args);

    }

    /**********************************************************************************************/
    /************************************【开始语音识别】******************************************/
    /**********************************************************************************************/
    /**
     * 开始第一次语音识别
     */
    protected void firstAsr(){

        // 结束监听
        cancelRecognizer();

        // 一次正常的会话开始
        isSessionGoing = true;
        // 是正常结束
        isFinishInAdvance = false;
        // 开始识别
        startRecognizer(2);
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

            /*
            Toast.makeText(mContext, "S:" + wakeInfo.getScore() +
                    " A:" + wakeInfo.getAngle() +
                    " N:" + wakeInfo.getBeam(), Toast.LENGTH_LONG).show();
            */

            PersistPresenter cp = PersistPresenter.getInstance();
            if(wakeInfo.getScore() >= cp.getThreshold()){

                // 唤醒亮屏，对手机好像没什么用
                SystemUtils.wakeUp(mContext);

                if(0 != wakeInfo.getAngle()){
                    StatePresenter sp = StatePresenter.getInstance();
                    boolean canLocal = true;
                    // 机器人在电话中
                    if(sp.isCalling()){
                        canLocal = false;
                    }
                    // 机器人正在工厂模式中
                    else if(sp.isFactory()){
                        canLocal = false;
                    }
                    // 机器人正在视频中
                    else if(sp.isVideoing()){
                        canLocal = false;
                    }
                    // 机器人在控制中
                    else if(sp.isInControl()){
                        canLocal = false;
                    }
                    // 声源定位在哪唤醒，拾音波束就在哪，如果进行声源定位了，就要进行波束重置
                    // 如果没有进行声源定位，拾音波束就是上一次唤醒的波束
                    if(canLocal && cp.isLocalization()){
                        mLocalization.rotate(wakeInfo.getAngle());
                        voiceWakeUp.setRealBeam(0);
                    }
                }

                // 发TouchSensor广播，为什么不直接调用监听呢
                // 因为TouchSensor广播，能触发表情，就是那个两眼一瞪的眼睛
                /*
                Bundle args = new Bundle();
                args.putString(SensorReceiver.TOUCH_KEY, SensorReceiver.TOUCH_HEAD_VALUE);
                args.putString(SensorReceiver.TOUCH_WAKE_KEY, SensorReceiver.VOICE_WAKE_VALUE);
                MyAppUtils.sendBroadcast(mContext, SensorReceiver.TOUCH_SENSOR, args);
                */

                isTouchWake = false;
                isUseExpression = true;
                firstUnderstand(mContext.getString(R.string.sensor_voice), true, isUseExpression);

            }else {
                wakeFailure(new Throwable(mContext.getString(R.string.wakeup_invalid)));
            }
        }

        /**
         * 五麦回调的数据
         *
         * 五麦一直返回音频数据，对于单麦来讲，这个是没有用的
         * 因为单麦根本就没有回调这个接口
         * @param audio
         * @param audioLen
         */
        @Override
        public void onAudio(byte[] audio, int audioLen) {

            // 原始语音音频生产者，监听期间一直BAK
            if(isSessionGoing){
                rawDataS.offer(audio);
            }

            // 如果是正常结束，则直接消费音频数据
            if(!isProducerConsumer){
                writeUnderstand(audio);
                writeRecognizer(audio);
            }
        }

        @Override
        public boolean isTouchWake() {
            return isTouchWake && voiceUnderstander.isListening();
        }

    }

    /**
     * 原始语音音频消费者
     */
    private class Consumer implements Runnable{
        @Override
        public void run() {
            try {
                // 在调用sleep()方法的过程中，线程不会释放对象锁。
                // 而当调用wait()方法的时候，线程会放弃对象锁，进入等待此对象的等待锁定池，
                // 只有针对此对象调用notify()方法后本线程才进入对象锁定池准备
                while (true) {
                    if(!isProducerConsumer) {
                        // 不打算锁在while循环外边,因为这样的话,循环一旦运行起来,就一直持有锁了
                        // 那样的话调用notify()的线程就会阻塞,因为它拿不到锁
                        synchronized (syncObj) {
                            // 等在这里，直到唤醒
                            syncObj.wait();
                        }
                        isProducerConsumer = true;
                    }

                    // 唤醒之后，队列一直写，直到正常结束
                    byte [] audio = rawDataS.take();
                    writeUnderstand(audio);
                    writeRecognizer(audio);

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
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
            parseMindData(result, UNDERSTAND_SUCCESS);
        }

        @Override
        public void onError(Throwable error) {
            onError_(error);
        }
    }

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

                LogUtils.e(TAG, "stopUnderstand");
                stopUnderstand();

                LogUtils.e(TAG, "stopRecognizer");
                stopRecognizer();

                //prettyEnd();
                onEnd_();
            }
        }, RECORD_TIME_OUT);
    }

    /**
     * 结束说话
     */
    private void onEnd_(){

        mVipTest.onEndOfSpeech();

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
                LogUtils.e(TAG, "onEndOfSpeech in advance -- " + rawDataS.size() + " in use");
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

        mVipTest.onError(speechError.getPlainDescription(false));

        // 【前端点异常，提前结束】
        if(isFinishInAdvance) {
            LogUtils.e(TAG, "onError in advance", error);

            // 队列里面有未消费的数据，才使用此模式
            if(rawDataS.size() > 0) {
                // 使用生产者与消费者模式
                synchronized (syncObj) {
                    syncObj.notify();
                }
            }

            // 前端点异常，继续输入
            continueListen(true);
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
                                continueListen(true);
                                mListenCheck.startListenFace();
                            }
                        });
                    }
                    // 不显示悬浮表情【翻译的时候就不显示】
                    else {
                        parseMindData(speechError.getPlainDescription(false), UNDERSTAND_FAILURE);
                    }

                }

                // 二次没说话，结束监听
                else if(mErrorCount > 1){
                    parseMindData(speechError.getPlainDescription(false), UNDERSTAND_FAILURE);
                }

            }catch (Exception e){

            }
        }
    }

    /**********************************************************************************************/
    /**
     * 【自行处理】文字理解的回调
     */
    private class TextUnderListener implements TextUnderCallback{

        @Override
        public void onResult(String result) {
            parseMindData(result, UNDERSTAND_SUCCESS);
        }

        @Override
        public void onError(Throwable e) {
            LogUtils.e(TAG, "TextUnder: onError", e);
        }
    }

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
        }

        @Override
        public void onResult(String result, int flag) {
            LogUtils.e(TAG, "Recognizer: onResult = " + result);

            // 【前端点异常，却正常结束】
            if(isFinishInAdvance){
                prettyEnd();
            }

            if(LISTEN_UNDERSTAND == flag){
                parseMindData(result, UNDERSTAND_SUCCESS);
            }else {
                parseAsrData(result, flag);
            }
        }

        @Override
        public void onError(Throwable error) {
            // 【前端点异常，提前结束】
            if(isFinishInAdvance) {
                LogUtils.e(TAG, "onError in advance", error);
                // 使用生产者与消费者模式
                synchronized (syncObj) {
                    syncObj.notify();
                }
                // 前端点异常，继续输入
                continueListen(false);
            }
            // 【正常结束】
            else {
                parseAsrData(error.getMessage(), LISTEN_ERROR);
            }

        }
    }

    /******************【重载发音方法，目地就是说话的时候闪蓝色呼吸灯】****************************/
    /**
     * 发音方法重载
     * @param words
     * @param tag
     * @param synthesizerCallback
     */
    @Override
    public void startTTS(String words, String tag, final SynthesizerCallback synthesizerCallback) {
        super.startTTS(words, tag, new SynthesizerCallback() {
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
     * @param runnable
     */
    @Override
    public void startTTS(String words, final Runnable runnable) {
        super.startTTS(words, null, new SynthesizerCallback() {
            @Override
            public void OnBegin() {
                LedUtils.startSpeakLed(mContext);
            }

            @Override
            public void OnPause() {

            }

            @Override
            public void OnResume() {

            }

            @Override
            public void OnComplete(Throwable throwable, String tag) {
                LedUtils.endSpeakLed(mContext);
                if(null != runnable){
                    runnable.run();
                }
            }
        });
    }

}
