package com.zccl.ruiqianqi.brain.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.zccl.ruiqianqi.brain.service.observer.NameObserver;
import com.zccl.ruiqianqi.brain.service.observer.VideoObserver;
import com.zccl.ruiqianqi.mind.service.SystemService;
import com.zccl.ruiqianqi.presentation.presenter.PersistPresenter;
import com.zccl.ruiqianqi.presentation.presenter.LocalPresenter;

import static com.zccl.ruiqianqi.brain.service.observer.NameObserver.NAME_URI;
import static com.zccl.ruiqianqi.brain.service.observer.VideoObserver.VIDEO_URI;

/**
 * Created by ruiqianqi on 2017/1/14 0014.
 */

public class MainService extends SystemService {

    /**
     * 启动主服务
     * @param context
     */
    public static void startMyService(Context context) {
        Intent intent = new Intent(context, MainService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(intent);
    }

    /**
     * 关闭主服务
     * @param context
     */
    public static void stopMyService(Context context) {
        Intent intent = new Intent(context, MainService.class);
        context.stopService(intent);
    }



    /**********************************************************************************************/
    /**********************************************************************************************/
    /**********************************************************************************************/

    // 机器人名字数据库观察者
    private NameObserver mNameObserver;
    // 视频配置数据库观察者
    private VideoObserver mVideoObserver;
    // 本地服务器数据处理中心
    private LocalPresenter mLocalPresenter;

    @Override
    public void onCreate() {
        super.onCreate();

        // 注册远程Media控制广播
        registers.registerMediaButton();
        // 注册耳机及远程Media控制广播
        registers.registerHeadsetPlugReceiver();

        // 观察机器人名字数据库
        mNameObserver = new NameObserver(this, null);
        Uri uriName = Uri.parse(NAME_URI);
        getContentResolver().registerContentObserver(uriName, true, mNameObserver);

        // 观察视频配置数据库
        mVideoObserver = new VideoObserver(this, null);
        Uri uriVideo = Uri.parse(VIDEO_URI);
        getContentResolver().registerContentObserver(uriVideo, true, mVideoObserver);

        // 初始化一些机器人配置
        PersistPresenter.getInstance().initSome();

        // 开启本地服务器
        mLocalPresenter = new LocalPresenter();
        mLocalPresenter.initSome();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // 注销远程Media控制广播
        registers.unregisterMediaButton();
        // 注销耳机及远程Media控制广播
        registers.registerHeadsetPlugReceiver();

        // 服务已死，取消观察机器人名字变化
        getContentResolver().unregisterContentObserver(mNameObserver);
        // 服务已死，取消观察视频状态
        getContentResolver().unregisterContentObserver(mVideoObserver);

        // 关闭本地服务器
        mLocalPresenter.release();

        // 死了，继续重启
        MainService.startMyService(getApplicationContext());
    }
}
