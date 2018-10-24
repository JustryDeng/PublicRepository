package com.aspire.util;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * EXCEL导入工具类
 *
 * @author JustryDeng
 * @DATE 2018年9月24日 下午7:38:34
 */
public class ExcelImportUtil {

    /** EXCEL2003后缀名 */
    private final static String EXCEL_VERSION_2003_SUFFIX = "xls";

    /** EXCEL2007后缀名 */
    private final static String EXCEL_VERSION_2007_SUFFIX = "xlsx";

    /** 日期格式 */
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * excel读取
     *
     * @param excelFile
     *            xls或xlsx文件
     * @param rowCount
     *            要读取的总列数;为null或大于实际总行数时,则读取实际总行数
     * @param columnCount
     *            要读取的总列数;为null或大于实际列数时,则读取实际总行数
     * @return Sheet集合
     * @DATE 2018年9月24日 下午9:44:05
     */
    public static List<ExcelSheetVO> readExcel(File excelFile, Integer rowCount, Integer columnCount)
            throws IOException {

        // 获取文件的后缀名
        String fileName = excelFile.getName();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        System.out.println(" this file's suffix is ---> " + suffix);

        // 此集合用于存放多个sheet的数据
        List<ExcelSheetVO> sheetList = new ArrayList<>();

        // 根据不同的后缀名(不同版本的excel)创建不同的Workbook
        Workbook wb = null;
        try {
            if (EXCEL_VERSION_2003_SUFFIX.equals(suffix)) {
                wb = new HSSFWorkbook(new FileInputStream(excelFile));
            } else if (EXCEL_VERSION_2007_SUFFIX.equals(suffix)) {
                wb = new XSSFWorkbook(new FileInputStream(excelFile));
            } else {
                throw new IllegalArgumentException("Invalid excel version");
            }


            // 循环遍历每一个sheet
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {

                Sheet sheet = wb.getSheetAt(i);
                ExcelSheetVO excelSheetVO = new ExcelSheetVO();

                excelSheetVO.setSheetName(sheet.getSheetName());
                List<List<Object>> dataList = new ArrayList<>(16);
                // 因为 引用的传递,所以我们可以先进行此操作
                excelSheetVO.setDataList(dataList);
                System.out.println(" first row num is ---> " + sheet.getFirstRowNum());
                System.out.println(" a total of ---> " + sheet.getPhysicalNumberOfRows());
                int readRowCount;
                // 要读取的总行数设置
                if (rowCount == null || rowCount > sheet.getPhysicalNumberOfRows()) {
                    // 总行数
                    readRowCount = sheet.getPhysicalNumberOfRows();
                } else {
                    readRowCount = rowCount;
                }

                // 已经读取了的行数,标识
                int alreadyReadRowTotal = 0;
                // 循环遍历sheet的每一行
                // rowNum与index类似,从0开始(即: 0 对应第一行)
                for (int j = sheet.getFirstRowNum(); alreadyReadRowTotal < readRowCount; j++) {
                    Row row = sheet.getRow(j);
                    if (row == null) {
                        System.out.println(" row = null ---> " + j);
                        continue;
                    }
                    if (row.getFirstCellNum() < 0) {
                        System.out.println(" the row doesn't exist cell --> " + j);
                        continue;
                    }
                    int readColumnCount;
                    // 该行要读取的总列数设置
                    if (columnCount == null || columnCount > row.getLastCellNum()) {
                        readColumnCount = (int) row.getLastCellNum();
                    } else {
                        readColumnCount = columnCount;
                    }
                    List<Object> rowValue = new LinkedList<>();
                    // 解析sheet 的列
                    for (int k = 0; k < readColumnCount; k++) {
                        Cell cell = row.getCell(k);
                        rowValue.add(getCellValue(cell));
                    }
                    dataList.add(rowValue);
                    alreadyReadRowTotal++;
                }
                sheetList.add(excelSheetVO);
            }
        } finally {
            if(wb != null) {
                wb.close();
            }
        }
        return sheetList;
    }

    /**
     * 获取单元格内的值
     *
     * @param cell
     *            单元格
     * @return 该单元格内的内容
     * @DATE 2018年9月24日 下午8:06:53
     */
    private static Object getCellValue(Cell cell) {
        Object value = null;
        if (cell != null) {
            // 获取单元格类型
            CellType ct = cell.getCellTypeEnum();
            // 枚举的比较 使用 == 或 equals 都是一样的(枚举重写了equals方法,该方法内部其实也是用的==进行比较的)
            if (ct == CellType.STRING) {
                value = cell.getStringCellValue();
            } else if (ct == CellType.NUMERIC) {
                // 日期格式会被认为是数字(因为日期在导入时，会转化为1900年到该日期的天数)
                // 这里我们先要判断是否为日期
                if(HSSFDateUtil.isCellDateFormatted(cell)) {
                    value = simpleDateFormat.format(cell.getDateCellValue());
                    return value;
                }
                /*
                 * 如果是数字类型的;那么我们将其类型设置为String,再取值
                 *
                 *  但是注意:这样取值的话,如果是小数,如1.228,那么 获得的却是 1.2228000000000001
                 *         我们在实际使用这个1.2228000000000001这个String值时,可以
                 *         Double.valueOf((String)value)一下,这样就能获取到准确地1.228这个值了
                 *     注意:如果该cell的值本来就是1.2228000000000001,那么进过上述步骤获得的仍然是1.228
                 *         所以此步骤仍然是有漏洞的;
                 */
                cell.setCellType(CellType.STRING);
                value = cell.getStringCellValue();

                /* 当然也可以直接取数字类型的值;但是这样取值的话,如果cell的值是小数,还行;
                 *    如果是整数的话,那么取出来的值会多一个.0    如:24取出来就变成了24.0
                 *    我们也可以使用java.text.DecimalFormat对十进制数据进行格式化
                 */
                // value = cell.getNumericCellValue();
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