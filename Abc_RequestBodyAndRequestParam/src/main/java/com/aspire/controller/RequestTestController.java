package com.aspire.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aspire.entity.Team;
import com.aspire.entity.User;

/**
 * RequestBody与RequestParam测试
 *
 * @author JustryDeng
 * @date 2018年7月6日 上午2:00:48
 */
@RestController
public class RequestTestController {

	/**
	 * 直接以String接收前端传过来的json数据
	 *
	 * @param jsonString
	 *            json格式的字符串
	 * @return json格式的字符串
	 * @date 2018年7月9日 下午1:24:13
	 */
	@RequestMapping("mytest0")
	public String myTestController0(@RequestBody String jsonString) {
		System.out.println(jsonString);
		return jsonString;
	}

	/**
	 * 以较简单的User对象接收前端传过来的json数据 (SpringMVC会智能的将符合要求的数据装配进该User对象中)
	 *
	 * @param user
	 *            用户实体类模型
	 * @return User重写后的toString
	 * @date 2018年7月9日 下午1:29:47
	 */
	@RequestMapping("mytest1")
	public String myTestController1(@RequestBody User user) {
		System.out.println(user.toString());
		return user.toString();
	}

	/**
	 * 以较复杂的Team对象接收前端传过来的json数据 (SpringMVC会智能的将符合要求的数据装配进该Teamr对象中)
	 * 注:如果后端@RequestBody后的对象，持有了集合等,当前端向传参 令该对象持有的该集合为空时,json字符串中,
	 * 对应位置应该形如"teamMembers":[]这么写;即:传递的json字符串中必须要有key，否者请求会出错
	 * 
	 *
	 * @param team
	 *            团队实体类模型
	 * @return Team重写后的toString
	 * @date 2018年7月9日 下午1:30:46
	 */
	@RequestMapping("mytest2")
	public String myTestController2(@RequestBody Team team) {
		System.out.println(team.toString());
		return team.toString();
	}

	/**
	 * @RequestBody与简单的@RequestParam()同时使用
	 *
	 * @param user
	 *            用户实体类模型
	 * @date 2018年7月6日 上午2:11:40
	 */
	@RequestMapping("mytest3")
	public String myTestController3(@RequestBody User user, @RequestParam("token") String token) {
		System.out.println(user.toString());
		System.out.println(token);
		return token + ">>>" + user.toString();
	}
	
	/**
	 * @RequestBody装配请求体重的信息;
	 * 第二个参数不加注解,装配url中的参数信息
	 *
	 * @param user
	 *            用户实体类模型
	 * @param token
	 *            
	 * @return
	 * @date 2018年7月9日 下午2:24:53
	 */
	@RequestMapping("mytest4")
	public String myTestController4(@RequestBody User user1,  User user2) {
		System.out.println(user1.toString());
		System.out.println(user2.toString());
		return user2.toString() + "\n" + user1.toString();
	}

	/**
	 * @RequestBody与复杂的@RequestParam()同时使用 注:这里 以集合 或者 以数组 接收数据都可以
	 *
	 * @param user
	 *            用户实体类模型
	 * @param arrays
	 *            从'key-value'中获取到的集合数组
	 * @return
	 * @date 2018年7月9日 下午1:34:33
	 */
	@RequestMapping("mytest5")
	public String myTestController5(@RequestBody User user, @RequestParam("arrays") List<String> arrays) {
		System.out.println(user.toString());
		StringBuffer sb = new StringBuffer();
		for (String array : arrays) {
			sb.append(array);
			sb.append("  ");
			System.out.println(array);
		}
		return sb.toString() + user.toString();
	}

	/**
	 * 如果参数前没有@RequestParam()，那么前端调用该请求时,这个参数可写可不写; 
	 * 注:写了该参数，那么会装配到;如果没写该参数,也不会请求出错
	 * 注:如果参数前写了@RequestParam()，那么前端调用该请求时,这个参数的参数名，参数自可以没有可以为空;
	 *
	 * @param user
	 *            用户实体类模型
	 * @param token
	 *            参数token
	 * @return
	 * @date 2018年7月9日 下午1:38:54
	 */
	@RequestMapping("mytest6")
	public String myTestController6(@RequestBody User user, String token) {
		System.out.println(user.toString());
		return token + ">>>" + user.toString();
	}

}
