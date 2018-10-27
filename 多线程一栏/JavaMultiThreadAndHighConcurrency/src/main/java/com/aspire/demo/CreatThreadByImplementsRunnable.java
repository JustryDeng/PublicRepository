package com.aspire.demo;

/**
 * 创建线程 ---- 测试
 *
 * @date 2018/10/11 14:54
 */
class Rtest {
    public static void main(String[] args) {
        // 获取该线程实例
        CreatThreadByImplementsRunnable ctb = new CreatThreadByImplementsRunnable();
        Thread thread = new Thread(ctb, "R线程");
        // 运行该线程
        thread.start();

        //主线程同时输出,以作对比
        for (int i = 1; i <= 100; i++) {
            Thread.currentThread().setName("main线程");
            System.out.println(Thread.currentThread().getName() + "->" + i);
        }
    }
}

/**
 * 创建线程 -> 实现Runnable接口,重写run方法;
 *
 * @author JustryDeng
 * @date 2018/10/11 14:52
 */
public class CreatThreadByImplementsRunnable implements Runnable{

    @Override
    public void run() {
        for (int i = 1; i <= 100; i++) {
            System.out.println(Thread.currentThread().getName() + "->" + i);
        }
    }
}
