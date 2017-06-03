package com.zccl.ruiqianqi.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.zccl.ruiqianqi.zcui.R;

/**
 * Created by zc on 2015/11/3.
 * 第一次点击是获取焦点，
 * 第二次才是点击事件
 * 因为增加了
 * android:focusable="true"
 * android:focusableInTouchMode="true"
 */
public class HomeItemContainer extends RelativeLayout {

    private Rect mBound;
    private Drawable mDrawable;
    private Rect mRect;
    private int boardWidth = 25;

    private Animation scaleSmallAnimation;
    private Animation scaleBigAnimation;
    private OnMyFocusListener onMyFocusListener;

    public HomeItemContainer(Context context) {
        super(context);
        init();
    }

    public HomeItemContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public HomeItemContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    protected void init() {
        setWillNotDraw(false);
        mRect = new Rect();
        mBound = new Rect();
        mDrawable = getResources().getDrawable(R.drawable.item_selected);
        setChildrenDrawingOrderEnabled(true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*
        if (hasFocus()) {
            super.getDrawingRect(mRect);
            mBound.set(-boardWidth+mRect.left, -boardWidth+mRect.top, boardWidth+mRect.right, boardWidth+mRect.bottom);
            mDrawable.setBounds(mBound);

            canvas.save();
            mDrawable.draw(canvas);
            canvas.restore();
        }
        */
    }

    /**
     * 注意onFocusChanged方法，为防止item被其他item遮挡，先调用bringToFront方法，使此item处于最上层，
     * 之后调用父view的方法进行重新绘制，其实注意一点，item必须处于同一父view中，否则requestLayout和invalidate可能会不起作用，
     * 只适用于RelativeLayout布局，经测试LinearLayout不适用。
     * @param gainFocus
     * @param direction
     * @param previouslyFocusedRect
     */
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {

        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
            if(onMyFocusListener!=null) {
                onMyFocusListener.onFocusChanged(this, gainFocus);
            }
            //setBackgroundResource(R.drawable.item_selected);
            bringToFront();
            getRootView().requestLayout();
            getRootView().invalidate();
            zoomOut();

        } else {
            if(onMyFocusListener!=null) {
                onMyFocusListener.onFocusChanged(this, gainFocus);
            }
            //setBackgroundResource(android.R.color.transparent);
            zoomIn();
        }
    }

    /**
     * 缩小动画
     */
    private void zoomIn() {
        if (scaleSmallAnimation == null) {
            scaleSmallAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_scale_small_normal);
        }
        startAnimation(scaleSmallAnimation);
    }

    /**
     * 放大动画
     */
    private void zoomOut() {
        if (scaleBigAnimation == null) {
            scaleBigAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_scale_big_selected);
        }
        startAnimation(scaleBigAnimation);
    }

    public void setOnMyFocusListener(HomeItemContainer.OnMyFocusListener onMyFocusListener) {
        this.onMyFocusListener = onMyFocusListener;
    }

    public interface OnMyFocusListener{
        void onFocusChanged(View view, boolean gainFocus);
    }
}