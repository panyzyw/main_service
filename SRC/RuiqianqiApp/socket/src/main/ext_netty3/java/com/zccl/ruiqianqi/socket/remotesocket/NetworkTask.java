package com.zccl.ruiqianqi.socket.remotesocket;

import android.content.Context;

import com.zccl.ruiqianqi.data.socket.R;
import com.zccl.ruiqianqi.socket.remotesocket.handler.TCPClientDecoder;
import com.zccl.ruiqianqi.socket.remotesocket.handler.TCPClientHandler;
import com.zccl.ruiqianqi.socket.remotesocket.handler.TimeOutHandler;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.executor.rxutils.MyRxUtils;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.DynamicChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.CharsetUtil;
import org.jboss.netty.util.HashedWheelTimer;

import java.net.InetSocketAddress;
import java.nio.ByteOrder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Mina 这样的框架很好，如果再配上 protobuf 这样的多平台序列化工具，
 * 可以很好的实现自定义协议的通信。自己订协议的好处就是安全，而且能做应答机制
 * @author zccl
 *
 */
public class NetworkTask {

	// 类标志
	private static String TAG = NetworkTask.class.getSimpleName();

	// 返回的最大字节数 1M
	public static final int MAX_LENGTH = 1024 * 1024 /* * 10 */;
    // 缓冲区大小
	public static final int BUF_SIZE = 1024 * 10;
	// 处理粘包问题用的是对协议的精准控制，服务器下发的数据又没有结束符
	// 前后间隔超过5秒，就可以清空缓冲区了【ms】
	public static final long READ_TIME_OUT = 10000;
	// 连接断开后，重连间隔时间【ms】
	public static final long RE_CONNECT_TIME = 10000;
	/**
	 * 心跳方式
	 * 0：不需要自建线程发送
	 * 1：需要自建线程发送
	 */
	public static final int HEART_BIT_WAY = 0;
	// 心跳方式间隔时间【ms】
	public static final long HEART_BIT_TIME = 10000;

	// 全局上下文
	private Context mContext;

	// BOSS线程
	//private ExecutorService boss;
	// WORK线程
	private ExecutorService work;
	// 客户端初始化参数
	private ClientBootstrap bootstrap;
	// TCP客户端
	private TCPClientHandler tcpClientHandler;
	// TCP客户端数据处理通道
	private Channel tcpClientChannel;
	// 远程地址
	private String mRemoteIP;
	// 远程端口
	private int mRemotePort;
	// 连接成功与否的回调
	private IConnectCallback mConnectCallback;

	public NetworkTask(Context context) {
		this.mContext = context;

	}

