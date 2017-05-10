package com.zccl.ruiqianqi.tools.config;

import android.content.Context;
import android.os.Environment;

import com.zccl.ruiqianqi.tools.FileUtils;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.tools.regex.CmdRegex;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

public class MyConfigure {

	/**失败重试次数*/
	public static final int RETRY_COUNT = 3;
	
	/**处理请求结果，返回OK（最终状态）*/
	public final static String OKEY = "OKEY";
	/**处理请求结果，返回失败（最终状态）*/
	public final static String ERROR = "ERROR";
	/**处理请求结果，返回重试（中间状态）*/
	public final static String RETRY = "RETRY";
	/**处理请求结果，重试结束（中间状态）*/
	public final static String RETRY_OVER = "RETRY_OVER";

	/**坐标参考的分辨率（宽）*/
	public final static int REFER_W = 720;
	/**坐标参考的分辨率（高）*/
	public final static int REFER_H = 1280;

	/**坐标参考的分辨率（宽）*/
	public final static int REFER_W2 = 1080;
	/**坐标参考的分辨率（高）*/
	public final static int REFER_H2 = 1920;

	/**从我的资源（用户版）加载*/
	public final static int ZERO_MYRES = 0;
	/**从assets加载*/
	public final static int ONE_ASSETS = 1;
	/**从SD卡和files加载*/
	public final static int TWO_SDCARD = 2;
	/**从files加载*/
	public final static int THREE_FILES = 3;
	/**从我的资源（系统版）加载*/
	public final static int FOUR_SYSTEMRES = 4;
	/**从系统目录 res 加载*/
	public final static int FIVE_RESRAW = 5;
	/**加载原始资源*/
	public final static int SIX_ABSOLUTE = 6;

	/** 我的资源路径getResourceAsStream, assets and sdcard */
	public static final String ZCCLRES = "zcclres"+File.separator;

	/** SD卡是否存在 */
	public static boolean SDEXIST = false;
	/** SD卡路径，系统函数的SD卡路径，JAVA截屏指令无法识别，只能用/sdcard/ */
	public static String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	/** 通用资源路径 */
	public static String ZCCL_SDCARD = File.separator + "sdcard" + File.separator + ZCCLRES;

	/**我的资源路径 getResourceAsStream（用户版） */
	public static String ZCCL_CLIENT = ZCCLRES + "client" + File.separator;

	/**我的资源路径 getResourceAsStream（系统版） */
	public static String ZCCL_SYSTEM = ZCCLRES + "system" + File.separator;

	/**我的资源路径assets*/
	public static String ZCCL_ASSETS = ZCCLRES;


	/** 下载路径文件夹名称 */
	public static final String DOWNLOAD = "download" + File.separator;
	/** 音乐下载路径文件夹名称 */
	public static final String MUSIC = "music" + File.separator;
	// 用户自定义图片存放位置
	public static final String PHOTO_DIR = "user_photo" + File.separator;

	// 思必驰的缓存目录
	public static final String SI_BI_CHI = "sibichi" + File.separator;

	/**开发包配置文件所在，从资源包根目录加载，zcclres/systemconfig.properties */
	private static String SYSTEM_CONFIG = MyConfigure.ZCCL_SYSTEM + "systemconfig";
	/**用户配置文件所在，从资源包根目录加载，zcclres/clientconfig.properties */
	private static String CLIENT_CONFIG = MyConfigure.ZCCL_CLIENT + "clientconfig";
	/** 读配置文件（系统）*/
	private static ResourceBundle SYSTEM_RESOURCE_BUNDLE = null;
	/** 系统配置文件所在 */
	private static final String SYSTEM_CONFIG_BUNDLE = SYSTEM_CONFIG.replace("/", ".");
	/** 读配置文件（用户）*/
	private static Properties CLIENT_PROPERTIES = null;

	/**
	 * 各种配置初始化
	 * @param context
     */
	public static void init(Context context){
		CmdRegex.getInstance(context).load("commands");
	}

	/**
	 * 加载配置文件
	 */
	static {
		// 选择系统配置文件
		loadSystemConfigure(null);
		// 选择用户配置文件
		loadClientConfigure(null);
	}

