package com.aspire.server;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.aspire.model.User;

/**
 * 对外提供的WebService方法
 *
 * @author JustryDeng
 * @DATE 2018年9月22日 下午8:58:42
 */
@WebService
public class MyWebService {
	
	/**
	 * 入参参数为基本参数,出参参数为对象
	 *
	 * @DATE 2018年9月22日 下午8:59:07
	 */
	public User userMethod(@WebParam(name ="name") String name, 
					@WebParam(name ="age") Integer age,
					@WebParam(name ="motto") String motto) {
		System.out.println("---> 进userMethod方法了！");
		User user = new User();
		user.setMyAge(age * 100);
		user.setMyName(name + "亨得帅");
		user.setMyMotto(motto + "就是这么流畅!");
		return user;
	}

}