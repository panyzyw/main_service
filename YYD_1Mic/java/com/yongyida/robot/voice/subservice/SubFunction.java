package com.yongyida.robot.voice.subservice;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.yongyida.robot.voice.bean.MainServiceInfo;
import com.yongyida.robot.voice.utils.LogUtils;

public class SubFunction implements SubFunctionInterface{

	private static final String TAG = "SubFunction";
	
	protected static Map<String, Class<? extends SubFunction>> subContext = new HashMap<String, Class<? extends SubFunction>>();
	
	protected Context context = MainServiceInfo.getInstance().getContext();
	
	protected String json;

	public void run() {}
	
	public static SubFunction getFunctions(String subType){
		
		try {
			Class<? extends SubFunction> C = subContext.get(subType);
			
			if(C != null){
				return C.newInstance();
				
			}
		} catch (Throwable e) {
			LogUtils.showLogError(TAG, TAG + " : getFunctions : " + e);
		}
		return null;
	}

	
	@Override
	public void stop() { }
	
	
	
	public void setJson(String json) {
		this.json = json;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	
	
	
}
