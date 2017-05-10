package com.yongyida.robot.voice.base;

import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.yongyida.robot.voice.bean.MainServiceInfo;
import com.yongyida.robot.voice.bean.RobotInfo;
import com.yongyida.robot.voice.utils.MediaPlayUtils;
import com.yongyida.robot.voice.utils.ThreadExecutorUtils;

public abstract class BaseMessage extends BaseCmd{
	
	protected RobotInfo robot = RobotInfo.getInstance();
	
	protected Random random = new Random();
	
	protected MediaPlayUtils mPlayer = MediaPlayUtils.getInstance();
	
	protected ScheduledThreadPoolExecutor executor = ThreadExecutorUtils.getExceutor();
	
	protected MainServiceInfo mainServiceInfo = MainServiceInfo.getInstance();
	
}
