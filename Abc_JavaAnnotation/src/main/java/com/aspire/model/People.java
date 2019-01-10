package com.aspire.model;

/**
 * 公民
 *
 * @author JustryDeng
 * @date 2018年6月21日 下午4:54:25
 */
@SuppressWarnings("unused")
public class People {
	/**
	 * id
	 */
	private Integer id;
	/**
	 * 姓名
	 */
	private String name;
	/**
	 * 身份证实体类模型
	 */
	private Idcard idcard;

	/**
	 * 无参
	 * 
	 */
	public People() {
	}

	/**
	 * 有参构造
	 * 
	 * @param id
	 *            id
	 * @param name
	 *            姓名
	 */
	public People(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	/**
	 * 全参
	 * 
	 * @param id
	 *            id
	 * @param name
	 *            姓名
	 * @param idcard
	 *            身份证实体模型
	 */
	public People(Integer id, String name, Idcard idcard) {
		super();
		this.id = id;
		this.name = name;
		this.idcard = idcard;
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

	public Idcard getIdcard() {
		return idcard;
	}

	public void setIdcard(Idcard idcard) {
		this.idcard = idcard;
	}

}
