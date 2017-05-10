package com.yongyida.robot.voice.robot;

import com.yongyida.robot.voice.base.BaseMessage;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.data.VoiceData;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;

import java.util.HashMap;
import java.util.Map;
/**
 * 摸右肩.
 * 
 * @author Administrator
 *
 */
public class TouchRightShoulder extends BaseMessage{

//	private Motor motor;
	private static boolean headMove = true;
	
	@Override
	public void execute() {
		try {
			String[] shouderVoice = VoiceData.shouderVoice;
			Map<String, String> map2 ;
			map2 = new HashMap<String, String>();
			map2.put(GeneralData.ACTION, IntentData.INTENT_STOP);
			map2.put(GeneralData.RESULT, GeneralData.TOUCHRIGHTSHOULDER);
			map2.put(GeneralData.FROM, GeneralData.INTENT_TOUCH_RIGHT_SHOULDER);
			SendBroadcastUtils.sendBroad(context, map2);
			
			
			
			
//			motor = (Motor) context.getSystemService(Service.MOTOR_SERVICE);
//			int lim = -1;
//			if(factory != null){
//				factory.setFactory(VoiceUnderstand.getInstance(context));
//				factory.parseStop();
//			}
//			

//				int randVoice = random.nextInt(shouderVoice.length);
//				int rand = random.nextInt(3);
//				if (mPlayer != null) {
//					mPlayer.playMusic(context, shouderVoice[randVoice]);
//				}

//			
//			if(TouchLeftShoulder.headMove){
//				lim = motor.robot_head_right(0);
//			}else{
//				lim = motor.robot_head_left(0);
//			}
//			if(lim != 2){
//				TouchLeftShoulder.headMove = !TouchLeftShoulder.headMove;
//			}
//			LogUtils.showLogInfo("success", lim + "");			
			/*if (motor != null) {
				switch (rand) {
				case 0:
					lim = motor.robot_head_right(0);
					break;
				case 1:
					lim = motor.robot_head_left(0);
					break;
				case 2:
					if(headMove){
						headMove = !headMove;
						motor.robot_head_up(0);
					}else{
						headMove = !headMove;
						motor.robot_head_down(0);
					}
					break;
				}
				
				if(lim == 2){
					motor.robot_head_right(0);
				}else if(lim == 3){
					motor.robot_head_left(0);
				}
			}*/
//			if (executor != null) {
//				executor.schedule(new Runnable() {
//
//					@Override
//					public void run() {
//						motor.robot_h_stop();
//					}
//				}, 250, TimeUnit.MILLISECONDS);
//			}
			
		} catch (Throwable e) {
			e.printStackTrace();
		}		
	}

}
