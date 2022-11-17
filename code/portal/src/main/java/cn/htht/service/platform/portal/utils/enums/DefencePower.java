package cn.htht.service.platform.portal.utils.enums;

/**
 * 力量类别
 *
 * @author 王卓
 */
public enum DefencePower {
    LLLB("LLLB", "力量类别"),
    BZZY("BZZY", "保障专业"),
    BZFS("BZFS", "保障方式"),
    FWLLMC("FWLLMC", "防卫力量名称"),
    RYS("RYS", "人员数");
    private final String code;
    private final String desc;

    DefencePower(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static String getDescByCode(String code) {
        for (DefencePower ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele.getDesc();
            }
        }
        return null;
    }
}
