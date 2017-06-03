package com.zccl.ruiqianqi.tools;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by ruiqianqi on 2016/7/20 0020.
 * FLAG_ACTIVITY_SINGLE_TOP:  这个FLAG就相当于加载模式中的singleTop，比如说原来栈中情况是A,B,C,D在D中启动D，栈中的情况还是A,B,C,D
 * FLAG_ACTIVITY_CLEAR_TOP:   这个FLAG就相当于加载模式中的SingleTask，这种FLAG启动的Activity会把要启动的Activity之上的Activity全部弹出栈空间。
 *                            类如：原来栈中的情况是A,B,C,D这个时候从D中跳转到B，这个时候栈中的情况就是A,B了
 * FLAG_ACTIVITY_REORDER_TO_FRONT:  当接收Intent的Activity处于非destroy状态，那么接收Intent的Activity就会被置于栈顶。
 *                                  使用该FLAG的建议： A、产生的activity为根activity。 B、确保activity没有被finish，或者确保activity已经destroy。
 * FLAG_ACTIVITY_NO_HISTORY:  用这个FLAG启动的Activity，一旦退出，他就不会存在于栈中，比方说！原来是A,B,C 这个时候再C中以这个FLAG启动D的，
 *                            D再启动E，这个时候栈中情况为A,B,C,E。
 * FLAG_ACTIVITY_REORDER_TO_FRONT:      这个标志将引发已经运行的Activity移动到历史stack的顶端。
 * FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS:  如果设置，新的Activity不会在最近启动的Activity的列表中保存。
 */
public class MyAppUtils {

    // 类标志
    private static String TAG = MyAppUtils.class.getSimpleName();

    /************************************【创建Intent】********************************************/
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

