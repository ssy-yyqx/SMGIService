package cn.htht.service.platform.portal.entity.system;

import lombok.Data;

import javax.persistence.Id;

/**
 * @ClassName SystemPermissionStrKey
 * @Description 多对多关系，多个用户权限名称对应多个权限关键字
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
public class SystemPermissionStrKey {

    @Id
    private String id;
    /**
     * 用户权限名称
     */
    private String strId;

    /**
     * 用户权限关键字
     */
    private String keyId;
}
