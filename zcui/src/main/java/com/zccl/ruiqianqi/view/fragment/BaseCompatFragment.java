package com.zccl.ruiqianqi.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.trello.rxlifecycle.components.support.RxFragment;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.MYUIUtils;
import com.zccl.ruiqianqi.tools.executor.asynctask.MyAsyncTask;
import com.zccl.ruiqianqi.view.activity.BaseCompatActivity;
import com.zccl.ruiqianqi.view.dialog.MyDialogFragment;

/**
 * Created by zc on 2015/10/22.
 *
 * FragmentTransaction tx = fm.beginTransaction();
 *
 * tx.add(exitFragment);
 * 从Activity中移除一个Fragment，如果被移除的Fragment没有添加到回退栈（回退栈后面会详细说），这个Fragment实例将会被销毁。
 * tx.remove(exitFragment);
 *
 * transaction.hide(exitFragment);
 * transaction.show(exitFragment);
 *
 * 触发顺序:
 * detach()->onPause()->onStop()->onDestroyView()
 * attach()->onCreateView()->onViewCreated()->onActivityCreated()->onStart()->onResume()
 *
 * 在不考虑回退栈的情况下，remove会销毁整个Fragment实例，而detach则只是销毁其视图结构，实例并不会被销毁。但是视图层次依然会被销毁，即会调用onDestoryView。
 * tx.detach(exitFragment);
 * 重建view视图，附加到UI上并显示，调用onCreateView。
 * tx.attach(exitFragment)
 *
 * 将当前的事务添加到了回退栈，所以FragmentOne实例不会被销毁，
 * 但是视图层次依然会被销毁，即会调用onDestoryView和onCreateView
 * addToBackStack(null);
 *
 * tx.commit();
 *
 * public void setRetainInstance(boolean retain)
 * 该方法用于设置在Activity对象被重建（如配置的变化）时，是否应该保留该Fragment对象的实例。它仅适用于没有在回退堆栈中Fragment对象。
 * 如果设置为true，那么该Fragment对象的生命周期与创建Activity时有些不同：
 *
 * 1.  onDestory()方法不会被调用（但是onDetach()方法会依然被调用，因为该Fragment对象会从当前的Activity中被解除绑定）。
 * 2.  onCreate(Bundle)方法不会被调用，因为该Fragment对象没有被重建；
 * 3.  onAttach(Activity)和onActivityCreated(Bundle)方法会依然被调用。
 *
 * Fragment分为两部分：实例与UI
 * 实例:
 * UI:
 *
 * remove(FA)
 * add(R.id.id_content, FB, FB.TAG)---------------结果就是FA[实例+UI]全部销毁
 *
 * replace(R.id.id_content, FB, FB.TAG)-----------结果就是FA[实例+UI]全部销毁
 *
 * remove(FA)
 * add(R.id.id_content, FB, FB.TAG)
 * addToBackStack(null)---------------------------结果就是FA[实例]保存，UI销毁，调用onDestoryView和onCreateView
 *
 * replace(R.id.id_content, FB, FB.TAG)
 * addToBackStack(null)---------------------------结果就是FA[实例]保存，UI销毁，调用onDestoryView和onCreateView
 *
 * hide(FA)
 * add(R.id.id_content, FB, FB.TAG)
 * addToBackStack(null)---------------------------结果就是FA[实例+UI]全部保存
 *
 */
public class BaseCompatFragment extends RxFragment {

    /** 类的标志 */
    protected String TAG = null;
    /** 默认参数一的key */
    public static final String KEY_PARAM1 = "param1";
    /** 默认参数二的key */
    public static final String KEY_PARAM2 = "param2";

    /** 默认参数一，【由外部传入赋值】 */
    protected String mParam1 = null;
    /** 默认参数二，【由外部传入赋值】 */
    protected String mParam2 = null;

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
     *
     */
    public BaseCompatFragment(){
        TAG = this.getClass().getSimpleName();

        Bundle args = new Bundle();
        args.putString(KEY_PARAM1, "hello");
        args.putString(KEY_PARAM1, "world");
        setArguments(args);
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
                LogUtils.e(TAG+"-"+mParam1, "getUserVisibleHint=true");
            }
            isVisible = true;

