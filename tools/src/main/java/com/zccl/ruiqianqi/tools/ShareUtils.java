package com.zccl.ruiqianqi.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by zc on 2015/11/16.
 */
public class ShareUtils {

    /**
     * 默认的 KV数据存储器
     * @param context
     * @return
     */
    public static SharedPreferences getP(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * 默认的 KV数据存储器
     * @param context
     * @return
     */
    public static SharedPreferences.Editor getE(Context context){
        return getP(context).edit();
    }

    /**
     * 指定名字的 KV数据存储器
     * @param context
     * @param name   XML文件名
     * @return
     */
    public static SharedPreferences getP(Context context, String name){
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    /**
     * 指定名字的 KV数据存储器
     * @param context
     * @param name   XML文件名
     * @return
     */
    public static SharedPreferences.Editor getE(Context context, String name){
        return getP(context, name).edit();
    }

    /**
     * 指定名字的 KV数据存储器
     * @return
     */
    /*public static SharedPreferences.Editor getE(){
        return getE(MyApplication.getContext());
    }*/

    /**
     * 默认的 KV数据存储器
     * @return
     */
    /*public static SharedPreferences getP(){
        return getP(MyApplication.getContext());
    }*/

}
