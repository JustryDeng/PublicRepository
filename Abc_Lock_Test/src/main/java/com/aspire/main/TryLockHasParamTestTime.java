package com.aspire.main;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * .tryLock(long time, TimeUnit unit)使用测试(注:这里主要测试time)
 * 注:如果线程没有抢占到锁,那么等待time这么久(单位为unit),
 * 注:等待期间,抢占到了返回true;过了时间仍然没有抢占到,那么返回false
 * 注:等待期间,线程中断了,那么throws InterruptedException
 *
 * @author JustryDeng
 * @DATE 2018年9月4日 上午11:20:17
 */
public class TryLockHasParamTestTime {
	
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
				try {
					// 需要获取到lock的锁才能进入if条件成立对应的代码
					// 设置等待获取Lock实例锁,最多等待100毫秒
					if(lock.tryLock(100, TimeUnit.MILLISECONDS)) {
						System.out.println(Thread.currentThread().getName() 
								              + "中的.tryLock(long time, TimeUnit unit)在指定时"
								                  + "间内抢占到了锁,返回true;进入if条件成立逻辑");
						try {
							// 竞争multiThreadTest()资源
							multiThreadTest(Thread.currentThread());
							// 休眠101毫秒。如果此线程获得了锁,那么另一个线程必定等待超时,返回false
							Thread.sleep(101);
						}finally {
							// 释放锁
							lock.unlock();
						}
					}else {
						System.out.println(Thread.currentThread().getName() 
								              + "中的.tryLock(long time, TimeUnit unit)指定时"
								                  + "间内没抢占到锁,返回false");
					}
				} catch (InterruptedException e) {
					System.out.println(Thread.currentThread().getName() 
				              + "在等待获取锁时,【被中断】了！！！");
				}
			};
		}.start();
		// 匿名内部类,开启第二个线程
		new Thread("线程Two") {
			public void run() {
				try {
					// 需要获取到lock的锁才能进入if条件成立对应的代码
					// 设置等待获取Lock实例锁,最多等待100毫秒
					if(lock.tryLock(100, TimeUnit.MILLISECONDS)) {
						System.out.println(Thread.currentThread().getName() 
								              + "中的.tryLock(long time, TimeUnit unit)在指定时"
								                  + "间内抢占到了锁,返回true;进入if条件成立逻辑");
						try {
							// 竞争multiThreadTest()资源
							multiThreadTest(Thread.currentThread());
							// 休眠50毫秒。如果此线程获得了锁,那么另一个线程必定等待超时,返回false
							Thread.sleep(50);
						}finally {
							// 释放锁
							lock.unlock();
						}
					}else {
						System.out.println(Thread.currentThread().getName() 
								              + "中的.tryLock(long time, TimeUnit unit)指定时"
								                  + "间内没抢占到锁,返回false");
					}
				} catch (InterruptedException e) {
					System.out.println(Thread.currentThread().getName() 
				              + "在等待获取锁时,【被中断】了！！！");
				}
			};
		}.start();
	}

}
