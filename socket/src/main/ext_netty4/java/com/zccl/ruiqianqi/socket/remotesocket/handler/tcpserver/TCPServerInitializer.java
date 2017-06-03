package com.zccl.ruiqianqi.socket.remotesocket.handler.tcpserver;

import android.content.Context;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Created by ruiqianqi on 2017/1/14 0014.
 */

public class TCPServerInitializer extends ChannelInitializer<SocketChannel> {

    // 类标志
    private static String TAG = TCPServerInitializer.class.getSimpleName();
    // 应用上下文
    private Context mContext;
    // TCP服务端数据处理器
    private TCPServerHandler tcpServerHandler;

    /**
     * TCP客户端初始化事件处理，这里只处理注册事件，在注册事件中再添加其他事件的处理类
     * @param context
     */
    public TCPServerInitializer(Context context){
        mContext = context;
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

        // 使用\n为分隔符，依次遍历ByteBuf中的可读字节，判断看其是否有”\n” 或则 “\r\n”，
        // 如果有就以此位置为结束位置。 从可读索引到结束位置的区间的字节就组成了一行。
        // 它是以换行符为结束标志的解码器，支持携带结束符和不带结束符两种解码方式，同时支持配置单行的最大长度，
        // 如果读到了最大长度之后仍然没有发现换行符，则抛出异常，同时忽略掉之前读到的异常码流
        pipeline.addLast(new LineBasedFrameDecoder(1024));

        // 就是将之前接收到的对象转换成字符串
        pipeline.addLast(new StringDecoder());

        pipeline.addLast("business", new TCPServerHandler(mContext));

    }

}
