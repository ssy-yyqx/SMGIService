package cn.htht.service.platform.portal.utils.doc;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName WordUtil
 * @Description word生成实体类
 * @Author Ken
 * @Date DATE{TIME}
 **/
public class WordUtil {

    private static XWPFDocument document = null;

    private static XWPFParagraph paragraph = null;

    /**
     * 初始化创建Word文件
     */
    public WordUtil() {
        document = new XWPFDocument();
    }

    /**
     * 给段落插入图片
     *
     * @param pictureMap
     * @param showName   是否显示图片路径
     */
    public static void addPicture(Map<XWPFParagraph, WordImage> pictureMap, boolean showName) {
        for (Map.Entry<XWPFParagraph, WordImage> imageObject : pictureMap.entrySet()) {
            XWPFParagraph p = imageObject.getKey();
            XWPFRun pRun = p.createRun();
            String imagePath = imageObject.getValue().getImagePath();
            int pictureType = getPictureType(imagePath.substring(imagePath.lastIndexOf(".") + 1));
            if (showName) {
                pRun.setText(imageObject.getValue().getName());
                pRun.addBreak();
            }
            try {
                pRun.addPicture(new FileInputStream(imagePath), pictureType, imagePath, Units.toEMU(imageObject.getValue().getWidth()), Units.toEMU(imageObject.getValue().getHeight())); // 200x200 pixels
            } catch (InvalidFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            pRun.addBreak(BreakType.PAGE);
        }
    }

    /**
     * 根据图片类型，取得对应的图片类型代码
     *
     * @param picType
     * @return int
     */
    private static int getPictureType(String picType) {
        int res = XWPFDocument.PICTURE_TYPE_PICT;
        if (picType != null) {
            if (picType.equalsIgnoreCase("png")) {
                res = XWPFDocument.PICTURE_TYPE_PNG;
            } else if (picType.equalsIgnoreCase("dib")) {
                res = XWPFDocument.PICTURE_TYPE_DIB;
            } else if (picType.equalsIgnoreCase("emf")) {
                res = XWPFDocument.PICTURE_TYPE_EMF;
            } else if (picType.equalsIgnoreCase("jpg") || picType.equalsIgnoreCase("jpeg")) {
                res = XWPFDocument.PICTURE_TYPE_JPEG;
            } else if (picType.equalsIgnoreCase("wmf")) {
                res = XWPFDocument.PICTURE_TYPE_WMF;
            } else if (picType.equalsIgnoreCase("gif")) {
                res = XWPFDocument.PICTURE_TYPE_GIF;
            } else if (picType.equalsIgnoreCase("tiff")) {
                res = XWPFDocument.PICTURE_TYPE_TIFF;
            } else if (picType.equalsIgnoreCase("eps")) {
                res = XWPFDocument.PICTURE_TYPE_EPS;
            } else if (picType.equalsIgnoreCase(".bmp")) {
                res = XWPFDocument.PICTURE_TYPE_BMP;
            } else if (picType.equalsIgnoreCase("wpg")) {
                res = XWPFDocument.PICTURE_TYPE_WPG;
            } else {
                System.err.println("Unsupported picture: " + picType +
                        ". Expected emf|wmf|pict|jpeg|png|dib|gif|tiff|eps|bmp|wpg");
            }
        }
        return res;
    }

    /**
     * 添加表格
     *
     * @param document 文档实体
     * @param width    表格宽度
     * @param headers  表头属性
     * @param rowList  行数据
     * @param border   是否需要边界
     */
    public static void addTable(XWPFDocument document, int width, WordTableRow headers, List<WordTableRow> rowList, boolean border) {
        //基本信息表格
        XWPFTable infoTable = document.createTable();
        //去表格边框
        if (!border) {
            infoTable.getCTTbl().getTblPr().unsetTblBorders();
        }

        //列宽自动分割
        CTTblWidth infoTableWidth = infoTable.getCTTbl().addNewTblPr().addNewTblW();
        infoTableWidth.setType(STTblWidth.DXA);
        infoTableWidth.setW(BigInteger.valueOf(width));

        //添加表头
        if (headers != null) {
            XWPFTableRow headerRow = infoTable.createRow();
            headerRow.setHeight(headerRow.getHeight());
            headerRow.setHeightRule(headerRow.getHeightRule());
            List<WordTableEntity> cellList = headers.getCellList();
            for (int i = 0; i < cellList.size(); i++) {
                WordTableEntity entity = cellList.get(i);
                if (i > 0) {
                    headerRow.createCell();
                }
                headerRow.getCell(i).setColor("#F5F5F5");
                XWPFParagraph paragraph = headerRow.getCell(i).addParagraph();
                createRun(paragraph,
                        entity.getContent(),
                        true,
                        false,
                        "000000",
                        14,
                        "黑体");
            }
        }


        for (WordTableRow row : rowList) {
            XWPFTableRow newRow = infoTable.createRow();
            newRow.setHeight(row.getHeight());
            newRow.setHeightRule(row.getHeightRule());
            List<WordTableEntity> cellList = row.getCellList();
            for (int i = 0; i < cellList.size(); i++) {
                WordTableEntity entity = cellList.get(i);
                if (i > 0) {
                    newRow.createCell();
                }
                if (entity.getWordTableCell() != null) {
                    newRow.getCell(i).setVerticalAlignment(entity.getWordTableCell().getvAlign());
                    newRow.getCell(i).setWidthType(entity.getWordTableCell().getWidthType());
                    newRow.getCell(i).setColor(entity.getWordTableCell().getColor());
                }
                if (entity.isImage()) {
                    XWPFParagraph paragraph = newRow.getCell(i).addParagraph();
                    Map<XWPFParagraph, WordImage> map = new HashMap<>();
                    WordImage wordImage = new WordImage();
                    wordImage.setImagePath(entity.getContent());
                    wordImage.setWidth(entity.getWordTableCell().getWidth());
                    wordImage.setHeight(entity.getWordTableCell().getHeight());
                    map.put(paragraph, wordImage);
                    WordUtil.addPicture(map, false);
                } else if (entity.isHasStyle()) {
                    XWPFParagraph paragraph = newRow.getCell(i).addParagraph();
                    createRun(paragraph,
                            entity.getContent(),
                            entity.getWordTableCell().getBold(),
                            entity.getWordTableCell().getItalic(),
                            entity.getWordTableCell().getFontColor(),
                            entity.getWordTableCell().getFontSize(),
                            entity.getWordTableCell().getFontFamily());
                } else {
                    newRow.getCell(i).setText(entity.getContent());
                }

            }
        }
        infoTable.removeRow(0);
    }

    /**
     * 合并表格中的单元格
     *
     * @param sourceFile 源文件地址
     * @param targetFile 目标文件地址
     * @param startRow   合并列范围 起始行
     * @param endRow     合并列范围 结束行
     * @param startCol   合并行范围 起始列
     * @param endCol     合并行范围 结束列
     */
    public static void mergeColumeAndRow(String sourceFile, String targetFile, int startRow, int endRow, int startCol, int endCol) {
        try {
            XWPFDocument doc = new XWPFDocument(new FileInputStream(sourceFile));

            List<XWPFTable> XWPFtables = doc.getTables();
            for (XWPFTable table : XWPFtables) {

                for (int pos = 0; pos < table.getRow(0).getTableCells().size(); pos++) {
                    if (pos < startCol || pos > endCol) continue;

                    //合并列：如果表格的第一列的上下两个单元格相同，进行合并。
                    for (int row = 0; row < table.getRows().size(); row++) {//每一行
                        if (row == table.getRows().size() - 1) continue; //最后一行跨过去

                        XWPFTableCell cell0 = table.getRow(row).getCell(pos);
                        XWPFTableCell cell1 = table.getRow(row + 1).getCell(pos);
                        if (cell0.getText().equals(cell1.getText())) {
                            //                      System.out.println("这两个列一样");
                            // First Row, 设置合并的开始点
                            CTVMerge vmerge = CTVMerge.Factory.newInstance();
                            vmerge.setVal(STMerge.RESTART);
                            cell0.getCTTc().getTcPr().setVMerge(vmerge);
                            cell1.getCTTc().getTcPr().setVMerge(vmerge);

                            // Second Row cell will be merged ,设置合并的结束点
                            CTVMerge vmerge1 = CTVMerge.Factory.newInstance();
                            vmerge1.setVal(STMerge.CONTINUE);
                            cell0.getCTTc().getTcPr().setVMerge(vmerge1);
                            cell1.getCTTc().getTcPr().setVMerge(vmerge1);
                        }
                    }
                }
                //合并行：如果一行中最后面的单元格全是空行，则合并它们
                for (int pos = 0; pos < table.getRows().size(); pos++) {
                    if (pos < startRow || pos > endRow) continue;

                    XWPFTableRow currentRow = table.getRow(pos);
                    int cellHasText = 0;//记录从第几个单元格以后开始为空的
                    for (int i = currentRow.getTableCells().size() - 1; i >= 0; i--) {
                        if (!currentRow.getCell(i).getText().equals("")) {
                            cellHasText = i;
                            break;
                        }
                    }
                    if (cellHasText > 0) {//开始合并
                        for (int i = cellHasText; i < currentRow.getTableCells().size(); i++) {
//                          System.out.println("开始合并行了");
                            if (i == cellHasText) {
                                currentRow.getCell(i).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
                            } else {
                                currentRow.getCell(i).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
                            }
                        }
                    }
                }
            }

            FileOutputStream fos = new FileOutputStream(targetFile);
            doc.write(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 创建一个段落
     *
     * @param position 段落位置
     *                 0：居左
     *                 1：居中
     *                 2：居右
     */
    public XWPFParagraph createParagraph(Integer position) {
        paragraph = document.createParagraph();
        switch (position) {
            case 1:
                paragraph.setAlignment(ParagraphAlignment.CENTER);
                break;
            case 2:
                paragraph.setAlignment(ParagraphAlignment.RIGHT);
                break;
            default:
                paragraph.setAlignment(ParagraphAlignment.LEFT);
                break;
        }
        return paragraph;
    }

    /**
     * 单线边框
     */
    public void createBorder() {

        paragraph.setBorderTop(Borders.THICK);//设置上边框
        paragraph.setBorderBottom(Borders.THICK);//设置下边框
        paragraph.setBorderLeft(Borders.THICK);//设置左边框
        paragraph.setBorderRight(Borders.THICK);//设置右边框
    }

    /**
     * 双线边框
     */
    public void createBorderDouble() {
        paragraph.setBorderTop(Borders.DOUBLE);//设置上边框
        paragraph.setBorderBottom(Borders.DOUBLE);//设置下边框
        paragraph.setBorderLeft(Borders.DOUBLE);//设置左边框
        paragraph.setBorderRight(Borders.DOUBLE);//设置右边框
    }

    /**
     * 首行缩进
     *
     * @param indentation
     */
    public void addTextIndent(Integer indentation) {
        paragraph.setIndentationFirstLine(indentation);
    }

    /**
     * 对当前段落创建文本信息
     *
     * @param text       文本信息
     * @param bold       是否加粗 true为加粗
     * @param italic     是否倾斜 true为倾斜
     * @param color      颜色码
     * @param fontSize   字体大小
     * @param fontFamily 设置字体
     */
    public XWPFRun createRun(String text, Boolean bold, Boolean italic, String color, Integer fontSize, String fontFamily) {
        return createRun(paragraph, text, bold, italic, color, fontSize, fontFamily);
    }

    public static XWPFRun writeInfo(XWPFRun r, String text, Boolean bold, Boolean italic, String color, Integer fontSize, String fontFamily) {
        r.setText(text);
        r.setBold(bold);//设置为粗体 true 为粗体
        r.setItalic(italic);//设置为倾斜 true 为粗体
        r.setColor(color);//设置颜色
        r.setFontSize(fontSize);
        CTRPr rpr = r.getCTR().isSetRPr() ? r.getCTR().getRPr() : r.getCTR().addNewRPr();
        CTFonts fonts = rpr.getRFontsArray().length > 0 ? rpr.getRFontsArray(0) : rpr.addNewRFonts();
        fonts.setAscii(fontFamily);
        fonts.setEastAsia(fontFamily);
        fonts.setHAnsi(fontFamily);
        return r;
    }

    /**
     * 指定段落创建文本信息
     *
     * @param
     * @param text       文本信息
     * @param bold       是否加粗 true为加粗
     * @param italic     是否倾斜 true为倾斜
     * @param color      颜色码
     * @param fontSize   字体大小
     * @param fontFamily 设置字体
     */
    public static XWPFRun createRun(XWPFParagraph paragraph, String text, Boolean bold, Boolean italic, String color, Integer fontSize, String fontFamily) {
        XWPFRun r = paragraph.createRun();//创建段落文本
        writeInfo(r, text, bold, italic, color, fontSize, fontFamily);
        return r;
    }

    /**
     * 写到磁盘
     *
     * @param path
     */
    public void write(String path) {
        try {
            FileOutputStream out = new FileOutputStream(path);
            document.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("文件出现错误");
        }

    }

    /**
     * 创建空行
     *
     * @param counts 空行个数
     */
    public void createEmpty(Integer counts) {
        for (int i = 0; i < counts; i++) {
            XWPFParagraph empty = document.createParagraph();
            XWPFRun e = empty.createRun();
            e.setText(" ");
        }
    }

    /**
     * 查询wold中的数据 返回List 集合
     *
     * @param path wold所在的地址
     * @return
     */
    public List<String> query(String path) {
        List<String> list = new ArrayList<String>();
        try {
            FileInputStream stream = new FileInputStream(path);
            XWPFDocument doc = new XWPFDocument(stream);// 创建Word文件
            for (XWPFParagraph p : doc.getParagraphs()) {//遍历段落
                System.out.println(p.getParagraphText());
                list.add(p.getParagraphText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public static void main(String args[]) throws Exception {
        WordUtil woldUtil = new WordUtil();
        woldUtil.createParagraph(1);
        woldUtil.createBorder();
        woldUtil.createRun("（标题居中）Apache POI的组件", true, false, "000000", 17, "微软雅黑");
        woldUtil.createBorderDouble();

        woldUtil.createParagraph(0);
        woldUtil.createRun("（边框）Apache POI包含用于处理MS-Office的所有OLE2复合文档的类和方法。该API的组件列表如下:", false, true, "4682B4", 15, "微软雅黑");

        woldUtil.createEmpty(3);

        woldUtil.createParagraph(0);
        woldUtil.addTextIndent(600);
        woldUtil.createRun("(首行缩进)• POIFS（不良混淆实现文件系统） - 此组件是所有其他POI元素的基本因素。它用于显式读取不同的文件", true, false, "A0522D", 13, "微软雅黑");

        woldUtil.createEmpty(1);

        woldUtil.createParagraph(0);
        woldUtil.createRun("•HWPF（可怕的字处理器格式） - 用于读写MS-Word的.doc扩展文件。", true, false, "eeff00", 13, "微软雅黑");

        XWPFParagraph paragraph = woldUtil.createParagraph(0);
        WordImage wordImage = new WordImage();
        wordImage.setImagePath("D:\\pictures\\1316615123296583680\\TAS_TPPS_NDVIP_TM_20201015134121657.png");
        wordImage.setWidth(350);
        wordImage.setHeight(250);
        Map<XWPFParagraph, WordImage> map = new HashMap<>();
        map.put(paragraph, wordImage);
        woldUtil.addPicture(map, false);

        List<WordTableRow> rowList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            WordTableRow row = new WordTableRow();
            row.setHeight(30);
            row.setHeightRule(TableRowHeightRule.AUTO);
            List<WordTableEntity> cellList = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                WordTableCell cell = new WordTableCell();
                cell.setWidthType(TableWidthType.DXA);
                cell.setWidth(50);
                cell.setvAlign(XWPFTableCell.XWPFVertAlign.TOP);
                cell.setColor("A0522D");
                cell.setHeight(row.getHeight());
                WordTableEntity entity = null;
                if (i == 3 && j == 4) {
                    entity = new WordTableEntity(cell, "D:\\pictures\\1316615123296583680\\TAS_TPPS_NDVIP_TM_20201015134121657.png", true, false);
                } else if (i == 1) {
                    entity = new WordTableEntity("default_" + i + j);
                } else {
                    entity = new WordTableEntity(cell, "type1_" + i + j, false, false);
                }
                cellList.add(entity);
            }
            row.setCellList(cellList);
            rowList.add(row);
        }
        woldUtil.addTable(document, 250, null, rowList, true);

        woldUtil.write("E:\\sample.doc");

        List<String> list = woldUtil.query("E:\\sample.doc");
        System.out.println("list=====>>>>>>>>  " + list.toString());
    }
}
