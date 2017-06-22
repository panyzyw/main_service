package com.zccl.ruiqianqi.brain.semantic;

/*
 * Created by ruiqianqi on 2017/3/27 0027.
 */
public class FuncIntent {

    /*********************************【不是发给master】*******************************************/
    // 翻译 -------------发给了【YYDRobotTranslation】
    public static final String INTENT_TRANSLATION = "com.yydrobot.TRANSLATION";
    // 音量控制----------发给了【YYDRobotVoiceBattery】
    public static final String INTENT_SOUND = "com.yongyida.robot.SOUND";
    // 相册--------------发给了【YYDRobotPhotos】
    public static final String INTENT_PHOTO = "com.yydrobot.PHOTOS";
    // 拍照 -------------发给了【YYDRobotVoiceCamera】
    public static final String INTENT_CAMERA = "com.yydrobot.CAMERA";
    // 电量--------------发给了【YYDRobotVoiceBattery】
    public static final String INTENT_BATTERY = "com.yongyida.robot.BATTERY";
    // 关机--------------发给了【YYDRobotVoiceBattery】
    public static final String INTENT_SHUTDOWN = "com.yongyida.robot.SHUTDOWN";
    // 人脸识别----------发给了【？？？？？？】
    public static final String INTENT_FACE = "com.yongyida.robot.FACE";
    // 表情聊天----------发给了【YYDRobotLockScreen】
    public static final String INTENT_EMOTION_CHAT = "com.yydrobot.emotion.CHAT";

    /**********************************【主服务自己处理】******************************************/
    // 显示文本----------主服务自己处理了
    //public static final String INTENT_DISPLAY = "com.yongyida.robot.DISPLAY";
    // 让机器人安静------主服务自己处理了
    //public static final String INTENT_MUTE = "com.yongyida.robot.MUTE";

    /*************************************【没有处理】*********************************************/
    // 发短信
    // 电视控制
    // 空调控制
    // 方言
    // 读短信
    // 表情显示

    /*********************************【发给master处理】*******************************************/
    // 红外感应的开关、二维码、打开测试界面
    public static final String INTENT_YYDCHAT = "com.yydrobot.YYDCHAT";

    // TEST
    public static final String INTENT_TEST = "com.yydrobot.TEST";
    // 打电话
    public static final String INTENT_CALL = "com.yydrobot.CALL";
    // 提醒
    //public static final String INTENT_REMIND = "com.yydrobot.REMIND";
    // 提醒
    public static final String INTENT_SCHEDULE = "com.yydrobot.SCHEDULE";
    // 地图
    public static final String INTENT_MAP = "com.yydrobot.MAP";
    // 股票
    public static final String INTENT_STOCK = "com.yydrobot.STOCK";
    // 天气
    public static final String INTENT_WEATHER = "com.yydrobot.WEATHER";
    // 音乐
    public static final String INTENT_MUSIC = "com.yydrobot.MUSIC";
    // 菜谱
    public static final String INTENT_COOKBOOK = "com.yydrobot.COOKBOOK";
    // 广场舞
    public static final String INTENT_SQUARE_DANCE = "com.yydrobot.SQUAREDANCE";
    // 诗词学习
    public static final String INTENT_STUDY = "com.yydrobot.STUDY";
    // 诗词学习
    //public static final String INTENT_POETRY_STUDY = "com.yydrobot.POETRY_STUDY";
    // 国学知识
    public static final String INTENT_SINOLOGY = "com.yydrobot.SINOLOGY";
    // 国学知识
    //public static final String INTENT_POETRY = "com.yydrobot.POETRY";
    // 影视资讯
    public static final String INTENT_MOVIE_INFO = "com.yydrobot.MOVIEINFO";
    // 戏曲
    public static final String INTENT_OPERA = "com.yydrobot.OPERA";
    // 健康养生
    public static final String INTENT_HEALTH = "com.yydrobot.HEALTH";
    // 故事
    public static final String INTENT_STORY = "com.yydrobot.STORY";
    // 跳舞
    public static final String INTENT_DANCE = "com.yydrobot.DANCE";
    // 笑话
    public static final String INTENT_JOKE = "com.yydrobot.JOKE";
    // 新闻
    public static final String INTENT_NEWS = "com.yydrobot.NEWS";
    // 习惯养成
    public static final String INTENT_HABIT = "com.yydrobot.HABIT";
    // 算术
    public static final String INTENT_ARITHMETIC = "com.yydrobot.ARITHMETIC";
    // 百科
    public static final String INTENT_ENCYCLOPEDIAS = "com.yydrobot.ENCYCLOPEDIAS";
    // 聊天、社区问答、开放语义、日期
    public static final String INTENT_CHAT = "com.yydrobot.CHAT";
    // 智能家居
    public static final String INTENT_SMART_HOME = "com.yydrobot.SMARTHOME";
    // 跟机器人说学习算术
    public static final String INTENT_QUESTION = "com.yydrobot.QUSETION";


    /*************************************【其他广播】*********************************************/
    // 播放提醒内容时停止其它语音【提醒发给master】，为什么不用INTENT_STOP呢，因为提醒时间到的时候，如果正在播放
    // 其他内容的话，就该停止其他的播放，而INTENT_STOP是停止所有音频播放、视频播放、动作等，这样就把自己也停止了
    // 所以重新启用一个广播
    //public static final String INTENT_STOP_OTHER = "com.yydrobot.STOPOTHER";
    // 这是干什么的，不知道
    public static final String INTENT_ACHT_ALL = "com.yydrobot.ACHT_ALL";
    // 摄影
    public static final String INTENT_RECORD = "com.yydrobot.SHOOT";
    // 旅游
    public static final String INTENT_TRIP = "com.yydrobot.TRIP";
    // 电子书
    public static final String INTENT_EBOOK = "com.yydrobot.EBOOK";
    // 影视资讯
    public static final String INTENT_VIDEO = "com.yydrobot.VIDEO";
    // 取消提醒
    public static final String INTENT_CANCEL = "com.yydrobot.CANCEL";
    // 游戏
    public static final String INTENT_GAME = "com.yydrobot.GAME";
    // 美容
    public static final String INTENT_COSMETOLOGY = "com.yydrobot.COSMETOLOGY";

}
