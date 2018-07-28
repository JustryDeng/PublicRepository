package com.aspire.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import com.aspire.model.Employee;

/**
 * 数据访问层
 * 
 * @author JustryDeng
 * @date 2018年6月13日下午11:36:37
 */
@Mapper
public interface JavaAnnotationMapper {

	/**
	 * 增------自动获取增加之后的主键(不需要再进行查询)
	 *
	 * @param employee
	 *            员工实体模型
	 * @return 成功插入的条数
	 * @date 2018年6月14日 上午10:10:37
	 */
	@Insert("INSERT INTO employee (e_name,e_age,e_gender) VALUES(#{e.name},#{e.age},#{e.gender})")
	@Options(useGeneratedKeys = true, keyProperty = "e.id", keyColumn = "id")
	int singleInsertAutoGetKey(@Param("e") Employee employee);

}
