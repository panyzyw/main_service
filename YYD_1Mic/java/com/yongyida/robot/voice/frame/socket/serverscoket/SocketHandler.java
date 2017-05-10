package com.yongyida.robot.voice.frame.socket.serverscoket;

import java.nio.ByteOrder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.DynamicChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.yongyida.robot.voice.base.BaseCmd;
import com.yongyida.robot.voice.bean.MainServiceInfo;
import com.yongyida.robot.voice.bean.PhotoCmdInfo;
import com.yongyida.robot.voice.bean.RobotInfo;
import com.yongyida.robot.voice.broadcast.BatteryBroadcastReceiver;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.data.RobotStateData;
import com.yongyida.robot.voice.data.UrlData;
import com.yongyida.robot.voice.robot.CmdRobot;
import com.yongyida.robot.voice.utils.LogUtils;
import com.yongyida.robot.voice.utils.NetUtils;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;
import com.yongyida.robot.voice.utils.SharePreferenceUtils;

public class SocketHandler extends SimpleChannelHandler {

	private static final String TAG = "SocketHandler";
	
	public static Channel channel;

	public RobotInfo robot = RobotInfo.getInstance();

	private Context context;
	
	private BatteryBroadcastReceiver receiver;

	public SocketHandler(Context context) {
		this.context = context;
		initSocketHandler();
	}

	public void initSocketHandler(){
		
	}
	
