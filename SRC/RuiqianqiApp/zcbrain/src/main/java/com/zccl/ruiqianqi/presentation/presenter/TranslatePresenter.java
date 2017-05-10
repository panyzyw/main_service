package com.zccl.ruiqianqi.presentation.presenter;

import android.content.Context;

import com.zccl.ruiqianqi.domain.interactor.ITranslateInteractor;
import com.zccl.ruiqianqi.domain.interactor.translate.TranslateInteractor;
import com.zccl.ruiqianqi.presenter.base.BasePresenter;
import com.zccl.ruiqianqi.storage.TranslateRepository;

import java.lang.ref.WeakReference;

/**
 * Created by ruiqianqi on 2016/7/19 0019.
 */
public class TranslatePresenter extends BasePresenter {

    /** 类标识 */
    private static String TAG = TranslatePresenter.class.getSimpleName();
    /** USE_CASE：翻译用例 */
    private ITranslateInteractor translateInteractor;
    /** 回调UI接口 */
    private TranslateCallback2V translateCallback2V;

    public TranslatePresenter(Context context) {
        super(context);
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        translateInteractor = new TranslateInteractor(mContext, new TranslateRepository(mContext));
        translateInteractor.setTranslateCallback2P(new TranslateListener(this));
    }

    /************************************【设置回调UI接口】****************************************/
    /**
     * 设置回调UI接口
     * @param translateCallback2V
     */
    public void setTranslateCallback2V(TranslateCallback2V translateCallback2V) {
        this.translateCallback2V = translateCallback2V;
    }

    /************************************【中心对外提供的方法】************************************/
    /**
     * 线程池执行：翻译任务
     * @param words
     */
    public void translate(String words) {
        translateInteractor.translate(words);
    }

    /**
     * RX执行：翻译任务
     * @param words
     */
    public void translateRx(String words) {
        translateInteractor.translateRx(words);
    }

    /***********************************【领域给中心的回调实现】***********************************/
    /**
     * 来自DOMAIN层的回调接口
     */
    private static class TranslateListener<T> implements ITranslateInteractor.TranslateCallback2P<T>{

        //告诉系统，这玩意要是其他地方没有引用了，可以回收
        private final WeakReference<TranslatePresenter> weakRef;

        private TranslateListener(TranslatePresenter translatePresenter) {
            this.weakRef = new WeakReference<>(translatePresenter);
        }

        @Override
        public void OnTransSuccess(T t) {
            TranslatePresenter tp = weakRef.get();
            if(null != tp && null != tp.translateCallback2V){
                tp.translateCallback2V.OnSuccess(t);
            }
        }

        @Override
        public void OnTransFailure(Throwable e) {
            TranslatePresenter tp = weakRef.get();
            if(null != tp && null != tp.translateCallback2V){
                tp.translateCallback2V.OnFailure(e);
            }
        }
    }

    /**
     * 回调给UI的接口
     * @param <T>
     */
    public interface TranslateCallback2V<T>{
        void OnSuccess(T t);
        void OnFailure(Throwable e);
    }

}
