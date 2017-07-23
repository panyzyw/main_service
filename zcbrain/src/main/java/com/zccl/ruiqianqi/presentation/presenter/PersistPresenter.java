package com.zccl.ruiqianqi.presentation.presenter;


import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.SystemProperties;

import com.yongyida.robot.idhelper.RobotIDHelper;
import com.zccl.ruiqianqi.beans.ReportBean;
import com.zccl.ruiqianqi.brain.R;
import com.zccl.ruiqianqi.storage.db.MyDbFlow;
import com.zccl.ruiqianqi.storage.db.ServerBean;
import com.zccl.ruiqianqi.presenter.base.BasePresenter;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.MYUIUtils;
import com.zccl.ruiqianqi.tools.ShareUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.tools.config.MyConfigure;
import com.zccl.ruiqianqi.tools.executor.rxutils.MyRxUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.zccl.ruiqianqi.config.MyConfig.STATE_ACCOUNT_EXCEPTION;
import static com.zccl.ruiqianqi.config.MyConfig.STATE_ACCOUNT_OKAY;

/**
 * Created by ruiqianqi on 2017/1/14 0014.
 *
 * 非运行持久
 */

public class PersistPresenter extends BasePresenter {

    // 类日志标志
    public static String TAG = PersistPresenter.class.getSimpleName();

    // HTTP请求地址
    public static final String KEY_HTTP_REQUEST = "key_http_req";
    // HTTP资源地址
    public static final String KEY_HTTP_RESOURCE = "key_http_res";

    // 服务器地址版本
    public static final String KEY_SERVER_ADDR = "key_server_addr";
    // 正式版
    public static final String FORMAL_SERVER = "formal_version";
    // 测试版
    public static final String DEBUG_SERVER = "debug_version";
    // 开发版
    public static final String DEV_SERVER = "dev_version";

    // 获取唤醒门限值
    public static final String KEY_THRESHOLD = "key_threshold";
    // 默认唤醒门限值
    private static final int THRESHOLD_VALUE = 5;

    // 获取ID
    public static final String KEY_ID = "key_robot_id";
    // 获取SID
    public static final String KEY_SID = "key_robot_sid";
    // 要不要显示文字
    private static final String KEY_SHOW_WORDS = "key_show_words";
    // 要不要声源定位
    private static final String KEY_LOCALIZATION = "key_localization";

    // 单例引用
    private static PersistPresenter instance;
    // xml文件
    private SharedPreferences mSharedPreferences;
    // 网络连接处理中心
    private SocketPresenter mSocketPresenter;
    // 获取机器人ID任务
    private ScheduledFuture<?> mGetIdFuture;

    private PersistPresenter() {
        init();
    }

