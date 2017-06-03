package com.zccl.ruiqianqi.tools.ui;

import android.annotation.SuppressLint;
import android.content.Context;

import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.MYUIUtils;

/**
 * Created by ruiqianqi on 2016/11/2 0002.
 */

public class JavaScript {
    private static String TAG = JavaScript.class.getSimpleName();
    private Context mContext;

    public JavaScript(Context mContext) {
        this.mContext = mContext;
    }

    public void showStr(String name) {
        MYUIUtils.showToast(mContext, name);
    }

    public void logStr(String name) {
        LogUtils.e(TAG, name);
    }
}
