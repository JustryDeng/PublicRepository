package com.aspire.demo;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * ConcurrentHashMap与HashMap测试比较
 *
 * @author JustryDeng
 * @date 2018/10/24 0:22
 */
public class ConcurrentHashMapDemo {

    private static int threadNum = 8000;

    /** 为保证不干扰,fa1()-fa4()各自使用一个 */
    private static Map<String, Object> hashMap1 = new HashMap<>(16);
    private static Map<String, Object> concurrentHashMap2 = new ConcurrentHashMap<>(16);
    private static Map<String, Object> hashMap3 = new HashMap<>(16);
    private static Map<String, Object> concurrentHashMap4 = new ConcurrentHashMap<>(16);

    /** 为保证不干扰,fa1()-fa4()各自使用一个 */
    private static CyclicBarrier cyclicBarrier1 = new CyclicBarrier(threadNum);
    private static CyclicBarrier cyclicBarrier2 = new CyclicBarrier(threadNum);
    private static CyclicBarrier cyclicBarrier3 = new CyclicBarrier(threadNum);
    private static CyclicBarrier cyclicBarrier4 = new CyclicBarrier(threadNum);

    /** 为保证不干扰,fa1()-fa4()各自使用一个 */
    private static CountDownLatch countDownLatch1 = new CountDownLatch(threadNum);
    private static CountDownLatch countDownLatch2 = new CountDownLatch(threadNum);
    private static CountDownLatch countDownLatch3 = new CountDownLatch(threadNum);
    private static CountDownLatch countDownLatch4 = new CountDownLatch(threadNum);

    /** fa3()会使用到的竞争锁对象 */
    private static final Object lock = new Object();

    /**
     * HashMap安全性测试
     * 注:由于HashMap并不安全，运行此方法时，除了不一定能得到理想的结果外，还可能会出现ClassCastException等异常
     */
    private static void fa1() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < threadNum; i++) {
            final int index = i;
            executorService.execute(() -> {
                try {
                    cyclicBarrier1.await();
                    hashMap1.put("key" + index, "value" + index);
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch1.countDown();
                }
            });
        }
        executorService.shutdown();
        countDownLatch1.await();
        System.out.println("fa1() -> hashMap1尺寸为:" + hashMap1.size());
    }

    /**
     * ConcurrentHashMap安全性测试
     */
    private static void fa2() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < threadNum; i++) {
            final int index = i;
            executorService.execute(() -> {
                try {
                    cyclicBarrier2.await();
                    concurrentHashMap2.put("key" + index, "value" + index);

                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch2.countDown();
                }
            });
        }
        executorService.shutdown();
        countDownLatch2.await();
        System.out.println("fa2() -> concurrentHashMap2尺寸为:" + concurrentHashMap2.size());
    }

    /**
     * HashMap性能测试
     */
    private static void fa3() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < threadNum; i++) {
            final int index = i;
            executorService.execute(() -> {
                try {
                    cyclicBarrier3.await();
                    synchronized (lock) {
                        hashMap3.put("key" + index, "value" + index);
                    }
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch3.countDown();
                }
            });
        }
        executorService.shutdown();
        countDownLatch3.await();
        long endTime = System.currentTimeMillis();
        System.out.print("fa3() -> hashMap3尺寸为:" + hashMap3.size());
        System.out.println("\t 耗时:" + (endTime - startTime) * 1.0 / 1000 + "秒！");
    }

    /**
     * ConcurrentHashMap性能测试
     */
    private static void fa4() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < threadNum; i++) {
            final int index = i;
            executorService.execute(() -> {
                try {
                    cyclicBarrier4.await();
                    concurrentHashMap4.put("key" + index, "value" + index);
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch4.countDown();
                }
            });
        }
        executorService.shutdown();
        countDownLatch4.await();
        long endTime = System.currentTimeMillis();
        System.out.print("fa4() -> concurrentHashMap4尺寸为:" + concurrentHashMap4.size());
        System.out.println("\t 耗时:" + (endTime - startTime) * 1.0 / 1000 + "秒！");
    }


    /**
     * 主函数
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("[线程安全] ---> 测试");
        fa1();
        fa2();
        System.out.println();
        System.out.println("[效率] ---> 测试");
        fa3();
        fa4();
    }
}

/**
 * 应用示例
 *        之
 *          多线程读取多个excel文件并以ConcurrentHashMap存储数据
 *
 * 注:重点是在多线程并发读取数据并操作同一个ConcurrentHashMap实例，所以这里Excel导入就简化处理了;
 *    对excel导入感兴趣的可以去看我的这篇博客 https://blog.csdn.net/justry_deng/article/details/82833508
 *
 * @date 2018/10/25 10:54
 */
