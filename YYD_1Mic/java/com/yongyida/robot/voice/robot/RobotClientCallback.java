package com.yongyida.robot.voice.robot;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import com.yongyida.robot.voice.base.BaseCmd;
import com.yongyida.robot.voice.frame.socket.localscket.SocketChannel;
import com.yongyida.robot.voice.utils.LogUtils;
import com.yongyida.robot.voice.utils.ThreadExecutorUtils;
/**
 *	将服务器返回的数据转发给视频.
 *
 * @author Administrator
 *
 */
public class RobotClientCallback extends BaseCmd {

	/*视频登录*/
	private static final String CLIENT_ID = "video";
	SocketChannel channel = new SocketChannel();
	@Override
	public void execute() {
		
		Log.d("success","RobotClientCallback : ");
		ThreadExecutorUtils.getExceutor().schedule(new Runnable() {
			
			@Override
			public void run() {
				String command = json.optString("command", null);
				channel.setData(command);
				channel.setId(CLIENT_ID);
				channel.sendData();
				
			}
		}, 0, TimeUnit.SECONDS);
		
	}
	
}
