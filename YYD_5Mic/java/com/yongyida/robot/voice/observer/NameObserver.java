package com.yongyida.robot.voice.observer;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.yongyida.robot.voice.bean.RobotInfo;
import com.yongyida.robot.voice.data.RobotStateData;
import com.yongyida.robot.voice.frame.socket.serverscoket.SocketHandler;

/**
 * 修改机器人名字类.
 * 
 * @author Administrator
 *
 */
public class NameObserver extends ContentObserver {
	//private final int ONLINE_SUCCESS = 0;
	private Context context;
	private RobotInfo robot;
	public NameObserver(Context context, Handler handler){
		super(handler);
		robot = RobotInfo.getInstance();
		this.context = context;
		
	}
	
	public NameObserver(Handler handler) {
		super(handler);
		robot = RobotInfo.getInstance();
	}
	
	@Override
	public void onChange(boolean selfChange) {
		try {
			Uri uri = Uri.parse("content://com.yongyida.robot.nameprovider//name");
			ContentResolver resolver = context.getContentResolver();
			Cursor cursor = resolver.query(uri, null, null, null, null);
			 if(cursor.moveToFirst()){
			    String name = cursor.getString(cursor.getColumnIndex("name"));//id
			    if(name != null){
			    	name = name.trim();
			    	if(!name.equals(robot.getName())){
			    		robot.setName(name);
			    		if(SocketHandler.channel != null && robot.getOnline().equals(RobotStateData.STATE_LOGIN_SUCCESS)){
				    		Map<String, String> map = new HashMap<String, String>();
				    		map.put("cmd", "/robot/flush");
				    		map.put("rname", name);
				    		SocketHandler.channel.write(map);
				    	}
			    	}
			    	
			    }
			    cursor.close();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} 
	
	}
	

}
