package com.aspire.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;

import com.aspire.mapper.provider.SqlProvider;
import com.aspire.model.Employee;
import com.aspire.model.People;

/**
 * 数据访问层
 * 
 * @author JustryDeng
 * @date 2018年6月13日下午11:36:37
 */
@Mapper
public interface JavaAnnotationMapper {
	/**
	 * 增------简单增
	 *
	 * @param employee
	 *            员工实体模型
	 * @return 成功插入的条数
	 * @date 2018年6月14日 上午9:51:13
	 */
	@Insert("INSERT INTO employee VALUES(null,#{e.name},#{e.age},#{e.gender})")
	int singleInsert(@Param("e") Employee employee);

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

	/**
	 * 增------参数既有对象,又有普通参数(这里使用@Options,则该对象可获得主键)
	 *
	 * @param employee
	 *            员工实体模型
	 * @param name
	 *            名字
	 * @param age
	 *            年龄
	 * @param gender
	 *            性别
	 * @return 受影响行数
	 * @date 2018年7月10日 下午6:13:40
	 */
	@Insert("INSERT INTO employee (e_name,e_age,e_gender) VALUES" + "(#{e.name},#{e.age},#{e.gender}),"
			+ "(#{name0},#{age0},#{gender0})")
	@Options(useGeneratedKeys = true, keyProperty = "e.id", keyColumn = "id")
	int singleInsertMultiParam(@Param("e") Employee employee, @Param("name0") String name, @Param("age0") Integer age,
			@Param("gender0") String gender);

	/*
	 * //除了用@Options获取增加后的主键外，家可以用@SelectKey来获取主键
	 * 
	 * @Insert("INSERT INTO employee (e_name,e_age,e_gender)" +
	 * " VALUES(#{e.name},#{e.age},#{e.gender})")
	 * 
	 * @SelectKey(statement="select last_insert_id()",keyProperty="e.id",
	 * resultType=Integer.class, before=false) int
	 * singleInsertAutoGetKey(@Param("e") Employee employee);
	 */

	/**
	 * 删------简单删
	 *
	 * @param id
	 *            员工id
	 * @return 受影响条数
	 * @date 2018年6月14日 下午1:24:01
	 */
	@Delete("DELETE FROM employee WHERE id = #{id}")
	int singleDelete(@Param("id") Integer id);

	/**
	 * 改------简单改
	 *
	 * @param name
	 *            员工姓名
	 * @param id
	 *            员工id
	 * @return 受影响行数
	 * @date 2018年6月14日 下午1:38:31
	 */
	@Update("UPDATE employee SET e_name = #{name} WHERE id = #{id}")
	int singleUpdate(@Param("name") String name, @Param("id") Integer id);

	/**
	 * 查------以 对象模型 接收数据
	 *
	 * @param name
	 *            员工姓名
	 * @return 该员工对象模型
	 * @date 2018年6月13日下午2:49:40
	 */
	@Select("SELECT e.id,e.e_age FROM employee e WHERE e.e_name = #{e_name}")
	@Results({ @Result(id = true, column = "id", property = "id"), @Result(column = "e_age", property = "age") })
	Employee singleSelectAcceptDataByObject(@Param("e_name") String name);

	/**
	 * 查------以 Map<String, Object> 接收数据
	 *
	 * @param name
	 *            员工姓名
	 * @return 将值存进Map中(注:如果没指定，那么column即为key;如果指定了，那么property即为key)
	 * @date 2018年6月13日下午2:49:40
	 */
	@Select("SELECT e.id,e.e_age FROM employee e WHERE e.e_name = #{e_name}")
	@Results({ @Result(id = true, column = "id", property = "id"), @Result(column = "e_age", property = "age") })
	Map<String, Object> singleSelectAcceptDataByMap(@Param("e_name") String name);

	/**
	 * 查------以 一般类型 接收数据
	 *
	 * @param name
	 *            员工姓名
	 * @return 员工id
	 * @date 2018年6月14日 下午4:10:56
	 */
	@Select("SELECT e.id FROM employee e WHERE e.e_name = #{name}")
	@Results({ @Result(column = "id", property = "id") })
	Integer singleSelectAcceptDataByString(@Param("name") String name);

	/**
	 * 查------以 List 接收多个实体模型数据
	 *
	 * @param id
	 *            员工id上限
	 * @return 集合List
	 * @date 2018年6月14日 下午4:24:55
	 */
	@Select("SELECT e.id,e.e_age,e.e_name,e.e_gender FROM employee e WHERE e.id <= #{maxId}")
	@Results({ @Result(id = true, column = "id", property = "id"), @Result(column = "e_name", property = "name"),
			@Result(column = "e_age", property = "age"), @Result(column = "e_gender", property = "gender") })
	List<Employee> singleSelectAcceptDataByList(@Param("maxId") Integer maxId);

	/**
	 * 增------动态增(单个参数)
	 *
	 * @param em
	 *            员工实体类模型
	 * @return 受影响条数
	 * @date 2018年6月15日 上午11:50:06
	 */
	@InsertProvider(type = SqlProvider.class, method = "dynamicInsertProvider")
	int dynamicInsert(Employee em);

