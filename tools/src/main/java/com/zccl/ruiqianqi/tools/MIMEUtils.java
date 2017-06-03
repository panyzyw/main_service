package com.zccl.ruiqianqi.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;

public class MIMEUtils {

    //建立一个MIME类型与文件后缀名的匹配表
    private static final String[][] MIME_MapTable = {
            //{后缀名，      MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".prop", "text/plain"},
            {".rar", "application/x-rar-compressed"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/xml"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/zip"},
            {"", "*/*"}
    };

    /**
     * 用已安装的应用程序打开文件
     *
     * @param context
     * @param file
     */
    public static void openFile(Context context, File file) {
        Intent intent = new Intent();
        //获取文件file的MIME类型
        String type = getMIMEType(file);
        if (TextUtils.isEmpty(type)) {
            return;
        }
        //设置intent的data和Type属性。
        intent.setDataAndType(Uri.fromFile(file), type);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 用已安装的应用程序打开文件
     *
     * @param context
     * @param filepath
     */
    public static void openFile(Context context, String filepath) {
        Intent intent = new Intent();

        //获取文件file的MIME类型
        File file = new File(filepath);
        String type = getMIMEType(file);
        if (TextUtils.isEmpty(type)) {
            return;
        }

        //设置intent的data和Type属性。
        intent.setDataAndType(Uri.parse("file://" + filepath), type);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 弹出系统设置界面
     *
     * @param context
     */
    @SuppressLint("InlinedApi")
    public static void showAppDetailed(Context context) {
        boolean isSetShow = true;
        //4.0及其之上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

        }
        //3.0及其之上
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

        }
        //2.3
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {

        }
        //之下
        else {
            isSetShow = false;
        }
        if (isSetShow) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            context.startActivity(intent);
        }
    }

    /**
     * 打开网页
     *
     * @param context
     * @param website "www.google.com"
     */
    public static void openWebsite(Context context, String website) {
        Uri uri = Uri.parse(website);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 调起打电话功能
     *
     * @param context
     * @param phoneNumber 12315
     */
    public static void openDialCall(Context context, String phoneNumber) {
        Uri uri = Uri.parse("tel:" + phoneNumber);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        //Intent intent = new Intent(Intent.ACTION_CALL,uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 发送短信
     *
     * @param context
     * @param toNumber
     * @param smsBody
     */
    public static void openSMS(Context context, String toNumber, String smsBody) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + toNumber));

        intent.putExtra("sms_body", smsBody);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 发送短信
     *
     * @param context
     * @param toNumber
     * @param smsBody
     */
    public static void openSMS2(Context context, String toNumber, String smsBody) {
        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.setType("vnd.android-dir/mms-sms");
        intent.setData(Uri.parse("smsto:" + toNumber));
        intent.putExtra("sms_body", smsBody);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 打开截图分享功能
     *
     * @param context
     * @param jieTuPath
     */
    public static void openShare(Context context, String jieTuPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, "截图分享");
        intent.putExtra(Intent.EXTRA_TITLE, "截图分享");
        intent.putExtra(Intent.EXTRA_TEXT, "我的截图分享");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(jieTuPath)));
        intent.setType("image/*");
        intent = Intent.createChooser(intent, "分享");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 发邮件 Intent.ACTION_SENDTO  无附件的发送
     *
     * @param context
     * @param mailTo  发送给谁
     * @param title   主题
     * @param content 内容
     */
    public static void openEmail(Context context, String mailTo, String title, String content) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + mailTo));
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, content);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 发邮件 Intent.ACTION_SEND  带附件的发送
     *
     * @param context
     * @param mailto  收件者们
     * @param cc      抄送者们
     * @param bcc     密送者们
     * @param title   主题
     * @param content 内容
     * @param addfile 附件
     */
    public static void openEmail2(Context context, String[] mailto, String[] cc, String[] bcc,
                                  String title, String content, String addfile) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        //收件者
        intent.putExtra(Intent.EXTRA_EMAIL, mailto);
        //抄送者
        intent.putExtra(Intent.EXTRA_CC, cc);
        //密送者
        intent.putExtra(Intent.EXTRA_BCC, bcc);
        //主题
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        //内容
        intent.putExtra(Intent.EXTRA_TEXT, content);

        if (!TextUtils.isEmpty(addfile)) {
            //获取文件file的MIME类型
            File file = new File(addfile);
            String type = getMIMEType(file);
            if (TextUtils.isEmpty(type)) {
                return;
            }
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
            intent.setType(type);

            //这是什么意思
            intent.setType("message/rfc882");
        }
        Intent.createChooser(intent, "Choose Email Client");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 发邮件  Intent.ACTION_SEND_MULTIPLE  带有多附件的发送
     *
     * @param context
     * @param mailTo  收件者们
     * @param cc      抄送者们
     * @param bcc     密送者们
     * @param title   主题
     * @param content 内容
     * @param uris    附件集合uris
     */
    public static void openEmail3(Context context, String[] mailTo, String[] cc, String[] bcc,
                                  String title, String content, ArrayList<Uri> uris) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        //收件者
        intent.putExtra(Intent.EXTRA_EMAIL, mailTo);
        //抄送者
        intent.putExtra(Intent.EXTRA_CC, cc);
        //密送者
        intent.putExtra(Intent.EXTRA_BCC, bcc);
        //主题
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        //内容
        intent.putExtra(Intent.EXTRA_TEXT, content);

        if (uris != null && !TextUtils.isEmpty(uris.get(0).getPath())) {
            //获取文件file的MIME类型
            File file = new File(uris.get(0).getPath());
            String type = getMIMEType(file);
            if (TextUtils.isEmpty(type)) {
                return;
            }
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            intent.setType(type);
            intent.setType("message/rfc882");
        }
        Intent.createChooser(intent, "Choose Email Client");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     *
     * @param file
     */
    private static String getMIMEType(File file) {
        if (!file.exists()) {
            return null;
        }
        String type = "*/*";
        String filename = file.getName();
        //获取后缀名前的分隔符"."在filename中的位置。
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        //获取文件的后缀名
        String end = filename.substring(dotIndex, filename.length()).toLowerCase();
        if (TextUtils.isEmpty(end)) {
            return type;
        }
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0])) {
                type = MIME_MapTable[i][1];
            }
        }
        return type;
    }

    /**
     * 打开不知道是哪的图片
     * 4.3返回的是带文件路径的,
     * 4.4返回的却是content://com.android.providers.media.documents/document/image:3951
     * @param activity
     */
    public static void openImage(Activity activity) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 在4.4或以上,官方建议用ACTION_OPEN_DOCUMENT
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }
        activity.startActivityForResult(intent, Activity.RESULT_OK);
    }

    /**
     * 查看指定图片
     *
     * @param context
     * @param picFile
     */
    public static void openImage(Context context, File picFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Android3.0以后最好不要通过该方法，存在一些小Bug
        //Uri mUri = Uri.parse("file://" + picFile.getPath());
        intent.setDataAndType(Uri.fromFile(picFile), "image/*");
        context.startActivity(intent);
    }

    /**
     * 主要区别是他们返回的Uri.
     * 4.3返回的是带文件路径的,
     * 4.4返回的却是content://com.android.providers.media.documents/document/image:3951
     * 这样的,没有路径,只有图片编号的uri.可以通过以下方式，处理URI
     * @param context
     * @param uri
     * @return 返回文件的绝对地址
     */
    public static String getPath(final Context context, final Uri uri) {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {

            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                String[] split = docId.split(":");
                String type = split[0];
                String id = split[1];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                // MediaStore.Images.Media._ID
                // MediaStore.Audio.Media._ID
                // MediaStore.Video.Media._ID
                String selection = "_id=?";
                String[] selectionArgs = new String[]{ id };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * 通过Uri和selection来获取真实的图片路径
     * @param context
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        // MediaStore.Images.Media.DATA
        // MediaStore.Audio.Media.DATA
        // MediaStore.Video.Media.DATA
        String column = "_data";
        String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 外部存储空间
     * @param uri
     * @return
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * 下载空间
     * @param uri
     * @return
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * 多媒体类型
     * @param uri
     * @return
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Google照片
     * @param uri
     * @return
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

}
