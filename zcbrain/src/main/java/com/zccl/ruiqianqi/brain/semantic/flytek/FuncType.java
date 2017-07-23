package com.zccl.ruiqianqi.brain.semantic.flytek;
/**
 * 科大讯飞后台功能类型
 */
public class FuncType {

	// 讯飞返回有结果
	public static final int RESULT_ZERO = 0;
	// 讯飞返回无结果
	public static final int RESULT_FOUR = 4;

	/***********************************【通用语义场景】*******************************************/
	// "打电话(telephone)"支持电话拨打与查询的语义解析
	public static final String FUNC_CALL = "telephone";

	// "提醒(schedule)"主要用于提醒或日程安排的新建、查看以及到时提醒的语义解析
	public static final String FUNC_SCHEDULE_ = "schedule";

	// "地图(map)"用于地点搜索，公交路线查询以及最快、最短、最省钱、高速优先等不同类型的自驾路线的语义解析
	public static final String FUNC_MAP = "map";

	// "股票(stock)"支持基于股票名称或股票代码的分时图、K线图、日K线、周K线、月K线的查询搜索的语义解析
	public static final String FUNC_STOCK_ = "stock";

	// "天气(weather)"主要用于天气情况查询的语义解析
	public static final String FUNC_WEATHER_ = "weather";

	// "音乐(music)"支持基于歌曲名、歌手名、专辑名称、歌曲类型的音乐查找或播放的语义解析
	public static final String FUNC_MUSIC_ = "music";

	// "菜谱(cookbook)"支持菜谱信息查询的语义解析和数据提供
	public static final String FUNC_COOKBOOK_ = "cookbook";

	// "翻译(translation)"主要用于多语种即时翻译的语义解析
	public static final String FUNC_TRANSLATE_ = "translation";


	/*********************************【没有处理之通用语义】***************************************/
	// "发短信(message)"支持短信发送与查询的语义解析
	public static final String FUNC_MESSAGE = "message";
	// "电视控制(tvControl)"支持电视控制命令的语义解析
	public static final String FUNC_TV_CONTROL = "tvControl";
	// "空调控制(airControl)"支持空调控制命令的语义解析
	public static final String FUNC_AIR_CONTROL = "airControl";





	/*********************************【没有处理之私有语义】***************************************/
	// 表情显示
	public static final String FUNC_EXPRESSION = "expression";
	// 方言
	public static final String FUNC_DIALECT = "dialect";
	// 查看短信
	public static final String FUNC_SMS = "sms";


	/****************************【私有语义场景，和通用语义场景有重复】****************************/
	// 翻译
	public static final String FUNC_TRANSLATE = "tran";
	// 查询提醒、删除提醒
	public static final String FUNC_SCHEDULE = "schedule";
	// 查询菜名
	public static final String FUNC_COOKBOOK = "cookbook";
	// 播报天气
	public static final String FUNC_WEATHER = "weather";
	// 股票
	public static final String FUNC_STOCK = "stock";
	// 音乐
	public static final String FUNC_MUSIC = "music";
	// 音乐控制
	public static final String FUNC_MUSIC_CTRL = "music_ctrl";

	/***********************************【私有语义场景】*******************************************/
	// 影视
	public static final String FUNC_VIDEO = "video";
	// 影视控制
	public static final String FUNC_VIDEO_CTRL = "video_ctrl";

	// 广场舞
	public static final String FUNC_SQUARE_DANCE = "squaredance";
	// 诗词学习
	public static final String FUNC_STUDY = "study";
	// 国学知识
	public static final String FUNC_SINOLOGY = "sinology";
	// 影视资讯
	public static final String FUNC_MOVIE_INFO = "movieinfo";
	// 戏曲
	public static final String FUNC_OPERA = "opera";
	// 健康养生
	public static final String FUNC_HEALTH = "health";
	// 音量控制
	public static final String FUNC_SOUND = "sound";
	// 故事
	public static final String FUNC_STORY = "story";
	// 跳舞
	public static final String FUNC_DANCE = "dance";
	// 拍照
	public static final String FUNC_CAMERA = "camera";
	// 相册
	public static final String FUNC_PHOTO = "photos";
	// 笑话
	public static final String FUNC_JOKE = "joke";
	// 新闻
	public static final String FUNC_NEWS = "news";
	// 习惯养成
	public static final String FUNC_HABIT = "habit";
	// 电量
	public static final String FUNC_BATTERY = "battery";
	// 英业达字典
	public static final String FUNC_DICT = "dictionary";



	/************************************【通用问答库】********************************************/
	// 算术
	public static final String FUNC_ARITHMETIC = "calc";
	// 百科
	public static final String FUNC_BAI_KE = "baike";
	// 日期
	public static final String FUNC_DATETIME = "datetime";
	// 社区问答
	public static final String FUNC_FAQ = "faq";
	// 开放语义
	public static final String FUNC_OPEN_QA = "openQA";


