package com.aspire.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

/**
 * 压缩、解压 -> 工具类
 * 更多示例,可见官网:http://commons.apache.org/proper/commons-compress/examples.html
 * 总体说明:如果有同名的文件,那么压缩/解压后的文件将会覆盖原来的文件
 *
 * @author JustryDeng
 * @DATE 2018年9月25日 下午12:26:47
 */
public class CompressionAndDecompressionUtils {

	/**
	 * tar打包压缩
	 *
	 * @param filesPathArray
	 *            要压缩的文件的全路径(数组)
	 * @param resultFilePath
	 *            压缩后的文件全文件名(.tar)
	 * @throws Exception
	 * @DATE 2018年9月25日 下午12:39:28
	 */
	public static boolean tarCompression(String[] filesPathArray, String resultFilePath) throws Exception {
		System.out.println(" tarCompression -> Compression start!");
		FileOutputStream fos = null;
		TarArchiveOutputStream taos = null;
		try {
			fos = new FileOutputStream(new File(resultFilePath));
			taos = new TarArchiveOutputStream(fos);
			for (String filePath : filesPathArray) {
				BufferedInputStream bis = null;
				FileInputStream fis = null;
				try {
					File file = new File(filePath);
					TarArchiveEntry tae = new TarArchiveEntry(file);
					// 此处指明 每一个被压缩文件的名字,以便于解压时TarArchiveEntry的getName()方法获取到的直接就是这里指定的文件名
					// 以(左边的)GBK编码将file.getName()“打碎”为序列,再“组装”序列为(右边的)GBK编码的字符串
					tae.setName(new String(file.getName().getBytes("GBK"), "GBK"));
					taos.putArchiveEntry(tae);
					fis = new FileInputStream(file);
					bis = new BufferedInputStream(fis);
					int count;
					byte data[] = new byte[1024];
					while ((count = bis.read(data, 0, 1024)) != -1) {
						taos.write(data, 0, count);
					}
				} finally {
					taos.closeArchiveEntry();
					if (bis != null) 
					    bis.close();
					if (fis != null) 
					    fis.close();
				}
			} 
		} finally {
			if (taos != null) 
			    taos.close();
			if (fos != null) 
			    fos.close();
			
		}
		System.out.println(" tarCompression -> Compression end!");
		return true;
	}

	/**
	 * tar拆包解压
	 *
	 * @param decompressFilePath
	 *            要被解压的压缩文件 全路径
	 * @param resultDirPath
	 *            解压文件存放绝对路径(目录)
	 * @throws Exception
	 * @DATE 2018年9月25日 下午12:39:43
	 */
	public static boolean tarDecompression(String decompressFilePath, String resultDirPath) throws Exception {
		System.out.println(" tarDecompression -> Decompression start!");
		TarArchiveInputStream tais = null;
		FileInputStream fis = null;
		try {
			File file = new File(decompressFilePath);
			fis = new FileInputStream(file);
			tais = new TarArchiveInputStream(fis);
			TarArchiveEntry tae = null;
			while ((tae = tais.getNextTarEntry()) != null) {
				BufferedOutputStream bos = null;
				FileOutputStream fos = null;
				try {
					System.out.println("  already decompression file -> " + tae.getName());
					String dir = resultDirPath + File.separator + tae.getName();// tar档中文件
					File dirFile = new File(dir);
					fos = new FileOutputStream(dirFile);
					bos = new BufferedOutputStream(fos);
					int count;
					byte[] data = new byte[1024];
					while ((count = tais.read(data, 0, 1024)) != -1) {
						bos.write(data, 0, count);
					}
				} finally {
					if (bos != null) 
					    bos.close();
					if (fos != null) 
					    fos.close();
				}
			} 
		} finally {
			if (tais != null)
			    tais.close();
			if (fis != null) 
			    fis.close();
		}
		System.out.println(" tarDecompression -> Decompression end!");
		return true;
	}

	/**
	 * 对.tar文件进行gzip压缩
	 * 说明:我们一般先把多个文件tar打包为一个,然后再使用gzip进行压缩; 进而获得形如“abc.tar.gz”这样的压缩文件
	 *  注:这里暂时不再深入学习,以后有闲暇时间可深入了解如何压缩多个文件等
	 *  注:如果明确知道解压后的是什么类型的文件;那么可以直接指定解压后的文件类型(实际上也需要这么做);
	 *     .tar.gz 解压后就是.tar文件,所以我们在解压时,给出的解压后的文件的全路径名就是以.tar结尾的
	 * @param filePath
	 *            要被压缩的压缩文件 全路径
	 * @param resultFilePath
	 *            压缩后的文件(全文件名 .gz)
	 * @throws IOException
	 * @DATE 2018年9月25日 下午2:50:22
	 */
	public static boolean gzipCompression(String filePath, String resultFilePath) throws IOException {
		System.out.println(" gzipCompression -> Compression start!");
		InputStream fin = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos= null;
		GzipCompressorOutputStream gcos = null;
		try {
			fin = Files.newInputStream(Paths.get(filePath));
			bis = new BufferedInputStream(fin);
			fos = new FileOutputStream(resultFilePath);
			bos = new BufferedOutputStream(fos);
			gcos = new GzipCompressorOutputStream(bos);
			byte[] buffer = new byte[1024];
			int read = -1;
			while ((read = bis.read(buffer)) != -1) {
				gcos.write(buffer, 0, read);
			}
		} finally {
			if(gcos != null)
			    gcos.close();
			if(bos != null)
			    bos.close();
			if(fos != null)
				fos.close();
			if(bis != null)
			    bis.close();
			if(fin != null)
			    fin.close();
		}
		System.out.println(" gzipCompression -> Compression end!");
		return true;
	}

