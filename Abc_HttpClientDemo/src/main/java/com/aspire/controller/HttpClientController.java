package com.aspire.controller;

import com.aspire.model.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * HttpClient测试
 *
 * @author JustryDeng
 * @date 2018年7月13日 下午4:05:57
 */
@RestController
public class HttpClientController {

	/**
	 * GET无参
	 *
	 * @return 测试数据
	 * @date 2018年7月13日 下午5:29:44
	 */
	@RequestMapping("/doGetControllerOne")
	public String doGetControllerOne() {
		return "123";
	}

	/**
	 * GET有参
	 *
	 * @param name
	 *            姓名
	 * @param age
	 *            年龄
	 * @return 测试数据
	 * @date 2018年7月13日 下午5:29:47
	 */
	@RequestMapping("/doGetControllerTwo")
	public String doGetControllerTwo(String name, Integer age) {
		return "没想到[" + name + "]都" + age + "岁了!";
	}

	/**
	 * POST无参
	 *
	 * @return 测试数据
	 * @date 2018年7月13日 下午5:29:49
	 */
	@RequestMapping(value = "/doPostControllerOne", method = RequestMethod.POST)
	public String doPostControllerOne() {
		return "这个post请求没有任何参数!";
	}

	/**
	 * POST有参(对象参数)
	 *
	 * @return 测试数据
	 * @date 2018年7月13日 下午5:29:52
	 */
	@RequestMapping(value = "/doPostControllerTwo", method = RequestMethod.POST)
	public String doPostControllerTwo(@RequestBody User user) {
		return user.toString();
	}

	/**
	 * POST有参(普通参数 + 对象参数)
	 *
	 * @return 测试数据
	 * @date 2018年7月13日 下午5:29:52
	 */
	@RequestMapping(value = "/doPostControllerThree", method = RequestMethod.POST)
	public String doPostControllerThree(@RequestBody User user,Integer flag, String meaning) {
		return user.toString() + "\n" + flag + ">>>" + meaning;
	}
	
	/**
	 * POST有参(普通参数)
	 *
	 * @return 测试数据
	 * @date 2018年7月14日 上午10:54:29
	 */
	@RequestMapping(value = "/doPostControllerFour", method = RequestMethod.POST)
	public String doPostControllerThree1(String name, Integer age) {
		return "[" + name + "]居然才[" + age + "]岁!!!";
	}

	/**
	 * application/x-www-form-urlencoded 请求测试
	 *
	 * @date 2019/9/19 9:59
	 */
	@PostMapping(value = "/form/data")
	public String formDataControllerTest(@RequestParam("name") String name,
									  @RequestParam("age") Integer age) {
		return "application/x-www-form-urlencoded请求成功，name值为【" + name + "】，age值为【" + age + "】";
	}

	/**
	 * httpclient传文件测试
	 *
	 * 注: 即multipart/form-data测试。
	 * 注:多文件的话，可以使用数组MultipartFile[]或集合List<MultipartFile>来接收
	 * 注:单文件的话，可以直接使用MultipartFile来接收
	 *
	 * @date 2019/9/19 9:59
	 */
	@PostMapping(value = "/file")
	public String fileControllerTest(
			                         @RequestParam("name") String name,
									 @RequestParam("age") Integer age,
									 @RequestParam("files") List<MultipartFile> multipartFiles)
			                          throws UnsupportedEncodingException {

		StringBuilder sb = new StringBuilder(64);
		// 防止中文乱码
		sb.append("\n");
		sb.append("name=").append(name)
		  .append("\tage=").append(age);
		String fileName;
		for (MultipartFile file : multipartFiles) {
			sb.append("\n文件信息:\n");
			fileName = file.getOriginalFilename();
			if (fileName == null) {
				continue;
			}
			// 防止中文乱码
			// 在传文件时，将文件名URLEncode，然后在这里获取文件名时，URLDecode。就能避免乱码问题。
			fileName = URLDecoder.decode(fileName, "utf-8");
			sb.append("\t文件名: ").append(fileName);
			sb.append("\t文件大小: ").append(file.getSize() * 1.0 / 1024).append("KB");
			sb.append("\tContentType: ").append(file.getContentType());
			sb.append("\n");
		}
		return  sb.toString();
	}

	/**
	 * httpclient传流测试
	 *
	 * @date 2019/9/19 9:59
	 */
	@PostMapping(value = "/is")
	public String fileControllerTest(@RequestParam("name") String name, InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder(64);
		sb.append("\nname值为: ").append(name);
		sb.append("\n输入流数据内容为: ");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			sb.append(line);
		}
		return  sb.toString();
	}
}
