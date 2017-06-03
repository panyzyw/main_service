package com.zccl.ruiqianqi.socket.remotesocket.handler;

import android.content.Context;

import com.zccl.ruiqianqi.socket.eventbus.SocketBusEvent;
import com.zccl.ruiqianqi.tools.LogUtils;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.DynamicChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static com.zccl.ruiqianqi.socket.eventbus.SocketBusEvent.SocketCarrier.RESPONSE_DATA;
import static com.zccl.ruiqianqi.socket.eventbus.SocketBusEvent.SocketCarrier.RESPONSE_HEART;
import static com.zccl.ruiqianqi.socket.eventbus.SocketBusEvent.SocketCarrier.RESPONSE_MSG;
import static com.zccl.ruiqianqi.socket.remotesocket.NetworkTask.BUF_SIZE;
import static com.zccl.ruiqianqi.socket.remotesocket.NetworkTask.MAX_LENGTH;
import static com.zccl.ruiqianqi.socket.remotesocket.NetworkTask.READ_TIME_OUT;

/**
 * Created by ruiqianqi on 2017/4/10 0010.
 *
 * 解码器的任务：就是将ChannelBuffer解码成指定对象，以便于使用
 *
 * 接收：
 * 【I/O线程（Work线程）】将消息从TCP缓冲区读取到SocketChannel的接收缓冲区中；
 * 由【I/O线程】负责生成相应的事件，触发事件向上执行，调度到ChannelPipeline中；
 * 【I/O线程】调度执行ChannelPipeline中Handler链的对应方法，直到业务实现的Last Handler;
 * Last Handler将消息封装成Runnable，放入到【业务线程池】中执行，【I/O线程返回】，继续读/写等I/O操作；
 * 业务线程池从任务队列中弹出消息，并发执行业务逻辑。
 *
 * 发送：
 * Netty将写操作封装成写事件，触发事件向下传播；
 * 写事件被调度到ChannelPipeline中，由【业务线程】按照Handler Chain串行调用支持Downstream事件的Channel Handler;
 * 执行到系统最后一个ChannelHandler，将编码后的消息Push到发送队列中，【业务线程返回】；
 * Netty的【I/O线】程从发送消息队列中取出消息，调用SocketChannel的write方法进行消息发送。
 */

public class TCPClientDecoder extends FrameDecoder {

    // 类标志
    private static String TAG = TCPClientDecoder.class.getSimpleName();

    /** 应用上下文 */
    private Context mContext;

    // 上次读取响应时的时间戳
    protected long lastCurrentTimeMillis = 0;

    // 用来缓存服务器响应的BUF
    protected ChannelBuffer mGlobalBuf = null;

    public TCPClientDecoder(Context context){
        this.mContext = context;
        this.mGlobalBuf = new DynamicChannelBuffer(ByteOrder.BIG_ENDIAN, BUF_SIZE);
    }

    @Override
    protected Object decode(ChannelHandlerContext channelHandlerContext, Channel channel, ChannelBuffer msg) throws Exception {
        LogUtils.e(TAG, "msg = " + msg.readableBytes());
        if (!channel.isReadable()) {
            channel.close();
            return null;
        }

        if (lastCurrentTimeMillis != 0) {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - lastCurrentTimeMillis > READ_TIME_OUT) {
                mGlobalBuf.clear();
            }
            lastCurrentTimeMillis = currentTimeMillis;
        }

        // 拷贝数据到全局缓存
        mGlobalBuf.writeBytes(msg);
        LogUtils.e(TAG, "GlobalBuf.readableBytes = " + mGlobalBuf.readableBytes());

        List<SocketBusEvent.SocketCarrier> list = new ArrayList<>();
        SocketBusEvent.SocketCarrier socketCarrier;
        // 用循环处理数据积累
        while (null != (socketCarrier = parseData())) {
            list.add(socketCarrier);
        }

