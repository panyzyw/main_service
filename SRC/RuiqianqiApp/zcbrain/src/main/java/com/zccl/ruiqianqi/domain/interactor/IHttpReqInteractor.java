package com.zccl.ruiqianqi.domain.interactor;

/**
 * Created by ruiqianqi on 2016/7/25 0025.
 */
public interface IHttpReqInteractor {

    // 查询薄言语义
    int QUERY_BO_YAN = 1;

    /**
     * 查询薄言语义
     *
     * @param words
     */
    void queryBoYan(String words, String rId, String rName);

    /**
     * 领域HTTP请求回调接口
     *
     * @param httpReqCallback2P
     */
    void setHttpReqCallback2P(HttpReqCallback2P httpReqCallback2P);

    /**
     * HTTP的回调接口
     */
    interface HttpReqCallback2P<T> {
        void OnHttpSuccess(int cmd, T t);

        void OnHttpFailure(int cmd, Throwable e);
    }

}
