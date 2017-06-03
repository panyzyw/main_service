package com.zccl.ruiqianqi.presenter.impl;

import com.zccl.ruiqianqi.mind.app.BaseApplication;
import com.zccl.ruiqianqi.mind.eventbus.MainBusEvent;
import com.zccl.ruiqianqi.mind.receiver.internet.NetChangedReceiver;
import com.zccl.ruiqianqi.mind.receiver.sensor.SensorReceiver;
import com.zccl.ruiqianqi.mind.receiver.system.SystemReceiver;
import com.zccl.ruiqianqi.mind.state.MyPhoneStateListener;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.plugin.voice.ProxyVoice;
import com.zccl.ruiqianqi.presenter.base.BasePresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by ruiqianqi on 2016/10/26 0026.
 */

public class MindPresenter extends BasePresenter {

    /** 类标识 */
    private static String TAG = MindPresenter.class.getSimpleName();

    /**
     * 音频处理对象
     */
    private AbstractVoice voice;

    /**
     * 私有构造子，防止外部初始化
     */
    private MindPresenter(){
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        // 注册事件总线，语言变化
        EventBus.getDefault().register(this);
        //EventBus.getDefault().unregister(this);
    }

    /**
     * 这也是构建单例的一个方法
     * @return
     */
    public static MindPresenter getInstance() {
        return MindInstance.instance;
    }
    /**
     * 类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例
     * 没有绑定关系，而且只有被调用到时才会装载，从而实现了延迟加载。
     */
    private static class MindInstance {
        private static final MindPresenter instance = new MindPresenter();
    }


    /************************************【中心对外提供的方法】************************************/
    /**
     * 初始化语音服务
     * MyApplication的OnCreate()中调用
     */
    public void initSpeech(AbstractVoice voice){
        //this.voice = new ProxyVoice(voice);
        this.voice = voice;
        this.voice.initSpeech();
    }

    /**
     * 返回音源设备
     * @return
     */
    public AbstractVoice getVoiceDevice() {
        return voice;
    }


    /***********************************【事件总线接收器】*****************************************/
    /**
     * 接收到语言变化事件，从
     * {@link BaseApplication#onConfigurationChanged} 发过来的
     * @param languageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1, sticky = true)
    public void OnLanguageEvent(MainBusEvent.LanguageEvent languageEvent){
        if(null != voice) {
            voice.switchLanguage(languageEvent.getLanguage());
        }
    }

    /**
     * 接收到网络变化事件，从
     * {@link NetChangedReceiver#onReceive} 发过来的
     * @param netEvent
     */
    @Subscribe(threadMode = ThreadMode.ASYNC, priority = 1)
    public void OnNetEvent(MainBusEvent.NetEvent netEvent){
        if(null != voice) {
            voice.notifyChange(AbstractVoice.NET_CHANGE, netEvent);
        }
    }

    /**
     * 接收到电话状态变化事件，从
     * {@link MyPhoneStateListener#onCallStateChanged} 发过来的
     * @param phoneEvent
     */
    @Subscribe(threadMode = ThreadMode.ASYNC, priority = 1)
    public void OnPhoneEvent(MainBusEvent.PhoneEvent phoneEvent){
        if(null != voice) {
            voice.notifyChange(AbstractVoice.PHONE_CHANGE, phoneEvent);
        }
    }

    /**
     * 接收到电话状态变化事件，从
     * {@link SensorReceiver#onReceive} 发过来的
     * @param sensorEvent
     */
    @Subscribe(threadMode = ThreadMode.ASYNC, priority = 1)
    public void OnSensorEvent(MainBusEvent.SensorEvent sensorEvent){
        if(null != voice) {
            voice.notifyChange(AbstractVoice.SENSOR_CHANGE, sensorEvent);
        }
    }

    /**
     * 接收到APP状态变化事件，从
     * {@link SystemReceiver#onReceive} 发过来的
     * @param appStatusEvent
     */
    @Subscribe(threadMode = ThreadMode.ASYNC, priority = 1)
    public void OnAppStatusEvent(MainBusEvent.AppStatusEvent appStatusEvent){
        if(null != voice) {
            voice.notifyChange(AbstractVoice.APP_STATUS_CHANGE, appStatusEvent);
        }
    }

    /**
     * 接收到监听事件，从
     * {@link SystemReceiver#onReceive} 发过来的
     * @param listenEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1)
    public void OnListenEvent(MainBusEvent.ListenEvent listenEvent){
        if(null != voice) {
            if(MainBusEvent.ListenEvent.RECYCLE_LISTEN == listenEvent.getType()) {
                voice.notifyChange(AbstractVoice.RECYCLE_LISTEN, listenEvent);
            }else {
                voice.notifyChange(AbstractVoice.STOP_LISTEN, listenEvent);
            }
        }
    }

    /**
     * 接收到电池电量变化事件，从 BatteryReceiver 的 onReceive 发过来的
     * @param batteryEvent
     */
    @Subscribe(threadMode = ThreadMode.ASYNC, priority = 1)
    public void OnBatteryEvent(MainBusEvent.BatteryEvent batteryEvent){
        if(null != voice) {
            voice.notifyChange(AbstractVoice.BATTERY_CHANGE, batteryEvent);
        }
    }

    /** 语言配置改变时的订阅者 */
    //private Subscription subscription;

    /**
     * 语言变化的观察者
     * 这个只能接收一次，要再次监听的话，得再次注册
     */
    /*
    public void languageChangeObserve() {
        subscription = MyRxBus.getDefault().doSubscribeBundle(
                new Action1<Bundle>() {
                    @Override
                    public void call(Bundle bundle) {
                        if (bundle.getString("type").equals("language")) {
                            if(device!=null) {
                                device.switchLanguage(bundle.getString("language"));
                            }
                            LogUtils.e(TAG, "OnNext");
                        }
                    }
                }, null);

    }
    */

    /**
     * 取消对语言变化的监听
     * @return
     */
    /*
    public void cancelLanguageObserve() {
        if(subscription!=null && !subscription.isUnsubscribed()){
            subscription.unsubscribe();
        }
    }
    */


}
