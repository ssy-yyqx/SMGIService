package cn.netty.server.web.enums;

/**
 * @ClassName UploadStatus.java
 * @Description 类描述
 * @Author YangZiJie
 * @Version 1.0.0
 * @Date 2022年07月25日
 */
public enum  UploadStatus {
    //
    UNKNOWN(-1, "-"),
    FAIL(0, "失败"),
    FINISHED(1, "已完成"),
    UNFINISHED(2, "未完成");
    private final Integer key;
    private final String value;

    UploadStatus(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static UploadStatus getType(Integer dataTypeCode) {
        for (UploadStatus enums : UploadStatus.values()) {
            if (enums.key.equals(dataTypeCode)) {
                return enums;
            }
        }
        return UNKNOWN;
    }
}
