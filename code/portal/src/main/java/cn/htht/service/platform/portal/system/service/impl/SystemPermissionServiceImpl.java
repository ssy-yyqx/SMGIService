package cn.htht.service.platform.portal.system.service.impl;

import cn.htht.service.platform.portal.common.AbstractBaseService;
import cn.htht.service.platform.portal.entity.system.SystemPermissionKey;
import cn.htht.service.platform.portal.entity.system.SystemPermissionStr;
import cn.htht.service.platform.portal.entity.system.SystemPermissionStrKey;
import cn.htht.service.platform.portal.system.mapper.SystemPermissionKeyMapper;
import cn.htht.service.platform.portal.system.mapper.SystemPermissionStrKeyMapper;
import cn.htht.service.platform.portal.system.mapper.SystemPermissionStrMapper;
import cn.htht.service.platform.portal.system.mapper.SystemRolePermissionMapper;
import cn.htht.service.platform.portal.system.service.ISystemPermissionService;
import cn.htht.service.platform.portal.utils.utils.uuid.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 菜单 业务层处理
 *
 * @author htht
 */
@Service
public class SystemPermissionServiceImpl extends AbstractBaseService<SystemPermissionStr> implements ISystemPermissionService  {

    @Autowired
    private SystemPermissionStrMapper systemPermissionStrMapper;

    @Autowired
    private SystemPermissionKeyMapper systemPermissionKeyMapper;

    @Autowired
    private SystemRolePermissionMapper systemRolePermissionMapper;

    @Autowired
    private SystemPermissionStrKeyMapper systemPermissionStrKeyMapper;

    @Override
    public Set<String> selectPermsByUserId(String userId) {
        return systemRolePermissionMapper.selectPermissionKeysByUserId(userId);
    }

    @Override
    public int savePermission(String name, String keyIds) {
        String id = IdUtils.fastSimpleUUID();
        SystemPermissionStr systemPermissionStr = new SystemPermissionStr();
        systemPermissionStr.setId(id);
        systemPermissionStr.setPermName(name);
        int rows = systemPermissionStrMapper.insertStr(systemPermissionStr);
        List<SystemPermissionStrKey> list = new ArrayList<>();
        for (String keyId : keyIds.split(",")) {
            SystemPermissionStrKey sk = new SystemPermissionStrKey();
            sk.setId(IdUtils.fastSimpleUUID());
            sk.setStrId(id);
            sk.setKeyId(keyId);
            list.add(sk);
        }
        systemPermissionStrKeyMapper.insertBatch(list);
        return rows;
    }

    @Override
    public int savePermissionKey(String key, String router) {
        SystemPermissionKey systemPermissionKey = new SystemPermissionKey();
        systemPermissionKey.setId(IdUtils.fastSimpleUUID());
        systemPermissionKey.setPermKey(key);
        systemPermissionKey.setRouter(router);
        return systemPermissionKeyMapper.insertKeys(systemPermissionKey);
    }

    @Override
    public List<SystemPermissionStr> selectAllPermission() {
        return systemPermissionStrMapper.selectAllPermission();
    }
}
