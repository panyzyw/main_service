package com.zccl.ruiqianqi.tools.executor.impl;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Handler的处理顺序：
 * 1.message中的回调---------------就是一个Runnable接口（msg携带的其他参数都没用了）
 * 2.成员变量的callback回调-------handleMessage
 * 3.handleMessage回调处理------handleMessage
 *
 *
 * 采用【线程池 + 队列】处理
 * 可能会有耗时任务出现，要考虑到多线程
 * Thread
 * HandlerThread
 * AsyncTask
 * TimerTask
 * @author zccl
 *
 */
public class MyWorkerTask {
	
	/** Handler的任务执行体 */
	private LooperThread looperThread = null;
	/** 启动了一个线程内部类，循环执行TimerTask */
	private Timer timer = null;

	public MyWorkerTask(){

	}

	/**
	 * 开始计时任务
	 * @param timerTask
	 */
	public void startTask(TimerTask timerTask){
		timer = new Timer();
		//任务，延时，周期
		timer.schedule(timerTask, 100, 1000);
	}

	/**
	 * 取消计时任务
	 * @return
	 */
	public void cancelTask(){
		if(null != timer) {
			timer.cancel();
			timer = null;
		}
	}

	/**
	 * 开始【单线程+队列】任务
	 * @param callback
	 */
	public void startHandler(Callback callback){
		looperThread = new LooperThread(callback);
		looperThread.start();
	}

	/**
	 * 获得HANDLER对象，进而发送消息
	 * @return
	 */
	public Handler getHandler(){
		if(looperThread != null) {
			return looperThread.mHandler;
		}
		return null;
	}

	/**
	 * 退出【单线程+队列】任务
	 */
	public void quit(){
		if(null != looperThread){
			looperThread.quit();
			looperThread = null;
		}
	}
	
	/**
	 * Handler的任务执行体
	 * @author zccl
	 *
	 */
	class LooperThread extends Thread {
	      public Handler mHandler;
		  private Callback callback;

		  public LooperThread(Callback callback){
			  this.callback = callback;
		  }

	      public void run() {
	          
	    	  // 将Looper放入线程局部变量存储器
	    	  Looper.prepare();
	          
	          // 事件处理Handler
	          mHandler = new Handler(Looper.myLooper(), callback);
	          
	          // 这个是死循环，没有消息就阻塞
	          Looper.loop();
	      }
	      
	      /**
	       * 带队列的线程退出方法
	       * @return
	       */
	      public boolean quit() {
	          Looper looper = Looper.myLooper();
	          if (null != looper) {
	              looper.quit();
	              return true;
	          }
	          return false;
	      }
	}
}
