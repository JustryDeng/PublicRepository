package com.aspire.demo;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CountDownLatch倒计时锁 使用示例
 *
 * @author JustryDeng
 * @date 2018/10/15 23:08
 */
public class CountDownLatchDemo {

    /** 创建CountDownLatch实例,其大小一般与线程任务数量一致 */
    private static CountDownLatch countDownLatch = new CountDownLatch(5);

    public static void main(String[] args) throws InterruptedException {

        // 创建定长线程池
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 1; i <= 5; i++) {
            // 使用lambel表达式简单实现Runnable接口的run方法
            executorService.execute(() -> {
                int consumeTime = new Random().nextInt(10) + 1;
                String name = Thread.currentThread().getName();
                System.out.println(name + ":我要" + consumeTime + "秒才能完成任务!");
                try {
                    Thread.sleep(consumeTime * 1000);
                    System.out.println(name + "完成任务啦!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // 倒计时减一
                    // 注.countDown()方法是线程安全的
                    countDownLatch.countDown();
                }

            });
        }
        // 当countDownLatch的值不为0时,此线程阻塞，继续等待;直到countDownLatch的值=0时,此线程才往下执行
        countDownLatch.await();
        // 关闭线程池
        executorService.shutdown();
        System.out.println(Thread.currentThread().getName() + ":终于该本线程出手了!");
    }

}
