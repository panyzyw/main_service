package com.zccl.ruiqianqi.mind.receiver;

import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothHeadset;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import com.zccl.ruiqianqi.mind.intent.MyIntent;
import com.zccl.ruiqianqi.mind.receiver.battery.BatteryReceiver;
import com.zccl.ruiqianqi.mind.receiver.internet.NetChangedReceiver;
import com.zccl.ruiqianqi.mind.receiver.media.MediaButtonReceiver;
import com.zccl.ruiqianqi.mind.receiver.phone.PhoneReceiver;
import com.zccl.ruiqianqi.mind.receiver.sensor.SensorReceiver;
import com.zccl.ruiqianqi.mind.receiver.system.SystemReceiver;
import com.zccl.ruiqianqi.mind.service.SystemService;
import com.zccl.ruiqianqi.mind.state.MyStateRegister;
import com.zccl.ruiqianqi.tools.Download;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Intent.ACTION_AIRPLANE_MODE_CHANGED;
import static android.content.Intent.ACTION_NEW_OUTGOING_CALL;
import static android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED;

/**
 * Created by ruiqianqi on 2016/8/10 0010.
 */
public class Registers {

    /** 类日志标志 */
    private static String TAG = Registers.class.getSimpleName();
    /** 单例引用 */
    private static Registers instance;
    /** 全局上下文 */
    protected Context mContext;

    /** 网络信号处理广播 */
    protected NetChangedReceiver netChangedReceiver;
    /** 跟触摸相关的广播 */
    private SensorReceiver sensorReceiver;
    /** 跟业务相关的广播 */
    private SystemReceiver systemReceiver;
    /** 跟电池相关的广播 */
    private BatteryReceiver batteryReceiver;
    /** 跟来去电话相关广播 */
    private PhoneReceiver phoneReceiver;
    // MediaButton组件
    private ComponentName componentMediaButton;
    // 跟远程控制音乐播放有关的广播
    private MediaButtonReceiver mediaButtonReceiver;

    // 来电相关状态注册
    private MyStateRegister myStateRegister;

    protected Registers(Context context){
        this.mContext = context.getApplicationContext();
        myStateRegister = new MyStateRegister(mContext);
    }

    /**
     * 用这个用话，instance不需要用volatile修饰
     * @return
     */
    public static Registers getInstance(Context context) {
        if(instance == null) {
            synchronized(Registers.class) {
                Registers temp = instance;
                if(temp == null) {
                    temp = new Registers(context);
                    instance = temp;
                }
            }
        }
        return instance;
    }

    /**
     * 初始化的注册功能
     * AlarmManager.ELAPSED_REALTIME表示闹钟在手机睡眠状态下不可用，该状态下闹钟使用相对时间（相对于系统启动开始），状态值为3；
     * AlarmManager.ELAPSED_REALTIME_WAKEUP表示闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟也使用相对时间，状态值为2；
     * AlarmManager.RTC表示闹钟在睡眠状态下不可用，该状态下闹钟使用绝对时间，即当前系统时间，状态值为1；
     * AlarmManager.RTC_WAKEUP表示闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟使用绝对时间，状态值为0；
     * AlarmManager.POWER_OFF_WAKEUP表示闹钟在手机关机状态下也能正常进行提示功能，所以是5个状态中用的最多的状态之一，该状态下闹钟也是用绝对时间，状态值为4；不过本状态好像受SDK版本影响，某些版本并不支持；
     *
     * @param cls       闹钟要启动的类
     * @param action    闹钟要发的ACTION
     */
    public void registerAlarm(Class<?> cls, String action){
        // 注册60秒一次的闹钟，动作是启动服务【动作也可以设置为启动广播或Activity】
        Intent intent = new Intent(mContext, cls);
        intent.setAction(action);
        AlarmManager am = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);

