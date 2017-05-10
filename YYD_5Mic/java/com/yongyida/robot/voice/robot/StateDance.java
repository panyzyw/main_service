package com.yongyida.robot.voice.robot;

import java.util.HashMap;
import java.util.Map;

import com.yongyida.robot.voice.base.BaseMessage;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.utils.FileUtil;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;
/**
 * 摸肩跳舞.
 * @author Administrator
 *
 */
public class StateDance extends BaseMessage{
	
	//File file = new File(FileUtil.getSdcardPath() + "/dance.txt");

	@Override
	public void execute() {
		if(mPlayer.isPlaying()){
			mPlayer.stopMusic();
		}
		String video = mainServiceInfo.getOpenVideo();
		Map<String, String> map;
		map = new HashMap<String, String>();
		
		map.put("video", video);
		map.put(GeneralData.ACTION, IntentData.INTENT_DANCE);
		map.put(GeneralData.RESULT, "-1");
		SendBroadcastUtils.sendBroad(context, map);
		
		FileUtil.putToFile(context, "com.yydrobot.DANCE", "dance.log");
		
	}

}
