package com.yongyida.robot.voice.subservice.subimpl;

import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.frame.http.Achieve;
import com.yongyida.robot.voice.frame.socket.localscket.SocketChannel;
import com.yongyida.robot.voice.subservice.SubFunction;
import com.yongyida.robot.voice.utils.LogUtils;
import com.yongyida.robot.voice.utils.SharePreferenceUtils;
import com.yongyida.robot.voice.utils.ThreadExecutorUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/12/16 0016.
 * by dean
 */

public class SubRemoveFriends extends SubFunction {

    private static final String CLIENT_ID = "video";

    private String remove = "";

    @Override
    public void run() {
        try {

            LogUtils.showLogDebug(GeneralData.SUCCESS, "SubRemoveFriends");

            if (context == null) return;
            if (json == null) return;

            SharePreferenceUtils sp = SharePreferenceUtils.getInstance(context);

            JSONObject ob = new JSONObject(json);
            String type = ob.optString("type", "default");
            Achieve achieve = new Achieve(context);

            if (type.equals("Robot")) {
                remove = achieve.removeRobotFriend(sp.getString("id", "123456"), sp.getString("serial", "123456"), ob.getString("number"));
            } else if (type.equals("Phone")) {
                remove = achieve.removePhoneFriend(
                        sp.getString("id", "123456"),
                        sp.getString("serial", "123456"),
                        ob.getString("number"));
            }

            LogUtils.showLogDebug(GeneralData.SUCCESS, "remove : " + remove);

            if (remove.contains("success")) {
                //添加success
                LogUtils.showLogDebug(GeneralData.SUCCESS, "删除成功发协议给视频");
                final SocketChannel channel = new SocketChannel();
//                channel.setData("/media/friend/remove/response");
//                channel.setId("video");
//                channel.sendData();

                ThreadExecutorUtils.getExceutor().schedule(new Runnable() {

                    @Override
                    public void run() {
                        channel.setData("{\"ret\":\"0\",\"cmd\":\"/media/friend/remove/response\"}");
                        channel.setId(CLIENT_ID);
                        channel.sendData();

                    }
                }, 0, TimeUnit.SECONDS);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
