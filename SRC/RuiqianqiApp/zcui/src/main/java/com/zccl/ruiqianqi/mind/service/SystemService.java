package com.zccl.ruiqianqi.mind.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;

import com.zccl.ruiqianqi.mind.provider.MyProviderMetaData;
import com.zccl.ruiqianqi.mind.receiver.Registers;
import com.zccl.ruiqianqi.mind.service.observer.ContactObserver;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.presenter.impl.MindPresenter;
import com.zccl.ruiqianqi.zcui.R;

/**
 * Created by ruiqianqi on 2016/7/20 0020.
 * The name of the process in which the activity should run. Normally, all components of an application run in a default process name created for the application and you do not need to use this attribute.
 * But if necessary, you can override the default process name with this attribute, allowing you to spread your app components across multiple processes.
 * If the name assigned to this attribute begins with a colon (':'), a new process, private to the application, is created when it's needed and the activity runs in that process.
 * If the process name begins with a lowercase character, the activity will run in a global process of that name, provided that it has permission to do so.
 * This allows components in different applications to share a process, reducing resource usage.
 * The <application> element's process attribute can set a different default process name for all components.
 */
public class SystemService extends BaseService {

    /** 全局上下文 */
    private Context mContext;
    /** 重启时，是否发送最后一个Intent */
    private boolean mRedelivery = true;

    /** 子线程消息循环 */
    private volatile Looper mServiceLooper;
    /** 子线程Handler */
    private SubThreadHandler subThreadHandler;
    /** 子线程体 */
    private HandlerThread handlerThread;

    /** 专门注册广播的类 */
    protected Registers registers;

    /** 观察用户数据库 */
    private MyDataObserver myDataObserver;

    /** 观察联系人数据库 */
    private ContactObserver contactObserver;

    /** 发音设备 */
    private AbstractVoice voiceDevice;

