package com.zccl.ruiqianqi.tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.KeyguardManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.PowerManager;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.zccl.ruiqianqi.tools.beans.DataPackInfo;
import com.zccl.ruiqianqi.tools.config.MyConfigure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;

import static android.security.KeyStore.getApplicationContext;

/**
 * Created by ruiqianqi on 2016/7/26 0026.
 */
public class SystemUtils {

    // 类标志
    private static String TAG = SystemUtils.class.getSimpleName();

    // 系统锁屏服务
    // 打开/关闭系统锁屏服务时必须使用同一个KeyguardLock对象，否则出错
    private static KeyguardManager.KeyguardLock mKeyguardLock;

    /**
     * 初始化System相关资源
     * @param context
     */
    public static void initSystem(Context context){
        KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        mKeyguardLock = mKeyguardManager.newKeyguardLock(TAG);
    }

    /**********************************【系统版本号相关】******************************************/
    /**
     * 获得应用版本号 100
     * @param context
     * @return
     */
    public static int getAppVersion(Context context){
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);

            /**
             ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_SHARED_LIBRARY_FILES);
             String libpath = appInfo.nativeLibraryDir;    //API 9 /data/data/com.zccl.game/lib
             String[] soList = appInfo.sharedLibraryFiles; //null

             List<PackageInfo> packs = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES | PackageManager.GET_UNINSTALLED_PACKAGES);
             List<PackageInfo> packs = pm.getInstalledPackages(0);

             pm.getActivityInfo(component, flags);
             pm.getServiceInfo(component, flags);
             pm.getReceiverInfo(component, flags);
             */

            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获得应用版本号对应名称 100.00
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pkgInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return pkgInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用的名称
     * @param context
     * @return
     */
    public static String getAppName(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), 0);
            String appName = pm.getApplicationLabel(appInfo).toString();
            appName = appInfo.loadLabel(pm).toString();
            return appName;
        } catch (PackageManager.NameNotFoundException e) {

        }
        return null;
    }

    /**
     * 当前应用PID，基本上就是我的应用本身的
     * @return
     */
    public static int getPid(){
        return android.os.Process.myPid();
    }

    /**
     * 当前应用UID，基本上就是我的应用本身的
     * @param context
     * @return
     */
    public static int getUid(Context context){
        android.os.Process.myUid();
        return context.getApplicationInfo().uid;
    }

    /**
     * 判断是不是 ART 虚拟机运行环境 or DEX虚拟机
     * @return
     */
    public static boolean isART(){
        String version = System.getProperty("java.vm.version");
        if (Integer.valueOf(version.substring(0, version.indexOf("."))) >= 2) {
            //You are currently using ART!
            return true;
        } else {
            //You are currently using Dalvik!;
            return false;
        }
    }


    /*********************************【CPU相关】**************************************************/
    /**
     * 获取CPU的核数
     * @return
     */
    public static int getNumberOfCPUCores(){
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * 获取CPU的核数2
     * @return
     */
    public static int getNumberOfCPUCores2() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            // Gingerbread doesn't support giving a single application access to both cores, but a
            // handful of devices (Atrix 4G and Droid X2 for example) were released with a dual-core
            // chipset and Gingerbread; that can let an app in the background run without impacting
            // the foreground application. But for our purposes, it makes them single core.
            return 1;
        }
        int cores;
        try {
            cores = new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER).length;
        } catch (SecurityException e) {
            cores = 1;
        } catch (NullPointerException e) {
            cores = 1;
        }
        return cores;
    }

    /**
     * 线程池的最大上限值
     * @param max
     * @return
     */
    public static int getDefaultThreadPoolSize(int max) {
        int availableProcessors = 2 * getNumberOfCPUCores() + 1;
        return availableProcessors > max ? max : availableProcessors;
    }


    /**
     * 获得进程的CPU使用率2
     * @return
     */
    public static String getCpuRate() {
        StringBuffer tv = new StringBuffer();
        String Result;
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("top -n 1");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((Result = br.readLine()) != null) {
                if (Result.trim().length() < 1) {
                    continue;
                } else {
                    String[] CPUusr = Result.split("%");
                    tv.append("USER:" + CPUusr[0] + "\n");
                    String[] CPUusage = CPUusr[0].split("User");
                    String[] SYSusage = CPUusr[1].split("System");
                    tv.append("CPU:" + CPUusage[1].trim() + " length:" + CPUusage[1].trim().length() + "\n");
                    tv.append("SYS:" + SYSusage[1].trim() + " length:" + SYSusage[1].trim().length() + "\n");
                    tv.append(Result + "\n");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tv.toString();
    }


    /**
     * 获得进程的CPU使用率
     * @return
     */
    public static int[] getProcessCpuRate() {

        int[] cpuRate = new int[2];
        long totalCpuTime1[] = getTotalCpuTime();
        long processCpuTime1 = getAppCpuTime();
        try {
            Thread.sleep(360);
        } catch (Exception e) {

        }
        long totalCpuTime2[] = getTotalCpuTime();
        long processCpuTime2 = getAppCpuTime();

        long div0 = totalCpuTime2[0] - totalCpuTime1[0];
        if(div0!=0) {
            cpuRate[0] = (int) (100 * (processCpuTime2 - processCpuTime1) / (div0));
        }else{
            cpuRate[0] = 0;
        }

        long a = (totalCpuTime2[0]-totalCpuTime1[0])-(totalCpuTime2[1]-totalCpuTime1[1]);
        long div1 = (totalCpuTime2[0]-totalCpuTime1[0]);
        if(div1!=0) {
            cpuRate[1] = (int) (100 * (a) / (div1));
        }else{
            cpuRate[1] = 0;
        }
        return cpuRate;
    }

    /**
     * 获取系统总CPU使用时间
     * @return
     */
    private static long[] getTotalCpuTime() {
        String[] cpuInfos = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        long[] totalCpu = new long[2];
        totalCpu[0] = Long.parseLong(cpuInfos[2])
                + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
                + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
                + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
        totalCpu[1] = Long.parseLong(cpuInfos[5]);
        return totalCpu;
    }

    /**
     * 获取应用占用的CPU时间
     * @return
     */
    private static long getAppCpuTime() {
        String[] cpuInfos = null;
        try {
            int pid = android.os.Process.myPid();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/" + pid + "/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        long appCpuTime = Long.parseLong(cpuInfos[13])
                + Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
                + Long.parseLong(cpuInfos[16]);
        return appCpuTime;
    }

    /**
     * 过滤CPU对应的核心数文件
     */
    private static final FileFilter CPU_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            String path = pathname.getName();
            //regex is slow, so checking char by char.
            if (path.startsWith("cpu")) {
                for (int i = 3; i < path.length(); i++) {
                    if (path.charAt(i) < '0' || path.charAt(i) > '9') {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    };

    /*************************************【运存相关】*********************************************/
    /**
     * 系统总内存
     * @param context
     * @return 单位为字节
     */
    public static String getSystemTotalMemory(Context context) {
        String memInfo = "/proc/meminfo";// 系统内存信息文件
        String info;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(memInfo);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            info = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
            arrayOfString = info.split("\\s+");

            for (String num : arrayOfString) {
                LogUtils.e(TAG, num + "");
            }

            initial_memory = Long.valueOf(arrayOfString[1]) * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();

        } catch (IOException e) {
        }
        return Formatter.formatFileSize(context, initial_memory);// Byte转换为KB或者MB，内存大小规格化
    }

    /**
     * 系统剩余内存
     * @param context
     * @return
     */
    public static String getSystemAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        // mi.availMem; 当前系统的可用内存
        return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
    }

    // 获取单个APP内存大小限制的属性值
    public static final String APP_LIMIT_MEMORY = "dalvik.vm.heapgrowthlimit";

    /**
     * 单个APP内存限制大小 -Xmx
     * JAVA虚拟机能申请的内存上限【8M，16M，24M，32M，64M，96M，128M，256M】
     * maxMemory()这个方法返回的是java虚拟机（这个进程）能够从操作系统那里挖到的最大的内存
     *
     * 方法3
     * getprop | grep dalvik.vm.heapgrowthlimit
     * [dalvik.vm.heapgrowthlimit]: [256m]------------------getMemoryClass
     * [dalvik.vm.heapsize]: [512m]-------------------------getLargeMemoryClass
     *
     * 方法4
     * cat /system/build.prop | grep dalvik.vm.heap
     * dalvik.vm.heapgrowthlimit=256m-----------------------getMemoryClass
     * dalvik.vm.heapsize=512m------------------------------getLargeMemoryClass
     *
     * @param context
     * @return
     */
    public static String getAppTotalMemory(Context context){
        // 方法1【字节为单位】
        long maxMemory = Runtime.getRuntime().maxMemory();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // 方法2【以兆为单位】
        int maxMemoryAndroid = am.getMemoryClass() * 1024 * 1024;
        // 以兆为单位
        int largeMemoryAndroid = am.getLargeMemoryClass() * 1024 * 1024;
        return Formatter.formatFileSize(context, maxMemoryAndroid);
    }

    /**
     * 进程真正的可用可申请的内存大小
     * maxMemory() - totalMemory() + freeMemory()
     * * 进程真正的可用可申请的内存大小【应用剩余内存】
     * maxMemory()-totalMemory()+freeMemory()
     * <p>
     * 查看指定程序内存使用情况
     * dumpsys meminfo com.zccl.ruiqianqi
     * <p>
     * 参数含义：
     * dalvik : dalvik使用的内存
     * native : native堆上的内存，指C\C++堆的内存（android 3.0以后bitmap就是放在这儿）
     * other  : 除了dalvik和native的内存，包含C\C++非堆内存······
     * Pss    : 该内存指将共享内存按比例分配到使用了共享内存的进程
     * allocated : 已使用的内存
     * free      : 空闲的内存
     * private dirty : 非共享，又不能被换页出去的内存（比如linux系统中为了提高分配内存速度而缓冲的小对象，即使你的进程已经退出，该内存也不会被释放）
     * share dirty   : 共享，但有不能被换页出去的内存
     * @return
     */
    public static String getAppAvailMemory(Context context){
        //java虚拟机能申请的内存上限【8M，16M，24M，32M，64M，128M，256M】
        long maxMemory = Runtime.getRuntime().maxMemory();
        //totalMemory()这个方法返回的是java虚拟机现在已经从操作系统那里挖过来的内存大小，
        //在java程序运行的过程的，内存总是慢慢的从操作系统那里挖的，基本上是用多少挖多少，
        //直到挖到maxMemory()为止，所以totalMemory()是慢慢增大的
        long totalMemory = Runtime.getRuntime().totalMemory();
        //java虚拟机每次在申请内存的稍微多挖一点的，这些挖过来而又没有用上的内存，实际上就是freeMemory()
        long freeMemory = Runtime.getRuntime().freeMemory();
        //byte
        long free = maxMemory - totalMemory + freeMemory;

        return Formatter.formatFileSize(context, free);//(maxMemory-totalMemory + freeMemory)/1024;
    }

    /**
     * 获得内存相关信息
     * @param context
     */
    public static void getMemInfo(Context context){
        String s1 = SystemUtils.getSystemTotalMemory(context);
        String s2 = SystemUtils.getSystemAvailMemory(context);
        String s3 = SystemUtils.getAppTotalMemory(context);
        String s4 = SystemUtils.getAppAvailMemory(context);
        LogUtils.e(TAG, s1 + "-" + s2 + "-" + s3 + "-" + s4);
    }

    /***********************************【屏幕亮度相关】*******************************************/
    /**
     * 得到屏幕当前状态
     * @param context
     * @return
     */
    public static String getScreenState(Context context){
        PowerManager manager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (manager.isScreenOn()) {
            return Intent.ACTION_SCREEN_ON;
        } else {
            return Intent.ACTION_SCREEN_OFF;
        }
    }

    /**
     * 保持屏幕常亮
     *  as long as this window is visible to the user, keep the device's screen turned on and bright.
     * @param activity
     */
    public static void keepScreenOn(Activity activity){
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 取消屏幕常亮
     * @param activity
     */
    public static void keepScreenOff(Activity activity){
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 唤醒屏幕
     * 另外WakeLock的设置是 Activity 级别的，不是针对整个Application应用的。
     * 可以在activity的onResume方法里面操作WakeLock,  在onPause方法里面释放。
     * @param context
     *
     * 关于int flags
     * PARTIAL_WAKE_LOCK:       保持CPU 运转，屏幕和键盘灯有可能是关闭的。
     * SCREEN_DIM_WAKE_LOCK：   保持CPU 运转，允许保持屏幕显示但有可能是灰的，允许关闭键盘灯
     * SCREEN_BRIGHT_WAKE_LOCK：保持CPU 运转，允许保持屏幕高亮显示，允许关闭键盘灯
     * FULL_WAKE_LOCK：         保持CPU 运转，保持屏幕高亮显示，键盘灯也保持亮度
     * ACQUIRE_CAUSES_WAKEUP：  正常唤醒锁实际上并不打开照明。相反，一旦打开他们会一直仍然保持(例如来世user的activity)。
     *                          当获得wakelock，这个标志会使屏幕或/和键盘立即打开。一个典型的使用就是可以立即看到那些对用户重要的通知。
     * ON_AFTER_RELEASE：       设置了这个标志，当wakelock释放时用户activity计时器会被重置，导致照明持续一段时间。
     *                          如果你在wacklock条件中循环，这个可以用来减少闪烁
     *
     * 获取WakeLock实例后通过acquire()获取相应的锁，然后进行其他业务逻辑的操作，最后使用release()释放（释放是必须的）。
     */
    public static void wakeUp(Context context){
        LogUtils.e(TAG, "wakeUp");
        // 唤醒亮屏
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "wakeUp");
        // 申请锁，这里会调用PowerManagerService里面acquireWakeLock()
        // 点亮屏幕
        wl.acquire();

        // 释放锁，显示的释放锁，如果申请的锁不在此释放，系统就不会进入休眠。
        if(wl.isHeld()) {
            wl.release();
        }
    }

    /**
     * 关闭系统锁屏服务
     * disableKeyguard只是关闭系统锁屏服务，调用该方法后并不会立即解锁，而是使之不显示解锁
     *
     * Disable the keyguard from showing. If the keyguard is currently showing, hide it.
     * The keyguard will be prevented from showing again until reenableKeyguard() is called.
     *
     * @param activity
     */
    public static void disableKeyguard(Activity activity){
        LogUtils.e(TAG, "disableKeyguard");

        mKeyguardLock.disableKeyguard();
        /*
        这一个标志的意思是去掉锁屏界面，但这对安全锁（图案或者密码锁屏界面）是无效的。在没有设置安全锁的时候，
        我们需要上滑一下（可能是其它方式）退出锁屏界面，才能进入桌面，这个标志就是去掉这一界面，
        使得比如网易云音乐这类在锁屏界面播放的时候，只需要直接解锁云音乐的锁屏即可，不需要再上滑解除系统本身的锁屏。
        可以认为锁屏界面是一个窗口视图，解锁图案界面是另一个窗口视图，FLAG_DISMISS_KEYGUARD只能控制去除锁屏界面窗口。
        */
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    /**
     * 打开系统锁屏服务
     * reenableKeyguard是恢复锁屏服务，并不会立即锁屏
     *
     * Reenable the keyguard. The keyguard will reappear if the previous call todisableKeyguard() caused it it to be hidden.
     *
     * @param activity
     */
    public static void enableKeyguard(Activity activity){
        LogUtils.e(TAG, "enableKeyguard");

        mKeyguardLock.reenableKeyguard();
        /*
        这一个标志的意思是使得窗口浮在锁屏界面之上。这给像电话这类应用一个快捷的不需要解锁的就能使用的便利。
        */
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }


    /**************************************【语言切换相关】****************************************/
    /**
     * 下面的方法只有在新启动的activity中才能生效
     * 应用内切换语言
     * 切换之后，要想生效必须重新启动所有Activity
     * @param language
     */
    public static void switchLanguage(Context context, String language) {
        // 设置应用语言类型
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (language.equals("en")) {
            config.locale = Locale.ENGLISH;
        } else {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        }
        resources.updateConfiguration(config, dm);

        // 保存设置语言的类型
        ShareUtils.getE(context).putString(MyConfigure.KEY_LANGUAGE, language).commit();
    }

    /**
     * 改变系统语言设置
     * @param locale
     */
    public static void switchSystemLanguage(Locale locale){
        IActivityManager iActMag = ActivityManagerNative.getDefault();
        try {
            Configuration config = iActMag.getConfiguration();
            config.locale = locale;
            // 此处需要声明权限:android.permission.CHANGE_CONFIGURATION
            // 会重新调用 onCreate();
            iActMag.updateConfiguration(config);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /*************************************【结束应用相关】*****************************************/
    // 反射参数集合
    private static List<Object> args = new ArrayList<>();
    /**
     * 当前APP及所在进程完全退出
     * @param context
     */
    public static void exitApp(Context context) {
        LogUtils.e("Exit APP", "SystemUtils Exit APP!");

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO) {// 2.2版本

            // activityManager.restartPackage(context.getPackageName());

            // 根据应用包名杀掉应用进程
            activityManager.killBackgroundProcesses(context.getPackageName());
            // 根据当前进程ID杀掉进程
            android.os.Process.killProcess(android.os.Process.myPid());

        } else {
            // 毁掉一切与此包相关的东西
            // activityManager.forceStopPackage(context.getPackageName());

            // 根据应用包名杀掉应用进程
            activityManager.killBackgroundProcesses(context.getPackageName());

            // 根据当前进程ID杀掉进程（用这个只能用于自杀，及杀掉与自身有关系的进程）
            //a、将被杀掉的进程 和 当前进程 处于同一个包或者应用程序中；
            //b、将被杀掉的进程 是 由当前应用程序所创建的附加进程；
            //c、将被杀掉的进程 和 当前进程 共享了普通用户的UID。
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        args.clear();
        args.add(context.getPackageName());
        ReflectUtils.callObjectMethod(activityManager, "forceStopPackage", args, new Class<?>[] { String.class });
    }


    /***********************************【当前正在运行的应用】*************************************/
    /**
     * 获取正在运行的应用
     * @param context
     * @return
     */
    public static List<RunningAppProcessInfo> getRunningAppProcesses(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return manager.getRunningAppProcesses();
    }

    /**
     * 结束App进程，计划用来杀别的，好像不行，就这么几个方法
     * @param context
     * @param packageName
     * @return
     */
    public static void killRunningApp(Context context, String packageName) {
        List<RunningAppProcessInfo> runningAppProcesses = getRunningAppProcesses(context);
        for(RunningAppProcessInfo runApp : runningAppProcesses){
            if(runApp.processName.contains(packageName)){
                LogUtils.e("killRunningApp", runApp.processName + "");
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                am.killBackgroundProcesses(runApp.processName);
            }
        }
    }

    /**
     * 结束App进程，计划用来杀别的，好像不行，就这么几个方法
     * @param context
     * @param pid
     * @return
     */
    public static void killRunningApp(Context context, int pid) {
        List<RunningAppProcessInfo> runningAppProcesses = getRunningAppProcesses(context);
        for(RunningAppProcessInfo runApp : runningAppProcesses){
            if(runApp.pid==pid){
                LogUtils.e("killRunningApp", runApp.processName + "");
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                am.killBackgroundProcesses(runApp.processName);
            }
        }
    }

    /**
     * 当前进程的包名
     * @param context  这个只是用来获取远程服务的
     * @return
     */
    public static String getProcessNameByPid(Context context, int pid) {
        String currentProcessName = null;
        if(0 == pid) {
            pid = android.os.Process.myPid();
        }
        List<RunningAppProcessInfo> runningAppProcesses = getRunningAppProcesses(context);
        for (RunningAppProcessInfo processInfo : runningAppProcesses) {
            if (processInfo.pid == pid) {
                currentProcessName = processInfo.processName;
                break;
            }
        }
        return currentProcessName;
    }

    /**
     * 当前进程的PID
     * @param context  这个只是用来获取远程服务的
     * @return
     */
    public static int getProcessPidByName(Context context, String packageName) {
        if(StringUtils.isEmpty(packageName))
            return 0;
        List<RunningAppProcessInfo> runningAppProcesses = getRunningAppProcesses(context);
        for (RunningAppProcessInfo processInfo : runningAppProcesses) {
            if (TextUtils.equals(packageName, processInfo.processName)) {
                return processInfo.pid;
            }
        }
        return 0;
    }

    /**
     * 根据进程名,得到进程的相关信息
     * @param context         任意上下文
     * @param packageName     要查的进程包名
     * @return
     */
    public static RunningAppProcessInfo getRunningAppInfoByName(Context context, String packageName){
        List<RunningAppProcessInfo> runningAppProcesses = getRunningAppProcesses(context);
        for (int i = 0; i < runningAppProcesses.size(); i++) {
            if(runningAppProcesses.get(i).processName.equalsIgnoreCase(packageName)){
                return runningAppProcesses.get(i);
            }
        }
        return null;
    }

    /**
     * 根据进程名,得到进程的相关信息
     * @param context         任意上下文
     * @param pid              要查的进程PID
     * @return
     */
    public static RunningAppProcessInfo getRunningAppInfoByPid(Context context, int pid){
        List<RunningAppProcessInfo> runningAppProcesses = getRunningAppProcesses(context);
        for (int i = 0; i < runningAppProcesses.size(); i++) {
            if(runningAppProcesses.get(i).pid==pid){
                return runningAppProcesses.get(i);
            }
        }
        return null;
    }

    /**
     * 判断指定的App是否正在运行
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppRunning(Context context, String packageName) {
        List<RunningAppProcessInfo> runningAppProcesses = getRunningAppProcesses(context);
        boolean isRunning = false;
        for(RunningAppProcessInfo runApp : runningAppProcesses){
            if(runApp.processName.equals(packageName)){
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    /**
     * 判断指定的App是否在后台运行
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppRunningBack(Context context, String packageName) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(context.KEYGUARD_SERVICE);
        List<RunningAppProcessInfo> runningAppProcesses = getRunningAppProcesses(context);
        boolean isRunning = false;
        for(RunningAppProcessInfo runApp : runningAppProcesses){
            if(runApp.processName.equals(packageName)){
                boolean isBackground = (
                        runApp.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                                runApp.importance != RunningAppProcessInfo.IMPORTANCE_VISIBLE
                );
                boolean isLockedState = keyguardManager.inKeyguardRestrictedInputMode();
                if (isBackground || isLockedState){
                    isRunning = true;
                    break;
                }
            }
        }
        return isRunning;
    }


    /***********************************【当前正在运行的服务】*************************************/
    /**
     * 获取正在运行的应用
     * @param context
     * @return
     */
    public static List<RunningServiceInfo> getRunningServices(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return manager.getRunningServices(Integer.MAX_VALUE);
    }

    /**
     * 判断服务是否是运行状态
     * @param context
     * @param serviceClassName
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceClassName){
        if(StringUtils.isEmpty(serviceClassName))
            return false;
        List<RunningServiceInfo> runningServices = getRunningServices(context);
        for (RunningServiceInfo service : runningServices) {
            if (serviceClassName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断服务是否是运行状态
     * @param context
     * @param pid
     * @return
     */
    public static boolean isServiceRunning(Context context, int pid){
        List<RunningServiceInfo> runningServices = getRunningServices(context);
        for (RunningServiceInfo service : runningServices) {
            if (service.pid==pid) {
                return true;
            }
        }
        return false;
    }

    /********************************【当前正在运行的ACTIVITY】************************************/
    /**
     * 得到当前的，栈顶的应用包名
     * 5.1之后，这个方法对第三方应用取消了，系统权限的应用可以调用
     * @param context
     * @return
     */
    public static RunningTaskInfo getCurRunningTaskInfo(Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // 当前1个任务栈集合，5.1之后，这个方法对第三方应用取消了
        List<RunningTaskInfo> tasks = manager.getRunningTasks(10);
        // 判断当前活动是不是我们指定的
        //if(tasks.get(0).topActivity.getClassName().equalsIgnoreCase("com.zccl.mvp.view.MainActivity")){
        //	int num = tasks.get(0).numActivities;
        //}
        // 栈最底部是Application
        //String packageName = tasks.get(0).baseActivity.getPackageName();
        //return packageName;

        //tasks.get(0).topActivity.getClassName() == "com.zccl.mvp.view.MainActivity"
        //tasks.get(0).topActivity.getShortClassName() == "MainActivity"
        return tasks.get(0);
    }

    /**
     * 判断Activity是否在前台运行
     * @param context
     * @param activityName
     * @return
     */
    public static boolean isActivityRunning(Context context, String activityName) {
        RunningTaskInfo runningTaskInfo = getCurRunningTaskInfo(context);
        if (null != runningTaskInfo) {
            return TextUtils.equals(runningTaskInfo.topActivity.getClassName(), activityName);
        }
        return false;
    }

    /**
     * 判断App是否在前台运行
     * @param context
     * @param pkgName
     * @return
     */
    public static boolean isAppForeRunning(Context context, String pkgName) {
        RunningTaskInfo runningTaskInfo = getCurRunningTaskInfo(context);
        if (null != runningTaskInfo) {
            LogUtils.e(TAG, runningTaskInfo.topActivity.getPackageName() + "");
            return TextUtils.equals(runningTaskInfo.topActivity.getPackageName(), pkgName);
        }else {
            LogUtils.e(TAG, "null == runningTaskInfo");
        }
        return false;
    }

    /**
     * 当前最上层应用的包名
     * @param context
     * @return
     */
    public static String getCurrentAppPkgName(Context context){
        RunningTaskInfo runningTaskInfo = getCurRunningTaskInfo(context);
        if(null == runningTaskInfo){
            //context.getPackageName();
            //return context.getApplicationInfo().packageName;
            return null;
        }else{
            return runningTaskInfo.topActivity.getPackageName();
        }
    }

    /**
     * 当前最上层应用的PID
     * @param context
     * @return
     */
    public static int getCurrentAppPid(Context context){
        RunningTaskInfo runningTaskInfo = getCurRunningTaskInfo(context);
        if(runningTaskInfo == null){
            return 0;
        }
        String packageName = runningTaskInfo.topActivity.getPackageName();
        RunningAppProcessInfo info = getRunningAppInfoByName(context, packageName);
        if(null != info){
            return info.pid;
        }else{
            return 0;
        }
    }

    /*************************************【5.0后】************************************************/
    /**
     * 判断当前设备中有没有“有权查看使用权限的应用”这个选项
     * 虽说这个选项是Android 系统中自带的，但是现在国内很多厂商ROM众多，很多都给阉割掉了，例如：小米、魅族
     * @param context
     * @return
     */
    public static boolean isUsageAccessOption(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 当前最上层应用的包名
     * 需要权限 android.permission.PACKAGE_USAGE_STATS
     *
     * @param context
     * @return
     */
    public static String getTaskPkgName(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();

            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 60 * 1000, time);

            // 可以获得最近一分钟内使用过的APP的统计数据
            appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, time - 60 * 1000, time);

            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    return mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        }
        // 5.0后，已不能用了
        else {
            return getCurrentAppPkgName(context);
        }
        return null;
    }

    /************************************ 【getMetaData】******************************************/
    /**
     * Return the full application info for this context's package
     * @param context
     * @return
     */
    public static ApplicationInfo getApplicationInfo(Context context){
        return context.getApplicationInfo();
    }

    /**
     *
     * {@link android.content.pm.PackageInfo}
     * {@link android.content.pm.ResolveInfo}
     *
     * {@link android.content.pm.PackageItemInfo}
     * --------- {@link android.content.pm.ApplicationInfo}
     * ----------{@link android.content.pm.ComponentInfo}
     *                  ------ {@link android.content.pm.ActivityInfo}
     *                  -----  {@link android.content.pm.ServiceInfo}
     *                  -----  {@link android.content.pm.ProviderInfo}
     *
     使用方式如下：
     <meta-data android:name="string" android:resource="resource specification" android:value="string"/>
     它可以被包括在如下组件中：
     <application>
     <activity>
     <activity-alias>
     <service>
     <receiver>
     通常值是通过其value属性来指定的。但是，也可以使用resource属性来代替，把一个资源ID跟值进行关联。
     例如，下面的代码就是把存储在@string/kangaroo资源中的值跟”zoo”名称进行关联：
     <meta-data android:name="zoo" android:value="@string/kangaroo"/>

     另一个方面，使用resource属性会给zoo分配一个数字资源ID，而不是保存在资源中的值。例如：
     <meta-data android:name="zoo" android:resource="@string/kangaroo" />
     注意：这里保存的是（对应的Id），而不是保存的Id对应的资源。
     所以要取得android:resource对应的值得话，首先通过Bundle的getInt()方式取得（对应的Id），
     然后根据这个资源Id获取资源文件中的值。

     注意：要避免使用多个独立的<meta-data>实体来提供相关的数据。
     相反如果有复杂的数据要跟组件关联，那么把数据作为资源来保存，并使用resource属性，把相关的（资源ID）通知给组件。

     属性介绍如下：
     android:name   唯一名称。使用Java样式的命名规则来确保名称的唯一性，例如：com.example.project.activity.fred。
     android:resource 要引用的资源。资源的ID会跟这个项目进行关联。通过Bundle.getInt()方法能够从meta-data的Bundle对象中获取这个ID。
     android:value   对应的值
     */
    /**
     * 获取额外传递的数据值来自Activity
     * @param key
     * @return
     */
    public static String getActivityMetaData(Activity activity, String key){
        try {
            ActivityInfo info = activity.getPackageManager().getActivityInfo(activity.getComponentName(), PackageManager.GET_META_DATA);
            String msg = info.metaData.getString(key);
            return msg;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取额外传递的数据值来自Application
     * @param key
     * @return
     */
    public static String getApplicationMetaData(Context context, String key){
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            String msg = info.metaData.getString(key);
            return msg;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取额外传递的数据值来自BroadcastReceiver
     * @param key
     * @return
     */
    public static String getBroadcastMetaData(Context context, Class<? extends BroadcastReceiver> clazz, String key){
        try {
            ActivityInfo info = context.getPackageManager().getReceiverInfo(
                    new ComponentName(context, clazz), PackageManager.GET_META_DATA);
            String msg = info.metaData.getString(key);
            return msg;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取额外传递的数据值来自Service
     * @param key
     * @return
     */
    public static String getServiceMetaData(Context context, Class<? extends Service> clazz, String key){
        try {
            ServiceInfo info = context.getPackageManager().getServiceInfo(
                    new ComponentName(context, clazz), PackageManager.GET_META_DATA);
            String msg = info.metaData.getString(key);
            return msg;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*************************************【查询应用集合】*****************************************/
    // APP应用集合
    private static ArrayList<DataPackInfo> appInfoList = new ArrayList<>();
    /**
     * 获得相对应的应用
     * @param context
     */
    public static ArrayList<DataPackInfo> checkAllApp(Context context) {
        PackageManager pm = context.getPackageManager();
        // 过滤条件
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        // 返回符合条件的ACTIVITY集合 PackageManager.MATCH_DEFAULT_ONLY
        List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
        //List<ResolveInfo> apps = pm.queryIntentServices(resolveIntent, 0);

        // 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(pm));

        appInfoList.clear();
        for (ResolveInfo reInfo : apps) {
            // 获得该应用程序的启动Activity的name
            String activityName = reInfo.activityInfo.name;
            // 获得应用程序的包名
            String pkgName = reInfo.activityInfo.packageName;
            // 获得应用程序名
            String appLabel = reInfo.loadLabel(pm).toString();
            // 获得应用程序图标
            Drawable icon = reInfo.loadIcon(pm);

            // 为应用程序的启动Activity 准备Intent
            Intent launchIntent = new Intent(Intent.ACTION_MAIN);
            launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            launchIntent.setComponent(new ComponentName(pkgName, activityName));

            // 创建一个AppInfo对象，并赋值
            DataPackInfo newInfo = new DataPackInfo();
            newInfo.setAppName(appLabel);
            newInfo.setPackageName(pkgName);
            newInfo.setIcon(icon);
            newInfo.setIntent(launchIntent);

            try {
                PackageInfo pi = pm.getPackageInfo(pkgName, 0);
                newInfo.setAppName(pi.applicationInfo.loadLabel(pm).toString());
                newInfo.setDataDir(pi.applicationInfo.dataDir);
                newInfo.setSourceDir(pi.applicationInfo.sourceDir);
                newInfo.setProcessName(pi.applicationInfo.processName);
                newInfo.setNativeDir(pi.applicationInfo.nativeLibraryDir);

                newInfo.setPackageName(pi.packageName);
                newInfo.setVersionName(pi.versionName);
                newInfo.setVersionCode(pi.versionCode);

                newInfo.setIcon(pi.applicationInfo.loadIcon(pm));
                newInfo.setIcon(pm.getApplicationIcon(pi.applicationInfo));
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            appInfoList.add(newInfo);
        }
        return appInfoList;
    }


    // APP应用名key 与 应用标志value
    private static Map<String, Integer> pkgNameFlags = new HashMap<>();
    // 所有应用程序
    public static final int FILTER_ALL_APP = 0;
    // 系统程序
    public static final int FILTER_SYSTEM_APP = 1;
    // 第三方应用程序
    public static final int FILTER_THIRD_APP = 2;
    // 安装在SDCard的应用程序
    public static final int FILTER_SDCARD_APP = 3;
    /**
     *
     * 得到已安装的应用程序
     * @param context
     * @param filter         过滤应用的方式
     * @param packageName   查找指令包名的应用信息
     *
     * 有点慢啊，获取所有已安装的APP相关信息，现在只取当前APP的相关信息了
     *
     * @return
     */
    public static ArrayList<DataPackInfo> checkAllApp2(Context context, int filter, String packageName) {

        // 用的是getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES)
        // 因为有些apk可能被删掉 但是数据还在 所以用GET_UNINSTALLED_PACKAGES 这个flag
        PackageManager pm = context.getPackageManager();

        // 这些参数PackageManager.GET_ACTIVITIES PackageManager.GET_UNINSTALLED_PACKAGES
        // 并不是所有手机都支持，有的拿不到数据，有的直接报错

        //List<PackageInfo> packs = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES | PackageManager.GET_UNINSTALLED_PACKAGES);
        List<PackageInfo> packs = pm.getInstalledPackages(0);
        List<ApplicationInfo> listApplications = pm.getInstalledApplications(0);

        appInfoList.clear();
        pkgNameFlags.clear();
        for (int i = 0; i < packs.size(); i++) {
            //PackageInfo里面有 太多太多的（其他Info）了
            PackageInfo pi = packs.get(i);
            if(!StringUtils.isEmpty(packageName)){
                // 查找当前应用
                if(!pi.packageName.equalsIgnoreCase(packageName)){
                    continue;
                }
            }
            DataPackInfo newInfo = new DataPackInfo();

            newInfo.setAppName(pi.applicationInfo.loadLabel(pm).toString());
            newInfo.setDataDir(pi.applicationInfo.dataDir);
            newInfo.setSourceDir(pi.applicationInfo.sourceDir);
            newInfo.setProcessName(pi.applicationInfo.processName);
            newInfo.setNativeDir(pi.applicationInfo.nativeLibraryDir);

            newInfo.setPackageName(pi.packageName);
            newInfo.setVersionName(pi.versionName);
            newInfo.setVersionCode(pi.versionCode);

            newInfo.setIcon(pi.applicationInfo.loadIcon(pm));
            newInfo.setIcon(pm.getApplicationIcon(pi.applicationInfo));

            boolean canLaunch = false;
            switch (filter) {

                case FILTER_ALL_APP:
                    if ((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        newInfo.setAppFlag(FILTER_SYSTEM_APP);
                    }else{
                        newInfo.setAppFlag(FILTER_THIRD_APP);
                        canLaunch = true;
                    }
                    appInfoList.add(newInfo);
                    break;

                case FILTER_SYSTEM_APP:
                    newInfo.setAppFlag(FILTER_SYSTEM_APP);
                    if ((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        appInfoList.add(newInfo);
                    }
                    break;

                case FILTER_THIRD_APP:
                    canLaunch = true;
                    newInfo.setAppFlag(FILTER_THIRD_APP);
                    if ((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                        appInfoList.add(newInfo);
                    }
                    // 本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
                    else if ((pi.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){
                        appInfoList.add(newInfo);
                    }
                    break;

                case FILTER_SDCARD_APP:
                    canLaunch = true;
                    newInfo.setAppFlag(FILTER_SDCARD_APP);
                    if ((pi.applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                        appInfoList.add(newInfo);
                    }
                    break;

                default:
                    break;

            }
            pkgNameFlags.put(newInfo.getPackageName(), newInfo.getAppFlag());

            if(canLaunch){
            	boolean needFindLaunchInfo = true;
                if(null != pi.activities){
                	// ActivityInfo 同样道理 他是 Activity的信息
                	ActivityInfo ai = pi.activities[0];
	                if(null != ai){
		                Intent launchIntent = new Intent(Intent.ACTION_MAIN);
		                launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		                ComponentName cn = new ComponentName(newInfo.getPackageName(), ai.name);
		                launchIntent.setComponent(cn);
		                newInfo.setIntent(launchIntent);
                        needFindLaunchInfo = false;
	                }
                }

                if(needFindLaunchInfo){
	            	// 写这么多仅仅是为了查找启动类名
		            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		            resolveIntent.setPackage(newInfo.getPackageName());
		            List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
		            if(0 != apps.size()){
			            ResolveInfo ri = apps.iterator().next();
			            if (ri != null ) {
				            String packageNameTmp = ri.activityInfo.packageName;
				            String classNameTmp = ri.activityInfo.name;

				            // 为应用程序的启动Activity 准备Intent
				            Intent launchIntent = new Intent(Intent.ACTION_MAIN);
				            launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				            ComponentName cn = new ComponentName(packageNameTmp, classNameTmp);
				            launchIntent.setComponent(cn);
				            newInfo.setIntent(launchIntent);
			            }
		            }
                }
            }
        }
        return appInfoList;
    }


    /************************************【安卓系统版本】******************************************/
    /**
     * 获取设备信息
     * after android Jelly Bean【4.1.2---16---JELLY_BEAN】
     * we prefer to use MediaCodec instead of iomx
     * @return
     */
    public static String getSDKVersionInfo() {
        StringBuffer sb = new StringBuffer();
        String NEWLINE = "\n";

        sb.append("第一版[1]：" + Build.VERSION_CODES.BASE + NEWLINE);
        sb.append("1.1版[2]：" + Build.VERSION_CODES.BASE_1_1 + NEWLINE);
        sb.append("1.5版[3]：" + Build.VERSION_CODES.CUPCAKE + NEWLINE);
        sb.append("1.6版[4]：" + Build.VERSION_CODES.DONUT + NEWLINE);
        sb.append("2.0版[5]：" + Build.VERSION_CODES.ECLAIR + NEWLINE);
        sb.append("2.0.1[6]版：" + Build.VERSION_CODES.ECLAIR_0_1 + NEWLINE);
        sb.append("2.1版[7]：" + Build.VERSION_CODES.ECLAIR_MR1 + NEWLINE);
        sb.append("2.2版[8]：" + Build.VERSION_CODES.FROYO + NEWLINE);
        sb.append("2.3.1版[9]：" + Build.VERSION_CODES.GINGERBREAD + NEWLINE);
        sb.append("2.3.3版[10]：" + Build.VERSION_CODES.GINGERBREAD_MR1 + NEWLINE);
        sb.append("3.0版[11]：" + Build.VERSION_CODES.HONEYCOMB + NEWLINE);
        sb.append("3.1版[12]：" + Build.VERSION_CODES.HONEYCOMB_MR1 + NEWLINE);
        sb.append("3.2版[13]：" + Build.VERSION_CODES.HONEYCOMB_MR2 + NEWLINE);
        sb.append("4.0版[14]：" + Build.VERSION_CODES.ICE_CREAM_SANDWICH + NEWLINE);
        sb.append("4.0.3版[15]：" + Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 + NEWLINE);
        sb.append("4.1.2版[16]：" + Build.VERSION_CODES.JELLY_BEAN + NEWLINE);
        sb.append("4.2.2版[17]：" + Build.VERSION_CODES.JELLY_BEAN_MR1 + NEWLINE);
        sb.append("4.3.1版[18]：" + Build.VERSION_CODES.JELLY_BEAN_MR2 + NEWLINE);
        sb.append("4.4.2版[19]：" + Build.VERSION_CODES.KITKAT + NEWLINE);
        sb.append("4.4W.2版[20]：" + Build.VERSION_CODES.KITKAT_WATCH + NEWLINE);
        sb.append("5.0.1版[21]：" + Build.VERSION_CODES.LOLLIPOP + NEWLINE);
        sb.append("5.1.1版[22]：" + Build.VERSION_CODES.LOLLIPOP_MR1 + NEWLINE);
        sb.append("6.0版[23]：" + Build.VERSION_CODES.M + NEWLINE); // Marshmallow
        sb.append("7.0版[24]：" + Build.VERSION_CODES.N + NEWLINE); // Nougat
        sb.append("7.1.1版[25]：" + Build.VERSION_CODES.N_MR1 + NEWLINE); // Nougat++
        sb.append("此版官方未发布：" + Build.VERSION_CODES.CUR_DEVELOPMENT + NEWLINE);

        return sb.toString();
    }
}
