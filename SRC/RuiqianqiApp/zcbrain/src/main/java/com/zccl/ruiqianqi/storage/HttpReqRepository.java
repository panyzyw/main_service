package com.zccl.ruiqianqi.storage;

import android.content.Context;

import com.zccl.ruiqianqi.config.MyConfig;
import com.zccl.ruiqianqi.domain.model.httpreq.BoYanUp;
import com.zccl.ruiqianqi.domain.repository.IHttpReqRepository;
import com.zccl.ruiqianqi.domain.repository.ITranslateRepository;
import com.zccl.ruiqianqi.storage.serviceapi.IHttpReqAPI;
import com.zccl.ruiqianqi.storage.serviceapi.ITranslate;
import com.zccl.ruiqianqi.tools.http.myhttp.HttpFactory;

import java.net.HttpURLConnection;

import rx.Observable;

/**
 * Created by ruiqianqi on 2016/7/18 0018.
 */
public class HttpReqRepository implements IHttpReqRepository {

    /** 全局上下文 */
    private Context mContext;
    /** 网络请求服务 */
    private MyRetrofit retrofit;

    public HttpReqRepository(Context context) {
        mContext = context.getApplicationContext();
        init();
    }

    /**
     * 初始化方法
     */
    private void init(){
        retrofit = new MyRetrofit(mContext);
    }

    @Override
    public Observable<String> queryBoYanRx(BoYanUp boYanUp) {
        IHttpReqAPI httpReq = retrofit.httpReqService();
        Observable<String> observable = httpReq.queryBoYan(boYanUp);
        return observable;
    }
}
