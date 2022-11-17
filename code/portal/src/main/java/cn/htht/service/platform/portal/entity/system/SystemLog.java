package cn.htht.service.platform.portal.entity.system;

import cn.htht.service.platform.portal.common.BaseEntity;
import lombok.Data;
import lombok.ToString;

/**
 * @ClassName SystemLog
 * @Description 系统日志表
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
public class SystemLog extends BaseEntity {

    /**
     * 操作模块
     */
    private String moduleName;

    /**
     * 操作类型：0=其它,1=新增,2=修改,3=删除,4=授权,5=导出,6=导入
     */
    private Integer operateType;

    /**
     * 调用方法名
     */
    private String method;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 操作用户id
     */
    private String operateUserId;

    /**
     * n
     * 操作用户名
     */
    private String operateUsername;
    /**
     * 操作用户真实姓名
     * */
    private String operateRealName;

    /**
     * 操作角色id
     */
    private String operateRoleId;

    /**
     * 操作角色名称
     */
    private String operateRoleName;

    /**
     * 操作结果状态 1：成功 2：失败
     */
    private Integer status;

    /**
     * 用户ip
     */
    private String ipAddr;

    /**
     * 请求参数
     */
    private String operateParam;

    /**
     * 返回结果
     */
    private String jsonResult;

    /**
     * 操作耗时
     */
    private Long costTime;

    /**
     * 错误信息
     */
    private String errorMsg;

    private Integer[] operateTypes;

}
