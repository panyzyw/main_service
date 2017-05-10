package com.yongyida.robot.voice.frame;

import com.yongyida.robot.voice.frame.iflytek.CommVoiceParse;


public class ParseFactory {
	private CommVoiceParse factory;
	
	public void setFactory(CommVoiceParse factory){
		
		this.factory = factory;
	}
	
	public void parseStart(String words){
		if(factory == null){
			return;
		}
		factory.setWords(words);
		factory.start();
	}
	
	public void parseStop(){
		if(factory == null){
			return;
		}
		factory.stop();
	}
	
}
