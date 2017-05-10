package com.yongyida.robot.voice.frame.socket.localscket;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.net.LocalSocket;
import android.util.Log;

import com.yongyida.robot.voice.bean.RobotInfo;
import com.yongyida.robot.voice.data.RobotStateData;
import com.yongyida.robot.voice.subservice.SubFunction;
import com.yongyida.robot.voice.utils.LogUtils;

public class SocketChannel implements Channel{
	
	public static final String SCOKETNAME = "com.yongyida.robot.mainservice.tcp.server";
	public static final String SCOKET_CONNECT = "0";
	public static final String SCOKET_DISCONNECT = "-1";
	
	protected static Map<String, LocalSocket> channelMap = new HashMap<String, LocalSocket>();
	protected LocalSocket channel;
	protected String id;  //发送数据的ID
	protected String data; //要发送的数据
	protected ThreadLocal<String> connect = new ThreadLocal<String>(); //socket是否连接
	protected ThreadLocal<LocalSocket> scoket = new ThreadLocal<LocalSocket>();
	protected ThreadLocal<String> userId = new ThreadLocal<String>();
	protected Data pack = new Data();
	
	protected byte[] byteBuffer;

	/**
	 * 发送数据.
	 */
	@Override
	public void sendData() {
		try {
			if(data == null) return;
			int bufferLen = data.getBytes().length + 20;
			byte[] sendBuffer = new byte[bufferLen];
			pack.packData(sendBuffer, data, 0, data.getBytes().length);
			channel = channelMap.get(id);
			if(channel == null) return;
			OutputStream os = channel.getOutputStream();
			os.write(sendBuffer, 0, sendBuffer.length);
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 接收本地数据.
	 */
	@Override
	public void revData() {
	
		synchronized (SocketChannel.class) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					
					DataInputStream is = null;
					byte[] jsonBuffer = null;
					connect.remove();
					scoket.remove();
					connect.set(SCOKET_CONNECT);
					scoket.set(channel);
					
					if(scoket.get() == null) return;
					
					try {
						is = new DataInputStream(scoket.get().getInputStream());
					} catch (IOException e2) {
						e2.printStackTrace();
						try {
							scoket.get().close();
							scoket.remove();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					while (connect.get().equals(SCOKET_CONNECT)) {
						LogUtils.showLogInfo("success", "while");
						try {
							
							is.skip(12);
							int jsonLength = is.readInt();
							if (jsonLength > 0) {
								jsonBuffer = new byte[jsonLength];
								is.readFully(jsonBuffer, 0, jsonLength);
							}
							int byteLength = is.readInt();
							if (byteLength > 0) {
								byteBuffer = new byte[byteLength];
								is.readFully(byteBuffer, 0, byteLength);
							}
							String json = new String(jsonBuffer);
							parseData(json);
							
							byteBuffer = null;
							jsonBuffer = null; 
							
						} catch (Throwable e) {
							e.printStackTrace();
							
							LogUtils.showLogInfo("success", "socket Exception" + e);
							if (is != null) {
								try {
									is.close();
									is = null;
									
								} catch (IOException e1) {
									e1.printStackTrace();
								}

							}
							connect.set(SCOKET_DISCONNECT);
						}
		
					}
				}
			}).start();
		}
		
	}
	
	
	private void parseData(String json){
		try {
			JSONObject objJson = new JSONObject(json);
			String cmd = objJson.optString("cmd", "");
			LogUtils.showLogInfo("success", json);
			if ("/localserver/login".equals(cmd)) {
				String user = objJson.optString("source", "-1");
				if (channelMap.get(userId) != null) {
					channelMap.get(userId).close();
					channelMap.remove(userId);
				}
				userId.set(user);
				channelMap.put(user, scoket.get());
				
				Map<String, String> callback = new HashMap<String, String>();
				if (RobotInfo.getInstance().getOnline().equals(RobotStateData.STATE_LOGIN_SUCCESS)
						&& !RobotInfo.getInstance().getRid().equals(RobotStateData.STATE_DEFAULT_RID)) {
					callback.put("cmd", "/localserver/login/callback");
					callback.put("ret", "0");
					callback.put("name", RobotInfo.getInstance().getName());
					callback.put("id", RobotInfo.getInstance().getRid());
					LogUtils.showLogInfo("success", "id : " + RobotInfo.getInstance().getRid());

				} else {
					callback.put("cmd", "/localserver/login/callback");
					callback.put("ret", "-1");
				}
				id = userId.get();
				data = new JSONObject(callback).toString();
				sendData();
				callback = null;

			} else {
				
				Log.d("success", "本地协议 ： " + cmd);
				SubFunction function = SubFunction.getFunctions(cmd);
				if(function == null) return;
				function.setJson(json);
				function.run();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
