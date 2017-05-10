package com.yongyida.robot.voice.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "http_data.db"; //数据库名称
    private static final int DB_VERSION = 8; //数据库版本

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqlitedatabase) {  //(_id integer primary key autoincrement,sname text,snumber text)
        String sql = "CREATE TABLE http_url_table(_id integer primary key autoincrement,http_server_host text,http_resouce_host text,robot_tcp_host text,test_http_host text,test_tcp_host text,port_socket text,test_http_resouce_host text)";

        sqlitedatabase.execSQL(Friends.SQL_CREATE_TABLE_ROBOTS);
        sqlitedatabase.execSQL(Friends.SQL_CREATE_TABLE_USERS);
        sqlitedatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqlitedatabase, int i, int j) {
        String sql = "CREATE TABLE http_url_table(_id integer primary key autoincrement,http_server_host text,http_resouce_host text,robot_tcp_host text,test_http_host text,test_tcp_host text,port_socket text,test_http_resouce_host text)";
        sqlitedatabase.execSQL(Friends.SQL_CREATE_TABLE_ROBOTS);
        sqlitedatabase.execSQL(Friends.SQL_CREATE_TABLE_USERS);
        sqlitedatabase.execSQL(sql);
    }
}