	/**
	 * 增------动态增(多个参数)
	 *
	 * @param em
	 *            员工实体类模型
	 * @return 受影响条数
	 * @date 2018年6月15日 下午1:43:06
	 */
	@InsertProvider(type = SqlProvider.class, method = "dynamicInsertMultiParamProvider")
    // @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	@SelectKey(statement="select last_insert_id()",keyProperty="id",resultType=Integer.class, before=false)
	int dynamicInsertMultiParam(Map<String, Object> map);

	/**
	 * 增------动态增(同时获得主键)
	 *
	 * @param em
	 *            员工实体类模型
	 * @return 受影响条数
	 * @date 2018年6月15日 下午1:16:32
	 */
	@InsertProvider(type = SqlProvider.class, method = "dynamicInsertProvider")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	int dynamicInsertMeanwhileGetKey(Employee em);

	/*
	 * 增------动态增(同时获得主键,方式二)
	 * 
	 * @InsertProvider(type = SqlProvider.class , method = "dynamicInsertProvider")
	 * 
	 * @SelectKey(statement="select last_insert_id()",keyProperty="id",
	 * resultType=Integer.class, before=false) int
	 * dynamicInsertMeanwhileGetKey(Employee em);
	 */

	/**
	 * 增------批量增
	 *
	 * @param map
	 *            包含了员工集合的map
	 * @return 受影响条数
	 * @date 2018年6月22日 上午10:39:14
	 */
	@InsertProvider(type = SqlProvider.class, method = "batchInsertProvider")
	int batchInsert(Map<String, List<Employee>> map);

	/**
	 * 增------批量增(同时获得主键)
	 *
	 * @param map
	 *            包含了员工集合的map
	 * @return 受影响条数
	 * @date 2018年6月22日 上午10:39:14
	 */
	@InsertProvider(type = SqlProvider.class, method = "batchInsertProvider")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	int batchInsertAutoGetKey(Map<String, List<Employee>> map);

	/**
	 * 删------动态删
	 *
	 * @param em
	 *            员工实体类模型
	 * @return 受影响的行数
	 * @date 2018年6月15日 下午3:40:26
	 */
	@DeleteProvider(type = SqlProvider.class, method = "dynamicDeleteProvider")
	int dynamicDelete(Employee em);

	/**
	 * 改------动态改
	 *
	 * @param em
	 *            员工实体类模型
	 * @return 受影响的行数
	 * @date 2018年6月15日 下午3:38:50
	 */
	@UpdateProvider(type = SqlProvider.class, method = "dynamicUpdateProvider")
	int dynamicUpdate(Employee em);

	/**
	 * 查------动态查
	 *
	 * @param age
	 *            员工年龄
	 * @return 员工实体类模型
	 * @date 2018年6月15日 下午4:26:40
	 */
	@SelectProvider(type = SqlProvider.class, method = "dynamicSelectProvider")
	@Results({ @Result(column = "id", property = "id", id = true), @Result(column = "e_name", property = "name"),
			@Result(column = "e_age", property = "age"), @Result(column = "e_gender", property = "gender") })
	List<Employee> dynamicSelect(Integer age);

	/**
	 * 查------动态查(多个参数&模糊查询&以Map接收)
	 * 注:我们可以提前把这些参数放进Map传过去;也可以不放入Map中,直接传多个参数,MyBatis框架会自动将这些参数放入Map中
	 *
	 * @param name
	 *            员工姓名
	 * @param age
	 *            员工年龄
	 * @param gender
	 *            员工性别
	 * @return 查询结果(以List接收,每一行数据以键值对的形式存储接收)
	 * @date 2018年8月2日 下午5:03:54
	 */
	@SelectProvider(type = SqlProvider.class, method = "dynamicSelectProviderMultiParamProvider")
	List<Map<String, Object>> dynamicSelectMultiParam(@Param("name") String name, @Param("age") Integer age,
			@Param("gender") String gender);

	/**
	 * 查------一对一,持有对象(xml方法)
	 *
	 * @param id
	 * @return 公民对象
	 * @date 2018年6月21日 下午5:03:57
	 */
	People selectOneToOneXML(Integer id);

	/**
	 * 通过xml实现批量增
	 *
	 * @param map
	 *            键值对存放参数
	 * @return 受影响行数
	 * @date 2018年7月25日 下午12:25:45
	 */
	int batchInsertByXML(List<Employee> list);
	
	/**
	 * 直接传List; MyBatis会将List封装为Map,其中key为参数前的@Param,
	 * 注:此时@Param是必须要有的
	 *
	 * @param list
	 * @return
	 * @date 2018年8月13日 下午2:50:11
	 */
	@InsertProvider(type = SqlProvider.class, method = "dynamicInsertListParamsProvider")
	int dynamicInsertListParams(@Param("listTest") List<Employee> list);
	
	/**
	 * 传递多参(一个Map,一个普通参数) --- 测试
	 * 
	 * @param map
	 * @param name
	 * @return 受影响行数
	 * @Date 2018年8月22日 下午10:48:00
	 */
	@InsertProvider(type = SqlProvider.class, method = "insertMultiParamsMapAndStringProvider")
	int insertMultiParamsMapAndString(@Param("map1") Map<String, Object> map, @Param("name1") String name);

}
