package com.yongyida.robot.voice.robot;

import org.json.JSONObject;

import com.yongyida.robot.voice.base.BasePushCmd;
import com.yongyida.robot.voice.data.IntentData;

/**
 * 更新提醒
 * @author Administrator
 *
 */
public class PushRemindUpdate extends BasePushCmd{  
	 
	@Override
	public void execute() {
		try {
			obJson = new JSONObject(json.optString("command"));
			if(obJson != null){
				sendBroadcast(IntentData.NOTIFICATION_UPDATE , obJson);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
}