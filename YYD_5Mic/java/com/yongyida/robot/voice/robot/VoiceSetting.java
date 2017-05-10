package com.yongyida.robot.voice.robot;


import com.yongyida.robot.voice.base.BaseMessage;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.frame.iflytek.VoiceWakeUp;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 设置语言模块.
 * @author jack
 *
 */
public class VoiceSetting extends BaseMessage{
	private VoiceLocalization voice;
	private VoiceWakeUp wakeUp;
	@Override
	public void execute() {
		String data = intent.getExtras().getString("data");
		int thresholdValue = intent.getExtras().getInt("thresholdValue",0);
		if(voice==null || wakeUp==null){
			voice=VoiceLocalization.getInstance();
			wakeUp=VoiceWakeUp.getInstance(context);
		}
		if(data != null){
			if(data.equals("rotate_off")){
				voice.setRotateUp(false);
			}else if(data.equals("rotate_on")){
				voice.setRotateUp(true);
			}else if(data.equals("getStatus")){
				sendRotateStatus();
			}else if(data.equals("getThresholdValue")){
				sendThresholdValue();
			}
		}
		if(thresholdValue != 0){
			wakeUp.setThresholdValue(thresholdValue);
		}
	}
	private void sendRotateStatus(){
		Map<String, String> map = new HashMap<String, String>();
		map.put(GeneralData.ACTION, "com.yongyida.robot.VOICE");
		if(voice.getRotateUp()){
			map.put("result", "rotate_on");
			SendBroadcastUtils.sendBroad(context, map);
		}else {
			map.put("result", "rotate_off");
			SendBroadcastUtils.sendBroad(context, map);
		}
	}
	private void sendThresholdValue(){
		Map<String, String> map = new HashMap<String, String>();
		map.put(GeneralData.ACTION, IntentData.INTENT_VOICE);
		map.put("result", Integer.toString(wakeUp.getThresholdValue()));
		SendBroadcastUtils.sendBroad(context, map);
	}

}