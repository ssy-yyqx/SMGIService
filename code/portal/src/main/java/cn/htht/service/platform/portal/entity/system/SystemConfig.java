package cn.htht.service.platform.portal.entity.system;

import cn.htht.service.platform.portal.common.BaseEntity;
import lombok.Data;
import lombok.ToString;

/**
 * @ClassName SystemConfig
 * @Description 系统配置表
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
public class SystemConfig extends BaseEntity {

    /**
     * 参数名称
     */
    private String configName;

    /**
     * 参数数据类型 1 整型 Integer, 2 浮点型 Double, 3 字符串 String 4 枚举
     */
    private Integer configType;

    /**
     * 是否内置 "Y|N"
     */
    private String isInternal;

    /**
     * 参数的键
     */
    private String configKey;

    /**
     * 参数值
     */
    private String configValue;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 取值范围正则校验
     */
    private String valueRange;
}
