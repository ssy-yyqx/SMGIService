package cn.htht.service.platform.portal.utils.doc;

import org.apache.poi.xwpf.usermodel.TableRowHeightRule;

import java.util.List;

/**
 * @ClassName WordTableRow
 * @Description 表格中的行实体
 * @Author Ken
 * @Date DATE{TIME}
 **/
class WordTableRow {

    /**
     * 高度类型 TableRowHeightRule -> AUTO(1), EXACT(2) AT_LEAST(3);
     */
    private TableRowHeightRule heightRule;

    /**
     * 设置固定高度
     */
    private int height;

    /**
     * 行列中的单元格实体
     */
    List<WordTableEntity> cellList;

    public TableRowHeightRule getHeightRule() {
        return heightRule;
    }

    public void setHeightRule(TableRowHeightRule heightRule) {
        this.heightRule = heightRule;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<WordTableEntity> getCellList() {
        return cellList;
    }

    public void setCellList(List<WordTableEntity> cellList) {
        this.cellList = cellList;
    }
}

