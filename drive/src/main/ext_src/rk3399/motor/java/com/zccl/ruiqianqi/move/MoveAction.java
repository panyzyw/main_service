package com.zccl.ruiqianqi.move;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.yongyida.robot.motorcontrol.MotorController;
import com.yongyida.robot.movecontrol.MoveController;

import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by ruiqianqi on 2017/1/17 0017.
 */

public class MoveAction {

    // 类日志标志
    public static String TAG = MoveAction.class.getSimpleName();
    // 单例引用
    private static MoveAction instance;

    // 通过时间驱动【时间/距离】
    public static final int DRIVE_BY_TIME = 0;
    // 通过距离驱动【时间/距离】
    public static final int DRIVE_BY_DISTANCE = 1;
    // 通过深蓝驱动
    public static final int DRIVE_BY_SLAM = 2;

    // 全局上下文
    private Context mContext;
    // 机器人移动类
    private MotorController mMotorService;
    // 深蓝驱动类
    private MoveController mMoveService;

    // 绑定服务与请求方法的同步
    private CountDownLatch countDownLatch;
    // 默认为时间驱动
    private int mDriveType = 0;

    /**
     * 连接远程服务
     */
    public ServiceConnection motorConn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMotorService = MotorController.Stub.asInterface(service);

            if(0 != countDownLatch.getCount()) {
                countDownLatch.countDown();
            }

