package cn.htht.service.platform.portal.utils.utils.ip;

import cn.htht.service.platform.portal.constant.Constants;
import cn.htht.service.platform.portal.utils.config.SMGIConfig;
import cn.htht.service.platform.portal.utils.utils.StringUtils;
import cn.htht.service.platform.portal.utils.utils.http.HttpUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取地址类
 *
 * @author htht
 */
public class AddressUtils {
    private static final Logger log = LoggerFactory.getLogger(AddressUtils.class);

    // IP地址查询
    public static final String IP_ADDRESS_URL = "http://whois.pconline.com.cn/ipJson.jsp";

    public static final String CITY_CODE_URL="http://toy1.weather.com.cn/search";

    public static final String IP_URL="http://pv.sohu.com/cityjson?ie=utf-8";

    public static final String WEATHER_URL = "http://t.weather.itboy.net/api/weather/city/";

    // 未知地址
    public static final String UNKNOWN = "XX XX";

    public static String getRealAddressByIP(String ip) {
        String address = UNKNOWN;
        // 内网不查询
        if (IpUtils.internalIp(ip)) {
            return "内网IP";
        }
        if (true) {
            try {
                String rspStr = HttpUtils.sendGet(IP_ADDRESS_URL, "ip=" + ip + "&json=true", Constants.GBK);
                if (StringUtils.isEmpty(rspStr)) {
                    log.error("获取地理位置异常 {}", ip);
                    return UNKNOWN;
                }
                JSONObject obj = JSONObject.parseObject(rspStr);
                String region = obj.getString("pro");
                String city = obj.getString("city");
                return String.format("%s %s", region, city);
            } catch (Exception e) {
                log.error("获取地理位置异常 {}", ip);
            }
        }
        return address;
    }

    //另一种方法，找到的有效：

    public static String getIP()  {
        String ip = IP_URL;
        String inputLine = "";
        String read = "";
        String toIp="";
        try {
            URL url = new URL(ip);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while ((read = in.readLine()) != null) {
                inputLine += read;
            }
            String ObjJson=inputLine.substring(inputLine.indexOf("=")+1,inputLine.length()-1);
//            System.out.println(ObjJson);
            JSONObject jsonObj= JSON.parseObject(ObjJson);
            toIp=jsonObj.getString("cip");
//            throw new Exception();
        } catch (Exception e) {
            toIp="";
        }
        return toIp;
    }


    /**
     * 根据域名获取ip地址
     */
    public static String getIfIP(String pathName) {
        String toIp="";
        try{
            InetAddress address =InetAddress.getAllByName(pathName)[0];
            toIp=address.getHostAddress();
        }catch (Exception e){
            toIp="";
        }
        return toIp;
    }

    public static void main(String[] args) {
        String ip = getIP();
        try {
            DbSearcher searcher = new DbSearcher(new DbConfig());
            DataBlock dataBlock = searcher.btreeSearch(ip);
            String city = dataBlock.getRegion().split("\\|")[3];
            String cityCode = getCityCode(city.substring(0, city.indexOf("市")));
            getWeather(cityCode);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DbMakerConfigException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(getRealAddressByIP(ip));
    }

    public static String getCityCode(String city) {
        try {
            city = URLEncoder.encode(city, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = HttpUtils.sendGet(CITY_CODE_URL, "cityname=" + city);
        return result.split("~")[0].split("\"")[3];
    }

    public static Map<String, String> getWeather(String cityCode) {
        Map<String, String> resultMap = new HashMap<>();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Accept-Encoding", "gzip, deflate");
        headerMap.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
        headerMap.put("Connection", "keep-alive");
        headerMap.put("Host", "d1.weather.com.cn");
        headerMap.put("Referer", "http://www.weather.com.cn/");
        headerMap.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/100.0.4896.127 Safari/537.36 Edg/100.0.1185.50");
        String weatherJSON = HttpUtils.sendGet(WEATHER_URL + cityCode, "");
        resultMap.put("currentTemperature", JSONObject.parseObject(weatherJSON).getJSONObject("data").get("wendu").toString());
        resultMap.put("temperatureList", JSONObject.parseObject(weatherJSON).getJSONObject("data").getString("forecast"));
        return resultMap;
    }
}
