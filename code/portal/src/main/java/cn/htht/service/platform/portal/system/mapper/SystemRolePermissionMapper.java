package cn.htht.service.platform.portal.system.mapper;


import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.system.SystemRolePermission;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 角色与菜单关联表 数据层
 *
 * @author htht
 */
@Component
public interface SystemRolePermissionMapper extends BasicMapper<SystemRolePermission> {

    void deleteRolePermissionByRoleId(@Param("roleId") String roleId);

    Set<String> selectPermissionKeysByUserId(@Param("userId") String userId);

    int insertBatch(@Param("list") List<SystemRolePermission> rpList);
}
