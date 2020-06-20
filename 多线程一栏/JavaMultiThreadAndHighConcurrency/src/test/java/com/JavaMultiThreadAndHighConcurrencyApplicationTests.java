package com;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JavaMultiThreadAndHighConcurrencyApplicationTests {

    @Test
    public void contextLoads() {
    }

    private static Integer threadNum = 400;

    private static Integer count = 0;

    //    private static AtomicInteger count = new AtomicInteger(0);
    public static void main(String[] args) throws Exception {

        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        for (int i = 0; i < threadNum; i++) {
            final int index = i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(index + "");
                    System.out.println(list + "\t" + list.hashCode());
                }
            });
        }
        executorService.shutdown();
        Thread.sleep(2000);
        System.out.println("----------------> over!");

    }


}