	/**
	 * 根据配置选择配置文件
	 * @param locale
	 */
	public static void loadSystemConfigure(Locale locale) {
		if(locale==null){
			locale = Locale.getDefault();
		}
		if(locale.equals(Locale.CHINA)){
			MyConfigure.SYSTEM_RESOURCE_BUNDLE = ResourceBundle.getBundle(MyConfigure.SYSTEM_CONFIG_BUNDLE, locale);
		}else if(locale.equals(Locale.TAIWAN)){
			MyConfigure.SYSTEM_RESOURCE_BUNDLE = ResourceBundle.getBundle(MyConfigure.SYSTEM_CONFIG_BUNDLE, locale);
		}else{
			MyConfigure.SYSTEM_RESOURCE_BUNDLE = ResourceBundle.getBundle(MyConfigure.SYSTEM_CONFIG_BUNDLE, new Locale("en", "US"));
		}
	}

	/**
	 * 加载用户配置文件
	 * @param locale
     */
	public static void loadClientConfigure(Locale locale) {
		if(locale==null){
			locale = Locale.getDefault();
		}

		if(CLIENT_CONFIG != null) {
			CLIENT_PROPERTIES = new Properties();
		}
		CLIENT_PROPERTIES.clear();
		InputStream is;
		if(locale.equals(Locale.CHINA)){
			is = FileUtils.getFileStream(null, "clientconfig_zh_CN.properties", ZERO_MYRES);
		}else if(locale.equals(Locale.TAIWAN)){
			is = FileUtils.getFileStream(null, "clientconfig_zh_TW.properties", ZERO_MYRES);
		}else{
			is = FileUtils.getFileStream(null, "clientconfig_en_US.properties", ZERO_MYRES);
		}
		if(is!=null) {
			try {
				CLIENT_PROPERTIES.load(is);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 得到对应的KEY值
	 * 用户配置文件对应的值，可以覆盖系统对应变量
	 * @param key
	 * @return
	 */
	public static String getValue(String key) {

		LogUtils.e("getValue", "key = "+key);
		if (CLIENT_CONFIG != null) {
			Object obj = CLIENT_PROPERTIES.get(key);
			if (obj != null) {
				return obj.toString();
			}
		}
		try {
			return SYSTEM_RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return null;
		}
	}

	/**
	 * 手动强制选择语言
	 *  @param context
	 *  @param locale
	 *
	 * 	locale = new Locale("zh", "CN");
	 *	locale = new Locale("zh", "TW");
	 *	locale = new Locale("en", "US");
	 */
	public static void chooseLanguage(Context context, Locale locale) {
		if(locale==null){
			locale = Locale.getDefault();
		}
		//locale = new Locale("zh", "CN");
		//locale = new Locale("zh", "TW");
		//locale = new Locale("en", "US");
		//Locale.getDefault().getLanguage();  //语言:取到的比如中文为zh，英文为en，日文为ko；
		//Locale.getDefault().toString();     //具体的类别:比如繁体为zh_TW，简体为zh_CN。英文中有en_GB；日文有ko_KR。

		/*
		Resources resources = context.getResources();//获得res资源对象
		Configuration config = resources.getConfiguration();//获得设置对象
		DisplayMetrics dm = resources.getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。
		config.locale = locale; //语言
		resources.updateConfiguration(config, dm);
		*/

		//选择系统配置文件
		loadSystemConfigure(locale);
		//选择用户配置文件
		loadClientConfigure(locale);
	}

	/**
	 * Locale.getDefault().getLanguage()
	 * 中文为 zh，
	 * 英文为 en，
	 * 日文为 ko；
	 * Locale.getDefault().getCountry()
	 * CN
	 * TW
	 * HK
	 * US
	 *
	 * Locale.getDefault().getDisplayLanguage() 中文  中文  中文  English
	 * Locale.getDefault().getDisplayCountry()  中国  台灣  香港  United States
	 *
	 * @return
     */
	public static String getLanguage(){
		if(StringUtils.isEmpty(Locale.getDefault().getCountry())){
			return Locale.getDefault().getLanguage();
		}
		return Locale.getDefault().getLanguage()/*+"-"+ Locale.getDefault().getCountry()*/;
	}


	// 存储的语言KEY
	public static final String KEY_LANGUAGE = "language";

}
