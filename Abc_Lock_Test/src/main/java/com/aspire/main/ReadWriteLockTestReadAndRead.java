package com.aspire.main;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁 ---> 已经获得锁对象的读锁的线程 与 其它要获得同一个锁对象的读锁的线程;不需要等待锁释放,可直接进行
 *
 * @author JustryDeng
 * @DATE 2018年9月4日 下午7:36:59
 */
public class ReadWriteLockTestReadAndRead {
	
	public static void main(String[] args) {
		// ReentrantReadWriteLock是ReadWriteLock接口的主要实现之一;不仅提供了基本的实现,还额外丰富了几种方法
		ReentrantReadWriteLock rrwl = new ReentrantReadWriteLock();
		// 匿名内部类 开启第一个线程
		new Thread("线程One") {
			public void run() {
				rrwl.readLock().lock();
				try {
					for (int i = 0; i < 100; i++) {	
						System.out.println(Thread.currentThread().getName() + "第" + i+ "次读");
					}
				} finally {
					rrwl.readLock().unlock();
				}
			};
		}.start();

		// 匿名内部类 开启第二个线程
		new Thread("线程Two") {
			public void run() {
				rrwl.readLock().lock();
				try {
					for (int i = 0; i < 100; i++) {	
						System.out.println(Thread.currentThread().getName() + "第" + i+ "次读");
					}
				} finally {
					rrwl.readLock().unlock();
				}
			};
		}.start();

	}

}