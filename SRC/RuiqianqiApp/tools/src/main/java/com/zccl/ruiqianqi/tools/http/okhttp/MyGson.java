package com.zccl.ruiqianqi.tools.http.okhttp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by roy on 16/7/11.
 */
public class MyGson {

    /**
     * 创建JSON解析对象
     * @return
     */
    public static Gson get() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson;
    }

}