            lazyLoad();

        } else {
            if(debugUI) {
                LogUtils.e(TAG+"-"+mParam1, "getUserVisibleHint=false");
            }
            isVisible = false;

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(debugUI) {
            LogUtils.e(TAG+"-"+mParam1, "onAttach");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(debugUI) {
            LogUtils.e(TAG+"-"+mParam1, "onCreate");
        }

        //早期使用的数据保存方式
        if(savedInstanceState != null) {
            mParam1 = savedInstanceState.getString(KEY_PARAM1);
            mParam2 = savedInstanceState.getString(KEY_PARAM2);
        }

        //现在使用的数据保存方式
        if (getArguments() != null) {
            mParam1 = getArguments().getString(KEY_PARAM1);
            mParam2 = getArguments().getString(KEY_PARAM2);
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
        mDisplayMetrics = MYUIUtils.getMetrics(getActivity());
        mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                return false;
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if(debugUI) {
            LogUtils.e(TAG+"-"+mParam1, "onCreateView");
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(debugUI) {
            LogUtils.e(TAG+"-"+mParam1, "onViewCreated");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(debugUI) {
            LogUtils.e(TAG+"-"+mParam1, "onActivityCreated");
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
            LogUtils.e(TAG+"-"+mParam1, "onStart");
        }
    }

    /**********************************************************************************************/
    @Override
    public void onResume() {
        super.onResume();
        if(debugUI) {
            LogUtils.e(TAG+"-"+mParam1, "onResume");
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
            LogUtils.e(TAG+"-"+mParam1, "onSaveInstanceState");
        }

        //早期使用的数据保存方式
        if(outState != null) {
            outState.putString(KEY_PARAM1, mParam1);
            outState.putString(KEY_PARAM2, mParam2);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(debugUI) {
            LogUtils.e(TAG+"-"+mParam1, "onPause");
        }
    }

    /**********************************************************************************************/
    @Override
    public void onStop() {
        super.onStop();
        if(debugUI) {
            LogUtils.e(TAG+"-"+mParam1, "onStop");
        }
    }

    /**********************************************************************************************/
    /**
     * 销毁UI
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(debugUI) {
            LogUtils.e(TAG+"-"+mParam1, "onDestroyView");
        }
    }

    /**
     * 手动销毁成员
     * 【对象可能还在，下次直接拿这个对象又用，就是一个普通的对象】
     * 【自己new一个对象，并且不保存其引用，然后交给FragmentManager或ViewPager去管理】
     * 手动回收与UI无关的对象
     * 调用了onDestroy, 下次就会调用onCreate
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(debugUI) {
            LogUtils.e(TAG+"-"+mParam1, "onDestroy");
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
            LogUtils.e(TAG+"-"+mParam1, "onDetach");
        }
        isPrepared = false;

    }

    /**********************************************************************************************/
    /**
     * 主线程消息处理
     * @param msg
     * @return
     */
    protected boolean handMessage(Message msg){
        return false;
    }

    /****************************************自定义方法********************************************/
    /**
     * Shows a Toast message.
     * @param msg
     */
    protected void showToastMessage(String msg) {
        MYUIUtils.showToast(getActivity(), msg+"");
    }

    /**
     * 取消任务
     * @param task
     */
    protected void cancelTask(MyAsyncTask task){
        //UI结束时，任务没完成，就直接取消
        if(task != null){
            if(task.getStatus() != MyAsyncTask.Status.FINISHED){
                task.cancel(true);
            }
        }
    }

    /**
     * 当前Fragment是否是正常使用的
     * @return
     */
    protected boolean isAvailable(){
        return isAdded() && !isHidden() && !isDetached();
    }

    /**
     * 显示对话框
     * @param title
     * @param content
     */
    protected void showDialog(MyDialogFragment.MODE mode, String title, String content, MyDialogFragment.NoticeDialogListener dialogListener){
        ((BaseCompatActivity)getActivity()).showDialog(mode, title, content, dialogListener);
    }
}
