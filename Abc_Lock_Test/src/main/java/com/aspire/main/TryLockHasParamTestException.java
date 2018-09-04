package com.aspire.main;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * .tryLock(long time, TimeUnit unit)使用测试(这里主要测试 线程 中断时 抛出异常)
 * 注:如果线程没有抢占到锁,那么等待time这么久(单位为unit),
 * 注:等待期间,抢占到了返回true;过了时间仍然没有抢占到,那么返回false
 * 注:等待期间,线程中断了,那么throws InterruptedException
 *
 * @author JustryDeng
 * @DATE 2018年9月4日 上午11:20:17
 */
public class TryLockHasParamTestException {
	
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
     * 为了将代码放在一个类里面展示,所以本人在这里使用了成员内部类
     * 注:实际上单独创建一个继承Thread的类更轻松
     *
     * @DATE 2018年9月4日 下午5:42:28
     */
    class MyThread extends Thread {
    	
    	Lock lock = null;

    	public MyThread(Lock lock, String name) {
    		super(name);
    		this.lock = lock;
		}
    	
		@Override
		public void run() {
			try {
				// 需要获取到lock的锁才能进入if条件成立对应的代码
				// 设置等待获取Lock实例锁,最多等待200毫秒
				if(lock.tryLock(200, TimeUnit.MILLISECONDS)) {
					System.out.println(Thread.currentThread().getName() 
							              + "中的.tryLock(long time, TimeUnit unit)在指定时"
							                  + "间内抢占到了锁,返回true;进入if条件成立逻辑");
					try {
						// 竞争multiThreadTest()资源
						multiThreadTest(Thread.currentThread());
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
		}
	}
	
	/**
	 * 程序入口
	 *
	 * @DATE 2018年9月4日 下午2:28:40
	 */
	public static void main(String[] args) {
		
		final Lock lock = new ReentrantLock();
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
		
		// 成员内部类,开启第二个线程
		TryLockHasParamTestException.MyThread myThread = 
				                new TryLockHasParamTestException(). new MyThread(lock, "线程Two");
		myThread.start();
		
		// 在主线程中计时,过20毫秒,中断myThread线程
		// 注:如果线程One率先抢占到了锁,那么至少会消耗101毫秒(因为我在线程One中sleep了200毫秒)才会释放锁
        //	   那么线程Two至少需要等待101毫秒;这里我们在其等待的第20毫秒左右中断该线程,那么线程Two中的
		//   lock.tryLock(100, TimeUnit.MILLISECONDS)方法会抛出InterruptedException异常，
		//    进而被捕获,输出"线程Two在等待获取锁时,【被中断】了！！！"
		long startTime = System.currentTimeMillis();
		for (;;) {
			if(System.currentTimeMillis() - startTime >= 20) {
				myThread.interrupt();
			     break;
			}
		}
		
	}

}
