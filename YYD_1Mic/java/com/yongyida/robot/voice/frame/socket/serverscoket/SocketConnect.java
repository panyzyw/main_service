package com.yongyida.robot.voice.frame.socket.serverscoket;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import android.content.Context;

import com.yongyida.robot.voice.utils.ThreadExecutorUtils;

/**
 * 和服务器端的socket连接类.
 * 
 * @author Administrator
 *
 */
public class SocketConnect {

	private ScheduledThreadPoolExecutor executor ;
	private InetSocketAddress addr ;
	private static SocketConnect connect;
	private ClientBootstrap bootstrap;
	private SocketConnect(final Context context, String host, int port) {
		this.addr = new InetSocketAddress(host, port);
		this.executor = ThreadExecutorUtils.getExceutor();
		bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));
		
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() {
				
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("decoder", new Decoder());
				pipeline.addLast("heart", new HeartBeat());
				pipeline.addLast("handler", new SocketHandler(context));
				
				return pipeline;
			}
		});
		
	}

	public void socketConnect(int time) {
		SocketThread thread = new SocketThread();
		executor.schedule( thread , time, TimeUnit.SECONDS);
		
	}

	private class SocketThread extends Thread{
		@Override
		public void run() {
			try {
				ChannelFuture future = bootstrap.connect(addr); //"120.24.242.163"8001
				future.awaitUninterruptibly(10000, TimeUnit.MILLISECONDS);
				
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	
	public void socketDestroyConnect(){
		if(SocketHandler.channel != null){
			SocketHandler.channel.disconnect();
		}
		connect = null;
	}
	
	public static SocketConnect getInstace(Context context, String host, int port){
		if(connect == null){
			synchronized(SocketConnect.class){
				if(connect == null){
					connect = new SocketConnect(context, host, port);
				}
			}
			
		}
		return connect;
	}
	
}
