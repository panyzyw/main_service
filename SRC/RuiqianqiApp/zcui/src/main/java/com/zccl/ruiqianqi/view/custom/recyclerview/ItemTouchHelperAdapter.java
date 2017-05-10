package com.zccl.ruiqianqi.view.custom.recyclerview;

import android.support.v7.widget.RecyclerView;

/**
 * Created by zc on 2016/3/31.
 */
public interface ItemTouchHelperAdapter {

    // 移动动作，交换位置
    void onItemMove(RecyclerView.ViewHolder source, RecyclerView.ViewHolder target, int fromPosition, int toPosition);

    // 删除动作
    void onItemDismiss(int position);

    // 激活状态取消了
    void onItemActiveCancel();
}
