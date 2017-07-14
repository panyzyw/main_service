package com.zccl.ruiqianqi.config;

import com.google.gson.Gson;
import com.zccl.ruiqianqi.domain.model.dataup.LogCollectBack;
import com.zccl.ruiqianqi.domain.model.dataup.QueryBindUser;
import com.zccl.ruiqianqi.domain.model.dataup.QueryPhotosBack;
import com.zccl.ruiqianqi.domain.model.dataup.RemindBack;
import com.zccl.ruiqianqi.domain.model.dataup.RobotMediaBack;
import com.zccl.ruiqianqi.domain.model.robotup.FlushUpBattery;
import com.zccl.ruiqianqi.domain.model.robotup.FlushUpName;
import com.zccl.ruiqianqi.domain.model.robotup.LoginUp;
import com.zccl.ruiqianqi.domain.model.robotup.MediaForwardUp;
import com.zccl.ruiqianqi.tools.LogUtils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Created by ruiqianqi on 2017/3/8 0008.
 */

public class RemoteProtocol {

    // 类标志
    private static String TAG = RemoteProtocol.class.getSimpleName();
    // 协议返回成功
    public static final String RET_SUCCESS = "0";
    // 协议返回失败，说是-1，但是服务器返回1
    public static final String RET_FAILURE = "-1";

    /**********************************************************************************************/
    /*************************************【SOCKET】***********************************************/
    /**********************************************************************************************/
    /**************************【机器人主动与服务器交互的协议】************************************/
    // 机器人登录协议
    public static final String A_LOGIN = "/robot/login";
    // 信息刷新协议
    public static final String A_FLUSH = "/robot/flush";

    // 视频数据转发给服务器的协议
    public static final String A_MEDIA_FORWARD_2_SERVER = "/media/push";

    /**********************【机器人与手机：手机借服务器推送下发的协议】****************************/
    // 手机控制协议    【一级指令】
    public static final String A_CONTROL_ON = "/robot/controll";

    // 手机断开控制协议【一级指令】
    public static final String A_CONTROL_OFF = "/robot/uncontroll";

    // 服务器的协议转发给视频，多媒体【一级指令】
    public static final String A_MEDIA_FORWARD_2_VIDEO = "/media/callback";
    // 【环信】不需要主服务转发
    // 【声网】视频会议邀请  【视频-->主服务-->服务器-->手机】或【手机-->服务器-->主服务-->视频】
    public static final String B_AGORA_VIDEO_INVITE = "/avm/invite";
    public static final String B_AGORA_VIDEO_INVITE_ACK = "/avm/invite/response";
    public static final String B_AGORA_VIDEO_REPLY = "/avm/invite/reply";
    public static final String B_AGORA_VIDEO_REPLY_ACK = "/avm/invite/reply/response";
    public static final String B_AGORA_VIDEO_CANCEL = "/avm/invite/cancel";
    public static final String B_AGORA_VIDEO_CANCEL_ACK = "/avm/invite/cancel/response";
    // 【WebRTC】视频会议邀请 【视频-->主服务-->服务器-->手机】或【手机-->服务器-->主服务-->视频】
    public static final String B_WEB_RTC_VIDEO_INVITE = "/wvm/invite";
    public static final String B_WEB_RTC_VIDEO_INVITE_ACK = "/wvm/invite/response";
    public static final String B_WEB_RTC_VIDEO_REPLY = "/wvm/invite/reply";
    public static final String B_WEB_RTC_VIDEO_REPLY_ACK = "/wvm/invite/reply/response";
    public static final String B_WEB_RTC_VIDEO_CANCEL = "/wvm/invite/cancel";
    public static final String B_WEB_RTC_VIDEO_CANCEL_ACK = "/wvm/invite/cancel/response";
    // 视频会议报告     【视频-->主服务-->服务器-->手机】或【手机-->服务器-->主服务-->视频】
    public static final String B_MEETING_REPORT = "/media/meeting/report";
    // 视频会议报告响应 【视频-->主服务-->服务器-->手机】或【手机-->服务器-->主服务-->视频】
    public static final String B_MEETING_REPORT_ACK = "/media/meeting/report/response";

    // 【TUTK_ID】
    public static final String B_TUTK_GET_ID = "/robot/get/tutk_id";


