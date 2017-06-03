package com.zccl.ruiqianqi.brain.service;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.zccl.ruiqianqi.brain.R;
import com.zccl.ruiqianqi.mind.eventbus.MainBusEvent;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.MYUIUtils;
import com.zccl.ruiqianqi.tools.anim.FrameAnimation;
import com.zccl.ruiqianqi.utils.AppUtils;
import com.zccl.ruiqianqi.utils.Constant;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by ruiqianqi on 2017/5/4 0004.
 */

public class FloatListen {

    public static final String TAG = FloatListen.class.getSimpleName();
    
    // 全局上下文
    private final Context mContext;
    // 悬浮按钮相关
    private WindowManager windowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private Button floatBtnView;

    // 非跟随音频的监听动画
    private FrameAnimation mFrameAnimation;

    /** 手势监听 */
    protected GestureDetectorCompat mDetector;

    protected FloatListen(Context context){
        this.mContext = context.getApplicationContext();
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        // 手势监控
        mDetector = new GestureDetectorCompat(mContext, gestureListener);

        // 构造悬浮按钮
        windowManager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        floatBtnView = MYUIUtils.createFloatView(mContext, mLayoutParams, Constant.MONITOR[0]);
        floatBtnView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = MotionEventCompat.getActionMasked(event);
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_UP:
                        zoomIn(0.8f);
                        break;

                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;

                    case MotionEvent.ACTION_POINTER_UP:
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        break;
                }
                return mDetector.onTouchEvent(event);
            }
        });
    }

    /**
     * 添加悬浮按钮
     */
    protected void addView(){
        hideView();
        windowManager.addView(floatBtnView, mLayoutParams);
    }

    /**
     * 隐藏悬浮按钮
     */
    protected void hideView(){
        zoomIn(0.8f);
        floatBtnView.setVisibility(View.GONE);
        floatBtnView.setEnabled(false);
    }

    /**
     * 显示悬浮按钮
     */
    protected void showView(){
        zoomIn(0.8f);
        floatBtnView.setVisibility(View.VISIBLE);
        floatBtnView.setEnabled(true);
    }

    /**
     * 移除悬浮按钮
     */
    protected void removeView(){
        windowManager.removeView(floatBtnView);
    }

    /**
     * 缩小动画，如果当前 scaleX 不是1.0f
     * 动画会先调整到1.0f，再缩小到0.8f
     */
    private void zoomIn(float from){
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(floatBtnView, "scaleX", from, 0.8f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(floatBtnView, "scaleY", from, 0.8f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(anim1).with(anim2);
        animSet.setTarget(floatBtnView);
        animSet.start();
    }

    /**
     * 放大动画
     */
    private void zoomOut() {
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(floatBtnView, "scaleX", 0.8f, 1.1f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(floatBtnView, "scaleY", 0.8f, 1.1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(anim1).with(anim2);
        animSet.setTarget(floatBtnView);
        animSet.start();
    }

    /**
     * 开始监听
     */
    protected void start(){
        showView();
        Animator animatorUp = AnimatorInflater.loadAnimator(mContext, R.animator.listen_slide_in_up);
        Animator animatorDown = AnimatorInflater.loadAnimator(mContext, R.animator.listen_slide_in_down);
        ObjectAnimator animatorUp2 = ObjectAnimator.ofFloat(floatBtnView, "Y", 30f, 0f);
        animatorUp2.setDuration(100);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(animatorUp).before(animatorDown).before(animatorUp2);
        animSet.setTarget(floatBtnView);
        animSet.start();
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(!Constant.IS_FOLLOW_VOLUME) {
                    if(null != mFrameAnimation){
                        mFrameAnimation.stop();
                    }
                    mFrameAnimation = new FrameAnimation(floatBtnView, Constant.MONITOR, Constant.INTERNAL, 300);
                    mFrameAnimation.playConstant(0);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 监听过程
     * @param volume
     */
    protected void going(int volume){
        if(Constant.IS_FOLLOW_VOLUME) {
            if (volume >= 0 && volume <= 30) {
                floatBtnView.setBackgroundResource(Constant.MONITOR[volume]);
            }
        }
    }

    /**
     * 结束监听
     */
    protected void end(){
        if(!Constant.IS_FOLLOW_VOLUME) {
            if (null != mFrameAnimation) {
                mFrameAnimation.stop();
                mFrameAnimation = null;
            }
        }else {
            floatBtnView.setBackgroundResource(Constant.MONITOR[0]);
        }
        hideView();
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
            AppUtils.sendStopListenEvent(TAG);
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
            return true;
        }

        /**
         * 长按
         * @param e
         */
        @Override
        public void onLongPress(MotionEvent e) {
            LogUtils.e(TAG, "onLongPress");
            zoomOut();
        }

    };
}
