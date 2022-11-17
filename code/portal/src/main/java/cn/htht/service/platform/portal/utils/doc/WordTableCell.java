package cn.htht.service.platform.portal.utils.doc;

import org.apache.poi.xwpf.usermodel.TableWidthType;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;

/**
 * @ClassName WordRow
 * @Description 单元格样式实体类
 * @Author Ken
 * @Date DATE{TIME}
 **/
public class WordTableCell {

    /**
     * 单元格宽度
     */
    private int width;

    /**
     * 单元格高度
     */
    private int height;

    /**
     * 单元格颜色
     */
    private String color;

    /**
     * 放置相对位置 XWPFTableCell.XWPFVertAlign -> TOP,CENTER,BOTH,BOTTOM
     */
    private XWPFTableCell.XWPFVertAlign vAlign;

    /**
     * 列宽类型 TableWidthType -> AUTO(STTblWidth.AUTO), DXA(STTblWidth.DXA), NIL(STTblWidth.NIL), PCT(STTblWidth.PCT);
     */
    private TableWidthType widthType;

    /**
     * 字体是否加粗
     */
    private Boolean bold = false;

    /**
     * 字体是否倾斜
     */
    private Boolean italic = false;

    /**
     * 字体颜色
     */
    private String fontColor = "000000";

    /**
     * 字体大小
     */
    private Integer fontSize = 10;

    /**
     * 字体样式
     */
    private String fontFamily = "宋体";

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public XWPFTableCell.XWPFVertAlign getvAlign() {
        return vAlign;
    }

    public void setvAlign(XWPFTableCell.XWPFVertAlign vAlign) {
        this.vAlign = vAlign;
    }

    public TableWidthType getWidthType() {
        return widthType;
    }

    public void setWidthType(TableWidthType widthType) {
        this.widthType = widthType;
    }

    public Boolean getBold() {
        return bold;
    }

    public void setBold(Boolean bold) {
        this.bold = bold;
    }

    public Boolean getItalic() {
        return italic;
    }

    public void setItalic(Boolean italic) {
        this.italic = italic;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }
}
