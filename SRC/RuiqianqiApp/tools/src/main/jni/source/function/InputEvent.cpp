//
// Created by ruiqianqi on 2016/12/5 0005.
//
/**
 * 引用总结
（1）在引用的使用中，单纯给某个变量取个别名是毫无意义的，引用的目的主要用于在函数参数传递中，解决大块数据或对象的传递效率和空间不如意的问题。
（2）用引用传递函数的参数，能保证参数传递中不产生副本，提高传递的效率，且通过const的使用，保证了引用传递的安全性。
（3）引用与指针的区别是，指针通过某个指针变量指向一个对象后，对它所指向的变量间接操作。程序中使用指针，程序的可读性差；而引用本身就是目标变量的别名，对引用的操作就是对目标变量的操作。
（4）使用引用的时机。流操作符<<和>>、赋值操作符=的返回值、拷贝构造函数的参数、赋值操作符=的参数、其它情况都推荐使用引用。
 */

#include "InputEvent.h"

#undef TAG
#define TAG "InputEvent.cpp"

// 静态变量初始化
pthread_mutex_t InputEvent::delayMutex = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t InputEvent::delayCond = PTHREAD_COND_INITIALIZER;

InputEvent::InputEvent(){
    touchInputFd = -1;
}

InputEvent::~InputEvent(){
    destroyDelay();
}

/**
 * 打开屏幕输入设备
 */
void InputEvent::openTouchInput(string touchInput){
    this->touchInput = touchInput;
    try {
        touchInputFd = open(this->touchInput.c_str(), O_RDWR);
    }catch (exception& e){
        LOGE("%s\n", e.what());
    }
}

/**
 * 事件写入函数
 */
void InputEvent::writeEvent(int type, int code, int value){
    event.type=type;
    event.code=code;
    event.value=value;
    if(touchInputFd > 0){
        if(write(touchInputFd, &event, sizeof(event)) != sizeof(event)) {
            LOGD("write error type=%d,code=%d,value=%d\n", type, code, value);
        }
    }
}


/**
 * 条件激活
 */
void InputEvent::activeDelay(){
    //激发条件有两种形式，
    //pthread_cond_signal() [[激活]] 一个等待该条件的线程，存在多个等待线程时按入队顺序激活其中一个；
    //pthread_cond_broadcast()则 [[激活]] 所有等待线程。
    pthread_mutex_lock(&delayMutex);
    //pthread_cond_signal(&delayCond);
    pthread_cond_broadcast(&delayCond);
    pthread_mutex_unlock(&delayMutex);
}


/**
 * 条件销毁
 */
void InputEvent::destroyDelay(){
    pthread_cond_destroy(&delayCond);
    pthread_mutex_destroy(&delayMutex);
}


/**
 * 回调保护函数
 */
void InputEvent::handlerDelay(){
    pthread_mutex_unlock(&delayMutex);
}

/***
 * 线程延时函数系统时钟计数（毫秒级别）
 * 回调函数保护，等待条件前锁定，pthread_cond_wait()返回后解锁
 */
void InputEvent::delayCondition(unsigned long millisecond){
    struct timeval  nowTime;
    struct timespec outTime;
    int s=0, us=0;
    //开始延时
    pthread_cleanup_push(handlerDelay, &delayMutex);
    pthread_mutex_lock(&delayMutex);
    do {
        if(millisecond >= 1000){
            s = (int)(millisecond / 1000);
        }
        us = (millisecond % 1000) * 1000;

        // 当前时间
        gettimeofday(&nowTime, NULL);

        outTime.tv_sec = nowTime.tv_sec + s;
        // 纳秒级别
        outTime.tv_nsec =(nowTime.tv_usec + us)*1000;
        // 计时等待(相对于现在的绝对时间)
        pthread_cond_timedwait(&delayCond, &delayMutex, &outTime);

        // 无条件等待
        //pthread_cond_wait(&delayCond, &delayMutex);

    }while(0);
    pthread_mutex_unlock(&delayMutex);
    pthread_cleanup_pop(0);
}


/**
 * 微秒级别的延时（微秒级别）
 */
void InputEvent::uSleeping(unsigned long uSecond){
    gettimeofday(&startTime, NULL);
    while(1){
        gettimeofday(&nowTime, NULL);
        timersub(&nowTime, &startTime, &restTime);
        long subRest = (restTime.tv_sec * 1000000 + restTime.tv_usec);
        if(subRest >= uSecond){
            return;
        }
    }
}


/**
 * 线程延时函数CMOS时钟计数（微秒级别）
 * 代码启动都要花100us左右，这怎么算
 */
void InputEvent::delaySelect(long second, long uSecond){
    delayTime.tv_sec = second;
    delayTime.tv_usec = uSecond;
    select(0, NULL, NULL, NULL, &delayTime);
}

/**
 * 线程延时函数CMOS时钟计数（纳秒级别）
 */
void InputEvent::delayNano(long second, long nSecond){
    nano.tv_sec = second;
    nano.tv_nsec = nSecond;
    nanosleep(&nano, NULL);
}
