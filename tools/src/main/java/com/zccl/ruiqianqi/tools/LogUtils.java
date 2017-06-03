package com.zccl.ruiqianqi.tools;

import android.os.Environment;
import android.util.Log;

import com.zccl.ruiqianqi.tools.BuildConfig;
import com.zccl.ruiqianqi.tools.config.MyConfigure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

    /**
     * 用文件记录
     * @param fileName
     * @param content
     */
    public static void f(String fileName, String content){
        if(StringUtils.isEmpty(fileName))
            return;
        if(StringUtils.isEmpty(content))
            return;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = MyConfigure.ZCCL_SDCARD;
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try {
                FileOutputStream fos = new FileOutputStream(path + fileName, true);
                fos.write(content.getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
