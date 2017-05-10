package com.yongyida.robot.voice.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtils {
	//private SharedPreferences sp;
	private final static String NAME = "share_data";
	Context context;
	private static SharePreferenceUtils spUtil;
	private SharePreferenceUtils(Context context) {
		this.context = context;
	}
	
	public static SharePreferenceUtils getInstance(Context context){
		
		if(spUtil == null){
			spUtil = new SharePreferenceUtils(context);
		}
		return spUtil;
	}

	public void putString(String key, String value) {

		if(key == null || value == null){
			return;
		}
		key = key.trim();
		value = value.trim();
		if(key.equals("") || value.equals("")){
			return;
		}
		
		SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
			
		
	}

	public void putInt(String key, int value) {

		if(key == null){
			return;
		}
		key = key.trim();
		if(key.equals("")){
			return;
		}
		
		SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
		
	}
	

	public void putBoolean(String key, boolean value) {

		if(key == null){
			return;
		}
		key = key.trim();
		if(key.equals("")){
			return;
		}
		
		SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
		
	}

	

	public String getString(String key, String defValue) {
		
		if(key == null || defValue == null){
			return "";
		}
		key = key.trim();
		defValue = defValue.trim();
		if(key.equals("") || defValue.equals("")){
			return "";
		}
		
		SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		return sp.getString(key, defValue);
	}

	public int getInt(String key, int defValue) {
		
		if(key == null){
			return -1;
		}
		key = key.trim();
		if(key.equals("")){
			return -1;
		}
		
		SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		return sp.getInt(key, defValue);
	}
	
	public boolean getBoolean(String key, boolean defValue) {
		
		if(key == null){
			return false;
		}
		key = key.trim();
		if(key.equals("")){
			return false;
		}
		
		SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		return sp.getBoolean(key, defValue);
	}

}