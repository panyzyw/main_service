package com.zccl.ruiqianqi.tools.http.okhttp;

import android.content.Context;
import android.os.Build;

import com.zccl.ruiqianqi.tools.BuildConfig;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.http.MySecurity;
import com.zccl.ruiqianqi.tools.http.Tls12SocketFactory;
import com.zccl.ruiqianqi.tools.http.okhttp.interceptor.MyAuthenticator;
import com.zccl.ruiqianqi.tools.http.okhttp.interceptor.MyInterceptor;
import com.zccl.ruiqianqi.tools.http.okhttp.interceptor.NetInterceptor;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by roy on 16/7/11.
 */
public class MyOkClient {

    // 类标志
    private static String TAG = MyOkClient.class.getSimpleName();
    // mediatype 这个需要和服务端保持一致
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    // 网络连接超时时间
    private static final int CONNECT_TIMEOUT = 10;
    // 读超时时间
    private static final int READ_TIMEOUT = 10;
    // 写超时时间
    private static final int WRITE_TIMEOUT = 10;

    // get请求
    public static final int TYPE_GET = 0;
    // post请求参数为json
    public static final int TYPE_POST_JSON = 1;
    // post请求参数为表单
    public static final int TYPE_POST_FORM = 2;

    // 单例引用
    private static MyOkClient instance;
    // OkHttp客户端
    private OkHttpClient mClient;
    // 全局上下文
    private Context mContext;
    // 基地址
    private String mBaseUrl;

    public MyOkClient(Context context) {
        this.mContext = context;
    }

    /**
     * 用这个用话，instance不需要用volatile修饰
     *
     * @return
     */
    public static MyOkClient getInstance(Context context) {
        if (instance == null) {
            synchronized (MyOkClient.class) {
                MyOkClient temp = instance;
                if (temp == null) {
                    temp = new MyOkClient(context);
                    instance = temp;
                }
            }
        }
        return instance;
    }

    /**
     * 获取OkHttp客户端
     * @return
     */
    public OkHttpClient getOkHttpClient() {
        if (null != mClient) {
            return mClient;
        }
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        // 打印请求到的json字符串和查看log
        if (BuildConfig.LOG_DEBUG) {
            // Log信息拦截器
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            // 设置 Debug Log 模式
            builder.addInterceptor(loggingInterceptor);
        }

        // 设置头信息及请求参数
        builder.addInterceptor(new MyInterceptor(mContext))
                // 没有TOKEN直接请求，有就带上
                .addNetworkInterceptor(new NetInterceptor(mContext))
                // 带上本地TOKEN直接请求
                .authenticator(new MyAuthenticator(mContext));

        // Gzip压缩
        //builder.addInterceptor(new GzipRequestInterceptor());

        // 设置cookie
        //CookieManager cookieManager = new CookieManager();
        //cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        //builder.cookieJar(new JavaNetCookieJar(cookieManager));

        // 开启缓存
        File cacheFile = new File(mContext.getExternalCacheDir(), "okHttpCache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 30);
        builder.cache(cache);
        //.addInterceptor(new CacheInterceptor());

        // 开启facebook debug
        //builder.addNetworkInterceptor(new StethoInterceptor());

        // 设置超时
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);

        // 错误重连
        builder.retryOnConnectionFailure(true);

        mClient = builder.build();

        return mClient;
    }

    /**
     * 获取OkHttp加密客户端
     *
     * Create a singleton OkHttp client that, hopefully, will someday be able to make sure all connections are valid according to AVS's strict
     * security policy--this will hopefully fix the Connection Reset By Peer issue.
     *
     * For some reason, android supports TLS v1.1 v1.2 from API 16, but enables it by
     * default only from API 20.
     * @return
     */
    public OkHttpClient getTLSOkHttp(){
        if (mClient != null) {
            return mClient;
        }

        OkHttpClient.Builder builder = getOkHttpClient().newBuilder();

        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 20) {
            try {

                MySecurity security = new MySecurity(MySecurity.SECURITY.TLS);
                SSLContext sslContext = security.getSSLContext();
                builder.sslSocketFactory(new Tls12SocketFactory(sslContext.getSocketFactory()), security.getTrustManager());

                ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .cipherSuites(CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384)
                        .build();
                List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(cs);
                builder.connectionSpecs(specs);

            } catch (Exception exc) {
                LogUtils.e(TAG, "Error while setting TLS 1.2", exc);
            }
        }

