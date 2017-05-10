package com.yongyida.robot.voice.robot;

import java.util.HashMap;
import java.util.Map;

import com.yongyida.robot.voice.base.BaseCmd;
import com.yongyida.robot.voice.frame.socket.serverscoket.SocketHandler;
import com.yongyida.robot.voice.utils.SharePreferenceUtils;

/**
 * 删除用户信息类
 * 
 * @author Administrator
 *
 */
public class CmdDeleteUserMessage extends BaseCmd{

	@Override
	public void execute() {
	
		if(SocketHandler.channel == null){
			return;
		}
		if(intent == null){
			return;
		}
		String id = intent.getStringExtra("id");//"100069"
		String robot_id = SharePreferenceUtils.getInstance(context).getString("id", "12356");
		if(id == null){
			return;
		}
		if(robot_id == null){
			return;
		}
		Map<String,String> map = new HashMap<String, String>();
		map.put("cmd", "/robot/unbind");
		map.put("id", id);
		map.put("robot_id", robot_id);
		SocketHandler.channel.write(map);
		
	}

}
