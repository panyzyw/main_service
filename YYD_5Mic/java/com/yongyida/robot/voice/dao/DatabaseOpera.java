package com.yongyida.robot.voice.dao;

import com.yongyida.robot.voice.robot.SwitchVersion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseOpera {
    DatabaseHelper helper;

    public DatabaseOpera(Context context) {
        //helper = new DatabaseHelper(context);
        helper = DatabaseHelper.getInstance(context);
    }

    public void insert() {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("http_server_host", "server.yydrobot.com");
            cv.put("http_resouce_host", "resource.yydrobot.com");
            cv.put("robot_tcp_host", "robot.yydrobot.com");
            cv.put("test_http_host", "120.24.242.163:81");
			cv.put("test_http_resouce_host","120.24.242.163");
            cv.put("test_tcp_host", "120.24.242.163");
            cv.put("port_socket", "8001");
            db.insert("http_url_table", null, cv);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void update(int version) {
        SQLiteDatabase db = helper.getReadableDatabase();
        ContentValues cv = new ContentValues();

        switch (version) {
            case SwitchVersion.Y50B_DEBUG_VERSION:
                cv.put("http_server_host", "120.24.242.163:81");
				cv.put("http_resouce_host","120.24.242.163");
                cv.put("robot_tcp_host", "120.24.242.163");
                break;
            case SwitchVersion.Y50B_DEV_VERSION:
                cv.put("http_server_host", "120.24.213.239:81");
				cv.put("http_resouce_host","120.24.242.163");
                cv.put("robot_tcp_host", "120.24.213.239");
                break;
            case SwitchVersion.Y50B_FORMAL_VERSION:

                cv.put("http_server_host", "server.yydrobot.com");
                cv.put("http_resouce_host", "resource.yydrobot.com");
                cv.put("robot_tcp_host", "robot.yydrobot.com");

                break;
            case SwitchVersion.Y50B_HK_VERSION:

                break;
        }

        cv.put("test_http_host", "120.24.242.163:81");
		cv.put("test_http_resouce_host","120.24.242.163");
        cv.put("test_tcp_host", "120.24.242.163");
        cv.put("port_socket", "8001");
        db.update("http_url_table", cv, "1", null);

    }

    public Cursor query() {
        Cursor c = null;
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            c = db.query("http_url_table", null, null, null, null, null, null);//查询并获得游标
        } catch (Throwable e) {
            if (c != null) {
                c.close();
            }
        }
        return c;
    }
}
