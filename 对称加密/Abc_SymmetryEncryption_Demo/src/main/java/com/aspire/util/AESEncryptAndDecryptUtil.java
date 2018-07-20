package com.aspire.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

/**
 * 对称加密解密AES算法
 *
 * @author JustryDeng
 * @date 2018年7月20日 下午6:12:35
 */
public class AESEncryptAndDecryptUtil {

	/** 对应:算法/模式/补码方式 */
	private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

	/** 高级加密标准算法 */
	private static final String ALGORITHM = "AES";

	/** 字符编码 */
	private static final String CHARSET = "utf-8";

	/** 必须16位 must be 16 bytes long */
	private static final String IV = "A-16-Byte-String";

	/**
	 * key为 [16位字节长度 或 24位字节长度 或者 32位字节长度]的字符串 注: key不要是中文
	 * 注:我们可以定死key,也可以通过传参来获取传过来的key(本人采用传参的方式,所以这个属性注释掉了)
	 */
	// private static final String KEY = "A-16-Byte-keyVal";

	/**
	 * 加密
	 *
	 * @param context
	 *            要加密的字符串数据
	 * @param key
	 *            秘钥
	 * @return 加密后的数据字符串
	 * @date 2018年7月20日 下午6:17:44
	 */
	public static String encrypt(String context, String key) {
		try {
			byte[] decode = context.getBytes(CHARSET);
			byte[] bytes = createKeyAndIv(decode, Cipher.ENCRYPT_MODE, key);
			return Base64.getEncoder().encodeToString(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解密
	 *
	 * @param context
	 *            要解密的字符串数据
	 * @param key
	 *            对应的秘钥(不论是前端还是后端加的密,key都要和加密时的一样)
	 * @return 解密后的数据字符串
	 * @date 2018年7月20日 下午6:18:35
	 */
	public static String decrypt(String context, String key) {
		try {
			Base64.Decoder decoder = Base64.getDecoder();
			byte[] decode = decoder.decode(context);
			byte[] bytes = createKeyAndIv(decode, Cipher.DECRYPT_MODE, key);
			return new String(bytes, CHARSET);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 与cipherFilter方法配合,获取加密/解密后的字节数组
	 *
	 * @date 2018年7月20日 下午6:21:34
	 */
	public static byte[] createKeyAndIv(byte[] context, int opmode, final String key) throws Exception {
		byte[] keyArray = key.getBytes(CHARSET);
		byte[] iv = IV.getBytes(CHARSET);
		return cipherFilter(context, opmode, keyArray, iv);
	}

	public static byte[] cipherFilter(byte[] context, int opmode, byte[] key, byte[] iv) throws Exception {
		Key secretKeySpec = new SecretKeySpec(key, ALGORITHM);
		AlgorithmParameterSpec ivParameterSpec = new IvParameterSpec(iv);
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(opmode, secretKeySpec, ivParameterSpec);
		return cipher.doFinal(context);
	}

	/// **
	// * 主方法测试
	// *
	// */
	// public static void main(String[] args) {
	// // 16个 或 24个 或 32个 字节长度 即可（注:不要是中文）
	// String key = "key0123456789key";
	// String context = "JustryDeng";
	// System.out.println("原数据:" + context);
	// // 加密
	// String encrypt = encrypt(context, key);
	// System.out.println("加密后:" + encrypt);
	// // 解密
	// String decrypt = decrypt(encrypt, key);
	// System.out.println("解密后:" + decrypt);
	// }

}
