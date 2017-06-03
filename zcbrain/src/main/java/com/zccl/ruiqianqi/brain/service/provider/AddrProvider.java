package com.zccl.ruiqianqi.brain.service.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zccl.ruiqianqi.presentation.presenter.PersistPresenter;
import com.zccl.ruiqianqi.storage.db.MyDbFlow;
import com.zccl.ruiqianqi.tools.LogUtils;

import static com.zccl.ruiqianqi.brain.service.provider.AddrMateData.AUTHORITY;
import static com.zccl.ruiqianqi.brain.service.provider.AddrMateData.AUTHORITY_TABLE;

/**
 * Created by ruiqianqi on 2017/4/17 0017.
 */

public class AddrProvider extends ContentProvider {

    /** 操作 BrainDB 表 所有数据 */
    private static final int HTTP_REQUEST_ALL = 1;
    /** 操作 BrainDB 表 指定数据 */
    private static final int HTTP_REQUEST_SINGLE = 2;

    /** URI检测与匹配类, NO_MATCH表示不匹配任何路径的返回码，清空匹配 */
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    /**
     *   注意*和#这两个符号在Uri中的作用
     *   其中*表示匹配任意长度的字符
     *   其中#表示匹配任意长度的数字
     *   所以：
     *   一个能匹配所有表的Uri可以写成:
     *   content://com.yongyida.robot.voice.master.httprequest/*
     *   一个能匹配http_request表中任意一行的Uri可以写成:
     *   content://com.yongyida.robot.voice.master.httprequest/http_request/#
     */
    static {
        // 匹配：content://com.yongyida.robot.voice.master.httprequest/http_request，返回值为1
        MATCHER.addURI(AUTHORITY, AUTHORITY_TABLE, HTTP_REQUEST_ALL);

        // 匹配：content://com.yongyida.robot.voice.master.httprequest/http_request/10，返回值为2
        // 后面加了#表示为数字通配符
        MATCHER.addURI(AUTHORITY, AUTHORITY_TABLE + "/#", HTTP_REQUEST_SINGLE);

    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // 传入的排序参数为空的时候采用默认的排序
        switch(MATCHER.match(uri)){
            case HTTP_REQUEST_ALL:
                Cursor cursor = MyDbFlow.queryServerBeanCursor(PersistPresenter.getInstance().getServerAddr(), true);
                return cursor;

            case HTTP_REQUEST_SINGLE:

                break;

            default:
                throw new IllegalArgumentException("Unknown Uri:" + uri.toString());
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
