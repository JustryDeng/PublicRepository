package com.aspire.util;

import java.util.Calendar;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Scheduled测试
 *
 * @author JustryDeng
 * @date 2018年7月9日 下午7:29:11
 */
@Component
public class TimingUtil {
	/**
	 * 定时计划One
	 *
	 * @throws InterruptedException
	 * @date 2018年7月9日 下午7:29:28
	 */
	@Scheduled(cron = "0/10 * * * * ?")
	@Async("asyncExecutor")// 使用asyncExecutor方法创建的线程池
	public void scheduledTestOne() throws InterruptedException {
		long start = System.currentTimeMillis();
		// 使用线程sleep来模拟此方法运行时所需时间(为了更加直观的进行说明,这里定的时间较大)
		Thread.sleep(6000);
		Calendar calendar = Calendar.getInstance();
		System.out.println("One>>>" + calendar.get(calendar.MINUTE) + "分" 
		                       + calendar.get(calendar.SECOND) + "秒");
		long end = System.currentTimeMillis();
		System.out.println("耗时---------------" + (end - start) + "毫秒\n");
	}

	/**
	 * 定时计划Two
	 *
	 * @date 2018年7月9日 下午7:29:42
	 */
	@Scheduled(cron = "0/5 * * * * ?")
	@Async("asyncExecutor") // 使用asyncExecutor方法创建的线程池
	public void scheduledTestTwo() {
		long start = System.currentTimeMillis();
		Calendar calendar = Calendar.getInstance();
		System.out.println("Two>>>" + calendar.get(calendar.MINUTE) + "分" 
		                       + calendar.get(calendar.SECOND) + "秒");
		long end = System.currentTimeMillis();
		System.out.println("耗时---------------" + (end - start) + "毫秒\n");
	}
}
