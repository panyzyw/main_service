package com.yongyida.robot.voice.frame.newflytek;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.yongyida.robot.voice.utils.LogUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by ruiqianqi on 2016/7/20 0020.
 */
public class MyAppUtils {

    /***
     * Android L (lollipop, API 21) introduced a new problem when trying to invoke implicit intent,
     * "java.lang.IllegalArgumentException: Service Intent must be explicit"
     *
     * If you are using an implicit intent, and know only 1 target would answer this intent,
     * This method will help you turn the implicit intent into the explicit form.
     *
     * Inspired from SO answer: http://stackoverflow.com/a/26318757/1446466
     * @param context
     * @param implicitIntent - The original implicit intent
     * @return Explicit Intent created from the implicit original intent
     */
    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);
        return explicitIntent;
    }

    /**
     * IllegalArgumentException: Service Intent must be explicit
     * 经过查找相关资料，发现是因为Android5.0中service的intent一定要显性声明，当这样绑定的时候不会报错。
     */
    public static Intent createExplicitFromImplicitIntent(Context context, String action){
        Intent intent = new Intent();
        intent.setAction(action);
        intent.setPackage(context.getPackageName());
        return intent;
    }

    /**
     * 已知包名和启动类
     *
     * @param context
     * @param packageName
     * @param className
     */
    public static void startApp(Context context, String packageName, String className) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        //intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName(packageName, className);
        intent.setComponent(cn);
        if (context instanceof Activity) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**
     * 已知包名和启动类
     *
     * @param context
     * @param packageName
     * @param className
     * @param args
     */
    public static void startApp(Context context, String packageName, String className, Bundle args) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        //intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName(packageName, className);
        intent.setComponent(cn);
        if (context instanceof Activity) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtras(args);
        context.startActivity(intent);
    }

    /**
     * 只知包名，不知类名的启动
     * @param context
     * @param packageName
     */
    public static void launchApp(Context context, String packageName){
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 只知包名不知类名
     * @param context
     * @param packageName
     */
    public static void openApp(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(pi.packageName);

        // 返回符合条件的ACTIVITY集合
        List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
        // 只要符合条件就启动，太多了就不知道启动哪个了
        ResolveInfo ri = apps.iterator().next();
        if (ri != null) {
            String packageNameTemp = ri.activityInfo.packageName;
            String classNameTemp = ri.activityInfo.name;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            if (context instanceof Activity) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            } else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            ComponentName cn = new ComponentName(packageNameTemp, classNameTemp);
            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }

    /**
     * 只知包名不知类名
     * @param context
     * @param packageName
     */
    public static Intent openAppIntent(Context context, String packageName, Intent resolveIntent) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(resolveIntent==null) {
            resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resolveIntent.setPackage(pi.packageName);
        }

        // 返回符合条件的ACTIVITY集合
        List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
        // 只要符合条件就启动，太多了就不知道启动哪个了
        ResolveInfo ri = apps.iterator().next();
        if (ri != null) {
            String packageNameTemp = ri.activityInfo.packageName;
            String classNameTemp = ri.activityInfo.name;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            if (context instanceof Activity) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            } else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            ComponentName cn = new ComponentName(packageNameTemp, classNameTemp);
            intent.setComponent(cn);
            return intent;
        }
        return null;
    }

    /**
     * 通过广播启动一个应用
     * @param context 本应用上下文
     * @param action  第三方应用监听的Action
     * @param bundle  携带的参数
     *
     * Android 3.1开始，系统向所有Intent的广播添加了FLAG_EXCLUDE_STOPPED_PACKAGES标志
     *
     * FLAG_INCLUDE_STOPPED_PACKAGES：表示包含未启动的App
     * FLAG_EXCLUDE_STOPPED_PACKAGES：表示不包含未启动的App（指的是安装后从来没有启动过和被用户手动强制停止的应用）
     * 系统的广播都含有这个标志FLAG_EXCLUDE_STOPPED_PACKAGES
     *
     * 这样做是为了防止广播无意或不必要地开启未启动App的后台服务。如果要强制调起未启动的App，
     * 后台服务或应用程序可以通过向广播Intent添加FLAG_INCLUDE_STOPPED_PACKAGES标志来唤醒
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public static void sendBroadcast(Context context, String action, Bundle bundle){
        Intent intent = new Intent(action);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //这句话的功能是：让没有启动过的，或者被用户强制停止的应用也能被启动
        //当然前提是第三方应用监听了这个Action
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        }
        if(bundle != null){
            intent.putExtras(bundle);
        }
        context.sendBroadcast(intent);
    }

    /**
     * 打开电池耗电信息
     * @param context
     */
    public static void startBatteryUsage(Context context){
        Intent powerUsageIntent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
        powerUsageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(powerUsageIntent, 0);
        // check that the Battery app exists on this device
        if(resolveInfo != null){
            context.startActivity(powerUsageIntent);
        }
    }

    /**
     * 打开电池状态信息
     * @param context
     */
    public static void startBatteryStatus(Context context){
        Intent intent = new Intent();
        ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.BatteryInfo");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 打开设置界面
     * 经过测试，使用下面字段可以在软件中直接打开相应的系统界面
     　　com.android.settings.AccessibilitySettings 辅助功能设置
     　　com.android.settings.ActivityPicker 选择活动
     　　com.android.settings.ApnSettings APN设置
     　　com.android.settings.ApplicationSettings 应用程序设置
     　　com.android.settings.BandMode 设置GSM/UMTS波段
     　　com.android.settings.BatteryInfo 电池信息
     　　com.android.settings.DateTimeSettings 日期和坝上旅游网时间设置
     　　com.android.settings.DateTimeSettingsSetupWizard 日期和时间设置
     　　com.android.settings.DevelopmentSettings 应用程序设置=》开发设置
     　　com.android.settings.DeviceAdminSettings 设备管理器
     　　com.android.settings.DeviceInfoSettings 关于手机
     　　com.android.settings.Display 显示——设置显示字体大小及预览
     　　com.android.settings.DisplaySettings 显示设置
     　　com.android.settings.DockSettings 底座设置
     　　com.android.settings.IccLockSettings SIM卡锁定设置
     　　com.android.settings.InstalledAppDetails 语言和键盘设置
     　　com.android.settings.LanguageSettings 语言和键盘设置
     　　com.android.settings.LocalePicker 选择手机语言
     　　com.android.settings.LocalePickerInSetupWizard 选择手机语言
     　　com.android.settings.ManageApplications 已下载（安装）软件列表
     　　com.android.settings.MasterClear 恢复出厂设置
     　　com.android.settings.MediaFormat 格式化手机闪存
     　　com.android.settings.PhysicalKeyboardSettings 设置键盘
     　　com.android.settings.PrivacySettings 隐私设置
     　　com.android.settings.ProxySelector 代理设置
     　　com.android.settings.RadioInfo 手机信息
     　　com.android.settings.RunningServices 正在运行的程序（服务）
     　　com.android.settings.SecuritySettings 位置和安全设置
     　　com.android.settings.Settings 系统设置
     　　com.android.settings.SettingsSafetyLegalActivity 安全信息
     　　com.android.settings.SoundSettings 声音设置
     　　com.android.settings.TestingSettings 测试——显示手机信息、电池信息、使用情况统计、Wifi information、服务信息
     　　com.android.settings.TetherSettings 绑定与便携式热点
     　　com.android.settings.TextToSpeechSettings 文字转语音设置
     　　com.android.settings.UsageStats 使用情况统计
     　　com.android.settings.UserDictionarySettings 用户词典
     　　com.android.settings.VoiceInputOutputSettings 语音输入与输出设置
     　　com.android.settings.WirelessSettings 无线和网络设置
     * @param context
     */
    public static void openSetting(Context context){
        //判断手机系统的版本  即API大于10 就是3.0或以上版本
        if(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1){
            Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else{
            Intent intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings", "com.android.settings.Settings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
            context.startActivity(intent);
        }
    }

    /**
     * 打开无线和网络设置界面
     * @param context
     */
    public static void openNetSetting(Context context){
        //判断手机系统的版本  即API大于10 就是3.0或以上版本
        if(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1){
            Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else{
            Intent intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
            context.startActivity(intent);
        }
    }

    /**
     * 打开wifi设置界面
     * @param context
     */
    public static void openWifiSetting(Context context){
        //判断手机系统的版本  即API大于10 就是3.0或以上版本
        if(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1){
            Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else{
            Intent intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WifiSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
            context.startActivity(intent);
        }
    }

    /**
     * 打开蓝牙设置界面
     * @param context
     */
    public static void openBluetoothSetting(Context context){
        //判断手机系统的版本  即API大于10 就是3.0或以上版本
        if(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1){
            Intent intent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else{
            Intent intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings", "com.android.settings.BluetoothSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
            context.startActivity(intent);
        }
    }

    /**
     * 打开关于界面
     * @param context
     */
    public static void openAboutSetting(Context context){
        Intent intent = new Intent(android.provider.Settings.ACTION_DEVICE_INFO_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 打开拨打电话界面
     * @param context
     */
    public static void openCall(Context context){
        Intent intent = new Intent();
        //系统默认的action，用来打开默认的电话界面
        intent.setAction(Intent.ACTION_CALL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //需要拨打的号码
        //intent.setData(Uri.parse("tel:"+telephone));
        context.startActivity(intent);
    }

    /**
     * 打开发送短信界面
     * @param context
     */
    public static void openSMS(Context context){
        Intent intent = new Intent();
        //系统默认的action，用来打开默认的电话界面
        intent.setAction(Intent.ACTION_SENDTO);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //需要发短息的号码
        //intent.setData(Uri.parse("smsto:"+telephone));
        context.startActivity(intent);
    }


    /**
     * 判断网络连接是否打开
     * @param context
     * @return
     */
    public static boolean isNetConnected(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(conMgr!=null){
            NetworkInfo network = conMgr.getActiveNetworkInfo();

            NetworkInfo wifiNetWorkInfo = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobileNetWorkInfo = conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (network==null || (network != null && !network.isAvailable())) {// 未开启网络
                // NetworkInfo 为空或者不可以用的时候正常情况应该是当前没有可用网络，
                // 但是有些电信机器，仍可以正常联网，
                // 所以当成net网络处理依然尝试连接网络。
                TelephonyManager telMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                if(telMgr != null){
                    telMgr.getDataActivity();
                    int state = telMgr.getDataState();
                    return state == TelephonyManager.DATA_CONNECTED;
                }
                return false;
            }else{
                return network.isConnected();
            }
        }
        return false;
    }

    /**
     * 打开一些设置
     * @param context
     * @param action
     */
    public static void openToolsUI(Context context, String action){
        Intent intent = new Intent();
        ComponentName component = new ComponentName("com.android.settings", action);
        intent.setComponent(component);
        intent.setAction("android.intent.action.VIEW");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 打开一些设置
     * @param context
     * @param action
     */
    public static void openToolsUI2(Context context, String action){
        Intent intent = new Intent(action);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
