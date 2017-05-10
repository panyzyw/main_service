//
// Created by ruiqianqi on 2016/9/18 0018.
//

#include "MyConfig.h"

#undef TAG
#define TAG "MyConfig.cpp"

/**************************************** 静态变量初始化  ******************************************/
// 这个当然是要在单例方法调用之前要初始化的
template<> MyConfig * MySingleton<MyConfig>::m_pInstance = NULL;
// 在使用单例前，要先初始化好互斥锁，因为单例方法里面有用
template<> pthread_mutex_t MySingleton<MyConfig>::singleMutex = PTHREAD_MUTEX_INITIALIZER;

/**
 * 构造方法
 */
MyConfig::MyConfig()
:mJavaVM(NULL)
,jBridgeObj(NULL)
,jBridgeID(NULL){
	// 创建线程局部变量，并关联其销毁方法
	int status = pthread_key_create(&mThreadLocalKey, cleanThreadKey_);
	if(status==0){
		LOGE("创建局部变量Key成功!");
	}
}

/**
 * 析构方法
 */
MyConfig::~MyConfig(){
	mJavaVM = NULL;
	jBridgeObj = NULL;
	jBridgeID = NULL;

	// 删了吧，虽然也没什么用
	pthread_key_delete(mThreadLocalKey);
}

/*************************************【非静态方法：属性设置】*************************************/
/**
 * 设置JAVA虚拟机指针
 */
void MyConfig::setJavaVM(JavaVM * mJavaVM){
	this->mJavaVM = mJavaVM;
}

/**
 * 返回JAVA虚拟机指针
 */
JavaVM * MyConfig::getJavaVM(){
	return mJavaVM;
}


/******************************************【公共方法】********************************************/
/**
 * 调用JAVA层的方法【该方法是可重入的，要设计成可重入的】
 * 输入：env ---- JNI环境指针
 * 		 msg ---- 要传递的字符串
 */
void MyConfig::callJavaMethod(JNIEnv* env, const char * charMsg){

	if(NULL != charMsg){
		if(env==NULL){
			env = useThreadKey(NULL);
		}
		jstring jStrMsg = env->NewStringUTF(charMsg);
		jclass jBridgeClazz = env->GetObjectClass(jBridgeObj);
		env->CallNonvirtualVoidMethod(jBridgeObj, jBridgeClazz, jBridgeID, jStrMsg);
		env->DeleteLocalRef(jBridgeClazz);
		env->DeleteLocalRef(jStrMsg);
	}
}

/**
 * 获得线程的JNI环境【显示的调用】
 * JNIEnv 和【当前线程】建立关联
 */
JNIEnv * MyConfig::useThreadKey(const char * threadName){

	if (mJavaVM==NULL){
		LOGE("JavaVM is NULL!");
	}else{
		JNIEnv * env = (JNIEnv*)pthread_getspecific(mThreadLocalKey);
		if (NULL==env){
			// 给env赋值，和当前线程建立关联
			if (mJavaVM->AttachCurrentThread(&env, NULL)!=0) {
				return NULL;
			}
			LOGE("useThreadKey success! [tid=%d]", gettid());
			pthread_setspecific(mThreadLocalKey, env);
		}
		return env;
	}
	return NULL;
}

/**
 * 清理线程体内的JAVA环境变量【显示的调用】
 * JNIEnv 断开和【当前线程】的关联
 * 输入：threadName----线程的名字
 */
void MyConfig::detachThreadEnv(const char * threadName){
	if(mJavaVM){
		mJavaVM->DetachCurrentThread();
	}
	LOGE("detachThreadEnv success! [%s][tid=%d]", threadName, gettid());
}

/**
 * 线程清理函数的方法【执行体】
 */
void MyConfig::cleanThreadKey(){
	JNIEnv * env = (JNIEnv*)pthread_getspecific(mThreadLocalKey);
	if (env != NULL) {
		pthread_setspecific(mThreadLocalKey, NULL);
	}
	LOGE("cleanThreadKey success! [tid=%d]", gettid());
}


/*************************************【静态方法】*************************************************/
/**
 * 线程清理函数的入口
 * param args----------就是线程局部变量所携带的参数
 */
void MyConfig::cleanThreadKey_(void * args){
	JNIEnv * env = (JNIEnv*)args;
	CONFIG->cleanThreadKey();
}