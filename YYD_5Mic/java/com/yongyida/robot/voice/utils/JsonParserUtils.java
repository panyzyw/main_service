package com.yongyida.robot.voice.utils;
import com.google.gson.Gson;

/**
 * Json结果解析.
 */
public class JsonParserUtils { 

	public static  <T> T parseResult(String json, Class<T> T){
		Gson gson = new Gson();
		return gson.fromJson(json, T);
		
	}
}
