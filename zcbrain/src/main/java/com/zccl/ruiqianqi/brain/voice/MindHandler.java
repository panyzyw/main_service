package com.zccl.ruiqianqi.brain.voice;

import android.content.Context;
import android.media.MediaPlayer;

import com.google.gson.Gson;
import com.zccl.ruiqianqi.beans.ReportBean;
import com.zccl.ruiqianqi.brain.R;
import com.zccl.ruiqianqi.brain.handler.BaseHandler;
import com.zccl.ruiqianqi.brain.handler.FirstHandler;
import com.zccl.ruiqianqi.brain.handler.LogHandler;
import com.zccl.ruiqianqi.brain.handler.OtherHandler;
import com.zccl.ruiqianqi.brain.handler.SDKHandler;
import com.zccl.ruiqianqi.brain.handler.SecondHandler;
import com.zccl.ruiqianqi.brain.semantic.flytek.BaseInfo;
import com.zccl.ruiqianqi.brain.semantic.flytek.ExpressionBean;
import com.zccl.ruiqianqi.brain.service.FloatListen;
import com.zccl.ruiqianqi.brain.service.MainService;
import com.zccl.ruiqianqi.domain.model.dataup.LogCollectBack;
import com.zccl.ruiqianqi.mind.eventbus.MainBusEvent;
import com.zccl.ruiqianqi.move.MoveAction;
import com.zccl.ruiqianqi.presentation.presenter.BatteryPresenter;
import com.zccl.ruiqianqi.presentation.presenter.ReportPresenter;
import com.zccl.ruiqianqi.presentation.presenter.StatePresenter;
import com.zccl.ruiqianqi.tools.JsonUtils;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.tools.media.MyMediaPlayer;
import com.zccl.ruiqianqi.utils.AppUtils;
import com.zccl.ruiqianqi.utils.LedUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.zccl.ruiqianqi.brain.handler.BaseHandler.SCENE_MY_MUSIC;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_APP;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_CALL;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_CHAT;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_DICT;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_DISPLAY;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_EMOTION_CHAT;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_FACE;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_GAME;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_GENERIC;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_HABIT;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_HEALTH;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_MOVE;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_MOVIE_INFO;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_MUSIC;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_MUSIC_CTRL;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_MUTE;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_OPERA;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_SMART_HOME;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_SMS;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_SOUND;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_SQUARE_DANCE;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_STORY;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_SWITCH;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_TRANSLATE;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_TRANSLATE_;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_TV_CONTROL;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_VIDEO;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_VIDEO_CTRL;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_WATCH_TV;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_YYD_CAHT;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_YYD_CHAT;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.OP_EMOTION_CHAT;
import static com.zccl.ruiqianqi.plugin.voice.AbstractVoice.APP_STATUS_CHANGE;
import static com.zccl.ruiqianqi.plugin.voice.AbstractVoice.BATTERY_CHANGE;
import static com.zccl.ruiqianqi.plugin.voice.AbstractVoice.HDMI_CHANGE;
import static com.zccl.ruiqianqi.plugin.voice.AbstractVoice.NET_CHANGE;
import static com.zccl.ruiqianqi.plugin.voice.AbstractVoice.PHONE_CHANGE;
import static com.zccl.ruiqianqi.plugin.voice.AbstractVoice.RECYCLE_LISTEN_CHANGE;
import static com.zccl.ruiqianqi.plugin.voice.AbstractVoice.RecognizerCallback.LISTEN;
import static com.zccl.ruiqianqi.plugin.voice.AbstractVoice.RecognizerCallback.OFFLINE_WORD;
import static com.zccl.ruiqianqi.plugin.voice.AbstractVoice.RecognizerCallback.ONLINE_WORD;
import static com.zccl.ruiqianqi.plugin.voice.AbstractVoice.SENSOR_CHANGE;
import static com.zccl.ruiqianqi.plugin.voice.AbstractVoice.STOP_LISTEN_CHANGE;
import static com.zccl.ruiqianqi.plugin.voice.AbstractVoice.UnderstandCallback.UNDERSTAND_FAILURE;

/**
 * Created by ruiqianqi on 2017/6/5 0005.
 */

public class MindHandler {

    // 类标志
    private static String TAG = MindHandler.class.getSimpleName();

