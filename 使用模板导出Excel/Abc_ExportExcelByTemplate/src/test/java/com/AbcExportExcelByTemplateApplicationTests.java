package com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.aspire.model.User;
import com.aspire.util.ExcelExportUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AbcExportExcelByTemplateApplicationTests {

	@Test
	public void contextLoads() {
	}

	// public static void main(String[] args) {
	//
	// // xls模板全限定名
	// String templateFileName =
	// "C:\\Users\\dengshuai.ASPIRE\\Desktop\\template.xls";
	// // 生成的xls全限定名
	// String destFileName =
	// "C:\\Users\\dengshuai.ASPIRE\\Desktop\\屌丝基本信息Object.xls";
	//
	// List<User> list = new ArrayList<User>();
	//
	// User u1 = new User("u1", 11, "男", "我是u1~");
	// User u2 = new User("u2", 12, "男", "我是u2~");
	// User u3 = new User("u3", 13, "男", "我是u3~");
	// User u4 = new User("u4", 14, "男", "我是u4~");
	// list.add(u1);
	// list.add(u2);
	// list.add(u3);
	// list.add(u4);
	// // 调用excel工具,生成excel
	// new ExcelExportUtil().createExcel(templateFileName, list, list,
	// destFileName);
	// }

	public static void main(String[] args) {
		// xls模板全限定名
		String templateFileName = "C:\\Users\\dengshuai.ASPIRE\\Desktop\\template.xls";
		// 生成的xls全限定名
		String destFileName = "C:\\Users\\dengshuai.ASPIRE\\Desktop\\屌丝基本信息Map.xls";

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("name", "u1");
		map1.put("age", 11);
		map1.put("gender", "男");
		map1.put("motto", "我是u1~");

		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("name", "u2");
		map2.put("age", 12);
		map2.put("gender", "男");
		map2.put("motto", "我是u2~");

		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("name", "u3");
		map3.put("age", 13);
		map3.put("gender", "男");
		map3.put("motto", "我是u3~");

		Map<String, Object> map4 = new HashMap<String, Object>();
		map4.put("name", "u4");
		map4.put("age", 14);
		map4.put("gender", "男");
		map4.put("motto", "我是u4~");

		list.add(map1);
		list.add(map2);
		list.add(map3);
		list.add(map4);
		// 调用工具
		new ExcelExportUtil().createExcel(templateFileName, list, list, destFileName);
	}
}
