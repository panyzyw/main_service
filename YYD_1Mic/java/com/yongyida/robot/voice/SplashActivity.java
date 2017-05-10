package com.yongyida.robot.voice;




import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.yongyida.robot.voice.service.SplashService;
import com.yongyida.robot.voice.utils.ThreadExecutorUtils;


/**
 * 启动页
 * 
 * @author Administrator
 */
public class SplashActivity extends Activity {
	
	private Button mBtnRecode;

	@SuppressWarnings("static-access")
	public static void actionStart(Context context){
	    	Intent intent = new Intent(context, SplashActivity.class);
	    	intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
	    	context.startService(intent);
	    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		initView();
		
	}
		

	
	public void initView() {
		
		SplashService.actionStart(SplashActivity.this);
		
		mBtnRecode = (Button) findViewById(R.id.btn_recoding);
		
		mBtnRecode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				/*Intent intent = new Intent("TouchSensor");
				intent.putExtra("android.intent.extra.Touch","t_head");
		    	sendBroadcast(intent);*/
				
				Intent intent = new Intent("com.yydrobot.qrcode.QUERY");      //"com.yydrobot.qrcode.DELETE"
				//intent.putExtra("android.intent.extra.Touch","t_head");
		    	sendBroadcast(intent);
				
			}
		});
		
		ThreadExecutorUtils.getExceutor().schedule(new Runnable() {
			
			@Override
			public void run() {
				
				SplashActivity.this.finish();
			}
		}, 500, TimeUnit.MILLISECONDS);
	}

}
