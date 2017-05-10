package com.zccl.ruiqianqi.view.custom.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.zccl.ruiqianqi.tools.LogUtils;

/**
 * Created by Administrator on 2016/11/20.
 */

public class MyRecyclerView extends RecyclerView {

    private static String TAG = MyRecyclerView.class.getSimpleName();

    /** 设置选择项为-1 */
    private int selectedIndex = -1;

    public MyRecyclerView(Context context) {
        this(context, null);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        // 启用子视图排序功能
        setChildrenDrawingOrderEnabled(true);
    }

    /**
     * 设置当前选择项
     * @param selectedIndex
     */
    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
}
