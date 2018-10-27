package com.aspire.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SingleThreadExecutor的创建
 *
 * @author JustryDeng
 * @date 2018/10/11 18:47
 */
public class ExecutorsCreateSingleThreadExecutor {
    private static Integer count = 10000;
    public static void main(String[] args) throws InterruptedException {

        // -> 创建定单线程线程池
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        for (int i = 1; i <= 10000; i++) {
            // 使用lambel表达式简单实现Runnable接口的run方法
            // 因为是单线程池,所以不需要任何其他操作,就能保证数据的安全准确性
            executorService.execute(() -> count--);
        }
        // 当线程池中所有线程(包括排着队的)都运行完毕后,关闭线程池
        executorService.shutdown();
        // 主线程阻塞2秒再输出count的值,为了避免输出打印count的值时,其余线程还没计算完;导致输出的不是count的最终值
        Thread.sleep(2000);
        System.out.println(count);
    }
}
