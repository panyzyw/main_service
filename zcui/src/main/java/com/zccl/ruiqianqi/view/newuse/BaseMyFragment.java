package com.zccl.ruiqianqi.view.newuse;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxFragment;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.MYUIUtils;
import com.zccl.ruiqianqi.tools.executor.rxutils.MyRxUtils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by ruiqianqi on 2016/9/19 0019.
 *
 * @FragmentArgsInherited 子类使用这个，表示成员变量从父类继承
 *
 * @FragmentWithArgs(inherited = false)子类使用这个，表示成员变量不从父类继承
 */
@FragmentWithArgs
public class BaseMyFragment extends RxFragment {

    /** 类的标志 */
    protected String TAG = null;

    /** 默认参数一，【由外部传入赋值】*/
    @Arg
    String mParam1;
    /** 默认参数二，【由外部传入赋值】*/
    @Arg
    String mParam2;

    /**
     * 表示为可选参数，【由外部传入赋值】
     */
    @Arg(required = false)
    protected int mId;

    /**
     * private fields requires a setter method
     * 【由外部传入赋值】
     */
    @Arg
    private String mTitle;


    /** 全局上下文，【对象由自己创建】*/
    protected Context mContext = null;
    /** Activity窗口，【对象由自己创建】*/
    protected Window mWindow = null;
    /** 手机设备及尺寸信息，【对象由自己创建】*/
    protected DisplayMetrics mDisplayMetrics = null;
    /** 主线程Handler，【对象由自己创建】*/
    protected Handler mHandler = null;

    /** 是否调试UI生命周期 */
    protected boolean debugUI = false;

    /** Fragment当前状态是否可见 */
    protected boolean isVisible;
    /** UI是否已准备好 */
    protected boolean isPrepared;

    /**
     * 成员长久对象在构造方法是构造
     * 生命周期对象，在onCreate里构造，在onDestroy或onDetach销毁
     */
    public BaseMyFragment(){
        TAG = this.getClass().getSimpleName();
    }

    /**
     * 配合viewpager使用时，会预加载什么玩意儿的。
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()) {
            if(debugUI) {
                LogUtils.e(TAG, "getUserVisibleHint=true");
            }
            isVisible = true;

            lazyLoad();

        } else {
            if(debugUI) {
                LogUtils.e(TAG, "getUserVisibleHint=false");
            }
            isVisible = false;

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(debugUI) {
            LogUtils.e(TAG, "onAttach");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // read @Arg fields
        FragmentArgs.inject(this);

        if(debugUI) {
            LogUtils.e(TAG, "onCreate");
        }

        // 当在onCreate()方法中调用了setRetainInstance(true)后，
        // 在Activity重新创建时可以不完全销毁Fragment，
        // Fragment恢复时会跳过onCreate()和onDestroy()方法，
        // 因此不能在onCreate()中放置一些初始化逻辑，因为再次恢复时不走这里了
        //
        // 需要注意的是，要使用这种操作的Fragment不能加入backstack后退栈中。
        // 并且，被保存的Fragment实例不会保持太久，若长时间没有容器承载它，也会被系统回收掉的。
        //
        // viewpager管理fragment，不吃setRetainInstance()这一套，当后台的Fragment过多，
        // 需要回收数据时：onPause  onStop   onDestroyView onDestroy   onDetach
        // 再需要预加载时：onAttach onCreate onViewCreated onActivityCreated onStart onResume
        // 因此viewpager里的fragment成员对象，若不想频繁创建，就要在构造方法中初始化。

        //设置成true是用来在，activity响应配置变化时，保存数据用的
        setRetainInstance(false);

        mContext = getActivity().getApplicationContext();
        mWindow = getActivity().getWindow();
        mDisplayMetrics = MYUIUtils.getMetrics(mContext);
        mHandler = new Handler(Looper.getMainLooper());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if(debugUI) {
            LogUtils.e(TAG, "onCreateView");
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(debugUI) {
            LogUtils.e(TAG, "onViewCreated");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(debugUI) {
            LogUtils.e(TAG, "onActivityCreated");
        }

        isPrepared = true;

        lazyLoad();
    }

    /**
     * Fragment显示之后，再加载
     */
    private void lazyLoad(){
        if(isVisible && isPrepared){
            onLazyLoad();
        }
    }

    /**
     * Fragment显示之后，再加载
     */
    protected void onLazyLoad(){

    }

    /**********************************************************************************************/
    @Override
    public void onStart() {
        super.onStart();
        if(debugUI) {
            LogUtils.e(TAG, "onStart");
        }

        //循环发送数字
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .compose(MyRxUtils.<Long>handleSchedulers())
                .compose(this.<Long>bindUntilEvent(FragmentEvent.STOP))
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        LogUtils.e(TAG, "lifecycle-stop-" + aLong);
                    }
                });
    }

    /*********************************************************************************************/
    @Override
    public void onResume() {
        super.onResume();
        if(debugUI) {
            LogUtils.e(TAG, "onResume");
        }
    }



    /**********************************************************************************************/
    /**
     * Bundle中的数据必须能够被序列化和反序列化
     *
     * Activity被系统杀死时被调用：
     * 1.屏幕方向改变时,Activity被销毁再重建；
     * 2.当前Activity处于后台，系统资源紧张将其杀死；
     * 3.当跳转到其他Activity或者按Home键回到主屏时，该方法也会被调用，系统是为了保存当前View组件的状态.
     *
     * 在onPause之前被调用.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(debugUI) {
            LogUtils.e(TAG, "onSaveInstanceState");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(debugUI) {
            LogUtils.e(TAG, "onPause");
        }
    }

    /**********************************************************************************************/
    @Override
    public void onStop() {
        super.onStop();
        if(debugUI) {
            LogUtils.e(TAG, "onStop");
        }
    }

    /**********************************************************************************************/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(debugUI) {
            LogUtils.e(TAG, "onDestroyView");
        }
    }

    /**
     * 【对象可能还在，下次直接拿这个对象又用，就是一个普通的对象】
     * 【自己new一个对象，并且不保存其引用，然后交给FragmentManager或ViewPager去管理】
     * 手动回收与UI无关的对象
     * 调用了onDestroy, 下次就会调用onCreate
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(debugUI) {
            LogUtils.e(TAG, "onDestroy");
        }
        mHandler = null;

    }

    /**
     * 与Activity脱离关系，回收与UI有关的对象
     */
    @Override
    public void onDetach() {
        super.onDetach();
        if(debugUI) {
            LogUtils.e(TAG, "onDetach");
        }
        isPrepared = false;

    }


    /**
     * Setter method for protected field
     * @param id
     */
    public void setId(int id) {
        this.mId = id;
    }

    /**
     * Setter method for private field
     */
    public void setTitle(String title) {
        this.mTitle = title;
    }
}
