package com.zccl.ruiqianqi.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;

import com.google.gson.Gson;
import com.zccl.ruiqianqi.brain.R;
import com.zccl.ruiqianqi.brain.eventbus.MindBusEvent;
import com.zccl.ruiqianqi.config.MyConfig;
import com.zccl.ruiqianqi.domain.model.Robot;
import com.zccl.ruiqianqi.domain.model.dataup.LogCollectBack;
import com.zccl.ruiqianqi.domain.model.dataup.ShutdownBack;
import com.zccl.ruiqianqi.mind.eventbus.MainBusEvent;
import com.zccl.ruiqianqi.presentation.presenter.StatePresenter;
import com.zccl.ruiqianqi.tools.JsonUtils;
import com.zccl.ruiqianqi.tools.MyAppUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

import org.greenrobot.eventbus.EventBus;

import static com.zccl.ruiqianqi.brain.handler.BaseHandler.ACTION_PLAYER;
import static com.zccl.ruiqianqi.brain.handler.BaseHandler.MUSIC_CONTROL;
import static com.zccl.ruiqianqi.brain.handler.BaseHandler.PLAYER_CATEGORY_KEY;
import static com.zccl.ruiqianqi.brain.handler.BaseHandler.PLAYER_RESULT_KEY;
import static com.zccl.ruiqianqi.config.MyConfig.KEY_RESULT;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_LOG_COLLECT;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_PUSH_TIMED_SHUTDOWN;
import static com.zccl.ruiqianqi.mind.receiver.system.SystemReceiver.ACT_RECYCLE_LISTEN;
import static com.zccl.ruiqianqi.mind.receiver.system.SystemReceiver.ACT_STOP_LISTEN;
import static com.zccl.ruiqianqi.mind.voice.impl.function.FuncIntent.INTENT_EMOTION_CHAT;

/**
 * Created by ruiqianqi on 2017/5/9 0009.
 */

public class AppUtils {

    private static String TAG = AppUtils.class.getSimpleName();

    // 互斥的状态归为同一类型
    // 灭屏、亮屏（screen）
    public static final int PRIORITY_1 = 1;
    // 低电量（lowPower）、正常电量（normalPower）、满电量（FullPower）
    public static final int PRIORITY_2 = 2;
    public static final int PRIORITY_3 = 3;
    public static final int PRIORITY_4 = 4;
    public static final int PRIORITY_5 = 5;
    // 说话（speak）、监听（monitor）
    public static final int PRIORITY_6 = 6;
    public static final int PRIORITY_7 = 7;
    // 来电（call）
    public static final int PRIORITY_8 = 8;
    public static final int PRIORITY_9 = 9;


    /**
     * 开始监听【广播】
     *
     * @param context            全局上下文
     * @param from               循环监听来自哪里
     * @param isUseFloatVoice  是否显示悬浮表情
     * @param isUseExpression  是否显示大表情
     */
    public static void startListen(Context context, String from, boolean isUseFloatVoice, boolean isUseExpression){
        Bundle bundle = new Bundle();
        bundle.putString(context.getString(R.string.recycle_from_key), from);
        bundle.putBoolean(context.getString(R.string.recycle_voice_float_key), isUseFloatVoice);
        bundle.putBoolean(context.getString(R.string.recycle_expression_key), isUseExpression);
        MyAppUtils.sendBroadcast(context, ACT_RECYCLE_LISTEN, bundle);
    }

    /**
     * 结束监听【广播】
     *
     * @param context  全局上下文
     * @param from     结束监听来自哪里
     */
    public static void stopListen(Context context, String from){
        Bundle bundle = new Bundle();
        bundle.putString(context.getString(R.string.stop_from_key), from);
        MyAppUtils.sendBroadcast(context, ACT_STOP_LISTEN, bundle);
    }

    /**
     * 开始监听，【总线】
     * @param from
     * @param isUseFloatVoice  是否显示悬浮表情
     * @param isUseExpression  是否显示大表情
     */
    public static void sendStartListenEvent(String from, boolean isUseFloatVoice, boolean isUseExpression){
        MainBusEvent.ListenEvent listenEvent = new MainBusEvent.ListenEvent();
        listenEvent.setType(MainBusEvent.ListenEvent.RECYCLE_LISTEN);
        listenEvent.setFrom(from);
        listenEvent.setUseVoiceFloat(isUseFloatVoice);
        listenEvent.setUseExpression(isUseExpression);
        // 通知进入监听
        EventBus.getDefault().post(listenEvent);
    }

    /**
     * 停止监听，【总线】
     * @param from
     */
    public static void sendStopListenEvent(String from){
        MainBusEvent.ListenEvent listenEvent = new MainBusEvent.ListenEvent();
        listenEvent.setType(MainBusEvent.ListenEvent.STOP_LISTEN);
        listenEvent.setFrom(from);
        // 通知进入监听
        EventBus.getDefault().post(listenEvent);
    }

