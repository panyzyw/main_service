package com.yongyida.robot.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.FileDescriptor;

/**
 * Created by ruiqianqi on 2017/7/8 0008.
 */

public class YYDUartCallback {

    // 类标志
    private static String TAG = YYDUartCallback.class.getSimpleName();
    // 得到唤醒后的数据
    public static int GET_WAKE_UP_DATA = 0;

    // 全局上下文
    private Context mContext;
    // 消息处理HANDLER
    private Handler mHandler;

    public YYDUartCallback(Context context, Handler handler){
        this.mContext = context;
        this.mHandler = handler;
    }

    /**
     * 由底层往上层回调的数据
     * @param data
     */
    public void getUartData(String data){
        Message message = mHandler.obtainMessage(GET_WAKE_UP_DATA, data);
        message.sendToTarget();
    }

}
