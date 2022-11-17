package cn.htht.service.platform.portal.utils.enums;

public enum DataType {
    BD("93159039c91111eba6bf70b5e84fe1ac"),

    ZB("9315b8d0c91111eba6bf70b5e84fe1ac");

    private String id;

    DataType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}