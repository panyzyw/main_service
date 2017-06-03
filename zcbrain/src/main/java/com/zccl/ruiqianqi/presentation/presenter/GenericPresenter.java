package com.zccl.ruiqianqi.presentation.presenter;

import android.provider.Settings;

import com.zccl.ruiqianqi.mind.voice.iflytek.beans.GenericBean;
import com.zccl.ruiqianqi.presenter.base.BasePresenter;
import com.zccl.ruiqianqi.tools.MyAppUtils;

/**
 * Created by ruiqianqi on 2017/5/26 0026.
 */

public class GenericPresenter extends BasePresenter{

    // 类标志
    private static String TAG = GenericPresenter.class.getSimpleName();

    // 返回主页
    public static final String HOME_PAGE = "homepage";
    // 退出当前应用
    public static final String CLOSE_APP = "closeapp";
    // 返回上级页面
    public static final String PAGE_BACK = "pageback";
    // 打开设置
    public static final String SETTINGS = "settings";
    // 打开遥控器
    public static final String REMOTE_CONTROL = "remotecontorl";
    // 打开游戏
    public static final String GAME = "game";
    // 打开工具
    public static final String TOOL = "tool";
    // 打开文档管理
    public static final String DOC_MANAGE = "docmanage";
    // 打开我的应用
    public static final String MY_APP = "myapp";
    // 打开学习
    public static final String STUDY = "study";
    // 打开档案
    public static final String PROFILE = "profile";
    // 打开无线网络设置
    public static final String WIFI = "wifi";
    // 打开蓝牙
    public static final String BLUE_TOOTH = "bluetooth";
    // 打开我的下载
    public static final String DOWNLOAD = "download";
    // 打开浏览器
    public static final String BROWSER = "browser";
    /*
    // 绑定二维码
    public static final String QR_CODE_BIND = "qrcode_bind";
    // 下载二维码
    public static final String QR_CODE_DOWNLOAD = "qrcode_download";
    */


    // 打开网络设置
    public static final String NET_SETTINGS = "net_settings";
    // 打开关于界面
    public static final String ABOUT = "about";
    // 打开飞行模式
    public static final String FLYING = "flying";
    // 打开网络热点
    public static final String WIFI_HOT = "wifi_hot";
    // 打开显示设置
    public static final String DISPLAY = "display";
    // 打开提示音设置
    public static final String SOUND = "sound";
    // 打开位置信息
    public static final String LOCATION = "location";
    // 打开语言
    public static final String LANGUAGE = "language";
    // 打开日期
    public static final String DATE_TIME = "date_time";
    // 恢复出厂设置
    public static final String BACK_TO_FACTORY = "back_to_factory";
    // 存储状况
    public static final String STORAGE = "storage";
    // 电池电量
    public static final String BATTERY = "battery";
    // 耗电信息
    public static final String BATTERY_USAGE = "battery_usage";
    // 应用列表
    public static final String APP_LIST = "app_list";

    // 信息
    public static final String MMS = "mms";
    // 录音机
    public static final String RECORDER = "recorder";
    // 文件管理
    public static final String FILE_MANAGER = "file_manager";
    // 日历
    public static final String CALENDAR = "calendar";
    // 时钟
    public static final String DESK_CLOCK = "deskclock";
    // 通讯录
    public static final String CONTACTS = "contacts";

    // 影视
    public static final String MOVIE_MUSEUM = "movie_museum";
    // 文艺馆
    public static final String ART_MUSEUM = "art_museum";
    // 投影设置
    public static final String PROJECTION = "projection";

    /**
     * 讯飞后台返回的
     * @param genericBean
     */
    public void genericOperator(GenericBean genericBean){
        genericOperator(genericBean.semantic.slots.action);
    }

