package com.zccl.ruiqianqi.brain.voice;

import android.content.Context;
import android.os.RemoteException;
import android.os.SystemProperties;

import com.zccl.ruiqianqi.brain.R;
import com.zccl.ruiqianqi.move.MoveAction;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

/**
 * Created by ruiqianqi on 2017/4/2 0002.
 */

public class Localization {

    // 类标志
    private static String TAG = Localization.class.getSimpleName();
    // 全局上下文
    private Context mContext;
    // 运动服务
    private MoveAction moveAction;
    // 马达驱动版本
    private String motorVersionKey;

    public Localization(Context context){
        this.mContext = context;
        moveAction = MoveAction.getInstance(mContext);
        motorVersionKey = mContext.getString(R.string.motor_version);
    }

    /**
     * 声源定位移动方法
     * 动态判断
     * @param angle
     */
    protected void rotate(int angle){
        String model = SystemProperties.get(motorVersionKey, "8163_20");
        if(!StringUtils.isEmpty(model)) {
            if(model.startsWith("8735")){
                if(model.contains("_20")){
                    motor_8735_20(angle);
                }else if(model.contains("_50")){
                    motor_8735_50(angle);
                }else if(model.contains("_128")){

                }else if(model.contains("_150")){

                }
            }else if(model.startsWith("8163")){
                if(model.contains("_20")){
                    motor_8163_20(angle);
                }else if(model.contains("_50")){
                    motor_8163_50(angle);
                }else if(model.contains("_128")){

                }else if(model.contains("_150")){

                }
            }
        }
    }

    /*********************************【8735声源定位】*********************************************/
    /**
     * 20机型的声源定位
     * @param angle
     */
    private void motor_8735_20(int angle){
        LogUtils.e(TAG, "motor_8735_20 localization");
        try {
            moveAction.setDriveType(MoveAction.DRIVE_BY_DISTANCE);
            if(angle < 180){
                moveAction.setSpeed(40);
                moveAction.left(19 * angle);
            }else if(angle >= 180){
                angle = 360 - angle;
                moveAction.setSpeed(40);
                moveAction.right(19 * angle);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 50机型的声源定位
     * @param angle
     */
    private void motor_8735_50(int angle){
        LogUtils.e(TAG, "motor_8735_50 localization");
        try {
            moveAction.headStop();
            moveAction.stop();
            if(angle <= 60){
                moveAction.setDriveType(MoveAction.DRIVE_BY_TIME);
                moveAction.setSpeed(53);
                moveAction.headLeft(12 * angle + angle / 2);
            }else if(angle >= 300){
                angle = 360 - angle;
                moveAction.setDriveType(MoveAction.DRIVE_BY_TIME);
                moveAction.setSpeed(53);
                moveAction.headRight(11 * angle + angle);
            }else if(angle > 60 && angle < 180){
                moveAction.setDriveType(MoveAction.DRIVE_BY_DISTANCE);
                moveAction.setSpeed(53);
                moveAction.left(5 * angle);
            }else if(angle >= 180 && angle < 300){
                angle = 360 - angle;
                moveAction.setDriveType(MoveAction.DRIVE_BY_DISTANCE);
                moveAction.setSpeed(53);
                moveAction.right(5 * angle - angle / 4);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /*********************************【8163声源定位】*********************************************/
    /**
     * 20机型的声源定位
     * @param angle
     */
    private void motor_8163_20(int angle){
        LogUtils.e(TAG, "motor_8163_20 localization");
        try {
            moveAction.setDriveType(MoveAction.DRIVE_BY_DISTANCE);
            if(angle <= 90){
                moveAction.setSpeed(40);
                moveAction.left((int) ((3.5 * angle)) + angle / 4);
            }else if(angle >90 && angle <=180){
                moveAction.setSpeed(40);
                moveAction.left((int) ((3.5 * angle) - angle / 4));
            }else if (angle > 180 && angle <= 270){
                angle = 360 - angle;
                moveAction.setSpeed(40);
                moveAction.right((int) ((3.5 * angle) - angle / 4));
            }else if(angle > 270){
                angle = 360 - angle;
                moveAction.setSpeed(40);
                moveAction.right((int) ((3.5 * angle) + angle / 4));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 50机型的声源定位
     * @param angle
     */
    private void motor_8163_50(int angle){
        LogUtils.e(TAG, "motor_8163_50 localization");
        try {
            moveAction.headStop();
            moveAction.stop();
            moveAction.setDriveType(MoveAction.DRIVE_BY_TIME);
            if(angle <= 60){
                moveAction.setSpeed(30);
                moveAction.headLeft(7 * angle);
            }else if(angle >= 300){
                angle = 360 - angle;
                moveAction.setSpeed(30);
                moveAction.headRight(7 * angle);
            }else if(angle > 60 && angle < 120){
                moveAction.setSpeed(27);
                moveAction.left(13 * angle - angle / 2);
            }else if(angle >= 120 && angle < 180){
                moveAction.setSpeed(27);
                moveAction.left(11 * angle );
            }else if(angle >= 180 && angle< 240) {
                angle = 360 - angle;
                moveAction.setSpeed(27);
                moveAction.right(11 * angle);
            } else if(angle >= 240 && angle < 300){
                angle = 360 - angle;
                moveAction.setSpeed(27);
                moveAction.right(13 * angle - angle / 2);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


}
