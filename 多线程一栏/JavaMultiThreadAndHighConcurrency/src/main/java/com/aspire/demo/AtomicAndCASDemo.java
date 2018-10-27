package com.aspire.demo;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Atomic包与CAS算法测试
 *
 * @author JustryDeng
 * @date 2018/10/25 16:14
 */
public class AtomicAndCASDemo {

    /** 线程数 */
    private static final Integer threadNum = 10000;

    /** AtomicBoolean为保证多个.compareAndSet()的原子性，会用到锁 */
    private static final ReentrantLock lock = new ReentrantLock();

    /** Integer与AtomicInteger */
    private static Integer count = threadNum;
    private static AtomicInteger atomicInteger = new AtomicInteger(threadNum);

    /** Boolean与AtomicBoolean */
    private static Boolean aBoolean = true;
    private static AtomicBoolean atomicBoolean = new AtomicBoolean(true);

    /** Long[]与AtomicLongArray */
    private static Long[] longArray = new Long[1];
    private static AtomicLongArray atomicLongArray = new AtomicLongArray(1);

    /** 为了避免干扰，每个方法都使用自己对应的 */
    private static CountDownLatch countDownLatch1 = new CountDownLatch(threadNum);
    private static CountDownLatch countDownLatch2 = new CountDownLatch(threadNum);
    private static CountDownLatch countDownLatch3 = new CountDownLatch(threadNum);
    private static CountDownLatch countDownLatch4 = new CountDownLatch(threadNum);
    private static CountDownLatch countDownLatch5 = new CountDownLatch(threadNum);
    private static CountDownLatch countDownLatch6 = new CountDownLatch(threadNum);

    /** 为了避免干扰，每个方法都使用自己对应的 */
    private static CyclicBarrier cyclicBarrier1 = new CyclicBarrier(threadNum);
    private static CyclicBarrier cyclicBarrier2 = new CyclicBarrier(threadNum);
    private static CyclicBarrier cyclicBarrier3 = new CyclicBarrier(threadNum);
    private static CyclicBarrier cyclicBarrier4 = new CyclicBarrier(threadNum);
    private static CyclicBarrier cyclicBarrier5 = new CyclicBarrier(threadNum);
    private static CyclicBarrier cyclicBarrier6 = new CyclicBarrier(threadNum);

    /**
     * 多线程使用共享的Integer测试
     */
    private static void fa1() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < threadNum; i++) {
            executorService.execute(() -> {
                try {
                    cyclicBarrier1.await();
                    count--;
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch1.countDown();
                }
            });
        }
        executorService.shutdown();
        countDownLatch1.await();
        System.out.println("结果应为0, 本次运行结果count = " + count);
    }

    /**
     * 多线程使用共享的AtomicInteger测试
     */
    private static void fa2() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < threadNum; i++) {
            executorService.execute(() -> {
                try {
                    cyclicBarrier2.await();
                    atomicInteger.getAndDecrement();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch2.countDown();
                }
            });
        }
        executorService.shutdown();
        countDownLatch2.await();
        System.out.println("结果应为0, 本次运行结果atomicInteger = " + atomicInteger);
    }

    /**
     * 多线程使用共享的Boolean测试
     */
    private static void fa3() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < threadNum; i++) {
            executorService.execute(() -> {
                try {
                    cyclicBarrier3.await();
                    aBoolean = !aBoolean;
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch3.countDown();
                }
            });
        }
        executorService.shutdown();
        countDownLatch3.await();
        System.out.println("结果应为true, 本次运行结果aBoolean = " + aBoolean);
    }

    /**
     * 多线程使用共享的AtomicBoolean测试
     */
    private static void fa4() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < threadNum; i++) {

            executorService.execute(() -> {
                try {
                    cyclicBarrier4.await();
                    // 虽然.compareAndSet()本身是原子性的，但是多个.compareAndSet()就不是原子性的了
                    // 这里使用可重入锁，保证多个.compareAndSet()的原子性
                    lock.lock();
                    try {
                        boolean result = atomicBoolean.compareAndSet(true, false);
                        if(!result) {
                            atomicBoolean.compareAndSet(false, true);
                        }
                    } finally {
                        lock.unlock();
                    }

                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch4.countDown();
                }
            });
        }
        executorService.shutdown();
        countDownLatch4.await();
        System.out.println("结果应为true, 本次运行结果atomicBoolean = " + atomicBoolean);
    }

    /**
     * 多线程使用共享的Long[]测试
     */
    private static void fa5() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < threadNum; i++) {
            final int index = i;
            executorService.execute(() -> {
                try {
                    cyclicBarrier5.await();
                    longArray[0] = longArray[0] + (long)index;
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch5.countDown();
                }
            });
        }
        executorService.shutdown();
        countDownLatch5.await();
        System.out.println("结果应为49995000, 本次运行Long[]测试方法结果为" + longArray[0]);
    }

    /**
     * 多线程使用共享的AtomicLongArray测试
     */
    private static void fa6() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < threadNum; i++) {
            final int index = i;
            executorService.execute(() -> {
                try {
                    cyclicBarrier6.await();
                    atomicLongArray.getAndAdd(0, index);
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch6.countDown();
                }
            });
        }
        executorService.shutdown();
        countDownLatch6.await();
        System.out.println("结果应为49995000, 本次运行AtomicLongArray测试方法结果为" + atomicLongArray.get(0));
    }

    /**
     * 主函数
     */
    public static void main(String[] args) throws InterruptedException {
        longArray[0] =(long)0;
        atomicLongArray.set(0, 0);
        fa1();
        System.out.println();
        fa2();
        fa3();
        System.out.println();
        fa4();
        fa5();
        System.out.println();
        fa6();
    }
}

