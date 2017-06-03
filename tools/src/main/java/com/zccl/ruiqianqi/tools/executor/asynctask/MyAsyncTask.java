package com.zccl.ruiqianqi.tools.executor.asynctask;

import android.os.AsyncTask;

import com.zccl.ruiqianqi.tools.config.MyConfigure;

import java.lang.ref.WeakReference;

/**
 * 每一个任务都会加入线程池中，在线程池中调用本对象的相关方法，
 * 执行完了之后，再用全局Handler把结果与对象一起发到主线程，
 * 由Handler来继续调用对象的相关方法，其实就是一个对象的多个方法，
 * 在两个线程中，按先后顺序执行。两个线程都有队列，前后对象互不干扰。
 * 所以这个对象绝对是可重入的。。。。。。
 *
 * 不同的任务可能有不同的model
 *
 * newCachedThreadPool     创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
 * newFixedThreadPool      创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
 * newScheduledThreadPool  创建一个定长线程池，支持定时及周期性任务执行。
 * newSingleThreadExecutor 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
 *
 * Android3.0及其以上的AsyncTask默认是串行运行，但也有并行运行
 * 	并行：executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR, params);
 *	串行：execute(params);
 *
 * Android3.0以下的版本有且只有并行，默认也是并行
 *  并行：execute(params);
 *
 * 当一个任务通过execute(Runnable)方法欲添加到线程池时：
 * 如果此时线程池中的数量小于corePoolSize，即使线程池中的线程都处于空闲状态，也要创建新的线程来处理被添加的任务。
 * 如果此时线程池中的数量等于corePoolSize，但是缓冲队列 workQueue未满，那么任务被放入缓冲队列。
 * 如果此时线程池中的数量大于corePoolSize，缓冲队列workQueue满，并且线程池中的数量小于maximumPoolSize，建新的线程来处理被添加的任务。
 * 如果此时线程池中的数量大于corePoolSize，缓冲队列workQueue满，并且线程池中的数量等于maximumPoolSize，那么通过 handler所指定的策略来处理此任务。
 *
 * 也就是：处理任务的优先级为：
 * 核心线程corePoolSize、任务队列workQueue、最大线程maximumPoolSize，如果三者都满了，使用handler处理被拒绝的任务。
 *
 * 当线程池中的线程数量大于corePoolSize时，如果某线程空闲时间超过keepAliveTime，线程将被终止。这样，线程池可以动态的调整池中的线程数。
 *
 * Object, Integer, MyLinkData【参数传入类型，更新传入类型，返回类型】
 * @author zccl
 */
public class MyAsyncTask extends AsyncTask<Object, Integer, MyLinkData> {

    /** 任务指令*/
    private int cmd;
    /** 处理事件的model */
    private BaseAsyncInteractor baseInteractor;
    /** 事件处理完的接收者的弱引用 */
    private WeakReference<OnAsyncResultCallback> weakRef;

    public MyAsyncTask(int cmd, BaseAsyncInteractor baseInteractor, OnAsyncResultCallback resultCallback){
        this.cmd = cmd;
        this.baseInteractor = baseInteractor;
        weakRef = new WeakReference<>(resultCallback);
    }

    /**
     * 构造此任务的线程中（其实也必须为主线程）
     */
    @Override
    protected void onPreExecute() {
        OnAsyncResultCallback resultCallback = weakRef.get();
        if(resultCallback != null){
            resultCallback.onPreExecute(cmd);
        }
    }

    /**
     * 主线程，过程的更新
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        OnAsyncResultCallback resultCallback = weakRef.get();
        if(resultCallback != null){
            resultCallback.onProgressUpdate(cmd, values);
        }
    }

    /**
     * 用来更新UI用（主线程），拿到本对象才能调用这个方法
     * @param values
     */
    public void publishUpdate(Integer values){
        this.publishProgress(values);
    }


    /**
     * 主线程，UI同步
     */
    @Override
    protected void onPostExecute(MyLinkData result) {
        OnAsyncResultCallback resultCallback = weakRef.get();
        if(resultCallback != null){
            resultCallback.onHandleUI(cmd, result);
        }
    }

    @Override
    protected MyLinkData doInBackground(Object... params) {
        MyLinkData myLinkData = null;
        for (int i = 0; i < MyConfigure.RETRY_COUNT; i++) {
            myLinkData= baseInteractor.execute(cmd, params);
            if(myLinkData.getState().equals(MyConfigure.RETRY)){
                //最后一次执行完后，如果状态还是RETRY，则结束。
                if(i==MyConfigure.RETRY_COUNT-1){
                    myLinkData.setState(MyConfigure.RETRY_OVER);
                }
                continue;
            }else{
                break;
            }
        }
        return myLinkData;
    }


    /**
     * 并行执行任务，参数与doInBackground对应
     * @param params
     * @return
     */
    public AsyncTask<Object, Integer, MyLinkData> runWorkParallel(Object... params) {
        return super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    /**
     * 串行执行任务，参数与doInBackground对应
     * @param params
     * @return
     */
    public AsyncTask<Object, Integer, MyLinkData> runWorkSerial(Object... params) {
        return super.execute(params);
    }

    /**
     * 多线程异步回调接口
     */
    public interface OnAsyncResultCallback {
        void onPreExecute(int cmd);
        void onProgressUpdate(int cmd, Integer... progress);
        void onHandleUI(int cmd, MyLinkData result);
    }

}
