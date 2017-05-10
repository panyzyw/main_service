package com.zccl.ruiqianqi.mind.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.presenter.impl.MindPresenter;
import com.zccl.ruiqianqi.tools.FileUtils;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.ShellUtils;
import com.zccl.ruiqianqi.zcui.R;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by ruiqianqi on 2016/7/20 0020.
 * 这个服务是：任务执行完就关闭的，直接调用onDestroy
 */
public class TaskService extends IntentService implements Handler.Callback {

    private static String TAG = TaskService.class.getSimpleName();

    /** 检查是否在录音 */
    private static final int CHECK_RECORD = 1;
    /** 重置日志检测进程 */
    private static final int RESET_PROCESS = 2;

    /** 主线程Handler */
    private Handler mainHandler;

    /** 需不需要重置唤醒 */
    private volatile boolean needResetWakeup = false;

    /** 原始录音是不是在运行 */
    private boolean isCycle = false;
    /** logcat进程 */
    private Process logcatProcess = null;
    /** 日志读取 */
    //private BufferedReader bufferedReader = null;
    /** 日志读取 */
    private DataInputStream dataInputStream;
    /** 日志读取的缓存 */
    private byte[] buf = new byte[1024];

    /** 发音设备 */
    private AbstractVoice voiceDevice;

    public TaskService() {
        super("TaskService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public TaskService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 给发音设备赋值
        voiceDevice = MindPresenter.getInstance().getVoiceDevice();

        setIntentRedelivery(true);
        isCycle = false;
        mainHandler = new Handler(Looper.getMainLooper(), this);

        // 重置日志监听进程
        resetProcess();

        // 发送原始录音检测消息，15秒钟后开始检测
        mainHandler.sendEmptyMessageDelayed(CHECK_RECORD, 15000);
    }

    /**
     * 重置日志监听进程
     */
    private void resetProcess(){
        if(logcatProcess!=null) {
            logcatProcess.destroy();
        }
        if(dataInputStream!=null){
            try {
                dataInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /**
         * “logcat”不用说了吧，我们就是要监听它
         * “MSC_LOG”表示监听的Tag 这里以上面点击按钮输出的LOG信息为例。
         * “D”表示监听的Log类型，当然这里还可以写其它类型 。VERBOSE(v) 、DEBUG(d)、 INFO(i)、 WARN(w)、 ERROR(e)， 不过须要与监听的与Tag一一对称才可以。
         * “*:s”表示监听所有的信息，这里表示只要tag是MSC_LOG ,Logcat类型为D的所有Log都会被获取到。
         */
        logcatProcess = ShellUtils.getProcess(new String[] {"logcat", "MSC_LOG:D *:S"});
        if(logcatProcess!=null) {
            //bufferedReader = new BufferedReader(new InputStreamReader(logcatProcess.getInputStream()));
            dataInputStream = new DataInputStream(logcatProcess.getInputStream());
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(dataInputStream==null){
            isCycle = false;
            return;
        }

        while (isCycle){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /*
            try {
                if (dataInputStream.read(buf) > -1) {
                    LogUtils.e(TAG, getString(R.string.wakeup_okey));
                    needResetWakeup = false;
                }else {

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            */

            LogUtils.e(TAG, getString(R.string.wakeup_okey));
            if(!voiceDevice.isReboot()){
                needResetWakeup = false;
            }

        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        LogUtils.e(TAG, "startId="+startId+" isCycle="+isCycle);
        if(!isCycle) {
            super.onStart(intent, startId);
            isCycle = true;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        keepForeground(startId, intent);
        return super.onStartCommand(intent, flags, startId);
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

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){

            case CHECK_RECORD:
                if(needResetWakeup){

                    // 实施动作，开始重启唤醒
                    if(voiceDevice.reboot()){
                        // 打印日志，开始重启唤醒
                        LogUtils.e(TAG, getString(R.string.wakeup_error));
                        // 保存唤醒死亡的日志
                        FileUtils.saveLog(getApplicationContext(), "wakeup", getString(R.string.wakeup_error));

                        // 确保检测线程已启动
                        //Intent intent = new Intent(this, TaskService.class);
                        //startService(intent);

                        // 5秒钟之后再重置日志检测进程
                        mainHandler.sendEmptyMessageDelayed(CHECK_RECORD, 5000);

                    }else {
                        // 打印日志，官方用法，不需要唤醒
                        LogUtils.e(TAG, getString(R.string.wakeup_no_use));

                        isCycle = false;
                        if(logcatProcess!=null) {
                            logcatProcess.destroy();
                        }
                        if(dataInputStream!=null){
                            try {
                                dataInputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }else {
                    // 每次检查都置成true
                    voiceDevice.setReboot(true);
                    needResetWakeup = true;
                    mainHandler.sendEmptyMessageDelayed(CHECK_RECORD, 8000);
                }

                break;

            case RESET_PROCESS:
                resetProcess();
                // 发送原始录音检测消息，15秒钟后开始检测
                mainHandler.sendEmptyMessageDelayed(CHECK_RECORD, 15000);
                break;

            default:
                break;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCycle = false;
        if(dataInputStream!=null){
            try {
                dataInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(logcatProcess!=null) {
            logcatProcess.destroy();
        }
    }

}