    /**
     * 用这个用话，instance不需要用volatile修饰
     *
     * @return
     */
    public static PersistPresenter getInstance() {
        if (instance == null) {
            synchronized (PersistPresenter.class) {
                PersistPresenter temp = instance;
                if (temp == null) {
                    temp = new PersistPresenter();
                    instance = temp;
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     */
    private void init() {
        mSharedPreferences = ShareUtils.getP(mContext);
        mSocketPresenter = SocketPresenter.getInstance();
    }

    /**************************************【对外提供的方法】**************************************/
    /**
     * 初始化一些配置
     * 1.服务器IP地址
     * 2.机器人帐号
     */
    public void initSome() {

        MyRxUtils.doAsyncRun(new Runnable() {
            @Override
            public void run() {

                // 初始化系统服务
                SystemPresenter.getInstance();

                // 初始化服务器地址
                initServerAddr();

                // 检测ID
                mGetIdFuture = scheduleTask.executeAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        // 检测ID，连接服务器
                        checkIdAndConnect();
                    }
                }, 2, 10, TimeUnit.SECONDS);

            }
        });

    }

    /**
     * 检测ID，连接服务器
     */
    public void checkIdAndConnect(){
        boolean isGetId = false;
        for (int i = 0; i < 3; i++) {
            isGetId = getAccountId();
            if (isGetId) {
                break;
            }
        }

        // 报告机器人帐号状态
        if (isGetId) {
            // 机器人帐号正常
            StatePresenter.getInstance().setRobotState(STATE_ACCOUNT_OKAY);
            //ReportPresenter.report(mContext.getString(R.string.okay_account_id));

            // 取到ID了，结束循环任务
            if(null != mGetIdFuture && !mGetIdFuture.isCancelled()) {
                mGetIdFuture.cancel(true);
                mGetIdFuture = null;
            }
            // 开始连接服务器
            mSocketPresenter.connectToServer(getServerAddr());

        } else {
            // 机器人帐号异常
            StatePresenter.getInstance().setRobotState(STATE_ACCOUNT_EXCEPTION);

            // 状态报告
            ReportBean reportBean = ReportBean.obtain(ReportBean.CODE_TTS, mContext.getString(R.string.error_account_id));
            ReportPresenter.report(reportBean);

            // 没取到就不能玩，所以要一直取

        }
    }

    /**
     * 返回连接的服务器版本
     *
     * @return
     * {@link PersistPresenter#FORMAL_SERVER }
     * {@link PersistPresenter#DEBUG_SERVER }
     * {@link PersistPresenter#DEV_SERVER }
     */
    public String getServerAddr() {
        return mSharedPreferences.getString(KEY_SERVER_ADDR, FORMAL_SERVER);
    }

    /**
     * 设置连接的服务器版本
     *
     * @param flagVersion
     */
    public void setServerAddr(String flagVersion, String httpRequest, String httpResource) {
        SharedPreferences.Editor editor = ShareUtils.getE(mContext);
        editor.putString(KEY_SERVER_ADDR, flagVersion);
        editor.putString(KEY_HTTP_REQUEST, httpRequest);
        editor.putString(KEY_HTTP_RESOURCE, httpResource);
        editor.commit();
    }

    /**
     * 获取HTTP请求地址
     *
     * @return
     */
    public String getHttpRequest() {
        return mSharedPreferences.getString(KEY_HTTP_REQUEST, "server.yydrobot.com");
    }

    /**
     * 获取HTTP资源地址
     *
     * @return
     */
    public String getHttpResource() {
        return mSharedPreferences.getString(KEY_HTTP_RESOURCE, "resource.yydrobot.com");
    }

    /**
     * 是否显示用户说的话
     *
     * @return
     */
    public boolean isShowWords() {
        return mSharedPreferences.getBoolean(KEY_SHOW_WORDS, true);
    }

    /**
     * 设置是否显示用户说的话
     *
     * @param isShow
     */
    public void setShowWords(boolean isShow) {
        ShareUtils.getE(mContext).putBoolean(KEY_SHOW_WORDS, isShow).commit();
    }

    /**
     * 得到唤醒阀值
     *
     * @return
     */
    public int getThreshold() {
        return mSharedPreferences.getInt(KEY_THRESHOLD, THRESHOLD_VALUE);
    }

    /**
     * 设置唤醒阀值
     *
     * @param threshold
     */
    public void setThreshold(int threshold) {
        ShareUtils.getE(mContext).putInt(KEY_THRESHOLD, threshold).commit();
    }

    /**
     * 得到是否声源定位的值，默认为true
     *
     * @return
     */
    public boolean isLocalization() {
        return mSharedPreferences.getBoolean(KEY_LOCALIZATION, true);
    }

    /**
     * 设置是否声源定位的值
     *
     * @param localization
     */
    public void setLocalization(boolean localization) {
        ShareUtils.getE(mContext).putBoolean(KEY_LOCALIZATION, localization).commit();
    }

    /****************************************【私有方法】******************************************/
    /**
     * 获得机器人帐号
     *
     * @return
     */
    private boolean getAccountId() {

        boolean isProvided = false;

        try {
            ContentResolver resolver;
            Cursor cursor;
            // launcher提供
            Uri uri = Uri.parse("content://com.yongyida.robot.idprovider//id");
            resolver = mContext.getContentResolver();
            cursor = resolver.query(uri, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    // id
                    String id = cursor.getString(cursor.getColumnIndex("id")).trim();
                    // sid
                    String sid = cursor.getString(cursor.getColumnIndex("sid")).trim();

                    if (StringUtils.isEmpty(id) || StringUtils.isEmpty(sid)) {

                    } else {
                        ShareUtils.getE(mContext).putString(KEY_ID, id).commit();
                        ShareUtils.getE(mContext).putString(KEY_SID, sid).commit();
                        isProvided = true;
                    }
                }
                cursor.close();
            }
        } catch (Throwable e) {

        }


        // 如果 ContentProvider 没有提供
        if (!isProvided) {
            String serial = SystemProperties.get("ro.boot.serialno", null);
            if (!StringUtils.isEmpty(serial)) {
                String[] id_sid = serial.trim().split("-");
                if (id_sid.length > 1) {
                    ShareUtils.getE(mContext).putString(KEY_ID, id_sid[0]).commit();
                    ShareUtils.getE(mContext).putString(KEY_SID, id_sid[1].substring(0, 4)).commit();
                    isProvided = true;
                }
            }
        }

        if (!isProvided) {
            String serial = SystemProperties.get("gsm.serial", null);
            if (!StringUtils.isEmpty(serial)) {
                String[] id_sid = serial.trim().split("-");
                if (id_sid.length > 1) {
                    ShareUtils.getE(mContext).putString(KEY_ID, id_sid[0]).commit();
                    ShareUtils.getE(mContext).putString(KEY_SID, id_sid[1].substring(0, 4)).commit();
                    isProvided = true;
                }
            }
        }


        // 如果那个gsm.serial属性没有取到
        if(!isProvided){
            String serial = RobotIDHelper.Builder.THIS.createIDHelper().getRobotSN();
            if (!StringUtils.isEmpty(serial)) {
                String[] id_sid = serial.trim().split("-");
                if (id_sid.length > 1) {
                    ShareUtils.getE(mContext).putString(KEY_ID, id_sid[0]).commit();
                    ShareUtils.getE(mContext).putString(KEY_SID, id_sid[1].substring(0, 4)).commit();
                    isProvided = true;
                }
            }
        }

        if(!isProvided) {
            boolean useDefaultId = Boolean.parseBoolean(MyConfigure.getValue("use_default_id"));
            if (useDefaultId) {
                // 测试用的ID
                ShareUtils.getE(mContext).putString(KEY_ID, MyConfigure.getValue("robot_id_9")).commit();
                ShareUtils.getE(mContext).putString(KEY_SID, MyConfigure.getValue("robot_sid_9")).commit();
                isProvided = true;
            }
        }

        //ShareUtils.getE(mContext).putString(KEY_ID, "Y128A1471339630244").commit();
        //ShareUtils.getE(mContext).putString(KEY_SID, "Q9DJ").commit();
        return isProvided;
    }

