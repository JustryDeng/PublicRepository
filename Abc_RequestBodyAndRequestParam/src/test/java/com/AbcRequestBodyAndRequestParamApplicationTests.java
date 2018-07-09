package com;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;
import com.aspire.entity.Team;
import com.aspire.entity.User;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AbcRequestBodyAndRequestParamApplicationTests {

	@Test
	public void contextLoads() {
		User user1 = new User();
		user1.setName("小丁");
		user1.setAge(40);
		user1.setGender("男");
		user1.setMotto("看俺防你一杆！");

		User user2 = new User();
		user2.setName("潘晓婷");
		user2.setAge(18);
		user2.setGender("女");
		user2.setMotto("动作要优雅！");

		User user3 = new User();
		user3.setName("邓沙利文");
		user3.setAge(24);
		user3.setGender("男");
		user3.setMotto("就是这么牛逼！");

		List<User> userList = new ArrayList<>();
		userList.add(user1);
		userList.add(user2);
		userList.add(user3);

		List<String> honorList = new ArrayList<>();
		honorList.add("速度最快");
		honorList.add("高度最高");
		honorList.add("合作最默契");

		Team team = new Team();
		team.setId(1);
		team.setTeamName("地表最强战队");
		team.setHonors(honorList);
		team.setTeamMembers(userList);
		System.out.println(JSON.toJSONString(team));
//		System.out.println(JSON.toJSONString(team, true));
	}

}
