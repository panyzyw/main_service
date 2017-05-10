package com.yongyida.robot.voice.dao;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;


public class RobotsFriendsProvider extends ContentProvider {

    private static final UriMatcher matcher
            = new UriMatcher(UriMatcher.NO_MATCH);

    // 整体
    private static final int ROBOTS_FRIENDS = 1;

    //小勇RID
    private static final int ROBOTS_FRIENDS_RID = 2;

    // 整体
    private static final int USERS_FRIENDS = 3;

    //手机号码
    private static final int USERS_FRIENDS_PHONE = 4;

    private SQLiteDatabase database;

    static {
// content://com.yongyidarobot.provide/robotsfriends
        matcher.addURI(Friends.AUTHORITY, "robotsfriends", ROBOTS_FRIENDS);

// content://com.yongyidarobot.provide/robotsfriends/123
        matcher.addURI(Friends.AUTHORITY, "robotsfriends/*", ROBOTS_FRIENDS_RID);

// content://com.yongyidarobot.provide/usersfriends
        matcher.addURI(Friends.AUTHORITY, "usersfriends", USERS_FRIENDS);

// content://com.yongyidarobot.provide/usersfriends/123
        matcher.addURI(Friends.AUTHORITY, "usersfriends/*", USERS_FRIENDS_PHONE);
    }

    public RobotsFriendsProvider() {
    }

    @Override
    public boolean onCreate() {
        //database = new DatabaseHelper(getContext()).getWritableDatabase();
		database = DatabaseHelper.getInstance(getContext()).getWritableDatabase();
        return database != null;
    }

    @Override
    public String getType(Uri uri) {
        switch (matcher.match(uri)) {
            case ROBOTS_FRIENDS:
                // vnd.android.cursor.dir/vnd.yongyidarobot.provider.robotsfriends
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.yongyidarobot.provider.robotsfriends";

            case ROBOTS_FRIENDS_RID:
                // vnd.android.cursor.item/vnd.yongyidarobot.provider.robotsfriends
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.yongyidarobot.provider.robotsfriends";

            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (matcher.match(uri)) {
            case ROBOTS_FRIENDS:
                // content://com.yongyidarobot.provide/robotsfriends
                long robotsId = database.insert(
                        Friends.ROBOTS_TABLE,
                        null,
                        values);

                // content://com.yongyidarobot.provide/robotsfriends/1
                return Uri.withAppendedPath(
                        uri,
                        String.valueOf(robotsId));

            case USERS_FRIENDS:
                // content://com.yongyidarobot.provide/usersfriends
                long usersId = database.insert(
                        Friends.USERS_TABLE,
                        null,
                        values);

                // content://com.yongyidarobot.provide/usersfriends/1
                return Uri.withAppendedPath(
                        uri,
                        String.valueOf(usersId));

            case UriMatcher.NO_MATCH:
                throw new IllegalArgumentException(uri.toString());
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int n = 0;
        switch (matcher.match(uri)) {
            case ROBOTS_FRIENDS:
                n = database.delete(Friends.ROBOTS_TABLE, selection, selectionArgs);
                break;
            case ROBOTS_FRIENDS_RID:
                // content://com.yongyidarobot.provide/robotsfriends/*
                String rid = uri.getLastPathSegment();
                n = database.delete(
                        Friends.ROBOTS_TABLE,
                        "rid = ?",
                        new String[]{rid});
                break;

            case USERS_FRIENDS:
                n = database.delete(Friends.USERS_TABLE, selection, selectionArgs);
                break;
            case USERS_FRIENDS_PHONE:
                // content://com.yongyidarobot.provide/usersfriends/*
                String phone = uri.getLastPathSegment();
                n = database.delete(
                        Friends.USERS_TABLE,
                        "phone = ?",
                        new String[]{phone});
                break;

            default:
                throw new IllegalArgumentException(uri.toString());
        }
        return n;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        switch (matcher.match(uri)) {
            case ROBOTS_FRIENDS:
                // content://com.yongyidarobot.provide/robotsfriends
                cursor = database.query(Friends.ROBOTS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case ROBOTS_FRIENDS_RID:
                // content://com.yongyidarobot.provide/robotsfriends/*
                String rid = uri.getLastPathSegment();
                cursor = database.query(
                        Friends.ROBOTS_TABLE,
                        projection,
                        "rid = ?",
                        new String[]{rid},
                        null,
                        null,
                        null);
                break;

            case USERS_FRIENDS:
                // content://com.yongyidarobot.provide/usersfriends
                cursor = database.query(Friends.USERS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case USERS_FRIENDS_PHONE:
                // content://com.yongyidarobot.provide/usersfriends/*
                String phone = uri.getLastPathSegment();
                cursor = database.query(
                        Friends.USERS_TABLE,
                        projection,
                        "phone = ?",
                        new String[]{phone},
                        null,
                        null,
                        null);
                break;
            default:
                throw new IllegalArgumentException(uri.toString());

        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int n = 0;
        switch (matcher.match(uri)) {
            case ROBOTS_FRIENDS:
                n = database.update(Friends.ROBOTS_TABLE, values, selection, selectionArgs);
                break;

            case ROBOTS_FRIENDS_RID:
                // content://com.yongyidarobot.provide/friends/*
                String rid = uri.getLastPathSegment();
                n = database.update(
                        Friends.ROBOTS_TABLE,
                        values,
                        "rid = ?",
                        new String[]{rid});
                break;

            case USERS_FRIENDS:
                n = database.update(Friends.USERS_TABLE, values, selection, selectionArgs);
                break;

            case USERS_FRIENDS_PHONE:
                // content://com.yongyidarobot.provide/usersfriends/*
                String phone = uri.getLastPathSegment();
                n = database.update(
                        Friends.USERS_TABLE,
                        values,
                        "phone = ?",
                        new String[]{phone});
                break;
            default:
                throw new IllegalArgumentException(uri.toString());
        }
        return n;
    }

/*class DbHelper extends SQLiteOpenHelper {

private static final String DB_ROBOTS_NAME = "friends.db";

public DbHelper(Context context, int version) {
    super(context, DatabaseHelper.DB_NAME, null, version);
}

@Override
public void onCreate(SQLiteDatabase db) {
    db.execSQL(Friends.SQL_CREATE_TABLE_ROBOTS);
    db.execSQL(Friends.SQL_CREATE_TABLE_USERS);
}

@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL(Friends.SQL_DROP_TABLE_ROBOTS);
    db.execSQL(Friends.SQL_DROP_TABLE_USERS);
    onCreate(db);
}
}*/
}
