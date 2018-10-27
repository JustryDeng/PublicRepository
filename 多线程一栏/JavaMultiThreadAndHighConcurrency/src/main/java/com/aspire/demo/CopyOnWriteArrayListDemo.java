package com.aspire.demo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CopyOnWriteArrayListD使用示例
 *
 * @author JustryDeng
 * @date 2018/10/23 12:26
 */
public class CopyOnWriteArrayListDemo {

    private static List<Integer> nonSafeList = new ArrayList<>(4);

    private static List<Integer> safeList = new CopyOnWriteArrayList<>();

    private static CountDownLatch countDownLatch1 = new CountDownLatch(10000);

    private static CountDownLatch countDownLatch2 = new CountDownLatch(10000);

    /**
     * 多线程时不安全的类ArrayList测试
     *
     * @date 2018/10/23 12:35
     */
    private static void fa1() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10000; i++) {
            final int index = i;
            executorService.execute(() -> {
                try {
                    nonSafeList.add(index);
                } finally {
                    countDownLatch1.countDown();
                }
            });
        }
        executorService.shutdown();
        countDownLatch1.await();
        System.out.println(nonSafeList.size());
    }


    /**
     * 多线程时安全的类CopyOnWriteArrayList测试
     *
     * @date 2018/10/23 12:35
     */
    private static void fa2() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10000; i++) {
            final int index = i;
            executorService.execute(() -> {
                try {
                    safeList.add(index);
                } finally {
                    countDownLatch2.countDown();
                }
            });
        }
        executorService.shutdown();
        countDownLatch2.await();
        System.out.println(safeList.size());
    }

    /**
     * 主函数
     */
    public static void main(String[] args) throws InterruptedException {
        fa1();
        fa2();
    }
}

/**
 * 小知识 --- 循环中删除元素
 * 注:fa1()、fa2()为错误示例
 * 注:fa3()、fa4()为正确示例
 *
 * @author JustryDeng
 * @date 2018/10/23 13:54
 */
class DeleteListItemDemo {

    /**
     * 错误示例
     */
    private static void fa1() {
        List<String> list = new ArrayList<>(16);
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        list.add("F");
        list.add("G");

        // -> foreach中删除元素会出异常
        for (String item : list) {
            if ("C".equals(item)) {
                list.remove("C");
            }
        }
        System.out.println(list);
    }

    /**
     * 错误示例
     */
    private static void fa2() {
        List<String> list = new ArrayList<>(16);
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        list.add("F");
        list.add("G");
        // -> fori中删除元素会出异常、或 出现乱删等情况
        for (int i = 1; i <= list.size(); i++) {
            if (i % 2 == 1) {
                list.remove(i - 1);
            }
            if ("C".equals(list.get(i))) {
                list.remove("C");
            }
        }
        System.out.println(list);
    }

    /**
     * Iterator正确示例
     */
    private static void fa3() {
        List<String> list = new ArrayList<>(16);
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        list.add("F");
        list.add("G");
        Iterator<String> item = list.iterator();
        while (item.hasNext()) {
            String str = item.next();
            if ("A".equals(str) || "D".equals(str)) {
                item.remove();
            }
        }
        System.out.println(list);
    }

    /**
     * CopyOnWriteArrayList正确示例
     * 注:使用CopyOnWriteArrayList时，仍然不能使用fori循环移除元素，但是可以使用foreach移除元素
     */
    private static void fa4() {
        List<String> list = new CopyOnWriteArrayList<>();
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        list.add("F");
        list.add("G");
        for (String item : list) {
            if ("A".equals(item)) {
                list.remove("A");
            }
            if ("C".equals(item)) {
                list.remove("C");
            }
            if ("E".equals(item)) {
                list.remove("E");
            }
        }
        System.out.println(list);
    }

    /**
     * 主函数
     */
    public static void main(String[] args) {
        fa4();
    }
}


