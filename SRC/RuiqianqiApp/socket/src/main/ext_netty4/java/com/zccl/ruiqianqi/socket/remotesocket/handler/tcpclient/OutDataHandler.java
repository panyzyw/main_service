package com.zccl.ruiqianqi.socket.remotesocket.handler.tcpclient;

import com.zccl.ruiqianqi.tools.LogUtils;

import java.net.SocketAddress;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * Created by ruiqianqi on 2017/2/5 0005.
 */

public class OutDataHandler extends ChannelOutboundHandlerAdapter {

    private static String TAG = OutDataHandler.class.getSimpleName();

    @Override
    public boolean isSharable() {
        LogUtils.e(TAG, "isSharable");
        return super.isSharable();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        LogUtils.e(TAG, "handlerAdded");
        super.handlerAdded(ctx);
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        LogUtils.e(TAG, "bind");
        super.bind(ctx, localAddress, promise);
    }

    /**
     * channelRegistered
     * @param ctx
     * @param remoteAddress
     * @param localAddress
     * @param promise
     * @throws Exception
     */
    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        LogUtils.e(TAG, "connect");
        super.connect(ctx, remoteAddress, localAddress, promise);
    }

    /**
     * channelActive、有数据到来、连接断开
     * @param ctx
     * @throws Exception
     */
    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        LogUtils.e(TAG, "read");
        super.read(ctx);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        LogUtils.e(TAG, "write");
        super.write(ctx, msg, promise);

    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        LogUtils.e(TAG, "flush");
        super.flush(ctx);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        LogUtils.e(TAG, "disconnect");
        super.disconnect(ctx, promise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        LogUtils.e(TAG, "deregister");
        super.deregister(ctx, promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        LogUtils.e(TAG, "close");
        super.close(ctx, promise);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        LogUtils.e(TAG, "handlerRemoved");
        super.handlerRemoved(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LogUtils.e(TAG, "exceptionCaught", cause);
        super.exceptionCaught(ctx, cause);
    }

}
