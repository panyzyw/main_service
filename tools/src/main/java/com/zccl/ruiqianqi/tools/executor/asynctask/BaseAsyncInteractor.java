package com.zccl.ruiqianqi.tools.executor.asynctask;

import android.content.Context;

/**
 * User Case 基类
 */
public abstract class BaseAsyncInteractor {

    /** 所在的上下文 */
    protected final Context mContext;

    public BaseAsyncInteractor(Context context) {
        this.mContext = context;
    }

    /**
     * 根据指令ID，来进行任务
     * @param cmd
     * @param params
     * @return
     */
    public MyLinkData execute(int cmd, Object... params){
        return null;
    }

}
