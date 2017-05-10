/*
 * public.h
 *
 *  Created on: 2014-4-10
 *      Author: Administrator
 */

#ifndef PUBLIC_H_
#define PUBLIC_H_

#ifdef __cplusplus
	#ifndef __STDC_CONSTANT_MACROS
		#define __STDC_CONSTANT_MACROS 1
	#endif
#endif

#include <stdint.h>

#include <stdio.h>
#include <stdlib.h>
#include <stddef.h>
#include <errno.h>

#include <jni.h>
#include <android/log.h>

#include <linux/input.h>
#include <pthread.h>

#include <fcntl.h>
#include <poll.h>

#include <sys/ptrace.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <sys/select.h>
#include <sys/time.h>
#include <sys/wait.h>
#include <sys/mman.h>
#include <sys/syscall.h>
#include <semaphore.h>

#include <linux/time.h>
#include <linux/fb.h>
#include <linux/kd.h>
#include <linux/user.h>
#include <linux/limits.h>

#include <string>
#include <iostream>
#include <exception>


#define  TAG    "ZCCL_NDK"
//#define ZCCL_DEBUG
#define __NO_USE_SDL__

#ifdef ZCCL_DEBUG
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#endif
#ifndef ZCCL_DEBUG
#define LOGD(...)
#endif

#ifdef ZCCL_DEBUG
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)
#endif
#ifndef ZCCL_DEBUG
#define LOGV(...)
#endif

#ifdef ZCCL_DEBUG
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#endif
#ifndef ZCCL_DEBUG
#define LOGI(...)
#endif

#ifdef ZCCL_DEBUG
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__)
#endif
#ifndef ZCCL_DEBUG
#define LOGW(...)
#endif

#ifdef ZCCL_DEBUG
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#endif
#ifndef ZCCL_DEBUG
#define LOGE(...)
#endif

#ifdef MY_DEBUG
#define LOG(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#else
#define LOG(...)
#endif

//计算数组的长度
#define ARRAY_LEN(x) ((int) (sizeof(x) / sizeof((x)[0])))


//真与假
#define TRUE   		1
#define FALSE   	0


//约定：成功一律返回BINGO
//失败：返回0或负值
#define BINGO   	1
#define ERROR(x)   -x


//成功与失败的字符串
#define SUCCESS  "SUCCESS"
#define FAILURE  "FAILURE"


//GNU C的一大特色就是__attribute__机制。__attribute__可以设置函数属性（Function Attribute）、变量属性（Variable Attribute）和类型属性（Type Attribute）。
//大致有六个参数值可以被设定，即：aligned, packed, transparent_union, unused, deprecated 和 may_alias 。
//4字节对齐【如果aligned 后面不紧跟一个指定的数字值，那么编译器将依据你的目标机器情况使用最大最有益的对齐方式。】
#define ALIGNED		__attribute__((packed, aligned(4)))
//取消字节对齐【packed 表示“使用最小对齐”方式，即对变量是字节对齐，对于域是位对齐。】
#define PACKED 		__attribute__((packed))




#endif /* PUBLIC_H_ */

/***************************************** 常用代码 ***********************************************/
/*
pid_t pid = vfork();
if(pid<0){
	printf("error in fork!\n");
}else if(pid == 0){
	execlp("su","su",NULL);
	printf("I am the child process,ID is %d\n",getpid());
}else{
	printf("I am the parent process,ID is %d\n",getpid());
}
*/
