package com.yongyida.robot.voice.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;

import com.yongyida.robot.voice.bean.RobotInfo;
import com.yongyida.robot.voice.frame.ParseFactory;
import com.yongyida.robot.voice.utils.LogUtils;

public abstract class BaseCmd {

	private static final String TAG = "BaseCmd";
	private static String systemPlat;
	protected static JSONObject json;
	protected Intent intent;
	protected ParseFactory factory = new ParseFactory();
	protected RobotInfo robot;
	protected static Context context;
	protected static Map<String, Class<? extends BaseCmd>> messageMap;
	
	protected static void register(String key, Class<? extends BaseCmd> clazz){
		if(messageMap == null){
			messageMap = new HashMap<>();
		}
		messageMap.put(key, clazz);
	}
	
	public static BaseCmd getCmd(String type){
		if(messageMap == null) return null;
		
		Class<? extends BaseCmd> C = messageMap.get(type);
		try {
			if(C != null){
				return C.newInstance();
			}
			
		} catch (Throwable e) {
			LogUtils.showLogInfo(TAG, TAG + " : getCmd : " + e);
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	public abstract void execute();
	
	public Context getContext() {
		return context;
	}
	
	@SuppressWarnings("static-access")
	public void setContext(Context context) {
		this.context = context;
	}
	
	public JSONObject getJson() {
		return json;
	}
	
	@SuppressWarnings("static-access")
	public void setJson(JSONObject json){
		this.json = json;
	}
	public Intent getIntent() {
		return intent;
	}
	public void setIntent(Intent intent) {
		this.intent = intent;
	}
	
	public String getSystemPlat()
	{
		if(systemPlat == null)
		{
			systemPlat = SystemProperties.get("ro.wisky.modelnumber", "Y50B Pro");	// get model number
		}
		return systemPlat;
	}
}
