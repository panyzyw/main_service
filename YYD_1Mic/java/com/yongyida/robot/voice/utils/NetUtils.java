package com.yongyida.robot.voice.utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.yongyida.robot.voice.dao.DatabaseOpera;
import com.yongyida.robot.voice.data.LogData;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * 检查网络类
 * 
 * @author Administrator
 *
 */
public class NetUtils {
	private NetUtils() {
	
	}
	
	public static String getSocketHost(Context context){
		DatabaseOpera opera = new DatabaseOpera(context);
		Cursor cursor = opera.query();
		if(cursor != null && cursor.moveToFirst()){
			String tcpHost	= cursor.getString(cursor.getColumnIndex("robot_tcp_host"));
			if(tcpHost != null){
				tcpHost = tcpHost.trim();
				return tcpHost;
			}
		}
		return null;
	}
	
	public static int getSocketPort(Context context){
		DatabaseOpera opera = new DatabaseOpera(context);
		Cursor cursor = opera.query();
		if(cursor != null && cursor.moveToFirst()){
			int tcpPort = cursor.getInt(cursor.getColumnIndex("port_socket"));
			
			return tcpPort;
			
		}
		return -1;
	}
	
	public static String getHttpHost(Context context){
		DatabaseOpera opera = new DatabaseOpera(context);
		Cursor cursor = opera.query();
		if(cursor != null && cursor.moveToFirst()){
			String httpHost	= cursor.getString(cursor.getColumnIndex("http_server_host"));
			cursor.close();
			Log.d("success", "httpHost : " + httpHost);
			if(httpHost != null){
				httpHost = httpHost.trim();
				return httpHost;
			}
		}
		return null;
	}
	
	/**
	 * 网络是否连接.
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isConnected(Context context) {

		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (null != connectivity) {

			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (null != info && info.isConnected()) {
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * wifi连接 .
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifi(Context context) {
		if (isConnected(context)) {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			if(activeNetwork == null){
				return false;
			}else if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
				return true;
			}else {
				return false;
			}			
		}
		return false;

	}

	/**
	 * 手机网络连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isMobile(Context context) {
		if (isConnected(context)) {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			if(activeNetwork == null){
				return false;
			}else if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
				return true;
			}else {
				return false;
			}			
			
		}

		return false;
	}

	/**
	 * 打开网络设置.
	 * 
	 * @param context
	 */
	public static void setNetworkMethod(final Context context) {

		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("网络设置")
				.setMessage("是否打开网络设置？")
				.setPositiveButton("设置", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = null;
						// 3.0以后的版本
						if (android.os.Build.VERSION.SDK_INT > 10) {
							intent = new Intent(
									android.provider.Settings.ACTION_WIRELESS_SETTINGS);

						} else {

							intent = new Intent();
							ComponentName component = new ComponentName(
									"com.android.settings",
									"com.android.settings.WirelessSettings");
							intent.setComponent(component);
							intent.setAction("android.intent.action.VIEW");

						}
						context.startActivity(intent);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

					}
				}).show();
	}

	/**
	 * 获取网络流.
	 * @param strUrl
	 * @return
	 */
	public static InputStream getInputStream(String strUrl) {
		try {
			InputStream is = null;
			HttpURLConnection connection;
			URL url;

			try {
				url = new URL(strUrl);
				connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(5000);
				connection.setDoInput(true);
				connection.setRequestMethod("GET");
//				if (connection.getResponseCode() == HttpStatus.SC_OK) {
//					is = connection.getInputStream();
//
//					return is;
//				}

			} catch (MalformedURLException e) {
				LogUtils.showLogError(LogData.URL_ERROR, null, e);

			} catch (ProtocolException e) {
				LogUtils.showLogError(LogData.TIMEOUT_ERROR, null, e);

			} catch (IOException e) {
				LogUtils.showLogError(LogData.CONNECTIONT_ERROR, null, e);
			}
		} catch (Throwable e) {
			LogUtils.showLogError(LogData.GETINPUTSTREAM_ERROR, null, e);
		}
		return null;
	}

}