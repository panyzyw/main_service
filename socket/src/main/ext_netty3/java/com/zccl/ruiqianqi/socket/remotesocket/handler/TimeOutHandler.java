package com.zccl.ruiqianqi.socket.remotesocket.handler;

import com.zccl.ruiqianqi.socket.eventbus.SocketBusEvent;
import com.zccl.ruiqianqi.tools.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;

import static com.zccl.ruiqianqi.socket.eventbus.SocketBusEvent.IdleEvent.REQUEST_HEART;

/**
 * Created by ruiqianqi on 2017/4/8 0008.
 */

public class TimeOutHandler extends IdleStateAwareChannelHandler {

    // 类标志
    private static String TAG = TimeOutHandler.class.getSimpleName();

    @Override
    public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception {
        super.channelIdle(ctx, e);
        if (IdleState.READER_IDLE == e.getState()) {
            // 未进行读操作
            LogUtils.e(TAG, "READER_IDLE");


        } else if (IdleState.WRITER_IDLE == e.getState()) {
            // 未进行读操作
            LogUtils.e(TAG, "WRITER_IDLE");

            // 发心跳包
            SocketBusEvent.IdleEvent idleEvent = new SocketBusEvent.IdleEvent();
            idleEvent.setCmd(REQUEST_HEART);
            EventBus.getDefault().post(idleEvent);

        }else if (IdleState.ALL_IDLE == e.getState()) {

            // 未进行读写
            LogUtils.e(TAG, "ALL_IDLE");
        }
    }
}
