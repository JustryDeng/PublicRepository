package com.aspire.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
public class UpFileController {

	/**
	 * 单文件上传(简单demo)
	 *
	 * @param file
	 *            接收到的文件
	 * @date 2018年6月29日 上午10:56:05
	 */
	@RequestMapping(value = "/file/upload", method = RequestMethod.POST)
	public void fileUpload(@RequestParam("fileName") MultipartFile file) {
		// 先设定一个放置上传文件的文件夹(该文件夹可以不存在，下面会判断创建)
		String deposeFilesDir = "C:\\Users\\dengshuai.ASPIRE\\Desktop\\file\\";
		// 判断文件手否有内容
		if (file.isEmpty()) {
			System.out.println("该文件无任何内容!!!");
		}
		// 获取附件原名(有的浏览器如chrome获取到的是最基本的含 后缀的文件名,如myImage.png)
		// 获取附件原名(有的浏览器如ie获取到的是含整个路径的含后缀的文件名，如C:\\Users\\images\\myImage.png)
		String fileName = file.getOriginalFilename();
		// 如果是获取的含有路径的文件名，那么截取掉多余的，只剩下文件名和后缀名
		int index = fileName.lastIndexOf("\\");
		if (index > 0) {
			fileName = fileName.substring(index + 1);
		}
		// 判断单个文件大于1M
		long fileSize = file.getSize();
		if (fileSize > 1024 * 1024) {
			System.out.println("文件大小为(单位字节):" + fileSize);
			System.out.println("该文件大于1M");
		}
		// 当文件有后缀名时
		if (fileName.indexOf(".") >= 0) {
			// split()中放正则表达式; 转义字符"\\."代表 "."
			String[] fileNameSplitArray = fileName.split("\\.");
			// 加上random戳,防止附件重名覆盖原文件
			fileName = fileNameSplitArray[0] + (int) (Math.random() * 100000) + "." + fileNameSplitArray[1];
		}
		// 当文件无后缀名时(如C盘下的hosts文件就没有后缀名)
		if (fileName.indexOf(".") < 0) {
			// 加上random戳,防止附件重名覆盖原文件
			fileName = fileName + (int) (Math.random() * 100000);
		}
		System.out.println("fileName:" + fileName);

		// 根据文件的全路径名字(含路径、后缀),new一个File对象dest
		File dest = new File(deposeFilesDir + fileName);
		// 如果该文件的上级文件夹不存在，则创建该文件的上级文件夹和其祖辈级文件夹;
		if (!dest.getParentFile().exists()) {
			dest.getParentFile().mkdirs();
		}
		try {
			// 将获取到的附件file,transferTo写入到指定的位置(即:创建dest时，指定的路径)
			file.transferTo(dest);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("文件的全路径名字(含路径、后缀)>>>>>>>" + deposeFilesDir + fileName);
	}

	/**
	 * 多文件上传(简单demo)
	 *
	 * @param files
	 *            文件数组
	 * @throws IOException
	 * @date 2018年6月29日 下午12:57:01
	 */
	@RequestMapping(value = "/file/mulFileUpload", method = RequestMethod.POST)
	public void mulFileUpload(@RequestParam("fileName") MultipartFile[] files) {
		// 先设定一个放置上传文件的文件夹(该文件夹可以不存在，下面会判断创建)
		String deposeFilesDir = "C:\\Users\\dengshuai.ASPIRE\\Desktop\\file\\";

		for (MultipartFile file : files) {

			if (file.isEmpty()) {
				System.out.println("未上传任何附件!!!");
			}
			// 获取附件原名(有的浏览器如chrome获取到的是最基本的含 后缀的文件名,如myImage.png)
			// 获取附件原名(有的浏览器如ie获取到的是含整个路径的含后缀的文件名，如C:\\Users\\images\\myImage.png)
			String fileName = file.getOriginalFilename();
			// 如果是获取的含有路径的文件名，那么截取掉多余的，只剩下文件名和后缀名
			if (fileName.indexOf("\\") > 0) {
				int index = fileName.lastIndexOf("\\");
				fileName = fileName.substring(index + 1);
			}
			// 判断单个文件大于1M
			long fileSize = file.getSize();
			if (fileSize > 1024 * 1024) {
				System.out.println("文件大小为(单位字节):" + fileSize);
				System.out.println("该文件大于1M");
			}
			// 当文件有后缀名时
			if (fileName.indexOf(".") >= 0) {
				// split()中放正则表达式; 转义字符"\\."代表 "."
				String[] fileNameSplitArray = fileName.split("\\.");
				// 加上random戳,防止附件重名覆盖原文件
				fileName = fileNameSplitArray[0] + (int) (Math.random() * 100000) + "." + fileNameSplitArray[1];
			}
			// 当文件无后缀名时(如C盘下的hosts文件就没有后缀名)
			if (fileName.indexOf(".") < 0) {
				// 加上random戳,防止附件重名覆盖原文件
				fileName = fileName + (int) (Math.random() * 100000);
			}
			System.out.println("fileName:" + fileName);
			// 根据文件的全路径名字(含路径、后缀),new一个File对象dest
			File dest = new File(deposeFilesDir + fileName);
			// 如果pathAll路径不存在，则创建相关该路径涉及的文件夹;
			if (!dest.getParentFile().exists()) {
				dest.getParentFile().mkdirs();
			}
			try {
				// 将获取到的附件file,transferTo写入到指定的位置(即:创建dest时，指定的路径)
				file.transferTo(dest);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("文件的全路径名字(含路径、后缀)>>>>>>>" + deposeFilesDir + fileName);
		}
	}

}