    /**
     * 子Handler
     * @author zccl
     */
    private final class SubThreadHandler extends Handler {
        public SubThreadHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            onHandleIntent((Intent)msg.obj);
        }
    }

    /**
     * 子线程中处理一些事情
     * @param obj
     */
    private void onHandleIntent(Intent obj) {

    }



    @Override
    public void onCreate() {
        super.onCreate();

        // 给发音设备赋值
        voiceDevice = MindPresenter.getInstance().getVoiceDevice();

        // 初始化
        init();
    }

    /**
     *
     */
    private void init(){
        this.mContext = getApplicationContext();

        // 观察用户数据库
        myDataObserver = new MyDataObserver(this, new Handler());
        Uri uri = MyProviderMetaData.UserTableMetaData.CONTENT_URI;
        getContentResolver().registerContentObserver(uri, true, myDataObserver);

        // 观察联系人数据库
        contactObserver = new ContactObserver(this, new Handler());
        contactObserver.setVoiceDevice(voiceDevice);
        uri = ContactsContract.Contacts.CONTENT_URI;
        getContentResolver().registerContentObserver(uri, true, contactObserver);

        // 子线程相关配置
        handlerThread = new HandlerThread("HandlerThread");
        handlerThread.start();
        mServiceLooper = handlerThread.getLooper();
        subThreadHandler = new SubThreadHandler(mServiceLooper);

        // 注册广播的类
        registers = Registers.getInstance(mContext);
        // 动态注册要监听的广播
        registers.registerSystemReceiver();
        // 动态注册触摸广播
        registers.registerSensorReceiver();
        // 动态注册要监听的网络状态广播
        registers.registerNetReceiver();
        // 动态注册要监听的电池状态广播
        registers.registerBatteryReceiver();

        // 动态注册下载完成广播
        registers.registerDownloadReceiver();

        // 动态注册来电，去电相关广播
        registers.registerPhoneReceiver();
        registers.registerPhoneListener();

        // 唤醒监听开启
        voiceDevice.startWakeup();

    }



    /**
     * （默认是和application同一个进程：也可以配置为全局进程及独立进程，像修改器类的应用可以配置为独立进程，因为入口就在服务中）
     * （像其他需要服务协助的应用，就不要配置了，采用默认的，在同一进程中；这样服务死了重启进程ID是不会变的，对象也是一样的）
     * （但是，如果要用独立服务监测什么东西的话也可以，那就涉及到进程间的通信了：安卓平台特有方式Binder，当然还有linux下通用方式）
     * （组件所在的进程是通过Android:process配置的）
     *
     * 返回值详解：
     * START_STICKY：如果service进程被kill掉，保留service的状态为【开始状态】，但不保留递送的intent对象。
     * 随后系统会尝试重新创建service，由于服务状态为开始状态，所以创建服务后一定会调用onStartCommand(Intent,int,int)方法。
     * 如果在此期间没有任何启动命令被传递到service，那么参数Intent将为null。
     *
     * START_REDELIVER_INTENT：重传Intent。使用这个返回值时，如果在执行完onStartCommand后，
     * 服务被异常kill掉，系统会自动重启该服务，并将最后一个传递的Intent的值传入。
     * （组件所在的进程是通过Android:process配置的）
     *
     * START_NOT_STICKY ：“非粘性的”。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，保留service的状态为【开始状态】，
     * 系统不会自动重启该服务，直到startService(Intent intent)方法再次被调用;
     * （组件所在的进程是通过Android:process配置的）
     *
     * START_STICKY_COMPATIBILITY：START_STICKY的兼容版本，但不保证服务被kill后一定能重启。
     * （组件所在的进程是通过Android:process配置的）
     *
     * 如果服务重启，来自客户端的通信接口会不会还有用，把参数带上，应该有用。
     * 每次启动服务，startId就不一样；每次启动的服务都有对应的ID即：startId
     *
     * 注意，每次启动服务都对应一个唯一的服务ID
     * stopSelf(startId)只能停止自己，不能停止别的ID的服务，一旦停止，所有服务都停止了还不如用stopSelf()；
     * stopSelfResult(int startId) 【startId：The most recent start identifier received in onStartCommand】；
     * stopSelf和stopService 一旦调用，整个服务都停止了；
     *
     * 如果服务没被杀掉
     * service1--------只进一次onStartCommand 且（startId=1）
     * service2--------只进一次onStartCommand 且（startId=2）
     * service3--------只进一次onStartCommand 且（startId=3）
     * 前面的没有处理，第四次启动时就是下面的结果
     * service4--------只进一次onStartCommand 且（startId=4）
     *
     *
     * flags详解：
     * START_FLAG_REDELIVERY
     * START_FLAG_RETRY
     *
     *
     * 如果服务被杀掉：
     * START_STICKY START_STICKY_COMPATIBILITY
     * service1--------没有stopself(1)
     * service2--------没有stopself(2)
     * service3--------没有stopself(3)
     * 前面的没有处理，第四次启动时就是下面的结果
     * service4--------只进一次onStartCommand 且（startId=4）
     *
     * START_REDELIVER_INTENT
     * service1--------没有stopself(1)
     * service2--------没有stopself(2)
     * service3--------没有stopself(3)
     * 前面的没有处理，第四次启动时就是下面的结果
     * service4--------要进四次onStartCommand 且（startId=对应的ID）
     * 对此的解释就是，service没有关闭，只要启动服务，就会进onStartCommand，就像重新开始一样
     *
     * START_NOT_STICKY
     * service1--------有/没有stopself(1)
     * service1--------有/没有stopself(1)
     * service1--------有/没有stopself(1)
     * service1--------有/没有stopself(1)
     * 不管你有没有stopself()，只要启动服务就是一个全新的开始，就是之前的服务已经关闭了，startId=1
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        this.startId = startId;
        keepForeground(startId, intent);

        Message msg = subThreadHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        subThreadHandler.sendMessage(msg);

        return mRedelivery ? START_REDELIVER_INTENT : START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return null;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // 在这儿对新配置做新调整，有的可以忽略，有的要重新加载
        int orientation1 = getResources().getConfiguration().orientation;
        int orientation2 = newConfig.orientation;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // 停止前台通知栏
        stopForeground(true);

        // 服务已死，取消观察
        getContentResolver().unregisterContentObserver(myDataObserver);
        // 服务已死，取消观察
        getContentResolver().unregisterContentObserver(contactObserver);

        // 服务已死，注销广播
        registers.unregisterSystemReceiver();
        // 服务已死，注销广播
        registers.unregisterSensorReceiver();
        // 服务已死，注销广播
        registers.unregisterNetReceiver();
        // 服务已死，注销广播
        registers.unregisterBatteryReceiver();
        // 服务已死，注销广播
        registers.unregisterDownloadReceiver();

        // 注销来电，去电相关广播
        registers.unregisterPhoneReceiver();
        registers.unregisterPhoneListener();

        // 退出子线程队列循环
        mServiceLooper.quit();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    /**
     * 保持服务不被杀死
     * @param startId
     * @param intent
     */
    private void keepForeground(int startId, Intent intent){
        Notification.Builder builder = new Notification.Builder(this)
                .setWhen(System.currentTimeMillis())
                //.setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(getText(R.string.serviceTicker))
                .setContentTitle(getText(R.string.serviceTitle))
                .setContentText(getText(R.string.serviceContent))
                .setContentIntent(PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setOngoing(true);
        Notification notification = builder.getNotification();
        notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_FOREGROUND_SERVICE;
        startForeground(startId, notification);
    }

}