    // 全局上下文
    protected Context mContext;
    // 音频处理类
    protected RobotVoice mRobotVoice;
    // 处理链
    private BaseHandler mFirstHandler;
    // VIP通道测试
    private TestVipChannel mTestVipChannel;

    // 我的播放器
    private MyMediaPlayer myMediaPlayer;
    // 播放回调
    private MyMediaPlayer.IPlayerListener playerListener;
    // HDMI是否已挂载
    private boolean isHdmiPlugged;


    // 头是不是朝向右边
    private boolean isHeadRight = false;

    public MindHandler(Context context, RobotVoice robotVoice) {
        this.mContext = context;
        this.mRobotVoice = robotVoice;
        mFirstHandler = new FirstHandler(mContext, mRobotVoice);
        BaseHandler secondHandler = new SecondHandler(mContext, mRobotVoice);
        BaseHandler otherHandler = new OtherHandler(mContext, mRobotVoice);
        secondHandler.setSuccessor(otherHandler);
        mFirstHandler.setSuccessor(secondHandler);

        myMediaPlayer = new MyMediaPlayer(mContext);
        playerListener = new MyMediaPlayer.IPlayerListener() {
            @Override
            public void OnBufferUpdate(int percent) {

            }

            @Override
            public void OnPlayLoad(MyMediaPlayer.MusicPlayLoad musicPlayLoad) {

            }

            @Override
            public void OnPlaying(MediaPlayer mp) {

            }

            @Override
            public void OnProgress(int curTimePos, int duration) {

            }

            @Override
            public void OnPlayEnd(MediaPlayer mp) {
                myMediaPlayer.playAssetsMusic("blankaudio.wav", playerListener);
            }

            @Override
            public void OnPlayError(Throwable e, int what, int extra) {
                myMediaPlayer.playAssetsMusic("blankaudio.wav", playerListener);
            }
        };

    }

    /**********************************************************************************************/
    /***********************************【结果解析】***********************************************/
    /**********************************************************************************************/
    /**
     * 语义理解解析
     * @param json
     */
    public void parseMindData(String json, int type){

        // VIP通道测试
        BaseInfo testBaseInfo = JsonUtils.parseJson(json, BaseInfo.class);
        if (null != testBaseInfo) {
            if(testBaseInfo.getText().contains(mTestVipChannel.startVip)){
                mTestVipChannel.create();
                return;
            }else if(testBaseInfo.getText().contains(mTestVipChannel.endVip)){
                mTestVipChannel.close();
                return;
            }
            mTestVipChannel.onResult(testBaseInfo.getText());
        }

        // 【第一次场景，处理】
        if(!mFirstHandler.handlerScene(json, type)){
            if(UNDERSTAND_FAILURE == type){
                LogUtils.e(TAG, "parseMindData = " + mContext.getString(R.string.under_error));

            }else {
                BaseInfo baseInfo = JsonUtils.parseJson(json, BaseInfo.class);
                if (null != baseInfo) {
                    // 【第二次语义，处理】
                    boolean isHandled = mFirstHandler.handleSemantic(json, baseInfo.getServiceType());
                    if(isHandled){

                    }else {

                    }
                } else {
                    LogUtils.e(TAG, "parseMindData = " +  mContext.getString(R.string.parse_under_error));
                }
            }
        }else {

        }

        // 上传用户操作日志
        LogHandler.logUpdate(mContext, json);
    }





    /**
     * 语音识别解析
     * @param result
     */
    public void parseAsrData(String result, int type){
        if(LISTEN == type){
            mFirstHandler.handleAsr(result, type);

        }else if(ONLINE_WORD == type){
            mFirstHandler.handleAsr(result, type);

        }else if(OFFLINE_WORD == type){
            mFirstHandler.handleAsr(result, type);

        }else {
            mFirstHandler.handleAsr(result, type);
        }

    }

    /**
     * 根据传入的数据，处理相应的功能
     * @param func
     */
    public void handlerFunc(String func){
        mFirstHandler.handlerFunc(func);
    }

    /*****************************【自身成员变量的读写】*******************************************/
    /**
     * 设置VIP测试功能
     * @param testVipChannel
     */
    public void setVipTest(TestVipChannel testVipChannel) {
        this.mTestVipChannel = testVipChannel;
    }

