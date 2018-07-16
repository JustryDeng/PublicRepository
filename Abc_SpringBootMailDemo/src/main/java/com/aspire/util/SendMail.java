package com.aspire.util;

import java.io.File;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendMail {

	/** 身份验证实体类模型工具 */
	public static MailAuthenticator authenticator;

	/**
	 * Message对象,用于封装要发送的邮件信息
	 */
	private MimeMessage message;

	/** 会话 */
	private Session session;

	/** Transport对象,用于执行邮件的发送任务 */
	private Transport transport;

	/**
	 * 参数信息 注:我们也可以创建.properties文件,然后将配置信息写到该文件中, 这样一来就不需要new Properties()对象了
	 */
	private Properties properties = new Properties();

	/** 要连接的邮件服务器(如:SMTP、IMAP、POP3等) */
	private String mailHost = null;

	/** 发送人邮箱 */
	private String sender_mail = null;

	/**
	 * 密码(或 授权码) 注:163邮箱使用的是授权码
	 */
	private String sender_password = null;
	
	/** 发送者的别名(注:收邮件会显示的 由  此名字发送给他的) */
	private String sender_alias = null;
			

	/**
	 * 初始化smtp发送邮件所需参数
	 *
	 * @return
	 * @date 2018年7月4日 下午7:40:14
	 */
	private boolean initSmtpParams() {
		// 发送者的邮箱
		sender_mail = "13548417409@163.com";

		// 密码(或 授权码) 163邮箱是授权码
		sender_password = "xxxxxx";//换成你自己的密码或授权码即可
		
		// 发送者的别名(注:收邮件会显示的 由  此名字发送给他的)
		sender_alias = "邓沙利文";

		// 要连接的SMTP服务器
		mailHost = "smtp.163.com";
		properties.put("mail.smtp.host", mailHost);

		// 是否开启身份验证
		properties.put("mail.smtp.auth", "true");
		
		// 发送邮件协议名称
		properties.put("mail.transport.protocol", "smtp");

		// 是否将纯文本连接升级为加密连接(TLS或SSL)
		// starttls主要针对于IMAP和POP3,本例使用过的是SMTP
		properties.put("mail.smtp.starttls.enable", "true");

		// 不做服务器证书校验
		properties.put("mail.smtp.ssl.checkserveridentity", "false");

		// 添加信任的服务器地址,多个地址之间用空格分开
		properties.put("mail.smtp.ssl.trust", mailHost);

		// 要连接的SMTP服务器的端口号(默认值为25)
		properties.put("mail.smtp.port", "25");

		// 指定 套接字工厂 要连接到的端口
		properties.put("mail.smtp.socketFactory.port", "25");

		// 如果设置为true,未能创建自己指定的套接字时将使用java.net.Socket创建的套接字类。默认为true
		properties.put("mail.smtp.socketFactory.fallback", "false");

		// 设置套接字连接超时值(单位毫秒) 默认不超时
		properties.put("mail.smtp.connectiontimeout", "10000");

		// Socket I/O超时值(单位毫秒) 缺省值不超时
		properties.put("mail.smtp.timeout", "10000");

		// 根据正好，授权码(或密码)进行身份验证
		authenticator = new MailAuthenticator(sender_mail, sender_password);

		// 获取session实体对象
		session = Session.getInstance(properties, authenticator);

		// 开启调试信息
		session.setDebug(true);

		// 获取message实体对象
		message = new MimeMessage(session);
		return true;
	}

	/**
	 * 供外界调用的发送邮件接口(里面初始化了参数 并 调用了真正发送邮件的方法)
	 */
	public boolean sendEmail(String title, String content, List<String> receivers, List<File> fileList) {
		try {
			// 初始化smtp发送邮件所需参数
			initSmtpParams();
			// 发送邮件
			doSendHtmlEmail(title, content, receivers, fileList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 发送邮件
	 */
	private boolean doSendHtmlEmail(String title, String htmlContent, List<String> receivers, List<File> fileList) {
		try {
			// 发件人邮箱、别名
			InternetAddress from = new InternetAddress(sender_mail,sender_alias);
			message.setFrom(from);

			// 收件人(多个)
			InternetAddress[] sendTo = new InternetAddress[receivers.size()];
			for (int i = 0; i < receivers.size(); i++) {
				sendTo[i] = new InternetAddress(receivers.get(i));
			}
			message.setRecipients(MimeMessage.RecipientType.TO, sendTo);

			// 邮件主题
			message.setSubject(title);

			// 添加邮件的各个部分内容,包括文本内容和附件
			Multipart multipart = new MimeMultipart();

			// 添加邮件正文
			BodyPart contentPart = new MimeBodyPart();
			contentPart.setContent(htmlContent, "text/html;charset=UTF-8");
			multipart.addBodyPart(contentPart);

			// 遍历添加附件
			if (fileList != null && fileList.size() > 0) {
				for (File file : fileList) {
					BodyPart attachmentBodyPart = new MimeBodyPart();
					DataSource source = new FileDataSource(file);
					attachmentBodyPart.setDataHandler(new DataHandler(source));
					attachmentBodyPart.setFileName(file.getName());
					multipart.addBodyPart(attachmentBodyPart);
				}
			}

			// 将multipart对象放到message中
			message.setContent(multipart);

			// 保存邮件
			message.saveChanges();

			// SMTP验证
			transport = session.getTransport("smtp");
			transport.connect(mailHost, sender_mail, sender_password);

			// 发送邮件
			transport.sendMessage(message, message.getAllRecipients());

			System.out.println(title + "发送成功!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (transport != null) {
				try {
					transport.close();
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}
}
