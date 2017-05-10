package com.yongyida.robot.voice.data;


public class GeneralData {


	
	/*json 结果*/
	public static final int RESULT_SUCCESS = 0;
	
	/*语义理解成功返回值*/
	public static final int SPEECH_SUCCESS = 0;
	
	/*getVerdion 错误*/
	public static final String VERSION_ERROR = null;
	
	/*中文*/
	public static final String LANGUAGE_CHINESE = "zh_cn";
	
	/*英文*/
	public static final String LANGUAGE_AMERICA = "en_us";
	
	/*普通话*/
	public static final String LANGUAGE_GENERAL = "mandarin";
	
	/*粤语*/
	public static final String LANGUAGE_CANTONESE = "cantonese";
	
	/*四川话*/
	public static final String LANGUAGE_SICHUAN = "lmz";
	
	/*河南话*/
	public static final String LANGUAGE_HELAN = "henanese";
	
	/*在四秒内没有收到声音不在录音*/
	public static final String LANGUAGE_BOS_TIME = "6000";
	
	/*录音完后1秒，不在录音*/
	public static final String LANGUAGE_EOS_TIME = "800";
	
	/*有标点*/
	public static final String LANGUAGE_HAVE_PUNCTUATION = "1"  ;
	
	/*无标点*/
	public static final String LANGUAGE_NONE_PUNCTUATION = "0";
	
	/*以WAV格式保存*/
	public static final String LANGUAGE_WAV_FORMAT = "wav";
	
	/*以PCM格式保存*/
	public static final String LANGUAGE_TEXT_ENCODING = "utf-8";

	public static final String LANGUAGE_PCM_FORMAT = "pcm";
	
	public static final String GRAMMAR_TYPE_ABNF = "abnf";
	
	/*发送给子服务的结果*/
	public static final String RESULT = "result";

	//发送此广播的起点
	public static final String FROM = "from";

	//接受此广播的目的地
	public static final String TO = "to";

	//状态信息
	public static final String STATUS = "status";

	/*讯飞返回结果*/
	public static final int RESULT_THERO = 0;
	
	public static final int RESULT_ONE = 1;
	
	public static final int RESULT_TWO = 2;
	
	public static final int RESULT_THREE = 3;

	public static final int RESULT_FOUR = 4;

	public static final String CONTRAL = "contral";

	public static final String SPEECH = "speech";

	public static final String APP = "app";
	
	/*机器人登录*/
	public static final String ROBOT_LOGIN = "/robot/login";
	
	/*app推送*/
	public static final String ROBOT_PUSH = "/robot/push";

	public static final String SUCCESS = "success";

	public static final String ROBOT_CONTROLL = "/robot/controll";

	public static final String ROBOT_UNCONTROLL = "/robot/uncontroll";

	public static final String HTTP_SEVER = "http_sever";

	public static final String ROBOT_SEVER = "robot_sever";

	public static final String RESOURCE_SERVER = "resource_server";

	public static final String HTTP_SEVER_PORT = "http_sever_port";

	public static final String ROBOT_SEVER_PORT = "robot_sever_port";

	public static final String RESOURCE_SERVER_PORT = "resource_server_port";

	public static final int NOVOICE = 10118;

	public static final int NETWOKTIMEOUT = 0;

	public static final String SENDBROCAST = "sendbrocast_error";

	/*摸头*/
	public static final String TOUCHHEAD = "touchHead";
	
	public static final String TOUCHCHIN = "touchChin";
	
	public static final String TOUCHLEFTSHOULDER = "touchLeftShoulder";
	
	public static final String TOUCHRIGHTSHOULDER = "touchRightShoulder";

	/*响铃*/
	public static final String RINGUP = "ringup";
	/*控制*/
	public static final String CONTRALL = "contrall";
    /*手机文本推送*/
	public static final String APPTEXT = "apptext";

	//手机打开监控
	public static final String OPEN_MONITOR = "open_monitor";

	public static final String SHUT_DOWN_VIDEO = "shut_down_video";

	public static final String PHOTO_THUMBNAIL = "photo_query_thumbnail";

	public static final String PHOTO_ORIGINAL = "photo_query_original";

	public static final String PHOTO_LIST = "photo_query_list";

	public static final String ROBOT_CHANGENAME = "/robot/flush";

	public static final String INTENT_TOUCH_HEAD = "t_head";
	
	public static final String INTENT_TOUCH_WAKEUP = "wakeup";

	public static final String INTENT_TOUCH_CHIN = "t_back";

	public static final String INTENT_TOUCH_LEFT_SHOULDER = "t_left";

	public static final String INTENT_TOUCH_RIGHT_SHOULDER = "t_right";

	public static final Object PHOTO_DELETE = "photo_delete";

	public static final String DEFAULT = "-1";

	public static final String ACTION = "action";

	public static final String NAME = "name";

	public static final String EXTRAVALUE = "extra";

	public static final String INTENT_USER_MESSAGE_RESULT = "/robot/bind/list";

	public static final String INTENT_USER_MESSAGE_DELETE_RESULT = "/robot/unbind";

	public static final String CLOSE_CHANNEL = "close_channel";

}
