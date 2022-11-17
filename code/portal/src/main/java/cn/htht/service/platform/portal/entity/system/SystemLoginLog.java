package cn.htht.service.platform.portal.entity.system;

import cn.htht.service.platform.portal.common.BaseEntity;
import lombok.Data;
import lombok.ToString;

/**
 * @ClassName SystemLoginLog
 * @Description 登录日志表
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
public class SystemLoginLog extends BaseEntity {

    /**
     * 操作类型 1：登录 2：注销
     */
    private Integer operateType;

    /**
     * 登录用户id
     */
    private String loginUserId;

    /**
     * 登录用户名
     */
    private String loginUsername;

    /**
     * 登录ip
     */
    private String loginIp;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 提示消息
     */
    private String msg;

    /**
     * 状态 0 成功 1 失败
     */
    private Integer status;
}
