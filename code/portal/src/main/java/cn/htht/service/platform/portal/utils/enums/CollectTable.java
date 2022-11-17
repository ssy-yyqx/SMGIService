package cn.htht.service.platform.portal.utils.enums;

/**
 * @Classname CollectTable
 * @Description TODO
 * @Date 2021/6/30 16:37
 * @Created by SunGao
 */
public enum CollectTable {

    ZZLL_BD("ZZLL_BD"),

    ZZLL_BD_JB("ZZLL_BD_JB"),

    ZZLL_GJGWRY("ZZLL_GJGWRY"),

    ZZLL_GJGWRY_JBQK("ZZLL_GJGWRY_JBQK"),

    ZZLL_BD_LSYG("ZZLL_BD_LSYG"),//作战力量_部队_历史沿革

    QB_TJWJQK_ZZFA_NR("QB_TJWJQK_ZZFA_NR"),//情报_台军、外军情况_作战方案_内容

    QB_TJWJQK_JBZL_NR("QB_TJWJQK_JBZL_NR"),//情报_台军、外军情况_基本资料_内容

    ZZLL_GJGWRY_GJGWRYLB("ZZLL_GJGWRY_GJGWRYLB"),

    ZZBZ_S_TY_MTGS("ZZBZ_S_TY_MTGS"),

    ZZBZ_SS_ZB_ZBZJXN("ZZBZ_SS_ZB_ZBZJXN");


    private String tableName;

    CollectTable(String tableName) {
        this.tableName = tableName;
    }

    public String tableName() {
        return tableName;
    }
}
