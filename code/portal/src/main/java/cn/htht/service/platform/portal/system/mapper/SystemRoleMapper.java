package cn.htht.service.platform.portal.system.mapper;


import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.system.SystemRole;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 角色表 数据层
 *
 * @author htht
 */
@Component
public interface SystemRoleMapper extends BasicMapper<SystemRole> {
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
     * @param roleName 角色名称
     * @return 角色信息
     */
    SystemRole checkRoleNameUnique(String roleName);

    /**
     * 校验角色权限是否唯一
     *
     * @param roleKey 角色权限
     * @return 角色信息
     */
    SystemRole checkRoleKeyUnique(String roleKey);

    /**
     * 修改角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    int updateRole(SystemRole role);

    /**
     * 新增角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    int insertRole(SystemRole role);

    /**
     * 通过角色ID删除角色
     *
     * @param roleId 角色ID
     * @return 结果
     */
    int deleteRoleById(String roleId);

}