        if (list.isEmpty()) {
            return null;
        } else {
            return list;
        }
    }

    /**
     * 解析数据
     * @return
     */
    private SocketBusEvent.SocketCarrier parseData(){
        // 定位ReaderIndex，用来reset的
        mGlobalBuf.markReaderIndex();

        // 数据长度不够【1字节】
        if (mGlobalBuf.readableBytes() < 1) {
            // 没有读，不用resetReaderIndex()
            //mGlobalBuf.resetReaderIndex();
            return null;
        }
        byte type = mGlobalBuf.readByte();

        // 心跳应答
        if(0 == type){

            SocketBusEvent.SocketCarrier socketCarrier = new SocketBusEvent.SocketCarrier();
            socketCarrier.setType(RESPONSE_HEART);
            //EventBus.getDefault().post(socketCarrier);

            // 将已读的数据丢弃，增加可写区域
            //mGlobalBuf.discardReadBytes();
            return socketCarrier;
        }

        //LogUtils.e(TAG, "Check Length Bytes");
        // 数据长度不够【4字节】
        if (mGlobalBuf.readableBytes() < 4) {
            mGlobalBuf.resetReaderIndex();
            return null;
        }

        int length = mGlobalBuf.readInt();
        //LogUtils.e(TAG, "Protocol Length = " + length);
        // 数据出错
        if (length < 0 || length > MAX_LENGTH) {
            mGlobalBuf.clear();
            return null;
        }

        //LogUtils.e(TAG, "ReadableBytes = " + mGlobalBuf.readableBytes());
        // 数据分包发了，长度不够
        if(mGlobalBuf.readableBytes() < length){
            // 读索引重置到mark
            mGlobalBuf.resetReaderIndex();
            return null;
        }
        //LogUtils.e(TAG, "OKAY");

        switch (type) {
            // 交互协议应答
            case 1:
                byte[] header = new byte[length];
                mGlobalBuf.readBytes(header);
                SocketBusEvent.SocketCarrier socketCarrier = new SocketBusEvent.SocketCarrier();
                socketCarrier.setType(RESPONSE_MSG);
                socketCarrier.setHeader(header);
                //EventBus.getDefault().post(socketCarrier);

                // 将已读的数据丢弃，增加可写区域
                mGlobalBuf.discardReadBytes();
                return socketCarrier;

            // 数据协议应答
            case 2:
                // 数据长度不够【4字节】
                if (mGlobalBuf.readableBytes() < 4) {
                    mGlobalBuf.resetReaderIndex();
                    return null;
                }
                int dataHeaderLength = mGlobalBuf.readInt();
                // 数据出错
                if (dataHeaderLength < 0 || dataHeaderLength > MAX_LENGTH) {
                    mGlobalBuf.clear();
                    return null;
                }
                // 数据分包发了，长度不够
                if(mGlobalBuf.readableBytes() < dataHeaderLength){
                    mGlobalBuf.resetReaderIndex();
                    return null;
                }
                byte[] dataHeader = new byte[dataHeaderLength];
                mGlobalBuf.readBytes(dataHeader);

                // 数据长度不够【4字节】
                if(mGlobalBuf.readableBytes() < 4){
                    return null;
                }
                int dataBodyLength = mGlobalBuf.readInt();
                // 数据出错
                if (dataBodyLength < 0 || dataBodyLength > MAX_LENGTH) {
                    mGlobalBuf.clear();
                    return null;
                }
                // 数据分包发了，长度不够
                if (mGlobalBuf.readableBytes() < dataBodyLength) {
                    mGlobalBuf.resetReaderIndex();
                    return null;
                }
                byte[] dataBody = new byte[dataBodyLength];
                mGlobalBuf.readBytes(dataBody);

                socketCarrier = new SocketBusEvent.SocketCarrier();
                socketCarrier.setType(RESPONSE_DATA);
                socketCarrier.setHeader(dataHeader);
                socketCarrier.setBody(dataBody);
                //EventBus.getDefault().post(socketCarrier);

                // 将已读的数据丢弃，增加可写区域
                mGlobalBuf.discardReadBytes();
                return socketCarrier;

            default:
                break;
        }
        return null;
    }

}
