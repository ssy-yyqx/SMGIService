package cn.htht.service.platform.portal.system.service.impl;

import cn.htht.service.platform.portal.common.AbstractBaseService;
import cn.htht.service.platform.portal.entity.system.SystemRole;
import cn.htht.service.platform.portal.entity.system.SystemRolePermission;
import cn.htht.service.platform.portal.system.mapper.SystemRoleMapper;
import cn.htht.service.platform.portal.system.mapper.SystemRolePermissionMapper;
import cn.htht.service.platform.portal.system.mapper.SystemUserRoleMapper;
import cn.htht.service.platform.portal.constant.UserConstants;
import cn.htht.service.platform.portal.utils.exception.CustomException;
import cn.htht.service.platform.portal.utils.utils.StringUtils;
import cn.htht.service.platform.portal.utils.utils.spring.SpringUtils;
import cn.htht.service.platform.portal.system.service.ISystemRoleService;
import cn.htht.service.platform.portal.utils.utils.uuid.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 角色 业务层处理
 *
 * @author htht
 */
@Service
public class SystemRoleServiceImpl extends AbstractBaseService<SystemRole> implements ISystemRoleService {
    @Autowired
    private SystemRoleMapper roleMapper;

    @Autowired
    private SystemRolePermissionMapper rolePermissionMapper;

    @Autowired
    private SystemUserRoleMapper userRoleMapper;


    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    @Override
    public List<SystemRole> selectRoleList(SystemRole role) {
        return roleMapper.selectRoleList(role);
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    @Override
    public List<SystemRole> selectRoleAll() {
        return roleMapper.selectRoleAll();
    }


    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    @Override
    public SystemRole selectRoleById(String roleId) {
        return roleMapper.selectRoleById(roleId);
    }

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public String checkRoleNameUnique(SystemRole role) {
        String roleId = StringUtils.isNull(role.getId()) ? "" : role.getId();
        SystemRole info = roleMapper.checkRoleNameUnique(role.getRoleName());
        if (StringUtils.isNotNull(info) && info.getId().equals(roleId)) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验角色权限是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public String checkRoleKeyUnique(SystemRole role) {
        String roleId = StringUtils.isNull(role.getId()) ? "" : role.getId();
        SystemRole info = roleMapper.checkRoleKeyUnique(role.getRoleKey());
        if (StringUtils.isNotNull(info) && info.getId().equals(roleId)) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    @Override
    public void checkRoleAllowed(SystemRole role) {
        if (StringUtils.isNotNull(role.getId()) && role.getRoleKey().equals("admin")) {
            throw new CustomException("不允许操作超级管理员角色");
        }
    }

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertRole(SystemRole role) {
        role.setId(IdUtils.fastSimpleUUID());
        // 新增角色信息
        roleMapper.insertRole(role);
        return insertRolePermission(role);
    }

    /**
     * 修改保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateRole(SystemRole role) {
        // 修改角色信息
        roleMapper.updateRole(role);
        // 删除角色与菜单关联
        rolePermissionMapper.deleteRolePermissionByRoleId(role.getId());
        return insertRolePermission(role);
    }

    /**
     * 新增角色菜单信息
     *
     * @param role 角色对象
     */
    public int insertRolePermission(SystemRole role) {
        int rows = 1;
        String[] perms = role.getPermStrIds();
        if (perms.length > 0) {
            List<SystemRolePermission> rpList = new ArrayList<>();
            for (String permId : perms) {
                SystemRolePermission rp = new SystemRolePermission();
                rp.setId(IdUtils.fastSimpleUUID());
                rp.setRoleId(role.getId());
                rp.setPermId(permId);
                rpList.add(rp);
            }
            rows = rolePermissionMapper.insertBatch(rpList);
        } else {
            rows = 0;
        }
        return rows;
    }

    /**
     * 通过角色ID删除角色
     *
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteRoleById(String roleId) {
        // 删除角色与菜单关联
        rolePermissionMapper.deleteRolePermissionByRoleId(roleId);
        return roleMapper.deleteRoleById(roleId);
    }

}
