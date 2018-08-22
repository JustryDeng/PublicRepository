package com.aspire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.SpringBootForJavaAnnotationApplication;
import com.aspire.mapper.JavaAnnotationMapper;
import com.aspire.model.Employee;
import com.aspire.model.People;

/**
 * JUnit测试
 * 
 * @author JustryDeng
 * @date 2018年6月13日下午4:16:03
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBootForJavaAnnotationApplication.class)
// @Transactional // 开启事物回滚
public class SpringBootForJavaAnnotationApplicationTests {

	/**
	 * 自动装配JavaAnnotationMapper
	 */
	@Autowired
	private JavaAnnotationMapper javaAnnotationMapper;

	/**
	 * 增------简单增(JUnit测试)
	 *
	 * @date 2018年6月14日 上午9:57:18
	 */
	@Test
	public void singleInsertTest() {
		Employee e = new Employee("王麻子", 24, "男");
		int result = javaAnnotationMapper.singleInsert(e);
		System.out.println(result);
		System.out.println(e.getId());
	}

	/**
	 * 增------同时获取增加后的主键(JUnit测试)
	 *
	 * @date 2018年6月14日 下午1:28:31
	 */
	@Test
	public void singleInsertAutoGetKey() {
		Employee e = new Employee("瓜田", 51, "男");
		int result = javaAnnotationMapper.singleInsertAutoGetKey(e);
		System.out.println(result);
		System.out.println(e.getId());
	}

	/**
	 * 增------参数既有对象,又有普通参数(JUnit测试)
	 *
	 * @date 2018年7月10日 下午6:16:34
	 */
	@Test
	public void singleInsertMultiParam() {
		Employee e = new Employee("瓜田", 51, "男");
		int result = javaAnnotationMapper.singleInsertMultiParam(e, "李下", 15, "男");
		System.out.println(result);
		System.out.println(e.getId());
	}

	/**
	 * 删------简单删(JUnit测试)
	 *
	 * @date 2018年6月14日 下午1:29:16
	 */
	@Test
	public void singleDelete() {
		int result = javaAnnotationMapper.singleDelete(5);
		System.out.println(result);
	}

	/**
	 * 改------简单改(JUnit测试)
	 *
	 * @date 2018年6月14日 下午1:40:28
	 */
	@Test
	public void singleUpdate() {
		int result = javaAnnotationMapper.singleUpdate("变", 2);
		System.out.println(result);
	}

	/**
	 * 查------以 对象模型 接收数据(JUnit测试)
	 *
	 * @date 2018年6月14日 下午1:59:07
	 */
	@Test
	public void singleSelectAcceptDataByObjectTest() {
		Employee e = javaAnnotationMapper.singleSelectAcceptDataByObject("小丁");
		System.out.println(e.getId() + "," + e.getAge());
	}

	/**
	 * 查------以 Map<String, Object> 接收数据(JUnit测试)
	 *
	 * @date 2018年6月14日 下午1:59:07
	 */
	@Test
	public void singleSelectAcceptDataByMapTest() {
		Map<String, Object> map = new HashMap<>();
		map = javaAnnotationMapper.singleSelectAcceptDataByMap("小丁");
		System.out.println(map.get("age"));
	}

	/**
	 * 查------以 一般数据类型 接收数据(JUnit测试)
	 *
	 * @date 2018年6月14日 下午4:09:13
	 */
	@Test
	public void singleSelectAcceptDataByStringTest() {
		Integer id = javaAnnotationMapper.singleSelectAcceptDataByString("亨得帅");
		System.out.println(id);
	}

	/**
	 * 查------以 List 接收多个实体模型数据(JUnit测试)
	 *
	 * @date 2018年6月14日 下午4:33:50
	 */
	@Test
	public void singleSelectAcceptDataByListTest() {
		List<Employee> list = javaAnnotationMapper.singleSelectAcceptDataByList(5);
		System.out.println("集合长度:" + list.size());
		for (Employee em : list) {
			System.out.println(em.toString());
			System.out.println("---------------------");
		}
	}

	/**
	 * 增------单参数动态增(JUnit测试)
	 *
	 * @date 2018年6月15日 上午11:58:50
	 */
	@Test
	public void dynamicInsertTest() {
		Employee em = new Employee("xx", 77, null);
		int result = javaAnnotationMapper.dynamicInsert(em);
		System.out.println(result);
		System.out.println(em.getId());
	}

	/**
	 * 增------多参数动态增(JUnit测试)
	 *
	 * @date 2018年6月15日 下午1:46:20
	 */
	@Test
	public void dynamicInsertMultiParamTest() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "xx");
		map.put("age", 8);
		map.put("gender", null);
		int result = javaAnnotationMapper.dynamicInsertMultiParam(map);
		System.out.println(result);
	}

	/**
	 * 增------动态增,同时获得主键(JUnit测试)
	 *
	 * @date 2018年6月15日 下午13:54:50
	 */
	@Test
	public void dynamicInsertMeanwhileGetKeyTest() {
		Employee em = new Employee("小小", 2, "女");
		int result = javaAnnotationMapper.dynamicInsertMeanwhileGetKey(em);
		System.out.println(result);
		System.out.println(em.getId());
	}

	/**
	 * 增------批量增(JUnit测试)
	 *
	 * @date 2018年6月21日 下午15:54:50
	 */
	@Test
	public void batchInsertTest() {
		Employee em0 = new Employee("小", 1, "女");
		Employee em1 = new Employee("丫", 2, "女");
		Employee em2 = new Employee("头", 3, "女");
		Employee em3 = new Employee("气", 4, "女");
		Employee em4 = new Employee("死", 5, "女");
		Employee em5 = new Employee("人", 6, "女");
		List<Employee> list = new ArrayList<Employee>();
		list.add(em0);
		list.add(em1);
		list.add(em2);
		list.add(em3);
		list.add(em4);
		list.add(em5);
		Map<String, List<Employee>> map = new HashMap<>();
		map.put("listTest", list);
		int result = javaAnnotationMapper.batchInsert(map);
		System.out.println(result);
	}

	/**
	 * 增------批量增,并批量获取主键(JUnit测试)
	 *
	 * @date 2018年6月21日 下午15:54:50
	 */
	@Test
	public void batchInsertAutoGetKeyTest() {
		Employee em0 = new Employee("小", 1, "女");
		Employee em1 = new Employee("丫", 2, "女");
		Employee em2 = new Employee("头", 3, "女");
		Employee em3 = new Employee("气", 4, "女");
		Employee em4 = new Employee("死", 5, "女");
		Employee em5 = new Employee("人", 6, "女");
		List<Employee> list = new ArrayList<Employee>();
		list.add(em0);
		list.add(em1);
		list.add(em2);
		list.add(em3);
		list.add(em4);
		list.add(em5);
		Map<String, List<Employee>> map = new HashMap<>();
		map.put("list", list);
		int result = javaAnnotationMapper.batchInsertAutoGetKey(map);
		System.out.println(result);
		for (Employee employee : list) {
			System.out.println(employee.getId());
		}
	}

	/**
	 * 删------动态删(JUnit测试)
	 *
	 * @date 2018年6月15日 下午4:00:11
	 */
	@Test
	public void dynamicDeleteTest() {
		Employee em = new Employee(null, 66, null);
		int result = javaAnnotationMapper.dynamicDelete(em);
		System.out.println(result);
	}

	/**
	 * 改------动态改(JUnit测试)
	 *
	 * @date 2018年6月15日 下午3:47:38
	 */
	@Test
	public void dynamicUpdateTest() {
		Employee em = new Employee("小苗", 16, "女");
		int result = javaAnnotationMapper.dynamicUpdate(em);
		System.out.println(result);
	}

	/**
	 * 查------动态查(JUnit测试)
	 *
	 * @date 2018年6月15日 下午4:32:50
	 */
	@Test
	public void dynamicSelectTest() {
		List<Employee> list = javaAnnotationMapper.dynamicSelect(80);
		System.out.println("集合长度:" + list.size());
		for (Employee em : list) {
			System.out.println(em.toString());
			System.out.println("---------------------");
		}
	}

	/**
	 * 查------一对一,持有对象(xml方法)
	 *
	 * @date 2018年6月21日 下午5:14:33
	 */
	@Test
	public void selectOneToOne() {
		People people = javaAnnotationMapper.selectOneToOneXML(1);
		System.out.println(people.getName());
		System.out.println(people.getIdcard().getNum());
	}

	/**
	 * 增------批量增(xml方法)
	 *
	 * @date 2018年7月25日 下午12:38:46
	 */
	@Test
	public void batchInsertByXMLTest() {
		Employee e1 = new Employee("1", 1, "女");
		Employee e2 = new Employee("2", 2, "男");
		Employee e3 = new Employee("3", 3, "女");
		Employee e4 = new Employee("4", 4, "男");
		List<Employee> list = new ArrayList<>();
		list.add(e1);
		list.add(e2);
		list.add(e3);
		list.add(e4);
		int result = javaAnnotationMapper.batchInsertByXML(list);
		System.out.println(result);
		System.out.println(e1.getId());
		System.out.println(e2.getId());
		System.out.println(e3.getId());
		System.out.println(e4.getId());
	}

	/**
	 * 查------动态查(多个参数&模糊查询&以Map接收)测试
	 *
	 * @date 2018年8月2日 下午5:20:42
	 */
	@Test
	public void dynamicSelectMultiParamTest() {
		List<Map<String, Object>> resultMapList = javaAnnotationMapper.dynamicSelectMultiParam("张", null, null);
		for (Map<String, Object> resultMap : resultMapList) {
			// 如果查询结果使用@Results指定对应property了的话,那么对应的property就位key;
			// 如果查询结果没有使用@Results指定对应property了的话,那么对应的列名就位key;
			System.out.println((Integer) resultMap.get("id"));
			System.out.println((String) resultMap.get("e_name"));
			System.out.println((Integer) resultMap.get("e_age"));
			System.out.println((String) resultMap.get("e_gender"));
		}
	}
	
	
	/**
	 * 直接以List作为参数
	 *
	 * @date 2018年8月13日 下午3:06:07
	 */
	@Test
	public void  dynamicInsertListParamsTest() {
		Employee e1 = new Employee("1", 1, "女");
		Employee e2 = new Employee("2", 2, "男");
		Employee e3 = new Employee("3", 3, "女");
		Employee e4 = new Employee("4", 4, "男");
		List<Employee> list = new ArrayList<>();
		list.add(e1);
		list.add(e2);
		list.add(e3);
		list.add(e4);
		javaAnnotationMapper.dynamicInsertListParams(list);
	}
	
	
	/**
	 * sql代理方法,形参同事有Map、String测试
	 *
	 * @Date 2018年8月22日 下午10:53:03
	 */
	@Test
	public void  insertMultiParamsMapAndStringTest() {
		Map<String, Object> map = new HashMap<>();
		map.put("age", 121);
		String name = "薰悟空";
		javaAnnotationMapper.insertMultiParamsMapAndString(map, name);
	}
	
}
