package com.zccl.ruiqianqi.tools;

import android.os.IBinder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

public class ReflectUtils {

	/***********************************外部jar/apk/dex反射调用*************************************/
	/*******************************************************************************************/
	/**
	 * 静态反射调用外部DEX文件中的方法
	 * @param dex 要加载的外部 jar/apk/dex
	 * @param output 可以读写解压的路径 optimizedDirectory（解压缩dex 为 优化后的odex）
	 * @param classname 类名
	 * @param funcname  方法名
	 * @param args      参数列表
	 * @param parameterTypes 参数类型列表
	 * @return
	 */
	public static Object callStaticMethodFromDex(String dex, String output, String classname,
												 String funcname, List<Object> args, Class<?>... parameterTypes){
		//DexClassLoader
		//这个可以加载jar/apk/dex，也可以从SD卡中加载，也是本文的重点。
		
		//PathClassLoader　　
		//只能加载已经安装到Android系统中的apk文件。
        try {
        	DexClassLoader dcl = new DexClassLoader(dex, output, null, ClassLoader.getSystemClassLoader().getParent());
        	// 获取相应的类对象名称（外部加载）
        	//Class<?> classType_out = dcl.loadClass(classname);
        	
        	Class<?> classType_out = Class.forName(classname, true, dcl);

        	//sourcePathName = jar/apk/dex
        	//outputPathName = odex
        	//DexFile.loadDex(sourcePathName, outputPathName, flags);
        	
        	if(args==null){
        		Method func_method = classType_out.getMethod(funcname, (Class<?>[])null);
        		func_method.setAccessible(true);
				return func_method.invoke(null, (Object[])null);
        	}else{
				Method func_method = classType_out.getMethod(funcname, parameterTypes);
				func_method.setAccessible(true);
				return func_method.invoke(null, args.toArray());
        	}
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        return null;
	}


	/**********************************Android系统内部反射调用*****************************************/
	/*********************************************************************************************/
	/**
	 * 反射调用类的静态方法
	 * （内部类+"$Stub"）（内部类+"$Stub$Proxy"）
	 * @param classname 类名
	 * @param funcname  方法名
	 * @param args      参数列表
	 * @param parameterTypes 参数类型列表
	 * @return
	 */
	public static Object callStaticMethod(String classname, String funcname,
										  List<Object> args, Class<?>... parameterTypes) {

        try {
        	// 获取相应的类对象名称（内部存在）
        	Class<?> classType_in = Class.forName(classname);
        	if(args==null){
        		Method func_method = classType_in.getMethod(funcname, (Class<?>[])null);
        		func_method.setAccessible(true);
				return func_method.invoke(null, (Object[])null);
        	}else{
				Method func_method = classType_in.getMethod(funcname, parameterTypes);
				func_method.setAccessible(true);
				return func_method.invoke(null, args.toArray());
        	}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        return null;
	}
	
	/**
	 * 反射调用类的静态方法
	 * @param classType_in 类的类型
	 * @param funcname	        方法名
	 * @param args		       参数集合
	 * @param parameterTypes 参数类型集合
	 * @return
	 */
	public static Object callStaticMethod2(Class<?> classType_in, String funcname, List<Object> args, Class<?>... parameterTypes) {

        try {
        	if(args==null){
        		// 获取相应的类对象名称（内部存在）
				Method func_method = classType_in.getMethod(funcname, (Class<?>[])null);
				func_method.setAccessible(true);
				return func_method.invoke(null, (Object[])null);
        	}else{
	        	// 获取相应的类对象名称（内部存在）
				Method func_method = classType_in.getMethod(funcname, parameterTypes);
				func_method.setAccessible(true);
				return func_method.invoke(null, args.toArray());
        	}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
        return null;
	}

	/**
	 * 反射调用内部实例方法
	 * （内部类+"$Stub"）（内部类+"$Stub$Proxy"）
	 * @param classname 类名
	 * @param funcname  方法名
	 * @param args      参数列表
	 * @param parameterTypes 参数类型列表
	 * @return
	 */
	public static Object callNewObjectMethod(String classname, String funcname,
											 List<Object> args, Class<?>... parameterTypes){
        try {
        	Class<?> classType_in = Class.forName(classname);
			Object obj = classType_in.newInstance();
			
			if(args==null){
        		Method func_method = classType_in.getMethod(funcname, (Class<?>[])null);
        		func_method.setAccessible(true);
				return func_method.invoke(obj, (Object[])null);
        	}else{
				Method func_method = classType_in.getMethod(funcname, parameterTypes);
				func_method.setAccessible(true);
				return func_method.invoke(obj, args.toArray());
        	}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        return null;
	}

	/**
	 * 反射调用外部实例方法
	 * （内部类+"$Stub"）（内部类+"$Stub$Proxy"）
	 * @param obj------- 对象
	 * @param funcname----- 方法名
	 * @param args--------- 参数列表
	 * @param parameterTypes---- 参数类型列表
	 * @return
	 */
	public static Object callObjectMethod(Object obj, String funcname,
										  List<Object> args, Class<?>... parameterTypes){
        try {
        	Class<?> classType_in = obj.getClass();
        	if(args==null){
        		Method func_method = classType_in.getMethod(funcname, (Class<?>[])null);
        		func_method.setAccessible(true);
				return func_method.invoke(obj, (Object[])null);
        	}else{
				Method func_method = classType_in.getMethod(funcname, parameterTypes);
				func_method.setAccessible(true);
				return func_method.invoke(obj, args.toArray());
        	}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
	}


	/************************************系统服务反射调用**************************************************/
	/*************************************************************************************************/
	/**参数集合*/
	private static List<Object> args = new ArrayList<Object>();;
	/**
	 * 从ServiceManager得到某个服务对象
	 * @param interfac---------服务接口类全名
	 * @param serviceName------服务注册的名字
	 * @return
	 */
	public static Object getServiceByStub(String interfac, String serviceName){
		args.clear();
		args.add(serviceName);
		Class<?>[] cls = {String.class};
		//与后台服务的通信管道
		Object binder =callStaticMethod("android.os.ServiceManager", "getService", args, cls);

		if(binder==null){
			return null;
		}
		args.clear();
		args.add(binder);
		Class<?>[] ismsclss = {IBinder.class};

		//返回一个代理对象，不管是内部的还是专门的代理类，反正就是代理对象（在管道之上做的包装）
		Object serviceProxyObj =callStaticMethod(interfac+"$Stub", "asInterface", args, ismsclss);
		return serviceProxyObj;
	}

	/**
	 * 调用服务的方法
	 * @param interfac------------服务接口类全名
	 * @param serviceobj----------服务实例
	 * @param funcname------------服务方法
	 * @param args----------------方法参数
	 * @param parameterTypes------参数类型
	 */
	/*public static Object callServiceMethodByStubProxy(Object serviceProxyObj, String funcname,
			List<Object> args, Class<?>... parameterTypes){
		//从真正调用的代理类中查找方法，并进行调用，这个代理类是内部类
		//interfac+"$Stub$Proxy"
		return callObjectMethod(serviceProxyObj,funcname,args,parameterTypes);
	}*/


	/**
	 * 从ServiceManager得到某个服务对象
	 * @param interfac---------服务接口类全名
	 * @param serviceName------服务注册的名字
	 * @return
	 */
	public static Object getService(String interfac, String serviceName){
		args.clear();
		args.add(serviceName);
		Class<?>[] cls = {String.class};
		//与后台服务的通信管道
		Object binder =callStaticMethod("android.os.ServiceManager", "getService", args, cls);

		if(binder==null){
			return null;
		}
		args.clear();
		args.add(binder);
		Class<?>[] ismsclss = {IBinder.class};

		//返回一个代理对象，不管是内部的还是专门的代理类，反正就是代理对象（在管道之上做的包装）
		Object serviceProxyObj =callStaticMethod(interfac, "asInterface", args, ismsclss);

		return serviceProxyObj;
	}

	/**
	 * 调用服务的方法
	 * @param interfac------------服务接口类全名
	 * @param serviceobj----------服务实例
	 * @param funcname------------服务方法
	 * @param args----------------方法参数
	 * @param parameterTypes------参数类型
	 */
	/*public static Object callServiceMethod(String interfac, Object serviceProxyObj, String funcname,
			List<Object> args,Class<?>... parameterTypes){
		//从真正调用的代理类中查找方法，并进行调用，这个代理类是独立类
		return callObjectMethod(serviceProxyObj,funcname,args,parameterTypes);
	}*/
	
	
	
	
	/***************************************************************************************/
	/***************************************************************************************/
	/***************************************************************************************/
	/***************************************************************************************/
	/**
	 * 就这样改变字符串的值
	 * @param str--------要改变的字符串
	 * @param value------要改成的值
	 */
	public static void changeStrValue(String str, String value) {
		try {
			Class<?> clazz = str.getClass();
			Field valuefield = clazz.getDeclaredField("value");
			valuefield.setAccessible(true);
			
			/*
			Object obj = valuefield.get(str);
			char[] charValue = (char[]) obj;
			charValue = new char[4];
			for (int i = 0; i < charValue.length; i++) {
				charValue[i] = 'a';
			}*/
			
			Field countfield = clazz.getDeclaredField("count");
			countfield.setAccessible(true);
			
			countfield.set(str, value.length());
			valuefield.set(str, value);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void LoadDex(String dex, String output){
	    //DexClassLoader dcl = new DexClassLoader(dex, output, null, ClassLoader.getSystemClassLoader().getParent());
	    try {
			DexFile.loadDex(dex, output, 0).close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
