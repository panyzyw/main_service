package com.zccl.ruiqianqi.socket.remotesocket.handler.tcpclient;

import android.content.Context;

import com.zccl.ruiqianqi.socket.eventbus.SocketBusEvent;
import com.zccl.ruiqianqi.tools.LogUtils;

import org.greenrobot.eventbus.EventBus;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.zccl.ruiqianqi.socket.eventbus.SocketBusEvent.SocketCarrier.RESPONSE_DATA;
import static com.zccl.ruiqianqi.socket.eventbus.SocketBusEvent.SocketCarrier.RESPONSE_HEART;
import static com.zccl.ruiqianqi.socket.eventbus.SocketBusEvent.SocketCarrier.RESPONSE_MSG;
import static com.zccl.ruiqianqi.socket.remotesocket.NetworkTask.BUF_SIZE;
import static com.zccl.ruiqianqi.socket.remotesocket.NetworkTask.MAX_LENGTH;
import static com.zccl.ruiqianqi.socket.remotesocket.NetworkTask.READ_TIME_OUT;

/**
 * Created by zc on 2015/12/30.
 * netty4 发消息write之后就把消息发给了IO线程，所以write是异步的
 * netty4 收消息之后，由开发者决定是直接在IO线程处理，还是转发到业务线程处理
 *
 * I/O线程NioEventLoop从SocketChannel中读取数据报，将ByteBuf投递到ChannelPipeline，触发ChannelRead事件；
 * I/O线程NioEventLoop调用ChannelHandler链，直到将消息投递到业务线程，然后I/O线程返回，继续后续的读写操作；
 * 业务线程调用ChannelHandlerContext.write(Object msg)方法进行消息发送；
 * 如果是由业务线程发起的写操作，ChannelHandlerInvoker将发送消息封装成Task，放入到I/O线程NioEventLoop的任务队列中，由NioEventLoop在循环中统一调度和执行。放入任务队列之后，业务线程返回；
 * I/O线程NioEventLoop调用ChannelHandler链，进行消息发送，处理Outbound事件，直到将消息放入发送队列，然后唤醒Selector，进而执行写操作。
 * 通过流程分析，我们发现Netty 4修改了线程模型，无论是Inbound还是Outbound操作，统一由I/O线程NioEventLoop调度执行。
 */
