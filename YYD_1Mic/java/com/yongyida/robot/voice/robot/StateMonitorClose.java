package com.yongyida.robot.voice.robot;

import com.yongyida.robot.voice.base.BaseMessage;

/**
 * 关闭监控.
 * 
 * @author Administrator
 *
 */
public class StateMonitorClose extends BaseMessage{

	@Override
	public void execute() {
		mainServiceInfo.setOpenVideo("close");
	}

}
