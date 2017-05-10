package com.zccl.ruiqianqi.storage.serviceapi;

import com.zccl.ruiqianqi.config.MyConfig;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by ruiqianqi on 2016/8/12 0012.
 */
public interface ITranslate {
    //这里最前面不能带“/”，后面不能带“？”，通过@Query指定key，后面跟上value
    //@Headers({"Content-Type: application/json", "Accept: application/json"})
    //自动 URLEncode
    @GET(MyConfig.TRANSLATE_PARAM)
    Observable<String> translate(@Query("q") String words);
}
