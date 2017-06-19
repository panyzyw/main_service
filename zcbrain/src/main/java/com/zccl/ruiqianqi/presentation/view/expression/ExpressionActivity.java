package com.zccl.ruiqianqi.presentation.view.expression;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.zccl.ruiqianqi.brain.R;
import com.zccl.ruiqianqi.brain.eventbus.MindBusEvent;
import com.zccl.ruiqianqi.brain.voice.RobotVoice;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.SystemUtils;
import com.zccl.ruiqianqi.view.activity.BaseCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;

/**
 * Created by ruiqianqi on 2017/5/11 0011.
 *
 * 包含关键字HideFloat，就隐藏悬浮按钮
 */
public class ExpressionActivity extends BaseCompatActivity {

    private OverlayDialog mOverlayDialog;

    /** 手势监听 */
    protected GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // debugUI = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expression_activity);
        ButterKnife.bind(this);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        initData();
        initView();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        EventBus.getDefault().register(this);
        // 手势监控
        mDetector = new GestureDetectorCompat(this, gestureListener);
    }

    /**
     * 初始化UI
     */
    private void initView() {

    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //hide();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
        //return super.onTouchEvent(event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        show();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        hide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    /*********************************【事件总线】*************************************************/
    /**
     * 接收到结束大表情事件，从
     * {@link RobotVoice#notifyChange} 发过来的
     * @param expressionEvent
     */
    @Subscribe(threadMode = ThreadMode.POSTING, priority = 1)
    public void OnExpressionEvent(MindBusEvent.ExpressionEvent expressionEvent){
        hide();
    }

    /**********************************************************************************************/
    /**
     * 显示表情
     */
    private void show() {
        // 关闭系统锁屏服务
        SystemUtils.disableKeyguard(this);

        if (null == mOverlayDialog) {
            mOverlayDialog = new OverlayDialog(this);
            mOverlayDialog.show();
        }
    }

    /**
     * 隐藏表情
     */
    private void hide() {
        // 打开系统锁屏服务
        SystemUtils.enableKeyguard(this);

        if (null != mOverlayDialog) {
            mOverlayDialog.dismiss();
            mOverlayDialog = null;
        }

        finish();
    }

    /**********************************************************************************************/
    /**
     * Create overlay dialog for lockedScreen to disable hardware buttons
     * TYPE_SYSTEM_ALERT跟TYPE_SYSTEM_OVERLAY的区别
     * 以上面的代码为例，system_alert窗口可以获得焦点，响应操作
     * system_overlay窗口显示的时候焦点在后面的Activity上，仍旧可以操作后面的Activity
     */
    private static class OverlayDialog extends AlertDialog {

        public OverlayDialog(Activity activity) {
            super(activity, R.style.OverlayDialog);
            try {
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;// 解决表情遮挡的问题
                params.dimAmount = 0.0F;
                params.width = 0;
                params.height = 0;
                params.gravity = Gravity.BOTTOM;
                getWindow().setAttributes(params);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN,
                                0xffffff);
                setOwnerActivity(activity);
                setCancelable(false);
            } catch (Exception e) {

            }
        }

        // consume touch events
        public final boolean dispatchTouchEvent(MotionEvent motionevent) {
            return true;
        }

    }

    /**
     * 手势监听
     */
    private GestureDetector.OnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

        /**
         * 确定了，就是单击
         * @param e
         * @return
         */
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            LogUtils.e(TAG, "onSingleTapConfirmed");
            return super.onSingleTapConfirmed(e);
        }

        /**
         * 双击
         * @param e
         * @return
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            LogUtils.e(TAG, "onDoubleTap");
            hide();
            return true;
        }

        /**
         * 长按
         * @param e
         */
        @Override
        public void onLongPress(MotionEvent e) {
            LogUtils.e(TAG, "onLongPress");
        }

    };
}
