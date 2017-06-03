package com.zccl.ruiqianqi.storage;

import android.content.Context;

import com.zccl.ruiqianqi.config.MyConfig;
import com.zccl.ruiqianqi.domain.repository.ITranslateRepository;
import com.zccl.ruiqianqi.storage.serviceapi.ITranslate;
import com.zccl.ruiqianqi.tools.http.myhttp.HttpFactory;

import java.net.HttpURLConnection;

import rx.Observable;

/**
 * Created by ruiqianqi on 2016/7/18 0018.
 */
public class TranslateRepository implements ITranslateRepository {

    /** 全局上下文 */
    private Context mContext;
    /** 网络请求服务 */
    private MyRetrofit retrofit;

    public TranslateRepository(Context context) {
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
    public String translate(String words) {
        String url = MyConfig.TRANSLATE_URL + MyConfig.TRANSLATE_PARAM + "=" + words;
        HttpURLConnection httpURLConnection = HttpFactory.createHttpConn(url, HttpFactory.GET);
        String json = HttpFactory.getHttpData(httpURLConnection);
        return json;
    }

    @Override
    public Observable<String> translateRx(String words) {
        ITranslate translate = retrofit.translateService();
        Observable<String> observable = translate.translate(words);
        return observable;
    }
}
