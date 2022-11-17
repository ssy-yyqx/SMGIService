package cn.htht.service.platform.portal.utils.enums;

/**
 * @author piesat
 * @className ChatTypeEnum
 * @description 描述
 * @date 2021/9/26
 */
public enum ChatTypeEnum {
    //管理员列表
    adminListChatType("admin_list", "管理员列表"),
    //管理员聊天
    adminChatType("admin_chat", "管理员聊天"),
    //用户聊天
    userChatType("user_chat", "用户聊天"),
    //消息数目
    chatCountType("chat_count","消息数目");


    private String key;

    public String value;

    ChatTypeEnum() {
    }

    ChatTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}