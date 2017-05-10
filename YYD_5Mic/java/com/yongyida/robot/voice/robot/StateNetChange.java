package com.yongyida.robot.voice.robot;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

import com.yongyida.robot.voice.base.BaseMessage;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.data.RobotStateData;
import com.yongyida.robot.voice.data.VoiceData;
import com.yongyida.robot.voice.frame.iflytek.CommVoiceParse;
import com.yongyida.robot.voice.frame.iflytek.VoiceUnderstand;
import com.yongyida.robot.voice.utils.LogUtils;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * 网络切换
 * 
 * @author Administrator
 *
 */
public class StateNetChange extends BaseMessage{
    	
	@Override
	public void execute() {

		if(context == null) return;
		  ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		  NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
	        if (activeNetwork == null){
    	    LogUtils.showLogInfo("success", "no net");
    	    mainServiceInfo.setNetConnect(RobotStateData.STATE_NET_UNCONNECT); 
    	    mainServiceInfo.setLoginFlash(RobotStateData.STATE_LOGIN_VOICE_PLAY);
		 
		    CommVoiceParse voiceUnderstand = VoiceUnderstand.getInstance(context);
            voiceUnderstand.stop();
            Map<String, String> voiceUnderstandStatus;
            voiceUnderstandStatus = new HashMap<String, String>();
            voiceUnderstandStatus.put(GeneralData.ACTION, IntentData.INTENT_VOICE_UNDERSTAND_STATUS);
            voiceUnderstandStatus.put(GeneralData.STATUS, "end");
            SendBroadcastUtils.sendBroad(context, voiceUnderstandStatus);				 
			 
            if(mPlayer.isPlaying()) return;
			 mPlayer.playMusic(context, VoiceData.ERROR_NETWORK);
	        }else if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
	        	LogUtils.showLogInfo("success", "wifi net");
		    	 mainServiceInfo.setNetConnect(RobotStateData.STATE_NET_CONNECT);
	        }else if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
	        	 LogUtils.showLogInfo("success", "mobile net");
		    	 mainServiceInfo.setNetConnect(RobotStateData.STATE_NET_CONNECT);
	        }
		    
	}

}

