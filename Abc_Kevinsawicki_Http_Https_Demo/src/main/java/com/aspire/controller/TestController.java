package com.aspire.controller;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

/**
 * Kevinsawicki测试Controller
 *
 * @author JustryDeng
 * @date 2018/10/31 11:07
 */
@RestController
public class TestController {

    /**
     * 测试 -> GET参请求
     *
     * @date 2018/10/31 14:04
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public String getController(@RequestParam("name") String name, Integer age, String symbol) {
        String str = "age:" + name + "\nage:" + age + "\nsymbol:" + symbol;
        return "欢迎来到getController! -> \n" + str;
    }

    /**
     * 测试 -> POST请求体
     *
     * @date 2018/10/31 14:04
     */
    @RequestMapping(value = "/post/requestBody", method = RequestMethod.POST)
    public String requestBodyController(@RequestBody String str) {
        return "欢迎来到requestBodyController! -> \n" + str;
    }

    /**
     * 测试 -> 表单信息
     *
     * @date 2018/10/31 14:04
     */
    @RequestMapping(value = "/post/formInfo", method = RequestMethod.POST)
    public String formInfoController(@RequestParam("name") String name, Integer age) {
        String str = "age:" + name + "\nage:" + age;
        return "欢迎来到formInfoController! -> \n" + str;
    }

    /**
     * 测试 -> 请求头
     * 注:头域中的key(name)不区分大小写
     *    value区分大小写
     *
     * @date 2018/10/31 14:04
     */
    @RequestMapping(value = "/requestHeadInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public String requestHeadInfoController(HttpServletRequest request) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder(32);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            // 获取到请求头中的key、value
            String key = preventGarbledHandler(headerNames.nextElement());
            String value = preventGarbledHandler(request.getHeader(key));
            sb.append("key(name)为:").append(key);
            sb.append("\tvalue为:").append(value);
            sb.append("\n");
        }
        // 请求域中的key，是不区分大小写的;所以下面这四个都能取出来
        System.out.println("第一次取值:" + request.getHeader("authorization"));
        System.out.println("第二次取值:" + request.getHeader("Authorization"));
        System.out.println("第三次取值:" + request.getHeader("AUTHORization"));
        System.out.println("第四次取值:" + request.getHeader("authoriZAtion"));
        return "欢迎来到requestHeadInfoController! -> \n" + sb;
    }

    /**
     * 防乱码处理
     */
    private String preventGarbledHandler(String originStr) throws UnsupportedEncodingException {
        return new String(originStr.getBytes("ISO-8859-1"), "UTF-8");
    }

    /**
     * 测试 -> HTTPS
     *
     * @date 2018/10/31 21:09
     */
    @RequestMapping(value = "/https", method = {RequestMethod.GET, RequestMethod.POST})
    public String httpsController(@RequestParam("name") String name) {
        return name + "进入HTTPS了!HTTPS调用成功!";
    }

}
