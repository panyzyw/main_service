package com.zccl.ruiqianqi.domain.interactor.httpreq;

import android.content.Context;

import com.zccl.ruiqianqi.domain.interactor.IHttpReqInteractor;
import com.zccl.ruiqianqi.domain.interactor.base.BaseInteractor;
import com.zccl.ruiqianqi.domain.model.httpreq.BoYanDown;
import com.zccl.ruiqianqi.domain.model.httpreq.BoYanUp;
import com.zccl.ruiqianqi.domain.model.translate.TransInfoD;
import com.zccl.ruiqianqi.domain.repository.IHttpReqRepository;
import com.zccl.ruiqianqi.tools.JsonUtils;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.executor.rxutils.MyRxUtils;

import java.lang.ref.WeakReference;

import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by ruiqianqi on 2017/4/24 0024.
 */

public class HttpReqInteractor extends BaseInteractor implements IHttpReqInteractor {

    // 类标志
    private static String TAG = HttpReqInteractor.class.getSimpleName();

    /**
     * HTTP数据仓库
     */
    private final IHttpReqRepository mHttpRepository;

    /**
     * 视图层注册的翻译回调接口
     */
    private HttpReqCallback2P mHttpReqCallback2P;

    public HttpReqInteractor(Context context, IHttpReqRepository httpRepository) {
        super(context);

        this.mHttpRepository = httpRepository;
        init();
    }

    /**
     * 初始化
     */
    private void init() {

    }

    /**
     * 设置HTTP请求回调接口
     *
     * @param httpReqCallback2P
     */
    @Override
    public void setHttpReqCallback2P(HttpReqCallback2P httpReqCallback2P) {
        this.mHttpReqCallback2P = httpReqCallback2P;
    }

    /**
     * 查询薄言数据
     *
     * @param words
     * @param rId
     * @param rName
     */
    @Override
    public void queryBoYan(String words, String rId, String rName) {
        BoYanUp boYanUp = new BoYanUp();
        boYanUp.setQuestion(words);
        boYanUp.setRid(rId);
        boYanUp.setRname(rName);

        LogUtils.e(TAG, " words = " + words);
        LogUtils.e(TAG, " rId = " + rId);
        LogUtils.e(TAG, " rName = " + rName);

        mHttpRepository.queryBoYanRx(boYanUp).
                // 对整个发射对象进行操作
                compose(MyRxUtils.<String>handleSchedulers()).
                // 把json格式转换成BoYanDown对象
                map(new Func1<String, BoYanDown>() {
                    @Override
                    public BoYanDown call(String boYanJson) {
                        LogUtils.e(TAG, " boYanJson = " + boYanJson);
                        return JsonUtils.parseJson(boYanJson, BoYanDown.class);
                    }
                }).
                // 开始订阅执行
                subscribe(new HttpReqInteractor.HttpReqTask<BoYanDown>(IHttpReqInteractor.QUERY_BO_YAN));
    }

    /**
     * 查询IP的任务体的回调
     */
    private class HttpReqTask<T> extends Subscriber<T> {

        private int cmd;

        public HttpReqTask(int cmd) {
            this.cmd = cmd;
        }

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onNext(T t) {
            LogUtils.e(TAG, "onNext");
            if(null != mHttpReqCallback2P){
                mHttpReqCallback2P.OnHttpSuccess(cmd, t);
            }
        }

        @Override
        public void onCompleted() {
            LogUtils.e(TAG, "onCompleted");
        }

        @Override
        public void onError(Throwable e) {
            LogUtils.e(TAG, "onError");
            if(null != mHttpReqCallback2P){
                mHttpReqCallback2P.OnHttpFailure(cmd, e);
            }
        }
    }
}
