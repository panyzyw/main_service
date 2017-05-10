package com.yongyida.robot.voice.data;

import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class UrlData {
	
	
	/*服务器域名*/
	public static final String ROBOT_TCP_HOST = "robot.yydrobot.com";//"robot.yydrobot.com";//"120.24.242.163";
	
	public static String TCP_IP_HOST = null;
	public static String TCP_IP = null;
	public static String TCP_IP_PORT = null;
	
	/*服务器端口号*/
	public static final int ROBOT_TCP_PORT = 8001;
	
	/*服务器域名*/
	public static final String HTTP_SERVER_HOST = "server.yydrobot.com";//"120.24.242.163";
	
	/*静态服务器域名*/
	public static final String HTTP_RESOUCE_HOST = "resource.yydrobot.com";//"120.24.242.163";
	
	private static final String BASE_SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	
	public final static Uri MEDIA_SONG = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	
	public final static Uri MEDIA_VIDEO = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	
	public final static String MEDIA_TYPE_SONG = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
	
	public final static String MEDIA_TYPE_VIDEO = MediaStore.Video.Media.DEFAULT_SORT_ORDER;
	
	public final static String MEDIA_ALL_SONG_NAME = MediaStore.Audio.Media.TITLE;
	
	public final static String MEDIA_ALL_VIDEO_NAME = MediaStore.Video.Media.TITLE;
	
	/*语义理解保存路径*/
	public static final String LANGUAGE_PATH = BASE_SD_PATH + "/msc/sud.wav";
	
	/*语音识别保存路径*/
	public static final String RECOGNIZE_PATH = BASE_SD_PATH + "/msc/asr.wav";
	
	/*语音合成保存路径*/
	public static final String READ_PATH = BASE_SD_PATH + "/msc/tts.wav";
	
	/*无网络连接读取录音的路径*/
	public static final String NO_NET_PATH = BASE_SD_PATH + "/tts.wav";
	
	/*网络连接错误读取录音路劲*/
//	public static final String NET_ERROR_PATH = BASE_SD_PATH + "/NetError.wav";
	
	
	
	
}

