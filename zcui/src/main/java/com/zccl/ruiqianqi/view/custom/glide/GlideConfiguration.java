package com.zccl.ruiqianqi.view.custom.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by ruiqianqi on 2016/11/17 0017.
 *
 * 改变图片的默认加载质量
 * then define it as meta-data inside AndroidManifest.xml
 * <meta-data android:name="com.zccl.ruiqianqi.view.custom.glide.GlideConfiguration" android:value="GlideModule"/>
 */
public class GlideConfiguration implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Apply options to the builder here.
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // register ModelLoaders here.
    }
}
