package com.yongyida.robot.voice.robot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.yongyida.robot.voice.base.BaseCmd;
import com.yongyida.robot.voice.frame.socket.serverscoket.SocketHandler;
import com.yongyida.robot.voice.utils.ThreadExecutorUtils;
/**
 * 查询用户信息.
 * 
 * @author Administrator
 *
 */
public class CmdQueryUserMessage extends BaseCmd{

	@Override
	public void execute() {
		ScheduledExecutorService executor = ThreadExecutorUtils.getExceutor();
		if(executor != null){
			executor.schedule(new Runnable() {
				
				@Override
				public void run() {
					
					if(SocketHandler.channel != null){
						Map<String, String> map = new HashMap<String, String>();
						map.put("cmd", "/robot/bind/list");
						
						SocketHandler.channel.write(map);
					}
				}
			}, 0, TimeUnit.SECONDS);
		}
		
	}

}
