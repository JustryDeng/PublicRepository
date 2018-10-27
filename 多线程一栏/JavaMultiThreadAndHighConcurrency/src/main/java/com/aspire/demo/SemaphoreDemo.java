package com.aspire.demo;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Semaphore信号量使用测试
 *
 * @author JustryDeng
 * @date 2018/10/15 23:45
 */
public class SemaphoreDemo {

    public static void main(String[] args) {
        // test1();
        // test2();
        test3();
    }

    /**
     *  Semaphore的.tryAcquire(long timeout, TimeUnit unit)方法 :如果在指定时间内都没获取到,那么久获取失败了
     *
     */
    public static void test3() {
        // 创建可缓存线程池
        ExecutorService executorService = Executors.newCachedThreadPool();
        // 获取型号量实例(设置允许最大线程数为20个)
        Semaphore semaphore = new Semaphore(20);
        for (int i = 1; i <= 100; i++) {
            // 使用lambel表达式简单实现Runnable接口的run方法
            executorService.execute(() -> {
                String name = Thread.currentThread().getName();
                try {
                    // 申请攀岩绳索(即:获取资格) -> 8秒内没获取到资格,那么久失败了
                    if (semaphore.tryAcquire(8000, TimeUnit.MILLISECONDS)) {
                        try {
                            int consumeTime = new Random().nextInt(7) + 4;
                            System.out.println(name + "获取了攀岩资格，开始攀岩!");
                            Thread.sleep(consumeTime * 1000);
                            System.out.println(name + "攀岩结束！");

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            // 让出绳索(即:释放资格)
                            semaphore.release();
                        }
                    } else {
                        System.out.println(name + "没有获取到资格!");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });
        }
        // 关闭线程池
        executorService.shutdown();
    }

    /**
     *   Semaphore的.tryAcquire()方法 :立马知道获取结果,就算没有获取到也不等待
     */
    public static void test2() {
        // 创建可缓存线程池
        ExecutorService executorService = Executors.newCachedThreadPool();
        // 获取型号量实例(设置允许最大线程数为20个)
        Semaphore semaphore = new Semaphore(20);
        for (int i = 1; i <= 100; i++) {
            // 使用lambel表达式简单实现Runnable接口的run方法
            executorService.execute(() -> {
                String name = Thread.currentThread().getName();
                // 申请攀岩绳索(即:获取资格)  -> 立马知道结果,成功还是失败
                if (semaphore.tryAcquire()) {
                    try {
                        int consumeTime = new Random().nextInt(7) + 4;
                        System.out.println(name + "获取了攀岩资格，开始攀岩!");
                        Thread.sleep(consumeTime * 1000);
                        System.out.println(name + "攀岩结束！");

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        // 让出绳索(即:释放资格)
                        semaphore.release();
                    }
                } else {
                    System.out.println(name + "没有获取到资格!");
                }

            });
        }
        // 关闭线程池
        executorService.shutdown();
    }

    /**
     *   Semaphore的.acquire()方法 :获取资格,如果没有获取到资格那么就一直等下去,直到获取到了资格为止
     */
    public static void test1() {
        // 创建可缓存线程池
        ExecutorService executorService = Executors.newCachedThreadPool();
        // 获取型号量实例(设置允许最大线程数为20个)
        Semaphore semaphore = new Semaphore(20);
        for (int i = 1; i <= 100; i++) {
            // 使用lambel表达式简单实现Runnable接口的run方法
            executorService.execute(() -> {
                try {
                    // 申请攀岩绳索(即:获取资格)  -> 如果没有获取到资格那么就一直等下去,直到获取到了资格为止
                    semaphore.acquire();
                    int consumeTime = new Random().nextInt(10) + 1;
                    String name = Thread.currentThread().getName();
                    System.out.println(name + "获取了攀岩资格，开始攀岩!");
                    Thread.sleep(consumeTime * 1000);
                    System.out.println(name + "攀岩结束！");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // 让出绳索(即:释放资格)
                    semaphore.release();
                }

            });
        }
        // 关闭线程池
        executorService.shutdown();
    }
}
