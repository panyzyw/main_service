package com.yongyida.robot.voice.robot;

import com.yongyida.robot.voice.base.BaseCmd;
import com.yongyida.robot.voice.frame.iflytek.VoiceRead;

/**
 * 删除用户信息状态（是否被控制）
 * 
 * @author Administrator
 *
 */
public class CmdDeleteUserMessageResult extends BaseCmd{

	@Override
	public void execute() {

		if(json == null){
			return;
		}
		
		if(json.optString("ret", "-1").equals("-1")){
			factory.setFactory(VoiceRead.getInstence(context));
			factory.parseStart("被控制状态不能删除用户信息");
		}
		
		
	}

}
