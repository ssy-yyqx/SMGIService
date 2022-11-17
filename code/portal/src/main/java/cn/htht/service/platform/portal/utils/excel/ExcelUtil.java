package cn.htht.service.platform.portal.utils.excel;

import cn.htht.service.platform.portal.utils.utils.file.FileUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.streaming.SXSSFDrawing;
import org.apache.poi.xssf.usermodel.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @ClassName ExcelUtil
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
public class ExcelUtil {
    /**
     * 标题样式
     */
    private final static String STYLE_HEADER = "header";
    /**
     * 表头样式
     */
    private final static String STYLE_TITLE = "title";
    /**
     * 数据样式
     */
    private final static String STYLE_DATA = "ipdata";

    /**
     * 存储样式
     */
    private static final HashMap<String, CellStyle> cellStyleMap = new HashMap<>();

    /**
     * 读取excel文件里面的内容 支持日期，数字，字符，函数公式，布尔类型
     *
     * @param file
     * @param rowCount
     * @param columnCount
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static List<ExcelSheetPO> readExcel(File file, Integer rowCount, Integer columnCount)
            throws FileNotFoundException, IOException {

        // 根据后缀名称判断excel的版本
        String extName = FileUtils.suffix(file);
        Workbook wb = null;
        if (ExcelVersion.V2003.getSuffix().equals(extName)) {
            wb = new HSSFWorkbook(new FileInputStream(file));
        } else if (ExcelVersion.V2007.getSuffix().equals(extName)) {
            wb = new XSSFWorkbook(new FileInputStream(file));
        } else {
            // 无效后缀名称，这里之能保证excel的后缀名称，不能保证文件类型正确，不过没关系，在创建Workbook的时候会校验文件格式
            throw new IllegalArgumentException("Invalid excel version");
        }
        // 开始读取数据
        List<ExcelSheetPO> sheetPOs = new ArrayList<>();
        // 解析sheet
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            Sheet sheet = wb.getSheetAt(i);
            List<List<Object>> dataList = new ArrayList<>();
            ExcelSheetPO sheetPO = new ExcelSheetPO();
            sheetPO.setSheetName(sheet.getSheetName());
            sheetPO.setDataList(dataList);
            int readRowCount = 0;
            if (rowCount == null || rowCount > sheet.getPhysicalNumberOfRows()) {
                readRowCount = sheet.getPhysicalNumberOfRows();
            } else {
                readRowCount = rowCount;
            }
            // 解析sheet 的行
            for (int j = sheet.getFirstRowNum(); j < readRowCount; j++) {
                Row row = sheet.getRow(j);
                if (row == null) {
                    continue;
                }
                if (row.getFirstCellNum() < 0) {
                    continue;
                }
                int readColumnCount = 0;
                if (columnCount == null || columnCount > row.getLastCellNum()) {
                    readColumnCount = (int) row.getLastCellNum();
                } else {
                    readColumnCount = columnCount;
                }
                List<Object> rowValue = new LinkedList<Object>();
                // 解析sheet 的列
                for (int k = 0; k < readColumnCount; k++) {
                    Cell cell = row.getCell(k);
                    rowValue.add(getCellValue(wb, cell));
                }
                dataList.add(rowValue);
            }
            sheetPOs.add(sheetPO);
        }
        return sheetPOs;
    }

    private static Object getCellValue(Workbook wb, Cell cell) {
        Object columnValue = null;
        if (cell != null) {
            DecimalFormat df = new DecimalFormat("0");// 格式化 number
            // String
            // 字符
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 格式化日期字符串
            DecimalFormat nf = new DecimalFormat("0.00");// 格式化数字
            switch (cell.getCellType()) {
                case STRING:
                    columnValue = cell.getStringCellValue();
                    break;
                case _NONE:
                    break;
                case NUMERIC:
                    if ("@".equals(cell.getCellStyle().getDataFormatString())) {
                        columnValue = df.format(cell.getNumericCellValue());
                    } else if ("General".equals(cell.getCellStyle().getDataFormatString())) {
                        columnValue = nf.format(cell.getNumericCellValue());
                    } else {
                        columnValue = sdf.format(DateUtil.getJavaDate(cell.getNumericCellValue()));
                    }
                    break;
                case BOOLEAN:
                    columnValue = cell.getBooleanCellValue();
                    break;
                case BLANK:
                    columnValue = "";
                    break;
                case FORMULA:
                    // 格式单元格
                    FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
                    evaluator.evaluateFormulaCell(cell);
                    CellValue cellValue = evaluator.evaluate(cell);
                    columnValue = cellValue.getNumberValue();
                    break;
                default:
                    columnValue = cell.toString();
            }
        }
        return columnValue;
    }

    /**
     * 在硬盘上写入excel文件
     *
     * @param version
     * @param excelSheets
     * @param filePath
     * @throws IOException
     */
    public static void createWorkbookAtDisk(ExcelVersion version, List<ExcelSheetPO> excelSheets, String filePath)
            throws IOException {
        FileOutputStream fileOut = new FileOutputStream(filePath);
        createWorkbookAtOutStream(version, excelSheets, fileOut, true);
    }

