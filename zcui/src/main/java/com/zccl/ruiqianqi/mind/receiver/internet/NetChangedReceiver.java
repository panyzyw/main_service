package com.zccl.ruiqianqi.mind.receiver.internet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.zccl.ruiqianqi.mind.eventbus.MainBusEvent;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.PhoneUtils;
import com.zccl.ruiqianqi.tools.ShareUtils;
import com.zccl.ruiqianqi.zcui.R;

import org.greenrobot.eventbus.EventBus;

import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_WIFI;
import static com.zccl.ruiqianqi.tools.PhoneUtils.NET_TYPE_WAP;
import static com.zccl.ruiqianqi.tools.PhoneUtils.SUB_NET_TYPE_2G;
import static com.zccl.ruiqianqi.tools.PhoneUtils.SUB_NET_TYPE_3G;
import static com.zccl.ruiqianqi.tools.PhoneUtils.SUB_NET_TYPE_4G;
import static com.zccl.ruiqianqi.tools.PhoneUtils.SUB_NET_TYPE_RESERVED;

/**
 * Created by ruiqianqi on 2016/8/10 0010.
 */
public class NetChangedReceiver extends BroadcastReceiver {

    /** 类标志 */
    private static String TAG = NetChangedReceiver.class.getSimpleName();
    // 网络状态存储
    public static final String NET_STATUS_KEY = "net_status";

    @Override
    public void onReceive(Context context, Intent intent) {

        // 这个监听wifi的打开与关闭，与wifi的连接无关
        if(intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
            if(wifiState == WifiManager.WIFI_STATE_DISABLED){
                LogUtils.e(TAG, "系统关闭wifi");
            }
            else if(wifiState == WifiManager.WIFI_STATE_DISABLING){
                LogUtils.e(TAG, "系统关闭wifi中");
            }
            else if(wifiState == WifiManager.WIFI_STATE_ENABLED){
                LogUtils.e(TAG, "系统开启wifi");
            }

            // 通知网络变化了
            checkNet(context);

        }
        // 这个监听wifi的连接状态即是否连上了一个有效无线路由，当上边广播的状态是WifiManager.WIFI_STATE_DISABLING，
        // 和WifiManager.WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
        // 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，
        // 当然刚打开wifi肯定还没有连接到有效的无线
        else if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != networkInfo) {
                if(PhoneUtils.isNetConnected(context)){
                    int netType = PhoneUtils.getNetType(context);
                }else {

                }
            }

            // 通知网络变化了
            checkNet(context);

        }

        // 这个监听网络连接的设置，包括wifi和移动数据的打开和关闭。.
        // 最好用的还是这个监听。wifi如果打开，关闭，以及连接上可用的连接都会接到监听。
        // 这个广播的最大弊端是比上边两个广播的反应要慢，如果只是要监听wifi，我觉得还是用上边两个配合比较合适
        else if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != networkInfo) {
                if(networkInfo.isConnected()){
                    int netType = PhoneUtils.getNetType(context);
                }else {

                }
            }

            // 通知网络变化了
            checkNet(context);

        }
        // 这个是信号强弱的广播
        else if(intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)){
            //signal strength changed
            WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if(wifiInfo==null)
                return;
            // 连接到网络名称
            String wifiName = wifiInfo.getSSID();
            // 获得信号强度值
            int level = wifiInfo.getRssi();
            // 可识别的信号值
            int wifiLevel = 1;

            // 根据获得的信号强度发送信息
            // 信号最好
            if (level <= 0 && level >= -50) {
                wifiLevel = 1;
            }
            // 信号较好
            else if (level < -50 && level >= -70) {
                wifiLevel = 2;
            }
            // 信号一般
            else if (level < -70 && level >= -80) {
                wifiLevel = 3;
            }
            // 信号较差
            else if (level < -80 && level >= -100) {
                wifiLevel = 4;
            }
            // 无信号
            else {
                wifiLevel = 5;
            }

        }
    }

    /**
     * 检测网络
     * @param context
     */
    private void checkNet(Context context){
        MainBusEvent.NetEvent netEvent = new MainBusEvent.NetEvent();
        if(PhoneUtils.isNetConnected(context)){
            int mNetType = PhoneUtils.getNetType(context);
            if(TYPE_WIFI == mNetType){
                netEvent.setConn(true);
                netEvent.setText(context.getString(R.string.net_wifi));
            }
            else if(TYPE_MOBILE == mNetType || NET_TYPE_WAP == mNetType){
                int subNetType = PhoneUtils.getSubNetType(context);
                if(SUB_NET_TYPE_2G==subNetType){
                    netEvent.setConn(true);
                    netEvent.setText(context.getString(R.string.net_2g));
                }else if(SUB_NET_TYPE_3G==subNetType){
                    netEvent.setConn(true);
                    netEvent.setText(context.getString(R.string.net_3g));
                }else if(SUB_NET_TYPE_4G==subNetType){
                    netEvent.setConn(true);
                    netEvent.setText(context.getString(R.string.net_4g));
                }else if(SUB_NET_TYPE_RESERVED==subNetType){
                    netEvent.setConn(true);
                    netEvent.setText(context.getString(R.string.net_unknown_mobile));
                }else {
                    netEvent.setConn(false);
                    netEvent.setText(context.getString(R.string.net_none));
                }
            }
            else {
                netEvent.setConn(true);
                netEvent.setText(context.getString(R.string.net_unknown));
            }
        }else {
            netEvent.setConn(false);
            netEvent.setText(context.getString(R.string.net_none));
        }

        boolean isConn = ShareUtils.getP(context).getBoolean(NET_STATUS_KEY, false);
        if(isConn == netEvent.isConn()){
            return;
        }else {
            ShareUtils.getE(context).putBoolean(NET_STATUS_KEY, netEvent.isConn()).commit();
            // 通知网络变化了
            EventBus.getDefault().post(netEvent);
        }
    }
}
