package cn.htht.service.platform.portal.utils.doc;

import org.apache.poi.xwpf.usermodel.TableWidthType;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;


/**
 * @ClassName WordTableEntity
 * @Description 单元格内容实体
 * @Author Ken
 * @Date DATE{TIME}
 **/
public class WordTableEntity {

    private WordTableCell wordTableCell;
    private String content;
    private boolean isImage;
    private boolean hasStyle;


    public WordTableCell getWordTableCell() {
        return wordTableCell;
    }

    public void setWordTableCell(WordTableCell wordTableCell) {
        this.wordTableCell = wordTableCell;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean image) {
        isImage = image;
    }

    public boolean isHasStyle() {
        return hasStyle;
    }

    public void setHasStyle(boolean hasStyle) {
        this.hasStyle = hasStyle;
    }

    /**
     * 默认样式表格内容
     *
     * @param content 表格文本内容
     */
    public WordTableEntity(String content) {
        WordTableCell defaultWordTableCell = new WordTableCell();
        defaultWordTableCell.setColor("000000");
        defaultWordTableCell.setvAlign(XWPFTableCell.XWPFVertAlign.CENTER);
        defaultWordTableCell.setWidthType(TableWidthType.AUTO);
        this.wordTableCell = defaultWordTableCell;
        this.content = content;
        this.isImage = false;
        this.hasStyle = false;
    }

    /**
     * 自定义文本样式表格内容
     *
     * @param wordTableCell
     * @param content
     */
    public WordTableEntity(WordTableCell wordTableCell, String content, boolean isImage, boolean hasStyle) {
        this.wordTableCell = wordTableCell;
        this.content = content;
        this.isImage = isImage;
        this.hasStyle = hasStyle;
    }

}

