package com.zccl.ruiqianqi.storage.serviceapi;

import com.zccl.ruiqianqi.config.MyConfig;
import com.zccl.ruiqianqi.domain.model.httpreq.BoYanUp;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by zc on 2016/6/23.
 */
public interface IHttpReqAPI {

    // application/x-www-form-urlencoded： 窗体数据被编码为名称/值对。这是标准的编码格式。
    // multipart/form-data： 窗体数据被编码为一条消息，页上的每个控件对应消息中的一个部分。
    // text/plain： 窗体数据以纯文本形式进行编码，其中不含任何控件或格式字符。

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=UTF-8")
    //@Headers("Content-Type: text/plain;charset=UTF-8")
    @POST(MyConfig.BO_YAN_URI)
    Observable<String> queryBoYan(@Body BoYanUp boYanUp);

}
