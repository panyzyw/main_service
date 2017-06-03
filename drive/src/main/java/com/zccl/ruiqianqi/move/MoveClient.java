package com.zccl.ruiqianqi.move;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.yongyida.robot.motorcontrol.MotorController;

/**
 * Created by ruiqianqi on 2017/1/17 0017.
 */

public class MoveClient {

    // 建立连接
    private static final int CREATE_BRIDGE = 1;
    // 加法动作
    private static final int ACTION_SUM = 2;
    // 全局上下文
    private Context mContext;

    /**
     * 构造方法
     * @param context
     */
    public MoveClient(Context context){
        mContext = context.getApplicationContext();
        init();
    }

    /**
     * 初始化
     */
    private void init(){

    }


    /****************************************【消息通道】******************************************/
    // 消息通道
    private Messenger mService;
    // 是否建立连接
    private boolean isConn;

    /**
     * 消息通道
     */
    private Messenger mMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msgFromServer) {
            switch (msgFromServer.what) {
                case CREATE_BRIDGE:
                    break;
                case ACTION_SUM:
                    break;
            }
        }
    });

    /**
     * 绑定服务的连接
     */
    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            isConn = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            isConn = false;
        }
    };

    /**
     * 初始化连接桥梁
     */
    public void createBridge(){
        Message msgFromClient = Message.obtain(null, CREATE_BRIDGE);
        msgFromClient.replyTo = mMessenger;
        if (isConn){
            // 往服务端发送消息
            try {
                mService.send(msgFromClient);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 远程加法
     * @param a
     * @param b
     */
    public void actionSum(int a, int b){
        Message msgFromClient = Message.obtain(null, ACTION_SUM, a, b);
        msgFromClient.replyTo = mMessenger;
        if (isConn){
            // 往服务端发送消息
            try {
                mService.send(msgFromClient);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 绑定服务
     */
    public void bindService() {
        Intent intent = new Intent();
        intent.setAction("com.zccl.ruiqianqi.move.MoveService");
        mContext.bindService(intent, mConn, Context.BIND_AUTO_CREATE);
    }

    /**
     * 解绑服务
     */
    public void unbindService() {
        mContext.unbindService(mConn);
    }

}