class ApplicatioExample {

    /** 以ConcurrentHashMap作为数据容器 */
    private static ConcurrentHashMap<String, List<Object>> concurrentHashMap = new ConcurrentHashMap<>(16);

    /** 线程数 */
    private static int threadNum = 2;

    /** 要导入的excel文件 */
    private static File[] files = new File[threadNum];

    /** 倒计时锁 */
    private static CountDownLatch countDownLatch = new CountDownLatch(threadNum);

    /**
     * 多线程时推荐使用实现Callable<>接口的方式,这里为了快速示例，就简单处理了
     */
    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        files[0] = new File("C:/Users/JustryDeng/Desktop/excelA.xlsx");
        files[1] = new File("C:/Users/JustryDeng/Desktop/excelB.xlsx");
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        for (int i = 0; i < threadNum; i++) {
            final int index = i;
            executorService.execute(() -> {
                try {
                    readExcel(files[index]);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        executorService.shutdown();
        countDownLatch.await();
        long endTime = System.currentTimeMillis();
        for (Map.Entry<String, List<Object>> entry :concurrentHashMap.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }
        System.out.println("多线程读取多个excel文件结束！\t 耗时:" + (endTime - startTime) * 1.0 / 1000 + "毫秒！");
    }




    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * excel读取(只读取第一个Sheet)
     */
    public static void readExcel(File excelFile) throws IOException {
        String fileName = excelFile.getName();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        Workbook wb = null;
        try {
            if ("xls".equals(suffix)) {
                wb = new HSSFWorkbook(new FileInputStream(excelFile));
            } else if ("xlsx".equals(suffix)) {
                wb = new XSSFWorkbook(new FileInputStream(excelFile));
            } else {
                throw new IllegalArgumentException("Invalid excel version");
            }
            Sheet sheet = wb.getSheetAt(0);
            List<Object> rowDataList = new ArrayList<>(8);
            int totalRowCount = sheet.getPhysicalNumberOfRows();
            int alreadyReadRowTotal = 0;
            for (int i = sheet.getFirstRowNum(); alreadyReadRowTotal < totalRowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null || row.getFirstCellNum() < 0) {
                    continue;
                }
                int readColumnCount = (int) row.getLastCellNum();
                // 以excel每一行的第一列的cell值为key,对应的行数据集合为value
                Object keyFlag = getCellValue(row.getCell(0));
                List<Object> rowValue = new ArrayList<>();
                for (int k = 0; k < readColumnCount; k++) {
                    Cell cell = row.getCell(k);
                    rowValue.add(getCellValue(cell));
                }
                if (keyFlag != null) {
                    // 将输入放入ConcurrentHashMap中
                    concurrentHashMap.put(keyFlag.toString(), rowValue);
                }
                alreadyReadRowTotal++;
            }
        } finally {
            if (wb != null) {
                wb.close();
            }
        }
    }

    private static Object getCellValue(Cell cell) {
        Object value = null;
        if (cell != null) {
            CellType ct = cell.getCellTypeEnum();
            if (ct == CellType.STRING) {
                value = cell.getStringCellValue();
            } else if (ct == CellType.NUMERIC) {
                if(HSSFDateUtil.isCellDateFormatted(cell)) {
                    value = simpleDateFormat.format(cell.getDateCellValue());
                    return value;
                }
                cell.setCellType(CellType.STRING);
                value = cell.getStringCellValue();
            } else if (ct == CellType.BOOLEAN) {
                value = cell.getBooleanCellValue();
            } else if (ct == CellType.BLANK) {
                // 如果Cell中无内容,则设置其值为null;
                value = null;
            } else {
                value = cell.toString();
            }
        }
        return value;
    }

}
