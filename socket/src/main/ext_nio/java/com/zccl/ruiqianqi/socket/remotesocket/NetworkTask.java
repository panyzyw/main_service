package com.zccl.ruiqianqi.socket.remotesocket;

import android.content.Context;

import com.zccl.ruiqianqi.socket.eventbus.SocketBusEvent;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.tools.executor.rxutils.MyRxUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * Mina 这样的框架很好，如果再配上 protobuf 这样的多平台序列化工具，
 * 可以很好的实现自定义协议的通信。自己订协议的好处就是安全，而且能做应答机制
 *
 * java.net.SocketException: recvfrom failed: ECONNRESET
 * 并发连接的服务器的数目超过了其承载能力，其中一些已连接到服务器将被关闭;
 *
 * @author zccl
 *
 */
public class NetworkTask {

	// 类标志
	private static String TAG = NetworkTask.class.getSimpleName();

	// 返回的最大字节数 1M
	private static final int MAX_LENGTH = 1024 * 1024;
    // 缓冲区大小
    private static final int BUF_SIZE = 1024 * 10;
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
	public static final int HEART_BIT_WAY = 1;
	// 心跳方式间隔时间【ms】
	public static final long HEART_BIT_TIME = 10000;

	// 全局上下文
	private Context mContext;
	// 客户端通道
	private SocketChannel clientChannel;
	// 通道管理器
	private Selector selector;
	// 事件
	private SelectionKey selectionKey;

	// 上次读取响应时的时间戳
	private long lastCurrentTimeMillis = 0;
	// 是不是正在监听中
	private boolean isListening = false;
	// 远程地址
	private String mRemoteIP;
	// 远程端口
	private int mRemotePort;
	// 连接成功与否的回调
	private IConnectCallback mConnectCallback;

	/**
	每个Buffer都有以下的属性：
	capacity 这个Buffer最多能放多少数据。capacity一般在buffer被创建的时候指定。
	limit 	 在Buffer上进行的读/写操作都不能越过这个下标。当写数据到buffer中时，
			 limit一般和capacity相等，当读数据时，limit代表buffer中有效数据的长度。
	position 读/写操作的当前下标。当使用buffer的相对位置进行读/写操作时，读/写会从这个下标进行，
			 并在操作完成后，buffer会更新下标的值。
	mark	 一个临时存放的位置下标。调用mark()会将mark设为当前的position的值，
	 		 以后调用reset()会将position属性设置为mark的值。mark的值总是小于等于position的值，
		     如果将position的值设的比mark小，当前的mark值会被抛弃掉。
	这些属性总是满足以下条件：0 <= mark <= position <= limit <= capacity
	*/

	// 接受数据缓冲区
    private ByteBuffer recvBuffer = null;


	/**
	 * Selected KeySet:  选择键集；
	 * Cancelled KeySet：注销键集； 
	 * KeySet：			 键集；
	 * Interest Sets：   兴趣操作集
	 */
	public NetworkTask(Context context) {
		this.mContext = context;

		// 分配接收缓冲区内存
		recvBuffer = ByteBuffer.allocate(BUF_SIZE);
		recvBuffer.order(ByteOrder.BIG_ENDIAN);

	}

