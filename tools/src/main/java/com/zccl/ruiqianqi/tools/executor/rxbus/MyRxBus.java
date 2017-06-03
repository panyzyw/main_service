package com.zccl.ruiqianqi.tools.executor.rxbus;

import android.os.Bundle;

import com.zccl.ruiqianqi.tools.executor.rxutils.MyRxUtils;

import java.util.HashMap;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ruiqianqi on 2016/11/8 0008.
 */

public class MyRxBus {

    // 主题，Subject是非线程安全的
    private SerializedSubject<Object, Object> mSubject;
    private HashMap<String, CompositeSubscription> mSubscriptionMap;

    /**
     * 单例
     * PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者
     * 序列化主题
     * 将Subject转换为一个SerializedSubject，类中把线程非安全的PublishSubject包装成线程安全的Subject
     */
    private MyRxBus() {
        mSubject = new SerializedSubject<>(PublishSubject.create());
    }

    /**
     * 对外提供的访问方法【现在暂时不用，因为不好用】
     * @return
     */
    private static MyRxBus getDefault() {
        return RxBusInstance.rxBus;
    }

    private static class RxBusInstance {
        private static final MyRxBus rxBus = new MyRxBus();
    }

    /**
     * 提供了一个新的事件用于发送,这时候的Subject相当于一个观察者
     *
     * @param obj 发送【事件类型类】的对象
     */
    public void post(Object obj) {
        mSubject.onNext(obj);
        //mSubject.onCompleted();
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     *
     * @param eventType
     * @param <T>
     * @return
     */
    private <T> Observable<T> toObservable(final Class<T> eventType) {
        return mSubject.ofType(eventType);
        // ofType = filter + cast
        /*return mSubject.filter(new Func1<Object, Boolean>() {
            @Override
            public Boolean call(Object obj) {
                return eventType.isInstance(obj);
            }
        }) .cast(eventType);*/
    }

    /**
     * 一个默认的订阅方法
     *
     * @param type  【设置事件类型】
     * @param next
     * @param error
     * @param <T>
     * @return
     */
    public <T> Subscription doSubscribe(Class<T> type, Action1<T> next, Action1<Throwable> error) {
        return toObservable(type)
                .compose(MyRxUtils.<T>handleSchedulers())
                .subscribe(next, error);
    }

    /**
     * 全部通过Bundle类型进行订阅的方法
     *
     * @param next
     * @param error
     * @return
     */
    public Subscription doSubscribeBundle(Action1<Bundle> next, Action1<Throwable> error) {
         Observable<Bundle> observable = toObservable(Bundle.class)
                .map(new Func1<Object, Bundle>() {
                    @Override
                    public Bundle call(Object obj) {
                        return (Bundle) obj;
                    }
                })
                .compose(MyRxUtils.<Bundle>handleSchedulers());
        if(null != error) {
            return observable.subscribe(next, error);
        }else {
            return observable.subscribe(next);
        }
    }

    /**
     * 保存订阅后的subscription
     * 一个obj可以保存、使用很多个subscription
     * @param obj
     * @param subscription
     */
    public void addSubscription(Object obj, Subscription subscription) {
        if (mSubscriptionMap == null) {
            mSubscriptionMap = new HashMap<>();
        }
        String key = obj.getClass().getName();
        if (mSubscriptionMap.get(key) != null) {
            mSubscriptionMap.get(key).add(subscription);
        } else {
            CompositeSubscription compositeSubscription = new CompositeSubscription();
            compositeSubscription.add(subscription);
            mSubscriptionMap.put(key, compositeSubscription);
        }
    }

    /**
     * 取消订阅
     * 一个obj可以保存、使用很多个subscription
     * @param obj
     */
    public void unSubscribe(Object obj) {
        if (mSubscriptionMap == null) {
            return;
        }
        String key = obj.getClass().getName();
        if (!mSubscriptionMap.containsKey(key)) {
            return;
        }
        if (mSubscriptionMap.get(key) != null) {
            mSubscriptionMap.get(key).unsubscribe();
        }
        mSubscriptionMap.remove(key);
    }

    /**
     * 是否已有观察者订阅
     *
     * @return
     */
    public boolean hasObservers() {
        return mSubject.hasObservers();
    }

    /**
     * 发布
     */
    private void testPublish() {
        // 利用bundle传值
        Bundle bundle = new Bundle();
        bundle.putString("type", "okay");
        MyRxBus.getDefault().post(bundle);
    }

    /**
     * 消费
     */
    private void testOperator() {
        MyRxBus.getDefault().doSubscribeBundle(
                new Action1<Bundle>() {
                    @Override
                    public void call(Bundle bundle) {
                        if (bundle.getString("type").equals("okay")) {

                        }
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });

    }

}
