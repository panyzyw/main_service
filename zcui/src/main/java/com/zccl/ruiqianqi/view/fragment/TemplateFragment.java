package com.zccl.ruiqianqi.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by ruiqianqi on 2016/11/24 0024.
 */

public class TemplateFragment extends BaseCompatFragment {

    /** 类标志 */
    public static String TAG = TemplateFragment.class.getSimpleName();
    /** 回调给ACTIVITY的接口 */
    private FragmentListener mListener;

    /**
     * 构造方法
     */
    public TemplateFragment(){
    }

    /**
     * 默认构造对象的方法
     * @param param1
     * @param param2
     * @return
     */
    public static TemplateFragment newInstance(String param1, String param2) {
        TemplateFragment fragment = new TemplateFragment();
        fragment.mParam1 = param1;
        fragment.mParam2 = param2;

        Bundle args = new Bundle();
        args.putString(KEY_PARAM1, param1);
        args.putString(KEY_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /************************************ 初始化对象***********************************************/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /************************************ 初始化UI ************************************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(0, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initView();
    }

    /**
     * 初始化数据
     */
    private void initData(){

    }

    /**
     * 初始化UI
     */
    private void initView(){
        //setEnterTransition();
        //setExitTransition();
        //transaction.setCustomAnimations();
    }

    /**********************************************************************************************/
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**********************************************************************************************/
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

    /**********************************【调用对象的销毁方法】**************************************/
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /*************************************【懒加载】***********************************************/
    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
    }

    /*********************************【与UI无关的方法】*******************************************/


    /*********************************【与UI有关的方法】*******************************************/


    /*********************************【与事件有关的方法】*****************************************/


    /********************************【与Activity通信接口】****************************************/
    /**
     * 设置回调监听【由Activity调用】
     * @param mListener
     */
    public void setFragmentListener(FragmentListener mListener){
        this.mListener = mListener;
    }

    /**
     * 回调的调用方法
     * @param cmd
     * @param obj
     */
    public void startCallBack(int cmd, Object obj){
        if (mListener != null) {
            mListener.onFragmentInteraction(cmd, obj);
        }
    }

    /**
     * Fragment回调给ACTIVITY的接口
     */
    public interface FragmentListener {
        void onFragmentInteraction(int cmd, Object obj);
    }

}
