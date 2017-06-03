package com.zccl.ruiqianqi.domain.interactor;

/**
 * Created by ruiqianqi on 2016/7/25 0025.
 */
public interface ITranslateInteractor {
    /**
     * 中英文互翻
     *
     * @param words
     */
    void translate(String words);

    /**
     * 中英文互翻
     *
     * @param words
     */
    void translateRx(String words);

    /**
     * 领域设置翻译回调接口
     *
     * @param translateCallback
     */
    void setTranslateCallback2P(TranslateCallback2P translateCallback);

    /**
     * 使用者要实现的接口
     */
    interface TranslateCallback2P<T> {
        void OnTransSuccess(T t);

        void OnTransFailure(Throwable e);
    }

}
