package com.yongyida.robot.voice.data;

public class IntentData {

    public static final String BOOT_ACTION_START = "android.intent.action.BOOT_COMPLETED";
    /*音乐*/
    public static final String INTENT_MUSIC = "com.yydrobot.MUSIC";
    /*新闻*/
    public static final String INTENT_NEWS = "com.yydrobot.NEWS";
    /*股票*/
    public static final String INTENT_STOCK = "com.yydrobot.STOCK";
    /*天气*/
    public static final String INTENT_WEATHER = "com.yydrobot.WEATHER";
    /*笑话*/
    public static final String INTENT_JOKE = "com.yydrobot.JOKE";
    /*故事*/
    public static final String INTENT_STORY = "com.yydrobot.STORY";
    /*聊天*/
    public static final String INTENT_CHAT = "com.yydrobot.CHAT";
    /*百科*/
    public static final String INTENT_ENCYCLOPEDIAS = "com.yydrobot.ENCYCLOPEDIAS";
    /*国学*/
    public static final String INTENT_POETRY = "com.yydrobot.POETRY";
    /*诗词学习*/
    public static final String INTENT_POETRY_STUDY = "com.yydrobot.POETRY_STUDY";
    /*提醒*/
    public static final String INTENT_REMIND = "com.yydrobot.REMIND";
    /*旅游*/
    public static final String INTENT_TRIP = "com.yydrobot.TRIP";
    /*美容*/
    public static final String INTENT_COSMETOLOGY = "com.yydrobot.COSMETOLOGY";
    /*游戏*/
    public static final String INTENT_GAME = "com.yydrobot.GAME";
    /*电子书*/
    public static final String INTENT_EBOOK = "com.yydrobot.EBOOK";
    /*算术*/
    public static final String INTENT_ARITHMETIC = "com.yydrobot.ARITHMETIC";
    /*习惯*/
    public static final String INTENT_HABIT = "com.yydrobot.HABIT";
    /*打电话*/
    public static final String INTENT_CALL = "com.yydrobot.CALL";
    /*保存用户交流语言*/
    public static final String INTENT_USER_VOICE = "com.yydrobot.SAVE_USER_VOICE";
    /*翻译*/
    public static final String INTENT_TRANSLATION = "com.yydrobot.TRANSLATION";
    /*国学*/
    public static final String INTENT_SINOLOGY = "com.yydrobot.SINOLOGY";
    /*诗词*/
    public static final String INTENT_STUDY = "com.yydrobot.STUDY";
    /*影视资讯*/
    public static final String INTENT_MOVIEINFO = "com.yydrobot.MOVIEINFO";
    /*提醒*/
    public static final String INTENT_SCHEDULE = "com.yydrobot.SCHEDULE";
    /*拍照*/
    public static final String INTENT_CAMERA = "com.yydrobot.CAMERA";
    /*广场舞*/
    public static final String INTENT_SQUAREDANCE = "com.yydrobot.SQUAREDANCE";
    /*机器人提问*/
    public static final String INTENT_QUSETION = "com.yydrobot.QUSETION";
    /*取消*/
    public static final String INTENT_CANCEL = "com.yydrobot.CANCEL";
    /*摄影*/
    public static final String INTENT_RECORD = "com.yydrobot.SHOOT";
    /*游戏*/
    public static final String INTENT_YYDCHAT = "com.yydrobot.YYDCHAT";
    /*地图*/
    public static final String INTENT_MAP = "com.yydrobot.MAP";
    /*跳舞*/
    public static final String INTENT_DANCE = "com.yydrobot.DANCE";
    /*戏曲*/
    public static final String INTENT_OPERA = "com.yydrobot.OPERA";
    /*健康资讯*/
    public static final String INTENT_HEALTH = "com.yydrobot.HEALTH";
    /*智能家居*/
    public static final String INTENT_SMARTHOME = "com.yydrobot.SMARTHOME";

    public static final String INTENT_COOKBOOK = "com.yydrobot.COOKBOOK";

    public static final String INTENT_STOP = "com.yydrobot.STOP";
	
	public static final String INTENT_CONTROL_STATUS = "com.yongyida.robot.service.CONTROL_STATUS";

    public static final String INTENT_START = "com.yydrobot.START";

    public static final String INTENT_ACHT_ALL = "com.yydrobot.ACHT_ALL";

    public static final String INTENT_MOVE = "com.yydrobot.CONTROLL";

    public static final String INTENT_SHUTUP = "com.yydrobot.SHUTUP";

    /*相册*/
    public static final String INTENT_PHOTO = "com.yydrobot.PHOTOS";

    //20 倾听
    public final static String INTENT_ACTION_LISTEN = "com.yongyida.action.lockscreen.ACTION_LISTEN";
    //16 听不明白
    public final static String INTENT_ACTION_UNKNOW = "com.yongyida.action.lockscreen.ACTION_UNKNOW";


