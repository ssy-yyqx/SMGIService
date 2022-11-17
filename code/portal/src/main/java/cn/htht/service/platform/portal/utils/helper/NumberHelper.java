package cn.htht.service.platform.portal.utils.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 提供一些数字相关的常规操作
 */
public class NumberHelper {
    /**
     * 默认除法运算精度
     */
    private static final int DEF_DIV_SCALE = 10;

    /**
     * 提供精确的加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static double doubleAdd(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    /**
     * 提供精确的减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double doubleSub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double doubleMul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * 小数点以后10位，以后的数字四舍五入。
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double doubleDiv(double v1, double v2) {
        return doubleDiv(v1, v2, DEF_DIV_SCALE);
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double doubleDiv(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        if (b1.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.doubleValue();
        }
        return b1.divide(b2, scale, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 提供精确的小数位四舍五入处理。
     *
     * @param v     需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double doubleRound(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 精确加法运算
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和(BigDecimal)
     */
    public static BigDecimal add(BigDecimal v1, BigDecimal v2) {
        if (null == v1) {
            v1 = BigDecimal.ZERO;
        }
        if (null == v2) {
            v2 = BigDecimal.ZERO;
        }
        return v1.add(v2);
    }

    /**
     * 精确减法运算
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差(BigDecimal)
     */
    public static BigDecimal subtract(BigDecimal v1, BigDecimal v2) {
        if (null == v1) {
            v1 = BigDecimal.ZERO;
        }
        if (null == v2) {
            v2 = BigDecimal.ZERO;
        }
        return v1.subtract(v2);
    }

    /**
     * 精确乘法运算
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积(BigDecimal)
     */
    public static BigDecimal multiply(BigDecimal v1, BigDecimal v2) {
        if (null == v1) {
            v1 = BigDecimal.ONE;
        }
        if (null == v2) {
            v2 = BigDecimal.ONE;
        }
        return v1.multiply(v2);
    }