	/**
	 * 初始化TCP客户端
	 *
	 * 编解码命名改变
	 * FrameDecoder －－－－－－> ByteToMessageDecoder
	 * OneToOneEncoder  －－－－> MessageToMessageEncoder
	 * OneToOneDecoder －－－－－> MessageToMessageDecoder
	 */
	private void initTCPClient() {
		LogUtils.e(TAG, "initTCPClient");

		/*
		if(null == boss){
			boss = Executors.newCachedThreadPool();
		}
		*/
		if(null == work){
			work = Executors.newCachedThreadPool();
		}

		if(null == tcpClientHandler) {
			// TCP客户端处理器
			tcpClientHandler = new TCPClientHandler(mContext);

			// 设置重连接口
			tcpClientHandler.setReConnectCallback(new TCPClientHandler.IReConnectCallback() {

				@Override
				public void OnConnected() {
				/*
				if(null != mConnectCallback){
					mConnectCallback.OnSuccess();
				}
				*/
				}

				@Override
				public void OnDisconnected() {
					if (null != mConnectCallback) {
						mConnectCallback.OnNetInActive();
					}

					// 【已连接上，断线重连】重连
					connectToServer(mRemoteIP, mRemotePort, mConnectCallback, RE_CONNECT_TIME);
				}
			});

		}

		if(null == bootstrap) {
			bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(work, work));
			bootstrap.setOption("tcpNoDelay", true);
			bootstrap.setOption("keepAlive", true);
			// 连接超时时间为10s【channelFuture.awaitUninterruptibly(10000, TimeUnit.MILLISECONDS);】
			bootstrap.setOption("connectTimeoutMillis", 10000);
			// 默认是32k
			bootstrap.setOption("writeBufferLowWaterMark", 64 * 1024);
			bootstrap.setOption("writeBufferHighWaterMark", 128 * 1024);

			bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
				@Override
				public ChannelPipeline getPipeline() throws Exception {
					ChannelPipeline pipeline = Channels.pipeline();
					// 添加解码器
					pipeline.addLast("decoder", new TCPClientDecoder(mContext));
					// 添加业务处理
					pipeline.addLast("business", tcpClientHandler);
					// 超时检测
					pipeline.addLast("timeout", new IdleStateHandler(new HashedWheelTimer(), 0, HEART_BIT_TIME, 0, TimeUnit.MILLISECONDS));
					// 心跳检测
					pipeline.addLast("timeoutTrigger", new TimeOutHandler());

					return pipeline;
				}
			});

			//bootstrap.shutdown();
		}
	}

	/**
	 * 开始连接服务器
	 *
	 * @param ip
	 * @param port
	 */
	private void startConnect(String ip, int port, IConnectCallback connectCallback) {
		LogUtils.e(TAG, "startConnect: " + ip + " : " + port);
		try {
			ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(ip, port));
			// 连接，超时10秒
			//channelFuture.awaitUninterruptibly(10000, TimeUnit.MILLISECONDS);
			channelFuture.awaitUninterruptibly();

			channelFuture.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					LogUtils.e(TAG, "isDone: " + future.isDone() +
							"\nisSuccess: " + future.isSuccess() +
							"\ncause: " + future.getCause() +
							"\nisCancelled: " + future.isCancelled());
					if (future.isSuccess()) {
						LogUtils.e(TAG, mContext.getString(R.string.connect_success));

						// 【连接成功的回调】
						if(null != mConnectCallback){
							mConnectCallback.OnSuccess();
						}
					}
					else {
						LogUtils.e(TAG, mContext.getString(R.string.connect_failure), future.getCause());

						// 【没有连接上服务器、连接失败的回调】
						if(null != mConnectCallback){
							mConnectCallback.OnFailure(future.getCause());
						}

						// 【没有连接上服务器，重连】
						connectToServer(mRemoteIP, mRemotePort, mConnectCallback, RE_CONNECT_TIME);
					}
				}
			});

			tcpClientChannel = channelFuture.getChannel();
			/*
			// Wait until the connection is closed.
			// closeFuture方法返回通道关闭的结果
			// 这：已经执行了关闭动作
			tcpClientChannel.close().await();
			tcpClientChannel = null;
			*/

		} catch (Exception e){
			e.printStackTrace();

		} finally {

		}

	}

	/**
	 * 客户端连接服务器的方法
	 * 这个由RX线程池执行
	 * @param ip					服务器地址
	 * @param port					服务器端口
	 * @param connectCallback		连接状态回调
	 * @param delay				连接前延时ms
	 */
	public void connectToServer(String ip, int port, IConnectCallback connectCallback, long delay){
		this.mRemoteIP = ip;
		this.mRemotePort = port;
		this.mConnectCallback = connectCallback;

		// 立即执行
		if(0 == delay){
			MyRxUtils.doAsyncRun(new Runnable() {
				@Override
				public void run() {
					closeTCPClient();
					initTCPClient();
					startConnect(mRemoteIP, mRemotePort, mConnectCallback);
				}
			});
		}
		// 延时执行
		else if(0 < delay){
			MyRxUtils.doAsyncRun(new Runnable() {
				@Override
				public void run() {
					closeTCPClient();
					initTCPClient();
					startConnect(mRemoteIP, mRemotePort, mConnectCallback);
				}
			}, delay);
		}
		// 切换服务器
		else {
			closeTCPClient();
		}
	}

	/**
	 * 发送数据
	 * channel有一个isWritable属性，可以来控制ChannelOutboundBuffer，不让其无限制膨胀。
	 *
	 * Netty将写操作封装成写事件，触发事件向下传播；
	 * 写事件被调度到ChannelPipeline中，由【业务线程】按照Handler Chain串行调用支持Downstream事件的Channel Handler;
	 * 执行到系统最后一个ChannelHandler，将编码后的消息Push到发送队列中，【业务线程返回】；
	 * Netty的【I/O线】程从发送消息队列中取出消息，调用SocketChannel的write方法进行消息发送。
	 *
	 * @param msg
	 */
	public void sendTcpData(String msg){
		if(null != tcpClientChannel &&
				tcpClientChannel.isConnected() &&
				tcpClientChannel.isWritable()){

			// 动态缓存，空间不够的时候可以动态增加
			ChannelBuffer buffer = new DynamicChannelBuffer(ByteOrder.BIG_ENDIAN, 1024);
			//buffer = ChannelBuffers.dynamicBuffer();
			buffer.writeBytes(msg.getBytes(CharsetUtil.UTF_8));

			tcpClientChannel.write(buffer).addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture channelFuture) throws Exception {
					if(channelFuture.isSuccess()){
						LogUtils.e(TAG, mContext.getString(R.string.send_data_success) + "--String");
					}else {
						LogUtils.e(TAG, mContext.getString(R.string.send_data_failure), channelFuture.getCause());
					}
				}
			});
		}
	}

	/**
	 * 发送数据
	 * channel有一个isWritable属性，可以来控制ChannelOutboundBuffer，不让其无限制膨胀。
	 *
	 * Netty将写操作封装成写事件，触发事件向下传播；
	 * 写事件被调度到ChannelPipeline中，由【业务线程】按照Handler Chain串行调用支持Downstream事件的Channel Handler;
	 * 执行到系统最后一个ChannelHandler，将编码后的消息Push到发送队列中，【业务线程返回】；
	 * Netty的【I/O线】程从发送消息队列中取出消息，调用SocketChannel的write方法进行消息发送。
	 *
	 * @param msg       要包装的消息源
	 * @param length    消息体的真实长度
	 */
	public void sendTcpData(byte[] msg, int length){
		if(null != tcpClientChannel &&
				tcpClientChannel.isConnected() &&
				tcpClientChannel.isWritable()){

			// 动态缓存，空间不够的时候可以动态增加
			ChannelBuffer buffer = new DynamicChannelBuffer(ByteOrder.BIG_ENDIAN, 1024);
			buffer.writeBytes(msg, 0, length);

			tcpClientChannel.write(buffer).addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture channelFuture) throws Exception {
					if(channelFuture.isSuccess()){
						LogUtils.e(TAG, mContext.getString(R.string.send_data_success) + "--byte[]");
					}else {
						LogUtils.e(TAG, mContext.getString(R.string.send_data_failure), channelFuture.getCause());
					}
				}
			});
		}
	}

	/**
	 * 发送数据
	 * channel有一个isWritable属性，可以来控制ChannelOutboundBuffer，不让其无限制膨胀。
	 *
	 * Netty将写操作封装成写事件，触发事件向下传播；
	 * 写事件被调度到ChannelPipeline中，由【业务线程】按照Handler Chain串行调用支持Downstream事件的Channel Handler;
	 * 执行到系统最后一个ChannelHandler，将编码后的消息Push到发送队列中，【业务线程返回】；
	 * Netty的【I/O线】程从发送消息队列中取出消息，调用SocketChannel的write方法进行消息发送。
	 *
	 * @param msg
	 */
	public void sendTcpData(ChannelBuffer msg){
		if(null != tcpClientChannel &&
				tcpClientChannel.isConnected() &&
				tcpClientChannel.isWritable()){

			tcpClientChannel.write(msg).addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture channelFuture) throws Exception {
					if(channelFuture.isSuccess()){
						LogUtils.e(TAG, mContext.getString(R.string.send_data_success) + "--ChannelBuffer");
					}else {
						LogUtils.e(TAG, mContext.getString(R.string.send_data_failure), channelFuture.getCause());
					}
				}
			});
		}
	}


	/**
	 * 关闭TCP客户端
	 */
	private void closeTCPClient(){
		LogUtils.e(TAG, "closeTCPClient");

		if(null != tcpClientChannel){
			tcpClientChannel.close();
			tcpClientChannel = null;
		}

		// Shut down all event loops to terminate all threads.
		// 不关闭，再次连接的时候就也不需要再初始化
		/*
		if(null != boss){
			boss.shutdown();
			boss = null;
		}
		*/
		if(null != work){
			work.shutdown();
			work = null;
		}
	}


	/**************************************【连接状态回调】****************************************/
	/**
	 * 连接服务器的回调接口
	 */
	public interface IConnectCallback{
		// 连接上了，又断开了
		void OnNetInActive();
		// 连接成功了
		void OnSuccess();
		// 没有连接上服务器
		void OnFailure(Throwable error);
	}

}
