package com.yongyida.robot.voice.frame.socket.localscket;

import java.util.Map.Entry;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.util.Log;

import com.yongyida.robot.voice.bean.MainServiceInfo;
/**
 * 本地scoket类.
 * 
 * @author Administrator
 *
 */
public class LocalServer extends SocketChannel implements Runnable{

	private LocalServerSocket scoket = null;
	
	@Override
	public void run() {
		
		try {
			scoket = new LocalServerSocket(SCOKETNAME);
			
			while (!MainServiceInfo.getInstance().getMainServiceDestroy()){
				Log.d("jlog", "run");
				channel =  scoket.accept();
				Log.d("jlog", "accept");
				revData();
			}
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 开启本地scoket
	 */
	public static void startLocalScoket(){
		new Thread(new LocalServer()).start();
	}


	public static void closeScoket() {
		try {
			for(Entry<String, LocalSocket> entry : channelMap.entrySet()){
				if(entry.getValue() != null){
					entry.getValue().close();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	

}
