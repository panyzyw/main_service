package com.zccl.ruiqianqi.utils;

import android.content.Context;

/**
 * Created by ruiqianqi on 2017/5/23 0023.
 */

public class LedUtilsBak {

    /**
     * 开始监听闪绿灯
     * @param context
     */
    public static void startMonitorLed(Context context){
        AppUtils.sendLedBroad(context, true, "monitor", 2, 2, AppUtils.PRIORITY_7);
    }

    /**
     * 结束监听
     * @param context
     */
    public static void endMonitorLed(Context context){
        AppUtils.sendLedBroad(context, false, "monitor", 2, 2, AppUtils.PRIORITY_7);
    }

    /**
     * 开始说话闪蓝灯
     * @param context
     */
    public static void startSpeakLed(Context context){
        AppUtils.sendLedBroad(context, true , "speak", 3, 2, AppUtils.PRIORITY_6);
    }

    /**
     * 结束说话不闪灯
     * @param context
     */
    public static void endSpeakLed(Context context){
        AppUtils.sendLedBroad(context, false, "speak", 3, 2, AppUtils.PRIORITY_6);
    }

    /*************************************【低电量】***********************************************/
    /**
     * 开启、低电量、非充电
     * @param context
     */
    public static void startLowPowerLed(Context context){
        AppUtils.sendLedBroad(context, true, "lowPower", 1, 2, AppUtils.PRIORITY_2);
    }

    /**
     * 结束、低电量、非充电
     * @param context
     */
    public static void endLowPowerLed(Context context){
        AppUtils.sendLedBroad(context, false, "lowPower", 1, 2, AppUtils.PRIORITY_2);
    }

    /**
     * 开启、低电量、充电
     * @param context
     */
    public static void startLowPowerChargeLed(Context context){
        AppUtils.sendLedBroad(context, true, "lowPower", 1, 2, AppUtils.PRIORITY_2);
    }

    /**
     * 结束、低电量、充电
     * @param context
     */
    public static void endLowPowerChargeLed(Context context){
        AppUtils.sendLedBroad(context, false, "lowPower", 1, 2, AppUtils.PRIORITY_2);
    }

    /*************************************【正常电量】*********************************************/
    /**
     * 开启、正常电量、非充电
     * @param context
     */
    public static void startNormalPowerLed(Context context){
        AppUtils.sendLedBroad(context, false, "normalPower", 2, 2, AppUtils.PRIORITY_2);
    }

    /**
     * 结束、正常电量、非充电
     * @param context
     */
    public static void endNormalPowerLed(Context context){
        AppUtils.sendLedBroad(context, false, "normalPower", 2, 2, AppUtils.PRIORITY_2);
    }

    /**
     * 开启、正常电量、充电
     * @param context
     */
    public static void startNormalPowerChargeLed(Context context){
        AppUtils.sendLedBroad(context, true, "normalPower", 2, 2, AppUtils.PRIORITY_2);
    }

    /**
     * 结束、正常电量、充电
     * @param context
     */
    public static void endNormalPowerChargeLed(Context context){
        AppUtils.sendLedBroad(context, false, "normalPower", 2, 2, AppUtils.PRIORITY_2);
    }

    /*************************************【满电量】***********************************************/
    /**
     * 开启、满电量、非充电
     * @param context
     */
    public static void startFullPowerLed(Context context){
        AppUtils.sendLedBroad(context, false, "fullPower", 2, 4, AppUtils.PRIORITY_2);
    }

    /**
     * 结束、满电量、非充电
     * @param context
     */
    public static void endFullPowerLed(Context context){
        AppUtils.sendLedBroad(context, false, "fullPower", 2, 4, AppUtils.PRIORITY_2);
    }

    /**
     * 开启、满电量、充电
     * @param context
     */
    public static void startFullPowerChargeLed(Context context){
        AppUtils.sendLedBroad(context, true, "fullPower", 2, 4, AppUtils.PRIORITY_2);
    }

    /**
     * 结束、满电量、充电
     * @param context
     */
    public static void endFullPowerChargeLed(Context context){
        AppUtils.sendLedBroad(context, false, "fullPower", 2, 4, AppUtils.PRIORITY_2);
    }

    /************************************【点亮或关闭屏幕】****************************************/
    /**
     * 灭屏闪绿灯
     * @param context
     */
    public static void startScreenOffLed(Context context){
        //AppUtils.sendLedBroad(context, true, "screen", 2, 2, AppUtils.PRIORITY_1);
    }

    /**
     * 亮屏不闪灯
     * @param context
     */
    public static void endScreenOnLed(Context context){
        //AppUtils.sendLedBroad(context, false, "screen", 2, 2, AppUtils.PRIORITY_1);
    }

}
