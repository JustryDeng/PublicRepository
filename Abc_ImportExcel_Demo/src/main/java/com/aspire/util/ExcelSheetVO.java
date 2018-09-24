package com.aspire.util;

import java.util.List;


public class ExcelSheetVO {

    /** 工作表表名(即:sheet的名称) */
    private String sheetName;

    /**
     * 该工作表数据集合
     * 说明:excel的每一个Cell对应为一个Object;
     *     那么一行即对应:List<Object>;
     *     那么一个sheet的内容即对应List<List<Object>>
     */
    private List<List<Object>> dataList;

    public List<List<Object>> getDataList() {
        return dataList;
    }

    public void setDataList(List<List<Object>> dataList) {
        this.dataList = dataList;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

	/*
	 * 说明:对集合.toString();相当于 对集合中的每一个元素进行toString(),然后以逗号凭借起来,首位再以[ ]括起来;
	 *   如: [我是一只小小小小鸟~, 2, 6]
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ExcelSheetVO {sheetName = '").append(sheetName).append("'");
		sb.append(", dataList = ").append(dataList).append("}");
		return sb.toString();
	}
    
}
