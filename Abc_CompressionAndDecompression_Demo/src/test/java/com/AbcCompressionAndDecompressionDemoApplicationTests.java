package com;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.aspire.util.CompressionAndDecompressionUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AbcCompressionAndDecompressionDemoApplicationTests {

	/**
	 * 主函数
	 */
	public static void main(String[] args) throws Exception {
        /* tar测试 */
		tarTest();
		/* gzip测试 */
        // gzipTest();
        /* zip测试 */
        // zipTest();
	}

	/**
	 * zip压缩、解压 -> 测试
	 *
	 * @DATE 2018年9月25日 下午1:10:27
	 */
	public static void zipTest() {
		// 压缩
		// 如果有文件与压缩后的文件同名,那么会覆盖之前的文件
		try {
			String targetFilePath0 = "C:\\Users\\JustryDeng\\Desktop\\鉴权参数.txt";
			String targetFilePath1 = "C:\\Users\\JustryDeng\\Desktop\\接口文档.docx";
			String targetFilePath2 = "C:\\Users\\JustryDeng\\Desktop\\积分.docx";
			String targetFilePath3 = "C:\\Users\\JustryDeng\\Desktop\\midnumber.xlsx";
			String[] filesPathArray = new String[4];
			filesPathArray[0] = targetFilePath0;
			filesPathArray[1] = targetFilePath1;
			filesPathArray[2] = targetFilePath2;
			filesPathArray[3] = targetFilePath3;
			String resultFilePath = "C:\\Users\\JustryDeng\\Desktop\\zip测试.zip";
			boolean result = CompressionAndDecompressionUtils.zipCompression(filesPathArray, resultFilePath);
			System.out.println(result == true ? "压缩成功！" : "压缩失败！");
		} catch (Exception e) {
			System.out.println("压缩失败！");
			e.printStackTrace();
		}

		// 解压
		// 如果有文件与解压后的文件同名,那么会覆盖之前的文件
		try {
			String decompressFilePath = "C:\\Users\\JustryDeng\\Desktop\\zip测试.zip";
			String resultDirPath = "C:\\Users\\JustryDeng\\Desktop";
			boolean result = CompressionAndDecompressionUtils.zipDecompression(decompressFilePath, resultDirPath);
			System.out.println(result == true ? "解压成功！" : "解压失败！");
		} catch (Exception e) {
			System.out.println("解压失败！");
			e.printStackTrace();
		}
	}

	/**
	 * gzip压缩(.tar 至 .tar.gz)、解压(.tar.gz 至 .tar) -> 测试
	 *
	 * @DATE 2018年9月25日 下午1:10:27
	 */
	public static void gzipTest() {
		// 压缩
		// 如果有文件与压缩后的文件同名,那么会覆盖之前的文件
		try {
			String targetFilePath = "C:\\Users\\JustryDeng\\Desktop\\打包后的tar文件.tar";
			String resultFilePath = "C:\\Users\\JustryDeng\\Desktop\\打包后的tar文件.tar.gz";
			boolean result = CompressionAndDecompressionUtils.gzipCompression(targetFilePath, resultFilePath);
			System.out.println(result == true ? "压缩成功！" : "压缩失败！");
		} catch (Exception e) {
			System.out.println("压缩失败！");
			e.printStackTrace();
		}

		// 解压
		// 如果有文件与解压后的文件同名,那么会覆盖之前的文件
		try {
			String decompressFilePath = "C:\\Users\\JustryDeng\\Desktop\\打包后的tar文件.tar.gz";
			String resultDirPath = "C:\\Users\\JustryDeng\\Desktop\\打包后的tar文件.tar";
			boolean result = CompressionAndDecompressionUtils.gzipDecompression(decompressFilePath, resultDirPath);
			System.out.println(result == true ? "解压成功！" : "解压失败！");
		} catch (Exception e) {
			System.out.println("解压失败！");
			e.printStackTrace();
		}
	}

	/**
	 * tar压缩、解压 -> 测试
	 *
	 * @DATE 2018年9月25日 下午1:10:27
	 */
	public static void tarTest() {
		// 压缩
		// 如果有文件与压缩后的文件同名,那么会覆盖之前的文件
		try {
			String targetFilePath0 = "C:\\Users\\JustryDeng\\Desktop\\鉴权参数.txt";
			String targetFilePath1 = "C:\\Users\\JustryDeng\\Desktop\\接口文档.docx";
			String targetFilePath2 = "C:\\Users\\JustryDeng\\Desktop\\积分.docx";
			String targetFilePath3 = "C:\\Users\\JustryDeng\\Desktop\\midnumber.xlsx";
			String[] filesPathArray = new String[4];
			filesPathArray[0] = targetFilePath0;
			filesPathArray[1] = targetFilePath1;
			filesPathArray[2] = targetFilePath2;
			filesPathArray[3] = targetFilePath3;
			String resultFilePath = "C:\\Users\\JustryDeng\\Desktop\\打包后的tar文件.tar";
			boolean result = CompressionAndDecompressionUtils.tarCompression(filesPathArray, resultFilePath);
			System.out.println(result == true ? "压缩成功！" : "压缩失败！");
		} catch (Exception e) {
			System.out.println("压缩失败！");
			e.printStackTrace();
		}

		// 解压
		// 如果有文件与解压后的文件同名,那么会覆盖之前的文件
		try {
			String decompressFilePath = "C:\\Users\\JustryDeng\\Desktop\\打包后的tar文件.tar";
			String resultDirPath = "C:\\Users\\JustryDeng\\Desktop";
			boolean result = CompressionAndDecompressionUtils.tarDecompression(decompressFilePath, resultDirPath);
			System.out.println(result == true ? "解压成功！" : "解压失败！");
		} catch (Exception e) {
			System.out.println("解压失败！");
			e.printStackTrace();
		}
	}

}
