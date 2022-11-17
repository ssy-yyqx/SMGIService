package cn.htht.service.platform.portal.utils.utils.elasticsearch;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EsUtiil {

    /**
     * 判断字符串中是否包含中文
     *
     * @param str 待校验字符串
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile(EsConstant.REQEX_CH_ONLY);
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 全为英文返回true  否则false
     *
     * @param val
     * @return
     */
    public static boolean isEnglishAll(String val) {
        boolean flag = true;
        if (val.matches(EsConstant.REQEX_A_ZA_Z)) {
            return flag;
        }
        return false;
    }

    /**
     * 全为数字返回true 否则false
     *
     * @param val
     * @return
     */
    public static boolean isNumberAll(String val) {
        boolean flag = true;
        if (val.matches(EsConstant.REQEX_NUMBER)) {
            return flag;
        }
        return false;
    }

    /**
     * [除英文和数字外无其他字符](只有英文数字的字符串)返回true 否则false
     *
     * @param val
     * @return
     */
    public static boolean isEngAndChar(String val) {
        boolean flag = true;
        if (val.matches(EsConstant.REQEX_A_ZA_Z0)) {
            return flag;
        }
        return false;
    }

    /**
     * 判断是否为纯中文，不是返回false
     *
     * @param val
     * @return
     */
    public static boolean isChinese(String val) {
        if (val.matches(EsConstant.REQEX_CH_ONLY)) {
            return true;
        }
        return false;
    }

    /**
     * 提取中文，排除其他字符
     *
     * @param val
     * @return
     */
    public static String replace(String val) {
        String val1 = val.replaceAll(EsConstant.REQEX_S, "").replaceAll(EsConstant.REQEX_CH_ONLY, "");
        return val1;
    }
}
