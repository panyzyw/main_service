package com.zccl.ruiqianqi.tools;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import com.zccl.ruiqianqi.tools.beans.MyDownItem;
import com.zccl.ruiqianqi.tools.config.MyConfigure;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ruiqianqi on 2016/8/29 0029.
 */
public class Download {
    /**
     * 如果服务组件没有安装打开语音服务组件下载页面，进行下载后安装。
     */
    public static void downloadInstall(Context context, String url){
        //直接下载方式
        Uri uri = Uri.parse(url);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(it);
    }

    /**
     * 安装APK，要请求结果，安装APK的程序也是一个程序
     * FLAG_ACTIVITY_PREVIOUS_IS_TOP: If set and this intent is being used to launch a new activity from an existing one,
     *                             the current activity will not be counted as the top activity for deciding
     *                             whether the new intent should be delivered to the top instead of starting a new one.
     *                             The previous activity will be used as the top, with the assumption being that
     *                             the current activity will finish itself immediately.
     * @param context
     * @param filePath
     * @param requestCode --------- 请求结果
     */
    public static void installApk(Context context, String filePath, int requestCode) {
        String command = "chmod 777 " + filePath;
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent();
        //不能使用Intent.FLAG_ACTIVITY_NEW_TASK  是因为Intent.FLAG_ACTIVITY_NEW_TASK无法获得返回的结果；
        //不能使用Intent.FLAG_ACTIVITY_CLEAR_TOP 是因为可能会有多个apk同时安装；
        //FLAG_ACTIVITY_SINGLE_TOP: 如果当前栈顶的activity就是要启动的activity, 则不会再启动一个新的activity；
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
        if(context instanceof Activity){
            //或FLAG_ACTIVITY_PREVIOUS_IS_TOP
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP );
            ((Activity)context).startActivityForResult(intent, requestCode);
        }else{
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * 安装APK
     * @param context
     * @param filePath
     */
    public static void installApk(Context context, String filePath){
        String command = "chmod 777 " + filePath;
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
	    Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
		context.startActivity(intent);
    }

    /**
     * 安装Assets文件
     * @param context
     * @param assetsApk
     */
    public static void installAssets(Context context, String assetsApk){
        boolean isSuccess =  FileUtils.copyAssetsToFiles(context, assetsApk, assetsApk);
        if(isSuccess){
            String url = context.getFilesDir().getAbsolutePath()+ File.separator+assetsApk;
            installApk(context, url);
        }
    }

    /** 下载文件管理 */
    public static Map<Long, MyDownItem> homeDownFiles = new HashMap<>();
    /**
     * 文件下载完成的广播监听
     */
    public static BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                // 下载管理喊叫
                DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                // 取出携带的唯一的下载ID
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                // 查询对象
                DownloadManager.Query query = new DownloadManager.Query();
                // 设置过滤ID
                query.setFilterById(downloadId);

                // 查询结果
                Cursor cursor = null;
                try {
                    cursor = dm.query(query);
                    if (cursor != null && cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        LogUtils.e("下载完成"," 状态： "+ cursor.getInt(columnIndex));
                        if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columnIndex)) {
                            downloadFinish(context, downloadId, homeDownFiles);
                        }else {
                            MYUIUtils.showToast(context, "文件下载失败");
                        }
                    }
                }finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }else if(intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)){
                MYUIUtils.showToast(context, "别瞎点");
            }
        }
    };


    /**
     * 开始下载APK文件，并安装
     * @param context
     * @param downUrl
     * @param downFiles
     */
    public static void downloadInstall(Context context, String downUrl, Map<Long, MyDownItem> downFiles){

        if(StringUtils.isEmpty(downUrl)){
            Toast.makeText(context, "路径为空，无法下载！", Toast.LENGTH_LONG).show();
            return;
        }else if(!downUrl.endsWith(".apk")){
            Toast.makeText(context, "路径格式不正确，取消下载！"+downUrl, Toast.LENGTH_LONG).show();
            return;
        }
        if(downFiles==null){
            downFiles = homeDownFiles;
        }

        // 下载文件名
        String downloadFile = FileUtils.getFileName(downUrl);
        // 文件下载路径
        String downloadPath = MyConfigure.ZCCL_SDCARD + MyConfigure.DOWNLOAD;

        // 判断文件是否正在下载
        for (MyDownItem downItem : downFiles.values()) {
            if (downItem.getUrl().equals(downUrl)) {
                Toast.makeText(context, downloadFile+": 正在下载中，无需重复下载！", Toast.LENGTH_LONG).show();
                return;
            }
        }

        // 判断保存目录是否存在
        File downloadDir = new File(downloadPath);
        if(!downloadDir.exists()){
            downloadDir.mkdirs();
        }

        // 判断该文件是否已经下载
        File downFile = new File(downloadPath+downloadFile);
        if (downFile.exists()) {
            // 安装apk
            if(downFile.getAbsolutePath().endsWith(".apk")){
                installApk(context, downFile.getAbsolutePath(), 0);
            }
            return;
        }

        Uri downUri = Uri.parse(downUrl);
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(downUri);
        // 设置下载文件的mineType。因为下载管理Ui中点击某个已下载完成文件及下载完成点击通知栏提示都会根据mimeType去打开文件，
        // 所以我们可以利用这个属性。比如上面设置了mimeType为application/cn.trinea.download.file，
        // 我们可以同时设置某个Activity的intent-filter为application/cn.trinea.download.file，用于响应点击的打开文件。
        request.setMimeType("application/vnd.android.package-archive");

        // 加下载后缀
        File temFile = new File(downFile.getAbsolutePath() + ".tmp");
        // 删除未下载完成的文件
        FileUtils.deleteFile(temFile);
        // 直接指定保存路径Uri
        request.setDestinationUri(Uri.fromFile(temFile));
        //request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, downloadFile);

        // 下载中和完成后都提示
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 仅在WIFI下，才能下载
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        // 设置标题
        request.setTitle(downloadFile);
        // 设置描述
        request.setDescription("正在下载：" + downloadFile);
        // 表示允许MediaScanner扫描到这个文件，默认不允许。
        request.allowScanningByMediaScanner();
        // 下载请求分配的一个唯一的ID
        long enqueue = dm.enqueue(request);

        MyDownItem myDownItem = new MyDownItem();
        myDownItem.setId(enqueue);
        myDownItem.setUrl(downUrl);
        myDownItem.setPath(downFile.getAbsolutePath());
        downFiles.put(enqueue, myDownItem);

    }

    /**
     * 根据下载ID, 查询下载进度及状态
     * @param context
     * @param downloadId
     * @return
     */
    public static int[] getBytesAndStatus(Context context, long downloadId) {
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        int[] bytesAndStatus = new int[] { -1, -1, 0 };
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor cursor = null;
        try {
            cursor = dm.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                bytesAndStatus[0] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                bytesAndStatus[1] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                bytesAndStatus[2] = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return bytesAndStatus;
    }

    /**
     * 系统下载，下载完成处理
     * @param context
     * @param enqueue
     * @param downFiles
     */
    public static void downloadFinish(Context context, Long enqueue, Map<Long, MyDownItem> downFiles){
        if(downFiles!=null){
            if(downFiles.containsKey(enqueue)){
                String filePath = downFiles.get(enqueue).getPath();
                File file = new File(filePath + ".tmp");
                if (file.exists()){
                    File newFile = new File(filePath);
                    file.renameTo(newFile);
                    installApk(context, newFile.getAbsolutePath(), 0);
                }
                downFiles.remove(enqueue);
            }
        }
    }
}
