package com.zccl.ruiqianqi.view.custom.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Created by zc on 2016/3/24.
 *
 * 在 ZcclCompatNoActionBar style中配置的
 */
public class DividerGridItemDecoration extends RecyclerView.ItemDecoration {

    /** 行间距，列间距，就是这个东西设定的 */
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private Drawable mDivider;

    public DividerGridItemDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        // 画横向间隔
        drawHorizontal(c, parent);
        // 画纵向间隔
        drawVertical(c, parent);

    }

    /**
     * 返回列数
     * @param parent
     * @return
     */
    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {

            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }

    /**
     * 画横向间隔
     * @param c
     * @param parent
     */
    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin + mDivider.getIntrinsicWidth();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    /**
     * 画纵向间隔
     * @param c
     * @param parent
     */
    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicWidth();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    /**
     * 是不是第一列
     * @param parent
     * @param pos           当前位置
     * @param spanCount        列数
     * @param childCount       总数
     * @return
     */
    private boolean isFirstColumn(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if (pos % spanCount == 0)// 如果是第一列，则加宽左边
            {
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {

            }
            // StaggeredGridLayoutManager 且横向滚动
            else {

            }
        }
        return false;
    }

    /**
     * 是不是最后一列
     * @param parent
     * @param pos           当前位置
     * @param spanCount        列数
     * @param childCount       总数
     * @return
     */
    private boolean isLastColumn(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
            {
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {

            }
            // StaggeredGridLayoutManager 且横向滚动
            else {

            }
        }
        return false;
    }

    /**
     * 是不是第一行
     * @param parent
     * @param pos           当前位置
     * @param spanCount        列数
     * @param childCount       总数
     * @return
     */
    private boolean isFirstRaw(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if (pos < spanCount) {
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {

            }
            // StaggeredGridLayoutManager 且横向滚动
            else{

            }
        }
        return false;
    }

    /**
     * 是不是最后一行
     * @param parent
     * @param pos           当前位置
     * @param spanCount        列数
     * @param childCount       总数
     * @return
     */
    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            childCount = childCount - childCount % spanCount;
            if (pos >= childCount)// 如果是最后一行，则不需要绘制底部
                return true;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {

            }
            // StaggeredGridLayoutManager 且横向滚动
            else{

            }
        }
        return false;
    }


    /**
     *
     * @param outRect  这个区域不是用来画的，是用来计算的，左：就是左边距离，右：就是右边距离
     * @param itemPosition
     * @param parent
     */
    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        // 列数
        int spanCount = getSpanCount(parent);
        // 总数
        int childCount = parent.getAdapter().getItemCount();

        if (isFirstRaw(parent, itemPosition, spanCount, childCount)){
            outRect.top = mDivider.getIntrinsicHeight();
            outRect.bottom = mDivider.getIntrinsicHeight()/2;
        }else if (isLastRaw(parent, itemPosition, spanCount, childCount)){
            outRect.top = mDivider.getIntrinsicHeight()/2;
            outRect.bottom = mDivider.getIntrinsicHeight();
        }else {
            outRect.top = mDivider.getIntrinsicHeight()/2;
            outRect.bottom = mDivider.getIntrinsicHeight()/2;
        }

        if(isFirstColumn(parent, itemPosition, spanCount, childCount)){
            outRect.left = mDivider.getIntrinsicWidth();
            outRect.right = mDivider.getIntrinsicWidth()/2;
        }
        else if (isLastColumn(parent, itemPosition, spanCount, childCount)){
            outRect.left = mDivider.getIntrinsicWidth()/2;
            outRect.right = mDivider.getIntrinsicWidth();
        }
        else {
            outRect.left = mDivider.getIntrinsicWidth()/2;
            outRect.right = mDivider.getIntrinsicWidth()/2;
        }

        /*outRect.set(mDivider.getIntrinsicWidth()/2, mDivider.getIntrinsicHeight()/2,
                mDivider.getIntrinsicWidth()/2, mDivider.getIntrinsicHeight()/2);*/
        /*
        outRect.set(mDivider.getIntrinsicWidth(),
                mDivider.getIntrinsicHeight(),
                mDivider.getIntrinsicWidth(),
                mDivider.getIntrinsicHeight());
                */
    }
}