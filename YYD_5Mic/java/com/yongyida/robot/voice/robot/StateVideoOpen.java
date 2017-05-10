package com.yongyida.robot.voice.robot;

import java.util.HashMap;
import java.util.Map;

import com.yongyida.robot.voice.base.BaseMessage;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;
/**
 * 视频打开.
 * 
 * @author Administrator
 *
 */
public class StateVideoOpen extends BaseMessage {

	@Override
	public void execute() {
		mainServiceInfo.setOpenVideo("open");
		Map<String, String> map2 ;
		map2 = new HashMap<String, String>();
		map2.put(GeneralData.ACTION, IntentData.INTENT_STOP);
		
		SendBroadcastUtils.sendBroad(context, map2);
		
		//SendBroadcastBiz.sendBroad(context, IntentData.INTENT_STOP);
		
	}

}
