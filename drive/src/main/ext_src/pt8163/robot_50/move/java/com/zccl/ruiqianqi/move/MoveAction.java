package com.zccl.ruiqianqi.move;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.yongyida.robot.motorcontrol.MotorController;

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
    // 绑定马达服务所需要的时间
    public static final int BIND_MOVE_TIME = 3;

    // 全局上下文
    private Context mContext;
    // 机器人移动类
    private MotorController mMotorController;

    // 绑定服务与请求方法的同步
    private CountDownLatch countDownLatch;

    /**
     * 连接远程服务
     */
    public ServiceConnection motorService = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMotorController = MotorController.Stub.asInterface(service);

            if(0 != countDownLatch.getCount()) {
                countDownLatch.countDown();
            }

            try {
                setDriveType(DRIVE_BY_TIME);
                setSpeed(64);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMotorController = null;
            countDownLatch = new CountDownLatch(1);
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
        bindMove();
    }

    /**********************************************************************************************/
    /**
     * 绑定服务，这个过程竟然要10多秒
     */
    private void bindMove(){
        if(null != mContext) {
            Intent intent = new Intent();
            intent.setAction("com.yongyida.robot.MotorService");
            intent.setPackage("com.yongyida.robot.motorcontrol");
            mContext.bindService(intent, motorService, Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * 解绑服务
     */
    public void unbindMove(){
        if(null != mContext) {
            mContext.unbindService(motorService);
        }
    }

    /**
     * 加载运动服务
     */
    private void loadService(){
        if(null == mMotorController){
            bindMove();
            boolean timeOut = false;
            try {
                timeOut = countDownLatch.await(BIND_MOVE_TIME, SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(timeOut){
                String errMsg = "";
                if(null != mContext) {
                    errMsg = mContext.getString(R.string.load_move_service_time_out);
                }
                Log.e(TAG, errMsg);
            }
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
        loadService();
        if(null != mMotorController){
            mMotorController.setDrvType(type);
        }
    }

    /**
     * 设置速度【1~100】
     * @param speed
     */
    public void setSpeed(int speed) throws RemoteException {
        loadService();
        if(null != mMotorController){
            if(speed < 1 || speed > 100){
                String errMsg = "";
                if(null != mContext) {
                    errMsg = mContext.getString(R.string.speed_out_of_range);
                }
                Log.e(TAG, errMsg);
                return;
            }
            mMotorController.setSpeed(speed);
        }
    }

    /****************************************【前进、后退】****************************************/
    /**
     * 设置向前走的【时间/距离】，左右马达前进
     * @param time_distance
     */
    public void forward(int time_distance) throws RemoteException {
        loadService();
        if(null != mMotorController){
            mMotorController.forward(time_distance);
        }
    }

    /**
     * 设置后退的【时间/距离】，左右马达后退
     * @param time_distance
     */
    public void back(int time_distance) throws RemoteException {
        loadService();
        if(null != mMotorController){
            mMotorController.back(time_distance);
        }
    }

    /****************************************【转小圈】********************************************/
    /**
     * 设置左转的【时间/距离】，左电机后退，右电机前进
     * @param time_distance
     */
    public void left(int time_distance) throws RemoteException {
        loadService();
        if(null != mMotorController){
            mMotorController.left(time_distance);
        }
    }

    /**
     * 设置右转的【时间/距离】，右电机前进，左电机后退
     * @param time_distance
     */
    public void right(int time_distance) throws RemoteException {
        loadService();
        if(null != mMotorController){
            mMotorController.right(time_distance);
        }
    }

    /*************************************【马达停止运动】*****************************************/
    /**
     * 停止，左右电机不动
     */
    public void stop() throws RemoteException {
        loadService();
        if(null != mMotorController){
            mMotorController.stop();
        }
    }

    /*****************************************【转大圈】*******************************************/
    /**
     * 设置向左转的【时间/距离】，左电机静止，右电机前进
     * @param time_distance
     */
    public void turnLeft(int time_distance) throws RemoteException {
        loadService();
        if(null != mMotorController){
            mMotorController.turnLeft(time_distance);
        }
    }

    /**
     * 设置向右转的【时间/距离】，右电机静止，左电机前进
     * @param time_distance
     */
    public void turnRight(int time_distance) throws RemoteException {
        loadService();
        if(null != mMotorController){
            mMotorController.turnRight(time_distance);
        }
    }

    /**
     * 设置反向左转的【时间/距离】，左电机后退，右电机静止
     * @param time_distance
     */
    public void reverseLeft(int time_distance) throws RemoteException {

    }

    /**
     * 设置反向右转的【时间/距离】，右电机后退，左电机静止
     * @param time_distance
     */
    public void reverseRight(int time_distance) throws RemoteException {

    }

    /***************************************【其他接口】*******************************************/
    /**
     * 防跌落设置
     * @param isFallOn true为开，false为关
     */
    public void setFallOn(boolean isFallOn) throws RemoteException {
        /*
        loadService();
        if(null != mMotorController){
            mMotorController.setFallOn(isFallOn);
        }
        */
    }

    /**
     * 马达硬件代码升级设置
     * @param updateStatus true为开，false为关
     */
    public void setUpdateStatus(boolean updateStatus) throws RemoteException {
        /*
        loadService();
        if(null != mMotorController){
            mMotorController.setUpdateStatus(updateStatus);
        }
        */
    }

    /**
     * 获取马达硬件代码升级状态
     * @return true正在升级中，false未在升级中
     */
    public boolean getUpdateStatus() throws RemoteException {
        /*
        loadService();
        if(null != mMotorController){
            return mMotorController.getUpdateStatus();
        }
        */
        return false;
    }

    /***************************************【头部操作】*******************************************/
    /**
     * 头向左转
     * @param time_distance
     * @throws RemoteException
     */
    public void headLeft(int time_distance) throws RemoteException{
        loadService();
        if(null != mMotorController){
            mMotorController.headLeft(time_distance);
        }
    }

    /**
     * 头向右转
     * @param time_distance
     * @throws RemoteException
     */
    public void headRight(int time_distance) throws RemoteException{
        loadService();
        if(null != mMotorController){
            mMotorController.headRight(time_distance);
        }
    }

    /**
     * 抬起头
     * @param time_distance
     * @throws RemoteException
     */
    public void headUp(int time_distance) throws RemoteException{
        loadService();
        if(null != mMotorController){
            mMotorController.headUp(time_distance);
        }
    }

    /**
     * 低下头
     * @param time_distance
     * @throws RemoteException
     */
    public void headDown(int time_distance) throws RemoteException{
        loadService();
        if(null != mMotorController){
            mMotorController.headDown(time_distance);
        }
    }

    /**
     * 头部转向最左边
     * @throws RemoteException
     */
    public void headLeftEnd() throws RemoteException{
        loadService();
        if(null != mMotorController){
            mMotorController.headLeftEnd();
        }
    }

    /**
     * 头总转向最右边
     * @throws RemoteException
     */
    public void headRightEnd() throws RemoteException{
        loadService();
        if(null != mMotorController){
            mMotorController.headRightEnd();
        }
    }

    /**
     * 头从左边转到中间
     * @throws RemoteException
     */
    public void headLeftTurnMid() throws RemoteException{
        loadService();
        if(null != mMotorController){
            mMotorController.headLeftTurnMid();
        }
    }

    /**
     * 头从右边转到中间
     * @throws RemoteException
     */
    public void headRightTurnMid() throws RemoteException{
        loadService();
        if(null != mMotorController){
            mMotorController.headRightTurnMid();
        }
    }

    /**
     * 左右一直摇头
     * @throws RemoteException
     */
    public void headShake() throws RemoteException{
        loadService();
        if(null != mMotorController){
            mMotorController.headShake();
        }
    }

    /**
     * 停止头部运动
     * @throws RemoteException
     */
    public void headStop() throws RemoteException{
        loadService();
        if(null != mMotorController){
            mMotorController.headStop();
        }
    }

}
