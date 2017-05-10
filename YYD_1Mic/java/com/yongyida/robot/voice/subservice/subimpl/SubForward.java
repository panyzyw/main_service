package com.yongyida.robot.voice.subservice.subimpl;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import com.yongyida.robot.voice.frame.socket.serverscoket.SocketHandler;
import com.yongyida.robot.voice.subservice.SubFunction;
import com.yongyida.robot.voice.utils.LogUtils;


/**
 * 视频数据转发.
 * 
 * @author Administrator
 *
 */
public class SubForward extends SubFunction{
	private static final String TAG = "SubForward";
	
	@Override
	public void run() {
		
		Log.d("success", "SubForward : " + json);
		if(context == null) return ;
		if(json == null) return;
		Map<String, String> map = new HashMap<String, String>();
		map.put("cmd", "/media/push");
		map.put("command", json);
		if (SocketHandler.channel != null) {
			SocketHandler.channel.write(map);
		}
		LogUtils.showLogInfo(TAG, json);
	}
}