    // 手机推送【一级指令】
    public static final String A_ORDER_PUSH = "/robot/push";

    // 文本问答【二级指令】
    public static final String B_TEXT_QUESTION = "text";

    // 同声说话【二级指令】
    public static final String B_TEXT_TALK = "talk";

    // 查询相册【二级指令】
    public static final String B_PHOTO_QUERY = "photo_query";
    // 查询类型为：获取照片列表【三级指令】
    public static final String TYPE_PHOTO_QUERY_LIST = "photo_query_list";
    // 查询类型为：获取照片缩略图【三级指令】
    public static final String TYPE_PHOTO_THUMBNAIL = "photo_query_thumbnail";
    // 查询类型为：获取照片原始图【三级指令】
    public static final String TYPE_PHOTO_ORIGINAL = "photo_query_original";
    // 查询类型为：删除指定照片集【三级指令】
    public static final String TYPE_PHOTO_DELETE = "photo_delete";

    // 插入提醒【二级指令】
    public static final String B_REMIND_INSERT= "remind_insert";
    // 删除提醒【二级指令】
    public static final String B_REMIND_DELETE = "remind_delete";
    // 更新提醒【二级指令】
    public static final String B_INSERT_UPDATE = "remind_updata";
    // 查询提醒【二级指令】
    public static final String B_REMIND_QUERY = "remind_query";

    // 移动【二级指令】
    public static final String B_MOVE = "move";
    // 前进【三级指令】
    public static final String TYPE_MOVE_FORWARD = "forward";
    // 后退【三级指令】
    public static final String TYPE_MOVE_BACK = "back";
    // 向左转【三级指令】
    public static final String TYPE_MOVE_TURN_LEFT = "turn_left";
    // 向右转【三级指令】
    public static final String TYPE_MOVE_TURN_RIGHT = "turn_right";
    // 停止【三级指令】
    public static final String TYPE_MOVE_STOP = "stop";

    // 抬起头【三级指令】
    public static final String TYPE_HEAD_UP = "head_up";
    // 低下头【三级指令】
    public static final String TYPE_HEAD_DOWN = "head_down";
    // 头向左转【三级指令】
    public static final String TYPE_HEAD_LEFT = "head_left";
    // 头向右转【三级指令】
    public static final String TYPE_HEAD_RIGHT = "head_right";
    // 头部回正【三级指令】
    public static final String TYPE_HEAD_MIDDLE = "head_middle";
    // 停止头部转动【三级指令】
    public static final String TYPE_HEAD_STOP = "head_stop";


    // 内容推送【二级指令】【新加的】
    public static final String B_CONTENT_PUSH = "content_push";
    // 推送的是音乐
    public static final String TYPE_CONTENT_MUSIC = "music";
    // 推送的是视频
    public static final String TYPE_CONTENT_VIDEO = "video";
    // 推送的是文字
    public static final String TYPE_CONTENT_TEXT = "text";

    // 音乐推送【二级指令】【新加的】
    public static final String B_PUSH_MUSIC = "playAll";
    // 音乐播放控制推送【二级指令】【新加的】
    public static final String B_PUSH_MUSIC_CTRL = "play_control";

    // 定时关机【二级指令】【新加的】
    public static final String B_PUSH_TIMED_SHUTDOWN = "timed_shutdown";



    // 手机推送的返回指令【一级指令】
    public static final String A_ORDER_CALLBACK = "/robot/callback";
    // 查询提醒的返回指令    【二级指令】
    public static final String B_REMIND_RESULT = "remind_result";
    // 查询所有照片的返回指令【二级指令】
    public static final String B_PHOTO_NAMES_RESULT = "photo_names";
    // 查询单个照片的返回指令【二级指令】
    public static final String B_PHOTO_QUERY_RESULT = "photo_query";
    // 机器人向手机推送的指令【二级指令】
    public static final String B_ROBOT_MEDIA_CONTROL = "robot_media_control";
    // 用户操作日志收集【二级指令】
    public static final String B_LOG_COLLECT = "log_collect";

    // 查询绑定用户列表【一级指令】
    public static final String A_BINDER_USER_QUERY = "/robot/bind/list";
    // 删除绑定用户【一级指令】
    public static final String A_BINDER_USER_DELETE = "/robot/unbind";


