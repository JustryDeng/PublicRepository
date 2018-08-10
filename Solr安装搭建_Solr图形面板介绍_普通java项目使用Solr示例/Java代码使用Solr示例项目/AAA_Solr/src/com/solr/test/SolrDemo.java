package com.solr.test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

/**
 * SolrJ使用demo
 *
 * @author JustryDeng
 * @date 2018年8月9日 上午9:31:57
 */
public class SolrDemo {

	/**
	 * Solr---查询
	 *
	 * @date 2018年8月9日 上午9:48:14
	 */
	public void solrQuery() {

		// 指定要连接的SolrCore
		String solrUrl = "http://127.0.0.1:8983/solr/solr_core";

		// 创建Solr客户端(注:与HttpClient类似)
		HttpSolrClient httpSolrClient = new HttpSolrClient.Builder(solrUrl).withConnectionTimeout(60000)
				.withSocketTimeout(20000).build();
		// 创建SolrQuery对象
		SolrQuery query = new SolrQuery();

		/*
		 * 输入查询条件 注:且用“AND”, 或用“OR”;这里不能能进行多复杂的筛选,如果想进一步筛选可配置下面的过滤
		 */
		query.setQuery("product_name:别致  OR product_name:欧式");

		/// 或查询时不指定域,但是给其配置默认域
		// query.setQuery("笔记本");
		// query.set("df", "product_name");

		// 或这样
		// query.set("q", "product_name:笔记本");

		// 设置过滤条件
		query.setFilterQueries("product_price:[1 TO 80]");
		/// 再给出几个示例
		// query.setFilterQueries("id:2009");
		// query.setFilterQueries("product_price:[1 TO 100] AND id:[2000 TO 2009]");
		// query.setFilterQueries("product_price:[1 TO 10] OR product_price:[30 TO
		/// 40]");

		// 排序设置(这里按价格降序)
		query.setSort("product_price", ORDER.desc);
		// 设置次级排序(这里即:当product_price一样时,按id升序排列),如果还要再设置次级的话,那么再进行此步骤即可
		query.addSort("id", ORDER.asc);

		// 设置分页信息(默认为:0,10)
		query.setStart(0);
		query.setRows(100);

		// 设置查询的Field(即:设置要查询的列)
		query.setFields("id", "product_name", "product_price", "product_picture");

		// 设置对应域中,对应的“查询条件”字段高亮显示
		query.setHighlight(true);
		query.addHighlightField("product_name");
		query.setHighlightSimplePre("[>>>");
		query.setHighlightSimplePost("<<<<]");

		try {
			// 执行查询并返回结果
			QueryResponse response = httpSolrClient.query(query);
			System.out.println(">>>>>>>>>>" + httpSolrClient.query(query).getHeader());

			// 从响应中获取到查询结果文档
			SolrDocumentList solrDocumentList = response.getResults();

			// 匹配结果总数
			long count = solrDocumentList.getNumFound();
			System.out.println("匹配结果总数:" + count);

			// 遍历查询结果
			// 获取高亮显示信息
			Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
			for (SolrDocument doc : solrDocumentList) {
				System.out.println("id:" + doc.get("id"));
				System.out.println("商品名:" + doc.get("product_name"));
				// 处理高亮显示的结果
				List<String> list2 = highlighting.get(doc.get("id")).get("product_name");
				if (list2 != null) {
					System.out.println("商品名称中满足查询条件,再次(高亮)显示商品名:" + list2.get(0));
				}
				System.out.println("价格:" + doc.get("product_price"));
				System.out.println("图片:" + doc.get("product_picture"));
				System.out.println("------------------华丽分割线------------------");
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭客户端,释放资源
				httpSolrClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Solr---新增/修改(有该id则修改,无该id则新增)
	 *
	 * @date 2018年8月9日 下午9:12:57
	 */
	public void solrAddOrUpdate() {

		// 指定要连接的SolrCore
		String solrUrl = "http://127.0.0.1:8983/solr/solr_core";

		// 创建Solr客户端(注:与HttpClient类似)
		HttpSolrClient httpSolrClient = new HttpSolrClient.Builder(solrUrl)
											.withConnectionTimeout(60000)
											.withSocketTimeout(20000)
											.build();

		// 创建文档doc
		SolrInputDocument document = new SolrInputDocument();

		/*
		 * 添加内容 注:参数分别是:(域名,对应的值) 注:域的名称必须是在schema.xml中定义的 注:id域一定要有
		 */
		document.addField("id", "12345");
		document.addField("name", "邓沙利文");
		document.addField("title", "测试solrAddOrUpdate");

		try {
			// 执行add并返回结果
			UpdateResponse updateResponse = httpSolrClient.add(document);
			System.out.println("方法操作耗时(毫秒)......" + updateResponse.getElapsedTime());
			// 这里必须要commit提交
			httpSolrClient.commit();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭客户端,释放资源
				httpSolrClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

/**
 * Solr---删除
 *
 * @date 2018年8月9日 下午9:36:05
 */
public void solrDelete() {

	// 指定要连接的SolrCore
	String solrUrl = "http://127.0.0.1:8983/solr/solr_core";

	// 创建Solr客户端(注:与HttpClient类似)
	HttpSolrClient httpSolrClient = new HttpSolrClient.Builder(solrUrl)
										.withConnectionTimeout(60000)
										.withSocketTimeout(20000)
										.build();

	try {
		// 执行删除
		httpSolrClient.deleteByQuery("id:12345");
		// 再给出两种常用的删除方式
		// httpSolrClient.deleteById(String id)
		// httpSolrClient.deleteById(List<String> ids)
		
		// 这里必须要commit提交
		httpSolrClient.commit();
	} catch (SolrServerException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		try {
			// 关闭客户端,释放资源
			httpSolrClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

	public static void main(String[] args) {
		SolrDemo solrDemo = new SolrDemo();
//		solrDemo.solrQuery();
//		 solrDemo.solrAddOrUpdate();
		solrDemo.solrDelete();
	}
}