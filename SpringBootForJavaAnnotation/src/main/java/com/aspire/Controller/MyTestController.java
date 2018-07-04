package com.aspire.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aspire.mapper.JavaAnnotationMapper;
import com.aspire.model.Employee;

@RestController
public class MyTestController {
	@Autowired
	JavaAnnotationMapper javaAnnotationMapper;

	@RequestMapping("/test123")
	public String testController() {
		Employee e = javaAnnotationMapper.singleSelectAcceptDataByObject("邓沙利文");
		return e.getId() + "," + e.getAge();
	}

	@RequestMapping("/testInsert")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String testController2() {
		int result = 0;
		try {
			Employee e = new Employee("a1hh", 22, "男");
			result = javaAnnotationMapper.singleInsert(e);
            //制造异常
			int a = 1/0;
		} catch(Exception e) {
			
		}
		return result + "";
	}
}
