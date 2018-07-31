package com.aspire.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Test {
	/**
	 * 测试一下
	 *
	 * @param args
	 * @date 2018年7月4日 下午8:17:11
	 */
	public static void main(String[] args) {
		// 邮件主题
		String title = "java代码发送mail";

		// 邮件正文
		String htmlContent = "<span style='color:red;'>我是一只小小鸟，咿呀咿呀哟~！</span>";

		// 收件人
		List<String> receivers = new ArrayList<String>();
		receivers.add("1612513157@qq.com");
		
		// 抄送人
		List<String> ccReceivers = new ArrayList<String>();
		ccReceivers.add("dengshuai@aspirecn.com");
		ccReceivers.add("m1asdasd@sina.com");
		
				
		// 密送人
		List<String> bccReceivers = new ArrayList<String>();
		bccReceivers.add("m1asdasd@sina.com");
		bccReceivers.add("dengshuai@aspirecn.com");
		

		// 附件
		String fileName1 = "C:\\Users\\JustryDeng\\Desktop\\file\\Eclipse插件编写54218.docx";
		File file1 = new File(fileName1);
		String fileName2 = "C:\\Users\\JustryDeng\\Desktop\\file\\hosts21441";
		File file2 = new File(fileName2);
		List<File> fileList = new ArrayList<File>();
		fileList.add(file1);
		fileList.add(file2);
		// 执行发送(带附件)
		// new SendMail().sendEmail(title, htmlContent, receivers, ccReceivers, bccReceivers, fileList);
		// 执行发送(无附件)
		new SendMail().sendEmail(title, htmlContent, receivers, ccReceivers, bccReceivers, null);
	}
}
