package com.aspire.entity;

import java.util.List;

/**
 * 小组测试实体类模型
 *
 * @author JustryDeng
 * @date 2018年7月8日 下午11:23:51
 */
public class Team {

	/** id */
	private Integer id;

	/** 小组名字 */
	private String teamName;

	/** 所获称号 */
	private List<String> honors;

	/** 小组成员 */
	private List<User> teamMembers;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public List<String> getHonors() {
		return honors;
	}

	public void setHonors(List<String> honors) {
		this.honors = honors;
	}

	public List<User> getTeamMembers() {
		return teamMembers;
	}

	public void setTeamMembers(List<User> teamMembers) {
		this.teamMembers = teamMembers;
	}

	/*
	 * 重写toString
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// 遍历出小组所获荣耀
		StringBuffer sbHonors = new StringBuffer("荣耀start----\n");
		for (String honor : honors) {
			sbHonors.append(honor);
			sbHonors.append("\n");
		}
		sbHonors.append("荣耀end----\n");

		// 遍历出小组成员
		StringBuffer sbMembers = new StringBuffer("成员start----\n");
		for (User user : teamMembers) {
			sbMembers.append(user.toString());
			sbMembers.append("\n");
		}
		sbMembers.append("成员end----\n");

		return "小组id:" + id + "\n" + "小组名字:" + teamName + "\n" + "小组所获荣誉:" 
		           + sbHonors + "\n" + "小组成员:" + sbMembers;
	}

}