    /**********************************************************************************************/
    /***************************************【HTTP】***********************************************/
    /**********************************************************************************************/
    // 添加机器人朋友地址
    public static final String ADD_ROBOT = "/friends/robot/addRobot";
    // 删除机器人朋友地址
    public static final String DEL_ROBOT = "/friends/robot/delRobot";
    // 操作机器人朋友
    public static final String ROBOT_TYPE = "Robot";
    // 添加手机朋友地址
    public static final String ADD_PHONE = "/friends/robot/addUser";
    // 删除手机朋友地址
    public static final String DEL_PHONE = "/friends/robot/delUser";
    // 操作手机朋友
    public static final String PHONE_TYPE = "Phone";

    // 添加机器人朋友上行参数
    public static final String[] ADD_ROBOT_ARGS = {
            ADD_ROBOT,
            "id",
            "serial",
            "frid"
    };
    // 添加手机朋友上行参数
    public static final String[] ADD_PHONE_ARGS = {
            ADD_PHONE,
            "robot_id",
            "robot_serial",
            "phone"
    };

    // 添加机器人朋友上行参数
    public static final String[] DEL_ROBOT_ARGS = {
            DEL_ROBOT,
            "id",
            "serial",
            "frid"
    };
    // 添加手机朋友上行参数
    public static final String[] DEL_PHONE_ARGS = {
            DEL_PHONE,
            "robot_id",
            "robot_serial",
            "phone"
    };

    /**
     * 1、堆内存（HeapByteBuf）字节缓冲区：特点是内存的分配和回收速度快，可以被JVM自动回收；
     * 缺点就是如果进行Socket的IO读写，需要额外做一次内存复制，将堆内存对应的缓冲区复制到内核Channel中，
     * 性能会有一定程度的下降
     *
     * 2、直接内存（DirectByteBuf） 字节缓冲区：非堆内存，它在对外进行内存分配，相比于堆内存，
     * 它的分配和回收速度会慢一些，但是将它写入或者从Socket Channel中读取时，由于少一次内存复制，速度比堆内存快
     *
     * Netty的最佳实践是在I/O通信线程的读写缓冲区使用DirectByteBuf，后端业务消息的编解码模块使用HeapByteBuf，
     * 这样组合可以达到性能最优。
     *
     * ByteBuf提供了一些较为丰富的实现类，逻辑上主要分为两种：HeapByteBuf和DirectByteBuf，
     * 实现机制则分为两种：PooledByteBuf和UnpooledByteBuf，
     * 除了这些之外，Netty还实现了一些衍生ByteBuf（DerivedByteBuf），
     * 如：ReadOnlyByteBuf、DuplicatedByteBuf、SlicedByteBuf、SwappedByteBuf、CompositedByteBuf
     *
     * 构造登录上行数据格式
     * @param loginUp
     * @return
     */
    /*
    public static ByteBuf buildLoginUp(LoginUp loginUp){

        //ByteBuf heapBuffer = Unpooled.buffer(1024);
        //ByteBuf directBuffer = Unpooled.directBuffer();
        //ByteBuf wrappedBuffer = Unpooled.wrappedBuffer(new byte[128]);
        //ByteBuf copiedBuffer = Unpooled.copiedBuffer(new byte[128]);

        // 底下的byte[]能够依赖JVM GC自然回收
        //UnpooledHeapByteBuf unpooledHeapByteBuf;
        // 底下是DirectByteBuffer，如 Java堆外内存扫盲贴所述 ，除了等JVM GC，最好也能主动进行回收；
        //UnpooledDirectByteBuf unpooledDirectByteBuf;
        // 则必须要主动将用完的byte[]/ByteBuffer放回池里，否则内存就要爆掉。
        //PooledHeapByteBuf pooledHeapByteBuf;
        // 则必须要主动将用完的byte[]/ByteBuffer放回池里，否则内存就要爆掉。
        //PooledDirectByteBuf pooledDirectByteBuf;

        return buildMsg(loginUp);
    }
    */

    /**
     * 构造电池电量数据上行格式
     * @param flushUpBattery
     * @return
     */
    /*
    public static ByteBuf buildFlushUpBattery(FlushUpBattery flushUpBattery){
        return buildMsg(flushUpBattery);
    }
    */