/**
 * ABA测试
 *
 * @author JustryDeng
 * @date 2018/10/27 11:08
 */
class AbaTest{
    private static User userA = new User("张三", 18);
    private static User userB = new User("李四", 24);
    // 初始化，userA与stamp组装为一个pair,封装进AtomicStampedReference<T>
    private static AtomicStampedReference<User> atomicStampedReference1 = new AtomicStampedReference<>(userA, 0);

    private static User userC = new User("张三", 18);
    private static User userD = new User("李四", 24);
    // 初始化，userA与初始值为0的stamp组装为一个pair,封装进AtomicStampedReference<T>
    private static AtomicStampedReference<User> atomicStampedReference2 = new AtomicStampedReference<>(userC, 0);

    /**
     * 主函数
     */
    public static void main(String[] args) {
        fa1();
        fa2();
    }

    /**
     * 引发ABA问题---测试
     * 注:为了便于说明、让读者更易理解,这里直接用单线程来模拟多线程环境;
     *
     * @author JustryDeng
     * @date 2018/10/27 11:08
     */
    public static void fa1() {

        // 我们是为了引起ABA问题，所以我们这里要消除stamp版本戳的效果，我们把其都设置为相同的值
        final int stamp = 0;

        // -> 线程A执行此步
        // ABA前 >>> 此时 内存值为userA，旧的预期值为userA
        boolean result1 = atomicStampedReference1.attemptStamp(userA, stamp);
        System.out.println("为true则说明atomicStampedReference1内的reference与userA是同一个引用! "
                + "result1结果为" + result1);

        // -> 其余线程执行此步(如果内存值是userA，则将内存值改为userB)
        boolean result2 = atomicStampedReference1.compareAndSet(userA, userB, stamp, stamp);
        System.out.println("为true则说明atomicStampedReference1内的reference与userA是同一个引用,"
                + "且将atomicStampedReference1内的reference替换为了userB！ result2结果为" + result2);
        // 篡改userA的属性值
        userA.setAge(81);
        // -> 其余线程执行此步(如果内存值是userB，则将内存值改为userA)
        boolean result3 = atomicStampedReference1.compareAndSet(userB, userA, stamp, stamp);
        System.out.println("为true则说明atomicStampedReference1内的reference与userB是同一个引用,"
                + "且将atomicStampedReference1内的reference替换为了userA！ result3结果为" + result3);

        // -> 线程A执行此步
        // ABA后 >>> 此时 内存值为userA，旧的预期值为userA，内存值与预期值是同一个对象
        boolean result4 = atomicStampedReference1.compareAndSet(userA, userA, stamp, stamp);
        System.out.println("不发生ABA问题时应该输出18! 而发生ABA问题时的输出却变为了:"
                + atomicStampedReference1.getReference().getAge());
    }


    /**
     * 解决ABA问题---测试
     * 注:为了便于说明、让读者更易理解,这里直接用单线程来模拟多线程环境;
     * 注: 因为要避免ABA问题,所以我们要对版本号渐进(一般为 + 1)
     *
     * @author JustryDeng
     * @date 2018/10/27 11:08
     */
    public static void fa2() {

        final int stamp = 1;

        // -> 线程A执行此步
        // ABA前 >>> 此时 内存值为userC，旧的预期值为userC, 版本戳为0
        // 注:因为ABA还没开始,ABA之后版本戳是和此语句执行之后的版本戳进行对比的，所以这里版本戳设置成和不和原来的版本戳一样都行
        boolean result1 = atomicStampedReference2.attemptStamp(userC, stamp);
        System.out.println("此时版本戳为 " + atomicStampedReference2.getStamp() + ", result1为true则说明"
                + "atomicStampedReference2内的reference与userC是同一个引用!"
                + "result1结果为" + result1);

        // -> 其它某线程执行
        // ABA前 >>> 此时 内存值为userC，旧的预期值为userC, 版本戳为0
        boolean result2 = atomicStampedReference2.compareAndSet(userC, userD, atomicStampedReference2.getStamp(), atomicStampedReference2.getStamp() + 1);
        System.out.println("此时版本戳为 " + atomicStampedReference2.getStamp() + ", result2为true则说明"
                + "atomicStampedReference2内的reference与userC是同一个引用, "
                + "且将atomicStampedReference2内的reference替换为了userD！ "
                + "result2结果为" + result2);
        userA.setAge(81);
        boolean result3 = atomicStampedReference2.compareAndSet(userD, userC,  atomicStampedReference2.getStamp(), atomicStampedReference2.getStamp() + 1);
        System.out.println("此时版本戳为 " + atomicStampedReference2.getStamp() + ", result3为true则说明"
                + "atomicStampedReference2内的reference与userD是同一个引用, "
                + "且将atomicStampedReference2内的reference替换为了userC！ "
                + "result3结果为" + result3);


        // -> 线程A执行此步此步骤会比较atomicStampedReference2中的reference与userC是否一致，并且期望的版本戳是否和stamp一致
        boolean result4 = atomicStampedReference2.compareAndSet(userC, null, 1, stamp + 1);
        System.out.println();
        System.out.print("期望引用为:" + userC.hashCode() + "实际内存引用为:" + userC.hashCode());
        System.out.println("\t\t\t期望stamp为:" + stamp + "实际stamp为:" + 1);
        System.out.println("不发生ABA问题时应该输出true,发生ABA问题时应输出false! 输出结果为:" + result4);
    }

}

/**
 * User辅助类
 */
class User{

    String name;

    Integer age;

    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{name='" + name + "', age=" + age +'}';
    }
}