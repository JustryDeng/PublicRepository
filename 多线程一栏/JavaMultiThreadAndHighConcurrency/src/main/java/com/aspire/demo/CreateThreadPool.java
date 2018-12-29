package com.aspire.demo;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * 虽然我们可以通过Excutors创建线程池，但是推荐:我们自己手动创建线程池
 *
 * @author JustryDeng
 * @date 2018/12/29 13:58
 */
public class CreateThreadPool {

    private static final Object OBJ = new Object();

    private static Integer count = 10000;

    public static void main(String[] args) {
        try {
            createThreadPoolTest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void createThreadPoolTest() throws InterruptedException {
        int length = 10000;

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("justry-deng-pool-%d").build();

        ExecutorService executorService = new ThreadPoolExecutor(5, 200,
                                                                 0L, TimeUnit.MILLISECONDS,
                                                                 new LinkedBlockingQueue<>(1024),
                                                                 namedThreadFactory,
                                                                 new ThreadPoolExecutor.AbortPolicy());
        for (int i = 1; i <= length; i++) {
            // 使用lambel表达式简单实现Runnable接口的run方法
            executorService.execute(() -> {
                // 只有获得了object的锁的线程,才能操作
                synchronized (OBJ) {
                    System.out.println(Thread.currentThread().getName());
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
