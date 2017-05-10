package com.yongyida.robot.voice.robot;

import java.util.HashMap;
import java.util.Map;

import com.yongyida.robot.voice.base.BaseMessage;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;

/**
 * 打开监控
 * 
 * @author Administrator
 *
 */
public class StateMonitorOpen extends BaseMessage{

	@Override
	public void execute() {
		mainServiceInfo.setOpenVideo("open");
		Map<String, String> map2 ;
		map2 = new HashMap<String, String>();
		map2.put(GeneralData.ACTION, IntentData.INTENT_STOP);
		map2.put(GeneralData.FROM, GeneralData.OPEN_MONITOR);
		
		SendBroadcastUtils.sendBroad(context, map2);
		//SendBroadcastBiz.sendBroad(context, IntentData.INTENT_STOP);		
		
	}

}
