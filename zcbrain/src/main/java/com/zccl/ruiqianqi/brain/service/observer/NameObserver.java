package com.zccl.ruiqianqi.brain.service.observer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.zccl.ruiqianqi.brain.eventbus.MindBusEvent;
import com.zccl.ruiqianqi.domain.model.Robot;
import com.zccl.ruiqianqi.presentation.presenter.StatePresenter;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.tools.http.Tls12SocketFactory;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by ruiqianqi on 2017/3/10 0010.
 */

public class NameObserver extends ContentObserver {

    // 类标志
    private static String TAG = NameObserver.class.getSimpleName();
    // 机器人名字数据库对外地址
    public static final String NAME_URI = "content://com.yongyida.robot.nameprovider//name";
    // 全局上下文
    private Context mContext;
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public NameObserver(Context context, Handler handler) {
        super(handler);
        this.mContext = context;
    }

    @Override
    public void onChange(boolean selfChange) {

        LogUtils.e(TAG, selfChange + "");
        Cursor cursor = null;
        try {
            Uri uri = Uri.parse(NAME_URI);
            ContentResolver resolver = mContext.getContentResolver();
            cursor = resolver.query(uri, null, null, null, null);
            if(null != cursor && cursor.moveToFirst()){
                String name = cursor.getString(cursor.getColumnIndex("name"));
                if(!StringUtils.isEmpty(name)){
                    name = name.trim();

                    StatePresenter sp = StatePresenter.getInstance();
                    Robot robotBak = sp.getRobot();
                    // 和临时持久信息备份的名字一亲，就不需要更新名字数据库
                    if(null != robotBak && name.equals(robotBak.getRname())){
                        return;
                    }

                    MindBusEvent.NameEvent nameEvent = new MindBusEvent.NameEvent();
                    nameEvent.setText(name);
                    EventBus.getDefault().post(nameEvent);

                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if(null != cursor){
                cursor.close();
            }
        }
    }
}