    /**
     * 把excel表格写入输出流中，输出流会被关闭
     *
     * @param version
     * @param excelSheets
     * @param outStream
     * @param closeStream 是否关闭输出流
     * @throws IOException
     */
    public static void createWorkbookAtOutStream(ExcelVersion version, List<ExcelSheetPO> excelSheets,
                                                 OutputStream outStream, boolean closeStream) throws IOException {
        if (CollectionUtils.isNotEmpty(excelSheets)) {
            Workbook wb = createWorkBook(version, excelSheets);
            wb.write(outStream);
            if (closeStream) {
                outStream.close();
            }
        }
    }

    public static Workbook createWorkBook(ExcelVersion version, List<ExcelSheetPO> excelSheets) {
        Workbook wb = createWorkbook(version);
        for (int i = 0; i < excelSheets.size(); i++) {
            ExcelSheetPO excelSheetPO = excelSheets.get(i);
            if (excelSheetPO.getSheetName() == null) {
                excelSheetPO.setSheetName("sheet" + i);
            }
            // 过滤特殊字符
            Sheet tempSheet = wb.createSheet(WorkbookUtil.createSafeSheetName(excelSheetPO.getSheetName()));
            buildSheetData(wb, tempSheet, excelSheetPO, version);
        }
        return wb;
    }

    private static void buildSheetData(Workbook wb, Sheet sheet, ExcelSheetPO excelSheetPO, ExcelVersion version) {
        sheet.setDefaultRowHeight((short) 400);
        sheet.setDefaultColumnWidth((short) 10);
        createTitle(sheet, excelSheetPO, wb, version);
        createHeader(sheet, excelSheetPO, wb, version);
        createBody(sheet, excelSheetPO, wb, version);
    }

    private static void createBody(Sheet sheet, ExcelSheetPO excelSheetPO, Workbook wb, ExcelVersion version) {
        List<List<Object>> dataList = excelSheetPO.getDataList();
        for (int i = 0; i < dataList.size() && i < version.getMaxRow(); i++) {
            List<Object> values = dataList.get(i);
            Row row = sheet.createRow(2 + i);
            for (int j = 0; j < values.size() && j < version.getMaxColumn(); j++) {
                Cell cell = row.createCell(j);
                cell.setCellStyle(getStyle(STYLE_DATA, wb));
                cell.setCellValue(values.get(j).toString());
            }
        }

    }

    private static void createHeader(Sheet sheet, ExcelSheetPO excelSheetPO, Workbook wb, ExcelVersion version) {
        String[] headers = excelSheetPO.getHeaders();
        Row row = sheet.createRow(1);
        for (int i = 0; i < headers.length && i < version.getMaxColumn(); i++) {
            Cell cellHeader = row.createCell(i);
            cellHeader.setCellStyle(getStyle(STYLE_HEADER, wb));
            cellHeader.setCellValue(headers[i]);
        }

    }

