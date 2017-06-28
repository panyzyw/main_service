package com.zccl.ruiqianqi.config;

import static com.zccl.ruiqianqi.config.RemoteProtocol.RET_FAILURE;
import static com.zccl.ruiqianqi.config.RemoteProtocol.RET_SUCCESS;

/**
 * Created by ruiqianqi on 2017/3/10 0010.
 */

public class MyConfig {
    // 机器人默认ID
    public static final String STATE_DEFAULT_ID = "123456";
    // 机器人默认SID
    public static final String STATE_DEFAULT_SID = "123456";
    // 机器人默认RID，运行时服务器分配的机器人ID
    public static final String STATE_DEFAULT_RID = "123456";

    // 使用这个TAG的话，我就不处理语音的回调逻辑
    public static final String TTS_NOT_DEAL_RESPONSE = "tts_not_deal_response";

    // 默认状态，无任何动作
    public final static String STATE_LOGIN_DEFAULT = "DEFAULT";
    // 帐号异常【就不能玩】
    public final static String STATE_ACCOUNT_EXCEPTION = "ACCOUNT_EXCEPTION";
    // 帐号正常【正常才能玩】【正常以后，就只有下面四种状态】
    public final static String STATE_ACCOUNT_OKAY = "ACCOUNT_OKAY";
    // 连接上不服务器【平行】
    public final static String STATE_CONNECT_EXCEPTION = "CONNECT_EXCEPTION";
    // 连接上服务器了，断开了【平行】
    public final static String STATE_CONNECT_OFF = "CONNECT_OFF";
    // 连接成功，正在登录
    public final static String STATE_LOGIN_ING = "LOGIN_ING";
    // 登录成功【平行】
    public final static String STATE_LOGIN_SUCCESS = RET_SUCCESS;
    // 登录失败【平行】
    public final static String STATE_LOGIN_FAILURE = RET_FAILURE;


    // 结束任务的广播
    public static final String INTENT_ACTION_STOP = "com.yydrobot.STOP";
    // 结束其他任务时，命令来自何处的KEY
    public static final String KEY_STOP_FROM = "stop_from";

    // 只要有结果就全局发送的广播
    public static final String INTENT_ACTION_OVERALL = "com.yongyida.robot.PARSE_RESULT";
    // 全局发送的广播时，携带的结果的KEY
    public static final String KEY_OVERALL_RESULT = "result";

    // 广播给子服务时，携带的结果的KEY
    public static final String KEY_RESULT = "result";
    // 广播给子服务时，携带的视频状态的KEY
    public static final String KEY_VIDEO = "video";


    /** 翻译地址，约定：后面一定要加“/” */
    public static final String TRANSLATE_URL = "http://fanyi.youdao.com/";
    /** 翻译后面跟的参数 */
    public static final String TRANSLATE_PARAM = "openapi.do?keyfrom=yongyidajiqiren&key=1993764552&type=data&doctype=json&version=1.1&only=translate";

    // 薄言语义请求地址
    public static final String BO_YAN_URI = "boyan_chat/query";



    // 喜马拉雅申请的应用私钥【现在用的还是测试的】
    public static final String AppSecret = "4d8e605fa7ed546c4bcb33dee1381179";
    // 音乐
    public static final String MUSIC = "2";
    // 电台
    public static final String RADIO = "17";
    // 文学
    public static final String LITERATURE = "9";
    // 儿童
    public static final String CHILD = "6";
    // 歌剧
    public static final String OPERA = "16";
    // 健康
    public static final String HEALTH = "7";
    // 脱口秀
    public static final String TALK_SHOW = "28";
    // 情感
    public static final String EMOTION_LIEF = "10";
    // 财经
    public static final String FINANCE = "8";
    // 讲道
    public static final String PULPIT = "14";
    // 教育
    public static final String EDUCATION = "13";
    // 诗歌
    public static final String POETRY = "34";
    // 相声
    public static final String CROSSTALK = "12";

    // 音乐
    public static final String MUSIC_SERVER = "music/query";
    public static final String MUSIC_RESOURCE = "resource/music/";

    // 笑话
    public static final String JOKE_SERVER = "joke/query";
    public static final String JOKE_RESOURCE = "resource/joke/";

    // 故事
    public static final String STORY_SERVER = "story/query";
    public static final String STORY_RESOURCE = "resource/story/";

