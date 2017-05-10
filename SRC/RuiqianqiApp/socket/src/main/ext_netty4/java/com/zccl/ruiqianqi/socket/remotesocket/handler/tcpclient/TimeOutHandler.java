package com.zccl.ruiqianqi.socket.remotesocket.handler.tcpclient;

import com.zccl.ruiqianqi.socket.eventbus.SocketBusEvent;
import com.zccl.ruiqianqi.tools.LogUtils;

import org.greenrobot.eventbus.EventBus;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;

import static com.zccl.ruiqianqi.socket.eventbus.SocketBusEvent.IdleEvent.REQUEST_HEART;

/**
 * Created by ruiqianqi on 2016/12/3 0003.
 */

public class TimeOutHandler extends ChannelInboundHandlerAdapter {

    // 类标志
    private static String TAG = TimeOutHandler.class.getSimpleName();
    // AttributeKey
    private final AttributeKey counter = AttributeKey.valueOf("counter");

    /**
     * 一段时间未进行读写操作的回调
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);

        ctx.channel().attr(counter);

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                // 未进行读操作
                LogUtils.e(TAG, "READER_IDLE");


            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                // 未进行写操作
                LogUtils.e(TAG, "WRITER_IDLE");

                // 发心跳包
                SocketBusEvent.IdleEvent idleEvent = new SocketBusEvent.IdleEvent();
                idleEvent.setCmd(REQUEST_HEART);
                EventBus.getDefault().post(idleEvent);

            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                // 未进行读写
                LogUtils.e(TAG, "ALL_IDLE");
            }
        }
    }
}
