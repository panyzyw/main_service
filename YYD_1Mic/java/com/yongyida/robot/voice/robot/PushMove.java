package com.yongyida.robot.voice.robot;


import android.os.RemoteException;
import android.util.Log;

import com.google.gson.Gson;
import com.yongyida.robot.entity.RobotCommand;
import com.yongyida.robot.motor.util.MotorControl;
import com.yongyida.robot.motorcontrol.MotorController;
import com.yongyida.robot.voice.base.BasePushCmd;

import org.json.JSONObject;

/**
 * 手机APP推送移动命令.
 *
 * @author Administrator
 */
public class PushMove extends BasePushCmd {
    private MotorController mMotorController;
    private static final int DRVTYPE_BY_TIME = 0;
    private static final int DRVTYPE_BY_DISTANCE = 1;
    String jsonCmd = "";

    @Override
    public void execute() {
        try {
            mMotorController = MotorControl.getInstance();
            Log.e("binder", "json------>" + json.toString());
            obJson = new JSONObject(json.optString("command"));
            Log.e("binder", "obJson------>" + obJson.toString());
            if (obJson != null) {
//				Map<String, String> map2 ;
//				map2 = new HashMap<String, String>();
//				map2.put(GeneralData.ACTION, IntentData.INTENT_MOVE);
//				map2.put(GeneralData.RESULT, json.toString());
//				SendBroadcastUtils.sendBroad(context, map2);
                jsonCmd = json.toString();
                Log.e("binder", "jsonCmd 1-->" + jsonCmd);
                jsonCmd = jsonCmd.replace("\\/", "/").replace("\\\"", "\"").replace("\"{", "{").replace("}\"", "}");
                RobotCommand robotCmd = null;
                robotCmd = new Gson().fromJson(jsonCmd, RobotCommand.class);
                mMotorController.setDrvType(DRVTYPE_BY_TIME);
                if (robotCmd.command.type.equals("forward")) {
                    Log.e("binder", "robotCmd.command.type" + robotCmd.command.type);
                    mMotorController.setSpeed(30);
                    mMotorController.forward(1500);
                } else if (robotCmd.command.type.equals("back")) {
                    mMotorController.setSpeed(30);
                    mMotorController.back(1500);
                } else if (robotCmd.command.type.equals("turn_left")) {
                    mMotorController.setSpeed(30);
                    mMotorController.left(1500);
                } else if (robotCmd.command.type.equals("turn_right")) {
                    mMotorController.setSpeed(30);
                    mMotorController.right(1500);
                } else if (robotCmd.command.type.equals("stop")) {
                    mMotorController.stop();
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}