        mClient = builder.build();
        return mClient;
    }


    /**
     * 统一为请求添加头信息
     *
     * @return
     */
    private Request.Builder addHeaders() {
        Request.Builder builder = new Request.Builder()
                .addHeader("Connection", "keep-alive")
                .addHeader("phoneModel", Build.MODEL)
                .addHeader("phoneRelease", Build.VERSION.RELEASE)
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Language", "zh-CN,en-US,zh;q=0.8,en;q=0.8")
                //.addHeader("Accept-Encoding", "gzip, deflate, sdch")
                //.addHeader("Content-Encoding", "gzip, deflate, sdch")
                //.addHeader("Content-Type", "application/json;charset=UTF-8,text/html;q=0.9,application/xml;q=0.8,*/*;q=0.7")
                //.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                .addHeader("Cookie", "add cookies here");
        return builder;
    }

    /************************************【自身成员变量的读写】************************************/
    /**
     * 设置基地址
     * @param mBaseUrl
     */
    public void setBaseUrl(String mBaseUrl) {
        this.mBaseUrl = mBaseUrl;
    }

    /**********************************************************************************************/
    /**
     * okHttp同步请求统一入口
     *
     * @param actionUrl   接口地址
     * @param requestType 请求类型
     * @param paramsMap   请求参数
     */
    public void requestSync(String actionUrl, int requestType, HashMap<String, String> paramsMap) {
        switch (requestType) {
            case TYPE_GET:
                requestGetBySync(actionUrl, paramsMap);
                break;
            case TYPE_POST_JSON:
                requestPostBySync(actionUrl, paramsMap);
                break;
            case TYPE_POST_FORM:
                requestPostBySyncWithForm(actionUrl, paramsMap);
                break;
        }
    }


    /**
     * okHttp get同步请求
     *
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     */
    private void requestGetBySync(String actionUrl, HashMap<String, String> paramsMap) {
        StringBuilder tempParams = new StringBuilder();
        try {
            if (paramsMap != null) {
                //处理参数
                int pos = 0;
                for (String key : paramsMap.keySet()) {
                    if (pos > 0) {
                        tempParams.append("&");
                    }
                    //对参数进行URLEncoder
                    tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                    pos++;
                }
            }
            //补全请求地址
            String requestUrl = String.format("%s%s%s", mBaseUrl, actionUrl, tempParams.toString());
            //创建一个请求
            Request request = addHeaders().url(requestUrl).build();
            //创建一个Call
            final Call call = getOkHttpClient().newCall(request);
            //执行请求
            final Response response = call.execute();

            String result = response.body().string();

        } catch (Exception e) {
            LogUtils.e(TAG, e.toString());
        }
    }

    /**
     * okHttp post同步请求
     * post请求参数为json
     *
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     */
    private void requestPostBySync(String actionUrl, HashMap<String, String> paramsMap) {
        try {
            RequestBody body = null;
            if(paramsMap!=null) {
                //处理参数
                StringBuilder tempParams = new StringBuilder();
                int pos = 0;
                for (String key : paramsMap.keySet()) {
                    if (pos > 0) {
                        tempParams.append("&");
                    }
                    tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                    pos++;
                }
                //生成参数
                String params = tempParams.toString();
                //创建一个请求实体对象 RequestBody
                body = RequestBody.create(MEDIA_TYPE_JSON, params);
            }

            //补全请求地址
            String requestUrl = String.format("%s%s", mBaseUrl, actionUrl);
            //创建一个请求
            final Request request = addHeaders().url(requestUrl).post(body).build();
            //创建一个Call
            final Call call = getOkHttpClient().newCall(request);
            //执行请求
            Response response = call.execute();
            //请求执行成功
            if (response.isSuccessful()) {
                //获取返回数据 可以是String，bytes ,byteStream
                LogUtils.e(TAG, "response ----->" + response.body().string());
            }
        } catch (Exception e) {
            LogUtils.e(TAG, e.toString());
        }
    }

    /**
     * okHttp post同步请求表单提交
     * post请求参数为表单
     *
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     */
    private void requestPostBySyncWithForm(String actionUrl, HashMap<String, String> paramsMap) {
        try {
            RequestBody formBody = null;
            if(paramsMap!=null) {
                //创建一个FormBody.Builder
                FormBody.Builder builder = new FormBody.Builder();
                for (String key : paramsMap.keySet()) {
                    //追加表单信息
                    builder.add(key, paramsMap.get(key));
                }
                //生成表单实体对象
                formBody = builder.build();
            }
            //补全请求地址
            String requestUrl = String.format("%s%s", mBaseUrl, actionUrl);
            //创建一个请求
            Request request = addHeaders().url(requestUrl).post(formBody).build();
            //创建一个Call
            Call call = getOkHttpClient().newCall(request);
            //执行请求
            Response response = call.execute();
            if (response.isSuccessful()) {
                LogUtils.e(TAG, "response ----->" + response.body().string());
            }
        } catch (Exception e) {
            LogUtils.e(TAG, e.toString());
        }
    }

    /**********************************************************************************************/
    /**
     * okHttp异步请求统一入口
     * @param actionUrl   接口地址
     * @param requestType 请求类型
     * @param paramsMap   请求参数
     * @param callBack 请求返回数据回调
     * @param <T> 数据泛型
     **/
    public <T> Call requestAsync(String actionUrl, int requestType, HashMap<String, String> paramsMap, ReqCallBack<T> callBack) {
        Call call = null;
        switch (requestType) {
            case TYPE_GET:
                call = requestGetByAsync(actionUrl, paramsMap, callBack);
                break;
            case TYPE_POST_JSON:
                call = requestPostByAsync(actionUrl, paramsMap, callBack);
                break;
            case TYPE_POST_FORM:
                call = requestPostByAsyncWithForm(actionUrl, paramsMap, callBack);
                break;
        }
        return call;
    }

    /**
     * okHttp get异步请求
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     * @param callBack 请求返回数据回调
     * @param <T> 数据泛型
     * @return
     */
    private <T> Call requestGetByAsync(String actionUrl, HashMap<String, String> paramsMap, final ReqCallBack<T> callBack) {
        StringBuilder tempParams = new StringBuilder();
        try {
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            String requestUrl = String.format("%s%s%s", mBaseUrl, actionUrl, tempParams.toString());
            final Request request = addHeaders().url(requestUrl).build();
            final Call call = getOkHttpClient().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtils.e(TAG, e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        LogUtils.e(TAG, "response ----->" + string);
                    } else {

                    }
                }
            });
            return call;
        } catch (Exception e) {
            LogUtils.e(TAG, e.toString());
        }
        return null;
    }


    /**
     * okHttp post异步请求
     * post请求参数为json
     *
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     * @param callBack 请求返回数据回调
     * @param <T> 数据泛型
     * @return
     */
    private <T> Call requestPostByAsync(String actionUrl, HashMap<String, String> paramsMap, final ReqCallBack<T> callBack) {
        try {

            RequestBody body = null;
            if(paramsMap!=null) {
                StringBuilder tempParams = new StringBuilder();
                int pos = 0;
                for (String key : paramsMap.keySet()) {
                    if (pos > 0) {
                        tempParams.append("&");
                    }
                    tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                    pos++;
                }
                String params = tempParams.toString();
                body = RequestBody.create(MEDIA_TYPE_JSON, params);
            }
            String requestUrl = String.format("%s%s", mBaseUrl, actionUrl);
            Request request = addHeaders().url(requestUrl).post(body).build();
            Call call = getOkHttpClient().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtils.e(TAG, e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        LogUtils.e(TAG, "response ----->" + string);
                    } else {

                    }
                }
            });
            return call;
        } catch (Exception e) {
            LogUtils.e(TAG, e.toString());
        }
        return null;
    }

    /**
     * okHttp post异步请求表单提交
     * post请求参数为表单
     *
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     * @param callBack 请求返回数据回调
     * @param <T> 数据泛型
     * @return
     */
    private <T> Call requestPostByAsyncWithForm(String actionUrl, HashMap<String, String> paramsMap, final ReqCallBack<T> callBack) {
        try {

            RequestBody formBody = null;
            if(paramsMap!=null) {
                FormBody.Builder builder = new FormBody.Builder();
                for (String key : paramsMap.keySet()) {
                    builder.add(key, paramsMap.get(key));
                }
                formBody = builder.build();
            }
            String requestUrl = String.format("%s%s", mBaseUrl, actionUrl);
            Request request = addHeaders().url(requestUrl).post(formBody).build();
            Call call = getOkHttpClient().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtils.e(TAG, e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        LogUtils.e(TAG, "response ----->" + string);
                    } else {

                    }
                }
            });
            return call;
        } catch (Exception e) {
            LogUtils.e(TAG, e.toString());
        }
        return null;
    }

    public interface ReqCallBack<T> {
        /**
         * 响应成功
         */
        void onReqSuccess(T result);
        /**
         * 响应失败
         */
        void onReqFailed(String errorMsg);
    }
}