package com.yongyida.robot.voice.robot;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import android.util.Log;

import com.yongyida.robot.voice.base.BasePushCmd;
import com.yongyida.robot.voice.bean.MainServiceInfo;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.data.RobotStateData;
import com.yongyida.robot.voice.frame.socket.serverscoket.SocketHandler;
import com.yongyida.robot.voice.utils.LogUtils;
import com.yongyida.robot.voice.utils.ThreadExecutorUtils;
/**
 * 查询提醒
 * 
 * @author Administrator
 *
 */
public class PushRemindQuery extends BasePushCmd {
	private ScheduledThreadPoolExecutor executor = ThreadExecutorUtils.getExceutor();
	private MainServiceInfo mainServiceInfo = MainServiceInfo.getInstance();
	private String jsonStr;
	private Timer timer = new Timer();

	@Override
	public void execute() {

		jsonStr = json.optString("command");
		if ("".equals(jsonStr)) {
			return;
		}
		if (executor == null) {
			return;
		}
		if(mainServiceInfo.getSendRemand().equals(RobotStateData.STATE_UNSEND_DATA)){
			return;
		}
		executor.schedule(new Runnable() {

			@Override
			public void run() {
				try {
					obJson = new JSONObject(jsonStr);
					mainServiceInfo.setRemind(false);
					mainServiceInfo.setSendRemand(RobotStateData.STATE_UNSEND_DATA);
					sendBroadcast(IntentData.NOTIFICATION_QUERY, obJson);
					timer.schedule(new TimerTask() {
						
						@Override
						public void run() {
							Map<String, String> map = new HashMap<String, String>();
							LogUtils.showLogInfo("success", "remind data is null");
							map.put("cmd", "/robot/callback");
							map.put("command", "get data is null");
							mainServiceInfo.setRemindMap(map);
							mainServiceInfo.setRemind(true);
								
						}
					}, 2000);
					
					while (!mainServiceInfo.getRemind());
					timer.cancel();
					mainServiceInfo.setSendRemand(RobotStateData.STATE_SEND_DATA);
					if (SocketHandler.channel != null) {
						Map<String, String> remindMap = mainServiceInfo.getRemindMap();
						Log.i("success", "PushRemindQuery");
						SocketHandler.channel.write(remindMap);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 0, TimeUnit.SECONDS);

	}

}