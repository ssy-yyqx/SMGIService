package cn.htht.service.platform.portal.system.service;


import cn.htht.service.platform.portal.common.BaseService;
import cn.htht.service.platform.portal.entity.system.SystemPermissionStr;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ISystemPermissionService extends BaseService<SystemPermissionStr> {

    Set<String> selectPermsByUserId(String id);

    int savePermission(String name, String keyIds);

    int savePermissionKey(String key, String router);

    List<SystemPermissionStr> selectAllPermission();
}
