package com.zccl.ruiqianqi.tools.http.http2;

import com.zccl.ruiqianqi.tools.LogUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ruiqianqi on 2017/2/14 0014.
 */

public class MyHttp2Client {

    // 类标志
    private static String TAG = MyHttp2Client.class.getSimpleName();

    private static OkHttpClient client;
    // 网络连接超时时间
    private static final int CONNECT_TIMEOUT = 15;
    // 读超时时间
    private static final int READ_TIMEOUT = 15;
    // 写超时时间
    private static final int WRITE_TIMEOUT = 15;

    /**
     * 得到HTTP客户端
     * @return
     */
    public static OkHttpClient getOkHttpClient() {
        if (client != null) {
            return client;
        }
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        // 设置超时
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);

        // 错误重连
        builder.retryOnConnectionFailure(true);

        // 构造对象
        client = builder.build();
        return client;
    }

    /**
     * 添加文字部分请求
     * @param json
     * @return
     */
    private static MultipartBody.Part partJson(String json){
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        return MultipartBody.Part.createFormData("metadata", null, body);
    }

    /**
     * 添加音频部分请求
     * @param data
     * @return
     */
    private static MultipartBody.Part partData(byte[] data){
        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), data);
        return MultipartBody.Part.createFormData("audio", null, body);
    }

    /**
     * 异步【POST】请求
     * @param url
     * @param json
     * @param data
     */
    public static void postAsync(Request.Builder builder, String url,
                                 String json, byte[] data, final ResponseListener responseListener){

        MultipartBody.Part part1 = partJson(json);
        MultipartBody.Part part2 = partData(data);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(part1)
                .addPart(part2)
                .build();

        Request request = builder.url(url).post(requestBody).build();

        getOkHttpClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.e(TAG, e.getMessage(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int code = response.code();
                LogUtils.e(TAG, "Http protocol: " + response.protocol());
                //LogUtils.e(TAG, "Http name: " + response.protocol().name());
                //LogUtils.e(TAG, "Http code: " + code);
                // 有数据返回
                if(code==200){
                    if(null != responseListener){
                        responseListener.OnSuccess(response);
                    }
                }
                // 没有数据返回
                else if(code==204){

                }
            }
        });
    }

    /**
     * 异步【GET】请求
     * @param url
     */
    public static void getAsync(String url){
        Request request = new Request.Builder()
                .url(url)
                .build();

        getOkHttpClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.e(TAG, e.getMessage(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtils.d(TAG, "Http protocol: " + response.protocol());
                LogUtils.d(TAG, "Http name: " + response.protocol().name());
                LogUtils.d(TAG, "Response code is " + response.code());
                LogUtils.d(TAG, "Response msg is " + response.body().string());
            }

        });
    }

    /**
     * 有返回的数据响应
     */
    public interface ResponseListener{
        void OnSuccess(Response response);
    }

}
