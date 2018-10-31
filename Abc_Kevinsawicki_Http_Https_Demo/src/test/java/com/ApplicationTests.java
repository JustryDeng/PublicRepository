package com;

import com.github.kevinsawicki.http.HttpRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

    /**
     * GET请求测试
     *
     * @date 2018/10/31 14:37
     */
    @Test
    public void getRequestTest() throws UnsupportedEncodingException {
        // 如果URL中含有特殊的字符值,那么需要编码一下
        String symbolValue = URLEncoder.encode("&",  "UTF-8");
        String url = "http://127.0.0.1:8080/get?name=lisi&age=18&symbol=" + symbolValue;
        HttpRequest httpRequest = HttpRequest.get(url);
        // 执行请求,并返回请求体数据
        String responseBody = httpRequest.body();
        System.out.println(responseBody);
    }

    /**
     * POST请求体 --- 测试
     *
     * @date 2018/10/31 14:37
     */
    @Test
    public void postRequestTest() {
        HttpRequest httpRequest = new HttpRequest("http://127.0.0.1:8080/post/requestBody", "POST");
        httpRequest.contentType("application/json", "UTF-8");
        // 将请求体信息放入send中
        httpRequest.send("i am data 我是json数据!");
        System.out.println(httpRequest.body());
    }


    /**
     * 表单数据 --- 测试
     *
     * @date 2018/10/31 14:37
     */
    @Test
    public void formTest() {
        Map<String, Object> data = new HashMap<>(8);
        data.put("name", "JustryDeng");
        data.put("age", 24);
        HttpRequest httpRequest = new HttpRequest("http://127.0.0.1:8080/post/formInfo", "POST");
        httpRequest.form(data);
        System.out.println(httpRequest.body());
    }

    /**
     * 请求头于测试
     * 注:头域里面的key,是不区分大小写的;
     *    值还是会区分大小写的
     *
     * @date 2018/10/31 14:37
     */
    @Test
    public void headerTest() {
        HttpRequest httpRequest = new HttpRequest("https://127.0.0.1:8080/requestHeadInfo", "GET");
        // 往请求头域中添加信息的同通用方式
        httpRequest.header("my-name", "邓沙利文");
        httpRequest.header("my-gender", "男");
        httpRequest.header("my-age", "24");
        httpRequest.header("my-motto", "我是一只小小小小鸟~");
        // 请求头域中经常会用到的的信息,HttpRequest又专门给我们提供了一些方法，如:
        httpRequest.authorization("AuThoriZatIon");
        httpRequest.contentType("text/xml", "UTF-8");
        //Accept all certificates
        httpRequest.trustAllCerts();
        //Accept all hostnames
        httpRequest.trustAllHosts();
        System.out.println(httpRequest.body());
    }

    /**
     * HTTPS请求测试
     *
     * @date 2018/10/31 14:37
     */
    @Test
    public void httpsTest() throws UnsupportedEncodingException {
        // 如果URL中含有特殊的字符值,那么需要编码一下
        String name = URLEncoder.encode("邓沙利文",  "UTF-8");
        HttpRequest httpsRequest = HttpRequest.post("https://127.0.0.1:8080/https?name=" + name);
        //Accept all certificates
        httpsRequest.trustAllCerts();
        //Accept all hostnames
        httpsRequest.trustAllHosts();
        System.out.println(httpsRequest.body());
    }
}