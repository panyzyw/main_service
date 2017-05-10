package com.yongyida.robot.voice.robot;

import java.util.HashMap;
import java.util.Map;

import com.yongyida.robot.voice.base.BaseCmd;
import com.yongyida.robot.voice.bean.MainServiceInfo;
import com.yongyida.robot.voice.bean.RobotInfo;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.data.RobotStateData;
import com.yongyida.robot.voice.data.VoiceData;
import com.yongyida.robot.voice.utils.MediaPlayUtils;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;

/**
 * 解除控制
 * 
 * @author Administrator
 * 
 */
public class CmdUnContrall extends BaseCmd {

	MainServiceInfo mainServiceInfo = MainServiceInfo.getInstance();

	@Override
	public void execute() {

		robot = RobotInfo.getInstance();

		robot.setContrallState(RobotStateData.STATE_UNCONTRALL);
		mainServiceInfo.setPlayComplete(false);

		Map<String, String> map2;
		map2 = new HashMap<String, String>();
		map2.put(GeneralData.ACTION, IntentData.INTENT_STOP);
		map2.put(GeneralData.RESULT, GeneralData.SHUT_DOWN_VIDEO);
		SendBroadcastUtils.sendBroad(context, map2);

		MediaPlayUtils.getInstance().playMusic(context, VoiceData.CONTROLL_DISCONNECT);

	}

}