    /**
     * 初始化服务器地址
     */
    private void initServerAddr() {
        // 正式版
        ServerBean formal = new ServerBean();
        formal.httpRequest = "server.yydrobot.com";
        formal.httpResource = "resource.yydrobot.com";
        formal.tcpRequest = "robot.yydrobot.com";
        formal.tcpPort = "8001";
        formal.flagVersion = FORMAL_SERVER;

        // 测试版
        ServerBean debug = new ServerBean();
        debug.httpRequest = "120.24.242.163:81";
        debug.httpResource = "120.24.242.163";
        debug.tcpRequest = "120.24.242.163";
        debug.tcpPort = "8001";
        debug.flagVersion = DEBUG_SERVER;

        // 开发版
        ServerBean dev = new ServerBean();
        dev.httpRequest = "120.24.213.239:81";
        dev.httpResource = "120.24.213.239";
        dev.tcpRequest = "120.24.213.239";
        dev.tcpPort = "8001";
        dev.flagVersion = DEV_SERVER;

        List<ServerBean> serverBeanList = new ArrayList<>();
        serverBeanList.add(formal);
        serverBeanList.add(debug);
        serverBeanList.add(dev);

        try {
            MyDbFlow.transactionServerBean(MyDbFlow.OP.INSERT, MyDbFlow.SYNC, serverBeanList, null);
        } catch (SQLException e) {
            LogUtils.e(TAG, "initServerAddr", e);
        }


    }

}
