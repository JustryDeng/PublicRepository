package com.aspire.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CachedThreadPool的创建
 *
 * @author JustryDeng
 * @date 2018/10/11 18:47
 */
public class ExecutorsCreateCachedThreadPool {
    private static Integer count = 10000;
    private static final Object OBJ = new Object();
    public static void main(String[] args) throws InterruptedException {

        // -> 创建可缓存线程池
        ExecutorService executorService = Executors.newCachedThreadPool();

        for (int i = 1; i <= 10000; i++) {
            // 使用lambel表达式简单实现Runnable接口的run方法
            executorService.execute(() -> {
                // 只有获得了object的锁的线程,才能操作
                synchronized (OBJ) {
                    count--;
                }
            });
        }
        // 当线程池中所有线程都运行完毕后,关闭线程池
        executorService.shutdown();
        // 主线程阻塞2秒再输出count的值,为了避免输出打印count的值时,其余线程还没计算完;导致输出的不是count的最终值
        Thread.sleep(2000);
        System.out.println(count);
    }
}