    /**
     * ( 相对 )精确除法运算 , 当发生除不尽情况时 , 精确到 小数点以后2位 , 以后数字四舍五入
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商(BigDecimal)
     */
    public static BigDecimal divide(BigDecimal v1, BigDecimal v2) {
        return v1.divide(v2, DEF_DIV_SCALE, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * ( 相对 )精确除法运算 . 当发生除不尽情况时 , 由scale参数指 定精度 , 以后数字四舍五入
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位
     * @return 两个参数的商(BigDecimal)
     */
    public static BigDecimal divide(BigDecimal v1, BigDecimal v2, Integer scale) {
        if (null == v1) {
            return BigDecimal.ZERO;
        }
        if (null == v2) {
            v2 = BigDecimal.ONE;
        }

        if (v2.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("除数不能为0");
        }

        if (scale < 0) {
            throw new IllegalArgumentException("精确度不能小于0");
        }

        return v1.divide(v2, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 精确加法运算
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和(String)
     */
    public static String add(String v1, String v2) {
        if (isBlank(v1)) {
            v1 = "0";
        }
        if (isBlank(v2)) {
            v2 = "0";
        }
        BigDecimal b1 = new BigDecimal(v1.trim());
        BigDecimal b2 = new BigDecimal(v2.trim());
        return String.valueOf(add(b1, b2));
    }


    /**
     * 精确减法运算
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差(String)
     */
    public static String subtract(String v1, String v2) {
        if (isBlank(v1)) {
            v1 = "0";
        }
        if (isBlank(v2)) {
            v2 = "0";
        }
        BigDecimal b1 = new BigDecimal(v1.trim());
        BigDecimal b2 = new BigDecimal(v2.trim());
        return String.valueOf(subtract(b1, b2));
    }

    /**
     * 精确乘法运算
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积(String)
     */
    public static String multiply(String v1, String v2) {
        if (isBlank(v1)) {
            v1 = "1";
        }
        if (isBlank(v2)) {
            v2 = "1";
        }
        BigDecimal b1 = new BigDecimal(v1.trim());
        BigDecimal b2 = new BigDecimal(v2.trim());
        return String.valueOf(multiply(b1, b2));
    }

    /**
     * ( 相对 )精确除法运算 , 当发生除不尽情况时 , 精确到 小数点以后2位 , 以后数字四舍五入
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商(String)
     */
    public static String divide(String v1, String v2) {
        return divide(v1, v2, DEF_DIV_SCALE);
    }

    /**
     * ( 相对 )精确除法运算 . 当发生除不尽情况时 , 由scale参数指 定精度 , 以后数字四舍五入
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位
     * @return 两个参数的商(String)
     */
    public static String divide(String v1, String v2, Integer scale) {
        if (null == v1) {
            return "0";
        }
        if (null == v2) {
            v2 = "1";
        }
        BigDecimal b1 = new BigDecimal(v1.trim());
        BigDecimal b2 = new BigDecimal(v2.trim());
        return String.valueOf(divide(b1, b2, scale));
    }

    /**
     * 精确加法运算 , 计算多个数值总和 , 若其中有null值则忽略
     *
     * @param valList 被加数集合
     * @return 两个参数的和(BigDecimal)
     */
    public static BigDecimal sum(BigDecimal v1, BigDecimal... valList) {
        if (null == v1) {
            v1 = BigDecimal.ZERO;
        }
        if (null == valList || valList.length == 0) {
            return v1;
        }
        for (BigDecimal val : valList) {
            if (null != val) {
                v1 = v1.add(val);
            }
        }
        return v1;
    }

    /**
     * 精确加法运算 , 计算多个数值总和 , 若其中有null值则忽略
     *
     * @param valList 被加数集合
     * @return 两个参数的和(String)
     */
    public static String sum(String v1, String... valList) {
        if (isBlank(v1)) {
            v1 = "0";
        }
        if (null == valList || valList.length == 0) {
            return v1;
        }
        BigDecimal b1 = new BigDecimal(v1.trim());
        for (String val : valList) {
            if (!isBlank(val)) {
                b1 = add(b1, new BigDecimal(val.trim()));
            }
        }
        return String.valueOf(b1);
    }

    /**
     * 平均数
     *
     * @param valList
     * @return
     */
    public static BigDecimal avg(BigDecimal... valList) {
        if (null != valList && valList.length != 0) {
            return divide(sum(BigDecimal.ZERO, valList), new BigDecimal(valList.length));
        }
        return BigDecimal.ZERO;
    }

    /**
     * 平均数
     *
     * @param valList
     * @return
     */
    public static String avg(String... valList) {
        if (null != valList && valList.length != 0) {
            return divide(sum("0", valList), String.valueOf(valList.length));
        }
        return "0";
    }

    /**
     * 最大值
     *
     * @param v1
     * @param valList
     * @return
     */
    public static BigDecimal max(BigDecimal v1, BigDecimal... valList) {
        BigDecimal max = v1;
        if (null == valList || valList.length == 0) {
            return max;
        }
        for (BigDecimal val : valList) {
            if (null != val && val.compareTo(max) > 0) {
                max = val;
            }
        }
        return max;
    }

    /**
     * 最大值
     *
     * @param valList
     * @return
     */
    public static BigDecimal maxArr(BigDecimal... valList) {
        if (null == valList || valList.length == 0) {
            return null;
        }

        return max(valList[0], valList);
    }

    /**
     * 最小值
     *
     * @param v1
     * @param valList
     * @return
     */
    public static BigDecimal min(BigDecimal v1, BigDecimal... valList) {
        BigDecimal min = v1;
        if (null == valList || valList.length == 0) {
            return min;
        }
        for (BigDecimal val : valList) {
            if (null != val && val.compareTo(min) < 0) {
                min = val;
            }
        }
        return min;
    }

    /**
     * 最小值
     *
     * @param valList
     * @return
     */
    public static BigDecimal minArr(BigDecimal... valList) {
        if (null == valList || valList.length == 0) {
            return null;
        }
        return min(valList[0], valList);
    }

    /**
     * 最大值
     *
     * @param v1
     * @param valList
     * @return
     */
    public static String max(String v1, String... valList) {
        if (isBlank(v1)) {
            return null;
        }
        if (null == valList || valList.length == 0) {
            return v1;
        }
        BigDecimal maxBd = new BigDecimal(v1.trim());

        for (String val : valList) {
            if (!isBlank(val) && new BigDecimal(val).compareTo(maxBd) > 0) {
                maxBd = new BigDecimal(val);
            }
        }
        return String.valueOf(maxBd);
    }

    /**
     * 最大值
     *
     * @param valList
     * @return
     */
    public static String maxArr(String... valList) {
        if (null == valList || valList.length == 0) {
            return null;
        }
        return max(valList[0], valList);
    }

    /**
     * 最小值
     *
     * @param v1
     * @param valList
     * @return
     */
    public static String min(String v1, String... valList) {
        if (isBlank(v1)) {
            return null;
        }
        if (null == valList || valList.length == 0) {
            return v1;
        }
        BigDecimal minBd = new BigDecimal(v1.trim());

        for (String val : valList) {
            if (!isBlank(val) && new BigDecimal(val).compareTo(minBd) < 0) {
                minBd = new BigDecimal(val);
            }
        }
        return String.valueOf(minBd);
    }

    /**
     * 最小值
     *
     * @param valList
     * @return
     */
    public static String minArr(String... valList) {
        if (null == valList || valList.length == 0) {
            return null;
        }
        return min(valList[0], valList);
    }

    /**
     * 判断字符串是否为空(不依赖第三方)
     *
     * @param str
     * @return
     */
    private static boolean isBlank(String str) {
        return null == str || str.trim().length() == 0;
    }

}
