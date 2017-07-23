package com.zccl.ruiqianqi.domain.interactor;

/**
 * Created by ruiqianqi on 2016/7/25 0025.
 */
public interface IHttpReqInteractor {

    // 查询薄言语义
    int QUERY_BO_YAN = 1;
    // 查询自定义语义
    int QUERY_CUSTOM_QA = 2;

    /**
     * 查询薄言语义
     *
     * @param words
     */
    void queryBoYan(String words, String rId, String rName);

    /**
     * 查询自定义语义
     *
     * @param question   问题
     * @param oem        定制机型号
     * @param rId        机器人序列号上的id
     * @param sId        机器人序列号上的serial(4个字符)
     */
    void queryCustomQA(String question, String oem, String rId, String sId);

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
