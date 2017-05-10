package com.zccl.ruiqianqi.domain.interactor.base;

import android.content.Context;

import com.zccl.ruiqianqi.tools.executor.IMainThread;
import com.zccl.ruiqianqi.tools.executor.IScheduleTask;
import com.zccl.ruiqianqi.tools.executor.impl.MyMainThread;
import com.zccl.ruiqianqi.tools.executor.impl.MyScheduleTask;

/**
 * User Case 基类
 */
public abstract class BaseInteractor {

    /** 所在的上下文 */
    protected final Context mContext;
    /** 子线程任务调度 */
    protected final IScheduleTask scheduleTask;
    /** 主线程响应调度 */
    protected final IMainThread mainThread;

    public BaseInteractor(Context context) {
        this.mContext = context;
        this.scheduleTask = MyScheduleTask.getInstance();
        this.mainThread = MyMainThread.getInstance();
    }

    public BaseInteractor(Context context, IScheduleTask scheduleTask, IMainThread mainThread) {
        this.mContext = context;
        this.scheduleTask = scheduleTask;
        this.mainThread = mainThread;
    }

}
