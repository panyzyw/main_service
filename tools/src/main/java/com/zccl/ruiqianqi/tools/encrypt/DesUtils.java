package com.zccl.ruiqianqi.tools.encrypt;

import com.zccl.ruiqianqi.tools.StringUtils;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * des 数据加密解密算法(对称加密)
 * Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
 * cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
 * DES一共有电子密码本模式（ECB）、加密分组链接模式（CBC）、加密反馈模式（CFB）和输出反馈模式（OFB）四种模式
 * 工作模式、填充模式、初始化向量这三种因素一个都不能少。否则，如果你不指定的话，那么就要程序就要调用默认实现。
 * 问题就来了，这就与平台有关了。难怪网上一搜"DES加密结果不一致“，出现n多网页结果
 *
 * 注意：DES加密和解密过程中，密钥长度都必须是8的倍数
 *
 * @author zccl
 */
public class DesUtils {

	/**DES算法的入口参数有三个：Key、Data、Mode。其中Key为8个字节共64位*/
	public static final int DES_KEY_LEN = 8;

	/**
	 * 加密方法1
	 * @param encryptStr
	 * @param encryptKey
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String encryptStr, String encryptKey) throws Exception {

		IvParameterSpec zeroIv = new IvParameterSpec(getIV());
		//DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		//方法1
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		//自动过滤8字节
		DESKeySpec dks = new DESKeySpec(encryptKey.getBytes());
		SecretKey securekey = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, securekey, zeroIv, sr);
		byte[] encryptedData = cipher.doFinal(encryptStr.getBytes());

		//bytes To 16Str
		//return parseByte2HexStr(encryptedData);
		//bytes To 26Str
		return Base64.encode(encryptedData);
	}

	/**
	 * 解密方法1
	 * @param decryptStr
	 * @param decryptKey
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String decryptStr, String decryptKey) throws Exception {
		byte[] bytes = Base64.decode(decryptStr);

		// DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		IvParameterSpec zeroIv = new IvParameterSpec(getIV());
		//方法1
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		//自动过滤8字节
		DESKeySpec dks = new DESKeySpec(decryptKey.getBytes());
		SecretKey securekey = keyFactory.generateSecret(dks);

		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

		cipher.init(Cipher.DECRYPT_MODE, securekey, zeroIv, sr);
		// 正式执行解密操作
		return new String(cipher.doFinal(bytes));
	}

	/**
	 * IV length: must be 8 bytes long
	 * @return
	 */
	private static byte[] getIV(){
		String iv = "12345678";
		return iv.getBytes();
	}
	/**
	 * 加密方法2
	 * @param encryptStr
	 * @param encryptKey
	 * @return
	 * @throws Exception
	 */
	public static String encryptDES(String encryptStr, String encryptKey) throws Exception {
		//DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		IvParameterSpec zeroIv = new IvParameterSpec(getIV());
		//方法2
		Key key = getKey(encryptKey);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv, sr);
		byte[] encryptedData = cipher.doFinal(encryptStr.getBytes());
		return MD5Util.parseByte2HexStr(encryptedData);
	}

	/**
	 * 解密方法2
	 * @param decryptStr
	 * @param decryptKey
	 * @return
	 * @throws Exception
	 */
	public static String decryptDES(String decryptStr, String decryptKey) throws Exception {
		byte[] bytes = MD5Util.parseHexStr2Byte(decryptStr);
		//DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		IvParameterSpec zeroIv = new IvParameterSpec(getIV());
		//方法2
		Key key = getKey(decryptKey);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key, zeroIv, sr);
		byte decryptedData[] = cipher.doFinal(bytes);
		return new String(decryptedData);
	}

	/**
	 * Key为8个字节共64位(DESKeySpec就是这么算的)
	 * @param keyStr
	 * @return
	 * @throws Exception
	 */
	private static Key getKey(String keyStr) throws Exception {
		if(StringUtils.isEmpty(keyStr)){
			keyStr = getDefaultKey();
		}
		byte[] keys = keyStr.getBytes();
		byte[] keyArray = new byte[8];
		for (int i = 0; i < keys.length && i < keyArray.length; i++) {
			keyArray[i] = keys[i];
		}
		Key key = new SecretKeySpec(keyArray, "DES");
		return key;
	}

	/**
	 * 游戏KEY,注意：DES加密和解密过程中，密钥长度都必须是8的倍数
	 * @return
	 */
	private final static String getDefaultKey(){
		return "";
	}
}
