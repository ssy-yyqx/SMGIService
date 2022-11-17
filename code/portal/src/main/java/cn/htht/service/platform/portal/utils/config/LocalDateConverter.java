package cn.htht.service.platform.portal.utils.config;

import cn.htht.service.platform.portal.utils.utils.DateUtils;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.sql.Date;

public class LocalDateConverter implements Converter<Date> {


    @Override
    public Class supportJavaTypeKey() {
        return Date.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Date convertToJavaData(CellData cellData, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return (Date) DateUtils.parseDate(cellData.getStringValue(), DateUtils.YYYY_MM_DD);
    }

    @Override
    public CellData<String> convertToExcelData(Date value, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return new CellData<>(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, value));
    }
}