package com.yongyida.robot.voice.broadcast;

import java.io.UnsupportedEncodingException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.yongyida.robot.voice.bean.MainServiceInfo;
import com.yongyida.robot.voice.frame.iflytek.VoiceUnderstand;
import com.yongyida.robot.voice.service.SplashService;

/**
 * @author Administrator
 */
public class StartMainServiceReceiver extends BroadcastReceiver {

    private static VoiceUnderstand mVoiceUnderstand = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent == null) {
            return;
        }
        if (intent.getAction().equals("com.yongyida.robot.voice.MAIN")) {
            if (MainServiceInfo.getInstance().getMainServiceDestroy()) {
                SplashService.actionStart(context);
            }
        } else if (intent.getAction().equals("com.yydrobot.AUDIO")) {

            String data = intent.getStringExtra("data");
            try {
                byte[] bytes = data.getBytes("ISO-8859-1");
//				if(mVoiceUnderstand == null)
//				{
//					mVoiceUnderstand = VoiceUnderstand.getInstance(context);
//					Log.d("jlog", "mVoiceUnderstand NULL");
//				}
//				else if(bytes != null)
//					mVoiceUnderstand.writeData(bytes, bytes.length);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
