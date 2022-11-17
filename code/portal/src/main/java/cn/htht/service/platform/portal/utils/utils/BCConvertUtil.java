package cn.htht.service.platform.portal.utils.utils;


import cn.htht.service.platform.portal.constant.BCConstant;

/**
 * @ClassName BCConvertUtil
 * @Description 半角和全角转换工具类
 * @Author Ken
 * @Date DATE{TIME}
 **/
public class BCConvertUtil {

    /**
     * <PRE>
     * 半角字符->全角字符转换
     * 只处理空格，!到˜之间的字符，忽略其他
     * </PRE>
     */
    public static String bj2qj(String src) {
        if (StringUtils.isEmpty(src)) {
            return "";
        }
        StringBuilder buf = new StringBuilder(src.length());
        char[] ca = src.toCharArray();
        for (char t : ca) {
            if (t == BCConstant.DBC_SPACE) {
                // 如果是半角空格，直接用全角空格替代
                buf.append(BCConstant.SBC_SPACE);
            } else if ((t >= BCConstant.DBC_CHAR_START) && (t <= BCConstant.DBC_CHAR_END)) {
                // 字符是!到~之间的可见字符
                buf.append((char) (t + BCConstant.CONVERT_STEP));
            } else {
                // 不对空格以及ascii表中其他可见字符之外的字符做任何处理
                buf.append(t);
            }
        }
        return buf.toString();
    }

    /**
     * <PRE>
     * 全角字符->半角字符转换
     * 只处理全角的空格，全角！到全角～之间的字符，忽略其他
     * </PRE>
     */
    public static String qj2bj(String src) {
        if (StringUtils.isEmpty(src)) {
            return "";
        }
        StringBuilder buf = new StringBuilder(src.length());
        char[] ca = src.toCharArray();
        for (int i = 0; i < src.length(); i++) {
            if (ca[i] >= BCConstant.SBC_CHAR_START && ca[i] <= BCConstant.SBC_CHAR_END) {
                // 如果位于全角！到全角～区间内
                buf.append((char) (ca[i] - BCConstant.CONVERT_STEP));
            } else if (ca[i] == BCConstant.SBC_SPACE) {
                // 如果是全角空格
                buf.append(BCConstant.DBC_SPACE);
            } else {
                // 不处理全角空格，全角！到全角～区间外的字符
                buf.append(ca[i]);
            }
        }
        return buf.toString();
    }
}
