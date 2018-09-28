package com;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AbcFtpUploadAndDownloadApplicationTests {

//	/**
//	 * deleteBlankDirOrFile删除 --- 测试
//	 * 
//	 * @DATE 2018年9月27日 上午10:12:56
//	 */
//	public static void main(String[] args) throws IOException {
//		/// 基本配置
//		FTPUtils ftpUtils = FTPUtils.getFTPUtilsInstance("10.2.6.16", 22, "miduser", "mid*2018");
//		// 注:根据不同的(Server/Client)情况,这里灵活设置
//		//    编码不对可能导致文件删除失败
//		ftpUtils.setSendCommandStringEncoding("GBK");
//		
//		String remoteDir = new String("/test/");
//		// 执行删除
//		ftpUtils.deleteBlankDirOrFile(remoteDir);
//	}
	
//	/**
//	 * recursiveDeleteBlankDirOrFile(即:deleteBlankDirOrFile升级版)删除 --- 测试
//	 * 
//	 * @DATE 2018年9月26日 下午4:51:51
//	 */
//	public static void main(String[] args) throws IOException {
//		/// 基本配置
//		FTPUtils ftpUtils = FTPUtils.getFTPUtilsInstance("10.2.6.16", 22, "miduser", "mid*2018");
//		// 注:根据不同的(Server/Client)情况,这里灵活设置
//		//    编码不对可能导致文件删除失败
//		ftpUtils.setSendCommandStringEncoding("GBK");
//		
//		String remoteDir = new String("/test");
//		// 执行删除
//		ftpUtils.recursiveDeleteBlankDirOrFile(remoteDir);
//	}
	
//	/**
//	 * downloadFile下载 --- 测试
//	 * 
//	 * @DATE 2018年9月26日 下午4:51:51
//	 */
//	public static void main(String[] args) throws IOException {
//		/// 基本配置
//		FTPUtils ftpUtils = FTPUtils.getFTPUtilsInstance("10.2.6.16", 22, "miduser", "mid*2018");
//		// 注:根据不同的(Server/Client)情况,这里灵活设置;
//		ftpUtils.setSendCommandStringEncoding("ISO-8859-1");
//		// 注:根据不同的(Server/Client)情况,这里灵活设置
//		ftpUtils.setDownfileNameEncodingParam1("ISO-8859-1");
//		ftpUtils.setDownfileNameDecodingParam2("GBK");
//		
//		String remoteDir = new String("/test/a/SQL语句.txt");
//		String localDir = new String("C:/Users/JustryDeng/Desktop/木头人D");
//		// 下载
//		ftpUtils.downloadFile(remoteDir,localDir);
//	}
	
//	/**
//	 * recursiveDownloadFile(即:downloadFile升级版)下载 --- 测试
//	 * 
//	 * @DATE 2018年9月26日 下午4:51:51
//	 */
//	public static void main(String[] args) throws IOException {
//		/// 基本配置
//		FTPUtils ftpUtils = FTPUtils.getFTPUtilsInstance("10.2.6.16", 22, "miduser", "mid*2018");
//		// 注:根据不同的(Server/Client)情况,这里灵活设置
//		ftpUtils.setSendCommandStringEncoding("ISO-8859-1");
//		// 注:根据不同的(Server/Client)情况,这里灵活设置
//		ftpUtils.setDownfileNameEncodingParam1("ISO-8859-1");
//		ftpUtils.setDownfileNameDecodingParam2("GBK");
//		
//		String remoteDir = new String("/test");
//		String localDir = new String("C:/Users/JustryDeng/Desktop/Justry帅123");
//		// 执行下载
//		ftpUtils.recursiveDownloadFile(remoteDir, localDir);
//	}

	
//	/**
//	 * uploadFile上传 --- 测试
//	 * 
//	 * @DATE 2018年9月26日 下午4:51:51
//	 */
//	public static void main(String[] args) throws IOException {
//		/// 基本配置
//		FTPUtils ftpUtils = FTPUtils.getFTPUtilsInstance("10.2.6.16", 22, "miduser", "mid*2018");
//		// 上传文件中有中文,这里设置为GBK
//		// 注:根据不同的(Server/Client)情况,这里灵活设置
//		ftpUtils.setSendCommandStringEncoding("GBK");
//		// 注:根据要上传下载的文件的情况,这里灵活设置
//		ftpUtils.setTransportFileType(FTP.BINARY_FILE_TYPE);
//		
//		String s = new String("C:/Users/JustryDeng/Desktop/SQL语句.txt");
//		File file = new File(s);
//		//执行上传
//		ftpUtils.uploadFile("/test/b", "SQL语句.txt", file);
//	}

}
