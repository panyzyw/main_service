package com.zccl.ruiqianqi.socket.remotesocket.handler;

import android.content.Context;

import com.zccl.ruiqianqi.socket.eventbus.SocketBusEvent;
import com.zccl.ruiqianqi.tools.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import java.util.List;

/**
 * Created by ruiqianqi on 2017/4/8 0008.
 */

public class TCPClientHandler extends SimpleChannelHandler {

    // 类标志
    private static String TAG = TCPClientHandler.class.getSimpleName();

    /** 应用上下文 */
    private Context mContext;

    // 重连接口
    private IReConnectCallback reConnectCallback;

    public TCPClientHandler(Context context){
        this.mContext = context;
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        LogUtils.e(TAG, "channelOpen");
        super.channelOpen(ctx, e);
    }

    @Override
    public void channelBound(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        LogUtils.e(TAG, "channelBound");
        super.channelBound(ctx, e);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        LogUtils.e(TAG, "channelConnected");
        super.channelConnected(ctx, e);
        // 回调连接已建立
        if(null != reConnectCallback){
            reConnectCallback.OnConnected();
        }
    }

    /**
     * 1. 已经与远程主机建立的连接，远程主机主动关闭连接，或者网络异常连接被断开的情况
     * 2. 已经与远程主机建立的连接，本地客户机主动关闭连接的情况
     * 3. 本地客户机在试图与远程主机建立连接时，遇到类似与connection refused这样的异常，未能连接成功时
     * 而只有当本地客户机已经成功的与远程主机建立连接（connected）时，连接断开的时候才会触发channelDisconnected事件，即对应上述的1和2两种情况。
     * @param ctx
     * @param e
     * @throws Exception
     */
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        LogUtils.e(TAG, "channelDisconnected");
        super.channelDisconnected(ctx, e);
        if(null != reConnectCallback){
            reConnectCallback.OnDisconnected();
        }
    }

    @Override
    public void channelUnbound(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        LogUtils.e(TAG, "channelUnbound");
        super.channelUnbound(ctx, e);
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        LogUtils.e(TAG, "channelClosed");
        super.channelClosed(ctx, e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        LogUtils.e(TAG, "exceptionCaught", e.getCause());
        super.exceptionCaught(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        //LogUtils.e(TAG, "messageReceived");
        Object object = e.getMessage();
        if (null == object)
            return;
        List<SocketBusEvent.SocketCarrier> list = (List<SocketBusEvent.SocketCarrier>) object;
        for (int i = 0; i < list.size(); i++) {
            EventBus.getDefault().post(list.get(i));
        }
    }

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        //LogUtils.e(TAG, "writeRequested");
        //super.writeRequested(ctx, e);

        Object object = e.getMessage();
        if(object instanceof ChannelBuffer){
            Channels.write(ctx, e.getFuture(), object);
        }else if (object instanceof String){

        }else if(object instanceof byte[]){

        }
    }

    /*
    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        LogUtils.e(TAG, "handleUpstream");
        super.handleUpstream(ctx, e);
    }

    @Override
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        LogUtils.e(TAG, "handleDownstream");
        super.handleDownstream(ctx, e);
    }
    */

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