    /**
     * 具体的通用功能
     * @param function
     */
    public void genericOperator(String function){

        // 游戏
        if(GAME.equals(function)){
            MyAppUtils.openApp(mContext, "com.yongyida.robot.game");
        }
        // 设置
        else if(SETTINGS.equals(function)){
            MyAppUtils.openSetting(mContext);
        }
        // 网络设置
        else if(NET_SETTINGS.equals(function)){
            MyAppUtils.openNetSetting(mContext);
        }
        // WIFI设置
        else if(WIFI.equals(function)){
            MyAppUtils.openWifiSetting(mContext);
        }
        // 蓝牙
        else if(BLUE_TOOTH.equals(function)){
            MyAppUtils.openBluetoothSetting(mContext);
        }
        // 关于
        else if(ABOUT.equals(function)){
            MyAppUtils.openAboutSetting(mContext);
        }
        // 飞行模式
        else if(FLYING.equals(function)){
            MyAppUtils.openToolsUI2(mContext, Settings.ACTION_AIRPLANE_MODE_SETTINGS);
        }
        // 网络热点
        else if(WIFI_HOT.equals(function)){
            MyAppUtils.openToolsUI(mContext, "com.android.settings.TetherSettings");
        }
        // 显示设置
        else if(DISPLAY.equals(function)) {
            MyAppUtils.openToolsUI2(mContext, Settings.ACTION_DISPLAY_SETTINGS);
        }
        // 提示音
        else if(SOUND.equals(function)) {
            MyAppUtils.openToolsUI2(mContext, Settings.ACTION_SOUND_SETTINGS);
        }
        // 位置信息
        else if(LOCATION.equals(function)) {
            MyAppUtils.openToolsUI2(mContext, Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        }
        // 语言
        else if(LANGUAGE.equals(function)) {
            MyAppUtils.openToolsUI2(mContext, Settings.ACTION_LOCALE_SETTINGS);
        }
        // 日期
        else if(DATE_TIME.equals(function)) {
            MyAppUtils.openToolsUI2(mContext, Settings.ACTION_DATE_SETTINGS);
        }
        // 出厂设置
        else if(BACK_TO_FACTORY.equals(function)) {
            MyAppUtils.openToolsUI2(mContext, Settings.ACTION_PRIVACY_SETTINGS);
        }
        // 存储状况
        else if(STORAGE.equals(function)) {
            MyAppUtils.openToolsUI2(mContext, Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
        }
        // 电池电量
        else if(BATTERY.equals(function)) {
            MyAppUtils.startBatteryStatus(mContext);
        }
        // 耗电信息
        else if(BATTERY_USAGE.equals(function)) {
            MyAppUtils.startBatteryUsage(mContext);
        }
        // 应用列表
        else if(APP_LIST.equals(function)) {
            MyAppUtils.openToolsUI2(mContext, Settings.ACTION_APPLICATION_SETTINGS);
        }


        // 下载
        else if(DOWNLOAD.equals(function)) {
            MyAppUtils.openApp(mContext, "com.android.providers.downloads.ui");
        }
        // 信息
        else if(MMS.equals(function)) {
            MyAppUtils.openApp(mContext, "com.android.mms");
        }
        // 录音机
        else if(RECORDER.equals(function)) {
            MyAppUtils.openApp(mContext, "com.android.soundrecorder");
        }
        // 文件管理
        else if(FILE_MANAGER.equals(function)) {
            MyAppUtils.openApp(mContext, "com.mediatek.filemanager");
        }
        // 日历
        else if(CALENDAR.equals(function)) {
            MyAppUtils.openApp(mContext, "com.android.calendar");
        }
        // 时钟
        else if(DESK_CLOCK.equals(function)) {
            MyAppUtils.openApp(mContext, "com.android.deskclock");
        }
        // 浏览器
        else if(BROWSER.equals(function)) {
            MyAppUtils.openApp(mContext, "com.android.browser");
        }
        // 通讯录
        else if(CONTACTS.equals(function)) {
            MyAppUtils.openApp(mContext, "com.android.contacts");
        }


        // 应用
        else if(MY_APP.equals(function)) {
            MyAppUtils.startApp(mContext, "com.yongyida.robot.launcher2", "com.yongyida.robot.launcher2.activity.sub.UserAppActivity");
        }
        // 影视
        else if(MOVIE_MUSEUM.equals(function)) {
            MyAppUtils.openApp(mContext, "com.yongyida.robot.videotutarial");
        }
        // 文艺馆
        else if(ART_MUSEUM.equals(function)) {
            MyAppUtils.openApp(mContext, "com.yongyida.robot.artmuseum");
        }
        // 工具
        else if(TOOL.equals(function)) {
            MyAppUtils.startApp(mContext, "com.yongyida.robot.launcher2", "com.yongyida.robot.launcher2.activity.sub.ToolsAppActivity");
        }
        // 投影设置
        else if(PROJECTION.equals(function)) {
            MyAppUtils.startApp(mContext, "com.yongyida.robot.launcher2", "com.yongyida.robot.launcher2.projector.ProjectorActivity");
        }
        // 学习
        else if(STUDY.equals(function)) {
            MyAppUtils.startApp(mContext, "com.yongyida.robot.launcher2", "com.yongyida.robot.launcher2.activity.sub.EducationActivity");
        }
        // 遥控器
        else if(REMOTE_CONTROL.equals(function)) {
            MyAppUtils.openApp(mContext, "com.yongyida.robot.irremote");
        }
        // 文档管理
        else if(DOC_MANAGE.equals(function)) {
            MyAppUtils.openApp(mContext, "com.yongyida.robot.resourcemanager");
        }

        // 小勇档案
        else if(PROFILE.equals(function)) {
            //
        }

        // 返回主界面
        else if(HOME_PAGE.equals(function)){
            MyAppUtils.returnHome(mContext);
        }
        // 关闭应用
        else if(CLOSE_APP.equals(function)){
            MyAppUtils.returnHome(mContext);
        }
        // 返回上级
        else if(PAGE_BACK.equals(function)){
            MyAppUtils.backOperation();
        }
    }

}
