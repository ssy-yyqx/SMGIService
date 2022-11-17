package cn.htht.service.platform.portal.component.web.service;

import cn.htht.service.platform.portal.entity.system.SystemUser;
import cn.htht.service.platform.portal.system.service.ISystemPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 用户权限处理
 *
 * @author htht
 */
@Component
public class SysPermissionService {

    @Autowired
    private ISystemPermissionService permissionService;

    /**
     * 获取角色数据权限
     *
     * @param user 用户信息
     * @return 角色权限信息
     */
    public Set<String> getRolePermission(SystemUser user) {
        Set<String> roles = new HashSet<String>();
        // 管理员拥有所有权限
        if (user.getIsAdmin() == 1) {
            roles.add("admin");
        } else {
            roles.addAll(permissionService.selectPermsByUserId(user.getId()));
        }
        return roles;
    }

    /**
     * 获取菜单数据权限
     *
     * @param user 用户信息
     * @return 菜单权限信息
     */
    public Set<String> getPermissionKeyByUser(SystemUser user) {
        Set<String> perms = new HashSet<String>();
        // 管理员拥有所有权限
        if (user.getIsAdmin() == 1) {
            perms.add("*:*:*");
        } else {
            perms.addAll(permissionService.selectPermsByUserId(user.getId()));
        }
        return perms;
    }
}
