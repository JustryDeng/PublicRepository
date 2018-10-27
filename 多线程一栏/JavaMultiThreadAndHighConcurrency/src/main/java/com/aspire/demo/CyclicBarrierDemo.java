package com.aspire.demo;

import java.util.Date;
import java.util.concurrent.*;

/**
 * 循环避障 示例
 *
 * @author JustryDeng
 * @date 2018/10/17 19:39
 */
public class CyclicBarrierDemo {

    /** 设置循环避障数 */
    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(5);

    public static void main(String[] args) throws InterruptedException {
        // -> 创建可缓存长线程池
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 1; i <= 10; i++) {
            Thread.sleep(1000);
            // 使用lambel表达式简单实现Runnable接口的run方法
            executorService.execute(() -> {
                try {
                    System.out.println(new Date() + "线程" + Thread.currentThread().getName() + "准备就绪!");
                    // 设置避障点（如果await超过了指定时间,那么不论线程数是否满足避障数量,都放行开始运行）
                    cyclicBarrier.await(3,TimeUnit.SECONDS);
                    System.out.println(new Date() + "线程" + Thread.currentThread().getName() + "开始运行!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            });
        }
        // 关闭线程池
        executorService.shutdown();
    }

//    /** 设置循环避障数 */
//    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(5);
//
//    public static void main(String[] args) throws InterruptedException {
//        // -> 创建可缓存长线程池
//        ExecutorService executorService = Executors.newCachedThreadPool();
//        for (int i = 1; i <= 10; i++) {
//            Thread.sleep(1000);
//            // 使用lambel表达式简单实现Runnable接口的run方法
//            executorService.execute(() -> {
//                try {
//                    System.out.println(new Date() + "线程" + Thread.currentThread().getName() + "准备就绪!");
//                    // 设置避障点
//                    cyclicBarrier.await();
//                    System.out.println(new Date() + "线程" + Thread.currentThread().getName() + "开始运行!");
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (BrokenBarrierException e) {
//                    e.printStackTrace();
//                }
//            });
//        }
//        // 关闭线程池
//        executorService.shutdown();
//    }
}
