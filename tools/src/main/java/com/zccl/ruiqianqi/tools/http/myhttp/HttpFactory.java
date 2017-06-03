package com.zccl.ruiqianqi.tools.http.myhttp;

import android.content.Context;

import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.PhoneUtils;
import com.zccl.ruiqianqi.tools.http.MySecurity;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

public class HttpFactory {

	/** 类的标识 */
	private static String TAG = HttpFactory.class.getSimpleName();

	/**连接超时时间*/
	private static final int DEFAULT_SOCKET_TIMEOUT = 20 * 1000;
	/**GET请求*/
	public static final int GET = 0;
	/**POST请求*/
	public static final int POST = 1;

	/**UTF-8编码*/
	public static final String UTF8 = "UTF-8";
	/**GBK编码*/
	public static final String GBK = "GBK";
	/**GB2312编码*/
	public static final String GB2312 = "GB2312";

	/**
	 * 创建HTTP连接及类型
	 * @param urls
	 * @param type
	 * @return
	 */
	public static HttpURLConnection createHttpConn(String urls, int type){
		HttpURLConnection connection = null;
		try {
			LogUtils.e(TAG, urls);
			URL url = new URL(urls);
			connection = (HttpURLConnection)url.openConnection();
			if(connection==null){
				return null;
			}
			if(GET == type){
				connection.setRequestMethod("GET");
			}else if(POST == type){
		        connection.setDoOutput(true);
		        connection.setDoInput(true);
		        connection.setRequestMethod("POST");
		        connection.setUseCaches(false);
		        connection.setInstanceFollowRedirects(true);
		        // 提交表单数据
		        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		        // 提交普通数据
				//connection.setRequestProperty("Content-Type", "text/plain;charset=UTF-8");
			}
			connection.setConnectTimeout(DEFAULT_SOCKET_TIMEOUT);
			connection.setReadTimeout(DEFAULT_SOCKET_TIMEOUT);

			// 对https做处理
			if (urls.startsWith("https")) {
				HttpsURLConnection connections = (HttpsURLConnection) connection;
				MySecurity mySecurity = new MySecurity(MySecurity.SECURITY.SSL);
				SSLContext sslContext = mySecurity.getSSLContext();
				SSLSocketFactory ssf = sslContext.getSocketFactory();

				connections.setSSLSocketFactory(ssf);
				HttpsURLConnection.setDefaultSSLSocketFactory(ssf);

				connections.setHostnameVerifier(new MySecurity.TrustAnyHostnameVerifier());
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (java.net.UnknownHostException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return connection;
	}


	/**
	 * 设置平台头信息
	 * @param urlConn
	 * @param context
	 * @param headers http上行的头信息
	 */
	public static void setHttpHeader(HttpURLConnection urlConn, Context context, Map<String,String> headers){
		
		if(headers != null){
			Iterator<Map.Entry<String, String>> it = headers.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				urlConn.addRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		//--手机串号,在ios平台下似是mac地址
		urlConn.addRequestProperty("imei",	PhoneUtils.getPhoneIMEI(context));
	}

	/**
	 * iso-8859-1是JAVA网络传输使用的标准字符集
	 * 因为URL采用ISO-8859-1编码传输，服务器默认先调用ISO-8859-1编码解一次
	 *
	 * 网页中的表单使用POST方法提交时，数据内容的类型是 application/x-www-form-urlencoded，这种类型会：
	 * 1.字符"a"-"z"，"A"-"Z"，"0"-"9"，"."，"-"，"*"，和"_" 都不会被编码;
	 * 2.将空格转换为加号 (+) ;
	 * 3.将非文本内容转换成"%xy"的形式,xy是两位16进制的数值;
	 * 4.在每个 name=value 对之间放置 & 符号。
	 *
	 * @param str
	 * @param charset UTF-8  GBK  GB2312
	 */
	public static String encode(String str, String charset){
		try {
			str = URLEncoder.encode(str, charset);

			// 客户端
			//String en = URLEncoder.encode("中","GB2312"); "%D6%D0"

			// 客户端解析
			//String str = URLDecoder.decode(en,"GB2312"); "中"

			// 服务端解析
			//String zhong = request.getParameter("zhong");
			//byte[] bytes = zhong.getBytes("iso-8859-1");
			//String str = new String(bytes,"GB2312");

			return str;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解码【执行encode的反序过程】
	 * @param str
	 * @param charset UTF-8  GBK  GB2312
	 * @return
	 */
	public static String decode(String str, String charset){
		try {
			return URLDecoder.decode(str, charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 因为URL采用ISO-8859-1编码传输：
	 * 如果数据是英文字母/数字，原样发送；
	 * 如果是空格，转换为+；
	 * 如果是中文/其他字符，则直接把字符串用BASE64加密，得出如： %E4%BD%A0%E5%A5%BD，其中%XX中的XX为该符号以16进制表示的ASCII。
	 * 
	 * POST请求传输数据
	 * @param conn
	 * @param paramsmap
	 * @return
	 */
	public static String postHttpData(HttpURLConnection conn, Map<String,String> paramsmap){
		
		StringBuffer postParams = new StringBuffer();
		String results = null;
		try {
			if (paramsmap != null && !paramsmap.isEmpty()){
				LogUtils.e(TAG, "postHttpData");
				for (Map.Entry<String, String> entry: paramsmap.entrySet()) {

					//将参数中的特殊字符，编码成URL格式
					String encodedName = encode(entry.getKey(), UTF8);
					       //encodedName = entry.getKey();
					String encodedValue = encode(entry.getValue(), UTF8);
					       //encodedValue = entry.getValue();
					if(postParams.length() > 0){
						postParams.append("&");
					}
					postParams.append(encodedName+"="+encodedValue);

					LogUtils.e(TAG, encodedName+"="+encodedValue);
				}

				//String ens = AesUtils.encryption(postparams.toString());
				String ens = postParams.toString();
				if(ens != null){
					byte[] by = ens.getBytes();
					conn.setRequestProperty("Content-Length", String.valueOf(by.length));
					DataOutputStream out = new DataOutputStream(conn.getOutputStream());
					out.write(by);
					out.flush();
					out.close();
				}else{
					conn.setRequestProperty("Content-Length", String.valueOf(0));
				}
				postParams.delete(0, postParams.length());
				postParams.setLength(0);
			}

			//已主动发起response了urlConn.getContentType()
			int result = conn.getResponseCode();//等待返回
			//conn.getContentType();
			//conn.getContentLength();

			if (result == HttpURLConnection.HTTP_OK) {

				//HttpURLConnection.connect函数，实际上只是建立了
				//一个与服务器的TCP连接，并没有实际发送HTTP请求。
				//无论是post还是get，HTTP请求实际上直到
				//HttpURLConnection.getInputStream()这个函数里面才正式发送出去。

				/*String temp = null;
				InputStream in = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
				while ((temp = br.readLine()) != null) {
					postparams.append(temp);
				}
				br.close();
				in.close();
				results = postparams.toString();*/

				int len = -1;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				InputStream in = conn.getInputStream();
				byte[] buf = new byte[1024];
				while ((len = in.read(buf)) > -1) {
					baos.write(buf, 0, len);
				}
				postParams.append(new String(baos.toByteArray(), UTF8));
				baos.close();
				in.close();
				results = postParams.toString();

			} else {

			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(conn != null){
				conn.disconnect();
			}
		}

		return results;
	}

	/**
	 * HTTP——get请求
	 * @param conn
	 * @return
	 */
	public static String getHttpData(HttpURLConnection conn){
		if(conn != null){
			StringBuffer buffer = null;
			//String temp = null;
			try {

				LogUtils.e(TAG, "getHttpData");
				int result = conn.getResponseCode();

				if((result == HttpURLConnection.HTTP_OK)){
					buffer = new StringBuffer();
					
					/*
					InputStream in = conn.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
					while ((temp = br.readLine()) != null) {
						buffer.append(temp);
					}
					br.close();
					in.close();
					*/
					
					int len = -1;
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					InputStream in = conn.getInputStream();
					byte[] buf = new byte[1024];
					while ((len = in.read(buf)) > -1) {
						baos.write(buf, 0, len);
					}
					buffer.append(new String(baos.toByteArray(), "utf-8"));
					
					baos.close();
					in.close();
					return buffer.toString();

				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				if(conn != null){
					conn.disconnect();
				}
			}
		}
		return null;
	}

	/**
	 * 下载HTTP文件，如果还要计算百分比
	 * @param conn 网络文件的地址
	 * @param fileSave 存放文件的绝对路径
	 * @return
	 */
	public static File getHttpFile(HttpURLConnection conn, String fileSave) {
		if(conn != null){
			int len = -1;
			try {
				if((conn.getResponseCode() == HttpURLConnection.HTTP_OK)){
					File file = new File(fileSave);
					file.delete();

					InputStream is = conn.getInputStream();
					FileOutputStream fs = new FileOutputStream(file);
					byte[] buf = new byte[1024];
					while ((len = is.read(buf)) > -1) {
						fs.write(buf, 0, len);
					}
					is.close();
					fs.close();
					return file;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				if(conn!=null){
					conn.disconnect();
				}
			}
		}
		return null;
	}

	/**
	 * 获取手机的外网IP
	 * http://www.input8.com/ip/
	 * http://www.cmyip.com/
	 * http://city.ip138.com/ip2city.asp
	 *
	 * 1.去连一个公网ip，然后，让这个公网ip告诉你你的ip地址。
	 * 2.用traceroute，去连一个公网ip，观察你的路由的转跳情况，以某一个基点，来判断你的公网ip地址。
	 */
	public static String getWifiWwwIp(String ipAddress) {

		HttpURLConnection httpConnection = createHttpConn(ipAddress, GET);
		if (httpConnection != null) {
			String ip = getHttpData(httpConnection);
			Pattern pat = Pattern.compile(".*(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}).*",
					Pattern.CASE_INSENSITIVE);
			Matcher mat = pat.matcher(ip);
			// 捕获组
			if (mat.find()) {
				// 整个字符串
				// mat.group(0);
				return mat.group(1);
			}
		}
		return null;
	}
}
