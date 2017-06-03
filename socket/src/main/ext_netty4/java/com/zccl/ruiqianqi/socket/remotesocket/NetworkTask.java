package com.zccl.ruiqianqi.socket.remotesocket;

import android.content.Context;

import com.zccl.ruiqianqi.data.socket.R;
import com.zccl.ruiqianqi.socket.remotesocket.handler.tcpclient.TCPClientHandler;
import com.zccl.ruiqianqi.socket.remotesocket.handler.tcpclient.TCPClientInitializer;
import com.zccl.ruiqianqi.socket.remotesocket.handler.tcpserver.TCPServerInitializer;
import com.zccl.ruiqianqi.socket.remotesocket.handler.udpclient.UDPClientDecoderHandler;
import com.zccl.ruiqianqi.socket.remotesocket.handler.udpclient.UDPClientSimpleHandler;
import com.zccl.ruiqianqi.socket.remotesocket.handler.udpserver.UDPServerDecoderHandler;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.tools.executor.rxutils.MyRxUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * Created by zc on 2015/12/28.
 * Bootstrap or ServerBootstrap：一个Netty应用通常由一个Bootstrap开始，它主要作用是配置整个Netty程序，
 * 串联起各个组件。
 * <p>
 * Handler or ChannelHandler：为了支持各种协议和处理数据的方式，便诞生了Handler组件。Handler主要用来处
 * 理各种事件，这里的事件很广泛，比如可以是连接、数据接收、异常、数据转换等。
 * <p>
 * ChannelInboundHandler：一个最常用的Handler。这个Handler的作用就是处理接收到数据时的事件，也就是说，
 * 我们的业务逻辑一般就是写在这个Handler里面的，ChannelInboundHandler就是用来处理我们的核心业务逻辑。
 * <p>
 * ChannelInitializer：当一个链接建立时，我们需要知道怎么来接收或者发送数据，当然，我们有各种各样的
 * Handler实现来处理它，那么ChannelInitializer便是用来配置这些Handler，它会提供一个ChannelPipeline，
 * 并把Handler加入到ChannelPipeline。
 * <p>
 * ChannelPipeline：一个Netty应用基于ChannelPipeline机制，这种机制需要依赖于EventLoop和EventLoopGroup，
 * 因为它们三个都和事件或者事件处理相关。
 * <p>
 * EventLoop：目的是为Channel处理IO操作，一个EventLoop可以为多个Channel服务。
 * <p>
 * EventLoopGroup：会包含多个EventLoop。
 * <p>
 * Channel：代表了一个Socket链接，或者其它和IO操作相关的组件，它和EventLoop一起用来参与IO处理。
 * <p>
 * Future or ChannelFuture：在Netty中所有的IO操作都是异步的，因此，你不能立刻得知消息是否被正确处理，
 * 但是我们可以过一会等它执行完成或者直接注册一个监听，具体的实现就是通过Future和ChannelFutures,他们可以
 * 注册一个监听，当操作执行成功或失败时监听会自动触发。总之，所有的操作都会返回一个ChannelFuture。
 * <p>
 * 在TCP/IP协议中，无论发送多少数据，总是要在数据前面加上协议头，同时，对方接收到数据，也需要发送ACK表示确认。
 * 为了尽可能的利用网络带宽，TCP总是希望尽可能的发送足够大的数据。这里就涉及到一个名为Nagle的算法， 该算法的
 * 目的就是为了尽可能发送大块数据，避免网络中充斥着许多小数据块。
 * TCP_NODELAY就是用于启用或关闭Nagle算法。如果要求高实时性，有数据发送时就马上发送，就将该选项设置为true
 * 关闭Nagle算法；如果要减少发送次数减少网络交互，就设置为false等累积一定大小后再发送。默认为false。
 * <p>
 * 对于初学socket编程的人来说，可能会忘记这里还有个“陷阱”。Nagle算法适用于小包、高延迟的场合，
 * 而对于要求交互速度的b/s或c/s就不合适了。socket在创建的时候，默认都是使用Nagle算法的，这会导致交互速度严重下降，
 * 所以需要setsockopt函数来设置TCP_NODELAY为1.
 * 不过取消了Nagle算法，就会导致TCP碎片增多，效率可能会降低。所以，这也是要有所取舍的。
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

    // 应用上下文
    private Context mContext;

    // TCP客户端事件处理线程【netty4.x就是线程池】
    // 单个线程的线程池，也就是俗称的IO线程
    private EventLoopGroup tcpClientWorkerGroup = null;
    // 连接配置
    private Bootstrap tcpClientBootstrap;
    // TCP客户端初始化配置
    private TCPClientInitializer tcpClientInitializer;
    // TCP客户端数据处理通道
    private Channel tcpClientChannel;
    // 远程IP
    private String mRemoteIP;
    // 远程PORT
    private int mRemotePort;
    // 连接成功与否的回调
    private IConnectCallback mConnectCallback;


    /**
     * 事件循环: 是一个处理I/O操作的多线程事件环【netty4.x就是线程池】
     */
    private EventLoopGroup tcpServerBossGroup = null;
    /**
     * 我们知道netty采用了reactor的设计模式，其中mainReactor主要负责连接的建立，连接建立后交由subReactor处理，
     * 而subReactor则主要负责处理读写等具体的事件。这里mainReactor的实际执行者是bossGroup，
     * 而subReactor的实际执行者则是workerGroup
     */
    private EventLoopGroup tcpServerWorkerGroup = null;
    // TCP服务端数据处理通道
    private Channel tcpServerChannel;

    // UDP客户端事件处理线程
    private EventLoopGroup udpClientWorkerGroup = null;
    // UDP客户端数据处理通道
    private Channel udpClientChannel;

    // UDP服务端事件处理线程
    private EventLoopGroup udpServerWorkerGroup = null;
    // UDP服务端数据处理通道
    private Channel udpServerChannel;

    /**
     * 构造方法
     * @param context
     */
    public NetworkTask(Context context) {
        this.mContext = context;
    }

    /*********************************************************************************************/
    /**
     * 初始化TCP客户端
     * NioSocketChannel, 代表异步的客户端 TCP Socket 连接.
     * NioServerSocketChannel, 异步的服务器端 TCP Socket 连接.
     * NioDatagramChannel, 异步的 UDP 连接
     * NioSctpChannel, 异步的客户端 Sctp 连接.
     * NioSctpServerChannel, 异步的 Sctp 服务器端连接.
     * OioSocketChannel, 同步的客户端 TCP Socket 连接.
     * OioServerSocketChannel, 同步的服务器端 TCP Socket 连接.
     * OioDatagramChannel, 同步的 UDP 连接
     * OioSctpChannel, 同步的 Sctp 服务器端连接.
     * OioSctpServerChannel, 同步的客户端 TCP Socket 连接.
     * <p>
     * 每个channel内部都会持有一个ChannelPipeline对象pipeline。pipeline默认实现DefaultChannelPipeline，
     * 其内部维护了一个DefaultChannelHandlerContext链表。
     * <p>
     * 连接TCP服务器
     * ChannelPipeline pipeline = channelFuture.channel().pipeline();
     * pipeline.addLast("register", new RegisterHandler());
     * 。。。。。。
     * <p>
     * 可以添加很多处理Handler，这些handler是怎么区分的呢，就是怎么被调用的呢
     * 下面听我一一道来：
     * 接口 {@link io.netty.channel.ChannelHandler} 有三个事件：
     * {@link io.netty.channel.ChannelHandler#handlerAdded}         事件已加入
     * {@link io.netty.channel.ChannelHandler#handlerRemoved}       事件已移除
     * {@link io.netty.channel.ChannelHandler#exceptionCaught}
     * <p>
     * 子接口 {@link io.netty.channel.ChannelInboundHandler} 有八个事件
     * {@link io.netty.channel.ChannelInboundHandler#channelRegistered}    注册连接
     * {@link io.netty.channel.ChannelInboundHandler#channelUnregistered}  取消注册
     * {@link io.netty.channel.ChannelInboundHandler#channelActive}        连接激活
     * {@link io.netty.channel.ChannelInboundHandler#channelInactive}      检测到连接断开
     * {@link io.netty.channel.ChannelInboundHandler#channelRead}          读取数据
     * {@link io.netty.channel.ChannelInboundHandler#channelReadComplete}  读取完成
     * {@link io.netty.channel.ChannelInboundHandler#userEventTriggered}   用户事件触发
     * {@link io.netty.channel.ChannelInboundHandler#channelWritabilityChanged}  可写状态改变
     * 子类 {@link io.netty.channel.ChannelInboundHandlerAdapter} 默认实现了以上八个事件
     * <p>
     * 子接口 {@link io.netty.channel.ChannelOutboundHandler} 有八个事件
     * {@link io.netty.channel.ChannelOutboundHandler#bind}             绑定端口
     * {@link io.netty.channel.ChannelOutboundHandler#connect}          发起连接
     * {@link io.netty.channel.ChannelOutboundHandler#disconnect}       与对方的连接断开
     * {@link io.netty.channel.ChannelOutboundHandler#close}            关闭自己的连接
     * {@link io.netty.channel.ChannelOutboundHandler#deregister}       触发取消注册
     * {@link io.netty.channel.ChannelOutboundHandler#read}             触发读操作
     * {@link io.netty.channel.ChannelOutboundHandler#write}            写数据
     * {@link io.netty.channel.ChannelOutboundHandler#flush}            将写的数据发送
     * 子类 {@link io.netty.channel.ChannelOutboundHandlerAdapter} 默认实现了以上八个事件
     * <p>
     * 然后其他的类，都要么实现，要么继承了以上子类与子接口，所有的事件都这么来的
     * {@link io.netty.channel.SimpleChannelInboundHandler}
     * {@link io.netty.channel.ChannelDuplexHandler}
     * {@link io.netty.channel.ChannelInitializer}
     *
     * ChannelInBoundHandler 负责数据进入并在ChannelPipeline中按照从上至下的顺序查找调用相应的InBoundHandler。
     * ChannelOutBoundHandler负责数据出去并在ChannelPipeline中按照从下至上的顺序查找调用相应的OutBoundHandler。
     *
     * 每个TCP socket在内核中都有一个发送缓冲区和一个接收缓冲区，TCP的全双工的工作模式以及TCP的滑动窗口
     * 便是依赖于这两个独立的buffer以及此buffer的填充状态。接收缓冲区把数据缓存入内核，应用进程一直没有调用read进行读取的话，
     * 此数据会一直缓存在相应socket的接收缓冲区内。再啰嗦一点，不管进程是否读取socket，对端发来的数据都会经由内核接收并且缓存到
     * socket的内核接收缓冲区之中。
     * read所做的工作，就是把内核缓冲区中的数据拷贝到应用层用户的buffer里面，仅此而已。
     * 进程调用send发送数据的时候，最简单情况（也是一般情况），将数据拷贝进入socket的内核发送缓冲区之中，然后send便会在上层返回。
     * 换句话说，send返回之时，数据不一定会发送到对端去（和write写文件有点类似），send仅仅是把应用层buffer的数据拷贝进socket的内核发送buffer中。
     *
     * 每个UDP socket都有一个接收缓冲区，没有发送缓冲区，从概念上来说就是只要有数据就发，不管对方是否可以正确接收，所以不缓冲，不需要发送缓冲区。
     * 接收缓冲区被TCP和UDP用来缓存网络上来的数据，一直保存到应用进程读走为止。
     * 对于TCP，如果应用进程一直没有读取，buffer满了之后，发生的动作是：通知对端TCP协议中的窗口关闭。这个便是滑动窗口的实现。
     * 保证TCP套接口接收缓冲区不会溢出，从而保证了TCP是可靠传输。因为对方不允许发出超过所通告窗口大小的数据。
     * 这就是TCP的流量控制，如果对方无视窗口大小而发出了超过窗口大小的数据，则接收方TCP将丢弃它。
     *
     * UDP：当套接口接收缓冲区满时，新来的数据报无法进入接收缓冲区，此数据报就被丢弃。
     * UDP是没有流量控制的；快的发送者可以很容易地就淹没慢的接收者，导致接收方的UDP丢弃数据报。
     */
    private void initTCPClient() {
        LogUtils.e(TAG, "initTCPClient");

        // 工作消息循环采用的线程
        if(null == tcpClientWorkerGroup) {
            tcpClientWorkerGroup = new NioEventLoopGroup();
        }

        if(null == tcpClientInitializer) {
            // 事件处理器
            tcpClientInitializer = new TCPClientInitializer(mContext);
            // 【已经连接上了、断线重连】
            tcpClientInitializer.setReConnectCallback(new TCPClientHandler.IReConnectCallback() {
                @Override
                public void OnConnected() {

                }

                @Override
                public void OnDisconnected() {
                    // 连接状态回调
                    if (null != mConnectCallback) {
                        mConnectCallback.OnNetInActive();
                    }

                    // 【已连接上，断线重连】重连
                    connectToServer(mRemoteIP, mRemotePort, mConnectCallback, RE_CONNECT_TIME);
                }

            });
        }

        if(null == tcpClientBootstrap) {
            // 客户端启动参数设置
            tcpClientBootstrap = new Bootstrap();
            // 指定channel类型
            tcpClientBootstrap.channel(NioSocketChannel.class)
                    // 指定Handler
                    .handler(tcpClientInitializer)
                    // 指定EventLoopGroup
                    .group(tcpClientWorkerGroup)
                    // 指定内存分配的方式，IO线程申请的ByteBuf必须由IO线程进行释放
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)

                    // 关闭Nagle算法，要求高实时性
                    .option(ChannelOption.TCP_NODELAY, true)
                    // 这个Socket选项可以影响close方法的行为。在默认情况下，当调用close方法后，将立即返回；如果这时仍然有未被送出的数据包，那么这 些数据包将被丢弃。如果将linger参数设为一个正整数n时（n的值最大是65，535），在调用close方法后，将最多被阻塞n秒。在这n秒内，系 统将尽量将未送出的数据包发送出去；如果超过了n秒，如果还有未发送的数据包，这些数据包将全部被丢弃；而close方法会立即返回。如果将linger 设为0，和关闭SO_LINGER选项的作用是一样的。
                    .option(ChannelOption.SO_LINGER, 0)
                    // 连接超时设置【channelFuture.awaitUninterruptibly(10000, TimeUnit.MILLISECONDS);】
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    // 可以通过这个选项来设置读取数据超时。当输入流的read方法被阻塞时，如果设置 timeout（timeout的单位是毫秒），那么系统在等待了timeout毫秒后会抛出一个InterruptedIOException例外。在 抛出例外后，输入流并未关闭，你可以继续通过read方法读取数据。
                    // 如果将timeout设为0，就意味着read将会无限等待下去，直到服务端程序关闭这个Socket.这也是timeout的默认值。
                    .option(ChannelOption.SO_TIMEOUT, 0)
                    // 如果将这个Socket选项打开，客户端Socket每隔段的时间（大约两个小时）就会利用空闲的连接向服务器发送一个数据包。这个数据包并没有其 它的作用，只是为了检测一下服务器是否仍处于活动状态。如果服务器未响应这个数据包，在大约11分钟后，客户端Socket再发送一个数据包，如果在12 分钟内，服务器还没响应，那么客户端Socket将关闭。如果将Socket选项关闭，客户端Socket在服务器无效的情况下可能会长时间不会关闭。
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    // 设置发送缓冲大小
                    .option(ChannelOption.SO_SNDBUF, 32 * 1024)
                    // 这是接收缓冲大小
                    .option(ChannelOption.SO_RCVBUF, 32 * 1024)
                    // 其实ChannelOutboundBuffer虽然无界，但是可以给它配置一个高水位线和低水位线，
                    // 当buffer的大小超过高水位线的时候对应channel的isWritable就会变成false，停止写入数据，
                    // 等到buffer中的数据由于被消费而低于低水位线时：也就是buffer的大小低于低水位线的时候，isWritable就会变成true。
                    // 所以应用应该判断isWritable，如果是false就不要再写数据了。高水位线和低水位线是字节数，
                    // 默认高水位是64K，低水位是32K，我们可以根据我们的应用需要支持多少连接数和系统资源进行合理规划。
                    .option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(32 * 1024, 64 * 1024));
        }
    }

    /**
     * 开始连接服务器
     *
     * @param ip
     * @param port
     */
    private void startConnect(String ip, int port) {
        LogUtils.e(TAG, "startConnect");

        try {
            // 开始连接动作
            ChannelFuture channelFuture = tcpClientBootstrap.connect(new InetSocketAddress(ip, port));

            //ChannelPipeline pipeline = channelFuture.channel().pipeline();
            // 对于Inbound，是从头head开始依次执行，而对于Outbound，则是从tail向前执行
            //pipeline.addLast("business", new TCPClientHandler(mContext));
            // 这是什么情况下触发的？，读、写、空闲，超时
            //pipeline.addLast("timeout", new TimeOutHandler(5, 5, 5, TimeUnit.SECONDS));

            // 同步等待，直到成功，响应中断
            //channelFuture.sync();
            // 一直等待，直到成功，忽略中断
            channelFuture.awaitUninterruptibly();
            // 等待在限定时间内完成，忽略中断，完成了返回true, 否则返回false
            //channelFuture.awaitUninterruptibly(10000, TimeUnit.MILLISECONDS);

            // 异步等待结果通知
            channelFuture.addListener(new GenericFutureListener<ChannelFuture>() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    LogUtils.e(TAG, "isDone: " + future.isDone() +
                            "\nisSuccess: " + future.isSuccess() +
                            "\ncause: " + future.cause() +
                            "\nisCancelled: " + future.isCancelled());

                    if (future.isSuccess()) {
                        LogUtils.e(TAG, mContext.getString(R.string.connect_success));

                        // 【连接成功的回调】
                        if(null != mConnectCallback){
                            mConnectCallback.OnSuccess();
                        }
                    }
                    else {
                        LogUtils.e(TAG, mContext.getString(R.string.connect_failure), future.cause());

                        // 【没有连接上服务器、连接失败的回调】
                        if(null != mConnectCallback){
                            mConnectCallback.OnFailure(future.cause());
                        }

                        // 【没有连接上服务器，重连】
                        connectToServer(mRemoteIP, mRemotePort, mConnectCallback, RE_CONNECT_TIME);

                        // 5秒后重新连接【直接在IO线程上操作】
                        /*
                        future.channel().eventLoop().schedule(new Runnable() {
                            @Override
                            public void run() {
                                connectToServer(mRemoteIP, mRemotePort, mConnectCallback);
                            }
                        }, 5, TimeUnit.SECONDS);
                        */
                    }
                }
            });


            tcpClientChannel = channelFuture.channel();
            // Wait until the connection is closed.
            // closeFuture方法返回通道关闭的结果
            // 这：还没有执行关闭动作
            tcpClientChannel.closeFuture().await();
            tcpClientChannel = null;

        } catch (InterruptedException e) {
            e.printStackTrace();

        } finally {
            // Shut down all event loops to terminate all threads.
            // 不关闭，再次连接的时候就也不需要再初始化
            tcpClientWorkerGroup.shutdownGracefully();
            tcpClientWorkerGroup = null;
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
                    startConnect(mRemoteIP, mRemotePort);
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
                    startConnect(mRemoteIP, mRemotePort);
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
     * write方法实际上并不是真的将消息写出去, 而是将消息和此次操作的promise放入到了一个队列中
     * channel有一个isWritable属性，可以来控制ChannelOutboundBuffer，不让其无限制膨胀。
     */
    public void sendTcpData(String msg){
        if(null != tcpClientChannel && tcpClientChannel.isActive() && tcpClientChannel.isWritable()){
            // 好像只能发送这个对象
            ByteBuf sendData = Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);
            tcpClientChannel.writeAndFlush(sendData).addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if(future.isSuccess()){
                        LogUtils.e(TAG, mContext.getString(R.string.send_data_success) + "--String");
                    }else {
                        LogUtils.e(TAG, mContext.getString(R.string.send_data_failure), future.cause());
                    }
                }
            });
            tcpClientChannel.flush();
        }
    }

    /**
     * 发送数据
     * write方法实际上并不是真的将消息写出去, 而是将消息和此次操作的promise放入到了一个队列中
     * channel有一个isWritable属性，可以来控制ChannelOutboundBuffer，不让其无限制膨胀。
     *
     * @param msg       要包装的消息源
     * @param length    消息体的真实长度
     */
    public void sendTcpData(byte[] msg, int length){
        if(null != tcpClientChannel && tcpClientChannel.isActive() && tcpClientChannel.isWritable()){

            ByteBuf sendData = Unpooled.wrappedBuffer(msg, 0, length);
            tcpClientChannel.writeAndFlush(sendData).addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if(future.isSuccess()){
                        LogUtils.e(TAG, mContext.getString(R.string.send_data_success) + "--byte[]");
                    }else {
                        LogUtils.e(TAG, mContext.getString(R.string.send_data_failure), future.cause());
                    }
                }
            });
            tcpClientChannel.flush();
        }
    }

    /**
     * 发送数据
     * write方法实际上并不是真的将消息写出去, 而是将消息和此次操作的promise放入到了一个队列中
     * channel有一个isWritable属性，可以来控制ChannelOutboundBuffer，不让其无限制膨胀。
     *
     * @param msg 好像只能发送这个对象
     */
    public void sendTcpData(ByteBuf msg){
        if(null != tcpClientChannel && tcpClientChannel.isActive() && tcpClientChannel.isWritable()){
            tcpClientChannel.writeAndFlush(msg).addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if(future.isSuccess()){
                        LogUtils.e(TAG, mContext.getString(R.string.send_data_success) + "--ByteBuf");
                    }else {
                        LogUtils.e(TAG, mContext.getString(R.string.send_data_failure), future.cause());
                    }
                }
            });
            tcpClientChannel.flush();
        }
    }

    /**
     * 关闭TCP客户端
     */
    private void closeTCPClient(){
        LogUtils.e(TAG, "closeTCPClient");

        if(null != tcpClientChannel){
            tcpClientChannel.close();
        }
    }


    /**********************************************************************************************/
    /**
     * 初始化TCP服务端
     * SO_BACKLOG：用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放已完成
     * 三次握手的请求的队列的最大长度。如果未设置或所设置的值小于1，Java将使用默认值50。
     */
    public void initTCPServer(int port) {

        if (null == tcpServerBossGroup && null == tcpServerWorkerGroup) {

            if(null != tcpServerChannel){
                tcpServerChannel.close();
            }

            try {
                // 主消息循环采用单线程
                tcpServerBossGroup = new NioEventLoopGroup(1);
                // 工作消息循环采用默认线程
                tcpServerWorkerGroup = new NioEventLoopGroup();

                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.group(tcpServerBossGroup, tcpServerWorkerGroup).
                        channel(NioServerSocketChannel.class).
                        option(ChannelOption.SO_BACKLOG, 100).
                        option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).

                        // 关闭Nagle算法，要求高实时性
                        childOption(ChannelOption.TCP_NODELAY, true).
                        // 在ServerChannel的处理链中加入初始化处理
                        // 只有在连接来了之后，才能进入处理链
                        childHandler(new TCPServerInitializer(mContext));

                // 绑定端口、同步等待
                // start最终目的是要执行bind，并且等待bind成功，所以一般我们会在ChannelFuture上加上sync()方法进行同步等待。

                // boolean isDone()：操作是否完成，completed 还是 uncompleted
                // boolean isCancelled()：如果Future已经完成，则判断操作是否被取消。
                // boolean isSuccess()：同上。
                // Throwable cause()：如果执行失败，此处可以获取导致失败的exception信息。
                // ChannelFuture await()：等待，直到异步操作执行完毕，内部基于wait实现。
                // ChannelFuture sync()：等待，直到异步操作执行完毕，核心思想同await。我们得到Future实例后，
                //      可以使用sync()方法来阻塞当前线程，直到异步操作执行完毕。和await的区别为，如果异步操作失败，
                //      那么将会重新抛出异常（将上述cause()方法中的异常抛出）。await和sync一样，当异步操作执行完毕后，
                //      通过notifyAll()唤醒。
                // ChannelFuture addListener(GenericFutureListener listener)：向Future添加一个listener，当异步操作执行完毕后
                //      （无论成败），会依次调用listener的operationCompleted方法。
                ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

                // 等待服务监听端口关闭
                tcpServerChannel = channelFuture.channel();

                // 添加事件成功与否的回调接口
                channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                        if (future.isSuccess()) {
                            LogUtils.e(TAG, mContext.getString(R.string.bind_success));

                        } else {
                            LogUtils.e(TAG, mContext.getString(R.string.bind_failure), future.cause());

                            // 失败了就直接关闭
                            closeTCPServer();
                        }
                    }
                });

                tcpServerChannel.closeFuture().await();
                tcpServerChannel = null;

                // Wait until the server socket is closed.
                //channelFuture.channel().closeFuture().sync();

            } catch (InterruptedException e) {
                e.printStackTrace();

            } finally {
                // Shut down all event loops to terminate all threads.
                // 退出，释放线程等相关资源
                tcpServerBossGroup.shutdownGracefully();
                tcpServerBossGroup = null;

                tcpServerWorkerGroup.shutdownGracefully();
                tcpServerWorkerGroup = null;
            }
        }
    }

    /**
     * 关闭TCP服务端
     */
    public void closeTCPServer(){
        if(null != tcpServerChannel){
            tcpServerChannel.close();
        }
    }


    /**********************************************************************************************/
    /**
     * 初始化UDP客户端
     */
    public void initUDPClient() {
        try {
            if (null == udpClientWorkerGroup) {
                Bootstrap bootstrap = new Bootstrap();
                udpClientWorkerGroup = new NioEventLoopGroup();
                bootstrap.group(udpClientWorkerGroup)
                        .channel(NioDatagramChannel.class)
                        .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                        .option(ChannelOption.SO_BROADCAST, true)
                        .handler(new UDPClientDecoderHandler());
                udpClientChannel = bootstrap.bind(0).sync().channel();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

        }
    }

    /**
     * 通过【NETTY】发送UDP数据
     *
     * @param msg
     * @param ip
     * @param port
     */
    public void sendUdpDataByNetty(String msg, String ip, int port) {
        try {
            if (StringUtils.isEmpty(msg))
                return;
            // 写数据的动作
            ChannelFuture channelFuture = udpClientChannel.writeAndFlush(
                    new DatagramPacket(Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8),
                            new InetSocketAddress(ip, port)));
            // 等待这个动作完成
            channelFuture.sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭UDP客户端
     */
    public void closeUDPClient() {

        // 执行关闭动作
        udpClientChannel.close();

        // QuoteOfTheMomentClientHandler will close the DatagramChannel when a response is received.
        // If the channel is not closed within 5 seconds, print an error message and quit.
        try {
            if (!udpClientChannel.closeFuture().await(3000)) {
                LogUtils.e(TAG, mContext.getString(R.string.udp_close_timeout));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        udpClientChannel = null;
        if (null != udpClientWorkerGroup) {
            udpClientWorkerGroup.shutdownGracefully();
            udpClientWorkerGroup = null;
        }
    }


    /**********************************************************************************************/
    /**
     * 初始化UDP服务端
     */
    public void initUDPServer(int port) {
        // 工作消息循环采用默认线程
        if (null == udpServerWorkerGroup) {
            try {
                udpServerWorkerGroup = new NioEventLoopGroup();

                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(udpServerWorkerGroup)
                        .channel(NioDatagramChannel.class)
                        .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                        .option(ChannelOption.SO_BROADCAST, true)
                        .handler(new UDPServerDecoderHandler());

                // Start the server.
                ChannelFuture channelFuture = bootstrap.bind(port).sync();

                // 添加处理handler
                ChannelPipeline pipeline = channelFuture.channel().pipeline();
                pipeline.addLast(new UDPClientSimpleHandler());

                // 就这么一直等着，closeFuture() 这个不是动作，是获取一个ChannelFuture
                udpServerChannel = channelFuture.channel();
                udpServerChannel.closeFuture().await();
                udpServerChannel = null;

            } catch (InterruptedException e) {
                e.printStackTrace();

            } finally {
                // Shut down all event loops to terminate all threads.
                udpServerWorkerGroup.shutdownGracefully();
                udpServerWorkerGroup = null;
            }
        }
    }

    /**
     * 关闭UDP服务端
     */
    public void closeUDPServer(){
        if(null != udpServerChannel){
            udpServerChannel.close();
        }
    }

    /*****************************************【NIO】**********************************************/
    /**
     * 通过【NIO】发送UDP数据
     *
     * @param msg
     * @param ip   对方的IP    192.168.11.135
     * @param port 对方的端口  3356
     */
    public void sendUdpDataByNio(String msg, String ip, int port) {
        if (StringUtils.isEmpty(msg))
            return;
        try {
            DatagramSocket datagramSocket = new DatagramSocket();
            // 由于数据报的数据是以字符数组传的形式存储的，所以传转数据
            byte[] buf = msg.getBytes();

            InetAddress inetAddress = InetAddress.getByName(ip);

            // 创建发送类型的数据报：
            java.net.DatagramPacket sendPacket = new java.net.DatagramPacket(buf, buf.length, inetAddress, port);

            datagramSocket.send(sendPacket);
            datagramSocket.close();

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
