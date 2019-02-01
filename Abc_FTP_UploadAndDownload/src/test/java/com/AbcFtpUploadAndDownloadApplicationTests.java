package com;


import com.aspire.util.FtpUtil;
import org.apache.commons.net.ftp.FTP;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

/**
 * 编写于2018年9月27日， 优化完善于2019年2月1日
 *
 * @author JustryDeng
 * @date 2019/2/1 12:12
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AbcFtpUploadAndDownloadApplicationTests {

	/** FTP地址 */
	private final String IP_ADDRESS = "192.168.2.103";

	/** FTP端口 */
	private final Integer PORT = 21;

	/** FTP用户名 */
	private final String USERNAME = "JustryDeng";

	/** FTP秘密 */
	private final String PASSWORD = "dengshuai";

	/**
	 * deleteBlankDirOrFile删除 --- 测试
	 *
	 * @date 2018年9月27日 上午10:12:56
	 */
	@Test
	public void deleteTest() throws IOException {

		/// 基本配置
		FtpUtil ftpUtil = FtpUtil.getFtpUtilInstance(IP_ADDRESS, PORT, USERNAME, PASSWORD);
		// 注:根据不同的(Server/Client)情况,这里灵活设置
		//    编码不对可能导致文件删除失败
		ftpUtil.setSendCommandStringEncoding("UTF-8");
		try {
			// 执行删除
			ftpUtil.deleteBlankDirOrFile("/abc/JustryDeng.html");
		} finally {
			// 释放资源
			ftpUtil.releaseResource();
		}
	}

	/**
	 * recursiveDeleteBlankDirOrFile(即:deleteBlankDirOrFile升级版)删除 --- 测试
	 *
	 * @date 2018年9月26日 下午4:51:51
	 */
	@Test
	public void recursiveDeleteTest() throws IOException {
		/// 基本配置
		FtpUtil ftpUtil = FtpUtil.getFtpUtilInstance(IP_ADDRESS, PORT, USERNAME, PASSWORD);
		// 注:根据不同的(Server/Client)情况,这里灵活设置
		//    编码不对可能导致文件删除失败
		ftpUtil.setSendCommandStringEncoding("UTF-8");
		boolean result;
		try {
			// 执行删除
			result = ftpUtil.recursiveDeleteBlankDirOrFile("/abc");
		} finally {
			// 释放资源
			ftpUtil.releaseResource();
		}
		System.out.println(" 删除结果 -> " + result);
	}


	/**
	 * recursiveDownloadFile(即:downloadFile升级版)下载 --- 测试
	 *
	 * @date 2018年9月26日 下午4:51:51
	 */
	@Test
	public  void recursiveDownloadTest()  throws IOException {
		/// 基本配置
		FtpUtil ftpUtil = FtpUtil.getFtpUtilInstance(IP_ADDRESS, PORT, USERNAME, PASSWORD);
		// 注:根据不同的(Server/Client)情况,这里灵活设置;
		ftpUtil.setDownfileNameEncodingParam1("UTF-8");
		// 注:根据不同的(Server/Client)情况,这里灵活设置
		ftpUtil.setDownfileNameDecodingParam2("UTF-8");
		// 控制编码的关键因素(编码不对，可能导致下载文件失败；可能导致ftpClient.listFiles(String pathname)获取不到指定文件)
		ftpUtil.setSendCommandStringEncoding("UTF-8");
		int result;
		try {
			// 执行下载
			result = ftpUtil.recursiveDownloadFile("/abc/",
					"C:/Users/JustryDeng/Desktop/download");
		} finally {
			// 释放资源
			ftpUtil.releaseResource();
		}
		System.out.println(" 成功下载文件个数为 -> " + result);
	}

	/**
	 * downloadFile下载 --- 测试
	 *
	 * @date 2018年9月26日 下午4:51:51
	 */
	@Test
	public void downloadTest() throws IOException {
		/// 基本配置
		FtpUtil ftpUtil = FtpUtil.getFtpUtilInstance(IP_ADDRESS, PORT, USERNAME, PASSWORD);
		// 注:根据不同的(Server/Client)情况,这里灵活设置;
		ftpUtil.setDownfileNameEncodingParam1("UTF-8");
		// 注:根据不同的(Server/Client)情况,这里灵活设置
		ftpUtil.setDownfileNameDecodingParam2("UTF-8");
		// 控制编码的关键因素(编码不对，可能导致下载文件失败；可能导致ftpClient.listFiles(String pathname)获取不到指定文件)
		ftpUtil.setSendCommandStringEncoding("UTF-8");
		try {
			// 执行下载
			ftpUtil.downloadFile("/abc/",
					"C:/Users/JustryDeng/Desktop/download");
		} finally {
			// 释放资源
			ftpUtil.releaseResource();
		}
	}

	/**
	 * uploadFile上传 --- 测试
	 *
	 * @date 2018年9月26日 下午4:51:51
	 */
	@Test
	public void uploadTest() throws IOException {
		/// 基本配置
		FtpUtil ftpUtil = FtpUtil.getFtpUtilInstance(IP_ADDRESS, PORT, USERNAME, PASSWORD);
		// 注:根据不同的(Server/Client)情况,这里灵活设置
		ftpUtil.setSendCommandStringEncoding("UTF-8");
		// 注:根据要上传下载的文件的情况,这里灵活设置
		ftpUtil.setTransportFileType(FTP.BINARY_FILE_TYPE);
		File file = new File("C:/Users/JustryDeng/Desktop/备忘录123.xml");
		boolean result;
		try {
		    //执行上传
			result = ftpUtil.uploadFile("/abc/a/", "a.html", file);
		} finally {
			// 释放资源
			ftpUtil.releaseResource();
		}
		System.out.println(" 上传结果 -> " + result);
	}

}
