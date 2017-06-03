package com.zccl.ruiqianqi.socket.remotesocket.handler.udpserver;

import com.zccl.ruiqianqi.tools.LogUtils;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * Created by zc on 2015/12/30.
 *
 4.X版本新增的内存池确实非常高效，但是如果使用不当则会导致各种严重的问题。诸如内存泄露这类问题，功能测试并没有异常，
 如果相关接口没有进行压测或者稳定性测试而直接上线，则会导致严重的线上问题。

 内存池 PooledByteBuf 的使用建议：
 1.申请之后一定要记得释放，Netty自身Socket读取和发送的ByteBuf系统会自动释放，用户不需要做二次释放；
 如果用户使用Netty的内存池在应用中做ByteBuf的对象池使用，则需要自己主动释放；

 2.避免错误的释放：跨线程释放、重复释放等都是非法操作，要避免。
 特别是跨线程申请和释放，往往具有隐蔽性，问题定位难度较大；

 3.防止隐式的申请和分配：之前曾经发生过一个案例，为了解决内存池跨线程申请和释放问题，有用户对内存池做了二次包装，
 以实现多线程操作时，内存始终由包装的管理线程申请和释放，这样可以屏蔽用户业务线程模型和访问方式的差异。
 谁知运行一段时间之后再次发生了内存泄露，最后发现原来调用ByteBuf的write操作时，如果内存容量不足，会自动进行容量扩展。
 扩展操作由业务线程执行，这就绕过了内存池管理线程，发生了“引用逃逸”。该Bug只有在ByteBuf容量动态扩展的时候才发生，
 因此，上线很长一段时间没有发生，直到某一天......因此，大家在使用Netty 4.X的内存池时要格外当心，特别是做二次封装时，一定要对内存池的实现细节有深刻的理解。

 */
public class UDPServerDecoderHandler extends MessageToMessageDecoder<DatagramPacket> {

    // 类标志
    private static String TAG = UDPServerDecoderHandler.class.getSimpleName();

    public UDPServerDecoderHandler(){
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        LogUtils.e(TAG, "channelRegistered");

        // 加入数据处理链
        ctx.channel().pipeline().addLast(new UDPServerSimpleHandler());

        super.channelRegistered(ctx);

    }

    /**
     * PooledByteBufAllocator在哪个线程申请，就得在哪个线程释放！
     * 所以继承自这个 SimpleChannelInboundHandler 因为它有默认的实现【默认释放了】
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {

        //LogUtils.e(TAG, "发布数据的线程ID=" + Thread.currentThread().getId());

        // 这个缓冲区必须在这个方法释放，所以得另想办法传送数据了
        ByteBuf byteBuf = msg.copy().content();

        //byteBuf.toString(Charset.forName("UTF-8"));
        //byteBuf.toString(CharsetUtil.UTF_8);
        byte[] req = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(req);
        out.add(req);


        // 向客户端回发消息，说明已收到
        String json = "copy that, timestamp:" + System.currentTimeMillis() + "\n";
        // 由于数据报的数据是以字符数组传的形式存储的，所以转化数据
        byte[] bytes = json.getBytes("UTF-8");
        DatagramPacket data = new DatagramPacket(Unpooled.copiedBuffer(bytes), msg.sender());
        // 向客户端发送消息
        ctx.writeAndFlush(data);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LogUtils.e(TAG, "exceptionCaught", cause);
        super.exceptionCaught(ctx, cause);
    }
}
