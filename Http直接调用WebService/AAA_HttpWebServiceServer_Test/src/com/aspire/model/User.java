package com.aspire.model;

/**
 * 用户实体类模型
 *
 * @author JustryDeng
 * @DATE 2018年9月22日 下午8:54:33
 */
public class User {

	/** 姓名 */
	private String myName;

	/** 年龄 */
	private Integer myAge;
	
	/** 座右铭 */
	private String myMotto;

	public String getMyName() {
		return myName;
	}

	public void setMyName(String myName) {
		this.myName = myName;
	}

	public Integer getMyAge() {
		return myAge;
	}

	public void setMyAge(Integer myAge) {
		this.myAge = myAge;
	}

	public String getMyMotto() {
		return myMotto;
	}

	public void setMyMotto(String myMotto) {
		this.myMotto = myMotto;
	}

}
