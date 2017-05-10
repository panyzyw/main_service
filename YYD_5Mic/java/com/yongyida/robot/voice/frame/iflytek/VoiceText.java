package com.yongyida.robot.voice.frame.iflytek;

import android.content.Context;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
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
	
    @Override
    protected void parseFail() {
    	// TODO Auto-generated method stub
    //	super.parseFail();
    }
	@Override
	public void start() {
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
