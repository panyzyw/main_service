package com.iflytek.alsa.jni;


import com.zccl.ruiqianqi.eventbus.MicBusEvent;
import com.zccl.ruiqianqi.tools.LogUtils;

import org.greenrobot.eventbus.EventBus;

public class AlsaJni {
	
	static {
		System.loadLibrary("alsa-jni");
	}
	
	/**
	 * 设置是否打印jni日志
	 * 
	 * @param show true打印，false不打印
	 */
	public static native void showJniLog(boolean show);
	
	/**
	 * 打开pcm设备
	 * 
	 * @param card 声卡编号
	 * @param sampleRate 声卡采样频率
	 * @param obj java对象，其中必须要有成员static int pcmHandle用于保存pcm指针
	 * @return 0表示成功，否则失败
	 */
	public static native int pcm_open(int card, int sampleRate, Object obj);
	
	//-----------------------
	public static native int pcm_buffer_empty();
	//---------------------------------------------------
	/**
	 * 获取pcm设备底层缓冲大小
	 * 
	 * @param handle pcm设备指针
	 * @return 缓冲大小（单位：字节），出错返回-1
	 */
	public static native int pcm_buffer_size(int handle);
	
	/**
	 * 开始录音，jni中创建线程不断读取音频写入一个缓冲队列
	 * 
	 * @param readSize 每次从pcm设备读取的音频长度
	 * @param queueSize 音频缓冲队列大小
	 * @return 0表示成功创建录音线程开始录音，否则失败
	 */
	public static native int pcm_start_record(int readSize, int queueSize);
	
	/**
	 * 从音频队列中读取pcm数据
	 * 
	 * @param data 预留的数据缓冲区
	 * @param count 要读取的数据字节数
	 * @return 0表示未读取到数据，否则为读到的数据长度
	 */
	public static native int pcm_read(byte[] data, int count);
	
	/**
	 * 关闭pcm设备，停止录音
	 * 
	 * @param handle pcm设备指针
	 * @return 0表示成功，否则失败
	 */
	public static native int pcm_close(int handle);

	public static int yyd_mic_callback(int val){
		MicBusEvent.Operator5MicEvent operator5MicEvent = new MicBusEvent.Operator5MicEvent();
		// 关闭五麦
		if(0 == val){
			operator5MicEvent.setStatus(MicBusEvent.Operator5MicEvent.CLOSE_5_MIC);
		}
		// 打开五麦
		else {
			operator5MicEvent.setStatus(MicBusEvent.Operator5MicEvent.OPEN_5_MIC);
		}
		//EventBus.getDefault().post(operator5MicEvent);
		return val;
	}
}
