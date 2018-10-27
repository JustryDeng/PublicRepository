package com.aspire.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * FixedThreadPool的创建
 *
 * @author JustryDeng
 * @date 2018/10/11 18:47
 */
public class ExecutorsCreateFixedThreadPool {
    private static Integer count = 10000;
    private static Lock lock = new ReentrantLock();
    public static void main(String[] args) throws InterruptedException {

        // -> 创建定长线程池(该线程池中总共存在200个线程)
        ExecutorService executorService = Executors.newFixedThreadPool(20);

        for (int i = 1; i <= 10000; i++) {
            // 使用lambel表达式简单实现Runnable接口的run方法
            executorService.execute(() -> {
                // 使用重入锁,保证线程安全同步
                lock.lock();
                try {
                    count--;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }

            });
        }
        // 当线程池中所有线程(包括排着队的)都运行完毕后,关闭线程池
        executorService.shutdown();
        // 主线程阻塞2秒再输出count的值,为了避免输出打印count的值时,其余线程还没计算完;导致输出的不是count的最终值
        Thread.sleep(2000);
        System.out.println(count);
    }
}
