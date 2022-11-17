package cn.htht.service.platform.portal.entity.dto;

import lombok.Data;

/**
 * @ClassName WeatherDto
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
public class WeatherDto {

    /**
     * 城市
     */
    private String city;

    /**
     * 天气状况
     */
    private String name;

    /**
     * 当前温度
     */
    private String currentTemperature;

    /**
     * 日期
     */
    private String date;

    /**
     * 天气图标
     */
    private String weather;

    /**
     * 温度区间
     */
    private String temperature;

    /**
     * 风向
     */
    private String windAspect;

    /**
     * 风级
     */
    private String windLevel;

}
