package com.zccl.ruiqianqi.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.zccl.ruiqianqi.tools.http.myhttp.HttpFactory;

import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ruiqianqi on 2016/8/25 0025.
 */
public class WifiUtils {

    /**
     * 用代码打开或关闭wifi
     * @param context
     * @param isEnable 使能WIFI
     */
    public static void setWifi(Context context, boolean isEnable) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //开启wifi
        if (isEnable) {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
        }
        //关闭wifi
        else {
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
        }
    }

    /**
     * 手机IP地址
     * @param context
     * @return
     */
    public static String getIpAddress(Context context){
        int netType = PhoneUtils.getNetType(context);
        if(ConnectivityManager.TYPE_MOBILE == netType || PhoneUtils.NET_TYPE_WAP == netType){
            return getLocalIP();
        }else if(ConnectivityManager.TYPE_WIFI == netType){
            return getLocalIP(context);
        }else {
            return null;
        }
    }

    /**
     * int型转成ip地址
     * @param ip
     * @return
     */
    private static String intToIP(long ip) {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF)+ "." + (ip >> 24 & 0xFF);
    }

    /**
     * 获取手机IP地址
     * @return
     */
    public static String getLocalIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        // if (!inetAddress.isLoopbackAddress() && inetAddress
                        // instanceof Inet6Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 获取IP地址，wifi情况下
     * @param context
     * @return
     */
    public static String getLocalIP(Context context) {
        // 获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // 判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        wifiInfo.getLinkSpeed();

        String ip = intToIP(ipAddress);
        return ip;
    }

    /**
     * 获取wifi网关地址
     * @param context
     * @return
     */
    public static String getWifiGateway(Context context) {
        // 获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // 判断wifi是否开启
        if(!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        DhcpInfo di = wifiManager.getDhcpInfo();
        long gateWayIpL = di.gateway;
        String gateWayIpS = intToIP(gateWayIpL);// 网关地址

        long netMaskIpL = di.netmask;
        String netMaskIpS = intToIP(netMaskIpL);// 子网掩码地址

        return gateWayIpS;
    }

    /**
     * 获取手机的外网IP
     * http://www.input8.com/ip/
     * http://www.cmyip.com/
     * http://city.ip138.com/ip2city.asp
     *
     * 1.去连一个公网ip，然后，让这个公网ip告诉你你的ip地址。
     * 2.用traceroute，去连一个公网ip，观察你的路由的转跳情况，以某一个基点，来判断你的公网ip地址。
     */
    public static String getWifiWwwIp(String ipAddr) {

        HttpURLConnection httpConnection = HttpFactory.createHttpConn(ipAddr, HttpFactory.GET);
        if (httpConnection != null) {
            String ip = HttpFactory.getHttpData(httpConnection);
            Pattern pat = Pattern.compile(".*(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}).*",
                    Pattern.CASE_INSENSITIVE);
            Matcher mat = pat.matcher(ip);
            // 捕获组
            if (mat.find()) {
                // 整个字符串
                // mat.group(0);
                return mat.group(1);
            }
        }
        return null;
    }
}
