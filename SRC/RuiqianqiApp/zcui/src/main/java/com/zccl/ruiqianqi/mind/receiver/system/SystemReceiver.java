package com.zccl.ruiqianqi.mind.receiver.system;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zccl.ruiqianqi.mind.eventbus.MainBusEvent;
import com.zccl.ruiqianqi.mind.intent.MyIntent;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.zcui.R;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by ruiqianqi on 2016/7/22 0022.
 * Android广播的分类：
 1、 普通广播：这种广播可以依次传递给各个处理器去处理
 2、 有序广播：这种广播在处理器端的处理顺序是按照处理器的不同优先级来区分的，
     高优先级的处理器会优先截获这个消息，并且可以将这个消息删除
 3、 粘性消息：粘性消息在发送后就一直存在于系统的消息容器里面，等待对应的处理器去处理，
     如果暂时没有处理器处理这个消息则一直在消息容器里面处于等待状态，粘性广播的Receiver如果被销毁，
     那么下次重建时会自动接收到消息数据。
     粘性广播主要为了解决，在发送完广播之后，动态注册的接收者，也能够收到广播.

 注意：普通广播和粘性消息不能被截获，而有序广播是可以被截获的。
 在Android系统粘性广播一般用来确保重要的状态改变后的信息被持久保存，并且能随时广播给新的广播接收器，
 比如电源的改变，因为耗电需要一个过程，前一个过程必须提前得到，否则可能遇到下次刚好接收到的广播后系统自动关机了，
 随之而来的是kill行为，所以对某些未处理完的任务来说，后果很严重。
 */
public class SystemReceiver extends BroadcastReceiver {

    /** 类标志 */
    private static String TAG = SystemReceiver.class.getSimpleName();

    // 闹钟广播事件，60秒一次（自定义）（动态注册了：隐式启动服务、广播、Activity）
    public static final String ACTION_ALARM = "com.zccl.ruiqianqi.ALARM_ACTION";
    // 60秒一次的系统定时广播【Sent every minute】
    public static final String ACTION_TIME_TICK = Intent.ACTION_TIME_TICK;
    // 程序启动广播，竟然是5秒钟一次（这个静态注册了）
    public static final String ACTION_LAUNCH = "com.yongyida.robot.voice.MAIN";
    // 系统启动广播（这个静态注册了）
    public static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";

    // HOME键行为分类字段（系统定义）
    public static final String SYSTEM_REASON = "reason";
    // HOME键单击（系统定义）
    public static final String SYSTEM_HOME_KEY = "homekey";// home key
    // HOME键长按（系统定义）
    public static final String SYSTEM_RECENT_APPS = "recentapps";// long home key

    // 循环监听
    public static final String ACT_RECYCLE_LISTEN = "com.yydrobot.RECYCLE";
    // 停止监听
    public static final String ACT_STOP_LISTEN = "com.yydrobot.STOPLISTEN";

