package com.yongyida.robot.voice.utils;

import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.Intent;

import com.yongyida.robot.voice.data.GeneralData;

public class SendBroadcastUtils {
	
	
	public static void sendBroad(Context context, Map<String, String> map){
		
		try {
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
			for (Entry<String, String> entry : map.entrySet()) {  
			  
				if(entry.getKey().equals(GeneralData.ACTION)){
					intent.setAction(entry.getValue());
					continue;
				}
				intent.putExtra(entry.getKey(), entry.getValue());
				
			} 
			context.sendBroadcast(intent);
			
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}
	

}
