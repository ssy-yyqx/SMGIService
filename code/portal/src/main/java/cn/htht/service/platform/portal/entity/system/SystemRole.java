package cn.htht.service.platform.portal.entity.system;

import cn.htht.service.platform.portal.common.BaseEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * @ClassName SystemRole
 * @Description 权限表
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
public class SystemRole extends BaseEntity {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色排序
     */
    private Integer roleSort;

    /**
     * 角色权限
     */
    private String roleKey;

    /**
     * 角色状态 (0 生效 1 未生效)
     */
    private Integer status;

    /**
     * 角色描述
     */
    private String describe;

    /**
     * 所选权限的Id字符串，多个以逗号分隔
     */
    private String[] permStrIds;

    /**
     * 权限列表
     */
    List<SystemPermissionStr> permissionStrList;

    /**
     * 权限组
     */
    private Map<String, Boolean> permissionIds;

}
