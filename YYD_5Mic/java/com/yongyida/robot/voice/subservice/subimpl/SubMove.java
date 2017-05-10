package com.yongyida.robot.voice.subservice.subimpl;

import android.os.RemoteException;
import android.util.Log;

import com.google.gson.Gson;
import com.yongyida.robot.entity.RobotCommand;
import com.yongyida.robot.motor.util.MotorControl;
import com.yongyida.robot.motorcontrol.MotorController;
import com.yongyida.robot.voice.bean.BroadcastInfo;
import com.yongyida.robot.voice.bean.MoveDataInfo;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.subservice.SubFunction;
import com.yongyida.robot.voice.utils.JsonParserUtils;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;
import com.yongyida.robot.voice.utils.ShowToast;
import com.yongyida.robot.voice.utils.ThreadExecutorUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 语音移动.
 *
 * @author Administrator
 */
public class SubMove extends SubFunction {

    private ScheduledThreadPoolExecutor executor = ThreadExecutorUtils.getExceutor();

    @Override
    public void run() {

        try {
            MoveDataInfo move = JsonParserUtils.parseResult(json, MoveDataInfo.class);
            if (move == null) {
                return;
            }
            if (move.getSemantic() == null) {
                return;
            }
            if (move.getSemantic().getSlots() == null) {
                return;
            }
            String action = BroadcastInfo.getInstance().getIntentBroadcast(move.getSemantic().getSlots().getDirect());
            Log.e("mlog", "action---->" + action);
            RobotCommand robotCmd;
            MotorController mMotorController = MotorControl.getInstance();
            action = action.replace("\\/", "/").replace("\\\"", "\"").replace("\"{", "{").replace("}\"", "}");
            robotCmd = new Gson().fromJson(action, RobotCommand.class);
            Log.e("mlog", "robotCmd---->" + robotCmd);
            mMotorController.setDrvType(0);
            switch (robotCmd.command.type) {
                case "forward":
                    mMotorController.setSpeed(30);
                    mMotorController.forward(3000);
                    recycleUnderstand(2700, TimeUnit.MILLISECONDS);
                    break;
                case "back":
                    mMotorController.setSpeed(30);
                    mMotorController.back(3000);
                    recycleUnderstand(2700, TimeUnit.MILLISECONDS);
                    break;
                case "turn_left":
                    mMotorController.setSpeed(30);
                    mMotorController.left(1500);
                    recycleUnderstand(1400, TimeUnit.MILLISECONDS);
                    break;
                case "turn_right":
                    mMotorController.setSpeed(30);
                    mMotorController.right(1500);
                    recycleUnderstand(1400, TimeUnit.MILLISECONDS);
                    break;
                case "head_up":
                    mMotorController.setSpeed(30);
                    mMotorController.headUp(1000);
                    recycleUnderstand(1, TimeUnit.SECONDS);
                    break;
                case "head_down":
                    mMotorController.setSpeed(30);
                    mMotorController.headDown(1000);
                    recycleUnderstand(1, TimeUnit.SECONDS);
                    break;
                case "head_left":
                    mMotorController.setSpeed(30);
                    mMotorController.headLeft(1000);
                    recycleUnderstand(1, TimeUnit.SECONDS);
                    break;
                case "head_right":
                    mMotorController.setSpeed(30);
                    mMotorController.headRight(1000);
                    recycleUnderstand(1, TimeUnit.SECONDS);
                    break;
                case "head_middle":
                    mMotorController.setSpeed(40);
                    mMotorController.headLeftTurnMid();
                    recycleUnderstand(1, TimeUnit.SECONDS);
                    break;
            }
            //一直向前向后暂无定义
//			else if(robotCmd.command.type.equals("always_forward")){
//				mMotorController.forward(0xffff);
//			}else if(robotCmd.command.type.equals("always_back")){
//				mMotorController.back(0xffff);
//			}
//			Map<String, String> map2 = new HashMap<String, String>();
//			map2.put(GeneralData.ACTION, IntentData.INTENT_MOVE);
//			map2.put(GeneralData.RESULT, action);
//			SendBroadcastUtils.sendBroad(context, map2);
        } catch (RemoteException e) {
            ShowToast.getInstance(context).show("驱动异常", "请联系客服");
            e.printStackTrace();
        }
    }

    private void recycleUnderstand(long time, TimeUnit timeUnit) {
        executor.schedule(sendRU, time, timeUnit);
    }

    private Runnable sendRU = new Runnable() {
        @Override
        public void run() {
            Map<String, String> map = new HashMap<>();
            map.put(GeneralData.ACTION, IntentData.INTENT_RECYCLE);
            map.put(GeneralData.FROM, "SubMove");
            SendBroadcastUtils.sendBroad(context, map);
        }
    };
}
