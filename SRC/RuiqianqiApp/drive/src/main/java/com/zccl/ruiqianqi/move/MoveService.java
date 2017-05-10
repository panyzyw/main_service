package com.zccl.ruiqianqi.move;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

/**
 * Created by ruiqianqi on 2017/1/17 0017.
 */

public class MoveService extends Service {

    // 建立连接
    private static final int CREATE_BRIDGE = 1;
    // 加法动作
    private static final int ACTION_SUM = 2;

    /** 子线程的Looper */
    protected Looper mServiceLooper = null;
    /** 子线程中执行 */
    protected ServiceHandler threadHandler;
    /** 由（客户端）发往（服务）的虫洞实体 */
    private Messenger mMessenger;

    /**子线程*/
    public final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msgFromClient) {

            // 返回给客户端的消息
            Message msgToClient = Message.obtain(msgFromClient);
            // 初始化连接桥梁
            if(msgFromClient.what==CREATE_BRIDGE){
                msgToClient.what = CREATE_BRIDGE;
                try {
                    msgFromClient.replyTo.send(msgToClient);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            // 远程加法
            else if(msgFromClient.what==ACTION_SUM){
                msgToClient.what = ACTION_SUM;
                msgToClient.arg2 = msgFromClient.arg1 + msgFromClient.arg2;
                try {
                    msgFromClient.replyTo.send(msgToClient);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 子线程相关配置，可能会有耗时的操作，在子线程中处理
        HandlerThread thread = new HandlerThread("HandlerThread");
        thread.start();
        mServiceLooper = thread.getLooper();
        threadHandler = new ServiceHandler(mServiceLooper);
        // 消息处理体
        mMessenger = new Messenger(threadHandler);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
