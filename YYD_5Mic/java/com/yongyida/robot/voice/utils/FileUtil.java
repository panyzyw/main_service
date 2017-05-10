package com.yongyida.robot.voice.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.yongyida.robot.voice.base.BaseSysState;

import android.content.Context;
import android.os.Environment;


public class FileUtil extends BaseSysState {

	
	
	public static void putToFile(Context context, String action, String path){
		
		synchronized (FileUtil.class) {
			try {
				
				if(context == null){
					return;
				}
				
				SharePreferenceUtils sp = SharePreferenceUtils.getInstance(context);
				long time = System.currentTimeMillis();
				Date date = new Date(time);
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
				String curTime = format.format(date);
				String[] subString = curTime.split(" ");
				int ymd = Integer.parseInt(subString[0]);
				File file = new File(FileUtil.getSdcardPath() + "/" + path);
				if(ymd - sp.getInt(action, -1) > 0){
					sp.putInt(action, ymd);
					if(file.exists()){
						file.delete();
					}
				}
				curTime = action + "/" + curTime + "\n";
				byte[] bt = curTime.getBytes();
				FileOutputStream fos = new FileOutputStream(file, true);
				fos.write(bt);
				fos.flush();
				fos.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 读取asset目录下文件。
	 *
	 * @return content
	 */
	public static String readFile(Context mContext, String file, String code) {
		int len = 0;
		byte[] buf = null;
		String result = "";
		try {
			InputStream in = mContext.getAssets().open(file);
			len = in.available();
			buf = new byte[len];
			in.read(buf, 0, len);

			result = new String(buf, code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static InputStream getFileStream(Context context, String fileName){
		try {
			return context.getResources().getAssets().open(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 文件流转字符串
	 * @param is
	 * @return
	 */
	public static String readStreamToStr(InputStream is) {
		byte[] buf = new byte[1024];
		try {
			if(is != null){
				int len = -1;
				ByteArrayOutputStream baoS = new ByteArrayOutputStream();
				while((len = is.read(buf)) > -1) {
					baoS.write(buf, 0, len);
				}
				String str = baoS.toString("UTF-8");
				baoS.close();
				is.close();
				return str;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 文件流转字符串
	 * @param is
	 * @return
	 */
	public static StringBuffer readStreamToStrBuf(InputStream is) {
		StringBuffer sb = new StringBuffer();
		InputStreamReader streamReader = new InputStreamReader(is);
		char[] buf = new char[1024];
		try {
			if(streamReader != null){
				int len = -1;
				while((len = streamReader.read(buf)) > -1) {
					sb.append(buf, 0, len);
				}
				is.close();
				return sb;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
