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
 * 机器人添加好友
 *
 * @author Administrator
 */
public class SubAddFriends extends SubFunction {

    private static final String CLIENT_ID = "video";
    private String add = "";

    @Override
    public void run() {
        try {

            if (context == null) return;
            if (json == null) return;

            SharePreferenceUtils sp = SharePreferenceUtils.getInstance(context);

            JSONObject ob = new JSONObject(json);
            String type = ob.optString("type", "default");
            Achieve achieve = new Achieve(context);

            if (type.equals("Robot")) {
                add = achieve.addRobotFriend(sp.getString("id", "123456"), sp.getString("serial", "123456"), ob.getString("number"));
            } else if (type.equals("Phone")) {
                add = achieve.addPhoneFriend(
                        sp.getString("id", "123456"),
                        sp.getString("serial", "123456"),
                        ob.getString("number"));
            }

            LogUtils.showLogDebug(GeneralData.SUCCESS, "add : " + add);

            if (add.contains("success")) {
                //添加success
                final SocketChannel channel = new SocketChannel();
//                channel.setData("/media/friend/remove/response");
//                channel.setId("video");
//                channel.sendData();

                ThreadExecutorUtils.getExceutor().schedule(new Runnable() {

                    @Override
                    public void run() {
                        LogUtils.showLogDebug(GeneralData.SUCCESS, "添加成功发协议给视频");
                        channel.setData("{\"ret\":\"0\",\"cmd\":\"/media/friend/add/response\"}");
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
