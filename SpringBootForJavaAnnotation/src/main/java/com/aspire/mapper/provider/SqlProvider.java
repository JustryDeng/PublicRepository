package com.aspire.mapper.provider;

import java.util.List;
import java.util.Map;

import com.aspire.model.Employee;

public class SqlProvider {
	/**
	 * 增------动态增 建议:建议使用StringBuffer或者StringBuilder拼接字符串,
	 * 而不使用String(这里是演示,为了看起来方便，所以用了String)
	 *
	 * @param em
	 *            员工实体类模型
	 * @return 最终的SQL语句
	 * @date 2018年6月15日 上午11:52:03
	 */
	public String dynamicInsertProvider(Employee em) {
		if (em.getName() == null || em.getName().equals("xx")) {
			em.setName("邓某");
		}
		if (em.getAge() == null || em.getAge() >= 20) {
			em.setAge(18);
		}
		if (em.getGender() == null || em.getGender().length() == 0) {
			em.setGender("男");
		}
		//#{}防止sql注入
		//#{}是直接从employee中根据property(即属性)进行取值的
		String sql = "insert into employee (e_name,e_age,e_gender) "
				         + "values (#{name}, #{age}, #{gender})";
		return sql;
	}

	/**
	 * 增------动态增(map传递多个参数)
	 *
	 * @param map
	 *            多个参数放入了map中，key分别是name,age,gender
	 * @return 最终的SQL语句
	 * @date 2018年6月15日 下午2:46:49
	 */
	public String dynamicInsertMultiParamProvider(Map<String, Object> map) {
		String name = (String) map.get("name");
		Integer age = (Integer) map.get("age");
		String gender = (String) map.get("gender");
		if (name == null || name.equals("xx")) {
			map.put("name", "邓某");
		}
		if (age == null || age >= 20) {
			map.put("age", "18");
		}
		if (gender == null || gender.length() == 0) {
			map.put("gender", "男");
		}
		//使用#{}防止sql注入
		//#{}是直接从map中根据key进行取值的
		String sql = "insert into employee (e_name,e_age,e_gender) values "
				         + "(#{name},#{age},#{gender})";
		return sql;
	}

	/**
	 * 增------批量增
	 *
	 * @param map
	 *            包含了员工集合的map
	 * @return sql语句
	 * @date 2018年6月22日 上午10:41:32
	 */
	public String batchInsertProvider(Map<String, List<Employee>> map) {
		List<Employee> list = map.get("listTest");
		StringBuilder sql = new StringBuilder();
		sql.append("insert into employee");
		sql.append(" (e_name,e_age,e_gender) ");
		sql.append("values ");
		for (int i = 0; i < list.size(); i++) {
			sql.append("(");
			String s = "#{listTest[" + i + "].name}, " 
			               + "#{listTest[" + i + "].age}, " 
					           + "#{listTest[" + i + "].gender}";
			sql.append(s);
			sql.append(")");

			if (i < list.size() - 1) {
				sql.append(",");
			}
		}
		System.out.println(sql + "");
		return sql + "";
	}
/*
 * //采用MessageFormat模板，实现批量增
 * public String batchInsertProvider(Map<String, List<Employee>> map) {
 *     List<Employee> list = map.get("listTest");
 *     StringBuilder sql = new StringBuilder();
 *     sql.append("insert into employee");
 *     sql.append(" (e_name,e_age,e_gender) ");
 *     sql.append("values ");
 *     //定义模板样式
 *     MessageFormat messageFormat = new
 *     MessageFormat("#'{'listTest[{0}].name},#'{'listTest[{0}].age},#'{'listTest[{0}].gender}");
 *     for (int i = 0; i < list.size(); i++) {
 *         sql.append("(");
 *         //循环用i的值代替模板中的占位符
 *         sql.append(messageFormat.format(new Object[] { i }));
 *         sql.append(")");
 *         if (i < list.size() - 1) {
 *             sql.append(",");
 *         }
 *     }
 *     return sql + "";
 * }
 */
	/**
	 * 删------动态删
	 *
	 * @param em
	 *            员工实体类模型
	 * @return 最终的SQL语句
	 * @date 2018年6月15日 下午3:46:25
	 */
	public String dynamicDeleteProvider(Employee em) {
		StringBuffer sql = new StringBuffer();
		boolean flag = em.getAge() == null || em.getAge() <= 40;
		if (flag) {
			sql.append("delete from employee where e_age > 40");
		}
		if (!flag) {
			//使用#{}防止sql注入
			sql.append("delete from employee where e_age = #{age}");
		}
		return sql + "";
	}

	/**
	 * 改------动态改
	 *
	 * @param em
	 *            员工实体类模型
	 * @return 最终的SQL语句
	 * @date 2018年6月15日 下午3:46:25
	 */
	public String dynamicUpdateProvider(Employee em) {
		if (em.getId() == null || em.getId() <= 5) {
			em.setId(72);
		}
		//使用#{}防止sql注入
		String sql = "update  employee set e_name = #{name} where id = #{id}";
		return sql;
	}

	/**
	 * 查------动态查
	 *
	 * @param age
	 *            员工年龄
	 * @return sql语句
	 * @date 2018年6月22日 下午1:40:50
	 */
	public String dynamicSelectProvider(Integer age) {
		StringBuffer sql = new StringBuffer();
		if (age == null || age >= 20) {
			age = 18;
		}
		sql.append("select em.id, em.e_name, em.e_age, em.e_gender from employee em ");
		sql.append("where em.e_gender = '女' and em.e_age = " + age);
		System.out.println(sql+"");
		return sql + "";
	}

}
