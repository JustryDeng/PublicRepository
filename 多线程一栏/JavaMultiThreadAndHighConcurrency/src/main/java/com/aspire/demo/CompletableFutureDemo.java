package com.aspire.demo;

import com.aspire.demo.author.JustryDeng;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.*;
import java.util.function.*;

/**
 * CompletableFuture学习
 *
 * 提示: ForkJoinPool.commonPool线程池即为ForkJoinPool线程池
 *
 * @author {@link JustryDeng}
 * @date 2020/6/6 17:13:24
 */
@SuppressWarnings("all")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = CompletableFutureDemo.class)
public class CompletableFutureDemo {
    
    /// ************************************************ 创建CompletableFuture(runAsync、supplyAsync)
    
    /**
     * public static CompletableFuture<Void> runAsync(Runnable runnable): (使用默认的线程池ForkJoinPool,)异步执行Runnable
     *
     * 输出:
     * ForkJoinPool.commonPool-worker-1	 runnable
     * main	 main
     */
    @Test
    public void runAsyncTest1() throws Exception {
        Runnable myRunnable = () -> {
            System.err.println(Thread.currentThread().getName() + "\t runnable");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        // runAsync(Runnable runnable)
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(myRunnable);
        // 这里sleep 1秒， 是为了验证 runnable是会被立即执行的(而不是懒加载执行)。
        TimeUnit.SECONDS.sleep(1);
        System.err.println(Thread.currentThread().getName() + "\t main");
    }
    
    /**
     * public static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor): 使用指定的线程池,异步执行Runnable
     *
     * 输出:
     * pool-1-thread-1	 runnable
     * main	 main
     */
    @Test
    public void runAsyncTest2() throws Exception {
        Executor myExecutor = Executors.newFixedThreadPool(3);
        Runnable myRunnable = () -> System.err.println(Thread.currentThread().getName() + "\t runnable");
        // runAsync(Runnable runnable, Executor executor)
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(myRunnable, myExecutor);
        // 这里sleep 1秒， 是为了验证 runnable是会被立即执行的(而不是懒加载执行)。
        TimeUnit.SECONDS.sleep(1);
        System.err.println(Thread.currentThread().getName() + "\t main");
    }
    
    /**
     * public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier): (使用默认的线程池ForkJoinPool,)异步执行Supplier<U>实例,
     *                                                                            并返回结果U
     *
     * 输出:
     * ForkJoinPool.commonPool-worker-1	 supplier
     * main	 JustryDeng~
     */
    @Test
    public void supplyAsyncTest1() throws Exception {
        Supplier<String> supplier = () -> {
            System.out.println(Thread.currentThread().getName() + "\t supplier");
            return "JustryDeng~";
        };
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(supplier);
        // 阻塞main线程， 获取completableFuture异步结果
        String result = completableFuture.get();
        System.err.println(Thread.currentThread().getName() + "\t " + result);
    }
    
    /**
     * public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier): 使用给定的线程池,异步执行Supplier<U>实例,
     *                                                                            并返回结果U
     *
     * 输出:
     * pool-1-thread-1	 supplier
     * main	 JustryDeng~
     */
    @Test
    public void supplyAsyncTest2() throws Exception {
        Executor myExecutor = Executors.newFixedThreadPool(3);
        Supplier<String> mySupplier = () -> {
            System.out.println(Thread.currentThread().getName() + "\t supplier");
            return "JustryDeng~";
        };
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(mySupplier, myExecutor);
        // 阻塞main线程， 获取completableFuture异步结果
        String result = completableFuture.get();
        System.err.println(Thread.currentThread().getName() + "\t " + result);
    }
    
    
    /// ********************************************************************* whenComplete、whenCompleteAsync
    
    
    /**
     * public CompletableFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> action):
     * (同一个异步线程)无消费返回值的消费前面future的异步结果。
     * 注: 无消费返回值、有future返回值。
     * 注: 若异步执行CompletableFuture逻辑的线程是a, 那么在a计算完异步逻辑后，接着还是由该线程进行whenComplete逻辑。
     * 注: CompletableFuture实例的异步结果,会作为形参传递给whenComplete.
     *     换句话说，传递给whenComplete方法的参数，是CompletableFuture实例异步结果的引用，那么对于部分类型的变量来说，
     *     可以在whenComplete中修改该引用指向的堆中的数据，来达到篡改结果的目的。 如下面的StringBuilder示例。
     * 注: 若异步执行CompletableFuture逻辑时异常了， 那么该异常会被包装为CompletionException，并通过
     *     BiConsumer<? super T, ? super Throwable> action中的Throwable使whenComplete方法可感知到；
     *     但是，这里仅仅是感知到(，需要的话可以做一些相应的处理)，但并不会抑制异常的抛出, 所以异常还是会按照自己原有的轨迹继续往外抛。
     *
     * 注: 当原线程执行到whenComplete时， 若future已经执行完成了，那么将会由原线程(这里为main线程)执行action的逻辑。
     *
     * 不抛异常时-输出:
     * ForkJoinPool.commonPool-worker-1	 supplier
     * ForkJoinPool.commonPool-worker-1	JustryDeng~
     * ForkJoinPool.commonPool-worker-1	JustryDeng~qwer~
     * ForkJoinPool.commonPool-worker-1	null
     * main	JustryDeng~qwer~
     *
     * 抛出异常时-输出:
     * ForkJoinPool.commonPool-worker-1	 supplier
     * ForkJoinPool.commonPool-worker-1	null
     * ForkJoinPool.commonPool-worker-1	java.util.concurrent.CompletionException: java.lang.UnsupportedOperationException: 异常了~
     * main	感知到了异步任务抛出的异常! java.lang.UnsupportedOperationException: 异常了~
     * main	null
     */
    @Test
    public void whenCompleteTest1() throws Exception {
        Supplier<StringBuilder> mySupplier = () -> {
            System.out.println(Thread.currentThread().getName() + "\t supplier");
            // 随机抛出异常, 测试BiConsumer<? super T, ? super Throwable>中的Throwable
            if (ThreadLocalRandom.current().nextBoolean()) {
                throw new UnsupportedOperationException("异常了~");
            }
            return new StringBuilder("JustryDeng~");
        };
        
        BiConsumer<StringBuilder, Throwable> action = (StringBuilder x, Throwable y) -> {
            System.out.println(Thread.currentThread().getName() + "\t" + x);
            if (x != null) {
                x.append("qwer~");
                System.out.println(Thread.currentThread().getName() + "\t" + x);
            }
            System.out.println(Thread.currentThread().getName() + "\t" + y);
        };
        // whenComplete
        CompletableFuture<StringBuilder> completableFuture = CompletableFuture
                .supplyAsync(mySupplier)
                .whenComplete(action);
        // 获取结果supplyAsync返回的结果的引用
        StringBuilder result = null;
        try {
            result = completableFuture.get();
        } catch (Exception e) {
            System.err.println(Thread.currentThread().getName() + "\t感知到了异步任务抛出的异常! " + e.getMessage());
        }
        System.out.println(Thread.currentThread().getName() + "\t" + result);
    }
    
    /**
     * 测试： 当原线程执行到whenComplete时， 若future已经执行完成了，那么将会由原线程(这里为main线程)执行action的逻辑。
     *
     * 输出:
     *  main	JustryDeng~
     */
    @Test
    public void whenCompleteTest11() throws Exception {
        Supplier<String> mySupplier = () -> "JustryDeng~";
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(mySupplier);
        
        TimeUnit.SECONDS.sleep(1);
        
        BiConsumer<String, Throwable> action = (String x, Throwable y) -> {
            System.out.println(Thread.currentThread().getName() + "\t" + x);
        };
        CompletableFuture<String> futureB = futureA.whenComplete(action);
        TimeUnit.SECONDS.sleep(1);
    }
    
    /**
     * 测试： 当原线程执行到whenComplete时， 若future已经执行完成了，那么将会由原线程(这里为main线程)执行action的逻辑。
     *
     * 输出:
     *  main	JustryDeng
     */
    @Test
    public void whenCompleteTest12() throws Exception {
        BiConsumer<String, Throwable> action = (String x, Throwable y) -> {
            System.out.println(Thread.currentThread().getName() + "\t" + x);
        };
        CompletableFuture<String> future = CompletableFuture.completedFuture("JustryDeng").whenComplete(action);
        TimeUnit.SECONDS.sleep(1);
    }
    
    /**
     * public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action):
     * (由默认的线程池分配线程来)无消费返回值的消费前面future的异步结果。
     *
     * 注: 无消费返回值、有future返回值
     * 注: 若异步执行CompletableFuture逻辑的线程是a, 那么在a计算完异步逻辑后，接着由默认线程池ForkJoinPool分配一个线程来进行whenComplete逻辑。
     *     追注: 因为是由线程池分配的线程, (如果执行异步逻辑的线程在执行完异步逻辑后就空闲了,那么)分配的这个执行whenCompleteAsync逻辑线程有(与
     *           执行异步逻辑的线程)是同一线程的可能。
     * 注: 其余说明与{@link this#whenCompleteTest1}同。
     *
     * 提示: 由于没有并发抢占线程，所以本方法测试出来的效果其实是 supplyAsync与whenCompleteAsync用了同一个线程，
     *       不过为了让读者更容易理解，所以本人手动将输出结果里的whenCompleteAsync改为了ForkJoinPool.commonPool-worker-2。
     * 不抛异常时-(本次)输出:
     * ForkJoinPool.commonPool-worker-1	 supplier
     * ForkJoinPool.commonPool-worker-2	JustryDeng~
     * ForkJoinPool.commonPool-worker-2	JustryDeng~qwer~
     * ForkJoinPool.commonPool-worker-2	null
     * main	JustryDeng~qwer~
     *
     * 抛出异常时-(本次)输出:
     * ForkJoinPool.commonPool-worker-1	 supplier
     * ForkJoinPool.commonPool-worker-2	null
     * ForkJoinPool.commonPool-worker-2	java.util.concurrent.CompletionException: java.lang.UnsupportedOperationException: 异常了~
     * main	感知到了异步任务抛出的异常! java.lang.UnsupportedOperationException: 异常了~
     * main	null
     */
    @Test
    public void whenCompleteTest2() throws Exception {
        Supplier<StringBuilder> mySupplier = () -> {
            System.out.println(Thread.currentThread().getName() + "\t supplier");
            // 随机抛出异常, 测试BiConsumer<? super T, ? super Throwable>中的Throwable
            if (ThreadLocalRandom.current().nextBoolean()) {
                throw new UnsupportedOperationException("异常了~");
            }
            return new StringBuilder("JustryDeng~");
        };
        
        BiConsumer<StringBuilder, Throwable> action = (StringBuilder x, Throwable y) -> {
            System.out.println(Thread.currentThread().getName() + "\t" + x);
            if (x != null) {
                x.append("qwer~");
                System.out.println(Thread.currentThread().getName() + "\t" + x);
            }
            System.out.println(Thread.currentThread().getName() + "\t" + y);
        };
        // whenComplete
        CompletableFuture<StringBuilder> completableFuture = CompletableFuture
                .supplyAsync(mySupplier)
                .whenCompleteAsync(action);
        // 获取结果supplyAsync返回的结果的引用
        StringBuilder result = null;
        try {
            result = completableFuture.get();
        } catch (Exception e) {
            System.err.println(Thread.currentThread().getName() + "\t感知到了异步任务抛出的异常! " + e.getMessage());
        }
        System.out.println(Thread.currentThread().getName() + "\t" + result);
    }
    
    /**
     * public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action):
     * (由指定的线程池分配线程来)无消费返回值的消费前面future的异步结果。
     * 注: 无消费返回值、有future返回值。
     * 注: 其余说明与{@link this#whenCompleteTest2}同。
     *
     * 不抛异常时-(本次)输出:
     * ForkJoinPool.commonPool-worker-1	 supplier
     * pool-1-thread-1	JustryDeng~
     * pool-1-thread-1	JustryDeng~qwer~
     * pool-1-thread-1	null
     * main	JustryDeng~qwer~
     *
     * 抛出异常时-(本次)输出:
     * ForkJoinPool.commonPool-worker-1	 supplier
     * pool-1-thread-1	null
     * pool-1-thread-1	java.util.concurrent.CompletionException: java.lang.UnsupportedOperationException: 异常了~
     * main	感知到了异步任务抛出的异常! java.lang.UnsupportedOperationException: 异常了~
     * main	null
     */
    @Test
    public void whenCompleteTest3() throws Exception {
        Executor myExecutor = Executors.newFixedThreadPool(3);
        Supplier<StringBuilder> mySupplier = () -> {
            System.out.println(Thread.currentThread().getName() + "\t supplier");
            // 随机抛出异常, 测试BiConsumer<? super T, ? super Throwable>中的Throwable
            if (ThreadLocalRandom.current().nextBoolean()) {
                throw new UnsupportedOperationException("异常了~");
            }
            return new StringBuilder("JustryDeng~");
        };
        
        BiConsumer<StringBuilder, Throwable> action = (StringBuilder x, Throwable y) -> {
            System.out.println(Thread.currentThread().getName() + "\t" + x);
            if (x != null) {
                x.append("qwer~");
                System.out.println(Thread.currentThread().getName() + "\t" + x);
            }
            System.out.println(Thread.currentThread().getName() + "\t" + y);
        };
        // whenComplete
        CompletableFuture<StringBuilder> completableFuture = CompletableFuture
                .supplyAsync(mySupplier)
                .whenCompleteAsync(action, myExecutor);
        // 获取结果supplyAsync返回的结果的引用
        StringBuilder result = null;
        try {
            result = completableFuture.get();
        } catch (Exception e) {
            System.err.println(Thread.currentThread().getName() + "\t感知到了异步任务抛出的异常! " + e.getMessage());
        }
        System.out.println(Thread.currentThread().getName() + "\t" + result);
    }
    
    
    /// ********************************************************************* runAfterEither、runAfterEitherAsync
    
    /**
     * public CompletableFuture<Void> runAfterEither(CompletionStage<?> other, Runnable action):
     * this和other任意一个执行完毕时，(由执行异步任务最快的那个线程来)执行action。
     * 注: 无消费返回值、无future返回值。
     * 注: 采用的是默认的线程池ForkJoinPool.commonPool。
     * 注: 哪个异步任务先执行完， 就用那个对应的线程来执行action。
     *     即: 假设执行this异步逻辑的线程是xyz, 执行other的异步逻辑的线程是abc, 若other先执行完， 那么由abc线程执行action。
     *     追注: 若执行到runAfterEither时, 两个异步任务全部都已经执行完成了， 那么将会由main线程来执行action的逻辑。
     *           所以说，总体来讲runAfterEither方法是异步的， 但是在某些特殊情况下，会智能的自动转换为同步。
     *
     * (当注释掉TimeUnit.SECONDS.sleep(4)时，)输出:
     * ForkJoinPool.commonPool-worker-1	 a
     * ForkJoinPool.commonPool-worker-2	 b
     * main	 main
     * ForkJoinPool.commonPool-worker-2	 action
     *
     * (当启用TimeUnit.SECONDS.sleep(4)时，)输出:
     * ForkJoinPool.commonPool-worker-1	 a
     * ForkJoinPool.commonPool-worker-2	 b
     * main	 action
     * main	 main
     */
    @Test
    public void runAfterEitherTest1() throws Exception {
        CompletableFuture<Void> a = CompletableFuture.runAsync(() -> {
            System.err.println(Thread.currentThread().getName() + "\t a");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    
        CompletableFuture<Void> b = CompletableFuture.runAsync(() -> {
            System.err.println(Thread.currentThread().getName() + "\t b");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        // 测试当 执行到runAfterEither时， 若两个异步逻辑全部都已经执行完毕时， 那么使用main线程执行
        // TimeUnit.SECONDS.sleep(4);
        a.runAfterEither(b, () -> System.err.println(Thread.currentThread().getName() + "\t action"));
        System.err.println(Thread.currentThread().getName() + "\t main");
        TimeUnit.SECONDS.sleep(4);
    }
    
    /**
     * public CompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action):
     * this和other任意一个执行完毕时，(由默认的线程池ForkJoinPool.commonPool分配线程来)执行action。
     * 注: 无消费返回值、无future返回值。
     * 注: 与{@link this#runAfterEitherTest1}不同的是， 即便执行到runAfterEitherAsync时, 两个异步任务全部都已经执行完成了，
     *     那么仍然会由默认的线程池ForkJoinPool.commonPool来分配线程执行action的逻辑。
     *     其它的与{@link this#runAfterEitherTest1}同。
     *
     * 输出:
     * ForkJoinPool.commonPool-worker-1	 a
     * ForkJoinPool.commonPool-worker-2	 b
     * ForkJoinPool.commonPool-worker-2	 c
     * main	 main
     * ForkJoinPool.commonPool-worker-3	 action
     */
    @Test
    public void runAfterEitherAsyncTest1() throws Exception {
        CompletableFuture<Void> a = CompletableFuture.runAsync(() -> {
            System.err.println(Thread.currentThread().getName() + "\t a");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    
        CompletableFuture<Void> b = CompletableFuture.runAsync(() -> {
            System.err.println(Thread.currentThread().getName() + "\t b");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        // ---------------------- 这一块儿的逻辑，是为了让c占用任务b释放出来的线程， 进而使我们能直观的看到runAfterEitherAsync用的线程时其它的线程
        TimeUnit.SECONDS.sleep(1);
        CompletableFuture<Void> c = CompletableFuture.runAsync(() -> {
            System.err.println(Thread.currentThread().getName() + "\t c");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        // ----------------------
        a.runAfterEitherAsync(b, () -> System.err.println(Thread.currentThread().getName() + "\t action"));
        System.err.println(Thread.currentThread().getName() + "\t main");
        TimeUnit.SECONDS.sleep(4);
    }
    
    /**
     * public CompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> other,
     *                                                        Runnable action,
     *                                                        Executor executor):
     * this和other任意一个执行完毕时，(由指定的线程池executor分配线程来)执行action。
     *
     * 注: 无消费返回值、无future返回值。
     * 注: 与{@link this#runAfterEitherTest1}不同的是， 即便执行到runAfterEitherAsync时, 两个异步任务全部都已经执行完成了，
     *     那么仍然会由指定的线程池来分配线程执行action的逻辑。
     *     其它的与{@link this#runAfterEitherTest1}同。
     *
     * 输出:
     * ForkJoinPool.commonPool-worker-1	 a
     * ForkJoinPool.commonPool-worker-1	 b
     * main	 main
     * pool-1-thread-1	 action
     */
    @Test
    public void runAfterEitherAsyncTest2() throws Exception {
        CompletableFuture<Void> a = CompletableFuture.runAsync(() ->
            System.err.println(Thread.currentThread().getName() + "\t a")
        );
        CompletableFuture<Void> b = CompletableFuture.runAsync(() ->
                System.err.println(Thread.currentThread().getName() + "\t b")
        );
        Executor myExecutor = Executors.newFixedThreadPool(3);
        a.runAfterEitherAsync(b, () -> System.err.println(Thread.currentThread().getName() + "\t action"), myExecutor);
        System.err.println(Thread.currentThread().getName() + "\t main");
        TimeUnit.SECONDS.sleep(4);
    }
    
    /// ********************************************************************* runAfterBoth、runAfterBothAsync
    
    /**
     * public CompletableFuture<Void> runAfterBoth(CompletionStage<?> other,
     *                                                 Runnable action):
     * this和other全部都执行完毕时，(由最后执行完异步任务的那个线程来)执行action。
     *
     * 注: 无消费返回值、无future返回值。
     * 注: 采用的是默认的线程池ForkJoinPool.commonPool。
     * 注: 哪个异步任务最后执行完异步逻辑， 就用那个对应的线程来执行action。
     *     即: 假设执行this异步逻辑的线程是xyz, 执行other的异步逻辑的线程是abc, 若other后执行完， 那么由abc线程执行action。
     *     追注: 若执行到runAfterBoth时, 两个异步任务全部都已经执行完成了， 那么将会由main线程来执行action的逻辑。
     *           所以说，总体来讲runAfterBoth方法是异步的， 但是在某些特殊情况下，会智能的自动转换为同步。
     *
     * (当注释掉TimeUnit.SECONDS.sleep(4)时，)输出:
     * ForkJoinPool.commonPool-worker-1	 a
     * ForkJoinPool.commonPool-worker-2	 b
     * main	 main
     * ForkJoinPool.commonPool-worker-2	 action
     *
     * (当启用TimeUnit.SECONDS.sleep(4)时，)输出:
     * ForkJoinPool.commonPool-worker-1	 a
     * ForkJoinPool.commonPool-worker-2	 b
     * main	 action
     * main	 main
     */
    @Test
    public void runAfterBothTest1() throws Exception {
        CompletableFuture<Void> a = CompletableFuture.runAsync(() -> {
            System.err.println(Thread.currentThread().getName() + "\t a");
            try {
                TimeUnit.SECONDS.sleep(1);
                // 用于测试: 由最后执行完异步任务的那个线程执行action
                // TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        CompletableFuture<Void> b = CompletableFuture.runAsync(() -> {
            System.err.println(Thread.currentThread().getName() + "\t b");
            try {
                TimeUnit.SECONDS.sleep(2);
                // 用于测试: 由最后执行完异步任务的那个线程执行action
                // TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        // 测试当 执行到runAfterBoth时， 若两个异步逻辑全部都已经执行完毕时， 那么使用main线程执行
        // TimeUnit.SECONDS.sleep(4);
        a.runAfterBoth(b, () -> System.err.println(Thread.currentThread().getName() + "\t action"));
        System.err.println(Thread.currentThread().getName() + "\t main");
        TimeUnit.SECONDS.sleep(4);
    }
    
    /**
     * public CompletableFuture<Void> runAfterBothAsync(CompletionStage<?> other,
     *                                                      Runnable action):
     * this和other全部都执行完毕时，(由默认的线程池ForkJoinPool.commonPool分配线程来)执行action。
     *
     * 注: 无消费返回值、无future返回值。
     * 注: 与{@link this#runAfterBothTest1}不同的是， 即便执行到runAfterBothAsync时, 两个异步任务全部都已经执行完成了，
     *     那么仍然会由默认的线程池ForkJoinPool.commonPool来分配线程执行action的逻辑。
     *     其它的与{@link this#runAfterBothTest1}同。
     *
     * 输出:
     * ForkJoinPool.commonPool-worker-1	 a
     * ForkJoinPool.commonPool-worker-2	 b
     * main	 main
     * ForkJoinPool.commonPool-worker-2	 action
     */
    @Test
    public void runAfterBothAsyncTest1() throws Exception {
        CompletableFuture<Void> a = CompletableFuture.runAsync(() -> {
            System.err.println(Thread.currentThread().getName() + "\t a");
        });
        
        CompletableFuture<Void> b = CompletableFuture.runAsync(() -> {
            System.err.println(Thread.currentThread().getName() + "\t b");
        });
    
        TimeUnit.SECONDS.sleep(4);
        a.runAfterBothAsync(b, () -> System.err.println(Thread.currentThread().getName() + "\t action"));
        System.err.println(Thread.currentThread().getName() + "\t main");
        TimeUnit.SECONDS.sleep(4);
    }
    
    /**
     * public CompletableFuture<Void> runAfterBothAsync(CompletionStage<?> other,
     *                                                      Runnable action,
     *                                                      Executor executor):
     * this和other全部都执行完毕时，(由指定的线程池executor分配线程来)执行action。
     *
     * 注: 无消费返回值、无future返回值。
     * 注: 与{@link this#runAfterBothTest1}不同的是， 即便执行到runAfterBothAsync时, 两个异步任务全部都已经执行完成了，
     *     那么仍然会由指定的线程池executor来分配线程执行action的逻辑。
     *     其它的与{@link this#runAfterBothTest1}同。
     *
     * 输出:
     * ForkJoinPool.commonPool-worker-1	 a
     * ForkJoinPool.commonPool-worker-1	 b
     * main	 main
     * pool-1-thread-1	 action
     */
    @Test
    public void runAfterBothAsyncTest2() throws Exception {
        CompletableFuture<Void> a = CompletableFuture.runAsync(() ->
                System.err.println(Thread.currentThread().getName() + "\t a")
        );
        CompletableFuture<Void> b = CompletableFuture.runAsync(() ->
                System.err.println(Thread.currentThread().getName() + "\t b")
        );
        Executor myExecutor = Executors.newFixedThreadPool(3);
        a.runAfterBothAsync(b, () -> System.err.println(Thread.currentThread().getName() + "\t action"), myExecutor);
        System.err.println(Thread.currentThread().getName() + "\t main");
        TimeUnit.SECONDS.sleep(4);
    }
    
    
    /// ********************************************************************* acceptEither、acceptEitherAsync
    /**
     * public CompletableFuture<Void> acceptEither(
     *         CompletionStage<? extends T> other, Consumer<? super T> action):
     * this和other谁先执行完，就以谁的结果作为入参，(同时以其所在线程作为执行线程)执行action。
     *
     * 注: 无消费返回值、无future返回值。
     * 注: 假设执行this异步逻辑的线程是xyz，执行结果是foo；执行other的异步逻辑的线程是abc，执行结果是zoo. 若other先执行完，
     *     那么以zoo作为入参，由abc线程执行action。
     *     追注: 若原线程执行到acceptEither时, 两个异步任务全部都已经执行完成了， 那么将会由原线程(这里为main线程)来执行action的逻辑。
     *           此时，假设调用是这么调用的xxx.acceptEither(yyy, function)，那么会选择异步逻辑xxx的返回结果作为action的入参。
     *           所以说，总体来讲acceptEither方法是异步的， 但是在某些特殊情况下，会智能的自动转换为同步。
     *
     *
     * (当注释掉TimeUnit.SECONDS.sleep(4)时，)输出:
     * ForkJoinPool.commonPool-worker-1	 a
     * ForkJoinPool.commonPool-worker-2	 b
     * main	 main
     * ForkJoinPool.commonPool-worker-2	 function	b
     *
     * (当启用TimeUnit.SECONDS.sleep(4)时，)输出:
     * ForkJoinPool.commonPool-worker-1	 a
     * ForkJoinPool.commonPool-worker-2	 b
     * main	 function	b
     * main	 main
     */
    @Test
    public void acceptEitherTest1() throws Exception {
        CompletableFuture<String> a = CompletableFuture.supplyAsync(() -> {
            System.err.println(Thread.currentThread().getName() + "\t a");
            try {
                TimeUnit.SECONDS.sleep(2);
                // 用于测试a比b快
                //TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "a";
        });
    
        CompletableFuture<String> b = CompletableFuture.supplyAsync(() -> {
            System.err.println(Thread.currentThread().getName() + "\t b");
            try {
                TimeUnit.SECONDS.sleep(1);
                // 用于测试a比b快
                //TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "b";
        });
        Consumer<String> consumer = x -> System.err.println(Thread.currentThread().getName() + "\t function\t" + x);
        // TimeUnit.SECONDS.sleep(4);
        a.acceptEither(b, consumer);
        System.err.println(Thread.currentThread().getName() + "\t main");
        TimeUnit.SECONDS.sleep(4);
    }
    
    /**
     * 测试: 若先完成的这个属于异常完成， 那么是不会走consumer的逻辑的， 一场会继续往外传递
     *
     * 输出:
     * ForkJoinPool.commonPool-worker-1	 a
     * ForkJoinPool.commonPool-worker-2	 b
     */
    @Test
    public void acceptEitherTest11() throws Exception {
        CompletableFuture<String> a = CompletableFuture.supplyAsync(() -> {
            System.err.println(Thread.currentThread().getName() + "\t a");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int b = 1 / 0;
            return "a";
        });
        
        CompletableFuture<String> b = CompletableFuture.supplyAsync(() -> {
            System.err.println(Thread.currentThread().getName() + "\t b");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "b";
        });
        Consumer<String> consumer = x -> System.err.println(Thread.currentThread().getName() + "\t function\t" + x);
        a.acceptEither(b, consumer);
        TimeUnit.SECONDS.sleep(4);
    }
    
    /**
     * public CompletableFuture<Void> acceptEitherAsync(
     *         CompletionStage<? extends T> other, Consumer<? super T> action):
     * this和other谁先执行完，就以谁的结果作为入参，(同时以默认的线程池ForkJoinPool.commonPool分配的线程，来)执行action。
     *
     * 注: 无消费返回值、无future返回值。
     * 注: 假设执行this异步逻辑的线程是xyz，执行结果是foo；执行other的异步逻辑的线程是abc，执行结果是zoo. 若other先执行完，
     *     那么以zoo作为入参，由abc线程执行action。
     *     追注: 若执行到acceptEitherAsync时, 即便两个异步任务全部都已经执行完成了， 那么仍然还是会由默认的
     *           线程池ForkJoinPool.commonPool分配线程来执行action的逻辑。此时，假设调用是这么调用的
     *           xxx.acceptEitherAsync(yyy, function)，那么会选择异步逻辑xxx的返回结果作为fn的入参。
     *
     *
     * (当注释掉TimeUnit.SECONDS.sleep(4)时，)输出:
     * ForkJoinPool.commonPool-worker-1	 a
     * ForkJoinPool.commonPool-worker-2	 b
     * main	 main
     * ForkJoinPool.commonPool-worker-2	 function	b
     *
     * (当启用TimeUnit.SECONDS.sleep(4)时，)输出:
     * ForkJoinPool.commonPool-worker-1	 a
     * ForkJoinPool.commonPool-worker-2	 b
     * main	 main
     * ForkJoinPool.commonPool-worker-1	 function	b
     */
    @Test
    public void acceptEitherAsyncTest1() throws Exception {
        CompletableFuture<String> a = CompletableFuture.supplyAsync(() -> {
            System.err.println(Thread.currentThread().getName() + "\t a");
            try {
                TimeUnit.SECONDS.sleep(2);
                // 用于测试a比b快
                //TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "a";
        });
        
        CompletableFuture<String> b = CompletableFuture.supplyAsync(() -> {
            System.err.println(Thread.currentThread().getName() + "\t b");
            try {
                TimeUnit.SECONDS.sleep(1);
                // 用于测试a比b快
                //TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "b";
        });
        Consumer<String> consumer = x -> System.err.println(Thread.currentThread().getName() + "\t function\t" + x);
        // TimeUnit.SECONDS.sleep(4);
        b.acceptEitherAsync(a, consumer);
        System.err.println(Thread.currentThread().getName() + "\t main");
        TimeUnit.SECONDS.sleep(4);
    }
    
    /**
     * public CompletableFuture<Void> acceptEitherAsync(
     *         CompletionStage<? extends T> other, Consumer<? super T> action, Executor executor):
     * this和other谁先执行完，就以谁的结果作为入参，(同时以指定的线程池executor分配的线程，来)执行action。
     *
     * 注: 无消费返回值、无future返回值。
     * 注: 假设执行this异步逻辑的线程是xyz，执行结果是foo；执行other的异步逻辑的线程是abc，执行结果是zoo. 若other先执行完，
     *     那么以zoo作为入参，由abc线程执行action。
     *     追注: 若执行到acceptEitherAsync时, 即便两个异步任务全部都已经执行完成了， 那么仍然还是会由指定的
     *           线程池分配线程来执行fn的逻辑。此时，假设调用是这么调用的
     *           xxx.applyToEitherAsync(yyy, function)，那么会选择异步逻辑xxx的返回结果作为fn的入参。
     *
     *
     * (当注释掉TimeUnit.SECONDS.sleep(4)时，)输出:
     * ForkJoinPool.commonPool-worker-1	 a
     * ForkJoinPool.commonPool-worker-2	 b
     * main	 main
     * pool-1-thread-1	 function	b
     *
     * (当启用TimeUnit.SECONDS.sleep(4)时，)输出:
     * ForkJoinPool.commonPool-worker-1	 a
     * ForkJoinPool.commonPool-worker-2	 b
     * main	 main
     * pool-1-thread-1	 function	b
     */
    @Test
    public void acceptEitherAsyncTest2() throws Exception {
        Executor myExecutor = Executors.newFixedThreadPool(3);
        CompletableFuture<String> a = CompletableFuture.supplyAsync(() -> {
            System.err.println(Thread.currentThread().getName() + "\t a");
            try {
                TimeUnit.SECONDS.sleep(2);
                // 用于测试a比b快
                //TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "a";
        });
        
        CompletableFuture<String> b = CompletableFuture.supplyAsync(() -> {
            System.err.println(Thread.currentThread().getName() + "\t b");
            try {
                TimeUnit.SECONDS.sleep(1);
                // 用于测试a比b快
                //TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "b";
        });
        Consumer<String> consumer = x -> System.err.println(Thread.currentThread().getName() + "\t function\t" + x);
        // TimeUnit.SECONDS.sleep(4);
        b.acceptEitherAsync(a, consumer, myExecutor);
        System.err.println(Thread.currentThread().getName() + "\t main");
        TimeUnit.SECONDS.sleep(4);
    }
    
    /// ********************************************************************* applyToEither、applyToEitherAsync(注: 与acceptEither、acceptEitherAsync不同的是， Function有返回结果)
    
    /**
     * public <U> CompletableFuture<U> applyToEither(CompletionStage<? extends T> other, Function<? super T, U> fn):
     * this和other谁先执行完，就以谁的结果作为入参，(同时以其所在线程作为执行线程)执行fn。
     *
     * 注: 有消费返回值、有future返回值。
     * 注: 假设执行this异步逻辑的线程是xyz，执行结果是foo；执行other的异步逻辑的线程是abc，执行结果是zoo. 若other先执行完，
     *     那么以zoo作为入参，由abc线程执行fn。
     *     追注: 若原线程执行到applyToEither时, 两个异步任务全部都已经执行完成了， 那么将会由原线程(这里为main线程)来执行fn的逻辑。
     *           此时，假设调用是这么调用的xxx.applyToEither(yyy, function)，那么会选择异步逻辑xxx的返回结果作为fn的入参。
     *           所以说，总体来讲applyToEither方法是异步的， 但是在某些特殊情况下，会智能的自动转换为同步。
     *
     *
     * (当注释掉TimeUnit.SECONDS.sleep(4)时，)输出:
     * ForkJoinPool.commonPool-worker-1	 a
     * ForkJoinPool.commonPool-worker-2	 b
     * main	 main
     * ForkJoinPool.commonPool-worker-2	 function	b
     *
     * (当启用TimeUnit.SECONDS.sleep(4)时，)输出:
     * ForkJoinPool.commonPool-worker-1	 a
     * ForkJoinPool.commonPool-worker-2	 b
     * main	 function	a
     * main	 main
     */
    @Test
    public void applyToEitherTest1() throws Exception {
        CompletableFuture<String> a = CompletableFuture.supplyAsync(() -> {
            System.err.println(Thread.currentThread().getName() + "\t a");
            try {
                TimeUnit.SECONDS.sleep(2);
                // 用于测试a比b快
                //TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "a";
        });
        
        CompletableFuture<String> b = CompletableFuture.supplyAsync(() -> {
            System.err.println(Thread.currentThread().getName() + "\t b");
            try {
                TimeUnit.SECONDS.sleep(1);
                // 用于测试a比b快
                //TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "b";
        });
        Function<String, String> function = x -> {
            System.err.println(Thread.currentThread().getName() + "\t function\t" + x);
            return x + ThreadLocalRandom.current().nextInt(1000);
        };
         TimeUnit.SECONDS.sleep(4);
        CompletableFuture<String> result = a.applyToEither(b, function);
        System.err.println(Thread.currentThread().getName() + "\t main");
        TimeUnit.SECONDS.sleep(4);
    }
    
    /**
     * public <U> CompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn):
     * this和other谁先执行完，就以谁的结果作为入参，(同时以默认的线程池ForkJoinPool.commonPool分配的线程，来)执行fn。
     *
     * 注: 有消费返回值、有future返回值。
     * 注: 假设执行this异步逻辑的线程是xyz，执行结果是foo；执行other的异步逻辑的线程是abc，执行结果是zoo. 若other先执行完，
     *     那么以zoo作为入参，由abc线程执行fn。
     *     追注: 若执行到applyToEitherAsync时, 即便两个异步任务全部都已经执行完成了， 那么仍然还是会由默认的
     *           线程池ForkJoinPool.commonPool分配线程来执行fn的逻辑。此时，假设调用是这么调用的
     *           xxx.applyToEitherAsync(yyy, function)，那么会选择异步逻辑xxx的返回结果作为fn的入参。
     *
     *
     * (当注释掉TimeUnit.SECONDS.sleep(4)时，)输出:
     * ForkJoinPool.commonPool-worker-1	 a
     * ForkJoinPool.commonPool-worker-2	 b
     * main	 main
     * ForkJoinPool.commonPool-worker-2	 function	b
     *
     * (当启用TimeUnit.SECONDS.sleep(4)时，)输出:
     * ForkJoinPool.commonPool-worker-1	 a
     * ForkJoinPool.commonPool-worker-2	 b
     * main	 main
     * ForkJoinPool.commonPool-worker-1	 function	b
     */
    @Test
    public void applyToEitherAsyncTest1() throws Exception {
        CompletableFuture<String> a = CompletableFuture.supplyAsync(() -> {
            System.err.println(Thread.currentThread().getName() + "\t a");
            try {
                TimeUnit.SECONDS.sleep(2);
                // 用于测试a比b快
                //TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "a";
        });
        
        CompletableFuture<String> b = CompletableFuture.supplyAsync(() -> {
            System.err.println(Thread.currentThread().getName() + "\t b");
            try {
                TimeUnit.SECONDS.sleep(1);
                // 用于测试a比b快
                //TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "b";
        });
        Function<String, String> function = x -> {
            System.err.println(Thread.currentThread().getName() + "\t function\t" + x);
            return x + ThreadLocalRandom.current().nextInt(1000);
        };
        // TimeUnit.SECONDS.sleep(4);
        CompletableFuture<String> result = b.applyToEitherAsync(a, function);
        System.err.println(Thread.currentThread().getName() + "\t main");
        TimeUnit.SECONDS.sleep(4);
    }
    
    /**
     * public <U> CompletableFuture<U> applyToEitherAsync(
     *         CompletionStage<? extends T> other, Function<? super T, U> fn, Executor executor):
     * this和other谁先执行完，就以谁的结果作为入参，(同时以指定的线程池executor分配的线程，来)执行fn。
     *
     * 注: 有消费返回值、有future返回值。
     * 注: 假设执行this异步逻辑的线程是xyz，执行结果是foo；执行other的异步逻辑的线程是abc，执行结果是zoo. 若other先执行完，
     *     那么以zoo作为入参，由abc线程执行fn。
     *     追注: 若执行到applyToEitherAsync时, 即便两个异步任务全部都已经执行完成了， 那么仍然还是会由指定的
     *           线程池分配线程来执行fn的逻辑。此时，假设调用是这么调用的
     *           xxx.applyToEitherAsync(yyy, function)，那么会选择异步逻辑xxx的返回结果作为fn的入参。
     *
     *
     * (当注释掉TimeUnit.SECONDS.sleep(4)时，)输出:
     * ForkJoinPool.commonPool-worker-1	 a
     * ForkJoinPool.commonPool-worker-2	 b
     * main	 main
     * pool-1-thread-1	 function	b
     *
     * (当启用TimeUnit.SECONDS.sleep(4)时，)输出:
     * ForkJoinPool.commonPool-worker-1	 a
     * ForkJoinPool.commonPool-worker-2	 b
     * main	 main
     * pool-1-thread-1	 function	b
     */
    @Test
    public void applyToEitherAsyncTest2() throws Exception {
        Executor myExecutor = Executors.newFixedThreadPool(3);
        CompletableFuture<String> a = CompletableFuture.supplyAsync(() -> {
            System.err.println(Thread.currentThread().getName() + "\t a");
            try {
                TimeUnit.SECONDS.sleep(2);
                // 用于测试a比b快
                //TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "a";
        });
        
        CompletableFuture<String> b = CompletableFuture.supplyAsync(() -> {
            System.err.println(Thread.currentThread().getName() + "\t b");
            try {
                TimeUnit.SECONDS.sleep(1);
                // 用于测试a比b快
                //TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "b";
        });
        Function<String, String> function = x -> {
            System.err.println(Thread.currentThread().getName() + "\t function\t" + x);
            return x + ThreadLocalRandom.current().nextInt(1000);
        };
         TimeUnit.SECONDS.sleep(4);
        CompletableFuture<String> result = b.applyToEitherAsync(a, function, myExecutor);
        System.err.println(Thread.currentThread().getName() + "\t main");
        TimeUnit.SECONDS.sleep(4);
    }
    
    
    
    
    /// ********************************************************************* allOf​、anyOf​
    
    /**
     * public static CompletableFuture<Void> allOf(CompletableFuture<?>... cfs):
     * 所有的cfs都完成了, this才完成。
     *
     * 注:只要有一个future异常完成， 那么allOf返回的future完成后的完成状态就属于异常完成。
     *    即: 只有所有的future全部都正常完成， allOf返回的future完成后的完成状态才是正常完成。
     * 注: 任何一个cfs正常执行完成 或者 有异常出现导致完成， 都属于完成。 追注: 取消也属于异常完成。
     *
     * 输出:
     * ......
     * ......
     * ForkJoinPool.commonPool-worker-1	 a完成了
     * ......
     * ......
     * ForkJoinPool.commonPool-worker-2	 b完成了
     * ......
     * c完成了
     */
    @Test
    public void allOfTest1() throws Exception {
        CompletableFuture<Void> a = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.err.println(Thread.currentThread().getName() + "\t a完成了");
            // 测试异常也算完成
            // int da = 1 /0;
        });
        
        CompletableFuture<Void> b = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 测试异常也算完成
            // int da = 1 /0;
            System.err.println(Thread.currentThread().getName() + "\t b完成了");
        });
        CompletableFuture<Void> c = CompletableFuture.allOf(a, b);
        
        while (!c.isDone()) {
            TimeUnit.MILLISECONDS.sleep(400);
            System.err.println("......");
        }
        System.err.println("c完成了");
    }
    
    /**
     * 验证: 只要有一个future异常完成， 那么allOf返回的future完成后的完成状态就属于异常完成。
     *      即: 只有所有的future全部都正常完成， allOf返回的future完成后的完成状态才是正常完成。
     *
     *
     * (注释A、B处)输出:
     * ForkJoinPool.commonPool-worker-1	 b完成了
     * a的完成状态	java.util.concurrent.CompletableFuture@70f02c32[Completed normally]
     * b的完成状态	java.util.concurrent.CompletableFuture@62010f5c[Completed normally]
     * c的完成状态	java.util.concurrent.CompletableFuture@51fadaff[Completed normally]
     *
     * (打开A处, 注释B处)输出:
     * a的完成状态	java.util.concurrent.CompletableFuture@70f02c32[Completed exceptionally]
     * b的完成状态	java.util.concurrent.CompletableFuture@62010f5c[Completed normally]
     * c的完成状态	java.util.concurrent.CompletableFuture@51fadaff[Completed exceptionally]
     *
     * (打开B处, 注释A处)输出:
     * a的完成状态	java.util.concurrent.CompletableFuture@70f02c32[Completed normally]
     * b的完成状态	java.util.concurrent.CompletableFuture@62010f5c[Completed exceptionally]
     * c的完成状态	java.util.concurrent.CompletableFuture@51fadaff[Completed exceptionally]
     *
     *
     * (打开A、B处)输出:
     * ForkJoinPool.commonPool-worker-1	 b完成了
     * a的完成状态	java.util.concurrent.CompletableFuture@70f02c32[Completed exceptionally]
     * b的完成状态	java.util.concurrent.CompletableFuture@62010f5c[Completed exceptionally]
     * c的完成状态	java.util.concurrent.CompletableFuture@51fadaff[Completed exceptionally]
     */
    @Test
    public void allOfTest2() throws Exception {
        CompletableFuture<Void> a = CompletableFuture.runAsync(() -> {
              int da = 1 /0; // A处
        });
        
        CompletableFuture<Void> b = CompletableFuture.runAsync(() -> {
             int da = 1 /0; // B处
        });
        CompletableFuture<Void> c = CompletableFuture.allOf(a, b);
    
        try {
            c.join();
        } catch (Exception e) {
            // ignore
        }
    
        System.err.println("a的完成状态\t" + a.toString());
        System.err.println("b的完成状态\t" + b.toString());
        System.err.println("c的完成状态\t" + c.toString());
    }
    
    /**
     * public static CompletableFuture<Object> anyOf(CompletableFuture<?>... cfs):
     * 只要cfs中任何一个完成了, 那么this就完成。并且this的结果与最先完成的那一个cf的结果一样。
     *
     * 注: anyOf返回的future的完成状态与第一个完成的future的完成状态一致。
     * 注: 任何一个cfs正常执行完成 或者 有异常出现导致完成， 都属于完成。 追注: 取消也属于异常完成。
     *
     * 注: 因为this的结果与最先完成的那一个cf的结果一样。所以，如果最先那个是正常完成的还好，
     *     如果最先那个是以异常完成的，那么获取this的结果时，会抛出对应的异常。
     *
     * 输出:
     * ......
     * ......
     * ForkJoinPool.commonPool-worker-1	 a完成了
     * ......
     * c完成了, c的结果是: abc
     * ForkJoinPool.commonPool-worker-2	 b完成了
     */
    @Test
    public void anyOfTest1() throws Exception {
        CompletableFuture<String> a = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.err.println(Thread.currentThread().getName() + "\t a完成了");
            /*
             * 测试:
             *     因为this的结果与最先完成的那一个cf的结果一样。所以，如果最先那个是正常
             * 完成的还好，如果最先那个是以异常完成的，那么获取this的结果时，会抛出对应的异常。
             */
             // int da = 1 /0;
             return "abc";
        });
        
        CompletableFuture<Integer> b = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.err.println(Thread.currentThread().getName() + "\t b完成了");
            /*
             * 测试:
             *     因为this的结果与最先完成的那一个cf的结果一样。所以，如果最先那个是正常
             * 完成的还好，如果最先那个是以异常完成的，那么获取this的结果时，会抛出对应的异常。
             */
            // int da = 1 /0;
            return 123;
        });
        // 测试原线程执行到anyOf时，cfs全部都已经完成了的场景;this的结果仍然与最先完成的那一个cf的结果一样。
        // TimeUnit.SECONDS.sleep(2);
        CompletableFuture<Object> c = CompletableFuture.anyOf(a, b);
        while (!c.isDone()) {
            TimeUnit.MILLISECONDS.sleep(400);
            System.err.println("......");
        }
        System.err.println("c完成了, c的结果是: " + c.get());
        TimeUnit.SECONDS.sleep(3);
    }
    
    /**
     * 验证: anyOf返回的future完成后的完成状态与第一个完成的future的完成状态一致。
     *
     * (打开A处)输出:
     * a的完成状态	java.util.concurrent.CompletableFuture@70f02c32[Completed exceptionally]
     * b的完成状态	java.util.concurrent.CompletableFuture@62010f5c[Not completed]
     * c的完成状态	java.util.concurrent.CompletableFuture@51fadaff[Completed exceptionally]
     *
     * (注释A处)输出:
     * a的完成状态	java.util.concurrent.CompletableFuture@70f02c32[Completed normally]
     * b的完成状态	java.util.concurrent.CompletableFuture@62010f5c[Not completed]
     * c的完成状态	java.util.concurrent.CompletableFuture@51fadaff[Completed normally]
     */
    @Test
    public void anyOfTest2() throws Exception {
        CompletableFuture<Void> a = CompletableFuture.runAsync(() -> {
//            int qwer = 1 / 0; // A处
        });
        
        CompletableFuture<Void> b = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        CompletableFuture<Object> c = CompletableFuture.anyOf(a, b);
        
        try {
            c.join();
        } catch (Exception e) {
            // ignore
        }
        
        System.err.println("a的完成状态\t" + a.toString());
        System.err.println("b的完成状态\t" + b.toString());
        System.err.println("c的完成状态\t" + c.toString());
    }
    
    /// ********************************************************************* cancel、isCancelled
    
    /**
     * public boolean cancel(boolean mayInterruptIfRunning): 将Future标识为cancel。
     * public boolean isCancelled(): 查看Future本身是否已取消。
     *
     * 注: 被取消了的任务(这里只要Futuren被标识为cancel，那么就视为任务被取消)，属于异常完成[Completed exceptionally]状态。
     *
     * 提示一: Future 的状态与对应线程的状态是分开的，取消状态的 Future 仅仅代表它自己的状态为已取消，与
     *         线程状态无关。一般来说，使用 Future 来实现异步操作的时候，都建议线程要响应 Future 的状态
     *         变更。如: Future标识为isCancelled后，线程对应标识为isInterrupted状态.
     *         但是这一条并不是强制的，所以就会出现Future 已经取消了，但线程却并没有响应,
     *         线程的isInterrupted()结果仍然为false的情况。
     *
     *  提示二: 若任务正处于执行中，此时对其进行cancel， 若参数为true, 那么该任务(所在的线程)会中断;
     *          若参数为true, 那么该任务(所在的线程)不会中断;
     *          但是！！！从提示一我们知道，线程不一定会鸟你的Future状态， 所以: 在某些时候，
     *          无论cancel方法的参数是true还是false, 都没什么卵用。
     *
     *  提示三: 若任务已经完成， 那么cancel和isCancelled都会返回false.
     *
     *  提示四: 若任务还在排队等待执行，此时对其cancel的话(此时参数传true或false都一样)，那么任务是会撤销成功的。
     *
     * (打开第一处， 关闭第二处时， 打开第三处时)输出:             验证提示一、提示三
     * futureA => pool-1-thread-1	 0	false
     * futureA => pool-1-thread-1	 1	false
     * futureA => pool-1-thread-1	 2	false
     * futureA => pool-1-thread-1	 3	false
     * futureA => pool-1-thread-1	 4	false
     * futureA => pool-1-thread-1	 5	false
     * futureB => pool-1-thread-1	 0	false
     * futureB => pool-1-thread-1	 1	false
     * futureB => pool-1-thread-1	 2	false
     * futureA.cancel=false,	futureA.isCancelled=false
     * futureB.cancel=true,	futureB.isCancelled=true
     * futureB => pool-1-thread-1	 3	false
     * futureB => pool-1-thread-1	 4	false
     * futureB => pool-1-thread-1	 5	false
     *
     *
     * (打开第一处， 打开第二处时， 关闭第三处时)输出:         和上面的输出对比，验证提示二： 在某些时候，无论cancel方法的参数是true还是false, 都没什么卵用。
     * futureA => pool-1-thread-1	 0	false
     * futureA => pool-1-thread-1	 1	false
     * futureA => pool-1-thread-1	 2	false
     * futureA => pool-1-thread-1	 3	false
     * futureA => pool-1-thread-1	 4	false
     * futureA => pool-1-thread-1	 5	false
     * futureB => pool-1-thread-1	 0	false
     * futureB => pool-1-thread-1	 1	false
     * futureB => pool-1-thread-1	 2	false
     * futureA.cancel=false,	futureA.isCancelled=false
     * futureB.cancel=true,	futureB.isCancelled=true
     * futureB => pool-1-thread-1	 3	false
     * futureB => pool-1-thread-1	 4	false
     * futureB => pool-1-thread-1	 5	false
     *
     *
     * (关闭第一处， 打开第二处时， 关闭第三处时,  或者   关闭第一处， 关闭第二处时， 打开第三处时,)输出:      验证提示四
     * futureA => pool-1-thread-1	 0	false
     * futureA => pool-1-thread-1	 1	false
     * futureA => pool-1-thread-1	 2	false
     * futureA => pool-1-thread-1	 3	false
     * futureA.cancel=true,	futureA.isCancelled=true
     * futureB.cancel=true,	futureB.isCancelled=true
     * futureA => pool-1-thread-1	 4	false
     * futureA => pool-1-thread-1	 5	false
     */
    @Test
    public void cancelTest1() throws Exception {
        Executor myExecutor = Executors.newFixedThreadPool(1);
        CompletableFuture<Void> futureA = CompletableFuture.runAsync(() -> {
            for (int i = 0; i < 6; i++) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    Thread currentThread = Thread.currentThread();
                    System.err.println("futureA => " + currentThread.getName() + "\t " + i + "\t" + currentThread.isInterrupted());
                    
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, myExecutor);
    
        CompletableFuture<Void> futureB = CompletableFuture.runAsync(() -> {
            for (int i = 0; i < 6; i++) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    Thread currentThread = Thread.currentThread();
                    System.err.println("futureB => " + currentThread.getName() + "\t " + i + "\t" + currentThread.isInterrupted());
                
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, myExecutor);
        TimeUnit.SECONDS.sleep(2);
        // TimeUnit.SECONDS.sleep(3); // 第一处
        boolean cancelResultA = futureA.cancel(true);
        System.err.println("futureA.cancel=" + cancelResultA + ",\tfutureA.isCancelled=" + futureA.isCancelled());
        
        // 参数 true or false有的时候并没什么卵用
        // boolean cancelResultB = futureB.cancel(false);  // 第二处
        boolean cancelResultB = futureB.cancel(true);  // 第三处
        System.err.println("futureB.cancel=" + cancelResultB + ",\tfutureB.isCancelled=" + futureB.isCancelled());
    
        TimeUnit.SECONDS.sleep(5);
    }
    
    /**
     * 输出:
     * future => ForkJoinPool.commonPool-worker-1	 0	false
     * future => ForkJoinPool.commonPool-worker-1	 1	false
     * java.util.concurrent.CompletableFuture@1fa1cab1[Not completed]
     * future.cancel=true,	future.isCancelled=true,	future.isCompletedExceptionally=true
     * java.util.concurrent.CompletableFuture@1fa1cab1[Completed exceptionally]
     * future => ForkJoinPool.commonPool-worker-1	 2	false
     * future => ForkJoinPool.commonPool-worker-1	 3	false
     * future => ForkJoinPool.commonPool-worker-1	 4	false
     * future => ForkJoinPool.commonPool-worker-1	 5	false
     */
    @Test
    public void cancelTest2() throws Exception {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            for (int i = 0; i < 6; i++) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    Thread currentThread = Thread.currentThread();
                    System.err.println("future => " + currentThread.getName() + "\t " + i + "\t" + currentThread.isInterrupted());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        TimeUnit.SECONDS.sleep(1);
        System.err.println(future.toString());
        System.err.println("future.cancel=" + future.cancel(true)
                + ",\tfuture.isCancelled=" + future.isCancelled() + ",\tfuture.isCompletedExceptionally=" + future.isCompletedExceptionally());
        System.err.println(future.toString());
        TimeUnit.SECONDS.sleep(3);
        
    }
    /// ********************************************************************* toString
    /**
     * public String toString(): 输出当前对象及其完成状态
     *
     * 输出:
     * ForkJoinPool.commonPool-worker-1	 0
     * ForkJoinPool.commonPool-worker-1	 1
     * result=java.util.concurrent.CompletableFuture@1224e1b6[Not completed]
     * ForkJoinPool.commonPool-worker-1	 2
     * ForkJoinPool.commonPool-worker-1	 3
     * result=java.util.concurrent.CompletableFuture@1224e1b6[Completed normally]
     */
    @Test
    public void toStringTest1() throws Exception {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            for (int i = 0; i < 4; i++) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    Thread currentThread = Thread.currentThread();
                    System.err.println(currentThread.getName() + "\t " + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        TimeUnit.SECONDS.sleep(1);
        System.err.println("result=" + future.toString());
        TimeUnit.SECONDS.sleep(2);
        System.err.println("result=" + future.toString());
    }
    
    /// ********************************************************************* complete
    /**
     * public boolean complete(T value): 主动将当前future标识为(正常)完成状态（论证见{@link this#completeTest1}）, 并以value作为当前future的完成(返回)值（论证见{@link this#completeTest2}）。
     *                                   若complete操作成功，那么返回true, 否者返回false。
     *
     * 注: 若在进行complete操作之前，futuren就已经完成了，那么返回false. （论证见{@link this#completeTest3}）
     *
     * 输出:
     * ForkJoinPool.commonPool-worker-1	 0
     * ForkJoinPool.commonPool-worker-1	 1
     * ForkJoinPool.commonPool-worker-1	 2
     * toString() => java.util.concurrent.CompletableFuture@4ffa7041[Not completed]
     * complete(...) => true
     * toString() => java.util.concurrent.CompletableFuture@4ffa7041[Completed normally]
     * ForkJoinPool.commonPool-worker-1	 3
     * ForkJoinPool.commonPool-worker-1	 4
     * ForkJoinPool.commonPool-worker-1	 5
     * toString() => java.util.concurrent.CompletableFuture@4ffa7041[Completed normally]
     */
    @Test
    public void completeTest1() throws Exception {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            for (int i = 0; i < 6; i++) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    Thread currentThread = Thread.currentThread();
                    System.err.println(currentThread.getName() + "\t " + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        TimeUnit.SECONDS.sleep(2);
        System.err.println("toString() => " + future.toString());
        System.err.println("complete(...) => " + future.complete(null));
        System.err.println("toString() => " + future.toString());
        TimeUnit.SECONDS.sleep(2);
        System.err.println("toString() => " + future.toString());
    }
    
    /**
     * 输出:
     * ForkJoinPool.commonPool-worker-1	 0
     * ForkJoinPool.commonPool-worker-1	 1
     * ForkJoinPool.commonPool-worker-1	 2
     * toString() => java.util.concurrent.CompletableFuture@3e4afd10[Not completed]
     * complete(...) => true
     * toString() => java.util.concurrent.CompletableFuture@3e4afd10[Completed normally]
     * future.get() => JustryDeng长得超级帅
     * ForkJoinPool.commonPool-worker-1	 3
     * ForkJoinPool.commonPool-worker-1	 4
     * ForkJoinPool.commonPool-worker-1	 5
     * toString() => java.util.concurrent.CompletableFuture@3e4afd10[Completed normally]
     * future.get() => JustryDeng长得超级帅
     */
    @Test
    public void completeTest2() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            for (int i = 0; i < 6; i++) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    Thread currentThread = Thread.currentThread();
                    System.err.println(currentThread.getName() + "\t " + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "JustryDeng长得一般般蛮~";
        });
        TimeUnit.SECONDS.sleep(2);
        System.err.println("toString() => " + future.toString());
        System.err.println("complete(...) => " + future.complete("JustryDeng长得超级帅"));
        System.err.println("toString() => " + future.toString());
        System.err.println("future.get() => " + future.get());
        TimeUnit.SECONDS.sleep(2);
        System.err.println("toString() => " + future.toString());
        System.err.println("future.get() => " + future.get());
    }
    
    /**
     * 输出:
     * toString() => java.util.concurrent.CompletableFuture@20faaf77[Completed normally]
     * complete(...) => false
     * toString() => java.util.concurrent.CompletableFuture@20faaf77[Completed normally]
     * future.get() => 咿呀咔咔~
     */
    @Test
    public void completeTest3() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            return "咿呀咔咔~";
        });
        TimeUnit.SECONDS.sleep(1);
        System.err.println("toString() => " + future.toString());
        System.err.println("complete(...) => " + future.complete("哟呵1994~"));
        System.err.println("toString() => " + future.toString());
        System.err.println("future.get() => " + future.get());
    }
    
    /// ********************************************************************* completeExceptionally​
    /**
     * public boolean completeExceptionally(Throwable ex): 主动将当前future标识为(异常)完成状态（论证见{@link this#completeExceptionallyTest1}）, 并以ex
     *                                                     作为引起当前future异常完成的异常（论证见{@link this#completeExceptionallyTest2}）。
     *                                                     若completeExceptionally操作成功，那么返回true, 否者返回false。
     *
     *  注: 若在进行completeExceptionally操作之前，futuren就已经完成了，那么返回false. （论证见{@link this#completeExceptionallyTest3}）
     *
     * 输出:
     * ForkJoinPool.commonPool-worker-1	 0
     * ForkJoinPool.commonPool-worker-1	 1
     * ForkJoinPool.commonPool-worker-1	 2
     * toString() => java.util.concurrent.CompletableFuture@311a0b3e[Not completed]
     * completeExceptionally(...) => true
     * toString() => java.util.concurrent.CompletableFuture@311a0b3e[Completed exceptionally]
     * ForkJoinPool.commonPool-worker-1	 3
     * ForkJoinPool.commonPool-worker-1	 4
     * ForkJoinPool.commonPool-worker-1	 5
     * toString() => java.util.concurrent.CompletableFuture@311a0b3e[Completed exceptionally]
     */
    @Test
    public void completeExceptionallyTest1() throws Exception {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            for (int i = 0; i < 6; i++) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    Thread currentThread = Thread.currentThread();
                    System.err.println(currentThread.getName() + "\t " + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        TimeUnit.SECONDS.sleep(2);
        System.err.println("toString() => " + future.toString());
        System.err.println("completeExceptionally(...) => " + future.completeExceptionally(new UnknownError()));
        System.err.println("toString() => " + future.toString());
        TimeUnit.SECONDS.sleep(2);
        System.err.println("toString() => " + future.toString());
    }
    
    /**
     * 输出:
     * ForkJoinPool.commonPool-worker-1	 0
     * ForkJoinPool.commonPool-worker-1	 1
     * ForkJoinPool.commonPool-worker-1	 2
     * toString() => java.util.concurrent.CompletableFuture@aa0dbca[Not completed]
     * completeExceptionally(...) => true
     * toString() => java.util.concurrent.CompletableFuture@aa0dbca[Completed exceptionally]
     * ForkJoinPool.commonPool-worker-1	 3
     *
     * java.util.concurrent.ExecutionException: java.lang.UnknownError: oop-123
     *
     * 	at java.util.concurrent.CompletableFuture.reportGet(CompletableFuture.java:357)
     * 	at java.util.concurrent.CompletableFuture.get(CompletableFuture.java:1895)
     * 	(省略...)
     */
    @Test
    public void completeExceptionallyTest2() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            for (int i = 0; i < 6; i++) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    Thread currentThread = Thread.currentThread();
                    System.err.println(currentThread.getName() + "\t " + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "JustryDeng长得一般般蛮~";
        });
        TimeUnit.SECONDS.sleep(2);
        System.err.println("toString() => " + future.toString());
        System.err.println("completeExceptionally(...) => " + future.completeExceptionally(new UnknownError("oop-123")));
        System.err.println("toString() => " + future.toString());
        System.err.println("future.get() => " + future.get());
        TimeUnit.SECONDS.sleep(2);
        System.err.println("toString() => " + future.toString());
        System.err.println("future.get() => " + future.get());
    }
    
    /**
     * 输出:
     * toString() => java.util.concurrent.CompletableFuture@f48a080[Completed normally]
     * completeExceptionally(...) => false
     * toString() => java.util.concurrent.CompletableFuture@f48a080[Completed normally]
     * future.get() => 咿呀咔咔~
     */
    @Test
    public void completeExceptionallyTest3() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            return "咿呀咔咔~";
        });
        TimeUnit.SECONDS.sleep(1);
        System.err.println("toString() => " + future.toString());
        System.err.println("completeExceptionally(...) => " + future.completeExceptionally(new RuntimeException()));
        System.err.println("toString() => " + future.toString());
        System.err.println("future.get() => " + future.get());
    }
    
    
    /// ********************************************************************* completedFuture
    
    /**
     * public static <U> CompletableFuture<U> completedFuture(U value): 构造一个处于已正常完成状态的CompletableFuture， value即为完成(返回)值。
     *
     * 注: 有future返回值。
     *
     * 输出:
     * toString() => java.util.concurrent.CompletableFuture@3de8f85c[Completed normally]
     * main获取到了结果 => I am JustryDeng
     */
    @Test
    public void completedFutureTest1() throws Exception {
        CompletableFuture<String> future = CompletableFuture.completedFuture("I am JustryDeng");
        System.err.println("toString() => " + future.toString());
        String result = future.get();
        System.err.println(Thread.currentThread().getName() + "获取到了结果 => " + result);
    }
    
    
    
    /// ********************************************************************* exceptionally​
    
    /**
     * public CompletableFuture<T> exceptionally(Function<Throwable, ? extends T> fn): 当future出现异常时, (才)会走exceptionally进行补偿。
     *
     * 注: 有补充返回值、有future返回值。
     * 注: 假设在进行exceptionally前，有很多个处理节点(如: xxx0.xxx1.xxx2.xxx3.exceptionally)， 那么：
     *     以到exceptionally时的异常状况为准: 到exceptionally时， 若没有感受到异常，那么不触发exceptionally；
     *                                     到exceptionally时， 若感受到了异常，那么触发exceptionally；
     *     追注一： 假设xxx1出现了异常， xxx2没有处理异常的能力，异常继续传递， 到了xxx3， xxx3处理了异常， 那么
     *             到exceptionally时， exceptionally是感受不到异常的(因为已经被处理了)。
     *     追注: 此处可见exceptionallyTest2、exceptionallyTest3、exceptionallyTest4的示例说明。
     *
     * 输出:
     * 测试二:
     *  future正常完成时 => 咿呀咔咔~
     *  future完成状态 => java.util.concurrent.CompletableFuture@b4711e2[Completed normally]
     *
     * 测试二:
     *  进exceptionally了
     *  future1异常完成时 => java.lang.ArithmeticException: / by zero
     *  future1完成状态 => java.util.concurrent.CompletableFuture@70f02c32[Completed normally]
     */
    @Test
    public void exceptionallyTest1() throws Exception {
        
        Function<Throwable, ? extends String> fn = (Throwable th) -> {
            // 补偿逻辑
            System.err.println(" 进exceptionally了");
            return th.getMessage();
        };
        
        // 测试一：
        System.err.println("测试二:");
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            return "咿呀咔咔~";
        }).exceptionally(fn);
        TimeUnit.MILLISECONDS.sleep(100);
        System.err.println(" future正常完成时 => "+ future.get());
        System.err.println(" future完成状态 => " + future.toString());
        
        // 测试二： futurn异常完成时， 会走exceptionally中的逻辑
        System.err.println();
        System.err.println("测试二:");
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            int a = 1 / 0; // 使发生异常
            return "...";
        }).exceptionally(fn);
        TimeUnit.MILLISECONDS.sleep(100);
        System.err.println(" future1异常完成时 => "+ future1.get());
        System.err.println(" future1完成状态 => " + future1.toString());
    }
    
    
    /**
     * 声明： 这里以thenApply进行多节点连接，并进行测试。
     * 测试： 当有个多节点时， 其中某个node出问题了， 会不会被处于最后的exceptionally拦截处理
     *       即,如: xxx1.xxx2.xxx3.xxxn.exceptionally时， 若xxx2出问题，会走exceptionally的补偿逻辑吗
     *
     * （注释A、B、C、D时，）输出:
     * functionA param -> JustryDeng
     * functionB param -> JustryDeng - 0
     * functionC param -> JustryDeng - 0 - 1
     * result	JustryDeng - 0 - 1 - 2
     *
     * （打开A， 注释B、C、D时，）输出:
     *   进exceptionally了
     * result	java.lang.ArithmeticException: / by zero
     *
     * （打开B， 注释A、C、D时，）输出:
     * JustryDeng
     *  进exceptionally了
     * result	java.lang.ArithmeticException: / by zero
     *
     * （打开C， 注释A、B、D时，）输出:
     * JustryDeng
     * JustryDeng - 0
     *  进exceptionally了
     * result	java.lang.ArithmeticException: / by zero
     *
     * （打开D， 注释A、B、C时，）输出:
     * JustryDeng
     * JustryDeng - 0
     * JustryDeng - 0 - 1
     *  进exceptionally了
     * result	java.lang.ArithmeticException: / by zero
     */
    @Test
    public void exceptionallyTest2() throws Exception {
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> {
//            int a = 1 / 0; // A处
            String str = "JustryDeng";
            System.err.println(str);
            return str;
        });
        Function<String, String> functionA = (String param) -> {
//            int a = 1 / 0; // B处
            String str =  param + " - " + 0;
            System.err.println(str);
            return str;
        };
    
        Function<String, String> functionB = (String param) -> {
//            int a = 1 / 0; // C处
            String str =  param + " - " + 1;
            System.err.println(str);
            return str;
        };
    
        Function<String, String> functionC = (String param) -> {
            int a = 1 / 0; // D处
            String str =  param + " - " + 2;
            System.err.println(str);
            return str;
        };
    
        Function<Throwable, ? extends String> fn = (Throwable th) -> {
            System.err.println(" 进exceptionally了");
            return th.getMessage();
        };
    
        CompletableFuture<String> resultFuturn =
                futureA.thenApply(functionA).thenApply(functionB).thenApply(functionC).exceptionally(fn);
        System.err.println("result\t" + resultFuturn.join());
    
    }
    
    /**
     * (注释掉A处时，)输出:
     * JustryDeng
     * whenComplete感知到了值:	JustryDeng
     * result	JustryDeng
     *
     * (打开A处时，)输出:
     * whenComplete感知到了值:	null
     * whenComplete感知到了异常:	java.lang.ArithmeticException: / by zero
     *  进exceptionally了
     * result	java.lang.ArithmeticException: / by zero
     */
    @Test
    public void exceptionallyTest3() throws Exception {
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> {
            int a = 1 / 0; // A处
            String str = "JustryDeng";
            System.err.println(str);
            return str;
        });
    
        BiConsumer<String, Throwable> action = (String x, Throwable y) -> {
            System.err.println("whenComplete感知到了值:\t" + x);
            if (y != null) {
                System.err.println("whenComplete感知到了异常:\t" + y.getMessage());
            }
        };
        
        Function<Throwable, ? extends String> fn = (Throwable th) -> {
            System.err.println(" 进exceptionally了");
            return th.getMessage();
        };
        
        CompletableFuture<String> resultFuturn = futureA.whenComplete(action).exceptionally(fn);
        System.err.println("result\t" + resultFuturn.join());
    }
    
    /**
     * （打开C处，注释掉A、B处时，）输出:
     *  JustryDeng
     *  handle感知到了值	JustryDeng
     *  result	JustryDeng
     *
     *
     * （打开A、C处时，注释掉B处时，）输出:
     *  handle感知到了值	null
     *  handle感知到,并处理了异常	java.lang.ArithmeticException: / by zero
     *  result	null
     *
     * （打开A、B处时，注释掉C处时，）输出:
     * handle感知到了值	null
     *  进exceptionally了
     * result	java.lang.RuntimeException: java.util.concurrent.CompletionException: java.lang.ArithmeticException: /
     * by zero
     *
     */
    @Test
    public void exceptionallyTest4() throws Exception {
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> {
            int a = 1 / 0; // A处
            String str = "JustryDeng";
            System.err.println(str);
            return str;
        });
    
        BiFunction<String, Throwable, String> biFunction = (String param, Throwable th) -> {
            System.err.println("handle感知到了值\t" + param);
            if (th != null) {  // B处
                throw new RuntimeException(th);      // B处
            }                  // B处
//            if (th != null) {                                                     // C处
//                System.err.println("handle感知到,并处理了异常\t" + th.getMessage()); // C处
//            }                                                                     // C处
            return param;
        };
        
        Function<Throwable, ? extends String> fn = (Throwable th) -> {
            System.err.println(" 进exceptionally了");
            return th.getMessage();
        };
        
        CompletableFuture<String> resultFuturn = futureA.handle(biFunction).exceptionally(fn);
        System.err.println("result\t" + resultFuturn.join());
    }
    
    
    /// ********************************************************************* get、getNow​
    /**
     * public T get() throws InterruptedException, ExecutionException: 阻塞当前线程，直到获取到futuren的结果。
     * public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException: 阻塞当前线程，直到获取到futuren的结果 或者 直到等待超时。
     * public T getNow(T valueIfAbsent): 立马获取futuren的值， 若此时futuren尚未完成， 那么以valueIfAbsent作为缺省值进行返回。
     *
     * 注: get可以感知到futuren抛出的异常.
     * 注: 如果进行getNow浅， futurn就异常完成了， 那么getNow也是能感知到其抛出的异常的。
     *
     * 输出:
     * main	 toString() => java.util.concurrent.CompletableFuture@2796aeae[Not completed]
     * ForkJoinPool.commonPool-worker-1	 0
     * ForkJoinPool.commonPool-worker-1	 1
     * ForkJoinPool.commonPool-worker-1	 2
     * main	 get() => 邓沙利文
     * main	 toString() => java.util.concurrent.CompletableFuture@2796aeae[Completed normally]
     */
    @Test
    public void getTest1() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            for (int i = 0; i < 3; i++) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    Thread currentThread = Thread.currentThread();
                    System.err.println(currentThread.getName() + "\t " + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "邓沙利文";
        });
        System.err.println(Thread.currentThread().getName() + "\t toString() => " + future.toString());
        System.err.println(Thread.currentThread().getName() + "\t get() => " + future.get());
        System.err.println(Thread.currentThread().getName() + "\t toString() => " + future.toString());
    }
    
    /**
     * 输出:
     * main	 toString() => java.util.concurrent.CompletableFuture@2796aeae[Not completed]
     * main	 get()感知到异常了 => class java.util.concurrent.ExecutionException	java.lang.ArithmeticException: / by zero
     * main	 toString() => java.util.concurrent.CompletableFuture@2796aeae[Completed exceptionally]
     */
    @Test
    public void getTest2() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            int a = 1 / 0;
            return "邓沙利文";
        });
        System.err.println(Thread.currentThread().getName() + "\t toString() => " + future.toString());
        try {
            System.err.println(Thread.currentThread().getName() + "\t get() => " + future.get());
        } catch (Exception e) {
            System.out.println(Thread.currentThread().getName() + "\t get()感知到异常了 => " + e.getClass() + "\t" + e.getMessage());
        }
        System.err.println(Thread.currentThread().getName() + "\t toString() => " + future.toString());
    }
    
    /**
     * 输出:
     * main	 toString() => java.util.concurrent.CompletableFuture@2796aeae[Not completed]
     * main	 get(...) =>异常了 => class java.util.concurrent.TimeoutException	null
     * main	 toString() => java.util.concurrent.CompletableFuture@2796aeae[Not completed]
     */
    @Test
    public void getTest3() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "邓沙利文";
        });
        System.err.println(Thread.currentThread().getName() + "\t toString() => " + future.toString());
        try {
            System.err.println(Thread.currentThread().getName() + "\t get(...) => " + future.get(3, TimeUnit.SECONDS));
        } catch (Exception e) {
            System.out.println(Thread.currentThread().getName() + "\t get(...) =>异常了 => " + e.getClass() + "\t" + e.getMessage());
        }
        System.err.println(Thread.currentThread().getName() + "\t toString() => " + future.toString());
    }
    
    /**
     * 输出:
     * main	 toString() => java.util.concurrent.CompletableFuture@2796aeae[Not completed]
     * main	 getNow(...) => 我是缺省值
     * main	 toString() => java.util.concurrent.CompletableFuture@2796aeae[Not completed]
     */
    @Test
    public void getNowTest1() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "邓沙利文";
        });
        System.err.println(Thread.currentThread().getName() + "\t toString() => " + future.toString());
        System.err.println(Thread.currentThread().getName() + "\t getNow(...) => " + future.getNow("我是缺省值"));
        System.err.println(Thread.currentThread().getName() + "\t toString() => " + future.toString());
    }
    
    
    /**
     * 输出:
     * main	 toString() => java.util.concurrent.CompletableFuture@2796aeae[Completed exceptionally]
     * main	 getNow(...) =>异常了 => class java.util.concurrent.CompletionException	java.lang.ArithmeticException: / by  zero
     * main	 toString() => java.util.concurrent.CompletableFuture@2796aeae[Completed exceptionally]
     */
    @Test
    public void getNowTest2() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
           int a = 1 / 0;
            return "邓沙利文";
        });
        TimeUnit.SECONDS.sleep(1);
        System.err.println(Thread.currentThread().getName() + "\t toString() => " + future.toString());
        try {
            System.err.println(Thread.currentThread().getName() + "\t getNow(...) => " + future.getNow("我是缺省值"));
        } catch (Exception e) {
            System.out.println(Thread.currentThread().getName() + "\t getNow(...) =>异常了 => " + e.getClass() + "\t" + e.getMessage());
        }
        System.err.println(Thread.currentThread().getName() + "\t toString() => " + future.toString());
    }
    
    /// ********************************************************************* join
    /**
     * public T join(): 阻塞获取future结果。
     *
     * 注: join与get唯一不同的是， get有声明异常，需要处理检查异常。而join没有声明异常，就算抛出异常，也抛出的是运行时异常。
     *
     * 输出:
     * main	 toString() => java.util.concurrent.CompletableFuture@2796aeae[Not completed]
     * ForkJoinPool.commonPool-worker-1	 0
     * ForkJoinPool.commonPool-worker-1	 1
     * ForkJoinPool.commonPool-worker-1	 2
     * main	 join() => 邓沙利文
     * main	 toString() => java.util.concurrent.CompletableFuture@2796aeae[Completed normally]
     */
    @Test
    public void joinTest1() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            for (int i = 0; i < 3; i++) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    Thread currentThread = Thread.currentThread();
                    System.err.println(currentThread.getName() + "\t " + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "邓沙利文";
        });
        System.err.println(Thread.currentThread().getName() + "\t toString() => " + future.toString());
        System.err.println(Thread.currentThread().getName() + "\t join() => " + future.join());
        System.err.println(Thread.currentThread().getName() + "\t toString() => " + future.toString());
    }
    
    /**
     * 输出:
     * main	 toString() => java.util.concurrent.CompletableFuture@2796aeae[Not completed]
     * main	 join()感知到异常了 => class java.util.concurrent.CompletionException	java.lang.ArithmeticException: / by zero
     * main	 toString() => java.util.concurrent.CompletableFuture@2796aeae[Completed exceptionally]
     */
    @Test
    public void joinTest2() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            int a = 1 / 0;
            return "邓沙利文";
        });
        System.err.println(Thread.currentThread().getName() + "\t toString() => " + future.toString());
        try {
            System.err.println(Thread.currentThread().getName() + "\t join() => " + future.join());
        } catch (Exception e) {
            System.out.println(Thread.currentThread().getName() + "\t join()感知到异常了 => " + e.getClass() + "\t" + e.getMessage());
        }
        System.err.println(Thread.currentThread().getName() + "\t toString() => " + future.toString());
    }
    
    
    /// ********************************************************************* getNumberOfDependents
    
    /**
     * public int getNumberOfDependents(): 获取正在等待future完成的CompletableFuture的预估数量。
     *
     * 注: 设计出方法的初衷是为了监控系统状态，而非是用于流程控制。
     *
     * 输出:
     * getNumberOfDependents() => 5
     * null
     * null
     * null
     * null
     * null
     *  getNumberOfDependents() => 0
     */
    @Test
    public void getNumberOfDependentsTest1() throws Exception {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    System.err.println(future.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        TimeUnit.SECONDS.sleep(1);
        // 此时future还未完成
        System.err.println(" getNumberOfDependents() => " + future.getNumberOfDependents());
        TimeUnit.SECONDS.sleep(5);
        // 此时future已完成
        System.err.println(" getNumberOfDependents() => " + future.getNumberOfDependents());
    }
    
    
    
    /// ********************************************************************* handle、handleAsync
    
    /**
     * public <U> CompletableFuture<U> handle(BiFunction<? super T, Throwable, ? extends U> fn): 当future(正常或异常)完成之后， （使用与执行future相同的那一个线程来）执行handle的逻辑
     * public <U> CompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn): 当future(正常或异常)完成之后， （使用默认的ForkJoinPool.commonPool线程池分配的线程来）执行handle的逻辑
     * public <U> CompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn, Executor executor): 当future(正常或异常)完成之后， （使用指定的executor线程池分配的线程来）执行handle的逻辑
     *
     * 注: 有消费返回值、有future返回值。
     * 注: handle与whenComplete类似， 类比使用即可。
     * 注: 若异步执行CompletableFuture逻辑的线程是a, 那么在a计算完异步逻辑后，接着还是由该线程进行handle逻辑。
     * 注: CompletableFuture实例的异步结果会作为形参传递给handle。
     * 注: 若CompletableFuture逻辑中抛出了异常，那么handle也是可以处理异常的， 该异常就不会继续往外抛，这是和whenComplete差异较大的地方。
     *
     * 输出:
     * ForkJoinPool.commonPool-worker-1	424
     * ForkJoinPool.commonPool-worker-1	424-oop
     * main	get() => 424-oop
     */
    @Test
    public void handleTest1() throws Exception {
        BiFunction<Integer, Throwable, String> biFunction = (Integer i, Throwable th) -> {
            String result;
            if (th == null) {
                result = String.valueOf(i).concat("-oop");
            } else {
                result = String.valueOf(i).concat("\t").concat(th.getMessage());
            }
            System.err.println(Thread.currentThread().getName() + "\t" + result);
            return result;
        };
    
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            int i = ThreadLocalRandom.current().nextInt(1000);
            System.err.println(Thread.currentThread().getName() + "\t" + i);
            return i;
        }).handle(biFunction);
        System.err.println(Thread.currentThread().getName() + "\tget() => " + future.get());
    }
    
    /**
     * 输出:
     * ForkJoinPool.commonPool-worker-1	748
     * ForkJoinPool.commonPool-worker-1	null	java.lang.ArithmeticException: / by zero
     * main	get() => null	java.lang.ArithmeticException: / by zero
     */
    @Test
    public void handleTest2() throws Exception {
        BiFunction<Integer, Throwable, String> biFunction = (Integer i, Throwable th) -> {
            String result;
            if (th == null) {
                result = String.valueOf(i).concat("-oop");
            } else {
                result = String.valueOf(i).concat("\t").concat(th.getMessage());
            }
            System.err.println(Thread.currentThread().getName() + "\t" + result);
            return result;
        };
        
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            int i = ThreadLocalRandom.current().nextInt(1000);
            System.err.println(Thread.currentThread().getName() + "\t" + i);
            // 使抛出异常
            int a = 1 / 0;
            return i;
        }).handle(biFunction);
        System.err.println(Thread.currentThread().getName() + "\tget() => " + future.get());
    }
    
    /**
     * 提示: 这里futuren完成后，线程池ForkJoinPool.commonPool中的worker-1线程就空闲了， 此时ForkJoinPool.commonPool
     *      在给handle分配线程时，自然就选择了ForkJoinPool.commonPool-worker-1。
     *      即: 在并发多的时候， 下面的输出可能就是：
     *      ForkJoinPool.commonPool-worker-1	446
     *      ForkJoinPool.commonPool-worker-2	446-oop
     *      main	get() => 446-oop
     *      这样子的了
     * 输出:
     * ForkJoinPool.commonPool-worker-1	446
     * ForkJoinPool.commonPool-worker-1	446-oop
     * main	get() => 446-oop
     */
    @Test
    public void handleAsyncTest1() throws Exception {
        BiFunction<Integer, Throwable, String> biFunction = (Integer i, Throwable th) -> {
            String result;
            if (th == null) {
                result = String.valueOf(i).concat("-oop");
            } else {
                result = String.valueOf(i).concat("\t").concat(th.getMessage());
            }
            System.err.println(Thread.currentThread().getName() + "\t" + result);
            return result;
        };
        
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            int i = ThreadLocalRandom.current().nextInt(1000);
            System.err.println(Thread.currentThread().getName() + "\t" + i);
            return i;
        }).handleAsync(biFunction);
        System.err.println(Thread.currentThread().getName() + "\tget() => " + future.get());
    }
    
    /**
     * 输出:
     * ForkJoinPool.commonPool-worker-1	2
     * pool-1-thread-1	2-oop
     * main	get() => 2-oop
     */
    @Test
    public void handleAsyncTest2() throws Exception {
        Executor myExecutor = Executors.newFixedThreadPool(3);
        BiFunction<Integer, Throwable, String> biFunction = (Integer i, Throwable th) -> {
            String result;
            if (th == null) {
                result = String.valueOf(i).concat("-oop");
            } else {
                result = String.valueOf(i).concat("\t").concat(th.getMessage());
            }
            System.err.println(Thread.currentThread().getName() + "\t" + result);
            return result;
        };
        
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            int i = ThreadLocalRandom.current().nextInt(1000);
            System.err.println(Thread.currentThread().getName() + "\t" + i);
            return i;
        }).handleAsync(biFunction, myExecutor);
        System.err.println(Thread.currentThread().getName() + "\tget() => " + future.get());
    }
    
    
    
    
    
    /// ********************************************************************* isCompletedExceptionally​
    /**
     * public boolean isCompletedExceptionally(): 判断是否是异常完成。
     *
     * 注: 只有异常完成，才返回true; 其他情况(如未完成、正常完成等均)返回false.
     * 注: 任务被cacel,也属于异常完成.
     *
     * 输出:
     * java.util.concurrent.CompletableFuture@1fa1cab1[Completed normally]
     *  isCompletedExceptionally() => false
     * java.util.concurrent.CompletableFuture@62010f5c[Completed exceptionally]
     *  isCompletedExceptionally() => true
     */
    @Test
    public void isCompletedExceptionallyTest1() throws Exception {
        // 正常完成
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {});
        TimeUnit.SECONDS.sleep(1);
        System.err.println(future.toString());
        System.err.println(" isCompletedExceptionally() => " + future.isCompletedExceptionally());
    
        // 异常完成
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            int a = 1 / 0;
        });
        TimeUnit.SECONDS.sleep(1);
        System.err.println(future1.toString());
        System.err.println(" isCompletedExceptionally() => " + future1.isCompletedExceptionally());
    }
    
    /// ********************************************************************* isDone
    /**
     * public boolean isDone(): 判断future是否已完成。
     *
     * 注: 除了未完成， 其余的(正常完成、抛异常导致的异常完成、被取消导致的异常完成)都算已完成。
     *
     * 输出:
     * java.util.concurrent.CompletableFuture@1fa1cab1[Not completed]
     *  isDone() => false
     *
     * java.util.concurrent.CompletableFuture@62010f5c[Completed normally]
     *  isDone() => true
     *
     * java.util.concurrent.CompletableFuture@401f7633[Completed exceptionally]
     *  isDone() => true
     *
     * future3.cancel=true, 	future3.isCancelled=true
     * java.util.concurrent.CompletableFuture@5b6ec132[Completed exceptionally]
     *  isDone() => true
     */
    @Test
    public void isDoneTest1() throws Exception {
        // 未完成
        CompletableFuture<Void> future0 = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        TimeUnit.SECONDS.sleep(1);
        System.err.println(future0.toString());
        System.err.println(" isDone() => " + future0.isDone());
        
        // 正常完成
        System.err.println();
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {});
        TimeUnit.SECONDS.sleep(1);
        System.err.println(future1.toString());
        System.err.println(" isDone() => " + future1.isDone());
    
        // (抛异常导致的)异常完成
        System.err.println();
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            int a = 1 / 0;
        });
        TimeUnit.SECONDS.sleep(1);
        System.err.println(future2.toString());
        System.err.println(" isDone() => " + future2.isDone());
    
        // (被取消导致的)异常完成
        System.err.println();
        CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> {
            for (int i = 0; i < 4; i++) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        TimeUnit.SECONDS.sleep(1);
        System.err.println("future3.cancel=" + future3.cancel(true) + ", \tfuture3.isCancelled=" + future3.isCancelled());
        System.err.println(future3.toString());
        System.err.println(" isDone() => " + future3.isDone());
    }
    
    
    
    /// ********************************************************************* obtrudeException
    
    /**
     * public void obtrudeException(Throwable ex): (无论这个future当前是否已经完成), 强制使furetu以指定异常完成。
     *
     * 注: 此方法的设计初衷是为了错误复现。
     * 注: 若futureB = futureA.xxx(); 当执行到obtrudeException之前， futureA及futureB都属于正常完成状态，
     *     此时对futureA进行obtrudeException，那么futureA就变为了异常完成状态，但是futureB仍然是正常完成状态。
     *     追注: 测试代码可见{@link this#obtrudeExceptionTest2}
     *
     * 输出:
     * java.util.concurrent.CompletableFuture@2796aeae[Completed normally]
     * 我是完成返回值
     * java.util.concurrent.CompletableFuture@2796aeae[Completed exceptionally]
     *
     * java.util.concurrent.ExecutionException: java.lang.UnknownError: ~~~
     *
     * 	at java.util.concurrent.CompletableFuture.reportGet(CompletableFuture.java:357)
     * 	at java.util.concurrent.CompletableFuture.get(CompletableFuture.java:1895)
     * 	(省略...)
     */
    @Test
    public void obtrudeExceptionTest1() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "我是完成返回值");
        TimeUnit.SECONDS.sleep(1);
        System.err.println(future.toString());
        System.err.println(future.get());
        // 强制使future异常完成
        future.obtrudeException(new UnknownError("~~~"));
        System.err.println(future.toString());
        System.err.println(future.get());
    }
    
    /**
     * 输出:
     * main我是完成返回值	null
     * java.util.concurrent.CompletableFuture@56673b2c[Completed normally]
     * java.util.concurrent.CompletableFuture@56673b2c[Completed normally]
     */
    @Test
    public void obtrudeExceptionTest2() throws Exception {
        BiConsumer<String, Throwable> action = (String x, Throwable y) -> {
            System.out.println(Thread.currentThread().getName() + x + "\t" + y);
        };
        
        CompletableFuture<String> future = CompletableFuture.completedFuture("我是完成返回值");
        CompletableFuture<String> future1 = future.whenComplete(action);
    
        TimeUnit.SECONDS.sleep(1);
        System.err.println(future1.toString());
        // 强制使future异常完成
        future.obtrudeException(new UnknownError("~~~"));
        System.err.println(future1.toString());
    }
    
    
    
    /// ********************************************************************* obtrudeValue​
    
    
    /**
     * public void obtrudeValue(T value): (无论这个future当前是否已经完成), 强制使furetu以指定返回值正常完成。
     *
     * 注: 此方法的设计初衷是为了错误复现。
     * 注: 若futureB = futureA.xxx(); 当执行到obtrudeValue之前， futureA及futureB都已完成，
     *     此时对futureA进行obtrudeValue，那么futureA就变为了正常完成(且完成值为value)，但是futureB的完成状态完成值仍然是原来的。
     *     追注: 测试代码可见{@link this#obtrudeValueTest3}
     *
     * 输出:
     * java.util.concurrent.CompletableFuture@2796aeae[Completed normally]
     * 我是完成返回值
     * java.util.concurrent.CompletableFuture@2796aeae[Completed normally]
     * 蚂蚁呀嘿!嘿!
     */
    @Test
    public void obtrudeValueTest1() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "我是完成返回值");
        TimeUnit.SECONDS.sleep(1);
        System.err.println(future.toString());
        System.err.println(future.get());
        // 强制改变完成值
        future.obtrudeValue("蚂蚁呀嘿!嘿!");
        System.err.println(future.toString());
        System.err.println(future.get());
    }
    
    /**
     * 输出:
     * java.util.concurrent.CompletableFuture@2796aeae[Completed exceptionally]
     * get()时异常了 => java.lang.ArithmeticException: / by zero
     * java.util.concurrent.CompletableFuture@2796aeae[Completed normally]
     * 蚂蚁呀嘿!嘿!
     */
    @Test
    public void obtrudeValueTest2() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            int a = 1 / 0;
            return "我是完成返回值";
        });
        TimeUnit.SECONDS.sleep(1);
        System.err.println(future.toString());
        try {
            System.err.println(future.get());
        } catch (Exception e) {
            System.err.println("get()时异常了 => " + e.getMessage());
        }
        // 强制改变完成值
        future.obtrudeValue("蚂蚁呀嘿!嘿!");
        System.err.println(future.toString());
        System.err.println(future.get());
    }
    
    /**
     * 输出:
     *  future.get() => 我是完成返回值main	null
     *  future1.get() => 我是完成返回值main	null
     *  future.get() => ~~~
     *  future1.get() => 我是完成返回值main	null
     */
    @Test
    public void obtrudeValueTest3() throws Exception {
        BiConsumer<StringBuilder, Throwable> action = (StringBuilder x, Throwable y) -> {
            x = x == null ? new StringBuilder(): x;
            x.append(Thread.currentThread().getName() + "\t" + y);
        };
        
        CompletableFuture<StringBuilder> future = CompletableFuture.completedFuture(new StringBuilder("我是完成返回值"));
        CompletableFuture<StringBuilder> future1 = future.whenComplete(action);
        
        TimeUnit.SECONDS.sleep(1);
        System.err.println(" future.get() => " + future.get());
        System.err.println(" future1.get() => " + future1.get());
        // 强制使future异常完成
        future.obtrudeValue(new StringBuilder("~~~"));
        System.err.println(" future.get() => " + future.get());
        System.err.println(" future1.get() => " + future1.get());
    }
    
    
    
    /// ********************************************************************* thenAccept​、thenAcceptAsync​
    /**
     * public CompletableFuture<Void> thenAccept(Consumer<? super T> action): 当前一个future正常完成时， 会以此future的完成返回值的引用作为参数，
     *                                                                        (使用与执行future相同的线程)执行action的逻辑。
     * public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action): 当前一个future正常完成时， 会以此future的完成返回值的引用作为参数，
     *                                                                             (使用默认的线程池ForkJoinPool.commonPool分配的线程)执行action的逻辑。
     * public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor): 当前一个future正常完成时， 会以此future的完成返回值的引用作为参数，
     *                                                                                                (使用默认的线程池executor分配的线程)执行action的逻辑。
     *
     * 注: 无消费返回值、无future返回值。
     * 注: 若当前这个future没有正常完成，那么就不会执行action的逻辑.
     *
     * 输出:
     * ForkJoinPool.commonPool-worker-1	future0
     * future0状态 => java.util.concurrent.CompletableFuture@3c7ae459[Completed normally]
     * future0	ForkJoinPool.commonPool-worker-1	进入consumer了
     * future0.get() => future0	ForkJoinPool.commonPool-worker-1	进入consumer了
     */
    @Test
    public void thenAcceptTest1() throws Exception {
        CompletableFuture<StringBuilder> future0 = CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String str0 = "future0";
                    String str1 = Thread.currentThread().getName() + "\t" + str0;
                    System.err.println(str1);
                    return new StringBuilder(str0);
                }
        );
        
        Consumer<StringBuilder> consumer = (StringBuilder x) -> {
            System.err.println("future0状态 => " + future0.toString());
            String t = Thread.currentThread().getName();
            x = x == null ? new StringBuilder(t + "进入consumer了") : x.append("\t" + t + "\t进入consumer了");
            System.err.println(x);
        };
        CompletableFuture<Void> future1 = future0.thenAccept(consumer);
    
        future1.join();
        System.err.println("future0.get() => " + future0.get());
    }
    
    /**
     * (注释掉A处时)输出:
     * ForkJoinPool.commonPool-worker-1	future0
     * main
     *
     * (打开A处代码时)输出:
     * ForkJoinPool.commonPool-worker-1	future0
     * main
     *
     * java.util.concurrent.CompletionException: java.lang.ArithmeticException: / by zero
     *
     * 	at java.util.concurrent.CompletableFuture.encodeThrowable(CompletableFuture.java:273)
     * 	(省略...)
     */
    @Test
    public void thenAcceptTest2() throws Exception {
        CompletableFuture<StringBuilder> future0 = CompletableFuture.supplyAsync(() -> {
                    try {
                        System.err.println(Thread.currentThread().getName() + "\tfuture0");
                        TimeUnit.SECONDS.sleep(3);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    int a = 1 / 0;
                    return new StringBuilder("~~~~~~~~");
                }
        );
        
        Consumer<StringBuilder> consumer = (StringBuilder x) -> {
            System.err.println("future0状态 => " + future0.toString());
            String t = Thread.currentThread().getName();
            x = x == null ? new StringBuilder(t + "进入consumer了") : x.append("\t" + t + "\t进入consumer了");
            System.err.println(x);
        };
        CompletableFuture<Void> future1 = future0.thenAccept(consumer);
        System.err.println(Thread.currentThread().getName());
        TimeUnit.SECONDS.sleep(5);
        // future1.join(); // A
    }
    
    /**
     * 输出:
     * ForkJoinPool.commonPool-worker-1	future0
     * future0状态 => java.util.concurrent.CompletableFuture@3bf829db[Completed normally]
     * future0	ForkJoinPool.commonPool-worker-1	进入consumer了
     * future0.get() => future0	ForkJoinPool.commonPool-worker-1	进入consumer了
     */
    @Test
    public void thenAcceptAsyncTest1() throws Exception {
        CompletableFuture<StringBuilder> future0 = CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String str0 = "future0";
                    String str1 = Thread.currentThread().getName() + "\t" + str0;
                    System.err.println(str1);
                    return new StringBuilder(str0);
                }
        );
        
        Consumer<StringBuilder> consumer = (StringBuilder x) -> {
            System.err.println("future0状态 => " + future0.toString());
            String t = Thread.currentThread().getName();
            x = x == null ? new StringBuilder(t + "进入consumer了") : x.append("\t" + t + "\t进入consumer了");
            System.err.println(x);
        };
        CompletableFuture<Void> future1 = future0.thenAcceptAsync(consumer);
        
        future1.join();
        System.err.println("future0.get() => " + future0.get());
    }
    
    
    /**
     * 输出:
     * ForkJoinPool.commonPool-worker-1	future0
     * future0状态 => java.util.concurrent.CompletableFuture@25c93186[Completed normally]
     * future0	pool-1-thread-1	进入consumer了
     * future0.get() => future0	pool-1-thread-1	进入consumer了
     */
    @Test
    public void thenAcceptAsyncTest2() throws Exception {
        Executor myExecutor = Executors.newFixedThreadPool(3);
        CompletableFuture<StringBuilder> future0 = CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String str0 = "future0";
                    String str1 = Thread.currentThread().getName() + "\t" + str0;
                    System.err.println(str1);
                    return new StringBuilder(str0);
                }
        );
        
        Consumer<StringBuilder> consumer = (StringBuilder x) -> {
            System.err.println("future0状态 => " + future0.toString());
            String t = Thread.currentThread().getName();
            x = x == null ? new StringBuilder(t + "进入consumer了") : x.append("\t" + t + "\t进入consumer了");
            System.err.println(x);
        };
        CompletableFuture<Void> future1 = future0.thenAcceptAsync(consumer, myExecutor);
        
        future1.join();
        System.err.println("future0.get() => " + future0.get());
    }
    
    
    /// ********************************************************************* thenAcceptBoth、thenAcceptBothAsync​
    
    
    /**
     * public <U> CompletableFuture<Void> thenAcceptBoth(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action):
     * 当this和other这两个future全部都正常完成后， 以这两个future的结果为参数， (使用后完成的那个future的线程)执行action
     *
     * public <U> CompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> other,BiConsumer<? super T, ? super U> action):
     * 当this和other这两个future全部都正常完成后， 以这两个future的结果为参数， (使用默认的线程池ForkJoinPool.commonPool分配的线程)执行action
     *
     * public <U> CompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action, Executor executor):
     * 当this和other这两个future全部都正常完成后， 以这两个future的结果为参数， (使用指定的线程池executor分配的线程)执行action
     *
     * 注: 无消费返回值、无future返回值。
     * 注: 若其中一个(或者两个future都)没有正常完成， 那么是不会执行action的。
     * 注: 即便执行到thenAcceptBoth时， 两个futuren都已经正常完成了。action仍然可以修改两个future的值。 因为action的参数是这两个future的结果的引用。
     * 注: 1.若原线程执行到thenAcceptBoth时， 两个futuren都已经正常完成了, 那么由原线程(这里为main线程)执行action的逻辑。
     *     2.若原线程执行到thenAcceptBothAsync时，即便两个future都已经正常执行完了， 那么还是会使用默认的线程池ForkJoinPool.commonPool分配的线程来执行action.
     *     3.若原线程执行到thenAcceptBothAsync时，即便两个future都已经正常执行完了， 那么还是会使用定的线程池executor分配的线程来执行action.
     *
     *
     * 输出:
     * ForkJoinPool.commonPool-worker-1	futureA
     * ForkJoinPool.commonPool-worker-2	futureB
     *
     * futureA状态 => java.util.concurrent.CompletableFuture@5502e11c[Completed normally],	futureB状态 => java.util
     * .concurrent.CompletableFuture@5bc505cc[Completed normally]
     * ForkJoinPool.commonPool-worker-2
     * futureA-consumerX
     * futureB-consumerY
     *
     * 此时，futureA的结果为:futureA-consumerX
     * 此时，futureB的结果为:futureB-consumerY
     */
    @Test
    public void thenAcceptBothTest1() throws Exception {
        CompletableFuture<StringBuilder> futureA = CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String str0 = "futureA";
                    String str1 = Thread.currentThread().getName() + "\t" + str0;
                    System.err.println(str1);
                    return new StringBuilder(str0);
                }
        );
    
        CompletableFuture<StringBuilder> futureB = CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        // TimeUnit.SECONDS.sleep(3); 测试: 由后完成的那个future的线程执行action
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String str0 = "futureB";
                    String str1 = Thread.currentThread().getName() + "\t" + str0;
                    System.err.println(str1);
                    return new StringBuilder(str0);
                }
        );
        
        BiConsumer<StringBuilder, StringBuilder> consumer = (StringBuilder x, StringBuilder y) -> {
            System.err.println();
            System.err.println("futureA状态 => " + futureA.toString() + ",\tfutureB状态 => " + futureB.toString());
            String t = Thread.currentThread().getName();
            if (x == null || y == null) {
                // ...
                return;
            }
            x.append("-consumerX");
            y.append("-consumerY");
            System.err.println(t + "\n" + x + "\n" + y);
        };
        CompletableFuture<Void> future123 = futureA.thenAcceptBoth(futureB, consumer);
        future123.join();
        System.err.println();
        System.err.println("此时，futureA的结果为:" + futureA.join());
        System.err.println("此时，futureB的结果为:" + futureB.join());
    }
    
    /**
     * 测试: 若其中一个(或者两个future都)没有正常完成， 那么是不会执行action的
     *
     * 输出:
     * java.util.concurrent.CompletionException: java.lang.ArithmeticException: / by zero
     *
     * 	at java.util.concurrent.CompletableFuture.encodeThrowable(CompletableFuture.java:273)
     * 	at java.util.concurrent.CompletableFuture.completeThrowable(CompletableFuture.java:280)
     * 	at java.util.concurrent.CompletableFuture$AsyncSupply.run(CompletableFuture.java:1592)
     * 	(省略...)
     */
    @Test
    public void thenAcceptBothTest2() throws Exception {
        CompletableFuture<StringBuilder> futureA = CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // 抛异常
                    int a = 1 / 0;
                    return new StringBuilder();
                }
        );
        
        CompletableFuture<StringBuilder> futureB = CompletableFuture.supplyAsync(() -> {
                    return new StringBuilder();
                }
        );
        
        BiConsumer<StringBuilder, StringBuilder> consumer = (StringBuilder x, StringBuilder y) -> {
            System.err.println();
            System.err.println("futureA状态 => " + futureA.toString() + ",\tfutureB状态 => " + futureB.toString());
            String t = Thread.currentThread().getName();
            if (x == null || y == null) {
                // ...
                return;
            }
            x.append("-consumerX");
            y.append("-consumerY");
            System.err.println(t + "\n" + x + "\n" + y);
        };
        CompletableFuture<Void> future123 = futureA.thenAcceptBoth(futureB, consumer);
        future123.join();
    }
    
    /**
     * 测试: 1、若原线程执行到thenAcceptBoth时， 两个futuren都已经正常完成了。 那么由原线程(这里为main线程)执行action的逻辑。
     *      2、即便原线程执行到thenAcceptBoth时， 两个futuren都已经正常完成了。action仍然可以修改两个future的值。 因为action的参数是这两个future的结果的引用。
     *
     * 输出:
     * 此时，futureA的结果为:futureA
     * 此时，futureB的结果为:futureB
     *
     * futureA状态 => java.util.concurrent.CompletableFuture@2796aeae[Completed normally],	futureB状态 => java.util
     * .concurrent.CompletableFuture@b4711e2[Completed normally]
     * main
     * futureA-consumerX
     * futureB-consumerY
     *
     * 此时，futureA的结果为:futureA-consumerX
     * 此时，futureB的结果为:futureB-consumerY
     */
    @Test
    public void thenAcceptBothTest3() throws Exception {
        CompletableFuture<StringBuilder> futureA = CompletableFuture.completedFuture(new StringBuilder("futureA"));
        CompletableFuture<StringBuilder> futureB = CompletableFuture.completedFuture(new StringBuilder("futureB"));
    
        System.err.println("此时，futureA的结果为:" + futureA.join());
        System.err.println("此时，futureB的结果为:" + futureB.join());
        
        BiConsumer<StringBuilder, StringBuilder> consumer = (StringBuilder x, StringBuilder y) -> {
            System.err.println();
            System.err.println("futureA状态 => " + futureA.toString() + ",\tfutureB状态 => " + futureB.toString());
            String t = Thread.currentThread().getName();
            if (x == null || y == null) {
                // ...
                return;
            }
            x.append("-consumerX");
            y.append("-consumerY");
            System.err.println(t + "\n" + x + "\n" + y);
        };
        CompletableFuture<Void> future123 = futureA.thenAcceptBoth(futureB, consumer);
        future123.join();
        System.err.println();
        System.err.println("此时，futureA的结果为:" + futureA.join());
        System.err.println("此时，futureB的结果为:" + futureB.join());
    }
    
    /**
     * 输出:
     * ForkJoinPool.commonPool-worker-2	futureB
     * ForkJoinPool.commonPool-worker-1	futureA
     *
     * futureA状态 => java.util.concurrent.CompletableFuture@57f252f[Completed normally],	futureB状态 => java.util
     * .concurrent.CompletableFuture@bb8cbf0[Completed normally]
     * ForkJoinPool.commonPool-worker-1
     * futureA-consumerX
     * futureB-consumerY
     *
     * 此时，futureA的结果为:futureA-consumerX
     * 此时，futureB的结果为:futureB-consumerY
     */
    @Test
    public void thenAcceptBothAsyncTest1() throws Exception {
        CompletableFuture<StringBuilder> futureA = CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String str0 = "futureA";
                    String str1 = Thread.currentThread().getName() + "\t" + str0;
                    System.err.println(str1);
                    return new StringBuilder(str0);
                }
        );
        
        CompletableFuture<StringBuilder> futureB = CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        // TimeUnit.SECONDS.sleep(3); 测试: 由后完成的那个future的线程执行action
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String str0 = "futureB";
                    String str1 = Thread.currentThread().getName() + "\t" + str0;
                    System.err.println(str1);
                    return new StringBuilder(str0);
                }
        );
        
        BiConsumer<StringBuilder, StringBuilder> consumer = (StringBuilder x, StringBuilder y) -> {
            System.err.println();
            System.err.println("futureA状态 => " + futureA.toString() + ",\tfutureB状态 => " + futureB.toString());
            String t = Thread.currentThread().getName();
            if (x == null || y == null) {
                // ...
                return;
            }
            x.append("-consumerX");
            y.append("-consumerY");
            System.err.println(t + "\n" + x + "\n" + y);
        };
        CompletableFuture<Void> future123 = futureA.thenAcceptBothAsync(futureB, consumer);
        future123.join();
        System.err.println();
        System.err.println("此时，futureA的结果为:" + futureA.join());
        System.err.println("此时，futureB的结果为:" + futureB.join());
    }
    
    /**
     * 测试: 当执行到 thenAcceptBothAsync时，即便两个future都已经正常执行完了， 那么还是会使用默认的线程池ForkJoinPool.commonPool分配的线程来执行action.
     *
     * 输出:
     * 此时，futureA的结果为:futureA
     * 此时，futureB的结果为:futureB
     *
     * futureA状态 => java.util.concurrent.CompletableFuture@58eb9f48[Completed normally],	futureB状态 => java.util
     * .concurrent.CompletableFuture@562c6a25[Completed normally]
     * ForkJoinPool.commonPool-worker-1
     * futureA-consumerX
     * futureB-consumerY
     *
     * 此时，futureA的结果为:futureA-consumerX
     * 此时，futureB的结果为:futureB-consumerY
     */
    @Test
    public void thenAcceptBothAsyncTest2() throws Exception {
        CompletableFuture<StringBuilder> futureA = CompletableFuture.completedFuture(new StringBuilder("futureA"));
        CompletableFuture<StringBuilder> futureB = CompletableFuture.completedFuture(new StringBuilder("futureB"));
        
        System.err.println("此时，futureA的结果为:" + futureA.join());
        System.err.println("此时，futureB的结果为:" + futureB.join());
        
        BiConsumer<StringBuilder, StringBuilder> consumer = (StringBuilder x, StringBuilder y) -> {
            System.err.println();
            System.err.println("futureA状态 => " + futureA.toString() + ",\tfutureB状态 => " + futureB.toString());
            String t = Thread.currentThread().getName();
            if (x == null || y == null) {
                // ...
                return;
            }
            x.append("-consumerX");
            y.append("-consumerY");
            System.err.println(t + "\n" + x + "\n" + y);
        };
        CompletableFuture<Void> future123 = futureA.thenAcceptBothAsync(futureB, consumer);
        future123.join();
        System.err.println();
        System.err.println("此时，futureA的结果为:" + futureA.join());
        System.err.println("此时，futureB的结果为:" + futureB.join());
    }
    
    
    /**
     * 输出:
     * ForkJoinPool.commonPool-worker-2	futureB
     * ForkJoinPool.commonPool-worker-1	futureA
     *
     * futureA状态 => java.util.concurrent.CompletableFuture@49538ddf[Completed normally],	futureB状态 => java.util
     * .concurrent.CompletableFuture@47946a85[Completed normally]
     * pool-1-thread-1
     * futureA-consumerX
     * futureB-consumerY
     *
     * 此时，futureA的结果为:futureA-consumerX
     * 此时，futureB的结果为:futureB-consumerY
     */
    @Test
    public void thenAcceptBothAsyncTest3() throws Exception {
        Executor myExecutor = Executors.newFixedThreadPool(3);
        CompletableFuture<StringBuilder> futureA = CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String str0 = "futureA";
                    String str1 = Thread.currentThread().getName() + "\t" + str0;
                    System.err.println(str1);
                    return new StringBuilder(str0);
                }
        );
        
        CompletableFuture<StringBuilder> futureB = CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        // TimeUnit.SECONDS.sleep(3); 测试: 由后完成的那个future的线程执行action
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String str0 = "futureB";
                    String str1 = Thread.currentThread().getName() + "\t" + str0;
                    System.err.println(str1);
                    return new StringBuilder(str0);
                }
        );
        
        BiConsumer<StringBuilder, StringBuilder> consumer = (StringBuilder x, StringBuilder y) -> {
            System.err.println();
            System.err.println("futureA状态 => " + futureA.toString() + ",\tfutureB状态 => " + futureB.toString());
            String t = Thread.currentThread().getName();
            if (x == null || y == null) {
                // ...
                return;
            }
            x.append("-consumerX");
            y.append("-consumerY");
            System.err.println(t + "\n" + x + "\n" + y);
        };
        CompletableFuture<Void> future123 = futureA.thenAcceptBothAsync(futureB, consumer, myExecutor);
        future123.join();
        System.err.println();
        System.err.println("此时，futureA的结果为:" + futureA.join());
        System.err.println("此时，futureB的结果为:" + futureB.join());
    }
    
    /**
     * 测试: 当执行到 thenAcceptBothAsync时，即便两个future都已经正常执行完了， 那么还是会使用指定的线程池executor分配的线程来执行action.
     *
     * 输出:
     * 此时，futureA的结果为:futureA
     * 此时，futureB的结果为:futureB
     *
     * futureA状态 => java.util.concurrent.CompletableFuture@47eaa5bb[Completed normally],	futureB状态 => java.util
     * .concurrent.CompletableFuture@492d4336[Completed normally]
     * pool-1-thread-1
     * futureA-consumerX
     * futureB-consumerY
     *
     * 此时，futureA的结果为:futureA-consumerX
     * 此时，futureB的结果为:futureB-consumerY
     */
    @Test
    public void thenAcceptBothAsyncTest4() throws Exception {
        Executor myExecutor = Executors.newFixedThreadPool(3);
        CompletableFuture<StringBuilder> futureA = CompletableFuture.completedFuture(new StringBuilder("futureA"));
        CompletableFuture<StringBuilder> futureB = CompletableFuture.completedFuture(new StringBuilder("futureB"));
        
        System.err.println("此时，futureA的结果为:" + futureA.join());
        System.err.println("此时，futureB的结果为:" + futureB.join());
        
        BiConsumer<StringBuilder, StringBuilder> consumer = (StringBuilder x, StringBuilder y) -> {
            System.err.println();
            System.err.println("futureA状态 => " + futureA.toString() + ",\tfutureB状态 => " + futureB.toString());
            String t = Thread.currentThread().getName();
            if (x == null || y == null) {
                // ...
                return;
            }
            x.append("-consumerX");
            y.append("-consumerY");
            System.err.println(t + "\n" + x + "\n" + y);
        };
        CompletableFuture<Void> future123 = futureA.thenAcceptBothAsync(futureB, consumer, myExecutor);
        future123.join();
        System.err.println();
        System.err.println("此时，futureA的结果为:" + futureA.join());
        System.err.println("此时，futureB的结果为:" + futureB.join());
    }
    
    
    
    /// ********************************************************************* thenApply​、thenApplyAsync​
    
    /**
     * public <U> CompletableFuture<U> thenApply(Function<? super T,? extends U> fn): 当future正常完成时， 以此future的完成返回值作为fn的参数， (使用与执行future相同的线程)执行fn的逻辑
     *
     * public <U> CompletableFuture<U> thenApplyAsync(Function<? super T,? extends U> fn): 当future正常完成时， 以此future的完成返回值作为fn的参数， (使用默认的线程池ForkJoinPool.commonPool提供的线程)执行fn的逻辑
     *
     * public <U> CompletableFuture<U> thenApplyAsync(Function<? super T,? extends U> fn, Executor executor): 当future正常完成时， 以此future的完成返回值作为fn的参数， (使用给定的线程池executor提供的线程)执行fn的逻辑
     *
     * 注: 有消费返回值、有future返回值。
     * 注: 1. 若原线程执行到thenApply时，future就已经完成了， 那么以原线程(这里为main线程)执行fn的逻辑。
     *     2. 若原线程执行到thenApplyAsync时，future就已经完成了， 那么还是会以默认的线程池ForkJoinPool.commonPool提供的线程来执行fn的逻辑
     *     3. 若原线程执行到thenApplyAsync时，future就已经完成了， 那么还是会以给定的线程池executor提供的线程来执行fn的逻辑
     *
     * (打开A处)输出:
     * ForkJoinPool.commonPool-worker-1	futureA
     * ForkJoinPool.commonPool-worker-1	futureA状态 => java.util.concurrent.CompletableFuture@58f2bc9a[Completed
     * normally]
     * main	futureB结果 => 3
     *
     * (注释A处)输出:
     * ForkJoinPool.commonPool-worker-1	futureA
     * main	futureA状态 => java.util.concurrent.CompletableFuture@b4711e2[Completed normally]
     * main	futureB结果 => 3
     */
    @Test
    public void thenApplyTest1() throws Exception {
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2); // A处
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.err.println(Thread.currentThread().getName() + "\tfutureA");
            return "abc";
        });
    
        Function<String, Integer> function = (String param) -> {
            System.err.println(Thread.currentThread().getName() + "\tfutureA状态 => " + futureA.toString());
            String t = Thread.currentThread().getName();
            return param.length();
        };
        CompletableFuture<Integer> futureB = futureA.thenApply(function);
        System.err.println(Thread.currentThread().getName() + "\tfutureB结果 => " + futureB.join());
    }
    
    /**
     * 测试: 当future非正常完成时, 是不会走fn的逻辑的。
     *
     * 输出:
     * ForkJoinPool.commonPool-worker-1	futureA
     *
     * java.util.concurrent.CompletionException: java.lang.ArithmeticException: / by zero
     *
     * 	at java.util.concurrent.CompletableFuture.encodeThrowable(CompletableFuture.java:273)
     * 	(省略...)
     */
    @Test
    public void thenApplyTest2() throws Exception {
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> {
            System.err.println(Thread.currentThread().getName() + "\tfutureA");
            int a = 1 / 0;
            return "abc";
        });
        
        Function<String, Integer> function = (String param) -> {
            System.err.println(Thread.currentThread().getName() + "\tfutureA状态 => " + futureA.toString());
            String t = Thread.currentThread().getName();
            return param.length();
        };
        CompletableFuture<Integer> futureB = futureA.thenApply(function);
        System.err.println(Thread.currentThread().getName() + "\tfutureB结果 => " + futureB.join());
    }
    
    /**
     * (打开A处 or 注释A处时均)输出:
     * ForkJoinPool.commonPool-worker-1	futureA
     * ForkJoinPool.commonPool-worker-1	futureA状态 => java.util.concurrent.CompletableFuture@4308b146[Completed normally]
     * main	futureB结果 => 3
     */
    @Test
    public void thenApplyAsyncTest1() throws Exception {
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2); // A处
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.err.println(Thread.currentThread().getName() + "\tfutureA");
            return "abc";
        });
        
        Function<String, Integer> function = (String param) -> {
            System.err.println(Thread.currentThread().getName() + "\tfutureA状态 => " + futureA.toString());
            String t = Thread.currentThread().getName();
            return param.length();
        };
        CompletableFuture<Integer> futureB = futureA.thenApplyAsync(function);
        System.err.println(Thread.currentThread().getName() + "\tfutureB结果 => " + futureB.join());
    }
    
    /**
     * (打开A处 or 注释A处时均)输出:
     * ForkJoinPool.commonPool-worker-1	futureA
     * pool-1-thread-1	futureA状态 => java.util.concurrent.CompletableFuture@c5979e4[Completed normally]
     * main	futureB结果 => 3
     */
    @Test
    public void thenApplyAsyncTest2() throws Exception {
        Executor myExecutor = Executors.newFixedThreadPool(3);
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2); // A处
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.err.println(Thread.currentThread().getName() + "\tfutureA");
            return "abc";
        });
        
        Function<String, Integer> function = (String param) -> {
            System.err.println(Thread.currentThread().getName() + "\tfutureA状态 => " + futureA.toString());
            String t = Thread.currentThread().getName();
            return param.length();
        };
        CompletableFuture<Integer> futureB = futureA.thenApplyAsync(function, myExecutor);
        System.err.println(Thread.currentThread().getName() + "\tfutureB结果 => " + futureB.join());
    }
    
    /// ********************************************************************* thenCombine、thenCombineAsync
    
    /**
     * public <U,V> CompletableFuture<V> thenCombine(CompletionStage<? extends U> other, BiFunction<? super T,? super U,? extends V> fn)：
     * 当this和other这两个future全部都正常完成后， 以这两个future的结果为参数， (使用后完成的那个future的线程)执行fn
     *
     * public <U,V> CompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T,? super U,? extends V> fn):
     * 当this和other这两个future全部都正常完成后， 以这两个future的结果为参数， (使用默认的线程池ForkJoinPool.commonPool分配的线程)执行fn
     *
     * public <U,V> CompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T,? super U,? extends V> fn, Executor executor):
     * 当this和other这两个future全部都正常完成后， 以这两个future的结果为参数， (使用指定的线程池executor分配的线程)执行fn
     *
     * 注: 有消费返回值、有future返回值。
     * 注: 若原线程执行到thenCombine时， this任务已经完成，那么使用原线程(这里为main线程)执行fn
     *
     *
     * (打开A处， 注释B处输出)输出:                     // 验证由后执行完的那个任务的所在线程来执行fn
     * ForkJoinPool.commonPool-worker-2	futureB
     * ForkJoinPool.commonPool-worker-1	futureA
     *
     * futureA状态 => java.util.concurrent.CompletableFuture@28e12ebc[Completed normally],
     * futureB状态 => java.util.concurrent.CompletableFuture@2626d5d0[Completed normally]
     * ForkJoinPool.commonPool-worker-1	fn
     * 此时，future123的结果为:1.3
     *
     *
     * (打开B处， 注释A处输出)输出:                     // 验证由后执行完的那个任务的所在线程来执行fn
     * ForkJoinPool.commonPool-worker-1	futureA
     * ForkJoinPool.commonPool-worker-2	futureB
     *
     * futureA状态 => java.util.concurrent.CompletableFuture@6770ab1c[Completed normally],
     * futureB状态 => java.util.concurrent.CompletableFuture@69b75982[Completed normally]
     * ForkJoinPool.commonPool-worker-2	fn
     * 此时，future123的结果为:1.3
     *
     * (打开C处)输出:                                // 验证当原线程执行到thenCombine时， 若两个future都已经 执行完毕了， 那么由原线程(这里为main线程)执行fn
     * ForkJoinPool.commonPool-worker-2	futureB
     * ForkJoinPool.commonPool-worker-1	futureA
     *
     * futureA状态 => java.util.concurrent.CompletableFuture@70f02c32[Completed normally],
     * futureB状态 => java.util.concurrent.CompletableFuture@62010f5c[Completed normally]
     * main	fn
     *
     * 此时，future123的结果为:1.3
     */
    @Test
    public void thenCombineTest1() throws Exception {
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2); // A处
                //TimeUnit.SECONDS.sleep(1); // B处
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.err.println(Thread.currentThread().getName() + "\tfutureA");
                    return "futureA";
                }
        );
    
        CompletableFuture<Integer> futureB = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1); // A处
                //TimeUnit.SECONDS.sleep(2); // B处
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.err.println(Thread.currentThread().getName() + "\tfutureB");
                    return "futureB".length();
                }
        );
    
        futureA.join(); // C处
        futureB.join(); // C处
        BiFunction<String, Integer, Double> biFunction = (@NonNull String x, @NonNull Integer y) -> {
            System.err.println();
            System.err.println("futureA状态 => " + futureA.toString() + ",\nfutureB状态 => " + futureB.toString());
            System.err.println(Thread.currentThread().getName() + "\tfn");
            return x.length() * 1.3 /  y;
        };
        
        CompletableFuture<Double> future123 = futureA.thenCombine(futureB, biFunction);
        System.err.println();
        System.err.println("此时，future123的结果为:" + future123.join());
    }
    
    /**
     * 输出:    // 验证： 若this或other 中任意一个非正常完整， 那么是不会走fn的
     * main
     * ForkJoinPool.commonPool-worker-2	futureB
     *
     * java.util.concurrent.CompletionException: java.lang.UnknownError: futureA异常了
     *
     * 	at java.util.concurrent.CompletableFuture.encodeThrowable(CompletableFuture.java:273)
     * 	at java.util.concurrent.CompletableFuture.completeThrowable(CompletableFuture.java:280)
     * 	at java.util.concurrent.CompletableFuture$AsyncSupply.run(CompletableFuture.java:1592)
     * 	at java.util.concurrent.CompletableFuture$AsyncSupply.exec(CompletableFuture.java:1582)
     * 	at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)
     * 	at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1056)
     * 	at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692)
     * 	at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)
     * Caused by: java.lang.UnknownError: futureA异常了
     * 	at com.aspire.demo.CompletableFutureDemo.lambda$thenCombineAsyncTest2$126(CompletableFutureDemo.java:2803)
     * 	at java.util.concurrent.CompletableFuture$AsyncSupply.run(CompletableFuture.java:1590)
     * 	... 5 more
     */
    @Test
    public void thenCombineTest2() throws Exception {
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    throw new UnknownError("futureA异常了");
                }
        );
        
        CompletableFuture<Integer> futureB = CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.err.println(Thread.currentThread().getName() + "\tfutureB");
                    return "futureB".length();
                }
        );
        
        BiFunction<String, Integer, Double> biFunction = (@NonNull String x, @NonNull Integer y) -> {
            System.err.println();
            System.err.println("futureA状态 => " + futureA.toString() + ",\nfutureB状态 => " + futureB.toString());
            System.err.println(Thread.currentThread().getName() + "\tfn");
            return x.length() * 1.3 /  y;
        };
        System.err.println();
        System.err.println(Thread.currentThread().getName());
        CompletableFuture<Double> future123 = futureA.thenCombine(futureB, biFunction);
        System.err.println("此时，future123的结果为:" + future123.join());
    }
    
    /**
     * 输出:
     * ForkJoinPool.commonPool-worker-2	futureB
     * ForkJoinPool.commonPool-worker-1	futureA
     *
     * futureA状态 => java.util.concurrent.CompletableFuture@28e12ebc[Completed normally],
     * futureB状态 => java.util.concurrent.CompletableFuture@2626d5d0[Completed normally]
     * ForkJoinPool.commonPool-worker-1	fn
     * 此时，future123的结果为:1.3
     */
    @Test
    public void thenCombineAsyncTest1() throws Exception {
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> {
                    System.err.println(Thread.currentThread().getName() + "\tfutureA");
                    return "futureA";
                }
        );
        
        CompletableFuture<Integer> futureB = CompletableFuture.supplyAsync(() -> {
                    System.err.println(Thread.currentThread().getName() + "\tfutureB");
                    return "futureB".length();
                }
        );
        BiFunction<String, Integer, Double> biFunction = (@NonNull String x, @NonNull Integer y) -> {
            System.err.println();
            System.err.println("futureA状态 => " + futureA.toString() + ",\nfutureB状态 => " + futureB.toString());
            System.err.println(Thread.currentThread().getName() + "\tfn");
            return x.length() * 1.3 /  y;
        };
        
        CompletableFuture<Double> future123 = futureA.thenCombineAsync(futureB, biFunction);
        System.err.println();
        System.err.println("此时，future123的结果为:" + future123.join());
    }
    
    /**
     * 输出:
     * ForkJoinPool.commonPool-worker-1	futureA
     * ForkJoinPool.commonPool-worker-1	futureB
     *
     *
     * futureA状态 => java.util.concurrent.CompletableFuture@4b623218[Completed normally],
     * futureB状态 => java.util.concurrent.CompletableFuture@45a5c504[Completed normally]
     * pool-1-thread-1	fn
     * 此时，future123的结果为:1.3
     */
    @Test
    public void thenCombineAsyncTest2() throws Exception {
        Executor myExecutor = Executors.newFixedThreadPool(3);
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> {
                    System.err.println(Thread.currentThread().getName() + "\tfutureA");
                    return "futureA";
                }
        );
        
        CompletableFuture<Integer> futureB = CompletableFuture.supplyAsync(() -> {
                    System.err.println(Thread.currentThread().getName() + "\tfutureB");
                    return "futureB".length();
                }
        );
        BiFunction<String, Integer, Double> biFunction = (@NonNull String x, @NonNull Integer y) -> {
            System.err.println();
            System.err.println("futureA状态 => " + futureA.toString() + ",\nfutureB状态 => " + futureB.toString());
            System.err.println(Thread.currentThread().getName() + "\tfn");
            return x.length() * 1.3 /  y;
        };
        
        CompletableFuture<Double> future123 = futureA.thenCombineAsync(futureB, biFunction, myExecutor);
        System.err.println();
        System.err.println("此时，future123的结果为:" + future123.join());
    }
    
    /// ********************************************************************* thenCompose、thenComposeAsync
    
    /**
     * public <U> CompletableFuture<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn)：
     * 当this正常完成时， 以this的完成值为参数， (使用完成this任务的这个相同的线程)创建一个新的CompletableFuture
     *
     * public <U> CompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn)：
     * 当this正常完成时， 以this的完成值为参数， (使用默认线程池ForkJoinPool.commonPool分配的线程)创建一个新的CompletableFuture
     *
     * public <U> CompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn, Executor executor)：
     * 当this正常完成时， 以this的完成值为参数， (使用指定的线程池executor分配的线程)创建一个新的CompletableFuture
     *
     * 注: 有消费返回值、有future返回值。
     * 注: 若this非正常完成，那么是不会走fn的逻辑的。
     * 注: 若原线程执行到thenCompose时， this任务已经完成，那么使用原线程(这里为main线程)执行fn。
     *
     *
     * (打开A处时)输出:    // 验证: 验证若this非正常完成，那么是不会走fn的逻辑的。
     * java.util.concurrent.CompletionException: java.lang.ArithmeticException: / by zero
     *
     * 	at java.util.concurrent.CompletableFuture.encodeThrowable(CompletableFuture.java:273)
     * 	at java.util.concurrent.CompletableFuture.completeThrowable(CompletableFuture.java:280)
     * 	at java.util.concurrent.CompletableFuture$AsyncSupply.run(CompletableFuture.java:1592)
     * 	at java.util.concurrent.CompletableFuture$AsyncSupply.exec(CompletableFuture.java:1582)
     * 	at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)
     * 	at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1056)
     * 	at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692)
     * 	at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)
     * Caused by: java.lang.ArithmeticException: / by zero
     * 	at com.aspire.demo.CompletableFutureDemo.lambda$thenComposeTest1$135(CompletableFutureDemo.java:2948)
     * 	at java.util.concurrent.CompletableFuture$AsyncSupply.run(CompletableFuture.java:1590)
     * 	... 5 more
     *
     * (注释A处， 打开B处时)输出:    // 验证: 若原线程执行到thenCompose时， this任务已经完成，那么使用原线程(这里为main线程)执行fn
     * ForkJoinPool.commonPool-worker-1	futureA
     * main	fn: futureA状态 => java.util.concurrent.CompletableFuture@b4711e2[Completed normally]
     *
     * 此时，futureB的结果为:7
     *
     * (注释A处， 注释B处时)输出:    // 验证: 使用完成this任务的这个相同的线程，创建一个新的CompletableFuture
     * ForkJoinPool.commonPool-worker-1	futureA
     * ForkJoinPool.commonPool-worker-1	fn: futureA状态 => java.util.concurrent.CompletableFuture@470d2807[Completed
     * normally]
     * 此时，futureB的结果为:7
     */
    @Test
    public void thenComposeTest1() throws Exception {
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    int a = 1 / 0; // A处
                    System.err.println(Thread.currentThread().getName() + "\tfutureA");
                    return "futureA";
                }
        );