    /*************************************【启动APK】**********************************************/
    /**
     * 已知包名和启动类，打开只能用指定dataUri启动的activity
     * @param context
     * @param packageName
     * @param className
     * @param data ------ 特定的uri 如: "com.android.launch://LaunchActivity"
     */
    public static void startMyApp(Context context, String packageName, String className, String data) {
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(packageName, className);
        intent.setComponent(cn);
        if(!StringUtils.isEmpty(data)) {
            intent.setDataAndType(Uri.parse(data), null);
        }
        if (context instanceof Activity) {

        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(context, intent);
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
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(context, intent);
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
        if(null != args) {
            intent.putExtras(args);
        }
        startActivity(context, intent);
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
        if(null == pi)
            return;
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(pi.packageName);

        // check that the app exists on this device
        pm.resolveActivity(resolveIntent, 0);

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
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            } else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            ComponentName cn = new ComponentName(packageNameTemp, classNameTemp);
            intent.setComponent(cn);
            startActivity(context, intent);
        }
    }

    /**
     * 只知包名，不知类名的启动
     * 下载 com.android.providers.downloads.ui
     * 信息 com.android.mms
     * 录音机  com.android.soundrecorder
     * 文件管理 com.yongyida.robot.resourcemanager
     * 日历 com.android.calendar
     * 时钟 com.android.deskclock
     * 浏览器 com.android.browser
     * 通讯录 com.android.contacts
     *
     * 影视 com.yongyida.robot.videotutarial
     * 文艺馆 com.yongyida.robot.artmuseum
     * 遥控器 com.yongyida.robot.irremote
     *
     * @param context
     * @param packageName
     */
    public static void launchApp(Context context, String packageName){

        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(context, intent);

    }

    /**************************************【发送广播】********************************************/
    /**
     * 通过广播启动一个应用
     * @param context 应用上下文
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
        // 这句话的功能是：让没有启动过的，或者被用户强制停止的应用也能被启动
        // 当然前提是第三方应用监听了这个Action
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        }
        if(bundle != null){
            intent.putExtras(bundle);
        }
        context.sendBroadcast(intent);
    }

    /**
     * 发送广播
     * @param context
     * @param map
     */
    public static void sendBroadcast(Context context, Map<String, String> map){
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if(entry.getKey().equals("action")){
                    intent.setAction(entry.getValue());
                    continue;
                }
                intent.putExtra(entry.getKey(), entry.getValue());
            }
            context.sendBroadcast(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送一个有序广播，有序广播是可以被终止的
     * @param context  应用上下文
     * @param action   第三方应用监听的Action
     * @param bundle   携带的参数
     * @param receiverPermission  安全权限
     */
    public static void sendOrderedBroadcast(Context context, String action, Bundle bundle, String receiverPermission){
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
        context.sendOrderedBroadcast(intent, receiverPermission);
    }

    /************************************【判断应用是否安装】**************************************/
    /**
     * 遍历程序列表，判断是否安装安全支付服务
     * 1.判断支付宝
     * 2.判断银联
     * 3.判断财付通
     * 4.判断360
     * 5.判断腾讯手机管家
     *
     * @return
     */
    public static boolean checkAppExist(Context context, int flag) {
        PackageManager manager = context.getPackageManager();
        List<PackageInfo> pkgList = manager.getInstalledPackages(0);
        for (int i = 0; i < pkgList.size(); i++) {
            PackageInfo pi = pkgList.get(i);
            if (flag == 1) {
                if ("com.alipay.android.app".equalsIgnoreCase(pi.packageName)) {
                    return true;
                }
            } else if (flag == 2) {
                if ("com.unionpay.uppay".equalsIgnoreCase(pi.packageName)) {
                    return true;
                }
            } else if (flag == 3) {
                if ("com.tenpay.android.service".equalsIgnoreCase(pi.packageName)) {
                    return true;
                }
            } else if (flag == 4) {
                if ("com.qihoo360.mobilesafe".equalsIgnoreCase(pi.packageName)) {
                    return true;
                }
            } else if (flag == 5) {
                if ("com.tencent.qqpimsecure".equalsIgnoreCase(pi.packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断指定包名的应用是否存在
     * @param context
     * @param packageName
     * @return
     */
    public static boolean checkAppExist2(Context context, String packageName) {
        if (StringUtils.isEmpty(packageName))
            return false;
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
            PackageInfo pkgInfo = pm.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 检查是否安装的对应的包名的应用
     * @param context
     * @param packageName
     */
    public static boolean checkAppExist3(Context context, String packageName) {
        if (StringUtils.isEmpty(packageName))
            return false;
        PackageManager pm = context.getPackageManager();

        // 过滤条件,查找启动类
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN,null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 返回符合条件的ACTIVITY集合 PackageManager.MATCH_DEFAULT_ONLY
        List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);

        // 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(pm));

        for (ResolveInfo reInfo : apps) {
            // 获得应用程序的包名
            String pkgNameTmp = reInfo.activityInfo.packageName;
            if(packageName.equalsIgnoreCase(pkgNameTmp)){
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否安装的对应的包名的应用
     * @param context
     * @param packageName
     * @return
     */
    public boolean checkAppExist4(Context context, String packageName) {
        if (StringUtils.isEmpty(packageName))
            return false;
        PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pkgInfoS = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pkgInfoS.size(); i++) {
            if (packageName.equalsIgnoreCase(pkgInfoS.get(i).packageName)){
                return true;
            }
        }
        return false;
    }

    /**
     * 应用是从哪个市场装的
     * @param context
     * @return
     */
    public static String getPackageInstaller(Context context) {
        String store = context.getPackageManager().getInstallerPackageName(context.getPackageName());
        return store;
    }

    /**************************************【打开各类应用】****************************************/
    /**
     * 打开耗电信息
     * @param context
     */
    public static void startBatteryUsage(Context context){
        Intent powerUsageIntent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
        powerUsageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(powerUsageIntent, 0);
        // check that the Battery app exists on this device
        if(resolveInfo != null){
            startActivity(context, powerUsageIntent);
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
        startActivity(context, intent);
    }

    /**
     * 经过测试，使用下面字段可以在软件中直接打开相应的系统界面
 　　com.android.settings.AccessibilitySettings       辅助功能设置
 　　com.android.settings.ActivityPicker              选择活动
 　　com.android.settings.ApnSettings                 APN设置
 　　com.android.settings.ApplicationSettings         应用程序设置
 　　com.android.settings.BandMode                    设置GSM/UMTS波段
 　　com.android.settings.BatteryInfo                 电池信息
 　　com.android.settings.DateTimeSettings            日期和时间设置
 　　com.android.settings.DateTimeSettingsSetupWizard 日期和时间设置
 　　com.android.settings.DevelopmentSettings         应用程序设置=》开发设置
 　　com.android.settings.DeviceAdminSettings         设备管理器
 　　com.android.settings.DeviceInfoSettings          关于手机
 　　com.android.settings.Display                     显示——设置显示字体大小及预览
 　　com.android.settings.DisplaySettings             显示设置
 　　com.android.settings.DockSettings                底座设置
 　　com.android.settings.IccLockSettings             SIM卡锁定设置
 　　com.android.settings.InstalledAppDetails         语言和键盘设置
 　　com.android.settings.LanguageSettings            语言和键盘设置
 　　com.android.settings.LocalePicker                选择手机语言
 　　com.android.settings.LocalePickerInSetupWizard   选择手机语言
 　　com.android.settings.ManageApplications          已下载（安装）软件列表
 　　com.android.settings.MasterClear                 恢复出厂设置
 　　com.android.settings.MediaFormat                 格式化手机闪存
 　　com.android.settings.PhysicalKeyboardSettings    设置键盘
 　　com.android.settings.PrivacySettings             隐私设置
 　　com.android.settings.ProxySelector               代理设置
 　　com.android.settings.RadioInfo                   手机信息
 　　com.android.settings.RunningServices             正在运行的程序（服务）
 　　com.android.settings.SecuritySettings            位置和安全设置
 　　com.android.settings.Settings                    系统设置
 　　com.android.settings.SettingsSafetyLegalActivity 安全信息
 　　com.android.settings.SoundSettings               声音设置
 　　com.android.settings.TestingSettings             测试——显示手机信息、电池信息、使用情况统计、Wifi information、服务信息
 　　com.android.settings.TetherSettings              绑定与便携式热点、网络热点
 　　com.android.settings.TextToSpeechSettings        文字转语音设置
 　　com.android.settings.UsageStats                  使用情况统计
 　　com.android.settings.UserDictionarySettings      用户词典
 　　com.android.settings.VoiceInputOutputSettings    语音输入与输出设置
 　　com.android.settings.WirelessSettings            无线和网络设置

     android.settings.SETTINGS                  系统设置
     android.settings.LOCATION_SOURCE_SETTINGS  位置信息
     android.settings.USER_SETTINGS             用户
     android.settings.WIRELESS_SETTINGS         无线和网络
     android.settings.NFC_SETTINGS              无线和网络
     android.settings.ACCESSIBILITY_SETTINGS    无障碍
     android.settings.PRIVACY_SETTINGS          备份和重置
     android.settings.WIFI_SETTINGS             WLAN
     android.settings.BLUETOOTH_SETTINGS        蓝牙
     android.settings.DATE_SETTINGS             日期和时间
     android.settings.SOUND_SETTINGS            提示音和通知
     android.settings.NOTIFICATION_SETTINGS     提示音和通知
     android.settings.DISPLAY_SETTINGS          显示
     android.settings.LOCALE_SETTINGS           语言
     android.settings.APPLICATION_SETTINGS                应用
     android.settings.MANAGE_APPLICATIONS_SETTINGS        已安装的应用
     android.settings.MANAGE_ALL_APPLICATIONS_SETTINGS    所有的应用
     android.settings.APPLICATION_DEVELOPMENT_SETTINGS    开发者选项
     android.settings.DATA_ROAMING_SETTINGS               移动网络设置
     android.settings.INTERNAL_STORAGE_SETTINGS           内部存储设置
     android.settings.MEMORY_CARD_SETTINGS                记忆卡存储设置
     android.settings.DEVICE_INFO_SETTINGS                机器人状态
     android.settings.AIRPLANE_MODE_SETTINGS              飞行模式
     *
     * @param action
     * @param context
     */
    public static void openToolsUI(Context context, String action){
        Intent intent = new Intent();
        ComponentName component = new ComponentName("com.android.settings", action);
        intent.setComponent(component);
        intent.setAction("android.intent.action.VIEW");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(context, intent);
    }

    /**
     * 打开一些设置
     * 应用列表 Settings.ACTION_APPLICATION_SETTINGS
     * 存储状况 Settings.ACTION_INTERNAL_STORAGE_SETTINGS
     * 耗电信息 Intent.ACTION_POWER_USAGE_SUMMARY
     * 出厂设置 Settings.ACTION_PRIVACY_SETTINGS
     * 日期     Settings.ACTION_DATE_SETTINGS
     * 语言     Settings.ACTION_LOCALE_SETTINGS
     * 位置信息 Settings.ACTION_LOCATION_SOURCE_SETTINGS
     * 提示音   Settings.ACTION_SOUND_SETTINGS
     * 显示     Settings.ACTION_DISPLAY_SETTINGS
     * 飞行模式 Settings.ACTION_AIRPLANE_MODE_SETTINGS
     *
     * Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
     * 权限设置 Settings.ACTION_USAGE_ACCESS_SETTINGS
     *
     * @param context
     * @param action
     */
    public static void openToolsUI2(Context context, String action){
        Intent intent = new Intent(action);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(context, intent);
    }

    /**
     * 打开设置界面
     * @param context
     */
    public static void openSetting(Context context){
        //判断手机系统的版本  即API大于10 就是3.0或以上版本
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1){
            Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(context, intent);
        }else{
            Intent intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings", "com.android.settings.Settings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(context, intent);
        }
    }

    /**
     * 打开无线和网络设置界面
     * @param context
     */
    public static void openNetSetting(Context context){
        // 判断手机系统的版本  即API大于10 就是3.0或以上版本
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1){
            Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(context, intent);
        }else{
            Intent intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(context, intent);
        }
    }

    /**
     * 打开wifi设置界面
     * @param context
     */
    public static void openWifiSetting(Context context){
        //判断手机系统的版本  即API大于10 就是3.0或以上版本
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1){
            Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(context, intent);
        }else{
            Intent intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WifiSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(context, intent);
        }
    }

    /**
     * 打开蓝牙设置界面
     * @param context
     */
    public static void openBluetoothSetting(Context context){
        //判断手机系统的版本  即API大于10 就是3.0或以上版本
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1){
            Intent intent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(context, intent);
        }else{
            Intent intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings", "com.android.settings.BluetoothSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(context, intent);
        }
    }

    /**
     * 打开关于界面
     * @param context
     */
    public static void openAboutSetting(Context context){
        Intent intent = new Intent(android.provider.Settings.ACTION_DEVICE_INFO_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(context, intent);
    }

    /**
     * 打开拨打电话界面
     * @param context
     */
    public static void openCall(Context context){
        Intent intent = new Intent();
        // 系统默认的action，用来打开默认的电话界面
        intent.setAction(Intent.ACTION_CALL);
        // 需要拨打的号码
        //intent.setData(Uri.parse("tel:" + telephone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(context, intent);
    }

    /**
     * 打开拨打电话界面
     * @param context
     */
    public static void openDial(Context context){
        Intent intent = new Intent();
        // 系统默认的action，用来打开默认的电话界面
        intent.setAction(Intent.ACTION_DIAL);
        // 需要拨打的号码
        //intent.setData(Uri.parse("tel:" + telephone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(context, intent);

        //Intent intent= new Intent("android.intent.action.DIAL");
        //intent.setClassName("com.android.contacts", "com.android.contacts.DialtactsActivity");
    }

    /**
     * 打开通话记录界面
     * @param context
     */
    public static void openDialRecord(Context context){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL_BUTTON);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(context, intent);
    }

    /**
     * 打开发送短信界面
     * @param context
     */
    public static void openSendSMS(Context context){
        Intent intent = new Intent();
        // 系统默认的action，用来打开默认的电话界面
        intent.setAction(Intent.ACTION_SENDTO);
        // 需要发短息的号码
        //intent.setData(Uri.parse("smsto:"+telephone));
        // 短信内容
        //intent.putExtra("sms_body", "abcdefgh");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(context, intent);
    }

    /**
     * 打开读取短信界面
     * @param context
     */
    public static void openReadSMS(Context context){
        MyAppUtils.openApp(context, "com.android.mms");
        MyAppUtils.startApp(context, "com.android.mms", "com.android.mms.ui.ConversationList");
    }

    /**
     * 打开应用界面，捕获异常
     * @param context
     * @param intent
     */
    private static void startActivity(Context context, Intent intent){
        try {
            context.startActivity(intent);
        }catch (Exception e){

        }
    }

    /***********************************【工厂与开发者模式】***************************************/
    /**
     * 进入工厂模式
     * @param context
     */
    public static void factoryTestMode(Context context) {
        Uri engineerUri = Uri.parse("android_secret_code://4628");
        Intent intent = new Intent("android.provider.Telephony.SECRET_CODE");
        intent.setData(engineerUri);
        context.sendBroadcast(intent);
    }

    /**
     * 进入开发者模式
     * @param context
     */
    public static void developerMode(Context context) {
        Uri engineerUri = Uri.parse("android_secret_code://2846000");
        Intent intent = new Intent("android.provider.Telephony.SECRET_CODE");
        intent.setData(engineerUri);
        context.sendBroadcast(intent);
    }

    /**
     * 返回桌面
     * @param context
     */
    public static void returnHome(Context context){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(context, intent);

        sendBroadcast(context, "com.yydrobot.HOME", null);
    }

    /**
     * 最近应用
     */
    public static void recentApp(){
        try {
            Instrumentation ins = new Instrumentation();
            ins.sendKeyDownUpSync(KeyEvent.KEYCODE_APP_SWITCH);
        } catch (Exception e) {
            LogUtils.e(TAG, "recentApp", e);
        }
    }

    /**
     * 返回按键
     */
    public static void backOperation(){
        try {
            Instrumentation ins = new Instrumentation();
            ins.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
        } catch (Exception e) {
            LogUtils.e(TAG, "backOperation", e);
        }
    }

}
