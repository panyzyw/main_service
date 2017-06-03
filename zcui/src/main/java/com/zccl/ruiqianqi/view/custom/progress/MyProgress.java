package com.zccl.ruiqianqi.view.custom.progress;

import android.content.Context;
import android.widget.ProgressBar;

import com.zccl.ruiqianqi.zcui.R;


/**
 * Created by ruiqianqi on 2016/11/16 0016.
 */
public class MyProgress {

    private Context mContext;
    public MyProgress(Context context){
        this.mContext = context;
    }
    /**
     * 非确定进度的进度条
     style="?android:attr/progressBarStyleHorizontal"
     style="?android:attr/progressBarStyleSmall"
     style="?android:attr/progressBarStyleSmallInverse"
     style="?android:attr/progressBarStyleSmallTitle"
     style="?android:attr/progressBarStyle"
     style="?android:attr/progressBarStyleInverse"
     style="?android:attr/progressBarStyleLarge"
     style="?android:attr/progressBarStyleLargeInverse"

     style="@android:style/Widget.ProgressBar.Horizontal"
     style="@android:style/Widget.ProgressBar.Small"
     style="@android:style/Widget.ProgressBar.Small.Inverse"
     style="@android:style/Widget.ProgressBar.Inverse"
     style="@android:style/Widget.ProgressBar.Large"
     style="@android:style/Widget.ProgressBar.Large.Inverse"
     * @param progressBar
     */
    public void indeterminate(ProgressBar progressBar){
        // 超小圆形
        int style = android.R.attr.progressBarStyleSmallTitle;
        // 小圆形
        style = android.R.attr.progressBarStyleSmall;
        // 反转小圆形
        style = android.R.attr.progressBarStyleSmallInverse;
        // 默认中等圆形
        style = android.R.attr.progressBarStyle;
        // 反转中等圆形
        style = android.R.attr.progressBarStyleInverse;
        // 长方形进度条
        style = android.R.attr.progressBarStyleHorizontal;
        // 超大圆形
        style = android.R.attr.progressBarStyleLarge;
        // 反转超大圆形
        style = android.R.attr.progressBarStyleLargeInverse;

        // 设置为非确定的进度条
        progressBar.setIndeterminate(true);
        // 选择系统背景
        progressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(android.R.drawable.progress_indeterminate_horizontal));
        // 选择自定义背景
        progressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.progressbar_not_sure_style1));
    }

    /**
     * 确定进度的进度条
     style="?android:attr/progressBarStyleHorizontal"
     style="@android:style/Widget.ProgressBar.Horizontal"
     * @param progressBar
     */
    public void determinate(ProgressBar progressBar){
        // 长方形进度条
        int style = android.R.attr.progressBarStyleHorizontal;

        // 设置为确定的进度条
        progressBar.setIndeterminate(false);
        // 选择系统背景
        progressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(android.R.drawable.progress_horizontal));
        // 选择自定义背景
        progressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.progressbar_sure_style));
        progressBar.setMax(100);
        progressBar.setProgress(0);
        progressBar.setSecondaryProgress(0);
    }
}