//        TimeUnit.SECONDS.sleep(3); // B处
        Function<String, CompletableFuture<Integer>> function = (String t) -> {
            System.err.println(Thread.currentThread().getName() + "\tfn: futureA状态 => " + futureA.toString());
            CompletableFuture<Integer> newCompletionStage = CompletableFuture.supplyAsync(() -> {
                return t.length();
            });
            return newCompletionStage;
        };
    
        CompletableFuture<Integer> futureB = futureA.thenCompose(function);
        System.err.println();
        System.err.println("此时，futureB的结果为:" + futureB.join());
    }
    
    /**
     * 输出:
     * ForkJoinPool.commonPool-worker-1	futureA
     * ForkJoinPool.commonPool-worker-1	fn: futureA状态 => java.util.concurrent.CompletableFuture@1d509c38[Completed
     * normally]
     * 此时，futureB的结果为:7
     */
    @Test
    public void thenComposeAsyncTest1() throws Exception {
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.err.println(Thread.currentThread().getName() + "\tfutureA");
                    return "futureA";
                }
        );
        Function<String, CompletableFuture<Integer>> function = (String t) -> {
            System.err.println(Thread.currentThread().getName() + "\tfn: futureA状态 => " + futureA.toString());
            CompletableFuture<Integer> newCompletionStage = CompletableFuture.supplyAsync(() -> {
                return t.length();
            });
            return newCompletionStage;
        };
        
        CompletableFuture<Integer> futureB = futureA.thenComposeAsync(function);
        System.err.println();
        System.err.println("此时，futureB的结果为:" + futureB.join());
    }
    
    /**
     * 输出:
     * ForkJoinPool.commonPool-worker-1	futureA
     * pool-1-thread-1	fn: futureA状态 => java.util.concurrent.CompletableFuture@779057db[Completed normally]
     * 此时，futureB的结果为:7
     */
    @Test
    public void thenComposeAsyncTest2() throws Exception {
        Executor myExecutor = Executors.newFixedThreadPool(3);
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.err.println(Thread.currentThread().getName() + "\tfutureA");
                    return "futureA";
                }
        );
        Function<String, CompletableFuture<Integer>> function = (String t) -> {
            System.err.println(Thread.currentThread().getName() + "\tfn: futureA状态 => " + futureA.toString());
            CompletableFuture<Integer> newCompletionStage = CompletableFuture.supplyAsync(() -> {
                return t.length();
            });
            return newCompletionStage;
        };
        
        CompletableFuture<Integer> futureB = futureA.thenComposeAsync(function, myExecutor);
        System.err.println();
        System.err.println("此时，futureB的结果为:" + futureB.join());
    }
    
    
    /// ********************************************************************* thenRun、thenRunAsync
    
    /**
     * public CompletableFuture<Void> thenRun(Runnable action):
     * 当future正常完成时， (使用完成future任务的这个相同的线程)执行action
     *
     * public CompletableFuture<Void> thenRunAsync(Runnable action):
     * 当future正常完成时， (使用默认线程池ForkJoinPool.commonPool分配的线程)执行action
     *
     * public CompletableFuture<Void> thenRunAsync(Runnable action, Executor executor):
     * 当future正常完成时， (使用指定的线程池executor分配的线程)执行action
     *
     *
     * 注: 无消费返回值、无future返回值。
     * 注: 若future非正常完成，那么是不会走action的逻辑的。
     * 注: 若原线程执行到thenRun时， future任务已经完成，那么使用原线程(这里为main线程)执行action。
     *
     *
     * (打开A处时，)输出：      验证：若future非正常完成，那么是不会走action的逻辑的
     * java.util.concurrent.CompletionException: java.lang.ArithmeticException: / by zero
     *
     * 	at java.util.concurrent.CompletableFuture.encodeThrowable(CompletableFuture.java:273)
     * 	at java.util.concurrent.CompletableFuture.completeThrowable(CompletableFuture.java:280)
     * 	at java.util.concurrent.CompletableFuture$AsyncSupply.run(CompletableFuture.java:1592)
     * 	at java.util.concurrent.CompletableFuture$AsyncSupply.exec(CompletableFuture.java:1582)
     * 	at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)
     * 	at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1056)
     * 	at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692)
     * 	at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)
     * Caused by: java.lang.ArithmeticException: / by zero
     * 	at com.aspire.demo.CompletableFutureDemo.lambda$thenRunTest1$144(CompletableFutureDemo.java:3084)
     * 	at java.util.concurrent.CompletableFuture$AsyncSupply.run(CompletableFuture.java:1590)
     * 	... 5 more
     *
     * (注释A处， 注释B处时，)输出：   验证:(使用完成future任务的这个相同的线程)执行action
     * ForkJoinPool.commonPool-worker-1	futureA
     * ForkJoinPool.commonPool-worker-1	runnable
     * 此时，futureB的结果为:null
     *
     * (注释A处， 打开B处时，)输出：   验证:若原线程执行到thenRun时， future任务已经完成，那么使用原线程(这里为main线程)执行action
     * ForkJoinPool.commonPool-worker-1	futureA
     * main	runnable
     * 此时，futureB的结果为:null
     */
    @Test
    public void thenRunTest1() throws Exception {
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // nt a = 1 / 0; // A处
                    System.err.println(Thread.currentThread().getName() + "\tfutureA");
                    return "futureA";
                }
        );
        TimeUnit.SECONDS.sleep(2); // B处
        Runnable runnable = () -> System.err.println(Thread.currentThread().getName() + "\trunnable");

        CompletableFuture<Void> futureB = futureA.thenRun(runnable);
        System.err.println("此时，futureB的结果为:" + futureB.join());
    }
    
    /**
     * 输出:
     * ForkJoinPool.commonPool-worker-1	futureA
     * ForkJoinPool.commonPool-worker-1	runnable
     * 此时，futureB的结果为:null
     */
    @Test
    public void thenRunAsyncTest1() throws Exception {
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.err.println(Thread.currentThread().getName() + "\tfutureA");
                    return "futureA";
                }
        );
        Runnable runnable = () -> System.err.println(Thread.currentThread().getName() + "\trunnable");
        
        CompletableFuture<Void> futureB = futureA.thenRunAsync(runnable);
        System.err.println("此时，futureB的结果为:" + futureB.join());
    }
    
    /**
     * 输出:
     * ForkJoinPool.commonPool-worker-1	futureA
     * pool-1-thread-1	runnable
     * 此时，futureB的结果为:null
     */
    @Test
    public void thenRunAsyncTest2() throws Exception {
        Executor myExecutor = Executors.newFixedThreadPool(3);
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.err.println(Thread.currentThread().getName() + "\tfutureA");
                    return "futureA";
                }
        );
        Runnable runnable = () -> System.err.println(Thread.currentThread().getName() + "\trunnable");
        
        CompletableFuture<Void> futureB = futureA.thenRunAsync(runnable, myExecutor);
        System.err.println("此时，futureB的结果为:" + futureB.join());
    }
    
    
    
    
    /// ********************************************************************* toCompletableFuture
    
    /**
     * public CompletableFuture<T> toCompletableFuture(): 返回当前future对象自身。
     *
     * 输出:
     * futureA:	java.util.concurrent.CompletableFuture@2796aeae[Not completed]
     * futureB:	java.util.concurrent.CompletableFuture@2796aeae[Not completed]
     * futureA == futureB结果为：	true
     */
    @Test
    public void toCompletableFutureTest() throws Exception {
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.err.println(Thread.currentThread().getName() + "\tfutureA");
                    return "futureA";
                }
        );
        CompletableFuture<String> futureB = futureA.toCompletableFuture();
        System.err.println("futureA:\t" + futureB.toString());
        System.err.println("futureB:\t" + futureB.toString());
        System.err.println("futureA == futureB结果为：\t" + (futureA == futureB));
    }
    

}