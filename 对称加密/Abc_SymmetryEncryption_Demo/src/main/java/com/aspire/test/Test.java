package com.aspire.test;

import com.aspire.util.AESEncryptAndDecryptUtil;

/**
 * 以主函数进行测试
 *
 * @author JustryDeng
 * @date 2018年7月20日 下午6:40:05
 */
public class Test {
	public static void main(String[] args) {
		// 16个 或 24个 或 32个 字节长度 即可（注:不要是中文）
		String key = "key0123456789key";
		String context = "JustryDeng";
		System.out.println("原数据:" + context);
		// 加密
		String encrypt = AESEncryptAndDecryptUtil.encrypt(context, key);
		System.out.println("加密后:" + encrypt);
		// 解密
		String decrypt = AESEncryptAndDecryptUtil.decrypt(encrypt, key);
		System.out.println("解密后:" + decrypt);
	}
}
