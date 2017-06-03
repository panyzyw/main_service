package com.zccl.ruiqianqi.tools.executor.impl;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.zccl.ruiqianqi.tools.executor.IMainThread;

/**
 * Created by ruiqianqi on 2016/7/18 0018.
 */
public class MyMainThread implements IMainThread {

    /** 主线程单例 */
    private static MyMainThread instance;
    /** 主线程Handler */
    private Handler mHandler;

    private MyMainThread() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 单例模式
     * @return
     */
    public static MyMainThread getInstance() {
        if(instance == null) {
            synchronized(MyMainThread.class) {
                MyMainThread temp = instance;
                if(temp == null) {
                    temp = new MyMainThread();
                    instance = temp;
                }
            }
        }
        return instance;
    }

    @Override
    public void post(Runnable runnable) {
        mHandler.post(runnable);
    }

    @Override
    public void postDelayed(Runnable runnable, long delay) {
        mHandler.postDelayed(runnable, delay);
    }

    /**
     * 创建子线程Handler
     * @param name
     * @param callback
     * @return
     */
    public Handler createHandler(String name, Handler.Callback callback){
        // 子线程相关配置
        HandlerThread handlerThread = new HandlerThread(name);
        handlerThread.start();
        Looper mServiceLooper = handlerThread.getLooper();
        return new Handler(mServiceLooper, callback);
    }
}
