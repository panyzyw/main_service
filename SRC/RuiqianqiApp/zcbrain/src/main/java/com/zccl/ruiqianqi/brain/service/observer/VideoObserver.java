package com.zccl.ruiqianqi.brain.service.observer;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.zccl.ruiqianqi.presentation.presenter.StatePresenter;
import com.zccl.ruiqianqi.tools.LogUtils;

/**
 * Created by ruiqianqi on 2017/3/10 0010.
 */

public class VideoObserver extends ContentObserver {

    // 类标志
    private static String TAG = VideoObserver.class.getSimpleName();

    // 视频配置数据库对外地址
    public static final String VIDEO_URI = "content://com.yongyida.robot.video.provider/config";
    // 全局上下文
    private Context mContext;
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public VideoObserver(Context context, Handler handler) {
        super(handler);
        this.mContext = context;
    }

    @Override
    public void onChange(boolean selfChange) {
        boolean isVideo = isVideoing(mContext);
        StatePresenter sp = StatePresenter.getInstance();
        sp.setVideoing(isVideo);
    }

    /**
     * 查询机器人是否处于视频状态
     * @param context
     * @return true, 正在视频中
     *         false, 不在视频中
     */
    private boolean isVideoing(Context context) {
        String value = null;
        Cursor cursor = null;
        try {
            Uri uri = Uri.parse(VIDEO_URI);
            cursor = context.getContentResolver().query(uri, null, "name = ?", new String[]{"videoing"}, null);
            if (null != cursor && cursor.moveToFirst()) {
                value = cursor.getString(cursor.getColumnIndex("value"));
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "", e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return ("true").equals(value);
    }
}
