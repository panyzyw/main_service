package com.zccl.ruiqianqi.domain.tasks.localtask;

/**
 * Created by ruiqianqi on 2017/3/15 0015.
 */

public abstract class BaseLocalTask {

    // 服务器返回的结果
    protected String result;

    public BaseLocalTask() {

    }

    public BaseLocalTask(String result) {
        this.result = result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public abstract void run();
}
