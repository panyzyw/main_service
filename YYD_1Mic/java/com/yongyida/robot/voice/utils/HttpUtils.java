package com.yongyida.robot.voice.utils;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.util.Log;
/**
 * 用于获取语音云的分析结果 
 * */
public class HttpUtils {

	  public static String HttpGet(String result) {


		//	String path ="http://ltpapi.voicecloud.cn/analysis/?api_key=K1h4f4Q3323567v5Q7A6PKvoDHQaaJahLpBXbh6F&text=我是中国人。&pattern=dp&format=json";
	    	try {
	    		result = URLEncoder.encode(result, "utf-8");
	    		String path ="http://ltpapi.voicecloud.cn/analysis/?api_key=K1h4f4Q3323567v5Q7A6PKvoDHQaaJahLpBXbh6F&text="+result+"&pattern=dp&format=json";
	    		//webView.loadUrl("http://m.baidu.com/s?wd="+ key + "&pn=0&rn=50&tn=jsons");
				URL url = new URL(path);
				HttpURLConnection httpconn =(HttpURLConnection) url.openConnection();
				if(httpconn.getResponseCode() == HttpURLConnection.HTTP_OK){
					InputStreamReader isr = new InputStreamReader(httpconn.getInputStream(),"utf-8");
					int len = 0;
					StringBuffer str = new StringBuffer();
					while((len = isr.read())!=-1){
						str.append((char)len);
					}
					Log.d("voiceUnderstand", "json:" + str.toString());
					return str.toString();
				}else{
					Log.d("voiceUnderstand", "server error");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
			
	    }

}
