package com.zccl.ruiqianqi.mind.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.zccl.ruiqianqi.tools.LogUtils;

/**
 * Created by ruiqianqi on 2016/8/16 0016.
 */
public class BaseService extends Service {

    /** 类的标志 */
    protected String TAG = null;
    /** 服务ID */
    protected int startId = 0;

    @Override
    public void onCreate() {
        TAG = this.getClass().getSimpleName();
        super.onCreate();

        LogUtils.e(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.startId = startId;
        LogUtils.e(TAG, "onStartCommand: "+ flags +"--"+ startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        LogUtils.e(TAG, "onStart: "+startId);
        super.onStart(intent, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.e(TAG, "onBind");
        return null;
    }

    @Override
    public void onRebind(Intent intent) {
        LogUtils.e(TAG, "onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.e(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        LogUtils.e(TAG, "onDestroy: "+startId);
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        LogUtils.e(TAG, "onConfigurationChanged: "+newConfig);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        LogUtils.e(TAG, "onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }

}
