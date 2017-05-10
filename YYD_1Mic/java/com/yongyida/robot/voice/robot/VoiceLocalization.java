package com.yongyida.robot.voice.robot;

import android.os.RemoteException;

import com.yongyida.robot.motor.util.MotorControl;
import com.yongyida.robot.motorcontrol.MotorController;
import com.yongyida.robot.voice.app.MyApp;
import com.yongyida.robot.voice.utils.SharePreferenceUtils;

/**
 * Created by sunyibin on 2016/8/11 0011.
 */
public class VoiceLocalization {
    private MotorController mMotorController;
    private SharePreferenceUtils mSp=SharePreferenceUtils.getInstance(MyApp.getContext());
    private boolean rotateUp=true;
    private static final int DRVTYPE_BY_TIME = 0;
    private static final int DRVTYPE_BY_DISTANCE = 1;
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
    public boolean startRotate(int angle) {
        if(rotateUp==true){
            try {
                rotate(angle);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return rotateUp;
    }
    private void rotate(int r) throws RemoteException {
        if(mMotorController==null){
            mMotorController = MotorControl.getInstance();
        }
        mMotorController.setDrvType(DRVTYPE_BY_DISTANCE);
        if (r < 180) {
            mMotorController.setSpeed(40);
            mMotorController.left(19 * r);
        } else if (r >= 180) {
            r = 360 - r;
            mMotorController.setSpeed(40);
            mMotorController.right(19 * r );
        }
    }
}