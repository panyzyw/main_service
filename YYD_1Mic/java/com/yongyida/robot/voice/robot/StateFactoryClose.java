package com.yongyida.robot.voice.robot;

import java.util.HashMap;
import java.util.Map;

import com.yongyida.robot.voice.base.BaseMessage;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;

/**
 * 关闭工厂模式.
 * 
 * @author Administrator
 *
 */
public class StateFactoryClose extends BaseMessage{

	@Override
	public void execute() {
		mainServiceInfo.setFactory(false);
		Map<String, String> map;
		map = new HashMap<String, String>();
		map.put(GeneralData.ACTION, IntentData.INTENT_STOP);
		
		SendBroadcastUtils.sendBroad(context, map);
		//SendBroadcastBiz.sendBroad(context, IntentData.INTENT_STOP);
	}

}
