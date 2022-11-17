package cn.htht.service.platform.portal.entity.system;

import cn.htht.service.platform.portal.common.BaseEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Id;

/**
 * @ClassName SystemMenuRole
 * @Description 菜单权限表
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
    public class SystemRolePermission {

    @Id
    private String id;

    private String roleId;

    private String permId;
}
