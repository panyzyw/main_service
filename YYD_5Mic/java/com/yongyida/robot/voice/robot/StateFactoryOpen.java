package com.yongyida.robot.voice.robot;

import java.util.HashMap;
import java.util.Map;

import com.yongyida.robot.voice.base.BaseMessage;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.frame.iflytek.VoiceWakeUp;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;
/**
 * 打开工厂模式.
 * 
 * @author Administrator
 *
 */
public class StateFactoryOpen extends BaseMessage{

	@Override
	public void execute() {
		mainServiceInfo.setFactory(true);
		Map<String, String> map2 ;
		map2 = new HashMap<String, String>();
		map2.put(GeneralData.ACTION, IntentData.INTENT_STOP);
		SendBroadcastUtils.sendBroad(context, map2);

		VoiceWakeUp.isFactoryTest=true;
		//SendBroadcastBiz.sendBroad(context, IntentData.INTENT_STOP);
	}

}
