package com.zccl.ruiqianqi.mind.service.observer;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.db.DbContact;

/**
 * Created by ruiqianqi on 2016/9/24 0024.
 */

public class ContactObserver extends ContentObserver {

    private static String TAG = ContactObserver.class.getSimpleName();

    /**
     * 全局上下文
     */
    private Context mContext;

    /**
     * 发音设备
     */
    private AbstractVoice voiceDevice;

    /**
     * Creates a content observer.
     *
     * @param context
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public ContactObserver(Context context, Handler handler) {
        super(handler);
        this.mContext = context;
    }

    /**
     * @param selfChange
     * @param uri        content://com.android.contacts
     */
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        LogUtils.e(TAG, selfChange + " - " + uri);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        LogUtils.e(TAG, selfChange + "");

        // 直接查出来，然后，更新联系人
        String contactInfoS = DbContact.queryContactNames(mContext);
        if (voiceDevice != null) {
            voiceDevice.updateRule("contact", contactInfoS);
        }

    }

    /***************************************设置自身的参数*****************************************/
    /**
     * 设置发音设备
     *
     * @param voiceDevice
     */
    public void setVoiceDevice(AbstractVoice voiceDevice) {
        this.voiceDevice = voiceDevice;
    }
}
