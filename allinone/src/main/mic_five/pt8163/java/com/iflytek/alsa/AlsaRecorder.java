package com.iflytek.alsa;

import java.lang.Thread.UncaughtExceptionHandler;

import android.os.Process;
import android.util.Log;

import com.iflytek.alsa.jni.AlsaJni;

/**
 * 直接调用tinyalsa接口的录音机类，单例
 * 
 * @author jianhuang3
 */
public class AlsaRecorder {
	private static String TAG = "AlsaRecorder";
	
	private static AlsaRecorder instance;
	private static int card = 0;
	private static int sampleRate = 96000;
	private static int pcmHandle = 0;
	
	private PcmReadThread mPcmReadThread;
	private PcmListener mPcmListener;
	private boolean mIsRecording;
	
	// 每次从jni读取的数据长度
	private int mBufferSize = 12288;
	
	private Object mSyn = new Object();
	
	/**
	 * pcm音频回调接口
	 * 
	 * @author jianhuang3
	 */
	public interface PcmListener {
		void onPcmData(byte[] data, int dataLen);
	}
	
	private AlsaRecorder(int card, int sampleRate) {
		AlsaRecorder.card = card;
		AlsaRecorder.sampleRate = sampleRate;
	}
	
	/**
	 * 创建AlsaRecorder单例对象
	 * 
	 * @param card pcm采集声卡设备号
	 * @return AlsaRecorder单例对象，采用96K的采样率
	 */
	public static AlsaRecorder createInstance(int card) {
		return createInstance(card, sampleRate);
	}
	
	/**
	 * 创建AlsaRecorder单例对象
	 * 
	 * @param card pcm采集声卡设备号
	 * @param sampleRate pcm采样率
	 * @return AlsaRecorder单例对象
	 */
	public static AlsaRecorder createInstance(int card, int sampleRate) {
		if (null == instance) {
			instance = new AlsaRecorder(card, sampleRate);
		}
		return instance;
	}
	
	/**
	 * 返回AlsaRecorder单例对象
	 * 
	 * @return AlsaRecorder单例对象，未创建则返回null
	 */
	public static AlsaRecorder getInstance() {
		return instance;
	}
	
	/**
	 * 打开pcm设备，开始录音
	 * 
	 * @param listener pcm监听对象
	 * @return 0表示开启录音成功，否则失败
	 */
	public int startRecording(PcmListener listener) {
		if (null == instance) {
			Log.e(TAG, "startRecording | AlsaRecorder instance is null.");
			return -1;
		}
		
		if (mIsRecording) {
			Log.e(TAG, "startRecording | be repeatedly called.");
			return -1;
		}
		
		mPcmListener = listener;
	
		Thread pcmOpenThread = new Thread() {

			@Override
			public void run() {
				// 当pcm设备处于打开状态时，pcm_open将会一直阻塞，故新开线程调用
				AlsaJni.pcm_open(card, sampleRate, instance);
				synchronized (mSyn) {
					// 打pcm设备打开成功，通知主线程解除等待
					mSyn.notify();
				}
			}
		};

		synchronized (mSyn) {
			try {
				pcmOpenThread.start();
				// 主线程阻塞1s，等待子线程打开pcm设备
				mSyn.wait(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if (0 != pcmHandle) {
			mBufferSize = AlsaJni.pcm_buffer_size(pcmHandle)/2;
			if (0 == AlsaJni.pcm_start_record(mBufferSize, mBufferSize * 4)) {
				mIsRecording = true;
			}
			
			if (null == mPcmReadThread) {
				mPcmReadThread = new PcmReadThread();
				mPcmReadThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					
					@Override
					public void uncaughtException(Thread arg0, Throwable arg1) {
						Log.e(TAG, "uncaughtException Throwable message="+arg1.getMessage()+" Thread name=="+arg0.getName());
					}
				});
				mPcmReadThread.start();
			}
		} else {
			// 此时pcm_open发生阻塞，表明pcm设备正被占用，强行终止子线程并返回错误
			pcmOpenThread.interrupt();
			Log.e(TAG, "startRecording | open pcm device failed.");
			return -1;
		}
		
		return 0;
	}
	
	/**
	 * 是否正在录音
	 * 
	 * @return 录音返回true，否则false
	 */
	public boolean isRecording() {
		return mIsRecording;
	}
	
	/**
	 * 停止录音
	 */
	public void stopRecording() {
		if (null == instance) {
			Log.e(TAG, "stopRecording | AlsaRecorder instance is null.");
			return;
		}
		
		if (null != mPcmReadThread) {
			mPcmReadThread.stopRun();
			mPcmReadThread = null;
		}
	}
	
	/**
	 * 销毁AlsaRecorder单例对象，之后{@link #getInstance()}返回null
	 */
	public void destroy() {
		stopRecording();
		instance = null;
	}
	
	/**
	 * 获取版本信息
	 * 
	 * @return 版本信息
	 */
	public static String getVersion() {
		return "1.1";
	}
	
	// 读pcm音频线程
	class PcmReadThread extends Thread {
		private boolean mStop = false;
		
		public void stopRun() {
			/*
			Log.e(TAG, "close five mai");
			AlsaJni.pcm_close(pcmHandle);
			pcmHandle = 0;
			mIsRecording = false;
			Log.e(TAG, "quit record");
			*/
			mStop = true;
		}
		
		@Override
		public void run() {
			super.run();
			Log.e(TAG, "start record: " + Process.myPid());
			while (!mStop) {
				byte[] pcmData = new byte[mBufferSize];
				int readLen = AlsaJni.pcm_read(pcmData, pcmData.length);
				if (null != mPcmListener && 0 != readLen) {
					mPcmListener.onPcmData(pcmData, pcmData.length);
				}
			}
			Log.e(TAG, "five mai is closed");

			Log.e(TAG, "close five mai");
			AlsaJni.pcm_close(pcmHandle);
			pcmHandle = 0;
			mIsRecording = false;
			Log.e(TAG, "quit record");

		}
		
	}
	
}
