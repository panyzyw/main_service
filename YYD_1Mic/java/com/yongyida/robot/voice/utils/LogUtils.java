package com.yongyida.robot.voice.utils;

import android.util.Log;

public class LogUtils{
	private static boolean isShow = true;
	
	public static void showLogError(String name, String result){
		if(isShow) {
			Log.e(name, result);
		}
	}
	
	public static void showLogError(String name, String result, Throwable e){
		if(isShow) {
			Log.e(name, result, e);
		}
	}
	
	public static void showLogInfo(String name, String result){
		if(isShow) {
			Log.i(name, result);
		}
	}
	
	public static void showLogInfo(String name, String result, Throwable e){
		if(isShow) {
			Log.i(name, result, e);
		}
	}
	
	public static void showLogDebug(String name, String result){
		if(isShow) {
			Log.d(name, result);
		}
	}
	
	public static void showLogDebug(String name, String result, Throwable e){
		if(isShow) {
			Log.d(name, result, e);
		}
	}
	
	public static void showLogWarn(String name, String result){
		if(isShow) {
			Log.w(name, result);
		}
	}
	
	public static void showLogWarn(String name, String result, Throwable e){
		if(isShow) {
			Log.w(name, result, e);
		}
	}
}
