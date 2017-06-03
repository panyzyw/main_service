package com.zccl.ruiqianqi.presenter.base;

import android.content.Context;

import com.zccl.ruiqianqi.mind.app.BaseApplication;
import com.zccl.ruiqianqi.tools.executor.IMainThread;
import com.zccl.ruiqianqi.tools.executor.IScheduleTask;
import com.zccl.ruiqianqi.tools.executor.impl.MyMainThread;
import com.zccl.ruiqianqi.tools.executor.impl.MyScheduleTask;

/**
 * Created by ruiqianqi on 2016/7/18 0018.
 */
public abstract class BasePresenter {

    /** 所在的上下文 */
    protected final Context mContext;
    /** 子线程任务调度 */
    protected IScheduleTask scheduleTask;
    /** 主线程响应调度 */
    protected IMainThread mainThread;

    protected BasePresenter(){
        this.mContext = BaseApplication.getContext();
        initBase();
    }

    protected BasePresenter(Context context){
        this.mContext = context.getApplicationContext();
        initBase();
    }

    private void initBase(){
        this.scheduleTask = MyScheduleTask.getInstance();
        this.mainThread = MyMainThread.getInstance();
    }

}
