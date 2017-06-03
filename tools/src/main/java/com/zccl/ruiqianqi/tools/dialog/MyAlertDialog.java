package com.zccl.ruiqianqi.tools.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by ruiqianqi on 2016/8/30 0030.
 */
public class MyAlertDialog {
    /**
     * 弹出多项选择
     * @param context  全局上下文
     * @param title    单选框标题
     * @param choices  单选框有几项,各是什么名字
     * @param selected 默认选项是第几项
     * @param listener 点击单选框后的处理
     */
    public static void showChoice(Context context, String title, String[] choices, int selected, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setSingleChoiceItems(choices, selected, listener);
        builder.show();
    }

    /**
     * 弹出确认提示框
     * @param context   全局上下文
     * @param title       确认提示框标题
     * @param content       确认内容
     * @param positiveListener 确认回调
     */
    public static void showConfirm(Context context, String title, String content, DialogInterface.OnClickListener positiveListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton("确定", positiveListener)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    /**
     * 弹出确认提示框
     * @param context  全局上下文
     * @param title       确认提示框标题
     * @param content        确认内容
     * @param positiveListener  确认回调
     */
    public static void showConfirmNeutral(Context context, String title, String content, DialogInterface.OnClickListener positiveListener){
        // 通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton("确定", positiveListener)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNeutralButton("忽略", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        //显示出该对话框
        builder.show();
    }
}
