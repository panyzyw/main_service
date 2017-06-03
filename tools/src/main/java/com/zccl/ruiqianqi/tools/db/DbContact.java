package com.zccl.ruiqianqi.tools.db;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

/**
 * Created by ruiqianqi on 2016/9/24 0024.
 */

public class DbContact {

    /** 这是类标志 */
    private static String TAG = DbContact.class.getSimpleName();

    /** 获取库Phone表字段 **/
    private static final String[] PHONES_PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Photo.PHOTO_ID,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID };
    /** 联系人显示名称 **/
    private static final int PHONES_NAME_INDEX = 0;
    /** 电话号码 **/
    private static final int PHONES_NUMBER_INDEX = 1;

    /**
     * 返回手机当前联系人名单
     * @param context
     * @return
     */
    public static String queryContactNames(Context context){
        StringBuilder sb = new StringBuilder();
        Uri uriContact = ContactsContract.Contacts.CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uriContact, new String[]{ContactsContract.Contacts._ID}, null, null, null);
        while (cursor.moveToNext()) {
            int contract_id = cursor.getInt(0);
            uriContact = Uri.parse("content://com.android.contacts/contacts/" + contract_id + "/data");
            Cursor cursor1 = resolver.query(uriContact, new String[]{"mimetype", "data1", "data2"}, null, null, null);
            while (cursor1.moveToNext()) {
                String mimeType = cursor1.getString(cursor1.getColumnIndex("mimetype"));
                String data1 = cursor1.getString(cursor1.getColumnIndex("data1"));
                if ("vnd.android.cursor.item/name".equals(mimeType)) { //是姓名
                    sb.append(data1+"\n");
                } else if ("vnd.android.cursor.item/email_v2".equals(mimeType)) { //邮箱

                } else if ("vnd.android.cursor.item/phone_v2".equals(mimeType)) { //手机

                }
            }
            cursor1.close();
        }
        cursor.close();
        return sb.toString();
    }

    /**
     * 根据名字查询电话号码
     * @param context
     * @param name
     * @return
     */
    public static String queryPhoneNumByName(Context context, String name){
        Uri uriContact = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();
        Cursor phoneCursor = resolver.query(uriContact, PHONES_PROJECTION, null, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                // 得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                LogUtils.e(TAG, "phoneNumber = " + phoneNumber);
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (StringUtils.isEmpty(phoneNumber))
                    continue;
                // 得到联系人名称
                String contactName = phoneCursor.getString(PHONES_NAME_INDEX);
                LogUtils.e(TAG, "contactName = " + contactName);
                if (name.equals(contactName)) {
                    return phoneNumber;
                }
            }
        }
        return null;
    }
}
