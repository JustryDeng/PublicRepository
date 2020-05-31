package com.aspire.author;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这是一个自恋的注解
 *
 * @author {@link JustryDeng}
 * @date 2020/4/24 0:25:59
 */
@SuppressWarnings("all")
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface JustryDeng {

    /** 姓名 */
    String[] name() default {"邓沙利文", "亨得帅", "JustryDeng", "邓二洋", "邓帅"};

    /** 座右铭 */
    String motto() default "我是一只小小小小鸟~嗷！嗷！";

    /** 邮箱 */
    String[] email() default {"13548417409@163.com", "dengeryanger@gmail.com"};

    /** 好好学习，天天向上 */
    String[] DAY_DAY_UP() default {"https://blog.csdn.net/justry_deng", "https://github.com/JustryDeng?tab=repositories"};
}
