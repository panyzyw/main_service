package com.yongyida.robot.voice.frame.iflytek;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.yongyida.robot.voice.bean.BaseInfo;
import com.yongyida.robot.voice.bean.ParticipleBean;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.robot.ShowVoiceText;
import com.yongyida.robot.voice.utils.HttpUtils;
import com.yongyida.robot.voice.utils.JsonParserUtils;
import com.yongyida.robot.voice.utils.LogUtils;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;
import com.yongyida.robot.voice.utils.SharePreferenceUtils;
import com.yongyida.robot.voice.utils.ShowToast;
/**
 * 文本理解
 * 
 * @author Administrator
 *
 */
public class VoiceText extends CommVoiceParse{
	
	// 文本理解对象
	private TextUnderstander mTextUnderstander;
	
	private static VoiceText voiceText;
	/**
	 * 用于区别手机控制的文本解析和语音云的数据解析
	 * */
	private boolean isVoiceUnderstandAgain = false;
	TextUnderstanderListener listener = new TextUnderstanderListener() {

		@Override
		public void onResult(UnderstanderResult result) {
			if(result != null){
				json = result.getResultString();
				parse();
			}
		}

		@Override
		public void onError(SpeechError arg0) {
			
		}
	};
		
	private VoiceText(Context context){
		
		this.context = context;
		 mTextUnderstander = TextUnderstander.createTextUnderstander(context,
			new InitListener() {
			 
				@Override
				public void onInit(int code) {
				}
			});
	}
/**
 * 用于rc=4时的文本解析
 * */	
	public void praseAgain(final String text) {
		isVoiceUnderstandAgain = true;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String str = text;
				String result= HttpUtils.HttpGet(str);
				if(result==null){
					mTextUnderstander.understandText(str, listener);
					return ;
				}
				ArrayList<ParticipleBean> lists = JsonParserUtils.praseJsonResult(result);
				ArrayList<ParticipleBean> list_result = new ArrayList<ParticipleBean>();
				for (ParticipleBean participleBean : lists) {
					  String type = participleBean.getPos();
					  if(type.contains("n")&&!type.contains("nd")){
						  list_result.add(participleBean);
					  }
				}
				if (list_result.size()>0) {
					str = list_result.get(list_result.size()-1).getCont();
					Log.d("voiceUnderstand", "text_key:" + str);
				}
				if(mTextUnderstander == null){
					return;
				}
				
				if(str == null || str.equals("")){
					return;
				}
				
				if (mTextUnderstander.isUnderstanding()) {
					mTextUnderstander.cancel();
				}
				mTextUnderstander.understandText(str, listener);  
			}
		}).start();
		
	}

/*	@Override
	public void parse() {
		Log.d("jlog", "json:" + json);
        if (json == null) {
            return;
        }
        LogUtils.showLogInfo(GeneralData.SUCCESS, json);
        Map<String, String> map = new HashMap<String, String>();
        map.put(GeneralData.ACTION, IntentData.INTENT_PARSE_RESULT);
        BaseInfo type = JsonParserUtils.parseResult(json, BaseInfo.class);
        if (type == null) {
            return;
        }
        if(isVoiceUnderstandAgain){
        	if(type.getSuccess()==GeneralData.RESULT_FOUR){
        		map.put(GeneralData.RESULT, VoiceUnderstand.temp_json);        		
        	}else{
        		map.put(GeneralData.RESULT, json);  	
        	}
        }else{
        	map.put(GeneralData.RESULT, json);       	
        }
        SendBroadcastUtils.sendBroad(context, map);
        

        
        if ((SharePreferenceUtils.getInstance(context).getInt("showtext", ShowVoiceText.SHOW_TEXT) == ShowVoiceText.SHOW_TEXT)&&!isVoiceUnderstandAgain) {
            ShowToast.getInstance(context).show(type.getText());
        }
        switch (type.getSuccess()) {

            case GeneralData.RESULT_THERO:
                parseSuccess();
                break;

            case GeneralData.RESULT_FOUR:
                parseFail();
                break;
        }
	}*/
	
	
    /**
     * 在rc = 4时，薄言语义时，去掉讯飞解析失败时的语音
     * */
	@Override
	protected void parseFail() {
		// TODO Auto-generated method stub
		//super.parseFail();
	}
	@Override
	public void start() {
		isVoiceUnderstandAgain = false;
		if(mTextUnderstander == null){
			return;
		}
		
		if(words == null || words.equals("")){
			return;
		}
		
		if (mTextUnderstander.isUnderstanding()) {
			mTextUnderstander.cancel();
		}
		
		mTextUnderstander.understandText(words, listener);
	}


	@Override
	public void stop() {
		if(mTextUnderstander == null){
			return;
		}
		
		if (mTextUnderstander.isUnderstanding()) {
			mTextUnderstander.cancel();
		}
	}
	
	
	public static VoiceText getInstance(Context context){
		
		if(voiceText == null){
			synchronized (VoiceText.class) {
				if(voiceText == null){
					voiceText = new VoiceText(context);
				}
				
			}
		}
		
		return voiceText;
		
	}

}
