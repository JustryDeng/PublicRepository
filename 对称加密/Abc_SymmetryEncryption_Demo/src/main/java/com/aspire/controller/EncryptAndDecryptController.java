package com.aspire.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aspire.util.AESEncryptAndDecryptUtil;

/**
 * 前端对称加密 给 后端加密
 *
 * @author JustryDeng
 * @date 2018年7月20日 下午3:19:44
 */
@RestController
public class EncryptAndDecryptController {
	
	// KEY要与前端加密时保持一致(且:应为:16或24或32字节长度)
	private static final String KEY = "A-16-Byte-keyVal";

	/**
	 * 
	 *
	 * @param encryptedTarget
	 *            加密后的数据字符串
	 * @return 
	 * @date 2018年7月20日 下午6:47:28
	 */
	@RequestMapping("/EncryptAndDecryptTest")
	public String test(@RequestParam("encryptedTarget") String encryptedTarget) {
		StringBuffer sb = new StringBuffer();
		try {
            // 调用后端AES加密解密工具类(在上面的示例中以给出)
			String decryptTarget = AESEncryptAndDecryptUtil.decrypt(encryptedTarget,KEY);
			sb.append(">>>解密前:" + encryptedTarget);
			sb.append(">>>解密前的内容长度:" + encryptedTarget.length());
			sb.append(">>>加密密钥和解密密钥:" + KEY);
			sb.append(">>>解密后：" + decryptTarget);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

}
