package cn.htht.service.platform.portal.system.service;


import cn.htht.service.platform.portal.common.BaseService;
import cn.htht.service.platform.portal.entity.system.SystemRole;

import java.util.List;
import java.util.Set;

/**
 * 角色业务层
 *
 * @author htht
 */
public interface ISystemRoleService extends BaseService<SystemRole> {
    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    List<SystemRole> selectRoleList(SystemRole role);


    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    List<SystemRole> selectRoleAll();


    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    SystemRole selectRoleById(String roleId);

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    String checkRoleNameUnique(SystemRole role);

    /**
     * 校验角色权限是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    String checkRoleKeyUnique(SystemRole role);

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    void checkRoleAllowed(SystemRole role);


    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    int insertRole(SystemRole role);

    /**
     * 修改保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    int updateRole(SystemRole role);

    /**
     * 删除角色
     * @param roleId
     * @return
     */
    int deleteRoleById(String roleId);
}
