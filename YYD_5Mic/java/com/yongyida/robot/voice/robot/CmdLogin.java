package com.yongyida.robot.voice.robot;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.Settings;

import com.yongyida.robot.voice.base.BaseCmd;
import com.yongyida.robot.voice.bean.MainServiceInfo;
import com.yongyida.robot.voice.bean.RobotInfo;
import com.yongyida.robot.voice.data.RobotStateData;
import com.yongyida.robot.voice.data.VoiceData;
import com.yongyida.robot.voice.frame.http.Achieve;
import com.yongyida.robot.voice.frame.iflytek.CommVoiceParse;
import com.yongyida.robot.voice.frame.iflytek.VoiceRecognizer;
import com.yongyida.robot.voice.utils.MediaPlayUtils;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;
import com.yongyida.robot.voice.utils.SharePreferenceUtils;

/**
 * 机器人登录
 * 
 * @author Administrator
 *
 */
public class CmdLogin extends BaseCmd {

	SharePreferenceUtils sp = SharePreferenceUtils.getInstance(context);
	@Override
	public void execute() {
		try {
			robot = RobotInfo.getInstance();
			String ret = json.optString("ret");
			ret = ret.trim();
			if (!ret.equals(RobotStateData.STATE_LOGIN_SUCCESS)) {
				robot.setOnline(RobotStateData.STATE_LOGIN_FAIL);
				return;
			}
			
			Achieve achieva = new Achieve(context);
			String id = sp.getString("id", "123456");
			String serial = sp.getString("serial", "123456");
			achieva.synchronizeData(id,  serial);
			
			robot.setOnline(RobotStateData.STATE_LOGIN_SUCCESS);
			if(MainServiceInfo.getInstance().getLoginFlash().equals(RobotStateData.STATE_LOGIN_VOICE_PLAY)){

				uploadLexicon();

				MainServiceInfo.getInstance().setLoginFlash(RobotStateData.STATE_LOGIN_VOICE_STOP_PLAY);
				MediaPlayUtils.getInstance().playMusic(context, VoiceData.NET_CONNECT);
			}
			
			String str = json.optString("Robot");
			str = str.trim();
			if(str.equals("")) return;
			JSONObject obj = new JSONObject(str);
			String name = obj.optString("rname");
			String rid = obj.optString("rid");
			robot.setRid(obj.optString("rid"));
			if(!sp.getString("rid", "").equals(rid)){
				sp.putString("rid", obj.optString("rid"));
			}
			if(name.equals("")) return;		
			robot.setName(name);
			Uri uriName = Uri.parse("content://com.yongyida.robot.nameprovider//name");
			ContentResolver resolver = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("name", name);
			resolver.update(uriName, values, null, null);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("action", "com.settings.recevier");
			map.put("rid",rid );
			SendBroadcastUtils.sendBroad(context, map);
			

		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	private void uploadLexicon() {
		CommVoiceParse voiceRecognizer = VoiceRecognizer.getInstance(context);
		voiceRecognizer.start();
	}
}
