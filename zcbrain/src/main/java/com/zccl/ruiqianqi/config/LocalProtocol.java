package com.zccl.ruiqianqi.config;

/**
 * Created by ruiqianqi on 2017/3/14 0014.
 */

public class LocalProtocol {

    // 视频会议登录【来】
    public static final String LOGIN_GET = "/localserver/login";
    // 视频会议登录【去】
    public static final String LOGIN_PUSH = "/localserver/login/callback";

    // 视频会议添加朋友【来】
    public static final String ADD_FRIEND_GET = "/media/friend/add";
    // 视频会议添加朋友【去】
    public static final String ADD_FRIEND_PUSH = "/media/friend/add/response";

    // 视频会议移除朋友【来】
    public static final String REMOVE_FRIEND_GET = "/media/friend/remove";
    // 视频会议移除朋友【去】
    public static final String REMOVE_FRIEND_PUSH = "/media/friend/remove/response";



    // 接收到手机端发送的多媒体命令【转发给推送点播】
    public static final String ACTION_MEDIA_RECV = "com.yongyida.media.recv";
    // 往手机端发送多媒体命令【推送点播通过主服务发送给服务器，再转给手机】
    public static final String ACTION_MEDIA_SEND = "com.yongyida.media.SEND";
    // 携带的数据KEY
    public static final String KEY_MEDIA_RESULT = "media_result";

    // 用户操作日志收集
    public static final String ACTION_LOG_COLLECT = "com.yongyida.robot.COLLECT";
    // 日志收集来源
    public static final String KEY_COLLECT_FROM = "collect_from";
    // 日志收集的数据结果
    public static final String KEY_COLLECT_RESULT = "collect_result";

    // 其他应用发给主服务的数据及回调
    public static final String ACTION_MAIN_RECV = "com.yongyida.robot.MAIN_RECV";
    // 发给电池应用的数据通知
    public static final String ACTION_BATTERY_RECV = "com.yongyida.robot.BATTERY_RECV";
    // 来自何处
    public static final String KEY_MAIN_RECV_FROM = "from";
    // 是什么功能
    public static final String KEY_MAIN_RECV_FUNCTION = "function";
    // 携带参数
    public static final String KEY_MAIN_RECV_RESULT = "result";
}
