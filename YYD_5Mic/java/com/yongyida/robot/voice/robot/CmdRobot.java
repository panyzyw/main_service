package com.yongyida.robot.voice.robot;

import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;

import com.yongyida.robot.voice.base.BaseCmd;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.data.MediaData;

public class CmdRobot extends BaseCmd {
	
	public static void cmdRegiser(){
		register(GeneralData.ROBOT_CONTROLL, CmdContrall.class);
		register(GeneralData.ROBOT_CHANGENAME, CmdFlush.class);
		register(GeneralData.ROBOT_LOGIN, CmdLogin.class);
		register(GeneralData.ROBOT_UNCONTROLL, CmdUnContrall.class);
		register(GeneralData.ROBOT_PUSH, CmdPush.class);
		register(MediaData.MEDIA_MOVE, PushMove.class);
		register(MediaData.MEDIA_TEXT, PushText.class);
		register(MediaData.MEDIA_REMIND_INSERT, PushRemindInsert.class);
		register(MediaData.MEDIA_REMIND_UPDATA, PushRemindUpdate.class);
		register(MediaData.MEDIA_REMIND_QUERY, PushRemindQuery.class);
		register(MediaData.MEDIA_REMIND_DELETE, PushRemindDelete.class);
		register(MediaData.MEDIA_QUERY_PHOTO, PushPhotoQurey.class);
		register(IntentData.INTENT_FACTORYCLOSE, StateFactoryClose.class);
		register(IntentData.INTENT_FACTORYCSTART, StateFactoryOpen.class);
		register(IntentData.INTENT_CLOSE_MONITOR, StateMonitorClose.class);
		register(IntentData.INTENT_OPEN_MONITOR, StateMonitorOpen.class);
		register(TelephonyManager.ACTION_PHONE_STATE_CHANGED, StatePhoneChange.class);
		register(IntentData.INTENT_QUERY_RESULT, StateRemindResult.class);
		register(IntentData.INTENT_CLOSE_VIDEO, StateVideoClose.class);
		register(IntentData.INTENT_OPEN_VIDEO, StateVideoOpen.class);
		register(GeneralData.INTENT_TOUCH_CHIN, TouchChin.class);  
		register(GeneralData.INTENT_TOUCH_HEAD, TouchHead.class);
		register(GeneralData.INTENT_TOUCH_LEFT_SHOULDER, TouchLeftShoulder.class);
		register(GeneralData.INTENT_TOUCH_RIGHT_SHOULDER, TouchRightShoulder.class);
		register(IntentData.INTENT_TOUCH_SENSOR, TouchSensor.class);
		
		register(IntentData.INTENT_USER_MESSAGE_DELETE, CmdDeleteUserMessage.class);
		register(GeneralData.INTENT_USER_MESSAGE_DELETE_RESULT, CmdDeleteUserMessageResult.class);
		register(IntentData.INTENT_USER_MESSAGE_QUERY, CmdQueryUserMessage.class);
		register(GeneralData.INTENT_USER_MESSAGE_RESULT, CmdQueryUserMessageResult.class);
		
		register(IntentData.INTENT_SWITCH, SwitchVersion.class);
		
		register(IntentData.INTENT_RECYCLE, StateRecycle.class);
		
		register(IntentData.INTENT_DISPLAY, ShowVoiceText.class);
		
		register(IntentData.INTENT_TOUCH_DANCE, StateDance.class);
		register(IntentData.INTENT_VOICE, VoiceSetting.class);
		register(IntentData.INTENT_WRITE_PCMDATA,WritePcmData.class);
		register("/media/callback", RobotClientCallback.class);
		
		register(ConnectivityManager.CONNECTIVITY_ACTION,StateNetChange.class);
		register(IntentData.INTENT_MUTE, StateRecycleMute.class);
		
	}
	
	@Override
	public void execute() {
		
		
	}

}
