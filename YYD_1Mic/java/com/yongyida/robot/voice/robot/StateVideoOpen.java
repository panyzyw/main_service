package com.yongyida.robot.voice.robot;

import java.util.HashMap;
import java.util.Map;

import com.yongyida.robot.voice.base.BaseMessage;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.frame.http.Achieve;
import com.yongyida.robot.voice.frame.iflytek.VoiceUnderstand;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;
import com.yongyida.robot.voice.utils.SharePreferenceUtils;

/**
 * 视频打开.
 *
 * @author Administrator
 */
public class StateVideoOpen extends BaseMessage {

    SharePreferenceUtils sp = SharePreferenceUtils.getInstance(context);

    @Override
    public void execute() {
        mainServiceInfo.setOpenVideo("open");
        mainServiceInfo.setVideo(true);

        Achieve achieva = new Achieve(context);
        String id = sp.getString("id", "123456");
        String serial = sp.getString("serial", "123456");
        achieva.synchronizeData(id, serial);

        if (factory != null) {
            factory.setFactory(VoiceUnderstand.getInstance(context));
            factory.parseStop();
        }
        Map<String, String> map2;
        map2 = new HashMap<String, String>();
        map2.put(GeneralData.ACTION, IntentData.INTENT_STOP);

        SendBroadcastUtils.sendBroad(context, map2);

        //SendBroadcastBiz.sendBroad(context, IntentData.INTENT_STOP);
    }
}