package com.yongyida.robot.voice.subservice.subimpl;

import org.json.JSONException;
import org.json.JSONObject;

import com.yongyida.robot.voice.frame.http.Achieve;
import com.yongyida.robot.voice.frame.socket.localscket.SocketChannel;
import com.yongyida.robot.voice.subservice.SubFunction;
import com.yongyida.robot.voice.utils.SharePreferenceUtils;
/**
 * 机器人添加好友
 * 
 * @author Administrator
 *
 */
public class SubFriends extends SubFunction {
	String add = "";
	@Override
	public void run() {
		try {
			
			if(context == null) return;
			if(json == null) return;
			
			SharePreferenceUtils sp = SharePreferenceUtils.getInstance(context);
			
			JSONObject ob = new JSONObject(json);
			String type = ob.optString("type", "default");
			Achieve achieve = new Achieve(context);
			
			if (type.equals("Robot")) {
				add = achieve.addRobotFriend(sp.getString("id", "123456"), sp.getString("serial", "123456"), ob.getString("number"));
				
				
			} else if (type.equals("Phone")) {
				add = achieve.addPhoneFriend(
						sp.getString("id", "123456"),
						sp.getString("serial", "123456"),
						ob.getString("number"));
			}
			
			if(add.equals("")){
			//添加success
				SocketChannel channel = new SocketChannel();
				channel.setData("{.....}");
				channel.setId("video");
				channel.sendData();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
