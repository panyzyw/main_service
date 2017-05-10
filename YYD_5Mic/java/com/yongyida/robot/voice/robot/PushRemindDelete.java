package com.yongyida.robot.voice.robot;

import org.json.JSONObject;

import com.yongyida.robot.voice.base.BasePushCmd;
import com.yongyida.robot.voice.data.IntentData;
/**
 * 删除提醒.
 * @author Administrator
 *
 */
public class PushRemindDelete extends BasePushCmd{  
	 
	@Override
	public void execute() {
		try {
			obJson = new JSONObject(json.optString("command"));
			if(obJson != null){
				sendBroadcast(IntentData.NOTIFICATION_DEL, obJson);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
}
