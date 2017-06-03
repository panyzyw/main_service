package com.zccl.ruiqianqi.tools.encrypt;

import com.zccl.ruiqianqi.tools.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密类
 * AES是一种对称的私钥加密技术。它支持128，192，256位加密。
 */
public class AesUtils {
	static final String CIPHER_ALGORITHM_ECB = "AES/ECB/PKCS5Padding";
	static final String CIPHER_ALGORITHM_CBC = "AES/CBC/PKCS5Padding";
	/**
	 * AES/CBC/NoPadding 要求
	 * 密钥必须是16位的；Initialization vector (IV) 必须是16位
	 * 待加密内容的长度必须是16的倍数，如果不是16的倍数，就会出如下异常：
	 * javax.crypto.IllegalBlockSizeException: InputUtils length not multiple of 16 bytes
	 *
	 * 由于固定了位数，所以对于被加密数据有中文的, 加、解密不完整
	 *
	 * 可 以看到，在原始数据长度为16的整数n倍时，假如原始数据长度等于16*n，则使用NoPadding时加密后数据长度等于16*n，
	 * 其它情况下加密数据长 度等于16*(n+1)。在不足16的整数倍的情况下，假如原始数据长度等于16*n+m[其中m小于16]，
	 * 除了NoPadding填充之外的任何方 式，加密数据长度都等于16*(n+1).
	 */
	static final String CIPHER_ALGORITHM_CBC_NoPadding = "AES/CBC/NoPadding";

	/**
	 * 有密钥的加密
	 * @param input
	 * @param key
	 * @return
	 */
	public static String encrypt(String input, String key) {
		byte[] crypted = null;
		try {
			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_ECB);
			cipher.init(Cipher.ENCRYPT_MODE, skey);
			crypted = cipher.doFinal(input.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Base64.encode(crypted);
	}

	/**
	 * 有密钥的解密
	 * @param input
	 * @param key
	 * @return
	 */
	public static String decrypt(String input, String key) {
		byte[] output = null;
		try {
			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_ECB);
			cipher.init(Cipher.DECRYPT_MODE, skey);
			output = cipher.doFinal(Base64.decode(input));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return new String(output);
	}

	/**
	 * 默认密钥加密
	 * @param data
	 * @return
	 */
	public static String encryption(String data) {
		String key = getDefaultKey();
		if(StringUtils.isEmpty(key)){
			return null;
		}
		else{
			int len = key.length();
			if(len % 16 == 0){
				data = AesUtils.encrypt(data, getDefaultKey());
				return data;
			}
		}
		return null;
	}

	/**
	 * 默认密钥解密
	 * @param data
	 * @return
	 */
	public static String decryption(String data) {
		data = AesUtils.decrypt(data, getDefaultKey());
		return data;
	}

	/**
	 * 游戏KEY,密钥长度必须是16位的，加密内容长度没有限制
	 *
	 * @return
	 */
	private final static String getDefaultKey() {
		return "";
	}


	/****************************AES算法2************************************/
	/**
	 * IV length: must be 16 bytes long
	 * @return
	 */
	private static byte[] getIV(){
		String iv = "1234567812345678";
		return iv.getBytes();
	}
	/**
	 * 加密（这种方式密码长度没有限制，加密内容长度也没有限制）
	 * @param contents  需要加密的内容
	 * @param password  加密密码
	 * @return
	 */
	public static String encryptAES(String contents, String password) {
		try {
			//创建KEY (这段代码在不同方法中创建的KEY不一样，没办法呀)
			/*KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(password.getBytes()));
			Key secretKey = kgen.generateKey();
			LogUtils.e("secretKey", Base64.encode(secretKey.getEncoded())+"");
			secretKey = new SecretKeySpec(secretKey.getEncoded(), "AES");
			*/
			Key secretKey = new SecretKeySpec(password.getBytes(), "AES");

			IvParameterSpec zeroIv = new IvParameterSpec(getIV());

			//创建密码器
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, zeroIv);
			//加密
			byte[] result = cipher.doFinal(contents.getBytes("UTF-8"));

			return Base64.encode(result);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解密（这种方式密码长度没有限制，加密内容长度也没有限制）
	 * @param contents  待解密内容
	 * @param password  解密密钥
	 * @return
	 */
	public static String decryptAES(String contents, String password) {
		try {
			//创建KEY (这段代码在不同方法中创建的KEY不一样，没办法呀)
			/*KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(password.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			LogUtils.e("secretKey2", Base64.encode(secretKey.getEncoded())+"");
			secretKey = new SecretKeySpec(secretKey.getEncoded(), "AES");
			*/
			Key secretKey = new SecretKeySpec(password.getBytes(), "AES");

			IvParameterSpec zeroIv = new IvParameterSpec(getIV());

			//创建密码器
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, zeroIv);
			byte[] bytes = Base64.decode(contents);
			//解密
			byte[] result = cipher.doFinal(bytes);
			return new String(result,"UTF-8");

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return null;
	}
}
