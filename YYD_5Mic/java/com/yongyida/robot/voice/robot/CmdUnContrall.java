package com.yongyida.robot.voice.robot;

import com.yongyida.robot.voice.base.BaseCmd;
import com.yongyida.robot.voice.bean.MainServiceInfo;
import com.yongyida.robot.voice.bean.RobotInfo;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.data.RobotStateData;
import com.yongyida.robot.voice.data.VoiceData;
import com.yongyida.robot.voice.utils.MediaPlayUtils;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 解除控制
 *
 * @author Administrator
 */
public class CmdUnContrall extends BaseCmd {

    MainServiceInfo mainServiceInfo = MainServiceInfo.getInstance();

    @Override
    public void execute() {

        robot = RobotInfo.getInstance();

        robot.setContrallState(RobotStateData.STATE_UNCONTRALL);
        mainServiceInfo.setPlayComplete(false);

        Map<String, String> map2;
        map2 = new HashMap<>();
        map2.put(GeneralData.ACTION, IntentData.INTENT_STOP);
        map2.put(GeneralData.RESULT, GeneralData.SHUT_DOWN_VIDEO);
        SendBroadcastUtils.sendBroad(context, map2);

        Map<String, String> controlStatus;
        controlStatus = new HashMap<>();
        controlStatus.put(GeneralData.ACTION, IntentData.INTENT_CONTROL_STATUS);
        controlStatus.put(GeneralData.STATUS, "unControl");
        SendBroadcastUtils.sendBroad(context, controlStatus);

        MediaPlayUtils.getInstance().playMusic(context, VoiceData.CONTROLL_DISCONNECT);
    }
}
