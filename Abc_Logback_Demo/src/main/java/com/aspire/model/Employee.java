package com.aspire.model;

/**
 * Employee员工实体类模型
 * 
 * @author JustryDeng
 * @date 2018年6月13日下午2:41:53
 */

public class Employee {
	/**
	 * 员工id(数据库自增)
	 */
	private Integer id;
	/**
	 * 员工名字
	 */
	private String name;
	/**
	 * 员工年龄
	 */
	private Integer age;
	/**
	 * 员工性别
	 */
	private String gender;

	/**
	 * 无参构造
	 */
	public Employee() {
		super();
	}

	/**
	 * 有参构造
	 * 
	 * @param name
	 *            员工姓名
	 * @param age
	 *            员工年龄
	 * @param gender
	 *            员工性别
	 */
	public Employee(String name, Integer age, String gender) {
		super();
		this.name = name;
		this.age = age;
		this.gender = gender;
	}

	/**
	 * 全参构造
	 * 
	 * @param id
	 *            员工id
	 * @param name
	 *            员工姓名
	 * @param age
	 *            员工年龄
	 * @param gender
	 *            员工性别
	 */
	public Employee(Integer id, String name, Integer age, String gender) {
		super();
		this.id = id;
		this.name = name;
		this.age = age;
		this.gender = gender;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	@Override
	public String toString() {
		return "Employee{id=" + id + ", name='" + name + "', age="
				+ age + ", gender='" + gender + "'}";
	}
}