    public static final String INTENT_FORWARD = "{\"cmd\":\"/robot/push\",\"command\":\"{\"cmd\":\"move\",\"param\":\"45\",\"type\":\"forward\"}\"}";
    public static final String INTENT_BACK = "{\"cmd\":\"/robot/push\",\"command\":{\"cmd\":\"move\",\"param\":\"45\",\"type\":\"back\"}}";
    public static final String INTENT_TURN_LEFT = "{\"cmd\":\"/robot/push\",\"command\":\"{\"cmd\":\"move\",\"param\":\"45\",\"type\":\"turn_left\"}\"}";
    public static final String INTENT_TURN_RIGHT = "{\"cmd\":\"/robot/push\",\"command\":\"{\"cmd\":\"move\",\"param\":\"45\",\"type\":\"turn_right\"}\"}";
    public static final String INTENT_HEAD_UP = "{\"cmd\":\"/robot/push\",\"command\":\"{\"cmd\":\"move\",\"param\":\"45\",\"type\":\"head_up\"}\"}";
    public static final String INTENT_HEAD_DOWN = "{\"cmd\":\"/robot/push\",\"command\":\"{\"cmd\":\"move\",\"param\":\"45\",\"type\":\"head_down\"}\"}";
    public static final String INTENT_HEAD_LEFT = "{\"cmd\":\"/robot/push\",\"command\":\"{\"cmd\":\"move\",\"param\":\"45\",\"type\":\"head_left\"}\"}";
    public static final String INTENT_HEAD_RIGHT = "{\"cmd\":\"/robot/push\",\"command\":\"{\"cmd\":\"move\",\"param\":\"45\",\"type\":\"head_right\"}\"}";
    public static final String INTENT_HEAD_MIDDLE = "{\"cmd\":\"/robot/push\",\"command\":\"{\"cmd\":\"move\",\"param\":\"45\",\"type\":\"head_middle\"}\"}";
	public static final String INTENT_ALWAYS_FORWARD = "{\"cmd\":\"/robot/push\",\"command\":\"{\"cmd\":\"move\",\"param\":\"45\",\"type\":\"always_forward\"}\"}";
    public static final String INTENT_ALWAYS_BACK = "{\"cmd\":\"/robot/push\",\"command\":\"{\"cmd\":\"move\",\"param\":\"45\",\"type\":\"always_back\"}\"}";

    public static final String NOTIFICATION_QUERY = "android.intent.action.NOTIFICATION_QUERY";
    public static final String NOTIFICATION_UPDATE = "android.intent.action.NOTIFICATION_UPDATE";
    public static final String NOTIFICATION_ADD = "android.intent.action.NOTIFICATION_ADD";
    public static final String NOTIFICATION_DEL = "android.intent.action.NOTIFICATION_DEL";
    /*关机*/
    public static final String INTENT_SHUTDOWN = "com.yongyida.robot.SHUTDOWN";
    /*电量*/
    public static final String INTENT_BATTERY = "com.yongyida.robot.BATTERY";
    /*音量*/
    public static final String INTENT_SOUND = "com.yongyida.robot.SOUND";
    //显示文本
    public static final String INTENT_DISPLAY = "com.yongyida.robot.DISPLAY";
    //切换正式测试版本
    public static final String INTENT_SWITCH = "com.yongyida.robot. FORMALANDDEBUG";
    //查询用户信息返回的结果
    public static final String INTENT_USER_MESSAGE_RESULT = "com.yydrobot.qrcode.RESLUT";
    public static final String INTENT_USER_MESSAGE_QUERY = "com.yydrobot.qrcode.QUERY";
    public static final String INTENT_USER_MESSAGE_DELETE = "com.yydrobot.qrcode.DELETE";

    public static final String INTENT_FACTORYCLOSE = "com.yongyida.robot.FACTORYCLOSE";
    public static final String INTENT_FACTORYCSTART = "com.yongyida.robot.FACTORYSTART";
    public static final String INTENT_OPEN_VIDEO = "com.yydrobot.ENTERVIDEO";
    public static final String INTENT_CLOSE_VIDEO = "com.yydrobot.EXITVIDEO";
    public static final String INTENT_CLOSE_MONITOR = "com.yydrobot.EXITMONITOR";
    public static final String INTENT_OPEN_MONITOR = "com.yydrobot.ENTERMONITOR";
    public static final String INTENT_QUERY_RESULT = "com.yongyida.robot.notification.QUERY_RESULT";
    public static final String INTENT_RENAME = "com.yongyida.robot.RENAME";
    public static final String INTENT_TOUCH_SENSOR = "TouchSensor";
    //public static final String INTENT_RECYCLE = "com.yongyida.robot.RECYCLE";
    public static final String INTENT_RECYCLE = "com.yydrobot.RECYCLE";
    public static final String INTENT_TOUCH_DANCE = "dance";
    public static final String INTENT_PARSE_RESULT = "com.yongyida.robot.PARSE_RESULT";
    public static final String INTENT_FACE = "com.yongyida.robot.FACE";
    public static final String INTENT_VOICE = "com.yongyida.robot.VOICE";
    public static final String INTENT_MUTE = "com.yongyida.robot.MUTE";
    public static final String INTENT_WRITE_PCMDATA="com.yydrobot.MICTEST";
    //语音理解状态广播
    public static final String INTENT_VOICE_UNDERSTAND_STATUS = "com.yongyida.robot.voice.VOICE_UNDERSTAND";


}
