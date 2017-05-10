package com.yongyida.robot.voice.robot;

import java.util.HashMap;
import java.util.Map;

import com.yongyida.robot.voice.base.BaseCmd;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.frame.iflytek.VoiceRead;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;

/**
 * 查询用户信息结果（是否被控制）.
 * @author Administrator
 *
 */
public class CmdQueryUserMessageResult extends BaseCmd{

	@Override
	public void execute() {
		
		if(json == null){
			return;
		}
		if(json.optString("ret", "-1").equals("0")){
			Map<String, String> map2;
			map2 = new HashMap<String, String>();
			map2.put(GeneralData.ACTION, IntentData.INTENT_USER_MESSAGE_RESULT);
			map2.put(GeneralData.RESULT, json.toString());
			SendBroadcastUtils.sendBroad(context, map2);
		}else{
			
			factory.setFactory(VoiceRead.getInstence(context));
			factory.parseStart("被控制状态不能查询用户信息");
		}
		
	}

}