    /**
     * 构造机器人名字数据上行格式
     * @param flushUpName
     * @return
     */
    /*
    public static ByteBuf buildFlushUpName(FlushUpName flushUpName){
        return buildMsg(flushUpName);
    }
    */

    /**
     * 构造视频转发数据上行格式
     * @param mediaForwardUp
     * @return
     */
    /*
    public static ByteBuf buildMediaForwardUp(MediaForwardUp mediaForwardUp){
        return buildMsg(mediaForwardUp);
    }
    */

    /**
     * 构造推送指令：查询提醒，返回的数据上行格式
     * @param remindBack
     * @return
     */
    /*
    public static ByteBuf buildRemindBack(RemindBack remindBack){
        return buildMsg(remindBack);
    }
    */

    /**
     * 构造推送指令：查询所有照片，返回的数据上行格式
     * @param queryPhotosBack
     * @return
     */
    /*
    public static ByteBuf buildPhotoNamesBack(QueryPhotosBack queryPhotosBack){
        return buildMsg(queryPhotosBack);
    }
    */

    /**
     * 00000000000
     * 返回心跳协议
     * @return
     */
    /*
    public static ByteBuf buildHeart(){
        ByteBuf heapBuffer = Unpooled.buffer(1024);
        byte[] header = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
        heapBuffer.writeBytes(header);
        return heapBuffer;
    }
    */

    /**
     * 111111111111
     * 构造上行JSON数据格式
     * @param object
     * @return
     */
    /*
    private static ByteBuf buildMsg(Object object){
        ByteBuf heapBuffer = Unpooled.buffer(1024);
        byte[] header = new byte[]{1, 0, 0, 0, 0, 0, 0, 0};
        heapBuffer.writeBytes(header);
        Gson gson = new Gson();
        byte[] body = gson.toJson(object).getBytes(CharsetUtil.UTF_8);
        heapBuffer.writeInt(body.length);
        heapBuffer.writeBytes(body);
        LogUtils.e(TAG, "SEND1 = " + gson.toJson(object));
        return heapBuffer;
    }
    */

    /**
     * 2222222222222
     * 构造照片数据上传协议
     * @param queryPhotosBack
     * @param photoData
     * @return
     */
    /*
    public static ByteBuf buildPhotoDataBack(QueryPhotosBack queryPhotosBack, byte[] photoData){
        ByteBuf heapBuffer = Unpooled.buffer(1024);
        byte[] header = new byte[]{2, 0, 0, 0, 0, 0, 0, 0};
        heapBuffer.writeBytes(header);
        Gson gson = new Gson();
        byte[] bodyJson = gson.toJson(queryPhotosBack).getBytes(CharsetUtil.UTF_8);
        // 协议总长度
        heapBuffer.writeInt(8 + bodyJson.length + photoData.length);
        // 描述信息长度
        heapBuffer.writeInt(bodyJson.length);
        // 描述信息
        heapBuffer.writeBytes(bodyJson);
        // 照片数据长度
        heapBuffer.writeInt(photoData.length);
        // 照片数据
        heapBuffer.writeBytes(photoData);
        LogUtils.e(TAG, "SEND2 = " + gson.toJson(queryPhotosBack));
        return heapBuffer;
    }
    */

    /***********************************【利用NIO构造通用协议】************************************/
    /**
     * 00000000000
     * 返回心跳协议
     * @return
     */
    public static ByteBuffer buildHeart2(){
        ByteBuffer heapBuffer = ByteBuffer.allocate(16);
        byte[] header = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
        heapBuffer.put(header);
        // 设置为读模式
        heapBuffer.flip();
        return heapBuffer;
    }

    /**
     * 111111111111
     * 构造上行JSON数据格式
     * @param object
     * @return
     */
    private static ByteBuffer buildMsg2(Object object){
        byte[] header = new byte[]{1, 0, 0, 0, 0, 0, 0, 0};
        Gson gson = new Gson();
        byte[] body = gson.toJson(object).getBytes(Charset.defaultCharset());
        ByteBuffer heapBuffer = ByteBuffer.allocate(body.length + 16);
        heapBuffer.put(header);
        heapBuffer.putInt(body.length);
        heapBuffer.put(body);
        // 设置为读模式
        heapBuffer.flip();
        LogUtils.e(TAG, "SEND1 = " + gson.toJson(object));
        return heapBuffer;
    }

