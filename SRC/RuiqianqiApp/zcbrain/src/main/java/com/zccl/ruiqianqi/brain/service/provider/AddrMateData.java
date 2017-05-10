package com.zccl.ruiqianqi.brain.service.provider;

/**
 * Created by ruiqianqi on 2017/4/17 0017.
 */

public class AddrMateData {

    /**
     * 这里的AUTHORTY为包的全名+ContentProvider子类的全名，当然随便写也行
     * 授权“域名”,必须唯一，且与AndroidManifest里面注册的须一致
     */
    public final static String AUTHORITY = "com.yongyida.robot.voice.request";

    // 对外提供的访问表名
    public final static String AUTHORITY_TABLE = "http_request";

    // 表中记录的默认排序算法，这里是升序排列
    public static final String DEFAULT_SORT_ORDER = "_id asc";

    // 表中记录的默认排序算法，这里是降序排列
    public static final String DESC_SORT_ORDER = "_id desc";
}
