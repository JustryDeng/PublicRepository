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
		String htmlContent = "我是一只小小鸟，咿呀咿呀哟~！";

		// 收件人
		List<String> receivers = new ArrayList<String>();
		receivers.add("1612513157@qq.com");

		// 附件
		String fileName1 = "C:\\Users\\dengshuai.ASPIRE\\Desktop\\file\\Eclipse插件编写54218.docx";
		File file1 = new File(fileName1);
		String fileName2 = "C:\\Users\\dengshuai.ASPIRE\\Desktop\\file\\hosts21441";
		File file2 = new File(fileName2);
		List<File> fileList = new ArrayList<File>();
		fileList.add(file1);
		fileList.add(file2);
		// 执行发送(带附件)
		// new SendMail().sendEmail(title, htmlContent, receivers, fileList);
		// 执行发送(无附件)
		new SendMail().sendEmail(title, htmlContent, receivers, null);
	}
}
