/*
 * MySingleton.h
 *
 *  Created on: 2016年1月15日
 *      Author: zc
 */

#ifndef JNI_MYSINGLETON_H_
#define JNI_MYSINGLETON_H_

#include <pthread.h>
#include "MyMutex.h"
#include "MyNoncopy.h"

template<typename T>
class MySingleton: public MyNoncopy{

	//静态成员
private:
    //创建单例时使用的互斥锁
    static pthread_mutex_t singleMutex;
	static T* m_pInstance;

	//静态方法
public:
    static T* getInstance(){
    	if(m_pInstance == NULL){

			//double check
			//用lock实现线程安全，用资源管理类，实现异常安全
			MyMutex lock(singleMutex);

			//使用资源管理类，在抛出异常的时候，资源管理类对象会被析构，析构总是发生的无论是因为异常抛出还是语句块结束。
			if(m_pInstance == NULL){
				m_pInstance = new T();
			}

		}
		return m_pInstance;
    }

    static T& Instance(){
		static T me;
		return me;
	}

protected:
	MySingleton(){

	}

	virtual ~MySingleton(){

	}


private:
	class MyGarbo {// 它的唯一工作就是在析构函数中删除 MySingleton 的实例
	public:
		~MyGarbo() {
			if (MySingleton::m_pInstance) {
				delete MySingleton::m_pInstance;
				MySingleton::m_pInstance = NULL;
			}
		}
	};
	//利用程序在结束时析构全局变量的特性，选择最终的释放时机；
	//使用单例的代码不需要任何操作，不必关心对象的释放。
	//定义一个静态成员，在程序结束时，系统会调用它的析构函数
	static MyGarbo myGarbo;

};



#endif /* JNI_MYSINGLETON_H_ */