        // 隐式启动服务
        //PendingIntent pendingAlarm = PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 隐式启动广播
        PendingIntent pendingAlarm = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        /**
         * 某年某月某日触发
         * am.set(AlarmManager.RTC_WAKEUP, CheckUtils.getMonthBeginTime(), pending_alarm);
         * 某年某月某日触发，隔多少时间一直触发
         * SystemClock.elapsedRealtime();
         */
        // 10秒钟之后启动，间隔60秒发送一次
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10*1000, 60*1000, pendingAlarm);
    }

    /**
     * 取消闹钟
     *
     * @param cls       闹钟要启动的类
     * @param action    闹钟要发的ACTION
     */
    public void cancelAlarm(Class<?> cls, String action) {
        Intent intent = new Intent(mContext, cls);
        intent.setAction(action);
        AlarmManager manager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);

        //PendingIntent pendingIntent = PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 取消正在执行的服务
        manager.cancel(pendingIntent);
    }

    /**
     * 注册重复ACTION，系统心跳
     */
    private void registerRepeatAction(){

        /**
         * 这个广播【每分钟发送一次】【只能动态注册】
         * You can not receive this through components declared
         * in manifests, only by exlicitly registering for it with
         * Context.registerReceiver()}.
         */
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);// 设置优先级最大//取值范围：-1000到1000
        filter.addAction(SystemReceiver.ACTION_TIME_TICK);
        mContext.registerReceiver(systemReceiver, filter);

    }


    /**
     * 注册APP系统广播
     */
    public void registerSystemReceiver(){
        systemReceiver = new SystemReceiver();

        // 注册闹钟，【60秒一次】
        //registerAlarm(SystemReceiver.class, SystemReceiver.ACTION_ALARM);

        // 注册重复ACTION
        registerRepeatAction();

        // SystemReceiver.ACTION_LAUNCH 【由launcher发送的5秒钟一次】静态注册了
        // SystemReceiver.ACTION_BOOT   【系统开机广播】静态注册了

        IntentFilter filter = new IntentFilter();
        filter.addAction(SystemReceiver.ACT_RECYCLE_LISTEN);
        filter.addAction(SystemReceiver.ACT_STOP_LISTEN);
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        filter.addAction(SystemReceiver.ENTER_VIDEO_COMM);
        filter.addAction(SystemReceiver.EXIT_VIDEO_COMM);
        filter.addAction(SystemReceiver.ENTER_VIDEO_MONITOR);
        filter.addAction(SystemReceiver.EXIT_VIDEO_MONITOR);
        filter.addAction(SystemReceiver.FACTORY_START);
        filter.addAction(SystemReceiver.FACTORY_CLOSE);

        mContext.registerReceiver(systemReceiver, filter);

    }

    /**
     * 注销APP系统广播
     */
    public void unregisterSystemReceiver(){
        // 取消闹钟
        cancelAlarm(SystemService.class, SystemReceiver.ACTION_ALARM);
        // 注销APP系统广播
        mContext.unregisterReceiver(systemReceiver);

    }

    /**
     * 注册触摸相关广播
     */
    public void registerSensorReceiver(){
        /**
         * 注册开屏灭屏相关广播【只能动态注册】
         * You can not receive this through components declared
         * in manifests, only by exlicitly registering for it with
         * Context.registerReceiver().
         */
        sensorReceiver = new SensorReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SensorReceiver.TOUCH_SENSOR);
        filter.addAction(SensorReceiver.HDMI_SHORT_PRESS);
        filter.addAction(SensorReceiver.HDMI_LONG_PRESS);

        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SHUTDOWN );
        mContext.registerReceiver(sensorReceiver, filter);
    }

    /**
     * 注销触摸相关广播
     */
    public void unregisterSensorReceiver(){
        mContext.unregisterReceiver(sensorReceiver);
    }

    /**
     * 注册网络相关广播
     */
    public void registerNetReceiver(){
        netChangedReceiver = new NetChangedReceiver();
        IntentFilter filter = new IntentFilter();
        // 这个是信号强弱的广播
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        // 这个监听wifi的打开与关闭，与wifi的连接无关
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        // 网络状态
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        // 网络状态
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(netChangedReceiver, filter);
    }

    /**
     * 注销网络相关广播
     */
    public void unregisterNetReceiver(){
        mContext.unregisterReceiver(netChangedReceiver);
    }

    /**
     * 注册电池相关广播
     */
    public void registerBatteryReceiver(){
        batteryReceiver = new BatteryReceiver();
        IntentFilter filter = new IntentFilter();
        // 这个是电池电量改变时发送
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        // 这个是电池电量低时发送
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        // 这个是电池电量低了，随后又恢复了的时候发送
        //This will be sent after {@link #ACTION_BATTERY_LOW} once the battery has gone back up to an okay state.
        filter.addAction(Intent.ACTION_BATTERY_OKAY);
        // 这个是连接外部电源时
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        // 这个是断开连接电源
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);

        mContext.registerReceiver(batteryReceiver, filter);
    }

    /**
     * 注销电池相关广播
     */
    public void unregisterBatteryReceiver(){
        mContext.unregisterReceiver(batteryReceiver);

    }

    /**
     * 注册一个下载完成广播
     */
    public void registerDownloadReceiver(){
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mContext.registerReceiver(Download.downloadReceiver, filter);
    }

    /**
     * 注销一个下载完成广播
     */
    public void unregisterDownloadReceiver(){
        mContext.unregisterReceiver(Download.downloadReceiver);
    }



    /*************************************【来电多媒体相关】***************************************/
    /**
     * 注册电话广播
     */
    public void registerPhoneReceiver(){
        phoneReceiver = new PhoneReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PHONE_STATE_CHANGED);
        filter.addAction(ACTION_NEW_OUTGOING_CALL);
        filter.addAction(ACTION_AIRPLANE_MODE_CHANGED);
        mContext.registerReceiver(phoneReceiver, filter);
    }

    /**
     * 注销电话广播
     */
    public void unregisterPhoneReceiver(){
        mContext.unregisterReceiver(phoneReceiver);
    }

    /**
     * 注册监听各种状态
     */
    public void registerPhoneListener(){
        myStateRegister.registerPhoneListener();
    }

    /**
     * 注销监听各种状态
     */
    public void unregisterPhoneListener(){
        myStateRegister.unregisterPhoneListener();
    }


    /**********************************************************************************************/
    /**
     * 注册远程Media控制广播
     *
     * 下面说说两种注册ACTION_MEDIA_BUTTON不同的地方，需要说这个就要讲解一下ACTION_MEDIA_BUTTON的发送机制。
     * 这个主要是AudioManager做的事情，AudioManager是上层的一个高层封装的音频管理类，真正实现音频的管理的是IAudioService 实现，
     * 这里面涉及了Android的底层核心Binder机制，这个是分析Android系统层必须掌握的课程。今天主要是讲解ACTION_MEDIA_BUTTON，
     * 所以我这里不做深入分析Binder机制和下面的IAudioService ，后面有空我会写一些分析Binder机制的文章。
     * 下面简单说一下ACTION_MEDIA_BUTTON消息是如何广播分发的，让大家使用的时候知道何时使用哪种注册方式。
     A、AudioManager或者说AudioService服务端对象内部会利用一个栈来管理所有registerMediaButtonEventReceiver()注册的ComponentName对象，
     最后调用registerMediaButtonEventReceiver()注册的ComponentName就位置这个栈的栈顶。
     B、当系统发送MEDIA_BUTTON，系统MediaButtonBroadcastReceiver 监听到系统广播，它会做如下处理：
     如果栈为空，则所有注册了该Action的广播都会接受到，因为它是由系统发送的。
     如果栈不为空，那么只有栈顶的那个广播能接受到MEDIA_BUTTON的广播，手动发送了MEDIA_BUTTON广播，并且指定了目标对象(栈顶对象)去处理该MEDIA_BUTTON 。
     上面的两条规则，大家一定要记住，这个关系我们注册的ACTION_MEDIA_BUTTON能否正常工作。当然这个是系统全局广播，需要大家的APP都遵守这个规则才可以让系统正常运行。

     1、在AudioManager对象注册一个MediaButtonReceiver，使它成为MEDIA_BUTTON的唯一接收器，也就是说只有我能收到，
        其他的都收不到这个广播了，否则的话大家都收到会照成一定的混乱；
     2、该广播必须在AndroidManifest.xml文件中进行声明，否则就监听不到该MEDIA_BUTTON广播了。
     <receiver android:name="MediaButtonReceiver">
         <intent-filter >
            <action android:name="android.intent.action.MEDIA_BUTTON"></action>
         </intent-filter>
     </receiver>
     */
    public void registerMediaButton(){
        // 获取音频服务
        AudioManager audioManager = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);
        // 注册接收的Receiver
        componentMediaButton = new ComponentName(mContext.getPackageName(), MediaButtonReceiver.class.getName());
        // 注册MediaButton
        audioManager.registerMediaButtonEventReceiver(componentMediaButton);
    }

    /**
     * 注销远程Media控制广播
     */
    public void unregisterMediaButton(){
        // 获取音频服务
        AudioManager audioManager = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);
        // 取消注册
        audioManager.unregisterMediaButtonEventReceiver(componentMediaButton);
    }

    /**
     * 注册耳机及远程Media控制广播
     */
    public void registerHeadsetPlugReceiver() {
        mediaButtonReceiver = new MediaButtonReceiver();
        IntentFilter intentFilter = new IntentFilter();

        /*
        该广播必须在AndroidManifest.xml文件中进行声明，否则就监听不到该MEDIA_BUTTON广播了。
         <receiver android:name="MediaButtonReceiver">
             <intent-filter >
                <action android:name="android.intent.action.MEDIA_BUTTON"></action>
             </intent-filter>
         </receiver>
         也就是动态注册没什么用
        */
        intentFilter.addAction(Intent.ACTION_MEDIA_BUTTON);

        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        mContext.registerReceiver(mediaButtonReceiver, intentFilter);
    }

    /**
     * 注销耳机及远程Media控制广播
     */
    public void unregisterHeadsetPlugReceiver() {
        mContext.unregisterReceiver(mediaButtonReceiver);
    }



}
