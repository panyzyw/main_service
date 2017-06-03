package com.zccl.ruiqianqi.mind.service.aidl.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.zccl.ruiqianqi.tools.LogUtils;

/**
 * Created by ruiqianqi on 2016/8/16 0016.
 */
public class MyLocalClient extends Handler {

    private static String TAG = MyLocalClient.class.getSimpleName();

    /** 建立数据通道的命令 */
    private static final int INIT_BRIDGE_ON_BINDER = 1;
    /** 单例引用 */
    private static MyLocalClient instance;

    /** 应用上下文 */
    private Context context;

    /** 由（服务）发往（客户端）的虫洞实体（传给服务用的） */
    private Messenger clientMessenger = new Messenger(this);
    /** 由（客户端）发往（服务）的虫洞接口（客户端自己用的）*/
    private Messenger serviceMessenger;
    /** 绑定好了吗 */
    private boolean isUsing = false;

    /** 要启动及绑定的服务组件 */
    private ComponentName componentName;

    /** 采用startService方式启动服务 */
    private Intent startIntent = null;

    /** 采用bindService方式启动服务 */
    private Intent bindIntent = null;

    /**
     * 【bindService (3)】
     */
    private ServiceConnection mRemoteConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceMessenger = new Messenger(service);
            Message msg = new Message();
            msg.what = INIT_BRIDGE_ON_BINDER;
            msg.replyTo = clientMessenger;
            try {
                serviceMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            isUsing = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isUsing = false;
            serviceMessenger = null;
        }
    };

    private MyLocalClient(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * 用这个用话，instance不需要用volatile修饰
     * @return
     */
    public static MyLocalClient getInstance(Context context) {
        if(instance == null) {
            synchronized(MyLocalClient.class) {
                MyLocalClient temp = instance;
                if(temp == null) {
                    temp = new MyLocalClient(context);
                    instance = temp;
                }
            }
        }
        return instance;
    }

    /**
     * 开启后台服务【startService (1)】
     */
    public void startRemoteService(){
        startIntent = new Intent();
        startIntent.setComponent(componentName);
        startIntent.putExtra("clientMessenger", clientMessenger);
        context.startService(startIntent);
    }

    /**
     * 停止后台服务
     */
    public void stopRemoteService(){
        isUsing = true;
        context.stopService(startIntent);
    }

    /**
     * 绑定服务【bindService (1)】
     */
    public void bindRemoteService(){
        bindIntent = new Intent();
        bindIntent.setComponent(componentName);
        context.bindService(bindIntent, mRemoteConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 解绑服务
     */
    public void unbindRemoteService(){
        isUsing = false;
        context.unbindService(mRemoteConnection);
    }

    /**
     * 设置要启动及绑定的服务组件
     * @param pkg
     * @param cls
     */
    public void setServiceComponent(String pkg, String cls) {
        this.componentName = new ComponentName(pkg, cls);
    }

    @Override
    public void handleMessage(Message msg) {
        //【startService (3)】
        if(msg.what==INIT_BRIDGE_ON_BINDER){
            serviceMessenger = msg.replyTo;
            isUsing = true;
            LogUtils.e(TAG, "startService: success");
        }
        //开始处理
        else{

        }
    }

    /**
     * 发送给服务的消息
     * @param msg
     */
    public void sendMsgToRemote(Message msg){
        if(isUsing){
            try {
                serviceMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        //连接没有建立，无法发送消息
        else{

        }
    }

}
