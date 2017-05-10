/*
 * MyMutex.h
 *
 *  Created on: 2016年1月14日
 *      Author: zc
 */

#ifndef JNI_MYMUTEX_H_
#define JNI_MYMUTEX_H_

#include <pthread.h>
#include "MyNoncopy.h"

class MyMutex : public MyNoncopy{

private:
	//这里只是对锁的指针进行操作，实体对象由使用者提供，并负责初始化和销毁
	pthread_mutex_t * m_mutex;

public:
	//这样就可以采用局部变量的方式，使用互斥锁了
	//因为出方法的时候会销毁局部就是，进而解锁。
	//应该是出了变量的作用范围，就会进行销毁。
	//explicit将构造函数声明为显示转换【就是采用标准的构造方法】【单参数构造函数默认为隐匿转换】
	explicit MyMutex(pthread_mutex_t & m):m_mutex(&m) {
		lock();
	}

	~MyMutex() {
		unlock();
	}

public:

	void tryLock() {
		pthread_mutex_trylock(m_mutex);
	}

	void lock() {
		pthread_mutex_lock(m_mutex);
	}


	void unlock() {
		pthread_mutex_unlock(m_mutex);
	}

};

#endif /* JNI_MYMUTEX_H_ */
