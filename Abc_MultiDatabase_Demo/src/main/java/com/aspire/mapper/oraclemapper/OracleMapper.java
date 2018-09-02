package com.aspire.mapper.oraclemapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * ORACLE数据访问层
 *
 * @author JustryDeng
 * @Date 2018年9月1日 上午12:33:59
 */
@Mapper
public interface OracleMapper {

	/**
	 * 根据id查数据
	 */
	@Select("SELECT info FROM multi_datasource_table WHERE id = #{id}")
	String singleSelect(@Param("id") Integer id);
	
}