    /****************************【各种状态变化的处理】********************************************/
    /**
     * 各种状态变化的处理
     * @param flag
     * @param obj
     */
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
                    mRobotVoice.cancelExpression(true);

                }
                // 响铃
                else if(status.equals(mContext.getString(R.string.phone_ringing))){
                    sp.setCalling(true);
                    mRobotVoice.cancelExpression(true);

                }
                // 拨打
                else if(status.equals(mContext.getString(R.string.phone_dialing))){
                    sp.setCalling(true);
                    mRobotVoice.cancelExpression(true);

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
                if(text.equals(mContext.getString(R.string.sensor_touch)) ||
                        text.equals(mContext.getString(R.string.qian_e)) ||
                        text.equals(mContext.getString(R.string.hou_nao_shao))){

                    StatePresenter sp = StatePresenter.getInstance();
                    // 屏幕灭了，触摸就不响应
                    if(sp.isScreenOff()){
                        return;
                    }

                    mRobotVoice.setTouchWake(false);
                    // 触摸如果要用回音消除，就用前置拾音
                    if(!mRobotVoice.isTouchWake()){
                        mRobotVoice.setRealBeam(0);
                    }
                    mRobotVoice.setUseExpression(true);
                    mRobotVoice.handlerVoiceEntry(mContext.getString(R.string.sensor_touch), 0, true, mRobotVoice.isUseExpression());

                    // SDK处理
                    mRobotVoice.onSDKReceive(SDKHandler.RECV_SENSOR_HEADER, null);
                }

                // 摸下巴，头部回正
                else if(text.equals(mContext.getString(R.string.sensor_chin))){
                    MoveAction.getInstance(mContext).setDriveType(MoveAction.DRIVE_BY_TIME);
                    MoveAction.getInstance(mContext).setSpeed(60);
                    if(isHeadRight){
                        MoveAction.getInstance(mContext).headRightTurnMid();
                        LogUtils.e(TAG, "headRightTurnMid");
                    }else {
                        MoveAction.getInstance(mContext).headLeftTurnMid();
                        LogUtils.e(TAG, "headLeftTurnMid");
                    }

                    // SDK处理
                    mRobotVoice.onSDKReceive(SDKHandler.RECV_SENSOR_CHIN, null);
                }

                // 左胳膊
                else if(text.equals(mContext.getString(R.string.sensor_left_arm))){
                    isHeadRight = false;
                    MoveAction.getInstance(mContext).setDriveType(MoveAction.DRIVE_BY_TIME);
                    MoveAction.getInstance(mContext).setSpeed(60);
                    MoveAction.getInstance(mContext).headLeftEnd();

                    // SDK处理
                    mRobotVoice.onSDKReceive(SDKHandler.RECV_SENSOR_LEFT_ARM, null);
                }

                // 右胳膊
                else if(text.equals(mContext.getString(R.string.sensor_right_arm))){
                    isHeadRight = true;
                    MoveAction.getInstance(mContext).setDriveType(MoveAction.DRIVE_BY_TIME);
                    MoveAction.getInstance(mContext).setSpeed(60);
                    MoveAction.getInstance(mContext).headRightEnd();

                    // SDK处理
                    mRobotVoice.onSDKReceive(SDKHandler.RECV_SENSOR_RIGHT_ARM, null);
                }
                // 摸双肩
                else if(text.equals(mContext.getString(R.string.sensor_dance))){
                    handlerFunc(mContext.getString(R.string.sensor_dance));

                    // SDK处理
                    mRobotVoice.onSDKReceive(SDKHandler.RECV_SENSOR_LEFT_RIGHT_ARM, null);
                }
                // 紧急按钮
                else if(text.equals(mContext.getString(R.string.sensor_sos))){
                    // SDK处理
                    mRobotVoice.onSDKReceive(SDKHandler.RECV_SENSOR_SOS, null);

                    // 如果是工厂模式就发音
                    StatePresenter sp = StatePresenter.getInstance();
                    if(sp.isFactory()){
                        mRobotVoice.startTTS("SOS", null);
                    }
                }

                // 打开五麦
                else if(text.equals("5micon")){
                    mRobotVoice.startWakeup();
                    StatePresenter sp = StatePresenter.getInstance();
                    sp.setVideoing(false);
                    LogUtils.f("5micon", System.currentTimeMillis() + "：5micon\n");

                }
                // 关闭五麦
                else if(text.equals("5micoff")){
                    mRobotVoice.stopWakeup();
                    StatePresenter sp = StatePresenter.getInstance();
                    sp.setVideoing(true);
                    mRobotVoice.cancelExpression(true);
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
                    mRobotVoice.cancelExpression(true);

                    // 停止其他应用的功能
                    mRobotVoice.stopOtherAppFunc(text);
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
                    mRobotVoice.cancelExpression(true);

                }else if(action.equals(mContext.getString(R.string.exit_video_monitor))){
                    sp.setVideoing(false);

                }else if(action.equals(mContext.getString(R.string.entry_video_comm))){
                    sp.setVideoing(true);
                    mRobotVoice.cancelExpression(true);

                }else if(action.equals(mContext.getString(R.string.exit_video_comm))){
                    sp.setVideoing(false);
                }
                // 进入工厂模式
                else if(action.equals(mContext.getString(R.string.factory_start))){
                    sp.setFactory(true);
                    mRobotVoice.cancelExpression(true);

                }
                // 退出工厂模式
                else if(action.equals(mContext.getString(R.string.factory_close))){
                    sp.setFactory(false);

                }
            }

            // 任务完成后的循环监听来了
            else if(RECYCLE_LISTEN_CHANGE == flag){

                if(mTestVipChannel.isTestVip())
                    return;

                StatePresenter sp = StatePresenter.getInstance();
                // 屏幕灭了， 循环监听就不响应
                if(sp.isScreenOff()){
                    return;
                }

                MainBusEvent.ListenEvent listenEvent = (MainBusEvent.ListenEvent) obj;
                LogUtils.e(TAG, "RECYCLE_LISTEN from = " + listenEvent.getFrom());

                // 某些界面不要循环监听
                if(!mRobotVoice.isContinueListen(listenEvent.getFrom())){
                    return;
                }

                if(StringUtils.isEmpty(listenEvent.getFrom())){

                }else {
                    LogUtils.e(TAG, "VoiceFloat = " + listenEvent.isUseVoiceFloat());
                    LogUtils.e(TAG, "Expression = " + listenEvent.isUseExpression());
                    mRobotVoice.handlerVoiceEntry(listenEvent.getFrom(), 0, listenEvent.isUseVoiceFloat(), listenEvent.isUseExpression());
                }
            }

            // 停止监听
            else if(STOP_LISTEN_CHANGE == flag){
                MainBusEvent.ListenEvent listenEvent = (MainBusEvent.ListenEvent) obj;

                LogUtils.e(TAG, "STOP_LISTEN from = " + listenEvent.getFrom());
                if(StringUtils.isEmpty(listenEvent.getFrom())){

                }else {

                    // 处理停止监听的逻辑
                    handleStopListen(listenEvent.getFrom());

                    // 单击悬浮按钮结束监听
                    if(FloatListen.TAG.equals(listenEvent.getFrom())){
                        mRobotVoice.cancelExpression(false);
                    }
                    // 其他情况结束监听
                    else {
                        mRobotVoice.cancelExpression(true);
                    }

                }
            }

            // HDMI插拔事件
            else if(HDMI_CHANGE == flag){
                MainBusEvent.HdmiEvent hdmiEvent = (MainBusEvent.HdmiEvent) obj;
                if(hdmiEvent.isState()){
                    isHdmiPlugged = true;
                    myMediaPlayer.playAssetsMusic("blankaudio.wav", playerListener);
                }else {
                    isHdmiPlugged = false;
                    myMediaPlayer.stopMusic();
                }
            }

        }catch (Exception e){
            LogUtils.e(TAG, "notifyChange", e);
        }

    }


    /**
     * 处理单击悬浮按钮，停止监听
     * @param from
     */
    private void handleStopListen(String from){
        StatePresenter sp = StatePresenter.getInstance();
        String scene = sp.getScene();
        if(SCENE_MY_MUSIC.equals(scene)){
            AppUtils.controlMusicPlayer(mContext, "play");
        }
    }
}
