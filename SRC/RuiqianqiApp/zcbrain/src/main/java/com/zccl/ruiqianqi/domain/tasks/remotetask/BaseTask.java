package com.zccl.ruiqianqi.domain.tasks.remotetask;

import android.content.Context;

/**
 * Created by ruiqianqi on 2017/3/15 0015.
 */

public abstract class BaseTask {

    // 全局上下文
    protected Context mContext;
    // 服务器返回的结果
    protected String result;

    public BaseTask() {

    }

    public BaseTask(String result) {
        this.result = result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setContext(Context context){
        this.mContext = context;
    }
    public abstract void run();
}
