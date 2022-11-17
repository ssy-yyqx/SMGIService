package cn.htht.service.platform.portal.utils.enums;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/14
 */
public enum MessageTypeEnum {
    //上线通知
    online(1, "上线通知"),
    //下线通知
    offline(2, "下线通知"),
    //在线名单
    onlineUsers(3, "在线名单"),
    //普通消息
    message(4,"普通消息"),
    //系统消息
    sysMessage(5,"系统消息"),
    //超时断连
    outOfTime(6,"超时断连"),
    //繁忙
    busy(7, "繁忙"),
    //登入登出换名
    change(8, "换名");


    private int key;

    public String value;

    MessageTypeEnum() {
    }

    MessageTypeEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
