package cn.htht.service.platform.portal.utils.helper;

import cn.htht.service.platform.portal.constant.RegConstant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName RegHelper
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
public class RegHelper {

    /**
     * 判断是否为整数
     *
     * @param src 源字符串
     * @return 是否数字的标志
     */
    public final static boolean isIntegerNumeric(String src) {
        boolean return_value = false;
        if (src != null && src.length() > 0) {
            Matcher m = RegConstant.INTEGER_NUMERIC_PATTERN.matcher(src);
            if (m.find()) {
                return_value = true;
            }
        }
        return return_value;
    }

    /**
     * 判断是否为数字表示
     *
     * @param src 源字符串
     * @return 是否数字的标志
     */
    public final static boolean isNumeric(String src) {
        boolean return_value = false;
        if (src != null && src.length() > 0) {
            Matcher m = RegConstant.NUMERIC_PATTERN.matcher(src);
            if (m.find()) {
                return_value = true;
            }
        }
        return return_value;
    }

    /**
     * 判断是否纯字母组合
     *
     * @param src 源字符串
     * @return 是否纯字母组合的标志
     */
    public final static boolean isABC(String src) {
        boolean return_value = false;
        if (src != null && src.length() > 0) {
            Matcher m = RegConstant.ABC_PATTERN.matcher(src);
            if (m.find()) {
                return_value = true;
            }
        }
        return return_value;
    }


    /**
     * 判断是否浮点数字表示
     *
     * @param src 源字符串
     * @return 是否数字的标志
     */
    public final static boolean isFloatNumeric(String src) {
        boolean return_value = false;
        if (src != null && src.length() > 0) {
            Matcher m = RegConstant.FLOAT_NUMERIC_PATTERN.matcher(src);
            if (m.find()) {
                return_value = true;
            }
        }
        return return_value;
    }

    /**
     * 判断字符串str是否符合正则表达式reg
     *
     * @param str 需要处理的字符串
     * @param reg 正则
     * @return 是否匹配
     */
    public final static boolean isMatche(String str, String reg) {
        Pattern pattern = Pattern.compile(reg);
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 获取符合reg正则表达式的字符串在String中出现的次数
     *
     * @param str 需要处理的字符串
     * @param reg 正则
     * @return 出现的次数
     */
    public final static int countSubStrReg(String str, String reg) {
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);
        int i = 0;
        while (m.find()) {
            i++;
        }
        return i;
    }


    /**
     * 判断是否是符合邮箱
     *
     * @param email 判断的字符串
     * @return 是否是符合的邮箱
     */
    public final static boolean isEmail(String email) {
        if (email == null || email.length() < 1 || email.length() > 256) {
            return false;
        }
        Pattern pattern = Pattern.compile(RegConstant.REG_EMAIL);
        return pattern.matcher(email).matches();
    }

    /**
     * 判断是否符合身份证编号
     *
     * @param IDNumber 身份证号
     * @return 是否是符合的正确的身份证号
     */
    public static boolean isIDNumber(String IDNumber) {
        if (IDNumber == null || "".equals(IDNumber)) {
            return false;
        }
        // 定义判别用户身份证号的正则表达式（15位或者18位，最后一位可以为字母）
        //假设18位身份证号码:41000119910101123X  410001 19910101 123X
        //^开头
        //[1-9] 第一位1-9中的一个      4
        //\\d{5} 五位数字           10001（前六位省市县地区）
        //(18|19|20)                19（现阶段可能取值范围18xx-20xx年）
        //\\d{2}                    91（年份）
        //((0[1-9])|(10|11|12))     01（月份）
        //(([0-2][1-9])|10|20|30|31)01（日期）
        //\\d{3} 三位数字            123（第十七位奇数代表男，偶数代表女）
        //[0-9Xx] 0123456789Xx其中的一个 X（第十八位为校验值）
        //$结尾

        //假设15位身份证号码:410001910101123  410001 910101 123
        //^开头
        //[1-9] 第一位1-9中的一个      4
        //\\d{5} 五位数字           10001（前六位省市县地区）
        //\\d{2}                    91（年份）
        //((0[1-9])|(10|11|12))     01（月份）
        //(([0-2][1-9])|10|20|30|31)01（日期）
        //\\d{3} 三位数字            123（第十五位奇数代表男，偶数代表女），15位身份证不含X
        //$结尾

        boolean matches = IDNumber.matches(RegConstant.REG_IDENTIFICATION_CARD);

        //判断第18位校验值
        if (matches) {
            if (IDNumber.length() == 18) {
                try {
                    char[] charArray = IDNumber.toCharArray();
                    //前十七位加权因子
                    int[] idCardWi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
                    //这是除以11后，可能产生的11位余数对应的验证码
                    String[] idCardY = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
                    int sum = 0;
                    for (int i = 0; i < idCardWi.length; i++) {
                        int current = Integer.parseInt(String.valueOf(charArray[i]));
                        int count = current * idCardWi[i];
                        sum += count;
                    }
                    char idCardLast = charArray[17];
                    int idCardMod = sum % 11;
                    if (idCardY[idCardMod].toUpperCase().equals(String.valueOf(idCardLast).toUpperCase())) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return matches;
    }

    /**
     * 手机号码输入是否正确
     *
     * @param phone
     * @return
     */
    public static boolean isPhoneNumber(String phone) {
        if (phone == null || phone.length() != 11) {
            return false;
        }
        Pattern pattern = Pattern.compile(RegConstant.REG_MOBILE_TELEPHONE);
        return pattern.matcher(phone).matches();
    }

    /**
     * 用户账户输入是否合法（字母开头，允许5-16字节，允许字母数字下划线）
     *
     * @param account
     * @return
     */
    public static boolean legalAccount(String account) {
        if (account == null || account.length() < 5 || account.length() > 16) {
            return false;
        }
        Pattern pattern = Pattern.compile(RegConstant.REG_LEGAL_ACCOUNT);
        return pattern.matcher(account).matches();
    }

    /**
     * 判断是否输入的是否汉字
     *
     * @param input
     * @return
     */
    public static boolean isChinese(String input) {
        if (input == null || input.length() < 0) {
            return false;
        }
        Pattern pattern = Pattern.compile(RegConstant.REG_CHINESE);
        return pattern.matcher(input).matches();
    }


    /**
     * 根据正则匹配进行替换
     *
     * @param regex_pattern 要匹配的正则表达式
     * @param replacement   替换内容
     * @param target        要替换的目标
     * @return
     */
    private static String regexReplace(final Pattern regex_pattern, final String replacement, final String target) {
        Matcher m = regex_pattern.matcher(target);
        return m.replaceAll(replacement);
    }
}