    // 歌剧
    public static final String OPERA_SERVER = "chinese_opera/query";
    public static final String OPERA_RESOURCE = "resource/chinese_opera/";

    // 诗词
    public static final String POETRY_SERVER = "poetry/query";
    public static final String POETRY_RESOURCE = "resource/poetry/";

    // 广场舞
    public static final String SQUARE_DANCE_SERVER = "resource/square_dance.list";
    public static final String SQUARE_DANCE_RESOURCE = "resource/square_dance/";

    // 养生
    public static final String HEALTH_SERVER = "resource/health.list";
    public static final String HEALTH_RESOURCE = "resource/health/";

    // 习惯养成
    public static final String HABIT_SERVER = "resource/habit.list";
    public static final String HABIT_RESOURCE = "resource/habit/";

    // 电影信息
    public static final String MOVIE_SERVER = "screen/query/";
    // 新闻
    public static final String NEWS_SERVER = "news/query/";
    // 天气
    public static final String WEATHER_SERVER = "weather/query/";
    // 菜单
    public static final String MENU_SERVER = "menu/query/";
    // 股票
    public static final String STOCK_SERVER = "stock/query/";
    // 算术
    public static final String ARITHMETIC_SERVER = "arithmetic/question/";


    // 离线音乐
    public static final String OFF_LINE_MUSIC = "<music>";
    // 离线视频
    public static final String OFF_LINE_VIDEO = "<medias>";
    // 离线拍照
    public static final String OFF_LINE_PICTURE = "<picture>";
    public static final String OFF_LINE_CAMERA = "<camera>";
    // 离线关机
    public static final String OFF_LINE_SHUTDOWN = "<shutdown>";
    // 离线查看照片
    public static final String OFF_LINE_PHOTOS = "<photos>";
    // 离线游戏
    public static final String OFF_LINE_GAME = "<game>";
    // 离线查看二维码
    public static final String OFF_LINE_QRCODE_SCAN = "<qrCodeScan>";
    // 离线打开设置
    public static final String OFF_LINE_SETTINGS = "<settings>";
    // 离线打开工具
    public static final String OFF_LINE_TOOLS = "<tools>";
    // 离线打开LAUNCHER
    public static final String OFF_LINE_LAUNCHER = "<launcher>";
    // 离线回到主界面
    public static final String OFF_LINE_HOME = "<home>";
    // 离线调整音量到最大、最小
    public static final String OFF_LINE_POLE = "<pole>";
    // 离线加大音量
    public static final String OFF_LINE_VOICE_UP = "<voiceUp>";
    // 离线减小音量
    public static final String OFF_LINE_VOICE_DOWN = "<voiceDown>";
    // 自定义问答
    public static final String OFF_LINE_UPDATE_WORDS = "<updateWords>";

    // 离线拍照解析指令
    public static final String SEMANTIC_CAMERA = "{\"service\":\"camera\",\"text\":\"拍照。\",\"semantic\":{},\"rc\":0}";
    // 离线关机解析指令
    public static final String SEMANTIC_SHUTDOWN = "{\"semantic\":{},\"rc\":0,\"operation\":\"poweroff\",\"service\":\"shutdown\",\"text\":\"关机。\"}";
    // 离线查看照片解析指令
    public static final String SEMANTIC_PHOTO = "{\"semantic\":{\"slots\":{\"photo\":\"相册\"}},\"rc\":0,\"operation\":\"query\",\"service\":\"photos\",\"text\":\"打开相册。\"}";
    // 离线下载二维码解析指令
    public static final String SEMANTIC_DOWNLOAD_QR = "{\"semantic\":{},\"rc\":0,\"operation\":\"qrcode_download\",\"service\":\"yydchat\",\"text\":\"下载二维码。\"}";
    // 离线绑定二维码解析指令
    public static final String SEMANTIC_BIND_QR = "{\"semantic\":{},\"rc\":0,\"operation\":\"qrcode_bind\",\"service\":\"yydchat\",\"text\":\"绑定二维码。\"}";

    // 充电语义【电量低了，求求帮我充电】
    public static final String SEMANTIC_CHARGE = "{\"semantic\":{\"slots\":{\"action\":\"hungry\",\"answer\":\"\"}},\"rc\":0,\"operation\":\"emotionchat\",\"service\":\"chat\",\"text\":\"充电。\"}";
}