    private static void createTitle(Sheet sheet, ExcelSheetPO excelSheetPO, Workbook wb, ExcelVersion version) {
        Row titleRow = sheet.createRow(0);
        Cell titleCel = titleRow.createCell(0);
        titleCel.setCellValue(excelSheetPO.getTitle());
        titleCel.setCellStyle(getStyle(STYLE_TITLE, wb));
        // 限制最大列数
        int column = excelSheetPO.getDataList().size() > version.getMaxColumn() ? version.getMaxColumn()
                : excelSheetPO.getDataList().size();
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, column - 1));
    }

    private static CellStyle getStyle(String type, Workbook wb) {

        if (cellStyleMap.containsKey(type)) {
            return cellStyleMap.get(type);
        }
        // 生成一个样式
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setWrapText(true);

        if (STYLE_HEADER == type) {
            style.setAlignment(HorizontalAlignment.CENTER);
            Font font = wb.createFont();
            font.setFontHeightInPoints((short) 16);
            font.setBold(true);
            style.setFont(font);
        } else if (STYLE_TITLE == type) {
            style.setAlignment(HorizontalAlignment.CENTER);
            Font font = wb.createFont();
            font.setFontHeightInPoints((short) 18);
            font.setBold(true);
            style.setFont(font);
        } else if (STYLE_DATA == type) {
            style.setAlignment(HorizontalAlignment.LEFT);
            Font font = wb.createFont();
            font.setFontHeightInPoints((short) 12);
            style.setFont(font);
        }
        cellStyleMap.put(type, style);
        return style;
    }

    private static Workbook createWorkbook(ExcelVersion version) {
        switch (version) {
            case V2003:
                return new HSSFWorkbook();
            case V2007:
                return new XSSFWorkbook();
        }
        return null;
    }

    /**
     * * 合并多个ExcelSheet
     *
     * @param files     文件字符串(file.toString)集合,按顺序进行合并，合并的Excel中Sheet名称不可重复
     * @param excelName 合并后Excel名称(包含后缀.xslx)
     * @param dirPath   存储目录
     * @return
     */
    public static void mergexcel(List<String> files, String excelName, String dirPath) {

        XSSFWorkbook newExcelCreat = new XSSFWorkbook();
        // 遍历每个源excel文件，TmpList为源文件的名称集合
        for (int i = 0; i < files.size(); i++) {
            try (InputStream in = new FileInputStream(files.get(i))) {
                if (!ExcelVersion.V2007.getSuffix().equals(FileUtils.suffix(new File(files.get(i))))) {
                    throw new IllegalArgumentException("the version of excel is not merge！");
                }
                XSSFWorkbook fromExcel = new XSSFWorkbook(in);
                //第一个文件则创建一个sheet
                XSSFSheet newSheet = null;
                if (i == 0) {
                    newSheet = newExcelCreat.createSheet("sheet");
                } else {
                    newSheet = newExcelCreat.getSheet("sheet");
                }
                for (int j = 0; j < fromExcel.getNumberOfSheets(); j++) {
                    // 遍历每个sheet
                    XSSFSheet oldSheet = fromExcel.getSheetAt(j);
                    copySheet(newExcelCreat, oldSheet, newSheet);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 定义新生成的xlxs表格文件
        String allFileName = dirPath + File.separator + excelName;
        try (FileOutputStream fileOut = new FileOutputStream(allFileName)) {
            newExcelCreat.write(fileOut);
            fileOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                newExcelCreat.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 合并单元格
     *
     * @param fromSheet
     * @param toSheet
     */
    private static void mergeSheetAllRegion(XSSFSheet fromSheet, XSSFSheet toSheet) {
        int num = fromSheet.getNumMergedRegions();
        CellRangeAddress cellR = null;
        for (int i = 0; i < num; i++) {
            cellR = fromSheet.getMergedRegion(i);
            CellRangeAddress region = new CellRangeAddress(toSheet.getLastRowNum() + cellR.getFirstRow() + 1, toSheet.getLastRowNum() + cellR.getLastRow() + 1, cellR.getFirstColumn(), cellR.getLastColumn());
            toSheet.addMergedRegion(region);
        }
    }

    /**
     * 复制单元格
     *
     * @param wb
     * @param fromCell
     * @param toCell
     */
    private static void copyCell(XSSFWorkbook wb, XSSFCell fromCell, XSSFCell toCell) {
        XSSFCellStyle newstyle = wb.createCellStyle();
        // 复制单元格样式
        newstyle.cloneStyleFrom(fromCell.getCellStyle());
        // 样式
        toCell.setCellStyle(newstyle);
        if (fromCell.getCellComment() != null) {
            toCell.setCellComment(fromCell.getCellComment());
        }
        // 不同数据类型处理
        CellType fromCellType = fromCell.getCellType();
        toCell.setCellType(fromCellType);
        if (fromCellType == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(fromCell)) {
                toCell.setCellValue(fromCell.getDateCellValue());
            } else {
                toCell.setCellValue(fromCell.getNumericCellValue());
            }
        } else if (fromCellType == CellType.STRING) {
            toCell.setCellValue(fromCell.getRichStringCellValue());
        } else if (fromCellType == CellType.BLANK) {
            // nothing21
        } else if (fromCellType == CellType.BOOLEAN) {
            toCell.setCellValue(fromCell.getBooleanCellValue());
        } else if (fromCellType == CellType.ERROR) {
            toCell.setCellErrorValue(fromCell.getErrorCellValue());
        } else if (fromCellType == CellType.FORMULA) {
            toCell.setCellFormula(fromCell.getCellFormula());
        } else {
            // nothing29
        }
    }

    /**
     * 行复制功能
     *
     * @param wb
     * @param oldRow
     * @param toRow
     */
    private static void copyRow(XSSFWorkbook wb, XSSFRow oldRow, XSSFRow toRow) {
        toRow.setHeight(oldRow.getHeight());
        for (Iterator cellIt = oldRow.cellIterator(); cellIt.hasNext(); ) {
            XSSFCell tmpCell = (XSSFCell) cellIt.next();
            XSSFCell newCell = toRow.createCell(tmpCell.getColumnIndex());
            copyCell(wb, tmpCell, newCell);
        }
    }

    /**
     * Sheet复制
     *
     * @param wb
     * @param fromSheet
     * @param toSheet
     */
    private static void copySheet(XSSFWorkbook wb, XSSFSheet fromSheet, XSSFSheet toSheet) {
        mergeSheetAllRegion(fromSheet, toSheet);
        // 设置列宽
        int length = fromSheet.getRow(fromSheet.getFirstRowNum()).getLastCellNum();
        for (int i = 0; i <= length; i++) {
            toSheet.setColumnWidth(i, fromSheet.getColumnWidth(i));
        }
        for (Iterator rowIt = fromSheet.rowIterator(); rowIt.hasNext(); ) {
            XSSFRow oldRow = (XSSFRow) rowIt.next();
            XSSFRow newRow = toSheet.createRow(toSheet.getLastRowNum() + 1);
            copyRow(wb, oldRow, newRow);
        }
    }


    public static void addPicture(File image, Sheet sheet, Workbook wb, int width, int height, int col1, int row1, int col2, int row2) throws Exception {
        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        BufferedImage bufferImg = ImageIO.read(image);
        ImageIO.write(bufferImg, "jpg", byteArrayOut);
        SXSSFDrawing drawing = (SXSSFDrawing) sheet.createDrawingPatriarch();
        ClientAnchor anchor = drawing.createAnchor(
                0,
                0,
                width,
                height,
                col1, row1, col2, row2);
        //AnchorType.MOVE_AND_RESIZE,此时才可以设置图片的绝对宽度和高度,要不然,图片的宽度和高度,可能设置没有效果
        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        //插入图片
        drawing.createPicture(anchor, wb.addPicture(byteArrayOut.toByteArray(), getPictureType(wb, image)));
    }

    private static int getPictureType(Workbook wb, File image) {
        int format = 0;
        String imgFile = image.getName();
        if (imgFile.endsWith(".emf")) format = Workbook.PICTURE_TYPE_EMF;
        else if (imgFile.endsWith(".wmf")) format = Workbook.PICTURE_TYPE_WMF;
        else if (imgFile.endsWith(".pict")) format = Workbook.PICTURE_TYPE_PICT;
        else if (imgFile.endsWith(".jpeg") || imgFile.endsWith(".jpg"))
            format = Workbook.PICTURE_TYPE_JPEG;
        else if (imgFile.endsWith(".png")) format = Workbook.PICTURE_TYPE_PNG;
        else if (imgFile.endsWith(".dib")) format = Workbook.PICTURE_TYPE_DIB;
        else if (imgFile.endsWith(".gif")) format = XSSFWorkbook.PICTURE_TYPE_GIF;
        else if (imgFile.endsWith(".tiff")) format = XSSFWorkbook.PICTURE_TYPE_TIFF;
        else if (imgFile.endsWith(".eps")) format = XSSFWorkbook.PICTURE_TYPE_EPS;
        else if (imgFile.endsWith(".bmp")) format = XSSFWorkbook.PICTURE_TYPE_BMP;
        else if (imgFile.endsWith(".wpg")) format = XSSFWorkbook.PICTURE_TYPE_WPG;
        else {
            throw new IllegalArgumentException("Unsupported picture: " + imgFile
                    + ". Expected emf|wmf|pict|jpeg|png|dib|gif|tiff|eps|bmp|wpg");
        }
        return format;
    }
}
