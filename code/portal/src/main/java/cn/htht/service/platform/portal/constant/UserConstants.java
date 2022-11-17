package cn.htht.service.platform.portal.constant;

import cn.htht.service.platform.portal.entity.system.SystemRole;

/**
 * 常量信息
 *
 * @author htht
 */
public class UserConstants {
    /**
     * 平台内系统用户的唯一标志
     */
    public static final String SYS_USER = "SYS_USER";

    /**
     * 正常状态
     */
    public static final Integer NORMAL = 0;

    /**
     * 异常状态
     */
    public static final Integer EXCEPTION = 1;

    /**
     * 否
     */
    public static final Integer IS_NO = 0;

    /**
     * 是
     */
    public static final Integer IS_YES = 1;


    /**
     * 用户封禁状态
     */
    public static final Integer USER_DISABLE = 1;

    /**
     * 角色封禁状态
     */
    public static final Integer ROLE_DISABLE = 1;


    /**
     * 是否为系统默认（是）
     */
    public static final String YES = "Y";

    /**
     * 是否菜单外链（是）
     */
    public static final String YES_FRAME = "0";

    /**
     * 是否菜单外链（否）
     */
    public static final String NO_FRAME = "1";

    /**
     * 菜单类型（目录）
     */
    public static final String TYPE_DIR = "M";

    /**
     * 菜单类型（菜单）
     */
    public static final String TYPE_MENU = "C";

    /**
     * 菜单类型（按钮）
     */
    public static final String TYPE_BUTTON = "F";

    /**
     * Layout组件标识
     */
    public final static String LAYOUT = "Layout";

    /**
     * ParentView组件标识
     */
    public final static String PARENT_VIEW = "ParentView";

    /**
     * 校验返回结果码
     */
    public final static String UNIQUE = "0";
    public final static String NOT_UNIQUE = "1";

    /**
     * 未注册用户角色id
     */
    public final static String UNREGISTER_USER_ROLE_ID = "251096a7c841416aa234fc58b30d399c";

    /**
     * 普通用户角色id
     */
    public final static String NORMAL_USER_ROLE_ID = "fc2f45ea136448029eddd45ebfd6eeaa";

    /**
     * 未审批
     */
    public final static Integer USER_OFFLINE_APPOINTMENT_NOT_APPROVED = 0;

    /**
     * 已撤销
     */
    public final static Integer USER_OFFLINE_APPOINTMENT_REVOKED = 1;

    /**
     * 审批未通过
     */
    public final static Integer USER_OFFLINE_APPOINTMENT_FAILED = 2;

    /**
     * 审批通过
     */
    public final static Integer USER_OFFLINE_APPOINTMENT_PASSED = 3;

    /**
     * 操作完成
     */
    public final static Integer USER_OFFLINE_APPOINTMENT_FINISH = 4;

    public static final String CUSTOMER_SERVICE_ROLE_ID = "13";

    /**
     * 初始化密码
     */
    public static final String INIT_PASSWORD = "888888";
}
