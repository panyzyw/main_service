package com.zccl.ruiqianqi.mind.state;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * Created by ruiqianqi on 2016/12/27 0027.
 */

public class MyStateRegister {

    // 全局上下文
    private Context mContext;
    // 电话管理器
    private TelephonyManager mTelephonyManager;

    public MyStateRegister(Context context){
        this.mContext = context.getApplicationContext();
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        mTelephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * 注册监听各种状态
     */
    public void registerPhoneListener(){
        MyPhoneStateListener phoneStateListener = new MyPhoneStateListener(mContext);
        mTelephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR |
                        PhoneStateListener.LISTEN_CALL_STATE |
                        PhoneStateListener.LISTEN_CELL_LOCATION |
                        PhoneStateListener.LISTEN_DATA_ACTIVITY |
                        PhoneStateListener.LISTEN_DATA_CONNECTION_STATE |
                        PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR |
                        PhoneStateListener.LISTEN_SERVICE_STATE |
                        PhoneStateListener.LISTEN_SIGNAL_STRENGTH);
    }

    /**
     * 注销监听各种状态
     */
    public void unregisterPhoneListener(){
        mTelephonyManager.listen(null, 0);
    }

}
