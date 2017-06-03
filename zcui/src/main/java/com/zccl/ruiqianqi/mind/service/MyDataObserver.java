package com.zccl.ruiqianqi.mind.service;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.zccl.ruiqianqi.mind.provider.MyProviderMetaData;
import com.zccl.ruiqianqi.tools.LogUtils;

/**
 * Created by ruiqianqi on 2016/7/21 0021.
 */
public class MyDataObserver extends ContentObserver {

    /** 类标志 */
    private static String TAG = MyDataObserver.class.getSimpleName();

    /** 生命周期最长的上下文 */
    private Context context;

    /** 记录上一次操作时间 */
    private long lastTime = 0;

    /**
     * Creates a content observer.
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public MyDataObserver(Context context, Handler handler) {
        super(handler);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        if (System.currentTimeMillis() - lastTime > 2000) {
            ContentResolver resolver = context.getContentResolver();
            Uri uri = MyProviderMetaData.UserTableMetaData.CONTENT_URI;

            // 获取最新的一条数据
            Cursor cursor = resolver.query(uri, null, null, null, MyProviderMetaData.UserTableMetaData.DEFAULT_SORT_ORDER);
            if (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(MyProviderMetaData.UserTableMetaData.USER_NAME));
                LogUtils.e(TAG, "最新数据的名字："+name);
            }
            cursor.close();

            lastTime  =System.currentTimeMillis();

        }else{
            LogUtils.e(TAG, "时间间隔过短,忽略此次更新");
        }

    }

}
