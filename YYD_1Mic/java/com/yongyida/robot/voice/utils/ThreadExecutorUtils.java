package com.yongyida.robot.voice.utils;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ThreadExecutorUtils {
	
	private static ScheduledThreadPoolExecutor exceutor;

	public static ScheduledThreadPoolExecutor getExceutor(){
		
		if(exceutor == null){
			synchronized(ThreadExecutorUtils.class){
			
				if(exceutor == null){
					exceutor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
				}
			}
		}
		return exceutor;
	}
	
}
