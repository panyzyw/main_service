package com.zccl.ruiqianqi.socket.remotesocket.handler.udpclient;

import com.zccl.ruiqianqi.tools.LogUtils;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * Created by ruiqianqi on 2017/1/23 0023.
 */

public class UDPClientDecoderHandler extends MessageToMessageDecoder<DatagramPacket> {

    // 类标志
    private static String TAG = UDPClientDecoderHandler.class.getSimpleName();

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

        // 加入数据处理链
        ctx.channel().pipeline().addLast(new UDPClientSimpleHandler());

        super.channelRegistered(ctx);
    }

    /**
     * 连接激活
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LogUtils.e(TAG, "channelActive");
        super.channelActive(ctx);
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
     * 读取数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LogUtils.e(TAG, "channelRead");
        super.channelRead(ctx, msg);
    }

    /**
     * 读取数据【预留的用户自定义处理接口】
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        // 这个缓冲区必须在这个方法释放，所以得另想办法传送数据了
        ByteBuf byteBuf = msg.copy().content();

        byte[] req = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(req);
        out.add(req);

        LogUtils.e(TAG, "decode");
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
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LogUtils.e(TAG, "exceptionCaught", cause);
        super.exceptionCaught(ctx, cause);
    }

    /**********************************【其他文方法】**********************************************/
    /**
     * 可写状态改变
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        LogUtils.e(TAG, "channelWritabilityChanged");
        super.channelWritabilityChanged(ctx);
    }

}
