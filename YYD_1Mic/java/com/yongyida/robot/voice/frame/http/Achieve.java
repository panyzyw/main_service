package com.yongyida.robot.voice.frame.http;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.yongyida.robot.voice.dao.Friends;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 机器人添加朋友
 * <p/>
 * Author : 陈赫
 */
public class Achieve {
    private final String DEBUG = "120.24.242.163:81";
    private final String FORMAL = "server.yydrobot.com";

    public static String HTTP_HOST = "120.24.242.163:81";

    private ContentValues values = new ContentValues();
    private Context context = null;

    public Achieve(Context context) {
        this.context = context;
    }

    public String synchronizeData(String robot_id, String robot_serial) {

        LogUtils.showLogDebug(GeneralData.SUCCESS, "Achieve => synchronizeData");
        String res = null;
        Http http;
        try {
            http = new Http("http://" + HTTP_HOST + "/friends/robot/find");
            http.setCharset("utf-8");
            http.setRequestProperty("Content-type", "text/plain;charset=UTF-8");

            JSONObject json = new JSONObject();

            json.put("robot_id", robot_id);
            json.put("robot_serial", robot_serial);

            String result = http.post(json.toString());

            JSONObject object = new JSONObject(result);
            int ret = Integer.parseInt(object.getString("ret"));
            switch (ret) {
                case -1:
                    res = "缺少参数";
                    break;
                case 0:
                    int num = 0;

                    JSONArray jsonArrayPhones = new JSONArray(object.getString("Users"));
                    JSONArray jsonArrayRobots = new JSONArray(object.getString("Robots"));

                    Cursor cursorSynchronize = null;

                    for (int i = 0; i < jsonArrayPhones.length(); i++) {
                        object = jsonArrayPhones.getJSONObject(i);
                        String phone = object.getString("phone");
                        cursorSynchronize = queryPhoneFriend(phone);

                        if (!cursorSynchronize.moveToNext()) {

                            ++num;

                            values.clear();
                            values.put(Friends.USERS_CONTROLLER, object.getString("controller"));
                            values.put(Friends.USERS_PHONE, phone);
                            values.put(Friends.USERS_HEADSHOT, object.getString("headshot"));
                            values.put(Friends.USERS_NICKNAME, object.getString("nickname"));
                            values.put(Friends.USERS_NAME, object.getString("name"));
                            values.put(Friends.USERS_ID, object.getString("id"));
                            values.put(Friends.USERS_ALIAS, phone);

                            Uri uri = context.getContentResolver().insert(Friends.CONTENT_URI_USERS, values);
                        }
                    }

                    for (int i = 0; i < jsonArrayRobots.length(); i++) {
                        object = jsonArrayRobots.getJSONObject(i);
                        String rid = object.getString("rid");
                        cursorSynchronize = queryRobotsFriend(rid);

                        if (!cursorSynchronize.moveToNext()) {

                            ++num;

                            values.clear();
                            values.put(Friends.ROBOTS_CONTROLLER, object.getString("controller"));
                            values.put(Friends.ROBOTS_RNAME, object.getString("rname"));
                            values.put(Friends.ROBOTS_SERIAL, object.getString("serial"));
                            values.put(Friends.ROBOTS_ONLINE, object.getString("online"));
                            values.put(Friends.ROBOTS_ROBOTSID, object.getString("id"));
                            values.put(Friends.ROBOTS_RID, object.getString("rid"));
//                            values.put(Friends.ROBOTS_ADDR, object.getString("addr"));
                            values.put(Friends.ROBOTS_ADDR, "temp");
                            values.put(Friends.ROBOTS_BATTERY, object.getString("battery"));
//                    values.put(Friends.ROBOTS_VERSION, object1.getString("version"));
                            values.put(Friends.ROBOTS_VERSION, "temp");
                            values.put(Friends.ROBOTS_ALIAS, object.getString("rname"));

                            Uri uri = context.getContentResolver().insert(Friends.CONTENT_URI_ROBOTS, values);
                        }
                    }
                    if (cursorSynchronize != null) {
                        cursorSynchronize.close();
                    }

                    res = String.valueOf(num);
                    break;
                case 1:
                    res = "机器人信息为空";
                    break;
                case 2:
                    res = "机器人id或序列号不存在";
                    break;
            }

            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String addRobotFriend(String id, String serial, String frid) {
        LogUtils.showLogDebug(GeneralData.SUCCESS, "Achieve => addRobotFriend");
        try {

            String res = null;

            Http http = new Http("http://" + HTTP_HOST + "/friends/robot/addRobot");
            http.setCharset("utf-8");
            http.setRequestProperty("Content-type", "text/plain;charset=UTF-8");

            JSONObject json = new JSONObject();

            json.put("id", id);
            json.put("serial", serial);
            json.put("frid", frid);

            String result = http.post(json.toString());

            JSONObject object = new JSONObject(result);
            int ret = Integer.parseInt(object.getString("ret"));

            switch (ret) {
                case -1:
                    res = "缺少参数";
                    break;
                case 0:

                    LogUtils.showLogError(GeneralData.SUCCESS, "addRobotFriend服务器添加成功");
                    JSONObject object1 = new JSONObject(object.getString("Robots"));

                    values.clear();
                    values.put(Friends.ROBOTS_CONTROLLER, object1.getString("controller"));
                    values.put(Friends.ROBOTS_RNAME, object1.getString("rname"));
                    values.put(Friends.ROBOTS_SERIAL, object1.getString("serial"));
                    values.put(Friends.ROBOTS_ONLINE, object1.getString("online"));
                    values.put(Friends.ROBOTS_ROBOTSID, object1.getString("id"));
                    values.put(Friends.ROBOTS_RID, object1.getString("rid"));
//                    values.put(Friends.ROBOTS_ADDR, object1.getString("addr"));
                    values.put(Friends.ROBOTS_ADDR, "temp");
                    values.put(Friends.ROBOTS_BATTERY, object1.getString("battery"));
//                    values.put(Friends.ROBOTS_VERSION, object1.getString("version"));
                    values.put(Friends.ROBOTS_VERSION, "temp");
                    values.put(Friends.ROBOTS_ALIAS, object1.getString("rname"));

                    Uri uri = context.getContentResolver().insert(Friends.CONTENT_URI_ROBOTS, values);

                    res = "success:" + uri.getLastPathSegment();
                    break;
                case 1:
                    res = "机器人信息为空";
                    break;
                case 2:
                    res = "机器人id或序列号不存在";
                    break;
                case 3:
                    res = "机器人要添加的好友信息不存在";
                    break;
                case 4:
                    res = "该好友已被添加";
                    break;
                case 5:
                    res = "超过最大绑定数";
                    break;
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.showLogError(GeneralData.SUCCESS, "addRobotFriend出错");
        }
        return null;
    }

    public String addPhoneFriend(String robot_id, String robot_serial, String phone) {
        LogUtils.showLogDebug(GeneralData.SUCCESS, "Achieve => addPhoneFriend");
        Http http;
        String res = null;
        try {
            http = new Http("http://" + HTTP_HOST + "/friends/robot/addUser");
            http.setCharset("utf-8");
            http.setRequestProperty("Content-type", "text/plain;charset=UTF-8");

            JSONObject json = new JSONObject();

            json.put("robot_id", robot_id);
            json.put("robot_serial", robot_serial);
            json.put("phone", phone);

            String result = http.post(json.toString());

            JSONObject object = new JSONObject(result);
            int ret = Integer.parseInt(object.getString("ret"));

            switch (ret) {
                case -1:
                    res = "缺少参数";
                    break;
                case 0:

                    JSONObject object1 = new JSONObject(object.getString("Users"));

                    values.clear();
                    values.put(Friends.USERS_CONTROLLER, object1.getString("controller"));
                    values.put(Friends.USERS_PHONE, object1.getString("phone"));
                    values.put(Friends.USERS_HEADSHOT, object1.getString("headshot"));
                    values.put(Friends.USERS_NICKNAME, object1.getString("nickname"));
                    values.put(Friends.USERS_NAME, object1.getString("name"));
                    values.put(Friends.USERS_ID, object1.getString("id"));
                    values.put(Friends.USERS_ALIAS, object1.getString("phone"));

                    Uri uri = context.getContentResolver().insert(Friends.CONTENT_URI_USERS, values);

                    res = "success:" + uri.getLastPathSegment();
                    break;
                case 1:
                    res = "用户信息为空";
                    break;
                case 2:
                    res = "机器人信息为空";
                    break;
                case 3:
                    res = "机器人id或序列号不存在";
                    break;
                case 4:
                    res = "该好友已被添加";
                    break;
                case 5:
                    res = "超过最大绑定数";
                    break;
            }

            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String removeRobotFriend(String id, String serial, String frid) {
        LogUtils.showLogDebug(GeneralData.SUCCESS, "Achieve => removeRobotFriend");
        String res = null;

        Http http;
        try {
            http = new Http("http://" + HTTP_HOST + "/friends/robot/delRobot");
            http.setCharset("utf-8");
            http.setRequestProperty("Content-type", "text/plain;charset=UTF-8");

            JSONObject json = new JSONObject();

            json.put("id", id);
            json.put("serial", serial);
            json.put("frid", frid);

            String result = http.post(json.toString());

            JSONObject object = new JSONObject(result);
            int ret = Integer.parseInt(object.getString("ret"));

            switch (ret) {
                case -1:
                    res = "缺少参数";
                    break;
                case 0:
                    int deleteId = context.getContentResolver().delete(Uri.withAppendedPath(Friends.CONTENT_URI_ROBOTS, frid),
                            null, null);

                    res = "success:" + String.valueOf(deleteId);
                    break;
                case 1:
                    res = "机器人信息为空";
                    break;
                case 2:
                    res = "机器人id或序列号不存在";
                    break;
                case 3:
                    res = "机器人要删除的好友信息为空";
                    break;
                case 4:
                    res = "机器人要删除的好友id或序列号不存在";
                    break;
                case 5:
                    res = "要删除的好友信息不在好友列表";
                    break;
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String removePhoneFriend(String robot_id, String robot_serial, String phone) {
        LogUtils.showLogDebug(GeneralData.SUCCESS, "Achieve => removePhoneFriend");
        Http http;
        String res = null;
        try {
            http = new Http("http://" + HTTP_HOST + "/friends/robot/delUser");
            http.setCharset("utf-8");
            http.setRequestProperty("Content-type", "text/plain;charset=UTF-8");

            JSONObject json = new JSONObject();

            json.put("robot_id", robot_id);
            json.put("robot_serial", robot_serial);
            json.put("phone", phone);

            String result = http.post(json.toString());

            JSONObject object = new JSONObject(result);
            int ret = Integer.parseInt(object.getString("ret"));

            switch (ret) {
                case -1:
                    res = "缺少参数";
                    break;
                case 0:
                    int deleteId = context.getContentResolver().delete(Uri.withAppendedPath(Friends.CONTENT_URI_USERS, phone),
                            null, null);

                    res = "success:" + String.valueOf(deleteId);
                    break;
                case 1:
                    res = "用户信息为空";
                    break;
                case 2:
                    res = "机器人信息为空";
                    break;
                case 3:
                    res = "机器人id或序列号不存在";
                    break;
                case 4:
                    res = "要删除的好友信息不在好友列表";
                    break;
            }

            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List queryAllFriends() {

        List<Cursor> cursorsFriends = new ArrayList<>();

        Cursor cursorRobots = queryRobotsFriend(null);
        Cursor cursorUsers = queryPhoneFriend(null);

        cursorsFriends.add(cursorRobots);
        cursorsFriends.add(cursorUsers);

        return cursorsFriends;
    }

    /*
    rId = null时为查询所有好友
     */
    private Cursor queryRobotsFriend(String rId) {

        Cursor cursor;

        if (rId != null) {
            cursor = context.getContentResolver().query(Uri.withAppendedPath(Friends.CONTENT_URI_ROBOTS, rId),
                    Friends.ALL_ROBOTS, null, null, null);
        } else {
            cursor = context.getContentResolver().query(Friends.CONTENT_URI_ROBOTS,
                    Friends.ALL_ROBOTS, null, null, null);
        }
        return cursor;
    }

    /*
    phone = null时为查询所有好友
     */
    private Cursor queryPhoneFriend(String phone) {

        Cursor cursor;

        if (phone != null) {
            cursor = context.getContentResolver().query(Uri.withAppendedPath(Friends.CONTENT_URI_USERS, phone),
                    Friends.ALL_USERS, null, null, null);
        } else {
            cursor = context.getContentResolver().query(Friends.CONTENT_URI_USERS,
                    Friends.ALL_USERS, null, null, null);
        }
        return cursor;
    }

    public String update() {
        return null;
    }

    public String updateRobotAlias(String id, String serial, String frid, String alias) {
        String res = null;
        try {
            Http http;
            http = new Http("http://" + HTTP_HOST + "/friends/robot/set_robot_alias");
            http.setCharset("utf-8");
            http.setRequestProperty("Content-type", "text/plain;charset=UTF-8");

            JSONObject json = new JSONObject();

            json.put("robot_id", id);
            json.put("robot_serial", serial);
            json.put("frid", frid);
            json.put("frid_alias", alias);

            String result = http.post(json.toString());
            JSONObject object = new JSONObject(result);
            int ret = Integer.parseInt(object.getString("ret"));
            switch (ret) {
                case 0:
                    JSONArray array = new JSONArray(object.getString("robot_alias"));
                    object = new JSONObject(String.valueOf(array.getJSONObject(0)));
                    String alia = object.getString("alias");

                    values.clear();
                    values.put(Friends.ROBOTS_ALIAS, alia);

                    int rows = context.getContentResolver().update(Uri.withAppendedPath(Friends.CONTENT_URI_ROBOTS, frid)
                            , values, null, null);

                    res = "success:" + rows;
                    break;
                case 1:
                    res = "id为空";
                    break;
                case 2:
                    res = "serial为空";
                    break;
                case 3:
                    res = "frid格式不正确";
                    break;
                case 4:
                    res = "机器人不存在";
                    break;
                case 5:
                    res = "机器人id和serial校验失败";
                    break;
                case 6:
                    res = "好友机器人不存在";
                    break;
                case 7:
                    res = "添加的好友是自己";
                    break;
                case 8:
                    res = "没机器人好友";
                    break;
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String updatePhoneAlias(String id, String serial, String phone, String alias) {
        String res = null;
        try {
            Http http;
            http = new Http("http://" + HTTP_HOST + "/friends/robot/set_user_alias");
            http.setCharset("utf-8");
            http.setRequestProperty("Content-type", "text/plain;charset=UTF-8");

            JSONObject json = new JSONObject();

            json.put("robot_id", id);
            json.put("robot_serial", serial);
            json.put("phone", phone);
            json.put("phone_alias", alias);

            String result = http.post(json.toString());
            JSONObject object = new JSONObject(result);
            int ret = Integer.parseInt(object.getString("ret"));
            switch (ret) {
                case 0:
                    JSONArray array = new JSONArray(object.getString("phone_alias"));
                    object = new JSONObject(String.valueOf(array.getJSONObject(0)));
                    String alia = object.getString("alias");

                    values.clear();
                    values.put(Friends.USERS_ALIAS, alia);

                    int rows = context.getContentResolver().update(Uri.withAppendedPath(Friends.CONTENT_URI_USERS, phone)
                            , values, null, null);

                    res = "success:" + rows;
                    break;
                case 1:
                    res = "id为空";
                    break;
                case 2:
                    res = "serial为空";
                    break;
                case 3:
                    res = "frid格式不正确";
                    break;
                case 4:
                    res = "机器人不存在";
                    break;
                case 5:
                    res = "机器人id和serial校验失败";
                    break;
                case 6:
                    res = "手机好友不存在";
                    break;
                case 7:
                    res = "添加的好友是自己";
                    break;
                case 8:
                    res = "没手机好友";
                    break;
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}