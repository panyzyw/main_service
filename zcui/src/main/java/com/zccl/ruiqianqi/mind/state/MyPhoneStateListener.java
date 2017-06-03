package com.zccl.ruiqianqi.mind.state;

import android.content.Context;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import com.zccl.ruiqianqi.mind.eventbus.MainBusEvent;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.zcui.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by ruiqianqi on 2016/12/27 0027.
 */

public class MyPhoneStateListener extends PhoneStateListener {

    private String TAG = MyPhoneStateListener.class.getSimpleName();
    // 全局上下文
    private Context mContext;

    public MyPhoneStateListener(Context context) {
        this.mContext = context.getApplicationContext();
    }

    @Override
    public void onCallForwardingIndicatorChanged(boolean cfi) {

    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        // A new call arrived and is ringing or waiting. In the latter case, another call is already active.
        // 来电状态，电话铃声响起的那段时间或正在通话又来新电，新来电话不得不等待的那段时间。
        if (state == TelephonyManager.CALL_STATE_RINGING) {
            // 输出来电号码
            LogUtils.e(TAG, "incomingNumber = " + incomingNumber);
            sendEvent(mContext.getString(R.string.phone_ringing), incomingNumber);
        }
        // At least one call exists that is dialing, active, or on hold, and no calls are ringing or waiting.
        // on hold的意思是暂时挂起。比如在进行通话A的时候，来了个新的电话B，我们暂时挂起通话A，等通话B结束了，才继续进行A的通话。
        // 摘机状态，至少有个电话活动。该活动或是拨打（dialing）或是通话，或是 on hold。并且没有电话是ringing or waiting
        else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
            sendEvent(mContext.getString(R.string.phone_off_hook), incomingNumber);
        }
        // 空闲。没有任何活动。电话挂断状态
        else if (state == TelephonyManager.CALL_STATE_IDLE) {
            sendEvent(mContext.getString(R.string.phone_idle), incomingNumber);
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

    @Override
    public void onCellInfoChanged(List<CellInfo> cellInfo) {

    }

    @Override
    public void onCellLocationChanged(CellLocation location) {

    }

    @Override
    public void onDataActivity(int direction) {
        switch (direction){
            case TelephonyManager.DATA_ACTIVITY_NONE:
                break;
            case TelephonyManager.DATA_ACTIVITY_IN:
                break;
            case TelephonyManager.DATA_ACTIVITY_OUT:
                break;
            case TelephonyManager.DATA_ACTIVITY_INOUT:
                break;
            case TelephonyManager.DATA_ACTIVITY_DORMANT:
                break;
            default:
                break;
        }
    }

    @Override
    public void onDataConnectionStateChanged(int state) {
        switch (state){
            case TelephonyManager.DATA_DISCONNECTED:
                break;
            case TelephonyManager.DATA_CONNECTING:
                break;
            case TelephonyManager.DATA_CONNECTED:
                break;
            case TelephonyManager.DATA_SUSPENDED:
                break;
            default:
                break;
        }
    }

    @Override
    public void onDataConnectionStateChanged(int state, int networkType) {
        super.onDataConnectionStateChanged(state, networkType);
    }

    @Override
    public void onMessageWaitingIndicatorChanged(boolean mwi) {
        super.onMessageWaitingIndicatorChanged(mwi);
    }

    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);
    }

    @Override
    public void onSignalStrengthChanged(int asu) {
        super.onSignalStrengthChanged(asu);
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
    }

}
