package com.zccl.ruiqianqi.mind.service.aidl.server;

import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.zccl.ruiqianqi.mind.service.BaseService;
import com.zccl.ruiqianqi.tools.LogUtils;

/**
 * Created by ruiqianqi on 2016/8/16 0016.
 */
public class MyRemoteService extends BaseService implements Handler.Callback {

    /** 日志类标志 */
    private String TAG = MyRemoteService.class.getSimpleName();

    /** 建立数据通道的命令 */
    private static final int INIT_BRIDGE_ON_BINDER = 1;
    /** 建立数据通道的命令 */
    private static final int START_COMMUNICATION = 2;

    /** 由（客户端）发往（服务）的虫洞实体 */
    private Messenger serviceMessenger = new Messenger(new Handler(this));
    /** 由（服务）发往（客户端）的虫洞接口 */
    private Messenger clientMessenger = null;


    @Override
    public void onCreate() {
        super.onCreate();

    }

    /**
     * 【startService (2)】
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if(intent!=null){
            //传过来的通迅接口
            clientMessenger = intent.getParcelableExtra("clientMessenger");
            if (clientMessenger != null) {
                //信息载体
                Message msg = new Message();
                msg.what = INIT_BRIDGE_ON_BINDER;
                msg.replyTo = serviceMessenger;
                msg.arg1 = startId;
                try {
                    // 重启之后，对象虽然又传过来了，但是连接已经不通了
                    // 所以发送不成功了，再起来也只能自己玩了
                    // 还有一种情况，就是客户端退出了，但是服务还存在，当客户再次进入时，
                    // 两方的连接断裂时，需要一种机制让各自知道连接不在了，好做进一步处理。
                    clientMessenger.send(msg);
                } catch (RemoteException e) {
                    // 出错了，关闭出错的服务，如果非得依赖clientMessenger的话就要关闭服务，进而关闭整个程序
                    stopSelf(startId);
                }
            }
        }
        return START_NOT_STICKY;
    }


    /**
     * 返回 由（客户端）发往（服务）的虫洞接口【bindService (2)】
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return serviceMessenger.getBinder();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /**
     * startService stop by client 或 stopself
     * bindService  stop by calling unbindService
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            // 初始化交流通道【bindService (4)】
            case INIT_BRIDGE_ON_BINDER:
                clientMessenger = msg.replyTo;
                LogUtils.e(TAG, "bindService: success");
                break;

            case START_COMMUNICATION:
                break;

            default:
                break;
        }
        return false;
    }

    /**
     * 发送给客户端
     * @param msg
     */
    private void sendMsgToClient(Message msg) {
        try {
            clientMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
