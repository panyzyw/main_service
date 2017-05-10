package com.yongyida.robot.voice.robot;

import com.yongyida.robot.voice.base.BaseMessage;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.data.VoiceData;
import com.yongyida.robot.voice.frame.iflytek.VoiceUnderstand;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;

import java.util.HashMap;
import java.util.Map;
/**
 * 摸下巴.
 * 
 * @author Administrator
 *
 */
public class TouchChin extends BaseMessage{
//	private Motor motor;
	private static boolean headMove = true;
	
	@Override
	public void execute() {
		try {
			String[] chinVoice = VoiceData.chinVoice;
			Map<String, String> map2 ;
			map2 = new HashMap<String, String>();
			map2.put(GeneralData.ACTION, IntentData.INTENT_STOP);
			map2.put(GeneralData.RESULT, GeneralData.TOUCHCHIN);
			map2.put(GeneralData.FROM, GeneralData.INTENT_TOUCH_CHIN);
			SendBroadcastUtils.sendBroad(context, map2);
//			motor = (Motor) context.getSystemService(Service.MOTOR_SERVICE);
			//mUnderstand.stopSoundRecording();
			if(factory != null){
				factory.setFactory(VoiceUnderstand.getInstance(context));
				factory.parseStop();
			}
			
//				int randVoice = random.nextInt(chinVoice.length);
//				if (mPlayer != null) {
//					mPlayer.playMusic(context, chinVoice[randVoice]);
//				}
			
//			if(headMove){
//				headMove = !headMove;
//				motor.robot_head_up(0);
//			}else{
//				headMove = !headMove;
//				motor.robot_head_down(0);
//			}
		
		} catch (Throwable e) {
			e.printStackTrace();
		}

		
	}

}
