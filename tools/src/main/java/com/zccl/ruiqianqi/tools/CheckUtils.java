package com.zccl.ruiqianqi.tools;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zc on 2015/11/17.
 */
public class CheckUtils {

    /**
     * 过滤CPU对应的核心数文件
     */
    private static final FileFilter CPU_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            String path = pathname.getName();
            //regex is slow, so checking char by char.
            if (path.startsWith("cpu")) {
                for (int i = 3; i < path.length(); i++) {
                    if (path.charAt(i) < '0' || path.charAt(i) > '9') {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    };
    private static String TAG = CheckUtils.class.getSimpleName();
    private static Random random = new Random();

    /*************************************唯一数****************************/
    private static long lastClickTime = 0;

    /**
     * 取（0~(num-1)）间的随机数
     *
     * @param num
     * @return
     **/
    public static int getRandom(int num) {
        int number = random.nextInt(num);// 0~(num-1)
        number = random.nextInt() % num;   // 0~(num-1)
        number = (int) (Math.random() * num);// 0~(num-1)
        return number;
    }

    /**
     * 随机取激活码,也可以当作唯一数,这只是一种算法而已
     * 看看前面还是后面加上IMSI or IMEI or MACADDRESS
     *
     * @return
     */
    public static String getRandomString() {
        String str = "abcdefghigklmnopkrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
        String acode = null;
        Random random = new Random();
        StringBuffer sf = new StringBuffer();
        for (int i = 0; i < 10; i++) {
            int number = random.nextInt(62);// 0~61
            sf.append(str.charAt(number));
        }
        acode = System.currentTimeMillis() + sf.toString();
        sf = null;
        return acode;
    }

    /**
     * IsSupported OpenGLES 2.0
     * Return true if this device support OpenGLES 2.0 rendering.
     *
     * @param context
     * @return
     */
    public static boolean IsSupported(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        if (info.reqGlEsVersion >= 0x20000) {
            // Open GL ES 2.0 is supported.
            return true;
        }
        return false;
    }

    /**
     * 验证手机是否支持AEP
     *
     * @param context
     * @return
     */
    public static boolean IsSupportedAEP(Context context) {
        boolean deviceSupportsAEP = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_OPENGLES_EXTENSION_PACK);
        return deviceSupportsAEP;
    }

    /**
     * 本机是不是模拟器
     *
     * @param context
     * @return
     */
    public static boolean IsEmulator(Context context) {
        if (Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.startsWith("unknown") ||
                Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator") ||
                Build.MODEL.contains("Android SDK built for x86")) {
            return true;
        }
        return false;
    }

    /**
     * 获得进程的CPU使用率2
     * ps  查看当前进程
     * top 查看CPU及内存使用率
     * <p>
     * 内存耗用：VSS/RSS/PSS/USS 的介绍
     * VSS - Virtual Set Size      虚拟耗用内存（包含共享库占用的内存）
     * RSS - Resident Set Size     实际使用物理内存（包含共享库占用的内存）
     * PSS - Proportional Set Size 实际使用的物理内存（比例分配共享库占用的内存）
     * USS - Unique Set Size       进程独自占用的物理内存（不包含共享库占用的内存）
     * 一般来说内存占用大小有如下规律：VSS >= RSS >= PSS >= USS
     * <p>
     * top -m 10 -s cpu
     * -m 10 表示显示数量为10
     * -s    表示按指定行排序
     * <p>
     * 参数含义：
     * PID  : progress identification，应用程序ID
     * S    : 进程的状态，其中S表示休眠，R表示正在运行，Z表示僵死状态，N表示该进程优先值是负数
     * #THR : 程序当前所用的线程数
     * VSS  : Virtual Set Size虚拟耗用内存（包含共享库占用的内存）
     * RSS  : Resident Set Size实际使用物理内存（包含共享库占用的内存）
     * PCY  : 前台(fg)和后台(bg)进程
     * UID  : User　Identification，用户身份ID
     * Name : 应用程序名称
     *
     * @return
     */
    public static String getCpuRate() {
        StringBuffer tv = new StringBuffer();
        String result;
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("top -n 1");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((result = br.readLine()) != null) {
                if (result.trim().length() < 1) {
                    continue;
                } else {
                    String[] CpuUsage = result.split("%");
                    String[] UserUsage = CpuUsage[0].split("User");
                    String[] SystemUsage = CpuUsage[1].split("System");
                    tv.append("User:" + UserUsage[1].trim() + "\n");
                    tv.append("System:" + SystemUsage[1].trim() + "\n");
                    tv.append(result + "\n");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tv.toString();
    }

    /**
     * 获得进程的CPU使用率
     *
     * @return
     */
    public static int[] getProcessCpuRate() {

        int[] cpuRate = new int[2];
        long totalCpuTime1[] = getTotalCpuTime();
        long processCpuTime1 = getAppCpuTime();
        try {
            Thread.sleep(360);
        } catch (Exception e) {

        }
        long totalCpuTime2[] = getTotalCpuTime();
        long processCpuTime2 = getAppCpuTime();

        long div0 = totalCpuTime2[0] - totalCpuTime1[0];
        if (div0 != 0) {
            cpuRate[0] = (int) (100 * (processCpuTime2 - processCpuTime1) / (div0));
        } else {
            cpuRate[0] = 0;
        }

        long a = (totalCpuTime2[0] - totalCpuTime1[0]) - (totalCpuTime2[1] - totalCpuTime1[1]);
        long div1 = (totalCpuTime2[0] - totalCpuTime1[0]);
        if (div1 != 0) {
            cpuRate[1] = (int) (100 * (a) / (div1));
        } else {
            cpuRate[1] = 0;
        }
        return cpuRate;
    }

    /**
     * 获取系统总CPU使用时间
     *
     * @return
     */
    private static long[] getTotalCpuTime() {
        String[] cpuInfos = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        long[] totalCpu = new long[2];
        totalCpu[0] = Long.parseLong(cpuInfos[2])
                + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
                + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
                + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
        totalCpu[1] = Long.parseLong(cpuInfos[5]);
        return totalCpu;
    }

    /**
     * 获取应用占用的CPU时间
     *
     * @return
     */
    private static long getAppCpuTime() {
        String[] cpuInfos = null;
        try {
            int pid = android.os.Process.myPid();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/" + pid + "/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        long appCpuTime = Long.parseLong(cpuInfos[13])
                + Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
                + Long.parseLong(cpuInfos[16]);
        return appCpuTime;
    }

    /**
     * 获取CPU的核数
     *
     * @return
     */
    public static int getNumberOfCPUCores() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            // Gingerbread doesn't support giving a single application access to both cores, but a
            // handful of devices (Atrix 4G and Droid X2 for example) were released with a dual-core
            // chipset and Gingerbread; that can let an app in the background run without impacting
            // the foreground application. But for our purposes, it makes them single core.
            return 1;
        }
        int cores;
        try {
            cores = new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER).length;
        } catch (SecurityException e) {
            cores = 1;
        } catch (NullPointerException e) {
            cores = 1;
        }
        return cores;
    }

    /**
     * 获取CPU的核数2
     *
     * @return
     */
    public static int getNumberOfCPUCores2() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * 【系统总内存】
     *
     * @param context
     * @return 单位为字节
     */
    /*
    public static String getSystemTotalMemory(Context context) {
        String meminfo = "/proc/meminfo";// 系统内存信息文件
        String info = null;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(meminfo);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            info = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
            arrayOfString = info.split("\\s+");

            for (String num : arrayOfString) {
                LogUtils.e(TAG, num + "");
            }

            initial_memory = Long.valueOf(arrayOfString[1]) * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();

        } catch (IOException e) {
        }
        return Formatter.formatFileSize(context, initial_memory);// Byte转换为KB或者MB，内存大小规格化
    }
    */

    /**
     * 【系统剩余内存】
     *
     * @param context
     * @return
     */
    /*
    public static String getSystemAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        // mi.availMem; 当前系统的可用内存
        return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
    }
    */

    /**
     * 单个APP内存限制大小 -Xmx【应用总内存】
     * maxMemory()这个方法返回的是java虚拟机（这个进程）能够从操作系统那里挖到的最大的内存
     *
     * @param context
     * @return
     */
    /*
    public static String getAppTotalMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //以兆为单位
        int maxMemory_android = am.getMemoryClass() * 1024 * 1024;
        return Formatter.formatFileSize(context, maxMemory_android);
    }
    */

    /**
     * 进程真正的可用可申请的内存大小【应用剩余内存】
     * maxMemory()-totalMemory()+freeMemory()
     * <p>
     * 查看指定程序内存使用情况
     * dumpsys meminfo com.zccl.ruiqianqi
     * <p>
     * 参数含义：
     * dalvik : dalvik使用的内存
     * native : native堆上的内存，指C\C++堆的内存（android 3.0以后bitmap就是放在这儿）
     * other  : 除了dalvik和native的内存，包含C\C++非堆内存······
     * Pss    : 该内存指将共享内存按比例分配到使用了共享内存的进程
     * allocated : 已使用的内存
     * free      : 空闲的内存
     * private dirty : 非共享，又不能被换页出去的内存（比如linux系统中为了提高分配内存速度而缓冲的小对象，即使你的进程已经退出，该内存也不会被释放）
     * share dirty   : 共享，但有不能被换页出去的内存
     *
     * @return
     */
    /*
    public static String getAppAvailMemory(Context context) {
        //java虚拟机能申请的内存上限
        long maxMemory = Runtime.getRuntime().maxMemory();
        //totalMemory()这个方法返回的是java虚拟机现在已经从操作系统那里挖过来的内存大小，
        //在java程序运行的过程的，内存总是慢慢的从操作系统那里挖的，基本上是用多少挖多少，
        //直到挖到maxMemory()为止，所以totalMemory()是慢慢增大的
        long totalMemory = Runtime.getRuntime().totalMemory();
        //java虚拟机每次在申请内存的稍微多挖一点的，这些挖过来而又没有用上的内存，实际上就是freeMemory()
        long freeMemory = Runtime.getRuntime().freeMemory();
        //byte
        long free = maxMemory - totalMemory + freeMemory;

        return Formatter.formatFileSize(context, free);//(maxMemory-totalMemory + freeMemory)/1024;
    }
    */

    /*****************************************【排序】*********************************************/
    /**
     * 冒泡排序
     *
     * @param src
     */
    public static void bubbleSortArray(float[] src) {
        int len = src.length;
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                float temp;
                if (src[i] < src[j]) {
                    temp = src[j];
                    src[j] = src[i];
                    src[i] = temp;
                }
            }
        }
    }

    /**
     * 快速排序取中间数
     *
     * @param src
     * @param start
     * @param end
     * @return
     */
    private static int getMiddle(int[] src, int start, int end) {
        int tmp = src[start];
        while (start < end) {
            while (start < end && src[end] >= tmp) {
                end--;
            }
            src[start] = src[end];
            while (start < end && src[start] <= tmp) {
                start++;
            }
            src[end] = src[start];
        }
        src[start] = tmp;
        return start;
    }


    /**********************************遍历MAP*******************************************/

    /**
     * 快速排序走起
     *
     * @param src
     * @param low
     * @param high
     */
    public static void fastSortArray(int[] src, int low, int high) {
        if (low < high) {
            int result = getMiddle(src, low, high);
            fastSortArray(src, low, result - 1);
            fastSortArray(src, result + 1, high);
        }
    }

    /**
     * 普遍使用，二次取值
     *
     * @param map
     */
    public static void lookAroundMap1(Map<String, String> map) {
        for (String key : map.keySet()) {
            LogUtils.s("key= " + key + " and value= " + map.get(key));
        }
    }

    /**
     * 尤其是容量大时,通过Map.entrySet遍历key和value
     *
     * @param map
     */
    public static void lookAroundMap2(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            LogUtils.s("key= " + entry.getKey() + " and value= " + entry.getValue());
        }
    }

    /**
     * 通过Map.entrySet使用iterator遍历key和value
     *
     * @param map
     */
    public static void lookAroundMap3(Map<String, String> map) {
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            LogUtils.s("key= " + entry.getKey() + " and value= " + entry.getValue());
        }
    }

    /**
     * 通过Map.values()遍历所有的value，但不能遍历key
     *
     * @param map
     */
    public static void lookAroundMap4(Map<String, String> map) {
        LogUtils.s("通过Map.values()遍历所有的value，但不能遍历key");
        for (String v : map.values()) {
            LogUtils.s("value= " + v);
        }
    }

    /**
     * 防止快速点击
     *
     * @return
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 检查有没有指定权限
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean checkPermissions(Context context, String permission) {
        boolean granted = true;
        try {
            granted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        } catch (Throwable ignored) {

        }
        return granted;
    }

    /**
     * 验证是否是（正常）数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        // Pattern pattern = Pattern.compile("[0-9]*");
        Pattern pattern = Pattern.compile("^(0|[1-9][0-9]*)$");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * bytes(小端)转int
     *
     * @param buf
     * @return
     */
    public static int bytesLittleToInt(byte[] buf) {
        if (buf.length < 4) {
            return -1;
        }
        int yes = 0;
        for (int i = 0; i < 4; i++) {
            int n = (buf[i] < 0 ? buf[i] + 256 : buf[i]) << (8 * i);
            yes += n;
        }
        return yes;
    }

    /**
     * bytes(大端)转int
     *
     * @param buf
     * @return
     */
    public static int bytesBigToInt(byte[] buf) {
        if (buf.length < 4) {
            return -1;
        }
        int yes = 0;
        for (int i = 0; i < 4; i++) {
            int n = (buf[i] < 0 ? buf[i] + 256 : buf[i]) << (8 * (3 - i));
            yes += n;
        }
        return yes;
    }

    /**
     * 将int转换成bytes(大端)
     *
     * @param num
     * @return
     */
    public static byte[] intToBigBytes(int num) {
        byte[] b = new byte[4];
        b[0] = (byte) (num >> 24 & 0xff);
        b[1] = (byte) (num >> 16 & 0xff);
        b[2] = (byte) (num >> 8 & 0xff);
        b[3] = (byte) (num & 0xff);
        return b;
    }

    /**
     * 将int转换成bytes(小端)
     *
     * @param num
     * @return
     */
    public static byte[] intToLittleBytes(int num) {
        byte[] b = new byte[4];
        b[0] = (byte) (num & 0xff);
        b[1] = (byte) (num >> 8 & 0xff);
        b[2] = (byte) (num >> 16 & 0xff);
        b[3] = (byte) (num >> 24 & 0xff);
        return b;
    }

    /**
     * 检查有没有录音权限
     *
     * @param context
     * @return
     */
    protected static boolean checkRecordPermissions(Context context) {
        boolean granted = true;
        try {
            granted = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        } catch (Throwable ignored) {

        }
        return granted;
    }
}
