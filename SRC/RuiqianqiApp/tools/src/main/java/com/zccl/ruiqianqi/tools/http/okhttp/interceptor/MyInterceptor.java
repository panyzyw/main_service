package com.zccl.ruiqianqi.tools.http.okhttp.interceptor;

import android.content.Context;

import com.zccl.ruiqianqi.tools.LogUtils;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by roy on 16/6/27.
 * 头信息及参数重置
 */
public class MyInterceptor implements Interceptor {

    private static String TAG = MyInterceptor.class.getSimpleName();

    /** 全局上下文 */
    private Context mContext;

    public MyInterceptor(Context context){
        this.mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // 原始请求
        Request request = chain.request();
        /*
        Headers headers = request.headers();
        for (int i = 0; i < headers.size() ; i++) {
            LogUtils.e(TAG, headers.name(i)+": "+headers.value(i));
        }
        */
        Request.Builder builder = request.newBuilder();

        //更改HTTP头参数【这里是通用的头设置】
        /*
        请求头字段	    说明	                    响应头字段
        Accept	        告知服务器发送何种媒体类型	Content-Type
        Accept-Language	告知服务器发送何种语言	    Content-Language
        Accept-Charset	告知服务器发送何种字符集	Content-Type
        Accept-Encoding	告知服务器采用何种压缩方式	Content-Encoding
        普通浏览器访问网页，之所以添加：
        "Accept-Encoding" = "gzip,deflate" 加了这个就要添加gzip解码拦截器
        那是因为，浏览器对于从服务器中返回的对应的gzip压缩的网页，会自动解压缩，所以，其request的时候，添加对应的头，表明自己接受压缩后的数据。
        而此代码中，如果也添加此头信息，结果就是，返回的压缩后的数据，没有解码，而将压缩后的数据当做普通的html文本来处理，当前显示出来的内容，是乱码了。
        */

        /**
         * 定义静态请求头
         * 添加header参数Request提供了两个方法，一个是header(key, value)，另一个是.addHeader(key, value)，
         * 两者的区别是，header()如果有重名的将会覆盖，而addHeader()允许相同key值的header存在
         */
        builder//.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .addHeader("Accept", "*/*")
                //.addHeader("Accept-Encoding", "gzip, deflate, sdch")
                .addHeader("Accept-Language", "zh-CN,en-US,zh;q=0.8,en;q=0.8")
                //.addHeader("Content-Type", "application/json;charset=UTF-8,text/html;q=0.9,application/xml;q=0.8,*/*;q=0.7")
                //.addHeader("Content-Encoding", "gzip, deflate, sdch")
                .addHeader("Connection", "keep-alive")
                .addHeader("Cookie", "add cookies here");

        // 更改URL参数【这里是请求参数设置】
        HttpUrl modifiedUrl = request.url().newBuilder()
                //.addQueryParameter("name", "value")
                .build();

        builder.url(modifiedUrl);

        request = builder.build();

        // 打印请求头
        Headers headers = request.headers();
        for (int i = 0; i < headers.size(); i++) {
            LogUtils.e(TAG, headers.name(i) + " : " + headers.value(i));
        }

        // 发起请求
        Response response = chain.proceed(request);

        // 原因：由于某处两次调用了
        //response.body().string();导致closed，有且只能调用一次
        //ResponseBody responseBody = response.body();
        //LogUtils.e(TAG, "result: "+responseBody.string());
        return response;
    }
}
