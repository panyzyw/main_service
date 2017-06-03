package com.zccl.ruiqianqi.tools.encrypt;

import com.zccl.ruiqianqi.tools.LogUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * 非对称加密
 * 密钥的初始化长度为1024位，密钥的长度越长，安全性就越好，但是加密解密所用的时间就会越多。
 * 而一次能加密的密文长度也与密钥的长度成正比。一次能加密的密文长度为：密钥的长度/8-11。
 * 所以1024bit长度的密钥一次可以加密的密文为   1024/8-11=117 byte。
 * 所以非对称加密一般都用于加密对称加密算法的密钥，而不是直接加密内容。
 * 对于小文件可以使用RSA加密，但加密过程仍可能会使用分段加密。
 * 
 * @author zccl
 *
 */
public class RsaUtils {

    private static final String RSA_PUBLICE =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDiqS61e7TTJLlncD1P1pcMKFW1\n" +
            "Q6s/RPh5KPODR5oYBEbbD56hK6sQ5wN7wUuJ1EvIHEoX98ky/PGvQS7BzqouECRW\n" +
            "vhsr3tLXaA65KwuC6mhCI+pxQvMFoV2xG1DMD2qgS5DCEnP0QjpiWa3F7+nDpS0G\n" +
            "iY/T6onEcJO3fyDbBQIDAQAB";
    /*
    private static final String RSA_PRIVATE =
            "MIICXgIBAAKBgQDiqS61e7TTJLlncD1P1pcMKFW1Q6s/RPh5KPODR5oYBEbbD56h\n" +
            "K6sQ5wN7wUuJ1EvIHEoX98ky/PGvQS7BzqouECRWvhsr3tLXaA65KwuC6mhCI+px\n" +
            "QvMFoV2xG1DMD2qgS5DCEnP0QjpiWa3F7+nDpS0GiY/T6onEcJO3fyDbBQIDAQAB\n" +
            "AoGAF5D7mEb6cBGbbVPQLCLlpY/0CVq1zWhAZWiKmRWywdh//XrMRXmi2akLRay9\n" +
            "S+FU18OJOKC8vJ5sCoq+b8wrCVji/VJ7VtUC02OvshThEJVUxAgYWDbSRKbkxj/9\n" +
            "IXqS2y+C2wGraKYW+4bdXAzAz8n+DAh7ofa8uNtwv52B4ikCQQD8Z9btZbQjONHW\n" +
            "iSOXQbZKojyNgIwLwYuKTX5rXAfTN7h+4W+L21ZXz2tKMj3RBuPYkzBeO1aJBFT+\n" +
            "jcEdAHNXAkEA5eN9IU8A2V6lhWIzzKAxOI5xTh2Kx3FdflOOGnOycC7IJuz+DKEY\n" +
            "fKcqiWaIkTdPgFcv3aX/zx+2OHs3JNTnAwJBAK5SzWcyeeJlqM+B8dnSoaMUP7v/\n" +
            "DzpDVyuS8AAYFLN1ZXAEJPUz7INDFzesUXNfTdfTeHFsjAyfuFAC+WAdYosCQQCj\n" +
            "IBGimRTZKbf6NfzbrrA+mZT9ASmA7IiTI2cqjf6IbCnKtVBwVb+ydExSw1cU9FKN\n" +
            "l1B3z1r99lqvKX+vuwNRAkEA2qOiKMPRkz4AKX+266vFBoJA73GnMc4zYiVy3mSM\n" +
            "afRj9vl2Tc6AMHzrbKNi76qF3D9MQe8W6Cv2jQ+CkBC97g==";
    */

    private static final String KEY_SHA = "SHA";
    private static final String KEY_MD5 = "MD5";

	/**
	 * 默认实现：
	 * 每次生成的密文都不一致，即使每次你的明文一样、使用同一个公钥 
	 */
    private static final String KEY_ALGORITHM="RSA/None/PKCS1Padding";
	/** 
	 * 同一个明文、同一个公钥每次生成同一个密文 
	 * 因为使用了 nopadding 所以对于同一密钥同一明文，本文总是生成一样的密文
	 */
    private static final String KEY_ALGORITHM_NOPAD="RSA/None/NoPadding";

