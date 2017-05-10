package com.zccl.ruiqianqi.socket.remotesocket.handler.tcpclient;

import android.content.Context;

import com.zccl.ruiqianqi.socket.remotesocket.NetworkTask;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by ruiqianqi on 2017/1/14 0014.
 */

public class TCPClientInitializer extends ChannelInitializer<SocketChannel> {

    // 类标志
    private static String TAG = TCPClientInitializer.class.getSimpleName();
    /** 应用上下文 */
    private Context mContext;
    // 主要的TCP客户端处理
    private TCPClientHandler tcpClientHandler;

    /**
     * TCP客户端初始化事件处理，这里只处理注册事件，在注册事件中再添加其他事件的处理类
     * @param context
     */
    public TCPClientInitializer(Context context){
        mContext = context;
        tcpClientHandler = new TCPClientHandler(mContext);
    }

    /**
     * 注册连接，由【channelRegistered】调用
     * 其他的handler在 {@link io.netty.handler}
     * @param ch
     * @throws Exception
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        // 事件处理的管道
        ChannelPipeline pipeline = ch.pipeline();

        // 使用\n为分隔符
        //ByteBuf delimiter = Unpooled.copiedBuffer("\n".getBytes());
        //pipeline.addLast(new DelimiterBasedFrameDecoder(1024, delimiter));

        // 使用\n为分隔符，依次遍历ByteBuf中的可读字节，判断看其是否有”\n” 或则 “\r\n”，
        // 如果有就以此位置为结束位置。 从可读索引到结束位置的区间的字节就组成了一行。
        // 它是以换行符为结束标志的解码器，支持携带结束符和不带结束符两种解码方式，同时支持配置单行的最大长度，
        // 如果读到了最大长度之后仍然没有发现换行符，则抛出异常，同时忽略掉之前读到的异常码流
        //pipeline.addLast(new LineBasedFrameDecoder(1024));

        // 就是将之前接收到的对象转换成字符串
        //pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));

        // 对于Inbound，是从头head开始依次执行，而对于Outbound，则是从tail向前执行
        pipeline.addLast("business", tcpClientHandler);


        // 这是什么情况下触发的？，读、写、空闲，超时的时候
        // 其他的handler在{@link io.netty.handler}下面
        pipeline.addLast("timeout", new IdleStateHandler(0, NetworkTask.HEART_BIT_TIME, 0, TimeUnit.MILLISECONDS));
        // 心跳检测
        pipeline.addLast("timeoutTrigger", new TimeOutHandler());

        // 而对于Outbound，则是从tail向前执行
        pipeline.addLast(new OutDataHandler());
    }

    /**
     * 设置重连接口
     * @param reConnectCallback
     */
    public void setReConnectCallback(TCPClientHandler.IReConnectCallback reConnectCallback) {
        this.tcpClientHandler.setReConnectCallback(reConnectCallback);
    }

}
