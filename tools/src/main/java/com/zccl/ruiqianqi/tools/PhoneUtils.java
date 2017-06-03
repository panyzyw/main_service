package com.zccl.ruiqianqi.tools;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.zccl.ruiqianqi.tools.beans.MyApn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zc on 2015/11/17.
 */
public class PhoneUtils {

    /**
     * 权限READ_PHONE_STATE
     * 获取SIM卡串行号
     * @return 如果sim卡不可用 返回 null
     */
    public static String getPhoneIMSI(Context context) {
        TelephonyManager telMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telMgr != null) {
            return telMgr.getSubscriberId();
        }
        return null;
    }

    /**
     * 设备唯一编码
     * @param context
     * @return
     */
    public static String getPhoneIMEI(Context context){
        TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telMgr.getDeviceId();
        if(StringUtils.isEmpty(imei)){
            imei = getMacAddress(context);
            if(StringUtils.isEmpty(imei)){
                return getUniqueNO(context);
            }
            return imei;
        }else {
            //匹配全是零的字符串
            Pattern pat = Pattern.compile("(^0)*0*(0$)*");
            Matcher mat = pat.matcher(imei);
            if(mat.matches()){
                imei = getMacAddress(context);
                if(StringUtils.isEmpty(imei)){
                    return getUniqueNO(context);
                }
                return imei;
            }
        }
        return imei;
    }

    /**
     * 手机唯一标识码（设备独立标志）
     * IMSI：  international mobiles subscriber identity国际移动用户号码标识，这个一般大家是不知道，GSM必须写在卡内相关文件中；
     * MSISDN: mobile subscriber ISDN用户号码，这个是我们说的139，136那个号码；
     * ICCID:  ICC identity集成电路卡标识，这个是唯一标识一张卡片物理号码的；
     * IMEI：  international mobile Equipment identity手机唯一标识码；
     *
     * TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
     * String imei  = tm.getDeviceId();        //取出IMEI
     * String tele  = tm.getLine1Number();     //取出MSISDN，很可能为空(电话号码)
     * String iccid = tm.getSimSerialNumber(); //取出ICCID
     * String imsi  = tm.getSubscriberId();    //取出IMSI
     *
     * @return
     */
    public static String getUniqueNO(Context context) {
        //设备唯一ID
        String deviceid = null;
        //设备唯一ID对应的uuid
        String uuidstr = null;
        //UUID生成器
        UUID uuid = null;
        try {

            deviceid = ShareUtils.getP(context).getString("deviceid", null);
            uuidstr = ShareUtils.getP(context).getString("uuidstr", null);
            if(StringUtils.isEmpty(deviceid)){

                TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                // IMEI 864587028758234一个例子
                String imei = telMgr.getDeviceId();
                boolean isImeiUsed;
                if(StringUtils.isEmpty(imei)){
                    isImeiUsed = false;
                }else{
                    // 匹配全是零的字符串
                    Pattern pat = Pattern.compile("(^0)*0*(0$)*");
                    Matcher mat = pat.matcher(imei);
                    if(mat.matches()){
                        isImeiUsed = false;
                    }else {
                        isImeiUsed = true;
                    }
                }

                // IMEI没取到
                if(!isImeiUsed){

                    // f733857一个例子
                    deviceid = getSystemProperty("ro.serialno");

                    // 设备序列号没取到
                    if(StringUtils.isEmpty(deviceid)){

                        // 847b2ad23d6ca762一个例子
                        deviceid = generateOpenUDID(context);
                        // 设备ID号没取到
                        if(StringUtils.isEmpty(deviceid)){
                            uuid = UUID.randomUUID();
                            deviceid = uuid.toString();
                        }
                        // 设备ID号取到
                        else{

                        }
                    }
                    //设备序列号取到
                    else{

                    }
                }
                //IMEI取到
                else{
                    deviceid = imei;
                }
                ShareUtils.getE(context).putString("deviceid", deviceid).commit();

                if(uuid==null) {
                    //存入设备ID的UUID b17e5b6f-109f-31ae-af77-0a418ba8a6e7一个例子
                    uuid = UUID.nameUUIDFromBytes(deviceid.getBytes("utf-8"));
                }
                ShareUtils.getE(context).putString("uuidstr", uuid.toString()).commit();

            }else{
                uuid = UUID.fromString(uuidstr);
                uuidstr = uuid.toString();
            }
        } catch (Exception e) {

        }
        //return uuidstr;
        return deviceid;
    }

    /**
     * Generate a new OpenUDID
     * UDID是Unique Device Identifier的缩写,中文意思是设备唯一标识.
     * 在很多需要限制一台设备一个账号的应用中经常会用到,
     * 在Symbian时代,我们是使用IMEI作为设备的唯一标识的,可惜的是Apple官方不允许开发者获得设备的IMEI.
     *
     * 大多数应用都会用到苹果设备的UDID号，UDID通常有以下两种用途：
     * 1）用于一些统计与分析目的；【第三方统计工具如友盟，广告商如ADMOB等】
     * 2）将UDID作为用户ID来唯一识别用户，省去用户名，密码等注册过程。
     *
     * 替代方案1：
     * 苹果公司建议使用UUID【一种开放的软件构建标准】
     * 该方法每次都会获取一个唯一的标识字符串，开发者可以在应用第一次启动时候调用一次，
     * 然后将该串存储起来，以便以后替代UDID来使用。
     * 问题是如果用户删除该应用再次安装时，又会生成新的字符串，所以不能保证唯一识别该设备。
     *
     * 替代方案2：
     * 现在网上有一现成的解决方案,使用设备的Mac地址,因为Mac地址也是唯一的.unix有系统调用可以获取Mac地址.但有些事情需要注意:
     * 1）iPhone可能有多个Mac地址,wifi的地址,以及SIM卡的地址.一般来讲,我们取en0的地址,因为他是iPhone的wifi的地址,是肯定存在的.
     * 2）Mac地址涉及到隐私,不应该胡乱将用户的Mac地址传播!所以我们需要将Mac地址进行hash之后,才能作为DeviceId上传.
     *
     */
    public static String generateOpenUDID(Context context) {

        //847b2ad23d6ca762一个例子
        String OpenUDID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        //IMEI长度为15，这个长度应该为16，就用15作限制吧，万一读到IMEI了呢，是吧
        if(StringUtils.isEmpty(OpenUDID) || OpenUDID.equals("9774d56d682e549c") || OpenUDID.length()<15 ) {
            //if ANDROID_ID is null,
            //or it's equals to the GalaxyTab generic ANDROID_ID
            //or bad,
            //generates a new one
            SecureRandom random = new SecureRandom();

            //ed27ef13614cfc10一个例子
            OpenUDID = new BigInteger(64, random).toString(16);
        }
        return OpenUDID;
    }

    /**
     * 获取手机运营商
     * IMSI共有15位，其结构如下：
     * MCC+MNC+MIN
     * MCC：Mobile Country Code，移动国家码，共3位，中国为460;
     * MNC: Mobile Network Code，移动网络码，共2位
     * 在中国，
     * 移动的代码为00、02、07，
     * 联通的代码为01、06、10
     * 电信的代码为03、05，
     * @return 中国联通，中国移动，中国电信
     */
    public static String getOperatorName(Context context) {
        String imsi = getPhoneIMSI(context);
        String typeStr = null;
        if(StringUtils.isEmpty(imsi)){
            imsi = getOperatorType(context);
        }
        if(!StringUtils.isEmpty(imsi)){
            //中国移动TD系统使用00,中国移动GSM系统使用02
            if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007")) {// 大陆
                typeStr = "中国移动";
            }
            //中国联通GSM系统使用01
            else if (imsi.startsWith("46001") || imsi.startsWith("46006") || imsi.startsWith("46010")) {
                typeStr = "中国联通";
            }
            //中国电信CDMA系统使用03
            else if (imsi.startsWith("46003") || imsi.startsWith("46005")) {
                typeStr = "中国电信";
            }
            else if (imsi.startsWith("46020")) {
                //typeStr = "中国铁通";
                typeStr = "中国移动";
            }
        }else{
            TelephonyManager telMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            if(telMgr != null) {
                if(isCanUseSim(context)){
                    return telMgr.getSimOperatorName();
                }
            }
        }
        return typeStr;
    }

    /**
     * 获取手机服务商信息
     * @return (MCC + MNC)
     *
     * IMSI共有15位，其结构如下：
     * MCC+MNC+MIN
     * MCC：Mobile Country Code，移动国家码，共3位，中国为460;
     * MNC: Mobile Network Code，移动网络码，共2位
     *
     * 在中国，
     * 移动的代码为00、02、07、20，
     * 联通的代码为01、06、10，
     * 电信的代码为03、05，
     * @Description need READ_PHONE_STATE
     */
    public static String getOperatorType(Context context) {
        TelephonyManager telMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if(telMgr != null){
            if(isCanUseSim(context)){
                return telMgr.getSimOperator();
            }else{
                String imsi = getPhoneIMSI(context);
                if(!StringUtils.isEmpty(imsi)){
                    return imsi.substring(0,5);
                }
            }
        }
        return null;
    }

    /**
     * 获取手机Mac地址
     * @return String
     */
    public static String getMacAddress(Context context) {
        WifiManager wifiMgr = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr != null) {
            WifiInfo info = wifiMgr.getConnectionInfo();
            if (info != null) {
                String addr = info.getMacAddress();
                if(StringUtils.isEmpty(addr)){
                    return getMacAddress();
                }
                return addr;
            }
        }
        return null;
    }

    /**
     * 获取手机Mac地址
     * @return
     */
    public static String getMacAddress() {
        String macSerial = null;
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str;) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return macSerial;
    }



    /**
     * 判断网络连接是否打开
     * @param context
     * @return
     */
    public static boolean isNetConnected(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(null != conMgr){
            NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();

            // WIFI网络
            NetworkInfo wifiNetWorkInfo = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if(null != wifiNetWorkInfo && wifiNetWorkInfo.getState()== NetworkInfo.State.CONNECTED){
                //wifiNetWorkInfo.isAvailable();
                wifiNetWorkInfo.isConnected();
            }

            // 移动网络
            NetworkInfo mobileNetWorkInfo = conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if(null != mobileNetWorkInfo && mobileNetWorkInfo.getState()== NetworkInfo.State.CONNECTED){
                //mobileNetWorkInfo.isAvailable();
                mobileNetWorkInfo.isConnected();
            }

            if (null==networkInfo || (null != networkInfo && !networkInfo.isAvailable())) {// 未开启网络
                // NetworkInfo 为空或者不可以用的时候正常情况应该是当前没有可用网络，
                // 但是有些电信机器，仍可以正常联网，
                // 所以当成net网络处理依然尝试连接网络。
                TelephonyManager telMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                if(telMgr != null){

                    telMgr.getDataActivity();
                    int state = telMgr.getDataState();
                    return state == TelephonyManager.DATA_CONNECTED;
                }
                return false;
            }else{
                return networkInfo.isConnected();
            }
        }
        return false;
    }

    /**
     * Returns a constant indicating the device phone type.  This
     * indicates the type of radio used to transmit voice calls.
     *
     * #PHONE_TYPE_NONE
     * #PHONE_TYPE_GSM  移动或联通
     * #PHONE_TYPE_CDMA 电信
     * #PHONE_TYPE_SIP
     */
    public static int getPhoneType(Context context){
        try {
            TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if(telMgr!=null){
                int phoneType = telMgr.getPhoneType();
                return phoneType;
            }
        } catch (Exception e) {

        }
        return TelephonyManager.PHONE_TYPE_NONE;
    }

    /** 没检测到网络连接类型 */
    public static final int NET_TYPE_NONE = -1;
    // WAP网
    public static final int NET_TYPE_WAP = -2;

    /** 2G网络信号 */
    public static final int SUB_NET_TYPE_2G = 1;
    /** 3G网络信号 */
    public static final int SUB_NET_TYPE_3G = 2;
    /** 4G网络信号 */
    public static final int SUB_NET_TYPE_4G = 3;
    /** 预留网络信号 */
    public static final int SUB_NET_TYPE_RESERVED = 4;
    /** 没检测到网络信号类型 */
    public static final int SUB_NET_TYPE_NONE = -1;

    /**
     * 当前网络类型
     * {@link ConnectivityManager#TYPE_MOBILE},
     * {@link ConnectivityManager#TYPE_WIFI},
     * {@link ConnectivityManager#TYPE_WIMAX},
     * {@link ConnectivityManager#TYPE_ETHERNET},
     * {@link ConnectivityManager#TYPE_BLUETOOTH}
     */
    public static int getNetType(Context context) {
        if (isNetConnected(context)) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if(networkInfo != null) {
                String typeName = networkInfo.getTypeName();
                String netString = networkInfo.getExtraInfo();

                String proxyHost = android.net.Proxy.getDefaultHost();
                int proxyPort = Proxy.getDefaultPort();
                if(!StringUtils.isEmpty(proxyHost)){
                    return NET_TYPE_WAP;
                }
                return networkInfo.getType();
            }
        }
        return NET_TYPE_NONE;
    }

    /**
     * 返回是2G、3G、4G
     * {@link TelephonyManager#NETWORK_TYPE_UNKNOWN},
     *
     * 移动2G edge(2.75G)
     * {@link TelephonyManager#NETWORK_TYPE_EDGE}, EDGE 2G(2.75G) Enhanced Data Rate for GSM Evolution 384kbps
     *
     * 联通2G gprs(2.5G)
     * {@link TelephonyManager#NETWORK_TYPE_GPRS}, GPRS 2G(2.5) General Packet Radia Service 114kbps
     *
     * 联通3G
     * {@link TelephonyManager#NETWORK_TYPE_UMTS}, UMTS 3G WCDMA 联通3G Universal Mobile Telecommunication System 完整的3G移动通信技术标准
     * {@link TelephonyManager#NETWORK_TYPE_HSDPA}, HSDPA 3.5G 高速下行分组接入 3.5G WCDMA High Speed Downlink Packet Access 14.4mbps
     * {@link TelephonyManager#NETWORK_TYPE_HSPAP}, HSPAP 3G HSPAP 比 HSDPA 快些
     * {@link TelephonyManager#NETWORK_TYPE_HSUPA}, HSUPA 3.5G High Speed Uplink Packet Access 高速上行链路分组接入 1.4 - 5.8 mbps
     * {@link TelephonyManager#NETWORK_TYPE_HSPA}, HSPA 3G (分HSDPA,HSUPA) High Speed Packet Access

     * 电信2G cdma
     * {@link TelephonyManager#NETWORK_TYPE_CDMA}, CDMA 2G 电信 Code Division Multiple Access 码分多址
     * {@link TelephonyManager#NETWORK_TYPE_1xRTT}, 1xRTT 2G CDMA2000 1xRTT (RTT - 无线电传输技术) 144kbps 2G的过渡,
     * {@link TelephonyManager#NETWORK_TYPE_IDEN},  IDEN 2G Integrated Dispatch Enhanced Networks 集成数字增强型网络 （属于2G，来自维基百科）
     *
     * 电信3G
     * {@link TelephonyManager#NETWORK_TYPE_EVDO_0}, EVDO_0 3G (EVDO 全程 CDMA2000 1xEV-DO) Evolution - Data Only (Data Optimized) 153.6kps - 2.4mbps 属于3G
     * {@link TelephonyManager#NETWORK_TYPE_EVDO_A}, EVDO_A 3G 1.8mbps - 3.1mbps 属于3G过渡，3.5G
     * {@link TelephonyManager#NETWORK_TYPE_EVDO_B}, EVDO_B 3G EV-DO Rev.B 14.7Mbps 下行 3.5G
     * {@link TelephonyManager#NETWORK_TYPE_EHRPD}, EHRPD 3G CDMA2000向LTE 4G的中间产物 Evolved High Rate Packet Data HRPD的升级
     *
     * LTE是3g到4g的过渡，是3.9G的全球标准
     * {@link TelephonyManager#NETWORK_TYPE_LTE}, LTE 4G Long Term Evolution FDD-LTE 和 TDD-LTE , 3G过渡，升级版 LTE Advanced 才是4G
     *
     */
    public static int getSubNetType(Context context) {
        if (isNetConnected(context)) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if(networkInfo!=null) {
                int subNetworkType = networkInfo.getSubtype();
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                subNetworkType = telephonyManager.getNetworkType();
                if (subNetworkType == TelephonyManager.NETWORK_TYPE_CDMA
                        || subNetworkType == TelephonyManager.NETWORK_TYPE_GPRS
                        || subNetworkType == TelephonyManager.NETWORK_TYPE_EDGE
                        || subNetworkType == TelephonyManager.NETWORK_TYPE_1xRTT
                        || subNetworkType == TelephonyManager.NETWORK_TYPE_IDEN) {
                    return SUB_NET_TYPE_2G;
                } else if (subNetworkType == TelephonyManager.NETWORK_TYPE_UMTS
                        || subNetworkType == TelephonyManager.NETWORK_TYPE_HSDPA
                        || subNetworkType == TelephonyManager.NETWORK_TYPE_EVDO_A
                        || subNetworkType == TelephonyManager.NETWORK_TYPE_EVDO_0
                        || subNetworkType == TelephonyManager.NETWORK_TYPE_EVDO_B
                        || subNetworkType == TelephonyManager.NETWORK_TYPE_HSUPA
                        || subNetworkType == TelephonyManager.NETWORK_TYPE_HSPA
                        || subNetworkType == TelephonyManager.NETWORK_TYPE_EHRPD
                        || subNetworkType == TelephonyManager.NETWORK_TYPE_HSPAP) {
                    return SUB_NET_TYPE_3G;
                } else if (subNetworkType == TelephonyManager.NETWORK_TYPE_LTE) {// LTE是3g到4g的过渡，是3.9G的全球标准
                    return SUB_NET_TYPE_4G;
                }else {
                    return SUB_NET_TYPE_RESERVED;
                }
            }
        }
        return SUB_NET_TYPE_NONE;
    }


    /**
     * 判断sim卡是否可用
     * SIM的状态信息：
     * SIM_STATE_UNKNOWN         未知状态 0
     * SIM_STATE_ABSENT          没插卡 1
     * SIM_STATE_PIN_REQUIRED    锁定状态，需要用户的PIN码解锁 2
     * SIM_STATE_PUK_REQUIRED    锁定状态，需要用户的PUK码解锁 3
     * SIM_STATE_NETWORK_LOCKED  锁定状态，需要网络的PIN码解锁 4
     * SIM_STATE_READY           就绪状态 5
     * @return
     */
    public static boolean isCanUseSim(Context context) {
        try {
            TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if(telMgr!=null){
                int state = telMgr.getSimState();
                if(state==TelephonyManager.SIM_STATE_ABSENT || state==TelephonyManager.SIM_STATE_UNKNOWN){
                    return false;
                }
                return TelephonyManager.SIM_STATE_READY == telMgr.getSimState();
            }
        } catch (Exception e) {

        }
        return false;
    }


    /**
     * 获取系统属性值
     * @param propName
     * @return
     */
    public static String getSystemProperty(String propName) {
        String line = null;
        try{

            // 这个是硬件值(build.prop)
            Process process = Runtime.getRuntime().exec("getprop " + propName);
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()), 1024);
            line = input.readLine();

            if(null != input) {
                input.close();
            }

            if(StringUtils.isEmpty(line)){
                //这个是从api9开始开放的
                //imei = android.os.Build.SERIAL;
                //这样写以支持更多版本,android.os.SystemProperties的标签被打上@hide了，所以sdk中并不会存在
                //这个系统值
                //android.os.Build.VERSION;
                /*
                try{
                    Class<?> c = Class.forName("android.os.SystemProperties");
                    Method get = c.getMethod("get", String.class, String.class );
                    line = (String)(get.invoke(c, propName, null));
                }catch(Exception e){

                }
                */
                line = SystemProperties.get(propName);

                // 这个是虚拟机值
                if(StringUtils.isEmpty(line)){
                    Properties p = System.getProperties();
                    line = p.getProperty(propName, null);
                }
            }

        }catch(IOException e){
            e.printStackTrace();
        }
        return line;
    }

    /**
     * 所有的APN配置信息位置
     * _id,name,numeric,mcc,mnc,apn,user,server,password,proxy,port,
     * mmsproxy,mmsport,mmsc,authtype,type,current,protocol,
     * roaming_protocol,carrier_enabled,bearer,mvno_type,mvno_match_data
     */
    private static final Uri APN_TABLE_URI = Uri.parse("content://telephony/carriers");
    /**
     * 当前的APN配置信息
     * _id,name,numeric,mcc,mnc,apn,user,server,password,proxy,port,
     * mmsproxy,mmsport,mmsc,authtype,type,current,protocol,
     * roaming_protocol,carrier_enabled,bearer,mvno_type,mvno_match_data
     */
    private static final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");

    /**
     * （4.0之前，即小于等于2.3可以用 content://telephony/carriers 来读写apn）
     * 查询当前使用的APN
     * @param context
     * @return
     */
    public static MyApn queryApn(Context context){
        Cursor cur = context.getContentResolver().query(PREFERRED_APN_URI, null, null, null, null);
        if (cur != null) {
            /*
            for (int i = 0; i < cur.getColumnNames().length; i++) {
                LogUtils.e("ColumnNames "+i, cur.getColumnNames()[i]);
            }
            */
            if (cur.moveToFirst()) {
                String apn = cur.getString(cur.getColumnIndex("apn"));
                String proxy = cur.getString(cur.getColumnIndex("proxy"));
                String port = cur.getString(cur.getColumnIndex("port"));
                String user = cur.getString(cur.getColumnIndex("user"));

                MyApn myApn = new MyApn();
                myApn.setApn(apn);
                myApn.setProxy(proxy);
                myApn.setPort(port);
                myApn.setUser(user);
                return myApn;
            }
            cur.close();
        }
        return null;
    }
}
