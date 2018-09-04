package com.aspire.main;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * .lock()使用测试
 *
 * @author JustryDeng
 * @DATE 2018年9月4日 上午11:20:17
 */
public class LockTest {

	/**
	 * 多线程测试方法
	 * 注:多个线程同时调用此方法(即:抢占此资源)
	 *
	 * @DATE 2018年9月4日 下午2:18:49
	 */
	static void multiThreadTest(Thread thread) {
		String threadName = thread.getName();
		for (int i = 0; i < 10; i++) {
			System.out.println(threadName + "进入for循环,此次i为:" + i);
		}
		System.out.println(threadName + "执行完毕!");
	}
	
	/**
	 * 程序入口
	 *
	 * @DATE 2018年9月4日 下午2:28:40
	 */
	public static void main(String[] args) {
		
		Lock lock = new ReentrantLock();
		// 匿名内部类,开启第一个线程
		new Thread("线程One") {
			public void run() {
				// 需要获取到lock的锁才能进入下面的代码
				lock.lock();
				try {
					// 竞争multiThreadTest()资源
					multiThreadTest(Thread.currentThread());
				} catch (Exception e) {
				}finally {
					lock.unlock();
				}
			};
		}.start();
		// 匿名内部类,开启第二个线程
		new Thread("线程Two") {
			public void run() {
				// 需要获取到lock的锁才能进入下面的代码
				lock.lock();
				try {
					// 竞争multiThreadTest()资源
					multiThreadTest(Thread.currentThread());
				} catch (Exception e) {
				}finally {
					lock.unlock();
				}
			};
		}.start();
	}

}