	/* 连接成功调用 */
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		robotLogin(ctx);

	}

	/* 写数据调用 */
	@Override
	public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		Object o = e.getMessage();
		if (o instanceof String) {
			writeSocket(0, e.getMessage(), ctx, e);
		} else if (o instanceof PhotoCmdInfo) {
			writeSocket(2, e.getMessage(), ctx, e);
		} else if (o instanceof Map) {
			writeSocket(1, e.getMessage(), ctx, e);
		}
		
	}

	/* 接收成功调用 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		Object object = e.getMessage();
		if (object == null)	return;
		if (!(object instanceof Collection)) return;
		
		@SuppressWarnings("unchecked")
		Collection<Object> c = Collection.class.cast(object);
		
		for (Object o : c) {
			if (org.jboss.netty.handler.timeout.IdleState.READER_IDLE.equals(o)) {
				//LogUtils.showLogInfo(TAG, "receive hart");

			} else if (o instanceof Result1) {
				
				LogUtils.showLogInfo(TAG, "receive success" + o.toString());
				if (o instanceof JSONObject) {
					parseData((JSONObject)o);
				}
					
			} else if (o instanceof Result2) { }
		
		}
	}
	
	/* 关闭连接调用 */
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		super.channelClosed(ctx, e);
		LogUtils.showLogInfo(TAG, "channelClosed");
		closeChannel();
	}
	
	
	/**
	 * 机器人登录
	 * 
	 * @param ctx
	 */
	private void robotLogin(ChannelHandlerContext ctx) {
		
		if(context == null) return;
		SharePreferenceUtils sp = SharePreferenceUtils.getInstance(context);

		channel = ctx.getChannel();
		// 系统版本号
		String version = android.os.Build.DISPLAY;

		if (receiver == null)
			receiver = new BatteryBroadcastReceiver();
		if (robot.getmRegister().equals(RobotStateData.STATE_UNREGISTER)) {

			robot.setmRegister(RobotStateData.STATE_REGISTER);
			context.registerReceiver(receiver, new IntentFilter(
					Intent.ACTION_BATTERY_CHANGED));
		}
		Log.d("jlog", "id:" + sp.getString("id", "123456"));
		while (robot.getBattery().equals("null"));
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cmd", "/robot/login");
		map.put("id", sp.getString("id", "123456"));
		map.put("serial", sp.getString("serial", "123456"));
		map.put("version", version);
		map.put("battery", robot.getBattery());
		LogUtils.showLogInfo(TAG, robot.getBattery());
		channel.write(map);

		LogUtils.showLogInfo(TAG, map.toString());

		new HartThread().start();
	}
	
	
	/**
	 * 关闭连接函数
	 * 
	 */
	private void closeChannel(){
		
		if(robot.getmRegister().equals(RobotStateData.STATE_REGISTER)){
			
			robot.setmRegister(RobotStateData.STATE_UNREGISTER);
			context.unregisterReceiver(receiver);
			robot.setBattery("null");
		}
		if (robot.getContrallState().equals(RobotStateData.STATE_CONTRALL)) {
			robot.setContrallState(RobotStateData.STATE_UNCONTRALL);
			Map<String, String> map2;
			map2 = new HashMap<String, String>();
			map2.put(GeneralData.ACTION, IntentData.INTENT_STOP);
			map2.put(GeneralData.RESULT, GeneralData.SHUT_DOWN_VIDEO);
			map2.put(GeneralData.FROM, GeneralData.CLOSE_CHANNEL);
			SendBroadcastUtils.sendBroad(context, map2);
			
		}
		
		if(robot.getOnline().equals(RobotStateData.STATE_LOGIN_FAIL)) return;
		
		robot.setOnline(RobotStateData.STATE_LOGIN_DEFAULT);
		
		if(UrlData.TCP_IP != null){
			int port = NetUtils.getSocketPort(context);
			if (port != -1) {
				SocketConnect.getInstace(context, UrlData.TCP_IP, UrlData.ROBOT_TCP_PORT).socketConnect(5);
			}
		}
		
	}
	
	
	
	
	class HartThread extends Thread{
		@Override
		public void run() {
			synchronized(HartThread.class){
				
			//	Map<String, Object> hartMap = new HashMap<String, Object>();
				//hartMap.put("cmd", "/");
				while (channel.isConnected()) {
					if (robot.getOnline().equals(RobotStateData.STATE_LOGIN_SUCCESS)) {
						try {
							channel.write("0").awaitUninterruptibly();
							//LogUtils.showLogInfo(TAG,  "write hart");
							if (robot.getContrallState().equals(RobotStateData.STATE_CONTRALL)) {
								Thread.sleep(1000);
							} else {
								Thread.sleep(8000);
							}
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}
				
			}
		}
	}
	
	/**
	 * 解析服务器发送的数据
	 * 
	 * @param json
	 */
	private void parseData(JSONObject json){
		try {
			
			if (!MainServiceInfo.getInstance().getCall()) {
				String cmd = json.optString("cmd");
				cmd = cmd.trim();
				BaseCmd C = CmdRobot.getCmd(cmd);
				if(C != null){
					C.setJson(json);
					C.setContext(context);
					C.execute();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	

	/**
	 * 写发送的数据
	 * @param head
	 * @param obj
	 * @param ctx
	 * @param e
	 */
	public void writeSocket(int head, Object obj, ChannelHandlerContext ctx,
			MessageEvent e) {
		if (obj == null) {
			return;
		}
		try {
			ChannelBuffer buffer = null;
			switch (head) {
			case 0:
				buffer = new DynamicChannelBuffer(ByteOrder.BIG_ENDIAN, 1024);
				buffer.writeByte(0);
				buffer.writeByte(0);
				buffer.writeByte(0);
				buffer.writeByte(0);
				buffer.writeByte(0);
				buffer.writeByte(0);
				buffer.writeByte(0);
				buffer.writeByte(0);
				Channels.write(ctx, e.getFuture(), buffer);
				break;
			case 1:
				if (obj instanceof Map) {
					Map<String, String> map = (Map<String, String>) obj;
					JSONObject json = new JSONObject(map);
					byte[] datas = null;

					datas = json.toString().getBytes("utf-8");
					int length = datas.length;
					buffer = new DynamicChannelBuffer(ByteOrder.BIG_ENDIAN, length + 1024);

					buffer.writeByte(head);
					buffer.writeByte(0);
					buffer.writeByte(0);
					buffer.writeByte(0);
					buffer.writeByte(0);
					buffer.writeByte(0);
					buffer.writeByte(0);
					buffer.writeByte(0);
					buffer.writeInt(length);
					buffer.writeBytes(datas);

					Channels.write(ctx, e.getFuture(), buffer);

				}
				break;

			case 2:
				PhotoCmdInfo photo = (PhotoCmdInfo) obj;
				buffer = new DynamicChannelBuffer(ByteOrder.BIG_ENDIAN, 1024);
				Map<String, String> map = photo.getPhotoMap();
				JSONObject json = new JSONObject(map);
				byte[] datas = null;

				datas = json.toString().getBytes("utf-8");
				int headJsonLength = datas.length;
				int headByteLength = photo.getPhoto_Data().length;
				
				buffer.writeByte(2);
				buffer.writeByte(0);
				buffer.writeByte(0);
				buffer.writeByte(0);
				buffer.writeByte(0);
				buffer.writeByte(0);
				buffer.writeByte(0);
				buffer.writeByte(0);

				buffer.writeInt(headByteLength + headJsonLength + 8);
				buffer.writeInt(headJsonLength);
				buffer.writeBytes(datas);
				buffer.writeInt(headByteLength);
				buffer.writeBytes(photo.getPhoto_Data());

				Channels.write(ctx, e.getFuture(), buffer);

				break;
			}
		} catch (Throwable e2) {
			e2.printStackTrace();
		}

	}
}
