package cn.htht.service.platform.portal.utils.config;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class TimestampConverter implements Converter<Timestamp> {

    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//定义日期显示格式


    @Override
    public Class supportJavaTypeKey() {
        return Timestamp.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Timestamp convertToJavaData(CellData cellData, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return Timestamp.valueOf(cellData.getStringValue());
    }

    @Override
    public CellData<String> convertToExcelData(Timestamp timestamp, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        CellData<String> stringCellData = new CellData<>(df.format(timestamp));
        return stringCellData;
    }
}