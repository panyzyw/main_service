package com.yongyida.robot.voice.utils;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.yongyida.robot.voice.bean.ParticipleBean;


/**
 * Json结果解析.
 */
public class JsonParserUtils { 

	public static  <T> T parseResult(String json, Class<T> T){
		Gson gson = new Gson();
		return gson.fromJson(json, T);
		
	}
	 /**
	  * 语音云json解析
	  * */   
		public static ArrayList<ParticipleBean> praseJsonResult(String result){
			ArrayList<ParticipleBean> result_list = new ArrayList<ParticipleBean>();
			try {
				
			//	JSONObject jsonobject = new JSONObject(result);


				JSONArray jsons = new JSONArray(result);
				JSONArray json_array = jsons.getJSONArray(0).getJSONArray(0);
	          
				int size = json_array.length();
				
				for(int i=0;i<size;i++){
				  ParticipleBean participle = new ParticipleBean();
				  JSONObject json = json_array.getJSONObject(i);
				  int id  =  json.getInt("id");
				  String cont = json.getString("cont");
				  String pos = json.getString("pos");
				  int parent  =  json.getInt("parent");
				  String relate = json.getString("relate");
				  participle.setId(id);
				  participle.setCont(cont);
				  participle.setPos(pos);
				  participle.setParent(parent);
				  participle.setRelate(relate);
				  result_list.add(participle);
				  
				}


			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			return result_list;
			
			
			
		} 
}