    // 进入视频监听
    public static final String ENTER_VIDEO_MONITOR = "com.yydrobot.ENTERMONITOR";
    // 退出视频监听
    public static final String EXIT_VIDEO_MONITOR = "com.yydrobot.EXITMONITOR";
    // 进入视频通信
    public static final String ENTER_VIDEO_COMM = "com.yydrobot.ENTERVIDEO";
    // 退出视频通信
    public static final String EXIT_VIDEO_COMM = "com.yydrobot.EXITVIDEO";
    // 进入工厂模式
    public static final String FACTORY_START = "com.yongyida.robot.FACTORYSTART";
    // 退出工厂模式
    public static final String FACTORY_CLOSE = "com.yongyida.robot.FACTORYCLOSE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(null == intent){
            return;
        }

        String action = intent.getAction();
        LogUtils.e(TAG, "action = " + action);

        // 60秒一次的闹钟广播
        if (ACTION_ALARM.equals(action)) {

        }
        // 60秒一次的系统定时广播【Sent every minute】
        else if (ACTION_TIME_TICK.equals(action)) {

        }
        // 5秒钟一次launcher广播
        else if (ACTION_LAUNCH.equals(action)) {

        }
        // 系统开机广播
        else if(ACTION_BOOT.equals(action)){

        }
        // 监听HOME键按下
        else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
            String reason = intent.getStringExtra(SYSTEM_REASON);
            if (SYSTEM_HOME_KEY.equals(reason)) {
                //home key
                sendHomeEvent(context.getString(R.string.home_press));
            } else if (SYSTEM_RECENT_APPS.equals(reason)) {
                //long home key
                sendHomeEvent(context.getString(R.string.home_long_press));
            }
        }
        // 进入监听
        else if(ACT_RECYCLE_LISTEN.equals(action)){
            String fromKey = context.getString(R.string.recycle_from_key);
            String expressionKey = context.getString(R.string.recycle_expression_key);
            String from = intent.getExtras().getString(fromKey);
            boolean expression = intent.getExtras().getBoolean(expressionKey, true);
            sendStartListenEvent(from, expression);
        }
        // 退出监听
        else if(ACT_STOP_LISTEN.equals(action)){
            String fromKey = context.getString(R.string.stop_from_key);
            sendStopListenEvent(intent.getExtras().getString(fromKey));
        }
        // 进入视频监听
        else if(ENTER_VIDEO_MONITOR.equals(action)){
            sendEvent(context.getString(R.string.entry_video_monitor));
        }
        // 退出视频监听
        else if(EXIT_VIDEO_MONITOR.equals(action)){
            sendEvent(context.getString(R.string.exit_video_monitor));
        }
        // 进入视频通信
        else if(ENTER_VIDEO_COMM.equals(action)){
            sendEvent(context.getString(R.string.entry_video_comm));
        }
        // 退出视频通信
        else if(EXIT_VIDEO_COMM.equals(action)){
            sendEvent(context.getString(R.string.exit_video_comm));
        }
        // 进入工厂模式
        else if(FACTORY_START.equals(action)){
            sendEvent(context.getString(R.string.factory_start));
        }
        // 退出工厂模式
        else if(FACTORY_CLOSE.equals(action)){
            sendEvent(context.getString(R.string.factory_close));
        }

        else {
            //QueryIpPresenter.getInstance().execute(intent);

        }

    }

    /**
     * 发送APP状态变化事件
     * @param action
     */
    private void sendEvent(String action){
        MainBusEvent.AppStatusEvent statusEvent = new MainBusEvent.AppStatusEvent();
        statusEvent.setAction(action);
        // 通知传感器状态变化了
        EventBus.getDefault().post(statusEvent);
    }

    /**
     * 发送开始监听事件
     * @param from
     */
    private void sendStartListenEvent(String from, boolean isUseExpression){
        MainBusEvent.ListenEvent listenEvent = new MainBusEvent.ListenEvent();
        listenEvent.setType(MainBusEvent.ListenEvent.RECYCLE_LISTEN);
        listenEvent.setText(from);
        listenEvent.setUseExpression(isUseExpression);
        // 通知进入监听
        EventBus.getDefault().post(listenEvent);
    }

    /**
     * 发送停止监听事件
     * @param from
     */
    private void sendStopListenEvent(String from){
        MainBusEvent.ListenEvent listenEvent = new MainBusEvent.ListenEvent();
        listenEvent.setType(MainBusEvent.ListenEvent.STOP_LISTEN);
        listenEvent.setText(from);
        // 通知进入监听
        EventBus.getDefault().post(listenEvent);
    }

    /**
     * 发送Home事件
     * @param type
     */
    private void sendHomeEvent(String type){
        MainBusEvent.HomeEvent homeEvent = new MainBusEvent.HomeEvent();
        homeEvent.setText(type);
        // 通知进入监听
        EventBus.getDefault().post(homeEvent);
    }

}
