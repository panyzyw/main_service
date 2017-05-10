package com.yongyida.robot.voice.robot;

import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import android.util.Log;

import com.yongyida.robot.voice.R;
import com.yongyida.robot.voice.base.BaseCmd;
import com.yongyida.robot.voice.dao.DatabaseOpera;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.UrlData;
import com.yongyida.robot.voice.service.SplashService;
import com.yongyida.robot.voice.utils.SharePreferenceUtils;
import com.yongyida.robot.voice.utils.ShowToast;
import com.yongyida.robot.voice.utils.ThreadExecutorUtils;
/**
 * 机器人版本切换.
 * 
 * @author Administrator
 *
 */
public class SwitchVersion extends BaseCmd{

	private DatabaseOpera db;
	
	public static final int Y50B_DEBUG_VERSION = 1;
	
	public static final int Y50B_FORMAL_VERSION = 2;
	
	public static final int Y50B_HK_VERSION = 3;
	
	public static final int Y50B_DEV_VERSION = 4;
	
	
	private static int version;
	
	@Override
	public void execute() {

		try {
		
			String text;
			String jsonStr = intent.getExtras().getString(GeneralData.RESULT, "-1");
			Log.d("jlog", "str:" + jsonStr);
			if(jsonStr != "1-"){
				
				JSONObject obj = new JSONObject(jsonStr);
				JSONObject semantic = obj.getJSONObject("semantic");
				if(semantic != null){
					JSONObject solts = semantic.getJSONObject("slots");
					
					if(solts != null){
						String switchVersion = solts.getString("switch");
						if(switchVersion.equals("debug_version")){
							
							version = Y50B_DEBUG_VERSION;
							SharePreferenceUtils.getInstance(context).putInt("version", Y50B_DEBUG_VERSION);
							text = context.getResources().getString(R.string.debug_version_text);
							
						}else {
							version = Y50B_FORMAL_VERSION;
							SharePreferenceUtils.getInstance(context).putInt("version", Y50B_FORMAL_VERSION);
							text = context.getResources().getString(R.string.formal_version_text);
						}
						
						UrlData.TCP_IP = null;
						db = new DatabaseOpera(context);
						db.update(version);
						Log.d("jlog", "v:" + version);
						ShowToast.getInstance(context).show(text + version);
						
						//Toast.makeText(context, , 0).show();
						ThreadExecutorUtils.getExceutor().schedule(new Runnable() {
							
							@Override
							public void run() {
								
								SplashService.actionStop(context);
								
							}
						}, 500, TimeUnit.MILLISECONDS);
						
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}

}
