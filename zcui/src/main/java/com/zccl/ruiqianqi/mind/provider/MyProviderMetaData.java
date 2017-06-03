package com.zccl.ruiqianqi.mind.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by zc on 2016/4/8.
 */
public class MyProviderMetaData {
    /**
     * 这里的AUTHORTY为包的全名+ContentProvider子类的全名，当然随便写也行
     * 授权“域名”,必须唯一，且与AndroidManifest里面注册的须一致
     */
    public static final String AUTHORITY = "com.zccl.ruiqianqi.provider";

    /** 数据库的名称 */
    public static final String DATABASE_NAME = "DataDB.db";

    /** 数据库版本号 */
    public static final int DATABASE_VERSION = 1;

    /** 数据库中的表名 */
    public static final String USER_DATA_TABLE_NAME = "userdata";

    /** 表【userdata】的详细数据 */
    public static final class UserTableMetaData implements BaseColumns {

        // 子表名
        public static final String USER_DATA_TABLE_NAME = MyProviderMetaData.USER_DATA_TABLE_NAME;

        // CONTENT_URI为常量Uri; parse是将文本转换成Uri
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/userdata");

        // 返回ContentProvider中表的数据类型
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/userdata";

        // 返回ContentProvider表中item的数据类型
        public static final String CONTENT_TYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/userdata";

        // 还有一个ID字段
        //public static final String _ID = "_id";
        // 子表列名
        public static final String USER_NAME = "name";
        // 子表列名
        public static final String USER_VALUE = "value";

        // 表中记录的默认排序算法，这里是升序排列
        public static final String DEFAULT_SORT_ORDER = "_id asc";

        // 表中记录的默认排序算法，这里是降序排列
        public static final String DESC_SORT_ORDER = "_id desc";

    }

}
