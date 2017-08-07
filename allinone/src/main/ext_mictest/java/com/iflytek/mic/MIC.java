package com.iflytek.mic;

import com.zccl.ruiqianqi.tools.LogUtils;

public class MIC {

	// 类标志
	private static String TAG = "LOAD_MIC";
	
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
	 * @return
	 */
	public static native int[] vibrationTestWr(int[] data, int length);
	
	/**
	 * 开启录音测试
	 * @return
	 */
	public static native int[] recordTestWr(int[] data, int length);
}
