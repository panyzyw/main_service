package com.zccl.ruiqianqi.mind.receiver.sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zccl.ruiqianqi.mind.eventbus.MainBusEvent;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.zcui.R;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by ruiqianqi on 2016/9/21 0021.
 */
public class SensorReceiver extends BroadcastReceiver {

    /** 类标志 */
    private static String TAG = SensorReceiver.class.getSimpleName();

    /** 触摸广播携带的【数据一】的KEY */
    public static final String TOUCH_KEY = "android.intent.extra.Touch";
    // 摸肚子，摸头
    public static final String TOUCH_HEAD_VALUE = "t_head";
    // 摸下巴
    public static final String TOUCH_CHIN_VALUE = "t_back";
    // 右胳膊
    public static final String TOUCH_RIGHT_ARM_VALUE = "yyd5";
    // 左胳膊
    public static final String TOUCH_LEFT_ARM_VALUE = "yyd6";
    // 摸肩跳舞
    public static final String TOUCH_DANCE = "dance";

    /**
     * 触摸广播携带的【数据二】的KEY
     * frameworks/base/services/core/java/com/android/server/TouchSensorService.java:55:
     * intent.putExtra("android.intent.extra.Wakeup","t_head_wake");
     */
    public static final String TOUCH_WAKE_KEY = "android.intent.extra.Wakeup";
    // 触摸唤醒
    public static final String TOUCH_HEAD_WAKE_VALUE = "t_head_wake";
    // 语音唤醒
    public static final String VOICE_WAKE_VALUE = "voice_wake";

    /** 用手触摸启动发的广播 */
    public static final String TOUCH_SENSOR = "TouchSensor";
    /** 按一下投影按钮 */
    public static final String HDMI_SHORT_PRESS = "com.yongyida.robot.ACTION_HDMI_SHORT_PRESSED";
    /** 长按投影按钮 */
    public static final String HDMI_LONG_PRESS = "com.yongyida.robot.ACTION_HDMI_LONG_PRESSED";


    @Override
    public void onReceive(Context context, Intent intent) {
        if(null == intent){
            return;
        }

        Bundle bundle = intent.getExtras();
        if (null != bundle) {
            LogUtils.e(TAG, intent.getAction() + " - bundle.size is " + bundle.size());
        } else {
            LogUtils.e(TAG, intent.getAction()+ " - " + "bundle is null");
        }

        // 机器人传感器
        if (TOUCH_SENSOR.equals(intent.getAction())) {
            if (null != bundle) {
                String touchValue = bundle.getString(TOUCH_KEY);
                String wakeValue = bundle.getString(TOUCH_WAKE_KEY);

                LogUtils.e(TAG, "touchValue = " + touchValue);
                LogUtils.e(TAG, "wakeValue = " + wakeValue);

                // 触摸唤醒、语音唤醒
                if (TOUCH_HEAD_VALUE.equals(touchValue)) {
                    // 触摸唤醒
                    if(TOUCH_HEAD_WAKE_VALUE.equals(wakeValue)){
                        sendSensorEvent(context.getString(R.string.sensor_touch));
                    }
                    // 语音唤醒
                    else if(VOICE_WAKE_VALUE.equals(wakeValue)){
                        sendSensorEvent(context.getString(R.string.sensor_voice));
                    }

                }
                // 摸下巴
                else if (TOUCH_CHIN_VALUE.equals(touchValue)) {
                    sendSensorEvent(context.getString(R.string.sensor_chin));

                }
                // 右胳膊【20】
                else if (TOUCH_RIGHT_ARM_VALUE.equals(touchValue)) {
                    sendSensorEvent(context.getString(R.string.sensor_right_arm));
                }
                // 左胳膊【20】
                else if (TOUCH_LEFT_ARM_VALUE.equals(touchValue)) {
                    sendSensorEvent(context.getString(R.string.sensor_left_arm));
                }
                // 右胳膊【50】
                else if ("t_right".equals(touchValue)) {
                    sendSensorEvent(context.getString(R.string.sensor_right_arm));
                }
                // 左胳膊【50】
                else if ("t_left".equals(touchValue)) {
                    sendSensorEvent(context.getString(R.string.sensor_left_arm));
                }
                // 摸肩跳舞
                else if (TOUCH_DANCE.equals(touchValue)) {
                    sendSensorEvent(context.getString(R.string.sensor_dance));

                }
                // 其他值
                else {
                    sendSensorEvent(touchValue);
                }
            }
        }
        // 按一下投影按钮
        else if (HDMI_SHORT_PRESS.equals(intent.getAction())){
            sendSensorEvent(context.getString(R.string.hdmi_short_press));
        }
        // 长按投影按钮
        else if (HDMI_LONG_PRESS.equals(intent.getAction())){
            sendSensorEvent(context.getString(R.string.hdmi_long_press));
        }
        // 锁屏
        else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            sendSensorEvent(context.getString(R.string.screen_off));
        }
        // 解锁
        else if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
            sendSensorEvent(context.getString(R.string.user_present));
        }
        // 开屏
        else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            sendSensorEvent(context.getString(R.string.screen_on));
        }
        // 关机
        else if (Intent.ACTION_SHUTDOWN.equals(intent.getAction())) {
            sendSensorEvent(context.getString(R.string.shutdown));
        }
    }

    /**
     * 发送传感器状态
     * @param text
     */
    private void sendSensorEvent(String text){
        MainBusEvent.SensorEvent sensorEvent = new MainBusEvent.SensorEvent();
        sensorEvent.setText(text);
        // 通知传感器状态变化了
        EventBus.getDefault().post(sensorEvent);
    }
}
