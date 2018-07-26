package com.aspire.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常工具类
 *
 * @author JustryDeng
 * @date 2018年7月27日 上午1:32:44
 */
public class ExceptionUtil {

	/**
	 * 将异常堆栈 信息 转换为字符串
	 *
	 * @param e
	 *            异常
	 * @return 该异常的错误堆栈信息
	 * @date 2018年7月27日 上午1:35:27
	 */
	public static String getStackTraceMessage(Exception e) {
		StringWriter sw = null;
		PrintWriter pw = null;
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			// 将异常的的堆栈信息输出到printWriter中
			e.printStackTrace(pw);
			pw.flush();
			sw.flush();
		} finally {
			if (sw != null) {
				try {
					sw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (pw != null) {
				pw.close();
			}
		}
		return sw.toString();
	}
}
