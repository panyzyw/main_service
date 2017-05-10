/*
 * main.cpp
 *
 *  Created on: 2015年11月11日
 *      Author: zc
 */

#include "function.h"
#include "public.h"
#include "MyConfig.h"

#undef TAG
#define TAG "main.cpp"

#ifdef __cplusplus
extern "C"{
#endif


/*******************************************************************************************/

/** 本地方法对应的JAVA层的类 */
static const char * classPathName = "com/zccl/ruiqianqi/tools/jni/NdkTools";

static void bridgeInit_JNI(JNIEnv* env, jobject jObj, jobject jBridgeObj, jstring jMethodName);
static jstring getABI_JNI(JNIEnv * env, jobject jObj);
static int reset5Mic_JNI(JNIEnv * env, jobject jObj);
static void print_JNI(JNIEnv * env, jobject jObj);

/**
JAVA层与C层方法关联:
Signature　　Java中的类型
Z　　　　　　　boolean
B　　　　　　　byte
C　　　　　　　char
S　　　　　　　short
I　　　　　　　 int
J　　　　　　　 long
F　　　　　　　float
D　　　　　　　double
Lfully-qualified-class;　　 fully-qualified-class
[type　　	   				  type[]
(arg-types)ret-type　　	  method type
一个Java类的方法的Signature可以通过javap命令获取：
javap -s -p Java类名
*/
static JNINativeMethod methods[] = {
		{"bridgeInit", "(Lcom/zccl/ruiqianqi/tools/jni/NdkBridge;Ljava/lang/String;)V", (void*)bridgeInit_JNI},
        {"getABI", "()Ljava/lang/String;", (void*)getABI_JNI},
        {"reset5Mic", "()I", (void*)reset5Mic_JNI},
        {"print", "()V", (void*)print_JNI},
};


/***************************************【JNI方法】******************************************************/
/**
 * 初始化交互接口, 程序启动之后，只运行一次的玩意【目的只是构造上下层沟通的桥梁】
 * 上层的JAVA对象是个单例，所以这个方法所操作的对象是一直存在的
 *
 * param jObj---------------建立桥梁的对象
 * param jMethodName--------对象中使用的方法的名字
 */
void bridgeInit_JNI(JNIEnv* env, jobject jObj, jobject jBridgeObj, jstring jMethodName){

    const char * methodName = env->GetStringUTFChars(jMethodName, NULL);
	env->DeleteLocalRef(jMethodName);

	jclass jBridgeClazz = env->GetObjectClass(jBridgeObj);
	// 构造一个全局引用
	CONFIG->jBridgeObj = env->NewGlobalRef(jBridgeObj);
	// 拿到JAVA层对象的ID
	CONFIG->jBridgeID = env->GetMethodID(jBridgeClazz, methodName, "(Ljava/lang/String;)V");

    free((char *)methodName);
	env->DeleteLocalRef(jBridgeClazz);
}

/**
 * 返回底层接口类型
 */
jstring getABI_JNI(JNIEnv* env, jobject jObj){
    const char * ABI = getABI();
    return env->NewStringUTF(ABI);
}

/**
 * 检测五麦能否重置
 * @return 1:sucess 0:fail
 */
int reset5Mic_JNI(JNIEnv * env, jobject jObj){
    return reset5Mic();
}

/**
 * 打印点东西
 */
void print_JNI(JNIEnv* env, jobject jObj){
    return printMsg();
}


/*************************************************************************************************/

/** 进程只执行一次的变量设置 */
static pthread_once_t once_block = PTHREAD_ONCE_INIT;

/**
 * 【进程只执行一次的方法】
 * 【这里我要进行基本的程序运行配置】
 * 【好吧，SO一加载就执行这个方法了】
 * 【JNI_OnLoad中被调用的】
 */
static void nativeInitOnce(){
    CONFIG->setJavaVM(NULL);
}

/**
 *
 * 通过env获取JVM
 * env->GetJavaVM(&globalJvm);
 *
 * 通过JVM获取env
 * jvm->GetEnv((void**) &env, JNI_VERSION_1_6)
 * jvm->AttachCurrentThread((JNIEnv**) &env, NULL)
 *
 * The JNIEnv provides most of the JNI functions.
 * Your native functions all receive a JNIEnv as the first argument.
 *
 * The JNIEnv is used for thread-local storage.
 * For this reason, you cannot share a JNIEnv between threads.
 *
 * If a piece of code has no other way to get its JNIEnv, you should share the JavaVM,
 * and use GetEnv to discover the thread's JNIEnv.
 *  (Assuming it has one; see AttachCurrentThread below.)
 *
 */
jint JNI_OnLoad(JavaVM* jvm, void* reserved){

	JNIEnv * mainEnv = NULL;
    // 获取JNI环境对象
    if (jvm->GetEnv((void**) &mainEnv, JNI_VERSION_1_6) != JNI_OK) {
        LOGE("ERROR: GetEnv failed");
        return JNI_ERR;
    }

    // 定义一个静态变量
    jclass clazz = NULL;

    // 5.0新版本已经不允许全局使用findclass出来的局部引用了，会被GC回收掉。所以必须new成全局引用才行
    // 不过这里我也没有全局引用它.
    jclass tmp = mainEnv->FindClass(classPathName);

    // 新建一个全局的class类型引用
    clazz = (jclass)mainEnv->NewGlobalRef(tmp);

    // 删除本地引用
    mainEnv->DeleteLocalRef(tmp);

    if (clazz == NULL) {
        LOGE("ERROR: Native registration unable to find class '%s'", classPathName);
        return JNI_ERR;
    }

    // 注册本地native方法
    if(mainEnv->RegisterNatives(clazz, methods, ARRAY_LEN(methods)) < 0) {
        LOGE("ERROR: NativeMethods registration failed");
        return JNI_ERR;
    }

    // 删除全局引用
    mainEnv->DeleteGlobalRef(clazz);

    // 只执行一次的任务
    int status = pthread_once(&once_block, nativeInitOnce);
    if(status==0){
    	LOGE("进程一次性任务执行成功！");
    }else{
    	LOGE("进程一次性任务执行失败！");
    }

    // 设置Java虚拟机
    CONFIG->setJavaVM(jvm);

    return JNI_VERSION_1_6;
}

/**
 * JNI环境出口
 */
void JNI_OnUnload(JavaVM* jvm, void* reserved){

}

#ifdef __cplusplus
}
#endif