    /**
     * 广播饿了的表情广播
     * @param context
     */
    public static void sendEmotionHungry(Context context){
        Bundle args = new Bundle();
        args.putString(KEY_RESULT, MyConfig.SEMANTIC_CHARGE);
        args.putBoolean("while_play", true);
        MyAppUtils.sendBroadcast(context, INTENT_EMOTION_CHAT, args);
    }

    /**
     * 结束大表情
     * @param context
     */
    public static void endEmotion(Context context){

        Bundle bundle = new Bundle();
        bundle.putString(KEY_RESULT, "finish");
        MyAppUtils.sendBroadcast(context, INTENT_EMOTION_CHAT, bundle);

        // 结束大表情
        /*
        MindBusEvent.ExpressionEvent expressionEvent = new MindBusEvent.ExpressionEvent();
        EventBus.getDefault().post(expressionEvent);
        */
    }



    /**
     *
     * @param context
     * @param onOff 打开或关闭
     * @param from  监听或说话 monitor speak
     * @param color 颜色
     *              1 Red
     *              2 Green
     *              3 Blue
     * @param frequency 闪动频率
     *              1 Low
     *              2 Middle
     *              3 High
     *              4 Const
     * @param priority
     *
     */
    public static void sendLedBroad(Context context, boolean onOff, String from,
                                    int color, int frequency, int priority) {
        Intent intentSpeakOpen = new Intent("com.yongyida.robot.change.BREATH_LED");
        intentSpeakOpen.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intentSpeakOpen.putExtra("package", context.getPackageName());
        intentSpeakOpen.putExtra("on_off", onOff);
        intentSpeakOpen.putExtra("place", 3);
        intentSpeakOpen.putExtra("colour", color);
        intentSpeakOpen.putExtra("frequency", frequency);
        intentSpeakOpen.putExtra("Permanent", from);
        intentSpeakOpen.putExtra("priority", priority);
        context.sendBroadcast(intentSpeakOpen);
    }

    /**
     * 这样都可以吗
     * 因为这是一个sticky intent,你不需要注册广播接收器。简单地通过调用 registerReceiver，
     * 像下面的代码段传入一个null的接收器，当前电池状态的intent就会返回。你也可以传入一个真实的接收器对象
     * @param context
     */
    public static void queryBattery(Context context){
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, filter);
        //你可以读到充电状态,如果在充电，可以读到是usb还是交流电

        // 是否在充电
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        // 怎么充
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
    }

    /**
     * 关掉自有播放器
     * @param context
     */
    public static void exitMusicPlayer(Context context){
        Bundle bundle = new Bundle();
        bundle.putString(PLAYER_CATEGORY_KEY, MUSIC_CONTROL);
        bundle.putString(PLAYER_RESULT_KEY, "退出");
        MyAppUtils.sendBroadcast(context, ACTION_PLAYER, bundle);
    }

    /**
     * 用户操作日志收集
     * @param collect_result  json
     * @param collect_from    来自哪里
     * @param answer           对应的回答
     */
    public static void logCollectUp2Server(String collect_from, String collect_result, String answer){
        LogCollectBack logCollectBack = new LogCollectBack();
        LogCollectBack.LogCollect logCollect = JsonUtils.parseJson(collect_result, LogCollectBack.LogCollect.class);
        if(null != logCollect) {
            StatePresenter sp = StatePresenter.getInstance();
            Robot robot = sp.getRobot();
            if (null != robot) {
                logCollect.setId(robot.getRid());
            }
            logCollect.setCmd(B_LOG_COLLECT);
            logCollect.setFrom(collect_from);
            if(!StringUtils.isEmpty(answer)){
                logCollect.setAnswer(answer);
            }
            logCollectBack.setCommand(new Gson().toJson(logCollect));
        }

        MindBusEvent.ForwardSocketEvent forwardSocketEvent = new MindBusEvent.ForwardSocketEvent();
        forwardSocketEvent.setCmd(B_LOG_COLLECT);
        forwardSocketEvent.setText(new Gson().toJson(logCollectBack));
        EventBus.getDefault().post(forwardSocketEvent);
    }

    /**
     * 上行关机指令回调到手机端
     * @param type
     * @param countdownTime
     */
    public static void shutdownUp2Server(int type, int countdownTime){
        ShutdownBack shutdownBack = new ShutdownBack();
        ShutdownBack.Shutdown shutdown = new ShutdownBack.Shutdown();
        shutdown.setType(type);
        shutdown.setCountdownTime(countdownTime);
        shutdownBack.setCommand(new Gson().toJson(shutdown));
        MindBusEvent.ForwardSocketEvent forwardSocketEvent = new MindBusEvent.ForwardSocketEvent();
        forwardSocketEvent.setCmd(B_PUSH_TIMED_SHUTDOWN);
        forwardSocketEvent.setText(new Gson().toJson(shutdownBack));
        EventBus.getDefault().post(forwardSocketEvent);
    }
}
