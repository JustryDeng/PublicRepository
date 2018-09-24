package com;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.aspire.util.ExcelImportUtil;
import com.aspire.util.ExcelSheetVO;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AbcImportExcelDemoApplicationTests {

	@Test
	public void contextLoads() {
		String path = "C:\\Users\\JustryDeng\\Desktop\\test.xlsx";
		File excelFile = new File(path);
		try {
			// 调用工具类
			List<ExcelSheetVO> list = ExcelImportUtil.readExcel(excelFile, null, null);
			// 遍历每一个sheet
			for (ExcelSheetVO excelSheetPO : list) {
				System.out.println(" ---> " + excelSheetPO.getSheetName());
				// 遍历sheet中的每一row行
				for (List<Object> row : excelSheetPO.getDataList()) {
					System.out.print(row.toString());
					// 遍历每一行的每一cell单元格
					for (Object cell : row) {
						System.out.print("\t");
						String digitalString = (String)cell;
						if(digitalString.indexOf(".") >= 0) {
							System.out.print(Double.valueOf(digitalString));
						}else {
							System.out.print(digitalString);
						}
					}
					System.out.println();
				}
				System.out.println();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
