package com.zccl.ruiqianqi.mind.receiver.phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zccl.ruiqianqi.mind.eventbus.MainBusEvent;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.zcui.R;

import org.greenrobot.eventbus.EventBus;

import static android.telephony.TelephonyManager.EXTRA_INCOMING_NUMBER;
import static android.telephony.TelephonyManager.EXTRA_STATE;
import static android.telephony.TelephonyManager.EXTRA_STATE_IDLE;
import static android.telephony.TelephonyManager.EXTRA_STATE_OFFHOOK;
import static android.telephony.TelephonyManager.EXTRA_STATE_RINGING;

/**
 * Created by ruiqianqi on 2016/12/27 0027.
 */

public class PhoneReceiver extends BroadcastReceiver {

    // 类标志
    private String TAG = PhoneReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        if(null == intent)
            return;

        // 如果是去电
        if(Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction())){
            String outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            LogUtils.e(TAG, "outgoingNumber = " + outgoingNumber);

            sendEvent(context.getString(R.string.phone_dialing), outgoingNumber);
        }
        // 飞行模式
        else if(Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction())) {
            boolean isAirplaneModeOn = intent.getBooleanExtra("state", false);
        }
        // 来电？
        else{
            /*
            * 查了下android文档，貌似没有专门用于接收来电的action,所以，非去电即来电.
            * 如果我们想要监听电话的拨打状况，需要这么几步 :
            * 第一：获取电话服务管理器TelephonyManager manager = this.getSystemService(TELEPHONY_SERVICE);
            * 第二：通过TelephonyManager注册我们要监听的电话状态改变事件。manager.listen(new MyPhoneStateListener(),
                    * PhoneStateListener.LISTEN_CALL_STATE);这里的PhoneStateListener.LISTEN_CALL_STATE就是我们想要
                    * 监听的状态改变事件，除此之外，还有很多其他事件哦。
            * 第三步：通过extends PhoneStateListener来定制自己的规则。将其对象传递给第二步作为参数。
            * 第四步：这一步很重要，那就是给应用添加权限。android.permission.READ_PHONE_STATE
            * TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
            * tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
            */

            /**
             * String EXTRA_INCOMING_NUMBER
             * The lookup key used with the ACTION_PHONE_STATE_CHANGED
             * broadcast for a String containing the incoming phone number.
             * 在手机通话状态改变的广播，用于从extra取来电号码。
             * String EXTRA_STATE
             * The lookup key used with the ACTION_PHONE_STATE_CHANGED
             * broadcast for a String containing the new call state.
             */
            String state = intent.getStringExtra(EXTRA_STATE);
            if(!StringUtils.isEmpty(state)){
                String incomingNumber = intent.getStringExtra(EXTRA_INCOMING_NUMBER);
                // 输出来电号码
                LogUtils.e(TAG, "incomingNumber = " + incomingNumber);

                // 挂断
                if(state.equals(EXTRA_STATE_IDLE)){
                    sendEvent(context.getString(R.string.phone_idle), incomingNumber);
                }
                // 接听
                else if(state.equals(EXTRA_STATE_OFFHOOK)){
                    sendEvent(context.getString(R.string.phone_off_hook), incomingNumber);
                }
                // 来电
                else if(state.equals(EXTRA_STATE_RINGING)){
                    // 原来只写在这里
                    //String incomingNumber = intent.getStringExtra(EXTRA_INCOMING_NUMBER);

                    sendEvent(context.getString(R.string.phone_ringing), incomingNumber);
                }
            }
        }
    }

    /**
     * 发送电话状态
     * @param status
     * @param number
     */
    private void sendEvent(String status, String number){
        MainBusEvent.PhoneEvent phoneEvent = new MainBusEvent.PhoneEvent();
        phoneEvent.setStatus(status);
        phoneEvent.setNumber(number);
        // 通知网络变化了
        EventBus.getDefault().post(phoneEvent);
    }
}
