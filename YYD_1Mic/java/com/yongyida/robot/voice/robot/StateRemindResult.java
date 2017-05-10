package com.yongyida.robot.voice.robot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.yongyida.robot.entity.Notice;
import com.yongyida.robot.voice.base.BaseMessage;
import com.yongyida.robot.voice.data.RobotStateData;
import com.yongyida.robot.voice.utils.LogUtils;
/**
 * 获取提醒结果.
 * 
 * @author Administrator
 *
 */
public class StateRemindResult extends BaseMessage{

	@Override
	public void execute() {
		
		LogUtils.showLogInfo("success", "StateRemindResult");
		
		Map<String, String> map = new HashMap<String, String>();
	
		if (!mainServiceInfo.getCall()) {
			try {
				if (robot.getContrallState().equals(RobotStateData.STATE_CONTRALL)) {
					
					ArrayList<Notice> list = intent.getParcelableArrayListExtra("noticeList");
					Gson gson = new Gson();
					if(list == null){
						
						map.put("cmd", "/robot/callback");
						map.put("command", "get data is null");
						mainServiceInfo.setRemindMap(map);
						mainServiceInfo.setRemind(true);
						
						return;
					}
					
					String json = gson.toJson(list);
					String data = "{\"cmd\":\"remind_result\",\"data\":" + json + "}";
					
					map.put("cmd", "/robot/callback");
					map.put("command", data);
					mainServiceInfo.setRemindMap(map);
					mainServiceInfo.setRemind(true);
					
				}
			} catch (Throwable e) {
				
				map.put("cmd", "/robot/callback");
				map.put("command", "get data is null");
				mainServiceInfo.setRemindMap(map);
				mainServiceInfo.setRemind(true);
				
				LogUtils.showLogInfo("success", null, e);
			}
		}		
	}

}
