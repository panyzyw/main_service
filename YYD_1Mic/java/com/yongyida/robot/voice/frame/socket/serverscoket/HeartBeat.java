package com.yongyida.robot.voice.frame.socket.serverscoket;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;

/**
 * 心跳类.
 * @author Administrator
 *
 */
public class HeartBeat extends IdleStateHandler {

	protected static org.jboss.netty.util.Timer timer = new HashedWheelTimer();

	protected static int readerIdleTimeSeconds = 30;

	protected static int writerIdleTimeSeconds = 0;

	protected static int allIdleTimeSeconds = 0;

	//protected Logger logger = Logger.getLogger("socket");

	public HeartBeat() {
		super(timer, readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
	}

	@Override
	protected void channelIdle(ChannelHandlerContext ctx, IdleState state, long lastActivityTimeMillis)
			throws Exception {
		super.channelIdle(ctx, state, lastActivityTimeMillis);

		if (state == IdleState.READER_IDLE) {
			if (ctx.getChannel().isConnected()) {
				ctx.getChannel().close();
				//logger.info("IdleState.READER_IDLE " + ctx.getChannel().getRemoteAddress());
			}
		}

	}

}
