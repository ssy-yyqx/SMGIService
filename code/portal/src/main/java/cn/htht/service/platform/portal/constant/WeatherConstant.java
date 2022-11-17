package cn.htht.service.platform.portal.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName WeatherConstant
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
public class WeatherConstant {

    public final static Map<String, String> weatherMap = new HashMap<>();

    static {
        weatherMap.put("晴", "qintian");
        weatherMap.put("小雨", "xiaoyu");
        weatherMap.put("中雨", "zhongyu");
        weatherMap.put("大雨", "dayu");
        weatherMap.put("暴雨", "baoyu");
        weatherMap.put("小雪", "xiaoxue");
        weatherMap.put("中雪", "zhongxue");
        weatherMap.put("大雪", "daxue");
        weatherMap.put("暴雪", "baoxue");
        weatherMap.put("阴", "yintian");
        weatherMap.put("雷阵雨", "leizhenyu");
        weatherMap.put("浮尘", "fuchen");
        weatherMap.put("沙尘", "shachen");
        weatherMap.put("轻度雾霾", "wumai1");
        weatherMap.put("中度雾霾", "wumai2");
        weatherMap.put("重度雾霾", "wumai3");
        weatherMap.put("雾", "wu");
        weatherMap.put("雨夹雪", "yujiaxue");
        weatherMap.put("多云", "duoyun");
        weatherMap.put("强对流", "qiangduiliu");
        weatherMap.put("大风", "dafeng");
        weatherMap.put("冰雹", "bingbao");
    }
}
