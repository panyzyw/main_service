//
// Created by ruiqianqi on 2016/9/18 0018.
//


#ifndef JNI_MYCONFIG_H_
#define JNI_MYCONFIG_H_

#include "public.h"
#include "MySingleton.h"

/**
 * 我算是发现了，静态变量的初始化只能在实现文件里面
 *
 * 底层程序入口及出口
 * 1. 声明引用时，必须同时对其进行初始化。
 * 2. 不能建立数组的引用。因为数组是一个由若干个元素所组成的集合，所以无法建立一个数组的别名。
 *
 * 引用：
 *	MyClass& x = func();
 *	这个只是将引用指向func()返回的变量。
 *
 * 对象：
 *	分两种情况：
 *	MyClass x = func();
 *	这种情况调用的是MyClass的拷贝构造函数给x赋的值。
 *	MyClass x;
 * 	x = func();
 *	这种情况调用的是MyClass的operator=给x赋的值。
 */
class MyConfig : public MySingleton<MyConfig>{

public:
	MyConfig();
	virtual ~MyConfig();

public:
	/**
     * JAVA虚拟机指针
     * 整个JNI环境都能用,所有线程共享这个变量
     * java子线程调用C的时候会传入JNI环境
     * C层的子线程调用java的时候要用这个创建一个JNI环境【GetEnv】【AttachCurrentThread】
     */
    JavaVM * mJavaVM = NULL;

    /**
     * 线程本地变量所用的key
     */
    pthread_key_t mThreadLocalKey;

    /**
     * 与JAVA层交互对象
     */
    jobject jBridgeObj = NULL;

    /**
     * JAVA对象方法的ID
     */
    jmethodID jBridgeID = NULL;

public:

	/**
	 * 调用JAVA层的方法【该方法是可重入的，要设计成可重入的】
	 * 输入：
	 * 		env ---- JNI环境指针
	 * 		msg ---- 要传递的字符串
	 */
	void callJavaMethod(JNIEnv* env, const char * charMsg);


    /**
     * 使用线程独有变量
     * 输入：threadName----线程的名字
     */
    JNIEnv * useThreadKey(const char * threadName);

    /**
     * 清理线程体内的JAVA环境变量
     * 输入：threadName----线程的名字
     */
    void detachThreadEnv(const char * threadName);

    /**
     * 线程局部变量清理函数的方法
     */
    void cleanThreadKey();

    /**
     * 线程局部变量清理函数的入口
     */
    static void cleanThreadKey_(void * args);

public:
	/** 设置JAVA虚拟机指针 */
	void setJavaVM(JavaVM * mJavaVM);

	/** 返回JAVA虚拟机指针 */
	JavaVM * getJavaVM();


};

//定义单例 MAIN 方法
#define CONFIG MySingleton<MyConfig>::getInstance()

#endif //JNI_MYCONFIG_H_
