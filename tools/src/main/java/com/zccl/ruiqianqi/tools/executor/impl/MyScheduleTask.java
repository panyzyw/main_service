package com.zccl.ruiqianqi.tools.executor.impl;


import com.zccl.ruiqianqi.tools.executor.IScheduleTask;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ruiqianqi on 2016/7/15 0015.
 */
public class MyScheduleTask implements IScheduleTask {

    /** 设备CPU核数 */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /** 如果池中的实际线程数小于corePoolSize,无论是否其中有空闲的线程，都会给新的任务产生新的线程 */
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    /**
     * 如果池中的线程数>corePoolSize and <maximumPoolSize,而又有空闲线程，就给新任务使用空闲线程，如没有空闲线程，则产生新线程
     * 如果池中的线程数＝maximumPoolSize，则有空闲线程使用空闲线程，否则新任务放入workQueue。（线程的空闲只有在workQueue中不再有任务时才成立）
     * 【DelayedWorkQueue 队列大小为16】
     * */
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    /**
     * when the number of threads is greater than the core,
     * this is the maximum time that excess idle threads will wait for new tasks before terminating.
     */
    private static final int KEEP_ALIVE = 10;

    /** 单例引用 */
    private static MyScheduleTask instance = null;

    /** 延时调度器 */
    private ScheduledThreadPoolExecutor executor;

    /**
     * 构造方法
     */
    private MyScheduleTask(){
        // The default rejected execution handler
        executor = new ScheduledThreadPoolExecutor(CORE_POOL_SIZE, sThreadFactory);
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaximumPoolSize(MAXIMUM_POOL_SIZE);
        executor.setKeepAliveTime(KEEP_ALIVE, TimeUnit.SECONDS);
        executor.getQueue();
        // 允许核心线程数超时
        executor.allowCoreThreadTimeOut(true);

        // 设置有关在此执行程序已 shutdown 的情况下是否继续执行【现有定期任务】的策略
        executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        // 设置有关在此执行程序已 shutdown 的情况下是否继续执行【现有延迟任务】的策略
        executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
    }

    /**
     * 用这个用话，instance不需要用volatile修饰
     * @return
     */
    public static MyScheduleTask getInstance() {
        if(instance == null) {
            synchronized(MyScheduleTask.class) {
                MyScheduleTask temp = instance;
                if(temp == null) {
                    temp = new MyScheduleTask();
                    instance = temp;
                }
            }
        }
        return instance;
    }

    /**
     * 返回可延时精确控制的线程池调度器
     * @return
     */
    @Override
    public ScheduledThreadPoolExecutor getSchedule(){
        return executor;
    }

    /**
     * 立即执行任务
     * @param command
     */
    @Override
    public void execute(Runnable command){
        executor.execute(command);
    }

    /**
     * 立即执行任务
     * @param command 要执行的任务
     * @param result  该 Future 的 get 方法在成功完成时将会返回给定的结果
     * @return
     */
    @Override
    public Future<RESULT> submit(Runnable command, RESULT result){
        return executor.submit(command, result);
    }

    /**
     * 延时执行任务
     * @param command
     * @param delay
     * @param unit
     */
    @Override
    public ScheduledFuture<?> execute(Runnable command, long delay, TimeUnit unit){
        return executor.schedule(command, delay, unit);
    }

    /**
     * 固定周期执行任务，不包含任务时间
     * @param command
     * @param initialDelay
     * @param period
     * @param unit
     * @return
     */
    @Override
    public ScheduledFuture<?> executeAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit){
        return executor.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    /**
     * 固定周期执行任务，包含任务时间在内
     * @param command
     * @param initialDelay
     * @param period
     * @param unit
     * @return
     */
    @Override
    public ScheduledFuture<?> executeWithFixedDelay(Runnable command, long initialDelay, long period, TimeUnit unit){
        return executor.scheduleWithFixedDelay(command, initialDelay, period, unit);
    }

    /**
     * 线程生产工厂
     */
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);
        public Thread newThread(Runnable r) {
            return new Thread(r, "zccl*" + mCount.getAndIncrement());
        }
    };
}