	/*************************************【私有方法】*********************************************/
	/**
	 * 初始化客户端
	 */
	private void initClient(){
		LogUtils.e(TAG, "initClient");
		try {
			// 获得一个通道
			clientChannel = SocketChannel.open();
			// 设置为非阻塞模式
			clientChannel.configureBlocking(false);
			// 获得一个通道管理器
			selector = Selector.open();

		} catch (ClosedChannelException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 连接服务器
	 * @param ip
	 * @param port
	 */
	private void startConnect(String ip, int port){
		LogUtils.e(TAG, "startConnect: " + ip + " : " + port);
		try {

			// 客户端连接服务器,其实方法执行并没有实现连接，需要在listen（）方法中调
			// 用channel.finishConnect();才能完成连接
			boolean isConnected = clientChannel.connect(new InetSocketAddress(ip, port));
			// 将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_CONNECT事件。
			selectionKey = clientChannel.register(selector, SelectionKey.OP_CONNECT, "connect");

			/*
			if (isConnected) {
				LogUtils.e(TAG, "isConnected");
			} else {
				LogUtils.e(TAG, "isNotConnected");
				// 如果连接还在尝试中,则注册connect事件的监听.connect成功以后会出发connect事件.
				key.interestOps(SelectionKey.OP_CONNECT);
			}
			*/
			selector.wakeup();

		} catch (Exception e) {
			connectFailure(e);
		}
	}

	/**
	 * 连接失败
	 * @param e
	 */
	private void connectFailure(Throwable e){
		LogUtils.e(TAG, "connectFailure", e);
		isListening = false;

		if(null != mConnectCallback){
			mConnectCallback.OnFailure(e);
		}

		// 连接一旦关闭就要销毁
		closeClient();
		connectToServer(mRemoteIP, mRemotePort, mConnectCallback, RE_CONNECT_TIME);
	}

	/**
	 * 连接断开
	 * @param e
	 */
	private void connectOff(Throwable e){
		LogUtils.e(TAG, "connectOff", e);
		isListening = false;

		if(null != mConnectCallback){
			mConnectCallback.OnNetInActive();
		}

		// 连接一旦关闭就要销毁
		closeClient();
		connectToServer(mRemoteIP, mRemotePort, mConnectCallback, RE_CONNECT_TIME);
	}

	/***********************************【数据解析】***********************************************/
	/**
     * 开始监听操作响应
	 */
	private void listen() {
		recvBuffer.clear();

		isListening = true;
		while(isListening){
			/*
			select() 阻塞调用线程，直到有某个Channel的某个感兴趣的Op准备好了
			select(long) 阻塞调用线程，但超时会自动返回
			selectNow() 则不阻塞，当注册的事件到达时，方法返回；否则,该方法会一直阻塞
			select()只返回本次执行select时从【未准备好】到【准备好状态】的channel数，
			如果不为0，将调用selectedKeys进行处理
			*/
			try {
				int channelNum = selector.select();
				// nothing to do
				if (0 == channelNum) {
			        continue; 
			    }
			} catch (Exception e) {
				connectFailure(e);
			}  

            // 获得注册在这个Selector的所有Key
            //selector.keys();

            // 获得selector中选中的项的迭代器
			Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
			
			// 开始遍历
			while (iterator.hasNext()) {
				SelectionKey key = iterator.next();
                
				// 删除已选的key,以防重复处理
				iterator.remove();
				
				// a connection was established with a remote server.
				// 两边应该都是这个字段。。。。。。
				// 连接上服务器了，还是有连接上来了
				// connect()和finishConnect()方法是互相同步的，并且只要其中一个操作正在进行，
				// 任何读或写的方法调用都会阻塞，即使是在非阻塞模式下
                if(key.isConnectable()) {
					// 取得附带的值
					String attach = key.attachment().toString();
					LogUtils.e(TAG, "isConnectable = " + attach);

                	SocketChannel channel = (SocketChannel) key.channel();
					try {

						// 如果正在连接，则完成连接
						if(channel.isConnectionPending()) {
							// 连接没有完成
							if (channel.finishConnect()) {
								// 设置成非阻塞
								channel.configureBlocking(false);
								// 在和服务端连接成功之后，为了可以接收到服务端的信息，需要给通道设置读的权限。
								channel.register(selector, SelectionKey.OP_READ, "read");
								// 连接成功的回调
								if (null != mConnectCallback) {
									mConnectCallback.OnSuccess();
								}
							} else {
								connectFailure(new Throwable("Not Connected Yet"));
							}
						}

					} catch (ClosedChannelException e) {
						connectFailure(e);

					} catch (Exception e) {
						connectFailure(e);
					}
				}
                
                // 有数据可以读了
                else if(key.isReadable()){
					// 取得附带的值
					String attach = key.attachment().toString();
					LogUtils.e(TAG, "isReadable = " + attach);

					// 为下一次读取作准备，这行去掉也可以
					key.interestOps(SelectionKey.OP_READ);

					SocketChannel client = (SocketChannel) key.channel();
					try {
						if (lastCurrentTimeMillis != 0) {
							long currentTimeMillis = System.currentTimeMillis();
							if (currentTimeMillis - lastCurrentTimeMillis > READ_TIME_OUT) {
								recvBuffer.clear();
							}
							lastCurrentTimeMillis = currentTimeMillis;
						}

						// 读取数据到全局缓存
						int count = client.read(recvBuffer);

						// read返回-1说明对方数据发送完毕，并且主动close socket，达到流末端了
						if(count < 0){
							connectOff(new Throwable("count < 0"));
						}

						// 没有数据且没有达到流的末端时返回0
						else if(0 == count){
							LogUtils.e(TAG, "0 == count");
						}

						// 读取到正常数据
						else if(count > 0) {

							/*
							The limit is set to the current position,
							then the position is set to zero
							and the mark is cleared.
							将缓冲区准备为数据传出状态,也就是调用这个之后就不要再写入数据了
							如果不调用这个，hasRemaining()就无法使用
							*/
							// 每次读数据都是从零开始读取，因为position是读写共用的
							recvBuffer.flip();

							// 用循环处理数据积累
							boolean isParsed = true;
							while(isParsed){
								isParsed = parseData();
								// 要继续读取解析
								if(isParsed){

								}
								// 不读取了，该写了
								else {
									/*
									System.arraycopy(hb, ix(position()), hb, ix(0), remaining());
									position(remaining());
									limit(capacity());
									discardMark();
									*/
									// 将未读的数据往前移，并重置为写状态
									recvBuffer.compact();
								}
							}
						}

					} catch (IOException e) {
						connectFailure(e);

					} catch (NotYetConnectedException e){
						connectFailure(e);

					} catch (Exception e){
						connectFailure(e);

					}
                }
                
                // 这个其实不用，通道本来就是可写的
                else if(key.isWritable()){
                	LogUtils.e(TAG, "isWritable");
                }
			}
		}
	}

	/**
	 * 进行数据解析
	 * @return
	 */
	private boolean parseData(){
		// 备份position，用来reset的
		recvBuffer.mark();

		//LogUtils.e(TAG, "ReadableBytes = " + recvBuffer.remaining());

		// 数据分包了，长度不够【1字节】
		if (recvBuffer.remaining() < 1) {
			// 没有读，不用reset()
			recvBuffer.reset();
			return false;
		}
		byte type = recvBuffer.get();
		//LogUtils.e(TAG, "type = " + type);

		// 心跳应答
		if(0 == type){
			SocketBusEvent.SocketCarrier socketCarrier = new SocketBusEvent.SocketCarrier();
			socketCarrier.setType(0);
			EventBus.getDefault().post(socketCarrier);
			return true;
		}

		//LogUtils.e(TAG, "Check Length Bytes");
		// 数据分包了，长度不够【4字节】
		if (recvBuffer.remaining() < 4) {
			recvBuffer.reset();
			return false;
		}

		int length = recvBuffer.getInt();
		//LogUtils.e(TAG, "Protocol Length = " + length);
		// 数据出错
		if (length < 0 || length > MAX_LENGTH) {
			recvBuffer.clear();
			return false;
		}

		//LogUtils.e(TAG, "ReadableBytes = " + recvBuffer.remaining());
		// 数据分包了，长度不够
		if(recvBuffer.remaining() < length){
			// 读索引重置到mark
			recvBuffer.reset();
			return false;
		}
		//LogUtils.e(TAG, "OKAY = " + type);

		switch (type) {
			// 交互协议应答
			case 1:
				byte[] header = new byte[length];
				recvBuffer.get(header);
				SocketBusEvent.SocketCarrier socketCarrier = new SocketBusEvent.SocketCarrier();
				socketCarrier.setType(1);
				socketCarrier.setHeader(header);
				EventBus.getDefault().post(socketCarrier);
				return true;

			case 2:
				// 数据分包了，长度不够【4字节】
				if (recvBuffer.remaining() < 4) {
					recvBuffer.reset();
					return false;
				}
				int dataHeaderLength = recvBuffer.getInt();
				// 数据出错
				if (dataHeaderLength < 0 || dataHeaderLength > MAX_LENGTH) {
					recvBuffer.clear();
					return false;
				}
				// 数据分包了，长度不够
				if(recvBuffer.remaining() < dataHeaderLength){
					recvBuffer.reset();
					return false;
				}
				byte[] dataHeader = new byte[dataHeaderLength];
				recvBuffer.get(dataHeader);

				// 数据分包了，长度不够【4字节】
				if(recvBuffer.remaining() < 4){
					return false;
				}
				int dataBodyLength = recvBuffer.getInt();
				// 数据出错
				if (dataBodyLength < 0 || dataBodyLength > MAX_LENGTH) {
					recvBuffer.clear();
					return false;
				}
				// 数据分包了，长度不够
				if (recvBuffer.remaining() < dataBodyLength) {
					recvBuffer.reset();
					return false;
				}
				byte[] dataBody = new byte[dataBodyLength];
				recvBuffer.get(dataBody);

				socketCarrier = new SocketBusEvent.SocketCarrier();
				socketCarrier.setType(2);
				socketCarrier.setHeader(dataHeader);
				socketCarrier.setBody(dataBody);
				EventBus.getDefault().post(socketCarrier);
				return true;

			default:
				break;
		}
		return false;
	}



	/**********************************【提供给外部的方法】****************************************/
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
					initClient();
					startConnect(mRemoteIP, mRemotePort);
					doListen();
				}
			});
		}
		// 延时执行
		else if(0 < delay){
			MyRxUtils.doAsyncRun(new Runnable() {
				@Override
				public void run() {
					initClient();
					startConnect(mRemoteIP, mRemotePort);
					doListen();
				}
			}, delay);
		}
		// 切换服务器
		else {
			closeClient();
		}
	}

	/**
	 * 开始异步监听
	 */
	private void doListen(){
		MyRxUtils.doAsyncRun(new Runnable() {
			@Override
			public void run() {
				listen();
			}
		});
	}

	/**
	 * 发送字符串
	 * @param msg
	 */
	public void sendTcpData(String msg){
		if(null != clientChannel && clientChannel.isConnected()){
			try {
				if(!StringUtils.isEmpty(msg)) {

					ByteBuffer sendBuffer;
					if(msg.length() > BUF_SIZE){
						sendBuffer = ByteBuffer.allocate(msg.length() + 1);
					}else {
						sendBuffer = ByteBuffer.allocate(1024);
					}
					sendBuffer.put(msg.getBytes(Charset.defaultCharset()));
					clientChannel.write(sendBuffer);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 发送字节数组
	 * @param msg       要包装的消息源
	 * @param length    消息体的真实长度
	 */
	public void sendTcpData(byte[] msg, int length){
		if(null != clientChannel && clientChannel.isConnected()){
			try {
				if(null == msg)
					return;
				clientChannel.write(ByteBuffer.wrap(msg, 0, length));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 发送ByteBuffer
	 * @param msg
	 */
	public void sendTcpData(ByteBuffer msg){
		if(null != clientChannel && clientChannel.isConnected()){
			try {
				if(null == msg)
					return;
				clientChannel.write(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 关闭通道
	 */
	private void closeClient() {
		LogUtils.e(TAG, "closeClient");

		if(null != clientChannel) {
			// 找到当前该通道上准备好的操作Op
			SelectionKey key = clientChannel.keyFor(selector);
			if (null != key) {
				key.cancel();
			}
			try {
				if(clientChannel.isOpen()) {
					clientChannel.close();
				}
				clientChannel = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
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

	/*
	 * 
    public final Buffer flip() {
        limit = position;
        position = 0;
        mark = UNSET_MARK;
        return this;
    }
    
    public final Buffer clear() {
        position = 0;
        mark = UNSET_MARK;
        limit = capacity;
        return this;
    }
    
    public final Buffer rewind() {
        position = 0;
        mark = UNSET_MARK;
        return this;
    }
    
    public final boolean hasRemaining() {
        return position < limit;
    }

    */

}
