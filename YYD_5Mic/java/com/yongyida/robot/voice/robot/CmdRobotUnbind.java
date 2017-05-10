package com.yongyida.robot.voice.robot;

import com.yongyida.robot.voice.base.BaseCmd;
/**
 * 机器人解除绑定
 * @author Administrator
 *
 */
public class CmdRobotUnbind extends BaseCmd{

	@Override
	public void execute() {

		if(json == null){
			return;
		}
		
		if(json.optString("ret", "-2").equals("-1")){
			
		}
		
		
	}

}