public class TCPClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    // 类标志
    private static String TAG = TCPClientHandler.class.getSimpleName();

    /** 应用上下文 */
    private Context mContext;

    // 上次读取响应时的时间戳
    protected long lastCurrentTimeMillis = 0;

    // 用来缓存服务器响应的BUF
    private ByteBuf mGlobalBuf = null;

    // 重连接口
    private IReConnectCallback reConnectCallback;

    public TCPClientHandler(Context context){
        this.mContext = context;
        // 大端格式缓存
        this.mGlobalBuf = Unpooled.buffer(BUF_SIZE);
    }

    /**************************************【第一个被调用】****************************************/
    /**
     * 可共享否
     * @return
     */
    @Override
    public boolean isSharable() {
        LogUtils.e(TAG, "isSharable");
        return super.isSharable();
    }

    /**************************************【建立连接】********************************************/
    /**
     * 事件处理已添加
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        LogUtils.e(TAG, "handlerAdded");
        super.handlerAdded(ctx);
    }

    /**
     * 注册连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        LogUtils.e(TAG, "channelRegistered");
        super.channelRegistered(ctx);
    }

    /**
     * 连接激活
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LogUtils.e(TAG, "channelActive");
        super.channelActive(ctx);

        // 回调连接已建立
        if(null != reConnectCallback){
            reConnectCallback.OnConnected();
        }
    }


    /*************************************【断开连接】*********************************************/
    /**
     * 检测到连接断开
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LogUtils.e(TAG, "channelInactive");
        super.channelInactive(ctx);

        // 回调连接断开
        if(null != reConnectCallback){
            reConnectCallback.OnDisconnected();
        }
    }

    /**
     * 取消注册
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        LogUtils.e(TAG, "channelUnregistered");
        super.channelUnregistered(ctx);
    }

    /**
     * 事件处理已移除
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        LogUtils.e(TAG, "handlerRemoved");
        super.handlerRemoved(ctx);
    }

    /**************************************【读取数据】********************************************/
    /**
     * 读取数据【预留的用户自定义处理接口】
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        LogUtils.e(TAG, "msg = " + msg.readableBytes());

        if (!msg.isReadable()) {
            return;
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

        // 用循环处理数据积累
        while(parseData());
    }

    /**
     * 解析数据
     * @return
     */
    private boolean parseData(){
        // 定位ReaderIndex，用来reset的
        mGlobalBuf.markReaderIndex();

        // 数据长度不够【1字节】
        if (mGlobalBuf.readableBytes() < 1) {
            // 没有读，不用resetReaderIndex()
            //mGlobalBuf.resetReaderIndex();
            return false;
        }
        byte type = mGlobalBuf.readByte();

        // 心跳应答
        if(0 == type){
            /*
            ByteBufEvent.EventMsg eventMsg = new ByteBufEvent.EventMsg();
            eventMsg.setType(0);
            mMyDisruptor.publishEvent(eventMsg);
            */
            SocketBusEvent.SocketCarrier socketCarrier = new SocketBusEvent.SocketCarrier();
            socketCarrier.setType(RESPONSE_HEART);
            EventBus.getDefault().post(socketCarrier);

            // 将已读的数据丢弃，增加可写区域
            //mGlobalBuf.discardReadBytes();
            return true;
        }

        LogUtils.e(TAG, "Check Length Bytes");
        // 数据长度不够【4字节】
        if (mGlobalBuf.readableBytes() < 4) {
            mGlobalBuf.resetReaderIndex();
            return false;
        }

        int length = mGlobalBuf.readInt();
        LogUtils.e(TAG, "Protocol Length = " + length);
        // 数据出错
        if (length < 0 || length > MAX_LENGTH) {
            mGlobalBuf.clear();
            return false;
        }

        LogUtils.e(TAG, "ReadableBytes = " + mGlobalBuf.readableBytes());
        // 数据分包发了，长度不够
        if(mGlobalBuf.readableBytes() < length){
            // 读索引重置到mark
            mGlobalBuf.resetReaderIndex();
            return false;
        }
        LogUtils.e(TAG, "OKAY");

        switch (type) {
            // 交互协议应答
            case 1:
                byte[] header = new byte[length];
                mGlobalBuf.readBytes(header);
                /*
                ByteBufEvent.EventMsg eventMsg = new ByteBufEvent.EventMsg();
                eventMsg.setType(1);
                eventMsg.setBody(body);
                mMyDisruptor.publishEvent(eventMsg);
                */
                SocketBusEvent.SocketCarrier socketCarrier = new SocketBusEvent.SocketCarrier();
                socketCarrier.setType(RESPONSE_MSG);
                socketCarrier.setHeader(header);
                EventBus.getDefault().post(socketCarrier);

                // 将已读的数据丢弃，增加可写区域
                mGlobalBuf.discardReadBytes();
                return true;

            // 数据协议应答
            case 2:
                // 数据长度不够【4字节】
                if (mGlobalBuf.readableBytes() < 4) {
                    mGlobalBuf.resetReaderIndex();
                    return false;
                }
                int dataHeaderLength = mGlobalBuf.readInt();
                // 数据出错
                if (dataHeaderLength < 0 || dataHeaderLength > MAX_LENGTH) {
                    mGlobalBuf.clear();
                    return false;
                }
                // 数据分包发了，长度不够
                if(mGlobalBuf.readableBytes() < dataHeaderLength){
                    mGlobalBuf.resetReaderIndex();
                    return false;
                }

                byte[] dataHeader = new byte[length];
                mGlobalBuf.readBytes(dataHeader);

                // 数据长度不够【4字节】
                if(mGlobalBuf.readableBytes() < 4){
                    return false;
                }
                int dataBodyLength = mGlobalBuf.readInt();
                // 数据出错
                if (dataBodyLength < 0 || dataBodyLength > MAX_LENGTH) {
                    mGlobalBuf.clear();
                    return false;
                }
                // 数据分包发了，长度不够
                if (mGlobalBuf.readableBytes() < dataBodyLength) {
                    mGlobalBuf.resetReaderIndex();
                    return false;
                }

                byte[] dataBody = new byte[length];
                mGlobalBuf.readBytes(dataBody);

                /*
                eventMsg = new ByteBufEvent.EventMsg();
                eventMsg.setType(2);
                eventMsg.setHeader(dataHeader);
                eventMsg.setBody(dataBody);
                mMyDisruptor.publishEvent(eventMsg);
                */
                socketCarrier = new SocketBusEvent.SocketCarrier();
                socketCarrier.setType(RESPONSE_DATA);
                socketCarrier.setHeader(dataHeader);
                socketCarrier.setBody(dataBody);
                EventBus.getDefault().post(socketCarrier);

                // 将已读的数据丢弃，增加可写区域
                mGlobalBuf.discardReadBytes();
                return true;

            default:
                break;
        }
        return false;
    }
    /**
     * 读取完成
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        LogUtils.e(TAG, "channelReadComplete");
        super.channelReadComplete(ctx);
    }

    /************************************【用户事件触发】******************************************/
    /**
     * 用户事件触发
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        LogUtils.e(TAG, "userEventTriggered");
        super.userEventTriggered(ctx, evt);
    }

    /**********************************【异常处理】************************************************/
    /**
     * 异常处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LogUtils.e(TAG, "exceptionCaught", cause);
        super.exceptionCaught(ctx, cause);

        // 抛异常之后，延时10秒，开始检测网络状态，看是否需要重连
        // 抛异常之后，如果连接断开了，则会调用【channelInactive】
        /*
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                // 开始重连
                if(!ctx.channel().isActive()){
                    reConnect(ctx);
                }
            }
        }, 10, TimeUnit.SECONDS);
        */

    }

    /**********************************【其他文方法】**********************************************/
    /**
     * 可写状态改变
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        LogUtils.e(TAG, "channelWritabilityChanged");
        super.channelWritabilityChanged(ctx);
    }

    /**
     * 设置重连接口
     * @param reConnectCallback
     */
    public void setReConnectCallback(IReConnectCallback reConnectCallback) {
        this.reConnectCallback = reConnectCallback;
    }

    /**
     * 重连回调接口
     */
    public interface IReConnectCallback{
        // 连接建立
        void OnConnected();
        // 连接断开
        void OnDisconnected();
    }

}