    private static final String KEY_ECB_ALGORITHM="RSA/ECB/PKCS1Padding";

    private static final String ALGORITHM = "RSA";

	/** RSA的MD5签名*/
    private static final String SIGNATURE_ALGORITHM="MD5withRSA";

    private static final String PUBLIC_KEY = "RSAPublicKey";//公钥
    private static final String PRIVATE_KEY = "RSAPrivateKey";//私钥
	
	/**
     * 初始化：生成密钥
     * @return
     * @throws Exception
     */
    public static Map<String,Object> initKey() throws Exception{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
         
        //公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        //私钥
        RSAPrivateKey privateKey =  (RSAPrivateKey) keyPair.getPrivate();
         
        Map<String,Object> keyMap = new HashMap<String, Object>(2);
        
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
         
        return keyMap;
    }
    
    /**
     * 取得公钥，并转化为String类型
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap) throws Exception{
        Key key = (Key) keyMap.get(PUBLIC_KEY);  
        return encryptBASE64(key.getEncoded());     
    }
 
    /**
     * 取得私钥，并转化为String类型
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap) throws Exception{
        Key key = (Key) keyMap.get(PRIVATE_KEY);  
        return encryptBASE64(key.getEncoded());     
    }

    /************************************公钥1*************************************************/
    /**
     * 用公钥加密
     * @param data  要加密的数据
     * @param key   密钥
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String key)throws Exception{
        //对公钥解密
        byte[] keyBytes = decryptBASE64(key);
        //取公钥
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        //对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(data);
    }

    /**
     * 用公钥解密
     * @param data  已加密数据
     * @param key   密钥
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] data, String key)throws Exception{
        //对公钥解密
        byte[] keyBytes = decryptBASE64(key);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
         
        //对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
         
        return cipher.doFinal(data);
    }


    /************************************私钥1*************************************************/
    /**
     * 用私钥加密
     * @param data  要加密的数据
     * @param key   密钥
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String key)throws Exception{
        //解密密钥
        byte[] keyBytes = decryptBASE64(key);

        //取私钥
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

        //对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        return cipher.doFinal(data);
    }

    /**
     * 用私钥解密
     * @param data  加密数据
     * @param key   密钥
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] data, String key)throws Exception{
        //对私钥解密
        byte[] keyBytes = decryptBASE64(key);

        //取私钥
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, "BC");
        Key privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

        //对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(data);
    }



    /*****************************************************************************************/
    /*****************************************************************************************/
    /*****************************************************************************************/
    /*************************************公钥2************************************************/
    /**
     * 使用公钥加密
     * @param content 已加密数据
     * @return
     */
    public static String encryptByPublicKey(String content) {
        try {

            //对公钥解密
            byte[] keyBytes = decryptBASE64(RSA_PUBLICE);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

            //对数据解密
            Cipher cipher = Cipher.getInstance(KEY_ECB_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] output = cipher.doFinal(content.getBytes("utf-8"));
            String str = new String(Base64.encode(output));
            return str;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用公钥解密
     * @param content 密文
     * @return 解密后的字符串
     */
    public static String decryptByPublicKey(String content) {
        try {
            //对公钥解密
            byte[] keyBytes = decryptBASE64(RSA_PUBLICE);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

            Cipher cipher = Cipher.getInstance(KEY_ECB_ALGORITHM);
            //Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);


            byte [] decodeBytes = Base64.decode(content);
            if(decodeBytes != null) {
                InputStream ins = new ByteArrayInputStream(decodeBytes);
                ByteArrayOutputStream writer = new ByteArrayOutputStream();
                byte[] buf = new byte[128];
                int bufcheck = -1;
                while ((bufcheck = ins.read(buf)) != -1) {
                    byte[] block = null;
                    if (buf.length == bufcheck) {
                        block = buf;
                    } else {
                        block = new byte[bufcheck];
                        for (int i = 0; i < bufcheck; i++) {
                            block[i] = buf[i];
                        }
                    }
                    writer.write(cipher.doFinal(block));
                }
                return new String(writer.toByteArray(), "utf-8");
            }else{
                LogUtils.e("公钥解密", "Base64无法解密");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    /*************************************私钥2***********************************************/
    /**
     * 用私钥加密
     * @param content  要加密的数据
     * @return
     * @throws Exception
     */
    /*public static String encryptByPrivateKey(String content)throws Exception{
        //解密密钥
        byte[] keyBytes = decryptBASE64(RSA_PRIVATE);

        //取私钥
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, "BC");
        Key privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

        //对数据加密
        Cipher cipher = Cipher.getInstance(KEY_ECB_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        byte[] output = cipher.doFinal(content.getBytes("utf-8"));
        String str = new String(Base64.encode(output));
        return str;
    }*/

    /**
     * 用私钥解密
     * @param content  加密数据
     * @return
     * @throws Exception
     */
    /*public static String decryptByPrivateKey(String content)throws Exception{
        //对私钥解密
        byte[] keyBytes = decryptBASE64(RSA_PRIVATE);

        //取私钥
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, "BC");
        Key privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

        //对数据解密
        Cipher cipher = Cipher.getInstance(KEY_ECB_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        //分段解密
        InputStream ins = new ByteArrayInputStream(Base64.decode(content));
        ByteArrayOutputStream writer = new ByteArrayOutputStream();
        byte[] buf = new byte[128];
        int bufcheck = -1;
        while ((bufcheck = ins.read(buf)) != -1) {
            byte[] block = null;
            if (buf.length == bufcheck) {
                block = buf;
            } else {
                block = new byte[bufcheck];
                for (int i = 0; i < bufcheck; i++) {
                    block[i] = buf[i];
                }
            }
            writer.write(cipher.doFinal(block));
        }
        return new String(writer.toByteArray(), "utf-8");
    }
    */
    

    /******************************************************************************************/
    /**
     * 用私钥对信息生成数字签名
     * @param data  		加密数据
     * @param privateKey    私钥
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey)throws Exception{
        //解密私钥
        byte[] keyBytes = decryptBASE64(privateKey);
        //构造PKCS8EncodedKeySpec对象
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        //指定加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //取私钥匙对象
        PrivateKey privateKey2 = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        //用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey2);
        signature.update(data);
         
        return encryptBASE64(signature.sign());
    }
    
    /**
     * 用公钥校验数字签名
     * @param data  加密数据
     * @param publicKey 公钥
     * @param sign  数字签名
     * @return
     * @throws Exception
     */
    public static boolean verify(byte[] data,String publicKey,String sign)throws Exception{
        //解密公钥
        byte[] keyBytes = decryptBASE64(publicKey);
        //构造X509EncodedKeySpec对象
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        //指定加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //取公钥匙对象
        PublicKey publicKey2 = keyFactory.generatePublic(x509EncodedKeySpec);
         
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey2);
        signature.update(data);
        //验证签名是否正常
        return signature.verify(decryptBASE64(sign));
    }
    
    
	/**
	 * BASE64解密
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptBASE64(String key) throws Exception {
		return Base64.decode(key);
	}

	/**
	 * BASE64加密（16进制编码 和 64进制编码）
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encryptBASE64(byte[] key) throws Exception {
		return Base64.encode(key);
	}

	/**
	 * MD5加密
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptMD5(byte[] data) throws Exception {
		MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
		md5.update(data);
		return md5.digest();
	}

	/**
	 * SHA加密
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptSHA(byte[] data) throws Exception {
		MessageDigest sha = MessageDigest.getInstance(KEY_SHA);
		sha.update(data);
		return sha.digest();
	}
}
