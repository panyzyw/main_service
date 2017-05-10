package com.yongyida.robot.voice.robot;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.yongyida.robot.voice.base.BasePushCmd;
import com.yongyida.robot.voice.bean.PhotoCmdInfo;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.frame.socket.serverscoket.SocketHandler;
import com.yongyida.robot.voice.utils.GraphsUtil;

/**
 * 查询机器人的照片
 *
 * @author Administrator
 */
public class PushPhotoQurey extends BasePushCmd {
    private GraphsUtil photoByte = new GraphsUtil();

    @Override
    public void execute() {
        try {

            obJson = new JSONObject(json.optString("command", GeneralData.DEFAULT));
            if (!obJson.equals(GeneralData.DEFAULT)) {
                String type = obJson.optString("type", GeneralData.DEFAULT);
                if (!type.equals(GeneralData.DEFAULT)) {
                    type = type.trim();
                    String name;
                    if (type.equals(GeneralData.PHOTO_LIST)) {
                        Map<String, String> map = photoByte.getAllImageName();
                        if (map != null && SocketHandler.channel != null) {
                            SocketHandler.channel.write(map);
                        }
                    } else if (type.equals(GeneralData.PHOTO_DELETE)) {
                        JSONArray array = obJson.optJSONArray("names");
                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.optJSONObject(i);
                                if (obj != null) {
                                    name = obj.optString("name", GeneralData.DEFAULT);
                                    photoByte.deletePhoto(name);
                                    photoByte.deletePhoto("/PhotosCamera/", name);
                                    photoByte.deletePhoto("/BigPhotosCamera/", name);

                                }
                            }
                        }

                    } else {
                        name = obJson.optString("name", GeneralData.DEFAULT);
                        if (!name.equals(GeneralData.DEFAULT)) {
                            PhotoCmdInfo photoStr = photoByte.getPicturesFromName(name, type);
                            if (photoStr != null && SocketHandler.channel != null) {
                                SocketHandler.channel.write(photoStr);
                            }
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}