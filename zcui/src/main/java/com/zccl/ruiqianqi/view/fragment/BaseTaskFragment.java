package com.zccl.ruiqianqi.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.executor.rxutils.MyRxUtils;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by ruiqianqi on 2016/9/20 0020.
 * 当在onCreate()方法中调用了setRetainInstance(true)后，
 * 在Activity重新创建时可以不完全销毁Fragment，
 * Fragment恢复时会跳过onCreate()和onDestroy()方法，
 * 因此不能在onCreate()中放置一些初始化逻辑，因为再次恢复时不走这里了
 * <p>
 * 需要注意的是，要使用这种操作的Fragment不能加入BackStack后退栈中。
 * 并且，被保存的Fragment实例不会保持太久，若长时间没有容器承载它，也会被系统回收掉的。
 */
public class BaseTaskFragment extends Fragment {

    /** 类的标志 */
    protected String TAG = null;

    /** 订阅体 */
    private Subscription subscription;

    /** 任务接口 */
    private TaskRunnable runnable;

    /** 回调接口 */
    private CallBack callback;

    /**
     * 成员长久对象在构造方法是构造
     * 生命周期对象，在onCreate里构造，在onDestroy或onDetach销毁
     */
    public BaseTaskFragment(){
        TAG = this.getClass().getSimpleName();
    }

    /**
     * 持有一个父Activity的引用，以便在任务进度变化和需要返回结果的时候通知它。
     * 在每一次配置变化后，Android Framework会将新创建的Activity的引用传递给我们
     * getActivity();
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    /**
     * 这个方法只会被调用一次，只在这个被保存Fragment第一次被创建的时候
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 在配置变化的时候将这个fragment保存下来
        setRetainInstance(true);

        // 开始任务体
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    if(runnable != null) {
                        String result = runnable.taskRun();
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }else {
                        subscriber.onError(new Throwable("runnable is null"));
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
        .compose(MyRxUtils.<String>handleSchedulers());

        // 开始订阅
        subscription = observable.subscribe(new Action1<String>() {
            @Override
            public void call(String result) {
                LogUtils.e(TAG, "Success = " + result);
                if (callback != null) {
                    callback.OnSuccess(result);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                if (throwable != null) {
                    LogUtils.e(TAG, "Failure = " + throwable.getMessage());
                    if (callback != null) {
                        callback.OnFailure(throwable);
                    }
                }
            }
        }, new Action0() {
            @Override
            public void call() {
                if(subscription!=null && !subscription.isUnsubscribed()){
                    subscription.unsubscribe();
                }
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * 这个会调用
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    /**
     * 这个不会调用
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(subscription!=null && !subscription.isUnsubscribed()){
            subscription.unsubscribe();
        }
    }

    /**
     * 设置回调对象为null，防止我们意外导致Activity实例泄露（leak the Activity instance）
     * 这个会调用
     */
    @Override
    public void onDetach() {
        super.onDetach();

    }

    /**
     * 设置任务接口
     * @param runnable
     */
    public void setRunnable(TaskRunnable runnable) {
        this.runnable = runnable;
    }

    /**
     * 设置回调接口
     * @param callback
     */
    public void setCallback(CallBack callback) {
        this.callback = callback;
    }

    /**
     * 任务接口
     */
    public interface TaskRunnable{
        String taskRun();
    }

    /**
     * 回调接口
     */
    public interface CallBack{
        void OnSuccess(String result);
        void OnFailure(Throwable e);
    }

}
