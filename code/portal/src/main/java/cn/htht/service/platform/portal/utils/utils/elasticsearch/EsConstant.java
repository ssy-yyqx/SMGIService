package cn.htht.service.platform.portal.utils.utils.elasticsearch;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;

public class EsConstant {

    public static final String REQEX_A_ZA_Z = "[a-zA-Z]+";//全为英文

    public static final String REQEX_NUMBER = "[0-9]+";//全为数字

    public static final String REQEX_A_ZA_Z0 = "[a-zA-Z0-9]+";//只有英文数字的字符串

    public static final String REQEX_CH_ONLY = "[\\u4e00-\\u9fa5]+";//为纯中文

    public static final String REQEX_S = "\\s*";

    public static final String REQEX_X = "*";

    public static final String TITLE_KEY_WORD = "title.keyword";

    public static final String TITLE_ = "title";

}
