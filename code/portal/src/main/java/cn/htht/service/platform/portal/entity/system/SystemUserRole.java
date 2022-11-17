package cn.htht.service.platform.portal.entity.system;

import cn.htht.service.platform.portal.common.BaseEntity;
import lombok.Data;
import lombok.ToString;

/**
 * @ClassName SystemUserRole
 * @Description 用户权限表
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
public class SystemUserRole extends BaseEntity {

    private String userId;

    private String roleId;
}
