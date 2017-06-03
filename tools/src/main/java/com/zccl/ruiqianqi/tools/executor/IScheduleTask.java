package com.zccl.ruiqianqi.tools.executor;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by ruiqianqi on 2016/7/18 0018.
 */
public interface IScheduleTask {
    /**
     * 任务的执行结果
     */
    enum RESULT{
        SUCCESS,
        FAILURE
    }

    /**
     * 得到任务调度器
     * @return
     */
    ScheduledThreadPoolExecutor getSchedule();

    /**
     * 立即执行任务
     * @param command
     */
    void execute(Runnable command);

    /**
     * 立即执行任务
     * @param command 要执行的任务
     * @param result  该 Future 的 get 方法在成功完成时将会返回给定的结果
     * @return
     */
    Future<RESULT> submit(Runnable command, RESULT result);

    /**
     * 延时执行任务
     * @param command
     * @param delay
     * @param unit
     */
    ScheduledFuture<?> execute(Runnable command, long delay, TimeUnit unit);

    /**
     * 固定周期执行任务，不包含任务时间
     * @param command
     * @param initialDelay
     * @param period
     * @param unit
     * @return
     */
    ScheduledFuture<?> executeAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit);

    /**
     * 固定周期执行任务，包含任务时间在内
     * @param command
     * @param initialDelay
     * @param period
     * @param unit
     * @return
     */
    ScheduledFuture<?> executeWithFixedDelay(Runnable command, long initialDelay, long period, TimeUnit unit);

}
