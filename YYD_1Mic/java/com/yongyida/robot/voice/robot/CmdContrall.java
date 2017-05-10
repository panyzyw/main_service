package com.yongyida.robot.voice.robot;

import java.util.HashMap;
import java.util.Map;

import com.yongyida.robot.voice.base.BaseCmd;
import com.yongyida.robot.voice.bean.RobotInfo;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.data.RobotStateData;
import com.yongyida.robot.voice.data.VoiceData;
import com.yongyida.robot.voice.utils.MediaPlayUtils;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;
/**
 * 机器人控制
 * 
 * @author Administrator
 *
 */
public class CmdContrall extends BaseCmd {

	@Override
	public void execute() {
		
		robot = RobotInfo.getInstance();
		String ret = json.optString("ret");
		if ("".equals(ret)) {
			return;
		}
		ret = ret.trim();
		if (ret.equals("0")) {
			robot.setContrallState(RobotStateData.STATE_CONTRALL);
			// 控制上后停下之前的状态
			Map<String, String> map2;
			map2 = new HashMap<String, String>();
			map2.put(GeneralData.ACTION, IntentData.INTENT_STOP);
			map2.put(GeneralData.RESULT, GeneralData.CONTRALL);
			SendBroadcastUtils.sendBroad(context, map2);

			MediaPlayUtils.getInstance().playMusic(context, VoiceData.CONTROLL_CONNECT);
		}

	}

}
