package com.yongyida.robot.voice.robot;

import com.yongyida.robot.voice.base.BaseMessage;
/**
 * 视频关闭.
 * @author Administrator
 *
 */
public class StateVideoClose extends BaseMessage {

	@Override
	public void execute() {
		mainServiceInfo.setOpenVideo("close");
		mainServiceInfo.setVideo(false);
	}

}
