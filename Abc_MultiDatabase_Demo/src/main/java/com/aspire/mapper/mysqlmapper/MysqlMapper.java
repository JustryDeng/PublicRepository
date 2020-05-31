package com.aspire.mapper.mysqlmapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * MySQL数据访问层
 *
 * @author JustryDeng
 * @date 2018年9月1日 上午12:33:59
 */
@Mapper
@SuppressWarnings("all")
public interface MysqlMapper {


	@Select("SELECT info FROM multi_datasource_table WHERE id = #{id}")
	String simpleSelect(@Param("id") Integer id);

	@Insert("insert into multi_datasource_table (id, info) values(#{id}, #{info})")
	Integer simpleInsert(@Param("id") Integer id, @Param("info") String info);
}
