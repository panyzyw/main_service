package com.zccl.ruiqianqi.mind.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.zccl.ruiqianqi.tools.StringUtils;

/**
 * Created by zc on 2016/4/8.
 */
public class MyProvider extends ContentProvider {

    /** 数据库类 */
    private MySQLite mySqlite;
    /** 操作 userdata表 所有数据 */
    private static final int USER_DATA_ALL = 1;
    /** 操作 userdata表 指定数据 */
    private static final int USER_DATA_SINGLE = 2;

    /** URI检测与匹配类, NO_MATCH表示不匹配任何路径的返回码，清空匹配 */
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    /**
     *   注意*和#这两个符号在Uri中的作用
     *   其中*表示匹配任意长度的字符
     *   其中#表示匹配任意长度的数据
     *   所以：
     *   一个能匹配所有表的Uri可以写成:
     *   content://com.zccl.MyProvider/*
     *   一个能匹配person表中任意一行的Uri可以写成:
     *   content://com.zccl.MyProvider/person/#
     */
    static {
        // 匹配：content://com.zccl.MyProvider/userdata，返回值为1
        MATCHER.addURI(MyProviderMetaData.AUTHORITY, "userdata", USER_DATA_ALL);

        // 匹配：content://com.zccl.MyProvider/userdata/10，返回值为2
        // 后面加了#表示为数字通配符
        MATCHER.addURI(MyProviderMetaData.AUTHORITY, "userdata/#", USER_DATA_SINGLE);

    }

    @Override
    public boolean onCreate() {
        mySqlite = new MySQLite(getContext(), MyProviderMetaData.DATABASE_VERSION);
        // 访问到其中任何一个才会创建数据库
        mySqlite.getReadableDatabase();
        mySqlite.getWritableDatabase();
        return false;
    }

    /**
     * 增    insert into table(field1,field2) values(value1,value2)
     * @param uri        必须是全部的URI
     * @param values
     * @return
     */
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mySqlite.getWritableDatabase();
        switch(MATCHER.match(uri)){
            case USER_DATA_ALL:
                //name字段是为防止values是空的时候一种错误处理机制，就是把name字段置为空，让你知道插入失败
                Long rowId = db.insert(MyProviderMetaData.UserTableMetaData.USER_DATA_TABLE_NAME, MyProviderMetaData.UserTableMetaData.USER_NAME, values);
                if (rowId > 0) {
                    //Uri insertUri = ContentUris.withAppendedId(MyProviderMetaData.UserTableMetaData.CONTENT_URI, rowid);
                    //向外界通知该ContentProvider里的数据发生了变化 ,以便ContentObserver作出相应
                    //getContext().getContentResolver().notifyChange(insertUri, null);

                    //向外界通知该ContentProvider里的数据发生了变化 ,以便ContentObserver作出相应
                    getContext().getContentResolver().notifyChange(uri, null);
                    Uri insertUri = ContentUris.withAppendedId(uri, rowId);
                    return insertUri;
                }
                throw new SQLException("Failed to insert row into" + uri);
            default:
                throw new IllegalArgumentException("Unknown Uri:"+uri.toString());
        }
    }

    /**
     * 删    delete from table where id=2 and xxx=yyy
     * @param uri
     * @param selection       删除时参考的字段
     * @param selectionArgs  对应的值
     * @return
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mySqlite.getWritableDatabase();
        int deletedNum  = 0;
        switch(MATCHER.match(uri)){
            case USER_DATA_ALL:
                deletedNum = db.delete(MyProviderMetaData.UserTableMetaData.USER_DATA_TABLE_NAME, selection, selectionArgs);
                break;

            case USER_DATA_SINGLE:
                //contentUri.getLastPathSegment();
                long id = ContentUris.parseId(uri);
                String where = MyProviderMetaData.UserTableMetaData._ID + "=" + id;
                if(!StringUtils.isEmpty(selection)){
                    where = selection + " and " + where;
                }
                deletedNum = db.delete(MyProviderMetaData.UserTableMetaData.USER_DATA_TABLE_NAME, where, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown Uri:" + uri.toString());
        }

        // 向外界通知该ContentProvider里的数据发生了变化 ,以便ContentObserver作出相应
        getContext().getContentResolver().notifyChange(uri, null);

        return deletedNum ;
    }

    /**
     * 改    update table set field=value where id=2 and xxx=yyy
     * @param uri
     * @param values
     * @param selection      更改时参考的字段
     * @param selectionArgs  对应的值
     * @return
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mySqlite.getWritableDatabase();
        int updatedNum = 0;
        switch(MATCHER.match(uri)){
            case USER_DATA_ALL:
                updatedNum = db.update(MyProviderMetaData.UserTableMetaData.USER_DATA_TABLE_NAME, values, selection, selectionArgs);
                break;

            case USER_DATA_SINGLE:
                //contentUri.getLastPathSegment();
                long id = ContentUris.parseId(uri);
                String where = MyProviderMetaData.UserTableMetaData._ID + "=" + id;
                if(!StringUtils.isEmpty(selection)){
                    where = selection + " and " + where;
                }
                updatedNum = db.update(MyProviderMetaData.UserTableMetaData.USER_DATA_TABLE_NAME, values, where, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown Uri:" + uri.toString());
        }

        //向外界通知该ContentProvider里的数据发生了变化 ,以便ContentObserver作出相应
        getContext().getContentResolver().notifyChange(uri, null);

        return updatedNum;
    }

    /**
     * 查    select * from table where field1 like ’%value1%’
     * @param uri
     * @param projection        要查的字段
     * @param selection         查询时参考的字段
     * @param selectionArgs    对应的值
     * @param sortOrder        排序
     * @return
     */
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // 传入的排序参数为空的时候采用默认的排序
        if(StringUtils.isEmpty(sortOrder)){
            sortOrder = MyProviderMetaData.UserTableMetaData.DEFAULT_SORT_ORDER;
        }

        SQLiteDatabase db = mySqlite.getWritableDatabase();
        switch(MATCHER.match(uri)){
            case USER_DATA_ALL:
                return db.query(MyProviderMetaData.UserTableMetaData.USER_DATA_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

            case USER_DATA_SINGLE:
                //contentUri.getLastPathSegment();
                long id = ContentUris.parseId(uri);
                String where = MyProviderMetaData.UserTableMetaData._ID + "=" + id;
                if(!StringUtils.isEmpty(selection)){
                    where = selection + " and " + where;
                }
                return db.query(MyProviderMetaData.UserTableMetaData.USER_DATA_TABLE_NAME, projection, where, selectionArgs, null, null, sortOrder);
            default:
                throw new IllegalArgumentException("Unknown Uri:" + uri.toString());
        }
    }

    /**
     * 在自定义ContentProvider中必须覆写getType(Uri uri)方法.
     * 该方法用于获取Uri对象所对应的MIME类型.
     *
     * 一个Uri对应的MIME字符串遵守以下三点:
     * 1  必须以vnd开头
     * 2  如果该Uri对应的数据可能包含多条记录,那么返回字符串应该以vnd.android.cursor.dir/开头
     * 3  如果该Uri对应的数据只包含一条记录,那么返回字符串应该以vnd.android.cursor.item/开头
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(Uri uri) {
        switch(MATCHER.match(uri)){
            case USER_DATA_ALL:
                return MyProviderMetaData.UserTableMetaData.CONTENT_TYPE;

            case USER_DATA_SINGLE:
                return MyProviderMetaData.UserTableMetaData.CONTENT_TYPE_ITEM;

            default:
                throw new IllegalArgumentException("Unknown Uri:" + uri.toString());
        }
    }
}
