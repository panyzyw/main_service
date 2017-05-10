package com.yongyida.robot.voice.bean;

import java.util.HashMap;
import java.util.Map;

import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.data.MediaData;

public class BroadcastInfo {
	
	private Map<String, String> mBrcMap;

	private static BroadcastInfo mBroadcastInfo; 
	
	public BroadcastInfo() {

		mBrcMap = new HashMap<String, String>();
		/*音乐*/
		mBrcMap.put(MediaData.MEDIA_MUSIC, IntentData.INTENT_MUSIC);
		/*天气*/
		mBrcMap.put(MediaData.MEDIA_WEATHER, IntentData.INTENT_WEATHER);
		/*股票*/
		mBrcMap.put(MediaData.MEDIA_STOCK, IntentData.INTENT_STOCK);
		/*新闻*/
		mBrcMap.put(MediaData.MEDIA_NEWS, IntentData.INTENT_NEWS);
		/*算术*/
		mBrcMap.put(MediaData.MEDIA_ARITHMETIC, IntentData.INTENT_ARITHMETIC);
		/*百科*/
		mBrcMap.put(MediaData.MEDIA_ENCYCLOPEDIAS, IntentData.INTENT_ENCYCLOPEDIAS);
		/*习惯*/
		mBrcMap.put(MediaData.MEDIA_HABIT, IntentData.INTENT_HABIT);
		/*聊天*/
		mBrcMap.put(MediaData.MEDIA_CHAT, IntentData.INTENT_CHAT);
		/*日期(聊天)*/
		mBrcMap.put(MediaData.MEDIA_DATETIME, IntentData.INTENT_CHAT);
		/*电话*/
		mBrcMap.put(MediaData.MEDIA_CALL, IntentData.INTENT_CALL);
		/*聊天*/
		mBrcMap.put(MediaData.MEDIA_FAQ, IntentData.INTENT_CHAT);
		/*聊天*/
		mBrcMap.put(MediaData.MEDIA_OPENQA, IntentData.INTENT_CHAT);
		/*翻译*/
		mBrcMap.put(MediaData.MEDIA_TRANSLATION, IntentData.INTENT_TRANSLATION);
		/*翻译*/
		mBrcMap.put(MediaData.MEDIA_TRANSLATION_, IntentData.INTENT_TRANSLATION);
		/*笑话*/
		mBrcMap.put(MediaData.MEDIA_JOKE, IntentData.INTENT_JOKE);
		/*睡前故事*/
		mBrcMap.put(MediaData.MEDIA_STORY, IntentData.INTENT_STORY);
		/*国学*/
		mBrcMap.put(MediaData.MEDIA_SINOLOGY, IntentData.INTENT_SINOLOGY);
		/*诗词学习*/
		mBrcMap.put(MediaData.MEDIA_STUDY, IntentData.INTENT_STUDY);
		/*影视资讯*/
		mBrcMap.put(MediaData.MEDIA_MOVIEINFO, IntentData.INTENT_MOVIEINFO);
		/*提醒*/
		mBrcMap.put(MediaData.MEDIA_SCHEDULE, IntentData.INTENT_SCHEDULE);
		/*提醒*/
		mBrcMap.put(MediaData.MEDIA_SHUTUP, IntentData.INTENT_SHUTUP);
		/*拍照*/
		mBrcMap.put(MediaData.MEDIA_CAMERA, IntentData.INTENT_CAMERA);
		/*广场舞*/
		mBrcMap.put(MediaData.MEDIA_SQUAREDANCE, IntentData.INTENT_SQUAREDANCE);
		/*机器人提问*/
		mBrcMap.put(MediaData.MEDIA_QUSETION, IntentData.INTENT_QUSETION);
		/*取消*/
		mBrcMap.put(MediaData.MEDIA_CANCEL, IntentData.INTENT_CANCEL);
		/*摄影*/
		mBrcMap.put(MediaData.MEDIA_RECORD, IntentData.INTENT_RECORD);
		/*游戏*/
		mBrcMap.put(MediaData.MEDIA_YYDCHAT, IntentData.INTENT_YYDCHAT);
		/*地图*/
		mBrcMap.put(MediaData.MEDIA_MAP, IntentData.INTENT_MAP);
		/*跳舞*/
		mBrcMap.put(MediaData.MEDIA_DANCE, IntentData.INTENT_DANCE);
		/*戏曲*/
		mBrcMap.put(MediaData.MEDIA_OPERA, IntentData.INTENT_OPERA);
		/*移动*/
		mBrcMap.put(MediaData.MEDIA_MOVE, IntentData.INTENT_MOVE);
		/*健康*/
		mBrcMap.put(MediaData.MEDIA_HEALTH, IntentData.INTENT_HEALTH);
		/*智能家居*/
		mBrcMap.put(MediaData.MEDIA_SMARTHOME, IntentData.INTENT_SMARTHOME);
		//前进
		mBrcMap.put(MediaData.MEDIA_FORWARD, IntentData.INTENT_FORWARD);
		//后退
		mBrcMap.put(MediaData.MEDIA_BACK, IntentData.INTENT_BACK);
		//左转
		mBrcMap.put(MediaData.MEDIA_TURN_LEFT, IntentData.INTENT_TURN_LEFT);
		//右转
		mBrcMap.put(MediaData.MEDIA_TURN_RIGHT, IntentData.INTENT_TURN_RIGHT);
		//上看
		mBrcMap.put(MediaData.MEDIA_HEAD_UP, IntentData.INTENT_HEAD_UP);
		//下看
		mBrcMap.put(MediaData.MEDIA_HEAD_DOWN, IntentData.INTENT_HEAD_DOWN);
		//左看
		mBrcMap.put(MediaData.MEDIA_HEAD_LEFT, IntentData.INTENT_HEAD_LEFT);
		//右看
		mBrcMap.put(MediaData.MEDIA_HEAD_RIGHT, IntentData.INTENT_HEAD_RIGHT);
		//一直向前走
		mBrcMap.put(MediaData.MEDIA_ALWAYS_FORWARD, IntentData.INTENT_ALWAYS_FORWARD);
		//一直向后走
		mBrcMap.put(MediaData.MEDIA_ALWAYS_BACK, IntentData.INTENT_ALWAYS_BACK);
		//菜谱
		mBrcMap.put(MediaData.MEDIA_COOKBOOK, IntentData.INTENT_COOKBOOK);
		//关机
		mBrcMap.put(MediaData.MEDIA_SHUTDOWN, IntentData.INTENT_SHUTDOWN);
		//电量sound
		mBrcMap.put(MediaData.MEDIA_BATTERY, IntentData.INTENT_BATTERY);
		//音量sound
		mBrcMap.put(MediaData.MEDIA_SOUND, IntentData.INTENT_SOUND);
		//显示文本
		mBrcMap.put(MediaData.MEDIA_DISPLAY, IntentData.INTENT_DISPLAY);
		//相册
		mBrcMap.put(MediaData.MEDIA_PHOTO, IntentData.INTENT_PHOTO);
		
		//切换正式、测试版本
		mBrcMap.put(MediaData.MEDIA_SWITCH, IntentData.INTENT_SWITCH);
		//打开人脸识别
		mBrcMap.put(MediaData.MEDIA_FACE, IntentData.INTENT_FACE);
		
		//机器人控制
		mBrcMap.put(MediaData.MEDIA_MUTE, IntentData.INTENT_MUTE);
		
	}
	public String getIntentBroadcast(String key){
		String str = null;
		if(mBrcMap != null){
			str = mBrcMap.get(key);
		}
		return str;
	}
	
	public static BroadcastInfo getInstance(){
		if(mBroadcastInfo == null){
			mBroadcastInfo = new BroadcastInfo();
		}
		return mBroadcastInfo;
	}

}
