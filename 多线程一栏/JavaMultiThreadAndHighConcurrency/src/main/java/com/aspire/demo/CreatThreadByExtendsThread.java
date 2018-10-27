package com.aspire.demo;

/**
 * 创建线程 --- 测试
 *
 * @author JustryDeng
 * @date 2018/10/11 14:52
 */
public class CreatThreadByExtendsThread {
    public static void main(String[] args) {
        // 获取该线程实例
        MyThread myThread = new MyThread();
        // 运行该线程
        myThread.start();

        //主线程同时输出,以作对比
        for (int i = 1; i <= 100; i++) {
            Thread.currentThread().setName("main线程");
            System.out.println(Thread.currentThread().getName() + "->" + i);
        }

    }


}

/**
 * 创建线程 -> 继承Thread类,重写run方法
 *
 * @date 2018/10/11 14:54
 */
class MyThread extends Thread{

    @Override
    public void run() {
        this.setName("T线程");
        for (int i = 1; i <= 100; i++) {
            System.out.println(this.getName() + "->" + i);
        }
    }
}