package com.yongyida.robot.voice.frame.socket.serverscoket;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.DynamicChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 解码类;
 * @author Administrator
 *
 */
public class Decoder extends FrameDecoder {


	protected ChannelBuffer buffer = new DynamicChannelBuffer(ByteOrder.BIG_ENDIAN, 1024);

	protected byte type = 0;

	protected int head = 0;

	protected long last_currentTimeMillis = 0;

	protected long read_time_out = 10000;
	
	protected int max_head = 10240000;

	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
		
		//LogUtils.showLogInfo("success", "decoder");
		if (!channel.isReadable()) {
			channel.close();
			return null;
		}

		if (last_currentTimeMillis != 0) {
			long currentTimeMillis = System.currentTimeMillis();
			if (currentTimeMillis - last_currentTimeMillis > read_time_out) {
				type = 0;
				head = 0;
				this.buffer.clear();
			}
			last_currentTimeMillis = currentTimeMillis;
		}

//		int readableBytes = buffer.readableBytes();
		this.buffer.writeBytes(buffer);
		/*Log.i("success", "type=" + type + ",head=" + head + ",readableBytes=" + this.buffer.readableBytes()
				+ ",RemoteAddress=" + channel.getRemoteAddress());*/
		
		Object o = null;
		List<Object> list = new ArrayList<Object>();
		while ((o = decode(channel)) != null) {
			list.add(o);
		}
		
		if (list.isEmpty()) {
			return null;
		} else {
			return list;
		}
	}

	protected Object decode(Channel channel) {
		this.buffer.markReaderIndex();
		
		if (this.buffer.readableBytes() < 1) {
			return null;
		}
//
//		byte[] msg = new byte[8];
//		buffer.getBytes(0, msg);
//		String str = new String(msg, Charset.forName("utf-8"));
//
//		if (str.startsWith("<policy")) {
//			String xml = "<cross-domain-policy> <allow-access-from domain=\"*\" to-ports=\"*\"/></cross-domain-policy>\0";
//			byte[] xmlarray = xml.getBytes(Charset.forName("utf-8"));
//			ChannelBuffer anquanHeader = ChannelBuffers.dynamicBuffer();
//			anquanHeader.writeBytes(xmlarray);
//			channel.write(anquanHeader);
//			return null;
//		}
//
//		// TGW
//		if (str.startsWith("GET")) {
//			return null;
//		}
		 
		byte b1 = buffer.readByte();
		/*byte b2 = buffer.readByte();
		byte b3 = buffer.readByte();
		byte b4 = buffer.readByte();
		byte b5 = buffer.readByte();
		byte b6 = buffer.readByte();
		byte b7 = buffer.readByte();
		byte b8 = buffer.readByte();*/

		type = b1;
	//	LogUtils.showLogInfo("success", "decoder");
		
		if (type == 0) {
			return org.jboss.netty.handler.timeout.IdleState.READER_IDLE;
		}

		head = buffer.readInt();

		if (head > max_head) {
			this.buffer.clear();
			return null;
		}
		if (this.buffer.readableBytes() < head) {
			this.buffer.resetReaderIndex();
			return null;
		}

		Object message = null;
		
		switch (type) {
		case 0:
			message = org.jboss.netty.handler.timeout.IdleState.READER_IDLE;
			break;
		case 1:
			message = decoderResult1();
			break;
		case 2:
			message = decoderResult2();
			break;
		
		}

		type = 0;
		head = 0;
		buffer.discardReadBytes();

		return message;
	}

	private Object decoderResult1() {
		try {
			byte[] body = new byte[head];
			this.buffer.readBytes(body);

			String jsonString = new String(body, "UTF-8");

			try {
				return new Result1(jsonString);
			} catch (Throwable e) {
				
				this.buffer.discardReadBytes();
				return null;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	private Result2 decoderResult2() {
		try {
			// int
			int head1 = this.buffer.readInt(); // length
			if (head1 < 0) {
				this.buffer.resetReaderIndex();
				return null;
			}
			if (this.buffer.readableBytes() < head1) {
				this.buffer.resetReaderIndex();
				return null;
			}
			byte[] body1 = new byte[head1];
			this.buffer.readBytes(body1);

			String jsonString = new String(body1, "UTF-8");
			if (!jsonString.startsWith("{")) {
				this.buffer.resetReaderIndex();
				return null;
			}

			JSONObject json;
			try {
				json = new JSONObject(jsonString);
			} catch (Throwable e) {
				return null;
			}

			// int
			if (this.buffer.readableBytes() < 8) {
				return null;
			}
			int head2 = this.buffer.readInt(); // length
			if (head2 < 0) {
				this.buffer.resetReaderIndex();
				return null;
			}
			if (this.buffer.readableBytes() < head2) {
				this.buffer.resetReaderIndex();
				return null;
			}

			byte[] body2 = new byte[head2];
			this.buffer.readBytes(body2);

			Result2 result2 = new Result2();
			result2.json = json;
			result2.datas = body2;

			return result2;
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return null;
	}

}

class Result1 extends org.json.JSONObject {
	public Result1(String s) throws JSONException {
		super(s);
	}
}

class Result2 {
	public JSONObject json;
	public byte[] datas;
}
