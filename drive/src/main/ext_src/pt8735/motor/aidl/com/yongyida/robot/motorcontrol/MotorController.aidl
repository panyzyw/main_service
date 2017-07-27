// MotorController.aidl
package com.yongyida.robot.motorcontrol;

interface MotorController {

    void setDrvType(int drvType);       //设置驱动类型，全局变量，参数设置0则以时间方式驱动(ms),参数设置1则以距离方式驱动(cm)

    void setSpeed(int speed);           //设置马达速度，全局变量，参数范围1-100

    int forward(int arg);               //前进，左电机前进，右电机前进，参数为行走的时间(ms)/距离(cm)，0xffff表示无限时运动

    int back(int arg);                  //后退，左电机后退，右电机后退，参数为行走的时间(ms)/距离(cm)，0xffff表示无限时运动

    int left(int arg);                  //原地左转，左电机后退，右电机前进，参数为行走的时间(ms)/距离(cm)，0xffff表示无限时运动

    int right(int arg);                 //原地右转，左电机前进，右电机后退，参数为行走的时间(ms)/距离(cm)，0xffff表示无限时运动

	int turnLeft(int arg);              //向左转，左电机停止，右电机前进，参数为行走的时间(ms)/距离(cm)，0xffff表示无限时运动

	int turnRight(int arg);             //向右转，左电机前进，右电机停止，参数为行走的时间(ms)/距离(cm)，0xffff表示无限时运动

    //int backTurnLeft(int arg);          //向后左转，左电机后退，右电机停止，参数为行走的时间(ms)/距离(cm)，0xffff表示无限时运动

    //int backTurnRight(int arg);         //向后右转，左电机停止，右电机后退，参数为行走的时间(ms)/距离(cm)，0xffff表示无限时运动

    int stop();                         //底座马达停止

    int headLeft(int arg);              //头部左转，参数为转动的时间(ms)/距离(cm)

    int headRight(int arg);             //头部右转，参数为转动的时间(ms)/距离(cm)

    int headLeftEnd();                  //头部转动到最左边限位位置

    int headRightEnd();                 //头部转动到最右边限位位置

    int headLeftTurnMid();              //头部从左侧恢复到中间限位位置

    int headRightTurnMid();             //头部从右侧恢复到中间限位位置

    int headShake();                    //头部左右无限时做摇头动作

    int headUp(int arg);                //抬头动作，参数为转动的时间(ms)/距离(cm)

    int headDown(int arg);              //低头动作，参数为转动的时间(ms)/距离(cm)

    int headStop();                     //头部停止动作

    void setUpdateStatus(boolean on);  //马达硬件代码升级设置，参数：true为开，false为关

    boolean getUpdateStatus();          //获取升级状态，返回true升级中，返回false未在升级中

    void readState(int arg);            //参数3，请求取回底座马达命令是否已经完成。参数4，请求电机系统的开启状态和防跌落功能的开启状态。
	                                     //参数7，请求取回系统版本信息。参数9，请求取回头部底座马达命令。参数10，请求电机过流检测的数据包
	                                     //所有的请求后的结果均从广播中发送出来

	void setFallOn(boolean on);        //防跌落设置，参数：true为开，false为关。全局变量
}