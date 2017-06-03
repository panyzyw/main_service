package com.zccl.ruiqianqi.tools.encrypt;

import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class MD5Util {
    /**
     * 默认的密码字符串组合，用来将字节转换成16进制表示的字符串
     * apache校验下载的文件的正确性用的就是默认的这个组合 
     */
    protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /**MD5单例对象*/
    protected static MessageDigest messagedigest = null;
    /**多种转换方式*/
    private static int whichWay = 1;

    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            LogUtils.e(MD5Util.class.getName(), "初始化失败，MessageDigest不支持MD5Util");
            e.printStackTrace();
        }
    }

    /**
     * 生成字符串的md5校验码
     *
     * @param str
     * @return
     */
    public static String getMD5String(String str) {
        return getMD5String(str.getBytes());
    }

    /**
     * 字节数组的MD5值
     * @param bytes
     * @return
     */
    public static String getMD5String(byte[] bytes) {
        messagedigest.update(bytes);
        return parseByte2HexStr(messagedigest.digest());
    }

    /**
     * 判断字符串的md5校验码是否与已知的md5码相匹配 
     *
     * @param password 要校验的字符串
     * @param md5PwdStr 已知的md5校验码
     * @return
     */
    public static boolean checkPassword(String password, String md5PwdStr) {
        String s = getMD5String(password);
        return s.equalsIgnoreCase(md5PwdStr);
    }


    /**
     * 生成文件的md5校验码
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String getFileMD5String(File file) throws IOException {
        InputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int numRead = 0;
        while ((numRead = fis.read(buffer)) > 0) {
            messagedigest.update(buffer, 0, numRead);
        }
        fis.close();
        return parseByte2HexStr(messagedigest.digest());
    }

    /**
     * JDK1.4中不支持以MappedByteBuffer类型为参数update方法，并且网上有讨论要慎用MappedByteBuffer
     * 原因是当使用 FileChannel.map方法时，MappedByteBuffer已经在系统内占用了一个句柄， 
     * 而使用FileChannel.close 方法是无法释放这个句柄的，且FileChannel又没有提供unmap方法， 
     * 因此会出现无法删除文件的情况。
     *
     * 不推荐使用
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String getFileMD5String_old(File file) {
        FileInputStream ins = null;
        try {
            ins = new FileInputStream(file);
            if(ins != null){
                FileChannel ch = ins.getChannel();
                MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY,
                        0, file.length());
                messagedigest.update(byteBuffer);

                BigInteger bi = new BigInteger(1, messagedigest.digest());
                String value = bi.toString(16);

                ch.close();
                ins.close();
                return value;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        return null;
    }

    /**
     * 不可读的字节数组要用一种可逆方式转换成可读的字符串
     * Convert byte array to string, byte[]{8, 16} will be converted to 0811.
     * @param array
     * @return String 转换后16进制字符串
     */
    public static String parseByte2HexStr(byte[] array) {
        if (null == array) {
            return null;
        }
        int length = array.length;

        //长度扩倍
        StringBuilder sb = new StringBuilder(length << 1);
        for (int i = 0; i < length; i++) {
            int b = array[i];

            if(whichWay==1){

                //低4位（0~15）
                int low = b & 0xf;
                //高4位（0~15）（逻辑右移）
                int high = (b >>> 4) & 0xf;

                //分别对应数组中的字符
                sb.append(hexDigits[high]);
                sb.append(hexDigits[low]);

            }else if(whichWay==2){

                String hex = Integer.toHexString(b & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                sb.append(hex.toUpperCase(Locale.getDefault()));

            }else if(whichWay==3){

                //高位
                sb.append(Character.forDigit((b & 0xF0) >>> 4, 16));
                //低位
                sb.append(Character.forDigit(b & 0x0F, 16));
            }
        }
        return sb.toString();
    }

    /**
     * 字节数组转16进制字符串
     * @param array
     * @return
     */
    public static String parseByte2HexStr2(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        return bi.toString(16);
    }

    /**
     * Convert string to byte array.
     * "0811"------>byte[]{8, 16}
     * @param hexStr 十六进制字符串
     * @return byte[] 转换后字节数组
     *
     * see {@link #parseByte2HexStr(byte[])}
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (StringUtils.isEmpty(hexStr)) {
            return null;
        }
        String sub = hexStr;

        int len = sub.length();

        //长度除以2
        byte[] array = new byte[len >> 1];
        for (int i = 0, j = 0; i < len; i += 2, j++) {
            if(whichWay==1){
                char highChar = sub.charAt(i);
                char lowChar = sub.charAt(i + 1);

                String str = new String(new char[] { highChar, lowChar });
                int value = Integer.parseInt(str, 16);
                array[j] = (byte) value;

            }else if(whichWay==2){

                int high = Integer.parseInt(hexStr.substring(i, i+1), 16);
                int low = Integer.parseInt(hexStr.substring(i+1, i+2), 16);
                array[j] = (byte) (high << 4 + low);

            }else if(whichWay==3){

                int high = Integer.parseInt(hexStr.substring(i, i+1), 16);
                int low = Integer.parseInt(hexStr.substring(i+1, i+2), 16);
                array[j] = (byte) (high << 4 + low);

            }

        }
        return array;
    }



    /**
     * 都是对字节数组做加密，然后转成可读的16进制字符串
     * MD5字符串--------签名
     */
    public static String sign(String content, String key) {
        try {
            byte[] btInput = (content + key).getBytes("UTF-8");
            messagedigest.update(btInput);
            byte[] md = messagedigest.digest();

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < md.length; i++) {
                int val = ((int) md[i]) & 0xff;
                if (val < 16){
                    sb.append("0");
                }
                sb.append(Integer.toHexString(val));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}  
