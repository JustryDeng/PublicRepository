package com.aspire.demo;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ScheduledThreadPool的创建
 *
 * @author JustryDeng
 * @date 2018/10/11 18:47
 */
public class ExecutorsCreateScheduledThreadPool {
    public static void main(String[] args) {

        // -> 创建定时调度线程池(初始化核心线程数为5)
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

        // 使用lambel表达式简单实现Runnable接口的run方法
        System.out.println("当前时间是:" + new Date());
        scheduledExecutorService.schedule(() -> System.out.println("10秒后输出此语句！ -> " + new Date()),10, TimeUnit.SECONDS);
        // 当线程池中所有线程(包括排着队的)都运行完毕后,关闭线程池
        scheduledExecutorService.shutdown();
    }
}
