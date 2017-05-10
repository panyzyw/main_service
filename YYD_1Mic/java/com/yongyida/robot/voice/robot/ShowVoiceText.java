package com.yongyida.robot.voice.robot;

import org.json.JSONObject;

import com.yongyida.robot.voice.R;
import com.yongyida.robot.voice.base.BaseMessage;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.utils.SharePreferenceUtils;
import com.yongyida.robot.voice.utils.ShowToast;
/**
 * 显示录音文本.
 * 
 * @author Administrator
 *
 */
public class ShowVoiceText extends BaseMessage{
	public static final int SHOW_TEXT = 0;
	public static final int CLOST_TEXT = 1;

	@Override
	public void execute() {

		try {
			String jsonStr = intent.getExtras().getString(GeneralData.RESULT, "-1");
			if(jsonStr != "1-"){
				
				JSONObject obj = new JSONObject(jsonStr);
				JSONObject semantic = obj.getJSONObject("semantic");
				if(semantic != null){
					JSONObject solts = semantic.getJSONObject("slots");
					
					if(solts != null){
						String display = solts.getString("display");
						if(display.equals("display_text")){
							String text = context.getResources().getString(R.string.show_voice_text);
							
							SharePreferenceUtils.getInstance(context).putInt("showtext", SHOW_TEXT);
							ShowToast.getInstance(context).show(text);
								
						}else {
							//mainServiceInfo.setShowText(false);
							SharePreferenceUtils.getInstance(context).putInt("showtext", CLOST_TEXT);
						}
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
