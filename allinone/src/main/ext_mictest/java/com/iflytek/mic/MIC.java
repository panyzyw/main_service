package com.iflytek.mic;

import android.util.Log;

import com.zccl.ruiqianqi.tools.LogUtils;

public class MIC {
 
	private static String TAG = "LOAD_MIC";
	//测试程序版本号
	private static String VERSION = "1.0.0.1001";
	
	/**
	 * 获取测试程序版本号
	 * @return version
	 */
	public static String getVersion()
	{
		return VERSION;
	}
	
	/**
	 * 加载动态库
	 */
	static {
		try {
			System.loadLibrary("mictest");
		} catch (UnsatisfiedLinkError error) {
			LogUtils.e(TAG, "mictest", error);
		}
	}
	
	/**
	 * 开启震动测试
	 * @param type ： 1
	 * @return
	 */
	public static native int[] vibrationTestWr(int[] data, int length);
	
	/**
	 * 开启录音测试
	 * @param type ： 1
	 * @return
	 */
	public static native int[] recordTestWr(int[] data, int length);
}
