package com.zccl.ruiqianqi.tools;

import android.util.Log;

import com.zccl.ruiqianqi.tools.BuildConfig;

/**
 * Created by zc on 2015/10/22.
 */
public class LogUtils {

    public final static String pre = "zccl-";
    /**
     * 当前应用PID，基本上就是我的应用本身的
     */
    public static int getPid(){
        return android.os.Process.myPid();
    }

    public static void d(String tag, String msg){
        if(BuildConfig.LOG_DEBUG){
            Log.d(pre + tag, "[ " + msg + " ]" + getPid() + ":" + Thread.currentThread().getId());
        }
    }

    public static void e(String tag, String msg){
        if(BuildConfig.LOG_DEBUG){
            Log.e(pre+tag, "[ "+msg+" ]"+getPid()+":"+Thread.currentThread().getId());
        }
    }

    public static void e(String tag, String msg, Throwable e){
        if(BuildConfig.LOG_DEBUG){
            Log.e(pre+tag, "[ "+msg+" ]"+getPid()+":"+Thread.currentThread().getId(), e);
        }
    }

    public static void i(String tag, String msg){
        if(BuildConfig.LOG_DEBUG){
            Log.i(pre+tag, "[ "+msg+" ]"+getPid()+":"+Thread.currentThread().getId());
        }
    }

    public static void w(String tag, String msg){
        if(BuildConfig.LOG_DEBUG){
            Log.w(pre+tag, "[ "+msg+" ]"+getPid()+":"+Thread.currentThread().getId());
        }
    }

    public static void v(String tag, String msg){
        if(BuildConfig.LOG_DEBUG){
            Log.v(pre+tag, "[ "+msg+" ]"+getPid()+":"+Thread.currentThread().getId());
        }
    }

    public static void s(String msg){
        if(BuildConfig.LOG_DEBUG){
            System.out.println("[ "+msg+" ]"+getPid()+":"+Thread.currentThread().getId());
        }
    }

}