	/*********************************【聊天语义场景】*********************************************/
	// 聊天
	public static final String FUNC_CHAT = "chat";
	// 询问名字
	public static final String OP_QUERY_NAME = "query_name";
	// 确定名字
	public static final String OP_IS_YOUR_NAME = "isname";
	// 改名字
	public static final String OP_CHANGE_NAME = "change_name";
	// 表情聊天
	public static final String OP_EMOTION_CHAT = "emotionchat";
	// 时间询问
	public static final String OP_QUERY_TIME = "patch";
	// 移动业务
	public static final String OP_CMCC = "cmcc";
	// 问答
	public static final String OP_ANSWER = "ANSWER";

	// 表情聊天
	public static final String FUNC_EMOTION_CHAT = "emotion_chat";

	// 通用打开功能
	public static final String FUNC_GENERIC = "generic";

	/*********************************【智能家居语义场景】*****************************************/
	// 智能家居、电视、机顶盒、影碟机、电视盒子、功放音响、投影仪
	public static final String FUNC_SMART_HOME = "smarthome";

	/*********************************【关机、重启语义场景】***************************************/
	// 关机、重启
	public static final String FUNC_SHUTDOWN = "shutdown";
	// 关机
	public static final String OP_POWER_OFF = "poweroff";
	// 重启
	public static final String OP_REBOOT = "reboot";

	/***********************************【人脸识别场景】*******************************************/
	// 打开人脸识别
	public static final String FUNC_FACE = "openapp";
	// 打开人脸识别
	public static final String SLOT_app__face = "face";

	/*********************************【个人认为设计不合理的语义】*********************************/
	// 跟机器人说学习算术
	public static final String FUNC_QUSETION = "qusetion";


	/**********************************************************************************************/
	/************************************【主服务自己处理】****************************************/
	/**********************************************************************************************/
	// 机器人移动
	public static final String FUNC_MOVE = "move";
	// 切换服务器
	public static final String FUNC_SWITCH = "switch";
	// 录音文本的显示与隐藏
	public static final String FUNC_DISPLAY = "display";


	/***********************************【机器人闭嘴场景】*****************************************/
	// 机器人闭嘴
	public static final String FUNC_MUTE = "cmd_mute";
	// 安静、闭嘴
	public static final String OP_SHUT_UP = "shutup";
	// 其他安静方式
	public static final String SLOT_cmd__mute = "mute";


	/***********************************【电视媒体场景】*******************************************/
	// 电视剧、电影、动漫、综艺、体育、直播
	public static final String FUNC_WATCH_TV = "wacthTV";
	// 打开电视
	public static final String OP_OPEN_TV = "open_TVprogramme";
	// 切换电视
	public static final String OP_WATCH_TV = "wacth_TVprogramme";


	/***********************************【打开各类界面】*******************************************/
	// 打开各类界面
	public static final String FUNC_APP = "open_interface";
	// 下载、信息、录音机、文件管理、日历、时钟、浏览器、通讯录
	public static final String OP_OPEN_TOOL = "open_tool";
	// 我的应用
	public static final String OP_OPEN_APP = "openapp";
	// 戏曲、儿歌、文学、电台、音乐
	public static final String OP_OPEN_ART = "open_art";
	// 网络设置、无线网络、蓝牙、机器人设置、机器人界面、关于机器人界面、WiFi、关于机器人
	public static final String OP_OPEN_SETTING = "related_wifi";


	/************************************【游戏场景】**********************************************/
	// 显示游戏图片、打开游戏
	public static final String FUNC_GAME = "game";
	// 显示小狗
	public static final String SLOT_imageType__dogImage = "dogImage";
	// 显示小猫
	public static final String SLOT_imageType__catImage = "catImage";
	// 显示老鹰
	public static final String SLOT_imageType__eagleImage = "eagleImage";
	// 显示凤凰
	public static final String SLOT_imageType__phoenixImage = "phoenixImage";
	// 显示恐龙
	public static final String SLOT_imageType__dinosaurImage = "dinosaurImage";
	// 显示虚拟游戏
	public static final String SLOT_imageType__virtualImage = "virtualImage";
	// 打开游戏
	public static final String OP_OPEN_GAME = "open_game";


	/*********************************【yydchat语义场景】******************************************/
	// 红外感应的开关、二维码、打开测试界面
	public static final String FUNC_YYD_CHAT = "yydchat";
	// 打开红外感应
	public static final String OP_OPEN_SENSE = "open_sense";
	// 关闭红外感应
	public static final String OP_CLOSE_SENSE = "close_sense";
	// 绑定二维码
	public static final String OP_BIND_QRCODE = "qrcode_bind";
	// 下载二维码
	public static final String OP_DOWNLOAD_QRCODE = "qrcode_download";
	// 打开测试界面
	// 没有任何参数下来

	/*********************************【yydcaht语义场景】******************************************/
	// 开关投影，简直就是乱JB写
	public static final String FUNC_YYD_CAHT = "yydcaht";
	// 打开投影
	public static final String OP_OPEN_PROJECTOR = "open_projector";
	// 关闭投影
	public static final String OP_CLOSE_PROJECTOR = "close_projector";





	/*********************************【小勇语音】【562edd7b】*************************************/
	// 音乐
	public static final String FUNC_MUSIC1 = "music1";
	// 电子书
	public static final String FUNC_E_BOOK = "ebook";
	// 取消提醒
	public static final String FUNC_CANCEL = "cancel";
	// 摄影录像
	public static final String FUNC_RECORD = "shoot";

}
