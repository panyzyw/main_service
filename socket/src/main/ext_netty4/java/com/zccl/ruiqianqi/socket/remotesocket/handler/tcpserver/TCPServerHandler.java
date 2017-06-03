package com.zccl.ruiqianqi.socket.remotesocket.handler.tcpserver;

import android.content.Context;

import com.zccl.ruiqianqi.tools.LogUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by ruiqianqi on 2016/7/26 0026.
 */
public class TCPServerHandler extends SimpleChannelInboundHandler<String> {

    // 类标志
    private static String TAG = TCPServerHandler.class.getSimpleName();

    // 客户端连接集合
    private static Map<String, Channel> mClientChannelMap = new ConcurrentHashMap<>();

    /** 应用上下文 */
    private Context mContext;



    public TCPServerHandler(Context context){
        super(true);
        this.mContext = context;
    }

    /**************************************【第一个被调用】****************************************/
    /**
     * 可共享否
     * @return
     */
    @Override
    public boolean isSharable() {
        LogUtils.e(TAG, "isSharable");
        return super.isSharable();
    }

    /**************************************【建立连接】********************************************/
    /**
     * 事件处理已添加
     * 覆盖了 handlerAdded() 事件处理方法。
     * 每当服务端收到新的客户端连接时
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        LogUtils.e(TAG, "handlerAdded");
        super.handlerAdded(ctx);
    }

    /**
     * 注册连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        LogUtils.e(TAG, "channelRegistered");
        super.channelRegistered(ctx);
    }

    /**
     * 连接激活
     * 覆盖channelActive() 方法在channel被启用的时候触发（在建立连接的时候）
     * 覆盖channelActive() 事件处理方法。服务端监听到客户端活动
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LogUtils.e(TAG, "channelActive");
        super.channelActive(ctx);

        // 新增的连接，加入客户端集合
        String ip = getIpString(ctx);
        String address = getRemoteAddress(ctx);
        LogUtils.e(TAG, "new-client-ip = " + ip);
        LogUtils.e(TAG, "new-client-address = " + address);
        mClientChannelMap.put(ip, ctx.channel());

    }


    /*************************************【断开连接】*********************************************/
    /**
     * 检测到连接断开
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LogUtils.e(TAG, "channelInactive");
        super.channelInactive(ctx);

        // 移除对应的客户端服务对象
        String ip = getIpString(ctx);
        if(mClientChannelMap.containsKey(ip)) {
            mClientChannelMap.remove(ip);
        }
        ctx.close();

    }

    /**
     * 取消注册
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        LogUtils.e(TAG, "channelUnregistered");
        super.channelUnregistered(ctx);
    }

    /**
     * 事件处理已移除
     * 覆盖了 handlerRemoved() 事件处理方法。
     * 每当服务端收到客户端断开时
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        LogUtils.e(TAG, "handlerRemoved");
        super.handlerRemoved(ctx);
    }

    /**************************************【读取数据】********************************************/
    /**
     * 读取数据【预留的用户自定义处理接口】
     * 覆盖了 channelRead0() 事件处理方法。
     * 每当从服务端读到客户端写入信息时，
     * 其中如果你使用的是 Netty 5.x 版本时，
     * 需要把 channelRead0() 重命名为messageReceived()
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        LogUtils.e(TAG, "channelRead0 = " + msg);
    }

    /**
     * 读取完成
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        LogUtils.e(TAG, "channelReadComplete");
        super.channelReadComplete(ctx);
    }

    /************************************【用户事件触发】******************************************/
    /**
     * 用户事件触发
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        LogUtils.e(TAG, "userEventTriggered");
        super.userEventTriggered(ctx, evt);
    }

    /**********************************【异常处理】************************************************/
    /**
     * 异常处理
     * exceptionCaught() 事件处理方法是当出现 Throwable 对象才会被调用，
     * 即当 Netty 由于 IO 错误或者处理器在处理事件时抛出的异常时。
     * 在大部分情况下，捕获的异常应该被记录下来并且把关联的 channel 给关闭掉。
     * 然而这个方法的处理方式会在遇到不同异常的情况下有不同的实现，
     * 比如你可能想在关闭连接之前发送一个错误码的响应消息。
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LogUtils.e(TAG, "exceptionCaught", cause);
        super.exceptionCaught(ctx, cause);

        if(!ctx.channel().isActive()){

        }
    }

    /**
     * 远程客户端IP地址
     * @param ctx
     * @return
     */
    public static String getIpString(ChannelHandlerContext ctx){
        String ipString = "";
        String socketString = ctx.channel().remoteAddress().toString();
        int colonAt = socketString.indexOf(":");
        ipString = socketString.substring(1, colonAt);
        return ipString;
    }

    /**
     * 远程客户端的相关信息
     * @param ctx
     * @return
     */
    public static String getRemoteAddress(ChannelHandlerContext ctx){
        String socketString = "";
        socketString = ctx.channel().remoteAddress().toString();
        return socketString;
    }

}
