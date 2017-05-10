package com.yongyida.robot.voice.base;

import org.json.JSONObject;

import android.content.Intent;

public abstract class BasePushCmd extends BaseCmd{

	protected JSONObject obJson;
	
	public void sendBroadcast(String action, JSONObject json) {
		try {
			Intent intent = new Intent();
			intent.setAction(action);
			intent.putExtra("id", json.getString("id"));
			intent.putExtra("time", json.getString("time"));
			intent.putExtra("title", json.getString("title"));
			intent.putExtra("content", json.getString("content"));
			intent.putExtra("seq", json.getString("seq"));
			context.sendBroadcast(intent);
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
