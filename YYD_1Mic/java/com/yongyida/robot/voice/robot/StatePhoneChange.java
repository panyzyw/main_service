package com.yongyida.robot.voice.robot;

import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.yongyida.robot.voice.base.BaseMessage;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;
/**
 * 电话状态改变.
 * @author Administrator
 *
 */
public class StatePhoneChange extends BaseMessage {

	private static boolean isRepeat = false;
	
	@Override
	public void execute() {
		try {
			PhoneStateListener listener = new PhoneStateListener() {

				@Override
				public void onCallStateChanged(int state, String incomingNumber) {
					super.onCallStateChanged(state, incomingNumber);

					switch (state) {
					case TelephonyManager.CALL_STATE_IDLE:// 电话挂断状态
						mainServiceInfo.setCall(false);
						isRepeat = false;
						break;
					case TelephonyManager.CALL_STATE_OFFHOOK:// 电话接听状态
						if (!isRepeat) {
							isRepeat = true;
							Map<String, String> map2 ;
							map2 = new HashMap<String, String>();
							map2.put(GeneralData.ACTION, IntentData.INTENT_STOP);
							map2.put(GeneralData.RESULT, GeneralData.RINGUP);
							SendBroadcastUtils.sendBroad(context, map2);
							//SendBroadcastBiz.sendBroad(context, IntentData.INTENT_STOP, GeneralData.RINGUP);
						}
						mainServiceInfo.setCall(true);
						break;
					case TelephonyManager.CALL_STATE_RINGING:// 电话铃响状态
						mainServiceInfo.setCall(true);
						if (!isRepeat) {
							isRepeat = true;
							
							Map<String, String> map2 ;
							map2 = new HashMap<String, String>();
							map2.put(GeneralData.ACTION, IntentData.INTENT_STOP);
							map2.put(GeneralData.RESULT, GeneralData.RINGUP);
							SendBroadcastUtils.sendBroad(context, map2);
							
							//SendBroadcastBiz.sendBroad(context, IntentData.INTENT_STOP, GeneralData.RINGUP);
						}
						
						break;
					}
				}
			};

			TelephonyManager phoneManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
			phoneManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}

}
