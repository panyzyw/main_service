package com.yongyida.robot.voice.robot;

import android.os.RemoteException;
import android.util.Log;

import com.yongyida.robot.motor.util.MotorControl;
import com.yongyida.robot.motorcontrol.MotorController;
import com.yongyida.robot.voice.app.MyApp;
import com.yongyida.robot.voice.bean.RobotInfo;
import com.yongyida.robot.voice.data.RobotStateData;
import com.yongyida.robot.voice.utils.SharePreferenceUtils;

/**
 * Created by sunyibin on 2016/8/11 0011.
 */
public class VoiceLocalization {
    private MotorController mMotorController;
		private SharePreferenceUtils mSp = SharePreferenceUtils.getInstance(MyApp.getContext());
    private boolean rotateUp = true;
    public static final int DRVTYPE_BY_TIME = 0;
		public static final int DRVTYPE_BY_DISTANCE = 1;
    private static final VoiceLocalization mVoiceLocalization = new VoiceLocalization();
    public static VoiceLocalization getInstance() {
        return mVoiceLocalization;
    }
    public void setRotateUp(boolean up){
        mSp.putBoolean("rotateState",up);
        rotateUp=up;
    }
    public boolean getRotateUp(){
        return mSp.getBoolean("rotateState", true);
    }
    public boolean startRotate(int angle){
    		RobotInfo robotInfo = RobotInfo.getInstance();
        if(rotateUp==true && !robotInfo.getContrallState().equals(RobotStateData.STATE_CONTRALL)){
            try {
                rotate(angle);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return rotateUp;
    }
    private void rotate(int r) throws RemoteException {
        Log.d("jlog", "rotate------>");
        if (mMotorController == null) {
            mMotorController = MotorControl.getInstance();
        }
        mMotorController.headStop();
        mMotorController.stop();
        if(r <= 60){
            mMotorController.setDrvType(DRVTYPE_BY_TIME);
            mMotorController.setSpeed(25);
            mMotorController.headLeft(5 * r);
        }else if(r >= 300){
            r=360-r;
            mMotorController.setDrvType(DRVTYPE_BY_TIME);
            mMotorController.setSpeed(25);
            mMotorController.headRight(5 * r);
        }else if(r > 60 && r < 180){
            mMotorController.setDrvType(DRVTYPE_BY_DISTANCE);
            mMotorController.setSpeed(40);
            mMotorController.left(r - r/6);
        }else if(r >= 180 && r < 300){
            r=360-r;
            mMotorController.setDrvType(DRVTYPE_BY_DISTANCE);
            mMotorController.setSpeed(40);
            mMotorController.right(r - r/6);
        }
    }
}
