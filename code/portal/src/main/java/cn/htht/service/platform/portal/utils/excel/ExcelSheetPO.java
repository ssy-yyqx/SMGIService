package cn.htht.service.platform.portal.utils.excel;

import java.util.List;

/**
 * @ClassName ExcelSheetPO
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
public class ExcelSheetPO<T> {
    /**
     * sheet的名称
     */
    private String sheetName;


    /**
     * 表格标题
     */
    private String title;

    /**
     * 头部标题集合
     */
    private String[] headers;

    /**
     * 数据集合
     */
    private List<List<T>> dataList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    public List<List<T>> getDataList() {
        return dataList;
    }

    public void setDataList(List<List<T>> dataList) {
        this.dataList = dataList;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
}
