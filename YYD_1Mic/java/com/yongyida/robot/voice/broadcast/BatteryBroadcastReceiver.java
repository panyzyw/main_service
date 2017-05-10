package com.yongyida.robot.voice.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yongyida.robot.voice.bean.RobotInfo;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.RobotStateData;
import com.yongyida.robot.voice.frame.socket.serverscoket.SocketHandler;
import com.yongyida.robot.voice.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;

public class BatteryBroadcastReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {

		if(intent == null) return;
		if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){ 

			String battery;
			//获取当前电量 
			int level = intent.getIntExtra("level", 0);
			//电量的总刻度 
			int scale = intent.getIntExtra("scale", 100);
			//把它转成百分比 
			battery = ((level*100)/scale) + "" ;
			LogUtils.showLogInfo(GeneralData.SUCCESS, "电池电量为"+((level*100)/scale)+"%"); 
			
			//RobotInfo.getInstance().setBattery(battery);
			if(RobotInfo.getInstance().getBattery().equals(battery)){
				return;
			}
			RobotInfo.getInstance().setBattery(battery);
			if(SocketHandler.channel != null && RobotInfo.getInstance().getOnline().equals(RobotStateData.STATE_LOGIN_SUCCESS)){
				Log.d("jlog","batter send"+battery);
	    		Map<String, String> map = new HashMap<String, String>();
	    		map.put("cmd", "/robot/flush");
	    		map.put("battery", battery);
	    		SocketHandler.channel.write(map);
	    	}
			
		} 
		
		
	}

}
