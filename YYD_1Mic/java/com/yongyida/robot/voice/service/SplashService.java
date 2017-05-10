package com.yongyida.robot.voice.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.telephony.TelephonyManager;

import com.yongyida.robot.voice.base.BaseCmd;
import com.yongyida.robot.voice.base.BaseService;
import com.yongyida.robot.voice.bean.MainServiceInfo;
import com.yongyida.robot.voice.bean.RobotInfo;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.data.UrlData;
import com.yongyida.robot.voice.frame.ParseFactory;
import com.yongyida.robot.voice.frame.iflytek.VoiceWakeUp;
import com.yongyida.robot.voice.frame.socket.localscket.LocalServer;
import com.yongyida.robot.voice.frame.socket.serverscoket.SocketConnect;
import com.yongyida.robot.voice.observer.NameObserver;
import com.yongyida.robot.voice.robot.CmdRobot;
import com.yongyida.robot.voice.utils.LogUtils;
import com.yongyida.robot.voice.utils.NetUtils;
import com.yongyida.robot.voice.utils.ThreadExecutorUtils;

import java.net.UnknownHostException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SplashService extends BaseService {
	
	//private AudioManager mAudioManager = null; 
	
	private SocketConnect connect;
	
	private String tcpHost; 
	
	private BroadcastReceiver receiver;
	
	private int tcpPost;
	
	@SuppressWarnings("static-access")
	public static void actionStart(Context context) {
		Intent intent = new Intent(context, SplashService.class);
		intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
		context.startService(intent);
	}

	public static void actionStop(Context context) {
		Intent intent = new Intent(context, SplashService.class);
		context.stopService(intent);

	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		
		
		LocalServer.startLocalScoket();
		
		/*mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mAudioManager.setBluetoothScoOn(true);
		mAudioManager.startBluetoothSco();
		mAudioManager.setMode(AudioManager.STREAM_MUSIC);
		mAudioManager.setSpeakerphoneOn(true);  */
		MainServiceInfo.getInstance().setMainServiceDestroy(false);
		MainServiceInfo.getInstance().setContext(this);
		getContentResolver().registerContentObserver(Uri.parse("content://com.yongyida.robot.nameprovider//name"),true, new NameObserver(this, new Handler()));
		/*唤醒*/
		ParseFactory factory = new ParseFactory();
		factory.setFactory(VoiceWakeUp.getInstance(this));
		factory.parseStart(null);
	
		robotLogin();
		receiveBroadcast();
		
	}

	@Override
	protected void destroyService() {

		/*MainServiceInfo.getInstance().setMainServiceDestroy(true);
		
		
		MainServiceInfo.getInstance().setLoginFlash(RobotStateData.STATE_LOGIN_VOICE_PLAY);*/
		
		LocalServer.closeScoket();
		//服务销毁，清楚数据
		MainServiceInfo.cleanInfoState();
		RobotInfo.cleanInfoState();
		/*mAudioManager.stopBluetoothSco();
		mAudioManager.setBluetoothScoOn(false);*/
		
		 UrlData.TCP_IP = null;
		if(receiver != null){
			this.unregisterReceiver(receiver);
		}
		if(connect != null){
			connect.socketDestroyConnect();
		}
		
	}
	
	public void receiveBroadcast(){
		IntentFilter filter = new IntentFilter();
		filter.addAction("TouchSensor");
		filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		filter.addAction("com.yongyida.robot.notification.QUERY_RESULT");
		filter.addAction("com.yydrobot.ENTERVIDEO");
		filter.addAction("com.yydrobot.EXITVIDEO");
		filter.addAction("com.yydrobot.ENTERMONITOR");
		filter.addAction("com.yydrobot.EXITMONITOR");
		filter.addAction("com.yongyida.robot.FACTORYSTART");
		filter.addAction("com.yongyida.robot.FACTORYCLOSE");
		filter.addAction("com.yydrobot.qrcode.QUERY");
		filter.addAction("com.yydrobot.qrcode.DELETE");
		filter.addAction(IntentData.INTENT_SWITCH);
		
		filter.addAction(IntentData.INTENT_DISPLAY);
		
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);;
		filter.addAction(IntentData.INTENT_RECYCLE);
		filter.addAction(IntentData.INTENT_VOICE);

		filter.addAction(IntentData.INTENT_WRITE_PCMDATA);

		filter.addAction(IntentData.INTENT_MUTE);
		
		filter.addAction(IntentData.INTENT_STOP_MONITOR);
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(final Context context, Intent intent) {
				if(intent != null){
					BaseCmd cmd = CmdRobot.getCmd(intent.getAction());
					if(cmd != null){
						cmd.setContext(SplashService.this);
						cmd.setIntent(intent);
						cmd.execute();
					}
				}
			}
		};
		
		this.registerReceiver(receiver, filter);
	
	}
	
	public void robotLogin() {

		ScheduledThreadPoolExecutor executor;
		executor = ThreadExecutorUtils.getExceutor();
		tcpHost = NetUtils.getSocketHost(this);
		tcpPost = NetUtils.getSocketPort(this);

		LogUtils.showLogInfo("success", tcpHost + " : " + tcpPost);
		executor.schedule(new Runnable() {

			@Override
			public void run() {

				while (UrlData.TCP_IP == null) {
					if (NetUtils.isConnected(SplashService.this)) {
						
						try {
							java.net.InetAddress address;
							address = java.net.InetAddress.getByName(tcpHost);
							UrlData.TCP_IP = address.getHostAddress();//得到字符串形式的ip地址
						} catch (UnknownHostException e) {
							e.printStackTrace();
						}
					}
				}
				if (tcpPost != -1) {
					connect = SocketConnect.getInstace(SplashService.this,
							UrlData.TCP_IP, tcpPost);
					LogUtils.showLogInfo("success", UrlData.TCP_IP + " :add: " + tcpPost);
					connect.socketConnect(3);
					
				}	
			}
		}, 0, TimeUnit.SECONDS);

	}
}
