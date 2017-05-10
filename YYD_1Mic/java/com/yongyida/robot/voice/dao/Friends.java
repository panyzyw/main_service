package com.yongyida.robot.voice.dao;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Administrator on 2016/4/8 0008.
 * by dean
 */
public class Friends implements BaseColumns {

	public static final String AUTHORITY = "com.yongyidarobot.provider";

    // 通过这个 Uri 操作 ROBOTS_TABLE
    // content://com.yongyidarobot.provider/robotsfriends
    public static final Uri CONTENT_URI_ROBOTS
            = Uri.parse("content://" + AUTHORITY + "/robotsfriends");

    // 通过这个 Uri 操作 USERS_TABLE
    // content://com.yongyidarobot.provider/usersfriends
    public static final Uri CONTENT_URI_USERS = Uri.parse("content://" + AUTHORITY + "/usersfriends");

    public static final String ROBOTS_TABLE = "robots_friends";
    public static final String USERS_TABLE = "users_friends";

    public static final String ROBOTS_CONTROLLER = "controller";//1
    public static final String ROBOTS_RNAME = "rname";//2
    public static final String ROBOTS_SERIAL = "serial";//3
    public static final String ROBOTS_ONLINE = "online";//4
    public static final String ROBOTS_ROBOTSID = "robotsid";//5
    public static final String ROBOTS_RID = "rid";//6
    public static final String ROBOTS_ADDR = "addr";//7
    public static final String ROBOTS_BATTERY = "battery";//8
    public static final String ROBOTS_VERSION = "version";//9
    public static final String ROBOTS_ALIAS = "alias";//10

    public static final String USERS_CONTROLLER = "controller";//1
    public static final String USERS_PHONE = "phone";//2
    public static final String USERS_HEADSHOT = "headshot";//3
    public static final String USERS_NICKNAME = "nickname";//4
    public static final String USERS_NAME = "name";//5
    public static final String USERS_ID = "usersid";//6
    public static final String USERS_ALIAS = "alias";//7

    public static final String[] ALL_ROBOTS = {_ID,
            ROBOTS_CONTROLLER,
            ROBOTS_RNAME,
            ROBOTS_SERIAL,
            ROBOTS_ONLINE,
            ROBOTS_ROBOTSID,
            ROBOTS_RID,
            ROBOTS_ADDR,
            ROBOTS_BATTERY,
            ROBOTS_VERSION,
            ROBOTS_ALIAS};

    public static final String[] ALL_USERS = {_ID,
            USERS_CONTROLLER,
            USERS_PHONE,
            USERS_HEADSHOT,
            USERS_NICKNAME,
            USERS_NAME,
            USERS_ID,
            USERS_ALIAS};

    public static final String SQL_CREATE_TABLE_ROBOTS = String.format(
            "CREATE TABLE %s(_id integer primary key autoincrement, %s text, %s text, %s text, %s text, %s text, %s text, %s text, %s text, %s text, %s text)",
            ROBOTS_TABLE,
            ROBOTS_CONTROLLER,
            ROBOTS_RNAME,
            ROBOTS_SERIAL,
            ROBOTS_ONLINE,
            ROBOTS_ROBOTSID,
            ROBOTS_RID,
            ROBOTS_ADDR,
            ROBOTS_BATTERY,
            ROBOTS_VERSION,
            ROBOTS_ALIAS);

    public static final String SQL_CREATE_TABLE_USERS = String.format(
            "CREATE TABLE %s(_id integer primary key autoincrement, %s text, %s text, %s text, %s text, %s text, %s text, %s text)",
            USERS_TABLE,
            USERS_CONTROLLER,
            USERS_PHONE,
            USERS_HEADSHOT,
            USERS_NICKNAME,
            USERS_NAME,
            USERS_ID,
            USERS_ALIAS);

    public static final String SQL_DROP_TABLE_ROBOTS = String.format(
            "DROP TABLE IF EXISTS %s",
            ROBOTS_TABLE);

    public static final String SQL_DROP_TABLE_USERS = String.format(
            "DROP TABLE IF EXISTS %s",
            USERS_TABLE);
}
