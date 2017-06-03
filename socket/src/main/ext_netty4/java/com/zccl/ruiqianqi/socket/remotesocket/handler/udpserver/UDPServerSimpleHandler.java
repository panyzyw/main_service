package com.zccl.ruiqianqi.socket.remotesocket.handler.udpserver;

import com.zccl.ruiqianqi.tools.LogUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * Created by ruiqianqi on 2017/1/23 0023.
 */

public class UDPServerSimpleHandler extends SimpleChannelInboundHandler<byte[]> {

    // 类标志
    private static String TAG = UDPServerSimpleHandler.class.getSimpleName();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        String body = new String(msg, CharsetUtil.UTF_8);
        LogUtils.e(TAG, body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LogUtils.e(TAG, "exceptionCaught", cause);
        super.exceptionCaught(ctx, cause);
    }

}