            try {
                setDriveType(DRIVE_BY_TIME);
                setSpeed(50);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMotorService = null;
            countDownLatch = new CountDownLatch(1);
        }

    };
    public ServiceConnection moveConn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMoveService = MoveController.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMoveService = null;
        }

    };

    /**
     * 构造方法
     */
    private MoveAction(Context context){
        if(null != context){
            this.mContext = context.getApplicationContext();
        }
        init();
    }

    /**
     * 用这个用话，instance不需要用volatile修饰
     *
     * @return
     */
    public static MoveAction getInstance(Context context) {
        if (instance == null) {
            synchronized (MoveAction.class) {
                MoveAction temp = instance;
                if (temp == null) {
                    temp = new MoveAction(context);
                    instance = temp;
                }
            }
        }
        if(null != instance && null==instance.mContext && null != context){
            instance.mContext = context.getApplicationContext();
        }
        return instance;
    }

    /**
     * 初始化
     */
    private void init(){
        countDownLatch = new CountDownLatch(1);
        bindMotorService();
        bindMoveService() ;
    }

    /**********************************************************************************************/
    /**
     * 绑定服务，这个过程竟然要10多秒
     */
    private void bindMotorService(){
        if(null != mContext && null == mMotorService) {
            Intent intent = new Intent();
            intent.setAction("com.yongyida.robot.MotorService");
            intent.setPackage("com.yongyida.robot.uartcontrol");
            mContext.bindService(intent, motorConn, Context.BIND_AUTO_CREATE);
        }
    }


    private void bindMoveService(){
        if(null != mContext && null == mMoveService) {
            Intent intent = new Intent();
            intent.setAction("com.yongyida.robot.MoveService");
//            intent.setPackage("com.example.administrator.slamconnecttest");
            intent.setPackage("com.yongyida.robot.slamconnectcontrol");
            mContext.bindService(intent, moveConn, Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * 解绑服务
     */
    public void unbindMotorService(){
        if(null != mContext && null != mMotorService) {
            mContext.unbindService(motorConn);
        }
    }

    /************************************【各种动作设置方法】**************************************/
    /**
     * 设置驱动方式
     * {@link MoveAction#DRIVE_BY_TIME}    【毫秒】
     * {@link MoveAction#DRIVE_BY_DISTANCE}【厘米】
     *
     * @param type
     */
    public void setDriveType(int type) throws RemoteException {
        this.mDriveType = type;
        bindMotorService();
        if(null != mMotorService){
            mMotorService.setDrvType(type);
        }
    }

    /**
     * 设置速度【1~100】
     * @param speed
     */
    public void setSpeed(int speed) throws RemoteException {
        if(mDriveType == DRIVE_BY_SLAM){
            return;
        }
        bindMotorService();
        if(null != mMotorService){
            if(speed < 1 || speed > 100){
                String errMsg = "";
                if(null != mContext) {
                    errMsg = mContext.getString(R.string.speed_out_of_range);
                }
                Log.e(TAG, errMsg);
                return;
            }
            mMotorService.setSpeed(speed);
        }
    }

    /****************************************【前进、后退】****************************************/
    /**
     * 设置向前走的【时间/距离】，左右马达前进
     * @param time_distance
     */
    public void forward(int time_distance) throws RemoteException {
        if(mDriveType == DRIVE_BY_SLAM){
            forward();
        }else {
            bindMotorService();
            if (null != mMotorService) {
                mMotorService.forward(time_distance);
            }
        }
    }

    /**
     * SLAM驱动
     * @throws RemoteException
     */
    private void forward() throws RemoteException {
        bindMoveService();
        if(null != mMoveService){
            mMoveService.forward();
        }
    }


    /**
     * 设置后退的【时间/距离】，左右马达后退
     * @param time_distance
     */
    public void back(int time_distance) throws RemoteException {
        if(mDriveType == DRIVE_BY_SLAM){
            back();
        }else {
            bindMotorService();
            if (null != mMotorService) {
                mMotorService.back(time_distance);
            }
        }
    }

    /**
     * SLAM驱动
     * @throws RemoteException
     */
    private void back() throws RemoteException {
        bindMoveService();
        if(null != mMoveService){
            mMoveService.back();
        }
    }



    /****************************************【转小圈】********************************************/
    /**
     * 设置左转的【时间/距离】，左电机后退，右电机前进
     * @param time_distance
     */
    public void left(int time_distance) throws RemoteException {
        if(mDriveType == DRIVE_BY_SLAM){
            left();
        }else {
            bindMotorService();
            if (null != mMotorService) {
                mMotorService.left(time_distance);
            }
        }
    }

    /**
     * SLAM驱动
     * @throws RemoteException
     */
    private void left() throws RemoteException {
        bindMoveService();
        if(null != mMoveService){
            mMoveService.left();
        }
    }

    /**
     * 设置右转的【时间/距离】，右电机前进，左电机后退
     * @param time_distance
     */
    public void right(int time_distance) throws RemoteException {
        if(mDriveType == DRIVE_BY_SLAM){
            right();
        }else {
            bindMotorService();
            if (null != mMotorService) {
                mMotorService.right(time_distance);
            }
        }
    }

    /**
     * SLAM驱动
     * @throws RemoteException
     */
    private void right() throws RemoteException {
        bindMoveService();
        if(null != mMoveService){
            mMoveService.right();
        }
    }

    /**
     * 定点播放
     * @param position
     */
    public void point(int position) {
        bindMoveService();
        if(null != mMoveService){
            try {
                mMoveService.point(position);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 介绍结束
     * @param position
     */
    public void introduceEnd(int position) {
        bindMoveService();
        if(null != mMoveService){
            try {
                mMoveService.introduceEnd(position);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }



    /*************************************【马达停止运动】*****************************************/
    /**
     * 停止，左右电机不动
     */
    public void stop() throws RemoteException {
        bindMotorService();
        if(null != mMotorService){
            mMotorService.stop();
        }
    }

    /*****************************************【转大圈】*******************************************/
    /**
     * 设置向左转的【时间/距离】，左电机静止，右电机前进
     * @param time_distance
     */
    public void turnLeft(int time_distance) throws RemoteException {
        bindMotorService();
        if(null != mMotorService){
            mMotorService.turnLeft(time_distance);
        }
    }

    /**
     * 设置向右转的【时间/距离】，右电机静止，左电机前进
     * @param time_distance
     */
    public void turnRight(int time_distance) throws RemoteException {
        bindMotorService();
        if(null != mMotorService){
            mMotorService.turnRight(time_distance);
        }
    }

    /**
     * 【8735独有】
     * 设置反向左转的【时间/距离】，左电机后退，右电机静止
     * @param time_distance
     */
    public void reverseLeft(int time_distance) throws RemoteException {

    }

    /**
     * 【8735独有】
     * 设置反向右转的【时间/距离】，右电机后退，左电机静止
     * @param time_distance
     */
    public void reverseRight(int time_distance) throws RemoteException {

    }

    /***************************************【头部操作】*******************************************/
    /**
     * 头向左转
     * @param time_distance
     * @throws RemoteException
     */
    public void headLeft(int time_distance) throws RemoteException {
        bindMotorService();
        if(null != mMotorService){
            mMotorService.headLeft(time_distance);
        }
    }

    /**
     * 头向右转
     * @param time_distance
     * @throws RemoteException
     */
    public void headRight(int time_distance) throws RemoteException {
        bindMotorService();
        if(null != mMotorService){
            mMotorService.headRight(time_distance);
        }
    }

    /**
     * 抬起头
     * @param time_distance
     * @throws RemoteException
     */
    public void headUp(int time_distance) throws RemoteException {
        bindMotorService();
        if(null != mMotorService){
            mMotorService.headUp(time_distance);
        }
    }

    /**
     * 低下头
     * @param time_distance
     * @throws RemoteException
     */
    public void headDown(int time_distance) throws RemoteException {
        bindMotorService();
        if(null != mMotorService){
            mMotorService.headDown(time_distance);
        }
    }

    /**
     * 【8163独有】
     * 头部转向最左边
     * @throws RemoteException
     */
    public void headLeftEnd() throws RemoteException {
        bindMotorService();
        if(null != mMotorService){
            mMotorService.headLeftEnd();
        }
    }

    /**
     * 【8163独有】
     * 头总转向最右边
     * @throws RemoteException
     */
    public void headRightEnd() throws RemoteException {
        bindMotorService();
        if(null != mMotorService){
            mMotorService.headRightEnd();
        }
    }

    /**
     * 【8163独有】
     * 头从左边转到中间
     * @throws RemoteException
     */
    public void headLeftTurnMid() throws RemoteException {
        bindMotorService();
        if(null != mMotorService){
            mMotorService.headLeftTurnMid();
        }
    }

    /**
     * 【8163独有】
     * 头从右边转到中间
     * @throws RemoteException
     */
    public void headRightTurnMid() throws RemoteException {
        bindMotorService();
        if(null != mMotorService){
            mMotorService.headRightTurnMid();
        }
    }

    /**
     * 【8163独有】
     * 左右一直摇头
     * @throws RemoteException
     */
    public void headShake() throws RemoteException {
        bindMotorService();
        if(null != mMotorService){
            mMotorService.headShake();
        }
    }

    /**
     * 停止头部运动
     * @throws RemoteException
     */
    public void headStop() throws RemoteException {
        bindMotorService();
        if(null != mMotorService){
            mMotorService.headStop();
        }
    }

    /***************************************【其他接口】*******************************************/
    /**
     * 【8163独有】
     * 马达硬件代码升级设置
     * @param updateStatus true为开，false为关
     */
    public void setUpdateStatus(boolean updateStatus) throws RemoteException {
        bindMotorService();
        if(null != mMotorService){
            mMotorService.setUpdateStatus(updateStatus);
        }
    }

    /**
     * 【8163独有】
     * 获取马达硬件代码升级状态
     * @return true正在升级中，false未在升级中
     */
    public boolean getUpdateStatus() throws RemoteException {
        bindMotorService();
        if(null != mMotorService){
            return mMotorService.getUpdateStatus();
        }
        return false;
    }

    /**
     * 【8163独有】
     * 参数3，请求取回底座马达命令是否已经完成。
     * 参数4，请求电机系统的开启状态和防跌落功能的开启状态。
     * 参数7，请求取回系统版本信息。
     * 参数9，请求取回头部底座马达命令。
     * 参数10，请求电机过流检测的数据包
     * 所有的请求后的结果均从广播中发送出来
     * @param state
     * @throws RemoteException
     */
    public void readState(int state) throws RemoteException{
        bindMotorService();
        if(null != mMotorService){
            mMotorService.readState(state);
        }
    }

    /**
     * 防跌落设置
     * @param isFallOn true为开，false为关
     */
    public void setFallOn(boolean isFallOn) throws RemoteException {
        bindMotorService();
        if(null != mMotorService){
            mMotorService.setFallOn(isFallOn);
        }
    }
}