    /**
     * 111111111111
     * 构造上行JSON数据格式
     * @param str
     * @return
     */
    public static ByteBuffer buildMsg2(String str){
        byte[] header = new byte[]{1, 0, 0, 0, 0, 0, 0, 0};
        byte[] body = str.getBytes(Charset.defaultCharset());

        ByteBuffer heapBuffer = ByteBuffer.allocate(body.length + 16);
        heapBuffer.put(header);
        heapBuffer.putInt(body.length);
        heapBuffer.put(body);
        // 设置为读模式
        heapBuffer.flip();
        LogUtils.e(TAG, "SEND1.5 = " + str);
        return heapBuffer;
    }

    /**
     * 2222222222222
     * 构造照片数据上传协议
     * @param queryPhotosBack
     * @param photoData
     * @return
     */
    public static ByteBuffer buildPhotoDataBack2(QueryPhotosBack queryPhotosBack, byte[] photoData){

        byte[] header = new byte[]{2, 0, 0, 0, 0, 0, 0, 0};
        Gson gson = new Gson();
        byte[] bodyJson = gson.toJson(queryPhotosBack).getBytes(Charset.defaultCharset());
        ByteBuffer heapBuffer = ByteBuffer.allocate(32 + bodyJson.length + photoData.length);

        heapBuffer.put(header);
        // 协议总长度
        heapBuffer.putInt(8 + bodyJson.length + photoData.length);
        // 描述信息长度
        heapBuffer.putInt(bodyJson.length);
        // 描述信息
        heapBuffer.put(bodyJson);
        // 照片数据长度
        heapBuffer.putInt(photoData.length);
        // 照片数据
        heapBuffer.put(photoData);
        // 设置为读模式
        heapBuffer.flip();
        LogUtils.e(TAG, "SEND2 = " + gson.toJson(queryPhotosBack));
        return heapBuffer;
    }

    /**
     * 构造登录上行格式
     * @param loginUp
     * @return
     */
    public static ByteBuffer buildLoginUp2(LoginUp loginUp){
        return buildMsg2(loginUp);
    }

    /**
     * 构造电池电量数据上行格式
     * @param flushUpBattery
     * @return
     */
    public static ByteBuffer buildFlushUpBattery2(FlushUpBattery flushUpBattery){
        return buildMsg2(flushUpBattery);
    }

    /**
     * 构造机器人名字数据上行格式
     * @param flushUpName
     * @return
     */
    public static ByteBuffer buildFlushUpName2(FlushUpName flushUpName){
        return buildMsg2(flushUpName);
    }

    /**
     * 构造视频转发数据上行格式
     * @param mediaForwardUp
     * @return
     */
    public static ByteBuffer buildMediaForwardUp2(MediaForwardUp mediaForwardUp){
        return buildMsg2(mediaForwardUp);
    }

    /**
     * 构造推送指令：查询提醒，返回的数据上行格式
     * @param remindBack
     * @return
     */
    public static ByteBuffer buildRemindBack2(RemindBack remindBack){
        return buildMsg2(remindBack);
    }

    /**
     * 构造推送指令：查询所有照片，返回的数据上行格式
     * @param queryPhotosBack
     * @return
     */
    public static ByteBuffer buildPhotoNamesBack2(QueryPhotosBack queryPhotosBack){
        return buildMsg2(queryPhotosBack);
    }

    /**
     * 构造查询绑定用户的上行指令
     * @param queryBindUser
     * @return
     */
    public static ByteBuffer buildQueryBindUser2(QueryBindUser queryBindUser){
        return buildMsg2(queryBindUser);
    }

    /**
     * 构造机器人向手机推送的上行指令
     * @param robotMediaBack
     * @return
     */
    public static ByteBuffer buildRobotMediaBack2(RobotMediaBack robotMediaBack){
        return buildMsg2(robotMediaBack);
    }

    /**
     * 构造用户操作日志上行指令
     * @param logCollectBack
     * @return
     */
    public static ByteBuffer buildLogCollectBack2(LogCollectBack logCollectBack){
        return buildMsg2(logCollectBack);
    }
}
