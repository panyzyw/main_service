//
// Created by ruiqianqi on 2016/12/5 0005.
//

#ifndef RUIQIANQIAPP_INPUTEVENT_H
#define RUIQIANQIAPP_INPUTEVENT_H

#include "public.h"
#include "MyNoncopy.h"

/**************** TYPE的定义  *****************************/
//#define EV_SYN 			0x00 //同步事件（非常常用）
//#define EV_KEY 			0x01 //按键（常用）
//#define EV_REL 			0x02 //轨迹球
//#define EV_ABS 			0x03 //触摸屏（非常常用）
//#define EV_MSC 			0x04 //杂项设备
//#define EV_SW  			0x05 //耳机
//#define EV_LED          0x11
//#define EV_SND          0x12
//#define EV_REP          0x14
//#define EV_FF           0x15
//#define EV_PWR          0x16
//#define EV_FF_STATUS    0x17
//#define EV_MAX          0x1f
//#define EV_CNT          (EV_MAX+1)

/**************** CODE的定义 ***************************************/
/*
 * 同步事件CODE
 */
//#define SYN_REPORT      0 //单点同步
//#define SYN_CONFIG      1
#define SYN_MT_REPORT   2 //多点同步

/*
 * 触摸屏的CODE
 */
//#define ABS_X           0x00
//#define ABS_Y           0x01
//#define ABS_Z           0x02
//#define ABS_RX          0x03
//#define ABS_RY          0x04
//#define ABS_RZ          0x05
//#define ABS_THROTTLE    0x06
//#define ABS_RUDDER      0x07
//#define ABS_WHEEL       0x08
//#define ABS_GAS         0x09
//#define ABS_BRAKE       0x0a
//#define ABS_HAT0X       0x10
//#define ABS_HAT0Y       0x11
//#define ABS_HAT1X       0x12
//#define ABS_HAT1Y       0x13
//#define ABS_HAT2X       0x14
//#define ABS_HAT2Y       0x15
//#define ABS_HAT3X       0x16
//#define ABS_HAT3Y       0x17
//#define ABS_PRESSURE    0x18
//#define ABS_DISTANCE    0x19
//#define ABS_TILT_X      0x1a
//#define ABS_TILT_Y      0x1b
//#define ABS_TOOL_WIDTH  0x1c
//#define ABS_VOLUME      0x20
//#define ABS_MISC        0x28
#define ABS_MT_SLOT  		0x2F    /* Major axis of touching ellipse */
//描述了主接触面的长轴
#define ABS_MT_TOUCH_MAJOR  0x30    /* Major axis of touching ellipse */
//描述了接触面的短轴，如果接触面是圆形，它可以不用
#define ABS_MT_TOUCH_MINOR  0x31    /* Minor axis (omit if circular) */
//描述了接触工具的长轴
#define ABS_MT_WIDTH_MAJOR  0x32    /* Major axis of approaching ellipse */
//描述了接触工具的短轴
#define ABS_MT_WIDTH_MINOR  0x33    /* Minor axis (omit if circular) */
#define ABS_MT_ORIENTATION  0x34    /* Ellipse orientation */
#define ABS_MT_POSITION_X   0x35    /* Center X ellipse position */
#define ABS_MT_POSITION_Y   0x36    /* Center Y ellipse position */
#define ABS_MT_TOOL_TYPE    0x37    /* Type of touching device */
#define ABS_MT_BLOB_ID      0x38    /* Group a set of packets as a blob */
#define ABS_MT_TRACKING_ID  0x39    /* Unique ID of initiated contact */
//（ABS_MT_TOUCH_MAJOR / ABS_MT_WIDTH_MAJOR）的比例可以用来描述压力
#define ABS_MT_PRESSURE     0x3a    /* Pressure on contact area */
#define ABS_MT_DISTANCE     0x3b    /* Contact hover distance */
#define ABS_MAX         	0x3f
#define ABS_CNT         	(ABS_MAX+1)

using namespace std;

/**
 *  A a0;            // 形式一：直接声明一个对象
 *  A a1(1);         // 形式二：隐式调用A带一个参数的构造函数
 *  A a2 = A(2);     // 形式三：显式调用A带一个参数构造函数
 *  A *p = new A(3); // 形式四：动态分配
    形式一：实际上等同于  A a0 = A();调用不带参数的构造函数进行对象的创建
    形式二：实际上等同于  A a1 = A(1);调用带一个参数的构造函数进行对象的创建
    形式三：与形式二相同，这三种形式其实都是按照参数调用对应的构造函数在栈中创建对象，使用完毕后，系统自动回收对象内存，无需手动释放。
    形式四：在堆内存中动态开辟空间创建对象，需要手动释放内存。
    还有一点需要注意“A a3();”编译和运行都没有问题，但是并没有创建对象
    所有的构造函数都要加上【explicit】去掉隐式转换
 */
class InputEvent: public MyNoncopy{

protected:
    explicit InputEvent();
    virtual ~InputEvent();

private:
    struct input_event event;
    // 互斥锁
    static pthread_mutex_t delayMutex;
    // 条件变量
    static pthread_cond_t  delayCond;

    struct timeval startTime;
    struct timeval nowTime;
    struct timeval restTime;
    struct timeval delayTime;
    struct timespec nano;
protected:
    string touchInput;
    int touchInputFd;

public:
    void openTouchInput(string touchInput);
    void writeEvent(int type, int code, int value);
    void delayCondition(unsigned long millisecond);
    void activeDelay();
    void destroyDelay();
    void uSleeping(unsigned long uSecond);
    void delaySelect(long second,long uSecond);
    void delayNano(long second,long nSecond);

public:
    static void handlerDelay();
};


#endif //RUIQIANQIAPP_INPUTEVENT_H
