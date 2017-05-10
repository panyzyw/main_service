package com.yongyida.robot.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.yongyida.robot.voice.utils.LogUtils;

/**
 * Created by ruiqianqi on 2016/12/12 0012.
 */

public class PlayerClient {
    /** 类标志 */
    private static String TAG = PlayerClient.class.getSimpleName();
    /** 单例引用 */
    private static PlayerClient instance;
    /** 应用上下文 */
    private Context mContext;
    /** 封装成对应接口 */
    private IPlayerService mRemoteService;
    // 是否在监听
    private boolean isListening = false;

    /**
     * 通信接口是我连接服务时，由系统的回调传递给我的
     */
    private ServiceConnection mRemoteConnection = new ServiceConnection() {
        /**
         * 这个回调是由ActivityManagerService所在进程，通过Binder间通迅机制（Binder引用为ApplicationThread）
         * 通知当前进程（ActivityThread），当前进程再通过自己的消息分发处理机制，来处理这个调用，其实也就是发送到
         * 主线程循环中进行调用。所以我们无法确认在调用方法之前是否已经绑定好了（如果绑定和调用是一起执行的话）
         */
        public void onServiceConnected(ComponentName className, IBinder service) {
            LogUtils.showLogError(TAG, "onServiceConnected");
            mRemoteService = IPlayerService.Stub.asInterface(service);
        }

        public void onServiceDisconnected(ComponentName className) {
            LogUtils.showLogError(TAG, "onServiceDisconnected");
            mRemoteService = null;
        }
    };

    private PlayerClient(Context context) {
        this.mContext = context.getApplicationContext();
        init();
    }

    /**
     * 用这个用话，instance不需要用volatile修饰
     * @return
     */
    public static PlayerClient getInstance(Context context) {
        if(instance == null) {
            synchronized(PlayerClient.class) {
                PlayerClient temp = instance;
                if(temp == null) {
                    temp = new PlayerClient(context);
                    instance = temp;
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     */
    private void init(){
        bindMyService();
    }

    /**
     * 绑定服务
     */
    private void bindMyService(){
        Intent intent = new Intent(IPlayerService.class.getName());
        intent.setPackage("com.zccl.ruiqianqi.player");
        mContext.bindService(intent, mRemoteConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 解除绑定
     */
    public void unbindMyService(){
        mContext.unbindService(mRemoteConnection);
    }

    /**
     * 开始识别，
     * 启动音乐的时候
     * 在线监听结束的时候
     */
    public void startRecognizer(String msg){
        if(mRemoteService!=null){
            try {
                mRemoteService.wakeUpSuccess(msg);
                isListening = true;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else {
            init();
        }
    }

    /**
     * 唤醒失败
     */
    public void wakeUpFailure(String errMsg){
        if(mRemoteService!=null){
            try {
                mRemoteService.wakeUpFailure(errMsg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else {
            init();
        }
    }

    /**
     * 发送音频数据
     */
    public void sendAudioData(String audioData){
        if(mRemoteService!=null){
            try {
                //if(isListening)
                {
                    mRemoteService.sendAudioData(audioData);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else {
            init();
        }
    }

    /**
     * 结束发送音频数据
     */
    public void stopRecognizer(){
        if(mRemoteService!=null){
            try {
                mRemoteService.endAudioData();
                isListening = false;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else {
            init();
        }
    }

}
