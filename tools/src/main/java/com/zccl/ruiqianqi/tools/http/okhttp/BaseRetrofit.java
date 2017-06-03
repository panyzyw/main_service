package com.zccl.ruiqianqi.tools.http.okhttp;

import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.protobuf.ProtoConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by zc on 2016/6/23.
 */
public class BaseRetrofit {

    /** 类的标志 */
    private static String TAG = BaseRetrofit.class.getSimpleName();

    /** 单例标识什么的 */
    private static BaseRetrofit instance = null;

    /** HTTP客户端 */
    private MyOkClient mOkClient = null;

    /** 全局上下文 */
    protected Context mContext;

    /**
     * 构造方法
     */
    public BaseRetrofit(Context context) {
        mContext = context;
        mOkClient = new MyOkClient(mContext);
    }

    /**
     * 用这个用话，instance不需要用volatile修饰
     * @return
     */
    public static BaseRetrofit getInstance(Context context) {
        if(instance == null) {
            synchronized(BaseRetrofit.class) {
                BaseRetrofit temp = instance;
                if(temp == null) {
                    temp = new BaseRetrofit(context);
                    instance = temp;
                }
            }
        }
        return instance;
    }

    /**
     * 通用的配置
     * @return
     */
    protected Retrofit.Builder getRetrofit(){
        Retrofit.Builder builder = new Retrofit.Builder()
                // 增加返回值为String的支持
                .addConverterFactory(ScalarsConverterFactory.create())
                // 增加返回值为protobuf的支持
                .addConverterFactory(ProtoConverterFactory.create())
                // 增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(GsonConverterFactory.create(MyGson.get()))
                // 增加返回值为Oservable<T>的支持
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                // 配置OKHTTP
                .client(mOkClient.getOkHttpClient());
        return builder;
    }


    /**
     * 查询IP服务
     * @return
     */
    /*
    public IQueryIp queryIpService(){
        Retrofit mRetrofit = getRetrofit()
                .baseUrl(APIConfigure.QUERY_IP_URL)
                .build();
        return mRetrofit.create(IQueryIp.class);
    }
    */

    /**
     * 翻译服务
     * @return
     */
    /*
    public ITranslate translateService(){
        Retrofit mRetrofit = getRetrofit()
                .baseUrl(APIConfigure.TRANSLATE_URL)
                .build();
        return mRetrofit.create(ITranslate.class);
    }
    */

    /**
     * 获取用户系统，操作接口
     * @return
     */
    /*
    public IUserAPI getUserService(){
        Retrofit mRetrofit = getRetrofit()
                .baseUrl(APIConfigure.BASE_URL)
                .build();
        return mRetrofit.create(IUserAPI.class);
    }
    */

    /**
     * 获取文件上传接口
     * @return
     */
    /*
    public IFileUploadService getFileUploadService(){
        Retrofit mRetrofit = getRetrofit()
                .baseUrl(APIConfigure.BASE_URL)
                .build();
        return mRetrofit.create(IFileUploadService.class);
    }
    */
}
