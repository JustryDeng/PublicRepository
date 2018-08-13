package com.aspire.model;

/**
 * 身份证实体模型
 *
 * @author JustryDeng
 * @date 2018年6月21日 下午4:55:40
 */
public class Idcard {
	/**
	 * id
	 */
	private Integer id;
	/**
	 * 证件号码
	 */
	private Integer num;

	/**
	 * 无参
	 * 
	 */
	public Idcard() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 全参
	 * 
	 * @param id
	 *            id
	 * @param num
	 *            证件号码
	 */
	public Idcard(Integer id, Integer num) {
		super();
		this.id = id;
		this.num = num;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

}
