package com.zccl.ruiqianqi.tools.db;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;

import com.zccl.ruiqianqi.tools.ShareUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by ruiqianqi on 2016/9/29 0029.
 */

public class DbSettings {

    private static String TAG = DbSettings.class.getSimpleName();

    /**
     * Generate a new OpenUDID
     * UDID是Unique Device Identifier的缩写,中文意思是设备唯一标识.
     * 在很多需要限制一台设备一个账号的应用中经常会用到,
     * 在Symbian时代,我们是使用IMEI作为设备的唯一标识的,可惜的是Apple官方不允许开发者获得设备的IMEI.
     *
     * 大多数应用都会用到苹果设备的UDID号，UDID通常有以下两种用途：
     * 1）用于一些统计与分析目的；【第三方统计工具如友盟，广告商如ADMOB等】
     * 2）将UDID作为用户ID来唯一识别用户，省去用户名，密码等注册过程。
     *
     * 替代方案1：
     * 苹果公司建议使用UUID【一种开放的软件构建标准】
     * 该方法每次都会获取一个唯一的标识字符串，开发者可以在应用第一次启动时候调用一次，
     * 然后将该串存储起来，以便以后替代UDID来使用。
     * 问题是如果用户删除该应用再次安装时，又会生成新的字符串，所以不能保证唯一识别该设备。
     *
     * 替代方案2：
     * 现在网上有一现成的解决方案,使用设备的Mac地址,因为Mac地址也是唯一的.unix有系统调用可以获取Mac地址.但有些事情需要注意:
     * 1）iPhone可能有多个Mac地址,wifi的地址,以及SIM卡的地址.一般来讲,我们取en0的地址,因为他是iPhone的wifi的地址,是肯定存在的.
     * 2）Mac地址涉及到隐私,不应该胡乱将用户的Mac地址传播!所以我们需要将Mac地址进行hash之后,才能作为DeviceId上传.
     *
     * 数据库名字是settings.db 创建了两个表 system, secure
     */
    public static String generateOpenUDID(Context context) {

        //847b2ad23d6ca762一个例子
        String OpenUDID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        //IMEI长度为15，这个长度应该为16，就用15作限制吧，万一读到IMEI了呢，是吧
        if(StringUtils.isEmpty(OpenUDID) || OpenUDID.equals("9774d56d682e549c") || OpenUDID.length()<15 ) {
            //if ANDROID_ID is null,
            //or it's equals to the GalaxyTab generic ANDROID_ID
            //or bad,
            //generates a new one
            SecureRandom random = new SecureRandom();

            //ed27ef13614cfc10一个例子
            OpenUDID = new BigInteger(64, random).toString(16);
        }
        return OpenUDID;
    }

    /**
     * 是否允许系统灭屏
     *
     *   方法一、调整代码：
         Settings.System.putInt(getContentResolver(),android.provider.Settings.System.SCREEN_OFF_TIMEOUT,-1);
         权限：<uses-permission android:name="android.permission.WRITE_SETTINGS" />

         方法二、调整数据库：
         android的这些设置都是存放在sql数据库里的，也就是说可以直接通过修改数据库来不让android睡眠。
         sqlite3 /data/data/com.android.providers.settings/databases/settings.db
         具体sql：
         UPDATE system SET value = '-1' WHERE name = 'screen_off_timeout';
     * @param context
     * @param isEnable 是否允许灭屏
     */
    public static void setScreenOffTimeOut(Context context, boolean isEnable) {
        SharedPreferences preferences = ShareUtils.getP(context);
        SharedPreferences.Editor editor = ShareUtils.getE(context);
        // 单位：milliseconds
        String key = Settings.System.SCREEN_OFF_TIMEOUT;
        if (isEnable) {
            try {
                // 读取当前灭屏时间
                int timeout = Settings.System.getInt(context.getContentResolver(), key);
                // 保存的时间
                int saveTime= preferences.getInt("timeout", 600000);
                // 如果当前为不灭屏
                if (timeout == -1) {
                    timeout = saveTime;
                } else {
                    if (timeout != saveTime && timeout > 0) {
                        // 时间不一致，可能用户设置过，重新保存当前值
                        editor.putInt("timeout", timeout);
                        editor.apply();
                    }
                    return;
                }
                // 设置灭屏时间
                Settings.System.putInt(context.getContentResolver(), key, timeout);
            } catch (Exception e) {

            }
        } else {
            // 不灭屏
            try {
                // 保存先前的值
                int timeout = Settings.System.getInt(context.getContentResolver(), key);
                if (timeout < 0) {
                    return;
                }
                editor.putInt("timeout", timeout);
                editor.apply();
                // 禁止灭屏
                Settings.System.putInt(context.getContentResolver(), key, -1);
            } catch (Exception e) {

            }
        }
    }

    /**
     * 设置是否开启重力感应
     * @param context
     * @param isEnable
     */
    public static void setAccelerometer(Context context, boolean isEnable){
        String key =  Settings.System.ACCELEROMETER_ROTATION;
        // 0为关闭 1为开启
        int gSensorStatus = Settings.System.getInt(context.getContentResolver(), key, 0);
        if(gSensorStatus==0){

        }else {

        }
        if(isEnable){
            Settings.System.putInt(context.getContentResolver(), key, 1);
        }else {
            Settings.System.putInt(context.getContentResolver(), key, 0);
        }

    }

    /**
     * 打开飞行模式
     * @param context
     */
    public static void openAirPlane(Context context){
        boolean isEnabled = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;
        if(!isEnabled) {
            Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 1);
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.putExtra("state", !isEnabled);
            context.sendBroadcast(intent);
        }
    }

    /**
     * 关闭飞行模式
     * @param context
     */
    public static void closeAirPlane(Context context){
        boolean isEnabled = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;
        if(isEnabled) {
            Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.putExtra("state", !isEnabled);
            context.sendBroadcast(intent);
        }
    }

}
