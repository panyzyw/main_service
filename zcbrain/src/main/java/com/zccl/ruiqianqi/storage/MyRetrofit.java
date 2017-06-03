package com.zccl.ruiqianqi.storage;

import android.content.Context;

import com.zccl.ruiqianqi.config.MyConfig;
import com.zccl.ruiqianqi.presentation.presenter.PersistPresenter;
import com.zccl.ruiqianqi.storage.serviceapi.IHttpReqAPI;
import com.zccl.ruiqianqi.storage.serviceapi.ITranslate;
import com.zccl.ruiqianqi.tools.http.okhttp.BaseRetrofit;

import retrofit2.Retrofit;

/**
 * Created by ruiqianqi on 2016/12/28 0028.
 */

public class MyRetrofit extends BaseRetrofit {

    /**
     * 构造方法
     *
     * @param context
     */
    public MyRetrofit(Context context) {
        super(context);
    }

    /**
     * 翻译服务
     * @return
     */
    public ITranslate translateService(){
        Retrofit mRetrofit = getRetrofit()
                .baseUrl(MyConfig.TRANSLATE_URL)
                .build();
        return mRetrofit.create(ITranslate.class);
    }

    /**
     * HTTP请求服务
     * @return
     */
    public IHttpReqAPI httpReqService(){
        Retrofit mRetrofit = getRetrofit()
                .baseUrl("http://" + PersistPresenter.getInstance().getHttpRequest() + "/")
                .build();
        return mRetrofit.create(IHttpReqAPI.class);
    }
}