	/**
	 * 解压对.tar.gz文件至 .tar文件
	 * 说明:我们一般都是对.tar.gz文件进行gzip解压; 进而获得形如.tar文件;再进行解压
	 *   注:这里暂时不再深入学习,以后有闲暇时间可深入了解学习
	 * 
	 * @param <GzipArchiveEntry>
	 *
	 * @param compressedFilePath
	 *            要被解压的压缩文件 全路径
	 * @param resultDirPath
	 *            解压文件存放绝对路径(目录)
	 * @throws IOException
	 * @DATE 2018年9月25日 下午4:35:09
	 */
	public static  boolean gzipDecompression(String compressedFilePath, String resultDirPath) throws IOException {
		System.out.println(" gzipDecompression -> Compression start!");
		InputStream fin = null;
		BufferedInputStream in = null;
		OutputStream out = null;
		GzipCompressorInputStream gcis = null;
		try {
			out = Files.newOutputStream(Paths.get(resultDirPath));
			fin = Files.newInputStream(Paths.get(compressedFilePath));
			in = new BufferedInputStream(fin);
			gcis = new GzipCompressorInputStream(in);
			final byte[] buffer = new byte[1024];
			int n = 0;
			while (-1 != (n = gcis.read(buffer))) {
				out.write(buffer, 0, n);
			} 
		} finally {
			if(gcis != null)
			    gcis.close();
			if(in != null)
			    in.close();
			if(fin != null)
			    fin.close();
			if(out != null)
				out.close();
		}
		System.out.println(" gzipDecompression -> Compression end!");
		return true;
	}
	
	
	/**
	 * zip压缩(注:与tar类似)
	 *
	 * @param filesPathArray
	 *            要压缩的文件的全路径(数组)
	 * @param resultFilePath
	 *            压缩后的文件全文件名(.tar)
	 * @throws Exception
	 * @DATE 2018年9月25日 下午17:55:28
	 */
	public static boolean zipCompression(String[] filesPathArray, String resultFilePath) throws Exception {
		System.out.println(" zipCompression -> Compression start!");
		ZipArchiveOutputStream zaos = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(resultFilePath));
			zaos = new ZipArchiveOutputStream(fos);
			for (String filePath : filesPathArray) {
				FileInputStream fis = null;
				BufferedInputStream bis = null;
				try {
					File file = new File(filePath);
					// 第二个参数如果是文件全路径名,那么压缩时也会将路径文件夹也缩进去;
					// 我们之压缩目标文件,而不压缩该文件所处位置的相关文件夹,所以这里我们用file.getName()
					ZipArchiveEntry zae = new ZipArchiveEntry(file, file.getName());
					zaos.putArchiveEntry(zae);
					fis = new FileInputStream(file);
					bis = new BufferedInputStream(fis);
					int count;
					byte data[] = new byte[1024];
					while ((count = bis.read(data, 0, 1024)) != -1) {
						zaos.write(data, 0, count);
					}
				} finally {
					zaos.closeArchiveEntry();
					if (bis != null)
					    bis.close();
					if (fis != null)
					    fis.close();
				}

			} 
		} finally {
			if (zaos != null)
			    zaos.close();
			if (fos != null)
			    fos.close();
		}
		System.out.println(" zipCompression -> Compression end!");
		return true;
	}

	/**
	 * zip解压(注:与tar类似)
	 *
	 * @param decompressFilePath
	 *            要被解压的压缩文件 全路径
	 * @param resultDirPath
	 *            解压文件存放绝对路径(目录)
	 * @throws Exception
	 * @DATE 2018年9月25日 下午18:39:43
	 */
	public static boolean zipDecompression(String decompressFilePath, String resultDirPath) throws Exception {
		System.out.println(" zipDecompression -> Decompression start!");
		ZipArchiveInputStream zais = null;
		FileInputStream fis = null;
		try {
			File file = new File(decompressFilePath);
			fis = new FileInputStream(file);
			zais = new ZipArchiveInputStream(fis);
			ZipArchiveEntry zae = null;
			while ((zae = zais.getNextZipEntry()) != null) {
				FileOutputStream fos = null;
				BufferedOutputStream bos = null;
				try {
					System.out.println("  already decompression file -> " + zae.getName());
					String dir = resultDirPath + File.separator + zae.getName();// tar档中文件
					File dirFile = new File(dir);
					fos = new FileOutputStream(dirFile);
					bos = new BufferedOutputStream(fos);
					int count;
					byte data[] = new byte[1024];
					while ((count = zais.read(data, 0, 1024)) != -1) {
						bos.write(data, 0, count);
					}
				} finally {
					if (bos != null) 
						bos.close();
					if (fos != null)
						fos.close();
				}
			} 
		} finally {
			if (zais != null)
			    zais.close();
			if (fis != null)
				fis.close();
		}
		System.out.println(" zipDecompression -> Decompression end!");
		return true;
	}

}
