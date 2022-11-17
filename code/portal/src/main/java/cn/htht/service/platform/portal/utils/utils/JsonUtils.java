package cn.htht.service.platform.portal.utils.utils;

import cn.htht.service.platform.portal.entity.manager.Template;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * @ClassName JsonUtils
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
public class JsonUtils {
    public static <T> T convertObject(JSONObject jsonObject, Class<T> t) {
        try {
            T obj = t.newInstance();
            //得到对象的所有私有属性
            Field fields[] = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object val = jsonObject.get(field.getName());
                field.set(obj, val);
            }
            return obj;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) {
        String abc = "{\"id\": \"1234\",\"moduleId\": \"23456\",\"searchKey\": \"asdsa\",\"abc\": 1}";
        Template template = convertObject(JSON.parseObject(abc), Template.class);
        System.out.printf(template.toString());
    }
}
