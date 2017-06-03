package com.zccl.ruiqianqi.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.zccl.ruiqianqi.zcui.R;
import com.zccl.ruiqianqi.tools.LogUtils;

/**
 * Created by zc on 2015/10/28.
 */
public class BaseCompatDialogFragment extends DialogFragment {

    /** 默认参数一的key */
    public static final String ARG_PARAM1 = "title";
    /** 默认参数二的key */
    public static final String ARG_PARAM2 = "content";

    /** 类的标志 */
    protected String TAG = null;
    /** 默认参数一标题 */
    protected String mTitle = null;
    /** 默认参数二内容 */
    protected String mContent = null;

    /** 对话框窗口 */
    protected Window mWindow;
    /** 手机设备及尺寸信息 */
    protected DisplayMetrics mDisplayMetrics =null;
    /** 是否调试UI生命周期 */
    private boolean debugUI = false;

    public BaseCompatDialogFragment() {
        TAG = this.getClass().getSimpleName();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(debugUI) {
            LogUtils.e(TAG, "onAttach");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(debugUI) {
            LogUtils.e(TAG, "onCreate");
        }
        setDialogStyle(STYLE_NO_TITLE, 0, 0);
        setDialogStyle(STYLE_NORMAL, android.R.style.Theme_Holo_Dialog_NoActionBar, 0);
        setDialogStyle(STYLE_NORMAL, R.style.DialogTheme_Holo, 0);
        setDialogStyle(STYLE_NORMAL, R.style.ZcclCompatLightDialog, 0);

        //构造及初始化对象
        mDisplayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if(debugUI) {
            LogUtils.e(TAG, "onCreateDialog");
        }

        mWindow = dialog.getWindow();
        mWindow.requestFeature(Window.FEATURE_NO_TITLE);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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
    }

    /*********************************************************************************************/
    @Override
    public void onStart() {
        super.onStart();
        if(debugUI) {
            LogUtils.e(TAG, "onStart");
        }

        //设置窗口大小
        mWindow.setLayout(mDisplayMetrics.widthPixels / 2, mWindow.getAttributes().height);

    }

    /*********************************************************************************************/
    @Override
    public void onResume() {
        super.onResume();
        if(debugUI) {
            LogUtils.e(TAG, "onResume");
        }
    }

    /*********************************************************************************************/
    @Override
    public void onPause() {
        super.onPause();
        if(debugUI) {
            LogUtils.e(TAG, "onPause");
        }
    }

    /*********************************************************************************************/
    @Override
    public void onStop() {
        super.onStop();
        if(debugUI) {
            LogUtils.e(TAG, "onStop");
        }
    }

    /*********************************************************************************************/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(debugUI) {
            LogUtils.e(TAG, "onDestroyView");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(debugUI) {
            LogUtils.e(TAG, "onDestroy");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(debugUI) {
            LogUtils.e(TAG, "onDetach");
        }
    }


    /************************************自定义方法***********************************************/
    /**
     * 设置村标题
     * @param title
     */
    public void setTitle(String title){
        this.mTitle = title;
    }

    /**
     * 设置内容
     * @param mContent
     */
    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    /**
     * 设置对话框风格
     * @param style 风格
     * @param theme 主题
     * @param flag  自定义
     */
    protected void setDialogStyle(int style, int theme, int flag){
        setStyle(style, theme);
    }

    /**
     * 当前Fragment是否是正常使用的
     * @return
     */
    protected boolean isAvailable(){
        return isAdded() && !isHidden() && !isDetached();
    }
}
