package com.yongyida.robot.voice.robot;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.yongyida.robot.voice.base.BasePushCmd;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.frame.iflytek.VoiceText;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;
/**
 * APP文本推送
 * 
 * @author Administrator
 *
 */
public class PushText extends BasePushCmd{

	@Override
	public void execute() {
		try {
			obJson = new JSONObject(json.optString("command"));
			if(obJson != null){
				Map<String, String> map2 ;
				map2 = new HashMap<String, String>();
				map2.put(GeneralData.ACTION, IntentData.INTENT_STOP);
				map2.put(GeneralData.RESULT, GeneralData.APPTEXT);
				map2.put(GeneralData.FROM, GeneralData.APPTEXT);
				SendBroadcastUtils.sendBroad(context, map2);
				map2 = null;
				
				if(factory != null){
					factory.setFactory(VoiceText.getInstance(context));
					factory.parseStart(obJson.getString("type"));
				}
				
			}
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
