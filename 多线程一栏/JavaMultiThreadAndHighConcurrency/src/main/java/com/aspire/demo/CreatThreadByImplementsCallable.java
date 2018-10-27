package com.aspire.demo;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

/**
 * 创建线程 ---- 测试
 *
 * @date 2018/10/11 14:54
 */
class Ctest {
    private static Integer count;

    public static void main(String[] args) {
        fa1();
//        fa2();
    }

    public static void fa1() {
        // 获取该线程实例
        CreatThreadByImplementsCallable ctbic = new CreatThreadByImplementsCallable();
        ctbic.setName("C线程");
        // 使用Executors调度器,创建有3个空闲线程的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        // 将Callable<V>实现类的实例提交到线程池中,如果线程池中有空的线程,那么执行此实例的call()方法
        executorService.submit(ctbic);
        // 线程池中的所有线程都运行完毕后,关闭线程池
        executorService.shutdown();
        //主线程同时输出,以作对比
        for (int i = 1; i <= 100; i++) {
            Thread.currentThread().setName("main线程");
            System.out.println(Thread.currentThread().getName() + "->" + i);
        }
    }

    public static void fa2() {
        FutureTask s = null;
        // 获取该线程实例
        CreatThreadByImplementsCallable ctbic = new CreatThreadByImplementsCallable();
        ctbic.setName("C线程");
        // 使用Executors调度器,创建有3个空闲线程的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        // 将Callable<V>实现类的实例提交到线程池中,如果线程池中有空的线程,那么执行此实例的call()方法
        // Future<V>接口与Callable<V>搭配使用,future.get()用于等待call()方法执行完毕并接受call()方法的返回值
        Future<Integer> future = executorService.submit(ctbic);
        try {
            // 注意:future.get()会使当前线程阻塞,一致等到call()执行完毕返回返回值之后,当前线程再往下执行
            System.out.println("ctbic实例的call()方法的返回值为:" + future.get());
            // 线程池中的所有线程都运行完毕后,关闭线程池
            executorService.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //主线程同时输出,以作对比
        for (int i = 1; i <= 100; i++) {
            Thread.currentThread().setName("main线程");
            System.out.println(Thread.currentThread().getName() + "->" + i);
        }
    }
}

/**
 * 创建线程 -> 实现Callable<V>接口,call方法
 * 注:指定泛型,就是制定call()方法的返回值类型
 * 注:不指定泛型，那么默认泛型为Object
 *
 * @author JustryDeng
 * @date 2018/10/11 14:52
 */
public class CreatThreadByImplementsCallable implements Callable<Integer> {
    /* 线程名 */
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 相比起其他两种方式,此方式的优势在于:
     * 1. 有返回值
     * 2. 可以抛出异常
     */
    @Override
    public Integer call() throws Exception {
        // 随机获得[0,1000)中的整数
        Integer num = new Random().nextInt(1000);
        if (num <= 3) {
            throw new Exception();
        }
        //主线程同时输出,以作对比
        for (int i = 1; i <= num; i++) {
            System.out.println(name + "->" + i);
        }
        return num;
    }
}
