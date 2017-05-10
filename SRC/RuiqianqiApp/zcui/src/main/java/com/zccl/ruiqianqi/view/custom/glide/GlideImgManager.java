package com.zccl.ruiqianqi.view.custom.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.DiskCache;

import java.io.File;

/**
 * Created by ruiqianqi on 2016/11/17 0017.
 */

public class GlideImgManager {

    /**
     * 返回这个什么对象
     * @param context
     * @param imageUrl
     * @return
     */
    public static DrawableRequestBuilder getBuilder(Context context, String imageUrl){
        DrawableRequestBuilder requestBuilder = Glide.with(context).load(imageUrl);
        return requestBuilder;
    }


    /**
     * 原生API测试
     * @param context
     * @param url
     * @param errImg
     * @param emptyImg
     * @param iv
     */
    public static void glideTest(Context context, String url, int errImg, int emptyImg, ImageView iv){
        //原生 API
        Glide.with(context).load(url).
                centerCrop().
                // let Glide cache both the full-size image and the resized one
                // The next time image is requested to show on any ImageView,
                // the full-size image would be loaded from cache, resized and then cached.
                /*
                DiskCacheStrategy.NONE 什么都不缓存，就像刚讨论的那样
                DiskCacheStrategy.SOURCE 仅仅只缓存原来的全分辨率的图像。在我们上面的例子中，将会只有一个 1000x1000 像素的图片
                DiskCacheStrategy.RESULT 仅仅缓存最终的图像，即，降低分辨率后的（或者是转换后的）
                DiskCacheStrategy.ALL 缓存所有版本的图像（默认行为）
                */
                diskCacheStrategy(DiskCacheStrategy.ALL).
                skipMemoryCache(true).// 跳过内存缓存，就是不将图片缓存到内存缓存中
                crossFade().// 加载图片时候淡入淡出动画
                dontAnimate().// 取消淡入淡出效果
                override(100, 100). //(设置图片大小，可能导致图片失真变形)
                placeholder(emptyImg).// 设置在加载图片过程图显示的占位图
                error(errImg).// 图片加载错误时候的加载
                /*
                Priority.LOW
                Priority.NORMAL
                Priority.HIGH
                Priority.IMMEDIATE
                 */
                priority(Priority.NORMAL).// 图片加载优先级
                /**
                 使用缩略图的话，就要考虑ImageView 确保ScaleType 的属性问题。要不然加载出来的图片会很小。
                 */
                thumbnail(0.1f). // 载缩略图，为原图的十分之一，
                into(iv);
    }

    /**
     * 加载指定ID图片资源
     * @param context
     * @param resourceId
     * @param errImg
     * @param emptyImg
     * @param iv
     * @param tag
     */
    public static void glideLoader(Context context, int resourceId, int errImg, int emptyImg, ImageView iv, int tag){
        if (0 == tag) {
            Glide.with(context).load(resourceId).
                    placeholder(emptyImg).
                    error(errImg).
                    transform(new GlideCircleTransform(context)).
                    into(iv);

        }else if(1 == tag){
            Glide.with(context).load(resourceId).
                    placeholder(emptyImg).
                    error(errImg).
                    transform(new GlideRoundTransform(context, 10)).
                    into(iv);
        }else {
            Glide.with(context).load(resourceId).
                    placeholder(emptyImg).
                    error(errImg).
                    into(iv);
        }
    }

    /**
     * 加载指定path图片
     * load normal  for  circle or round img
     * @param context
     * @param url
     * @param errImg
     * @param emptyImg
     * @param iv
     * @param tag
     */
    public static void glideLoader(Context context, String url, int errImg, int emptyImg, ImageView iv, int tag) {
        if (0 == tag) {
            Glide.with(context).load(url).
                    placeholder(emptyImg).
                    error(errImg).
                    transform(new GlideCircleTransform(context)).
                    into(iv);
        } else if (1 == tag) {
            Glide.with(context).load(url).
                    placeholder(emptyImg).
                    error(errImg).
                    transform(new GlideRoundTransform(context, 10)).
                    into(iv);
        }else {
            Glide.with(context).load(url).
                    placeholder(emptyImg).
                    error(errImg).
                    into(iv);
        }
    }

    /**
     * 加载指定path图片
     * load normal  for  circle or round img
     * @param context
     * @param url
     * @param errImg
     * @param emptyImg
     * @param iv
     * @param tag
     */
    public static void glideLoader(Context context, String url, Drawable errImg, Drawable emptyImg, ImageView iv, int tag) {
        if (0 == tag) {
            Glide.with(context).load(url).
                    placeholder(emptyImg).
                    error(errImg).
                    transform(new GlideCircleTransform(context)).
                    into(iv);
        } else if (1 == tag) {
            Glide.with(context).load(url).
                    placeholder(emptyImg).
                    error(errImg).
                    transform(new GlideRoundTransform(context, 10)).
                    into(iv);
        }else {
            Glide.with(context).load(url).
                    placeholder(emptyImg).
                    error(errImg).
                    into(iv);
        }
    }

    /**
     * 清理缓存
     * @param context
     */
    public static void clearDiskCache(Context context){
        Glide.get(context).clearDiskCache();
    }

    /**
     * 获取缓存大小
     * @param dir 缓存目录
     * @return 缓存的大小
     */
    private static long calculateSize(File dir) {
        if (dir == null) return 0;
        if (!dir.isDirectory()) return dir.length();
        long result = 0;
        File[] children = dir.listFiles();
        if (children != null)
            for (File child : children)
                result += calculateSize(child);
        return result;
    }

    /**
     * 获取缓存大小
     * @param context
     * @return
     */
    public static long getDiskCacheSize(Context context){
        long totalSize = calculateSize(new File(context.getCacheDir(), DiskCache.Factory.DEFAULT_DISK_CACHE_DIR));
        return totalSize;
    }
}
