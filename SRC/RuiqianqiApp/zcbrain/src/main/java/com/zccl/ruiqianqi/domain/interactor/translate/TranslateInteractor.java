package com.zccl.ruiqianqi.domain.interactor.translate;

import android.content.Context;

import com.zccl.ruiqianqi.domain.interactor.ITranslateInteractor;
import com.zccl.ruiqianqi.domain.interactor.base.BaseInteractor;
import com.zccl.ruiqianqi.domain.model.translate.TransInfoD;
import com.zccl.ruiqianqi.domain.repository.ITranslateRepository;
import com.zccl.ruiqianqi.tools.JsonUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.tools.executor.rxutils.MyRxUtils;
import com.zccl.ruiqianqi.tools.executor.security.WeakRunnable;

import java.lang.ref.WeakReference;

import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by ruiqianqi on 2016/7/25 0025.
 */
public class TranslateInteractor extends BaseInteractor implements ITranslateInteractor {
    // 类标志
    private static String TAG = TranslateInteractor.class.getSimpleName();
    /** 操作翻译数据仓库 */
    private final ITranslateRepository mTransRepository;
    /** 视图层注册的翻译回调接口 */
    private TranslateCallback2P translateCallback2P;

    public TranslateInteractor(Context context, ITranslateRepository transRepository) {
        super(context);

        this.mTransRepository = transRepository;
        init();
    }

    /**
     * 初始化
     */
    private void init(){

    }


    /************************************【设置UserCase回调接口】**********************************/
    /**
     * 设置翻译回调接口
     * @param translateCallback
     */
    @Override
    public void setTranslateCallback2P(TranslateCallback2P translateCallback) {
        this.translateCallback2P = translateCallback;
    }

    /*************************************【UserCase对外方法】*************************************/
    /**
     * 线程池执行：翻译任务
     * @param words
     */
    @Override
    public void translate(String words) {
        scheduleTask.execute(new TranslateRunnable(this, words));
    }

    /**
     * RX执行：翻译任务
     * 数据流转换：
     * map 使用一个转换的参数把源Observable中的数据转换为【另外一种类型的数据】。返回的 Observable中包含了转换后的数据。
     * flatMap 把源 Observable 中的一个数据转换为一个【新的 Observable】 发射出去。
     * cast(Integer.class) 把Observable中的数据强制转换为另一种类型，如果遇到类型不一样的对象的话，就会抛出一个error。
     * ofType(Integer.class) 判断Observable中的数据是否为该类型，如果不是则跳过这个数据。
     * timestamp() 把数据转换为 Timestamped 类型，里面包含了原始的数据和一个原始数据是何时发射的时间戳。
     *
     * @param words
     */
    @Override
    public void translateRx(String words) {
        mTransRepository.translateRx(words).
                // 对整个发射对象进行操作
                compose(MyRxUtils.<String>handleSchedulers()).
                // 把json格式转换成TransInfoD对象
                map(new Func1<String, TransInfoD>() {
                    @Override
                    public TransInfoD call(String transInfoJson) {
                        return JsonUtils.parseJson(transInfoJson, TransInfoD.class);
                    }
                }).
                // 开始订阅执行
                subscribe(new TranslateTask(this));
    }


    /**************************【静态内部类，不会持有外部类的引用】********************************/
    /**************************【线程执行完了，对返回数据的处理】**********************************/
    /**
     * 注册任务的任务体
     */
    private static class TranslateRunnable extends WeakRunnable<TranslateInteractor> {
        private String words;

        public TranslateRunnable(TranslateInteractor reference, String words) {
            super(reference);
            this.words = words;
        }

        @Override
        protected void run(TranslateInteractor ref) {
            final String json = ref.mTransRepository.translate(words);

            final TranslateInteractor refAfterTask = weak.get();
            if(null != refAfterTask) {
                refAfterTask.mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        if (refAfterTask.translateCallback2P != null) {
                            if (!StringUtils.isEmpty(json)) {
                                TransInfoD transInfoD = JsonUtils.parseJson(json, TransInfoD.class);
                                refAfterTask.translateCallback2P.OnTransSuccess(transInfoD);
                            } else {
                                refAfterTask.translateCallback2P.OnTransFailure(new Throwable("翻译失败"));
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * 查询IP的任务体的回调
     */
    private static class TranslateTask extends Subscriber<TransInfoD> {

        //告诉系统，这玩意要是其他地方没有引用了，可以回收
        private WeakReference<TranslateInteractor> weak;

        public TranslateTask(TranslateInteractor reference) {
            weak = new WeakReference<>(reference);
        }

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onNext(TransInfoD t) {
            TranslateInteractor ti = weak.get();
            if(null != ti && null != ti.translateCallback2P){
                ti.translateCallback2P.OnTransSuccess(t);
            }
        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            TranslateInteractor ti = weak.get();
            if(null != ti && null != ti.translateCallback2P){
                ti.translateCallback2P.OnTransFailure(e);
            }
        }
    }

}
