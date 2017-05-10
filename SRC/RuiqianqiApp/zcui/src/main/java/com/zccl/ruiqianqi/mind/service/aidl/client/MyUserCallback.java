package com.zccl.ruiqianqi.mind.service.aidl.client;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;

import com.zccl.ruiqianqi.mind.service.aidl.server.IMyUserCallback;

/**
 * Created by ruiqianqi on 2016/7/21 0021.
 */
public class MyUserCallback extends IMyUserCallback.Stub {

    /** 发送方法失败 */
    public static final int SEND_FAILURE = 0;
    /** 发送方法成功 */
    public static final int SEND_SUCCESS = 1;

    /** 接收到服务器回调后的处理类 */
    private Handler handler;

    public MyUserCallback(Handler handler){
        this.handler = handler;
    }
    @Override
    public void OnSuccess(String msg) throws RemoteException {
        if(handler!=null){
            Message message = handler.obtainMessage(SEND_SUCCESS);
            message.sendToTarget();
        }
    }

    @Override
    public void OnFailure(String errmsg) throws RemoteException {
        if(handler!=null){
            Message message = handler.obtainMessage(SEND_FAILURE);
            message.sendToTarget();
        }
    }
}
