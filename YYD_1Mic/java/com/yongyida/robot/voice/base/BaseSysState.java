package com.yongyida.robot.voice.base;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.LogData;
import com.yongyida.robot.voice.utils.LogUtils;

public abstract class BaseSysState {
	/**
	 * 获取应用程序的版本号
	 * 
	 * @param context
	 * @return
	 * 
	 */
	public static String getAppVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			
			return info.versionName;
		} catch (NameNotFoundException e) {
			LogUtils.showLogError(LogData.VERSION_NAME, null, e);
		}
		return GeneralData.VERSION_ERROR;
	}
	
	/**
	 * 判断SD卡是否存在
	 * 
	 * @return
	 */
	public static boolean isExtraSd(){
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			return true;
		}
		return false;
	}
	
	public static String getSdcardPath(){
		
		if(isExtraSd()){
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return null;
	}
	
}
