package com.aspire.main;

/*
 * 场景说明:
 * 甲和乙打赌,甲说可以在乙先跑39米的情况下，挥刀仍能伤着乙;乙不信，要比一下速度。于是请来裁判丁。
 *
 * 于是比赛规则(流程)就有了:
 *	 第一步:丁(裁判)倒计时5个数,数到0时,(通知)乙开始跑。
 *	 第二步:乙开始跑,跑到第39米时，(通知)甲可以挥刀了。
 *	 第三步:甲挥刀。
 */
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition简单测试
 *
 * @author JustryDeng
 * @DATE 2018年9月4日 下午8:42:14
 */
public class ConditionTest {

	/** 获取Lock实例 */
	Lock lock = new ReentrantLock();
	
	/** 丁(裁判)倒计时结束后,由false变为true; */
	boolean gameStartFlag = false;
	
	/** 乙跑了39米后,由false变为true; */
	boolean swingFlag = false;
	
	/** 对应jia中用到的Condition */
	Condition jiaCondition = lock.newCondition();
	
	/** 对应yi中用到的Condition */
	Condition yiCondition = lock.newCondition();
	
	/**
	 * 对应 --- 甲
	 */
	public void jia() {
		lock.lock();
		try {
			if(!swingFlag) {// 如果乙还没跑够39米,那么继续等乙跑够
				jiaCondition.await();
			}
			if(swingFlag) {
				System.out.println(Thread.currentThread().getName() + ":我挥刀了！！！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * 对应 --- 乙
	 */
	public void yi() {
		lock.lock();
		try {
			if(!gameStartFlag) { // 如果游戏还没开始,那么继续等
				yiCondition.await();
			}
			System.out.println(Thread.currentThread().getName() + ":我开始跑了！！！");
			Thread.sleep(2000);
			System.out.println(Thread.currentThread().getName() + ":我已经跑了39米了！！！");
			// 将 挥刀 标识符 状态改为true;这样 甲就知道可以 挥刀了
			swingFlag = true;
			// 由于要 唤醒  甲; 解铃还须系铃人,用jiaCondition.await()的,这里就用jiaCondition来唤醒
			jiaCondition.signal();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * 对应 --- 丁
	 */
	public void ding() {
		lock.lock();
		try {
			System.out.println(Thread.currentThread().getName() + "(裁判):倒计时开始！！！");
			for (int i = 5; i >= 0; i--) {
				System.out.println(Thread.currentThread().getName() + "(裁判):" + i);
			}
			System.out.println(Thread.currentThread().getName() + "(裁判):比赛开始！！！");
			// 将 开始 标识符 状态改为true;这样乙就知道可以开始跑了
			gameStartFlag = true;
			// 由于要 唤醒  乙; 解铃还须系铃人,用yiCondition.await()的,这里就用yiCondition来唤醒
			yiCondition.signal();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			lock.unlock();
		}
	}
    /**
     * 入口
     */
    public static void main(String[] args) {
    	ConditionTest ct = new ConditionTest();
    	new Thread("甲") {
    		@Override
    		public void run() {
    			ct.jia();
    		}
    	}.start();
    	
    	new Thread("乙") {
    		@Override
    		public void run() {
    			ct.yi();
    		}
    	}.start();
    	
    	new Thread("丁") {
    		@Override
    		public void run() {
    			ct.ding();
    		}
    	}.start();
    }
}