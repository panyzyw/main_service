package com.yongyida.robot.voice.data;

public class RobotStateData {

	/*机器人控制状态*/
	public static final String STATE_CONTRALL = "contrall";
	public static final String STATE_UNCONTRALL = "uncontrall";
	
	/*机器人默认名字*/
	public static final String STATE_DEFAULT_NAME = "xiaoyong";
	
	/*机器人默认ID*/
	public static final String STATE_DEFAULT_ID = "123456";
	public static final String STATE_DEFAULT_RID = "123456";
	
	/*登陆状态*/
	public static final String STATE_LOGIN_DEFAULT = "1";
	public final static String STATE_LOGIN_SUCCESS = "0";
	public final static String STATE_LOGIN_FAIL = "-1";
	public final static String STATE_ACCOUNT = "2";
	/*注册电量广播*/
	public static final String STATE_UNREGISTER = "unregister";
	public static final String STATE_REGISTER = "register";
	
	/*有无网络连接*/
	public static final String STATE_NET_CONNECT = "net_connect";
	public static final String STATE_NET_UNCONNECT = "net_unconnect";
	/*登录网络闪断播放声音*/
	public static final String STATE_LOGIN_VOICE_PLAY = "play" ;
	public static final String STATE_LOGIN_VOICE_STOP_PLAY = "stop_play" ;
	/*发送了提醒数据*/
	public static final String STATE_SEND_DATA = "send_remand_data";
	public static final String STATE_UNSEND_DATA = "unsend_remand_data";
	
	public static final int VOICE_KEY_XIAOYONG = 1;
	public static final int VOICE_KEY_XIAOER = 2;
	
	public static int mRotorVoiceKey = VOICE_KEY_XIAOYONG;
	
	
	
	
}
