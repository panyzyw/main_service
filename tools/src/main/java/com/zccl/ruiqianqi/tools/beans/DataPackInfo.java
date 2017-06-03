package com.zccl.ruiqianqi.tools.beans;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * 深拷贝就是对象引用和对象一起给复制了一遍，实现了这个物理上的完全隔离，
 * 而浅拷贝则是简单的复制了对象的引用，实质上还是指向同一个对象，
 * 基本数据类型和string类的拷贝都是深拷贝
 * @author Administrator
 *
 */
public class DataPackInfo implements Cloneable{
	
	/**
	 * 系统应用还是第三方应用
	 * 
	 * 所有应用程序
	 * {@link com.zccl.ruiqianqi.tools.SystemUtils#FILTER_ALL_APP}
	 * 系统程序
	 * {@link com.zccl.ruiqianqi.tools.SystemUtils#FILTER_SYSTEM_APP}
     * 第三方应用程序
	 * {@link com.zccl.ruiqianqi.tools.SystemUtils#FILTER_THIRD_APP}
     * 安装在SDCard的应用程序
	 * {@link com.zccl.ruiqianqi.tools.SystemUtils#FILTER_SDCARD_APP}
	 */
	private int appFlag = -1;
	// 应用PID
	private int processPid = 0;
	/**
	 * 应用UID
	 * UID <= 10000 的是系统应用
	 */
	private int processUid = 0;
	// 应用名
    private String appName = null;
    // 应用包名
    private String packageName = null;
    // 版本名
    private String versionName = null;
    // 版本号
    private int versionCode = 0;
    // 应用icon
    private Drawable icon = null;
    // 启动意图
    private Intent intent = null;
    // files的上级目录
    private String dataDir = null;
    // so所在目录
    private String nativeDir = null;
    // apk完整路径
    private String sourceDir = null;
    // 应用包名
    private String processName  =null;

	public int getAppFlag() {
		return appFlag;
	}

	public void setAppFlag(int appFlag) {
		this.appFlag = appFlag;
	}

	public int getProcessPid() {
		return processPid;
	}

	public void setProcessPid(int processPid) {
		this.processPid = processPid;
	}

	public int getProcessUid() {
		return processUid;
	}

	public void setProcessUid(int processUid) {
		this.processUid = processUid;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}

	public String getDataDir() {
		return dataDir;
	}

	public void setDataDir(String dataDir) {
		this.dataDir = dataDir;
	}

	public String getNativeDir() {
		return nativeDir;
	}

	public void setNativeDir(String nativeDir) {
		this.nativeDir = nativeDir;
	}

	public String getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	/**
	 * Object类的clone方法只会拷贝对象中的基本的数据类型，
	 * 对于数组、容器对象、引用对象等都不会拷贝，这就是浅拷贝。
	 * 如果要实现深拷贝，必须将原型模式中的数组、容器对象、引用对象等另行拷贝。
	 */
	public DataPackInfo clone(){
		DataPackInfo prototype = null;  
        try{  
        	
        	//（基本数据类型及String是深拷贝）
        	//（数组、容器对象、引用对象等浅拷贝）
            prototype = (DataPackInfo)super.clone(); 
            
            //（对象单独拷贝成深拷贝）
            prototype.intent = (Intent) intent.clone();
            
        }catch(CloneNotSupportedException e){  
            e.printStackTrace();  
        }  
        return prototype;   
    }  
    
}