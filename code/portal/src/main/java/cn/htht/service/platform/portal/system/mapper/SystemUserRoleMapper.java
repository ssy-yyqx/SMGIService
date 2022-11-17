package cn.htht.service.platform.portal.system.mapper;

import cn.htht.service.platform.portal.entity.system.SystemUserRole;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户与角色关联表 数据层
 *
 * @author htht
 */
@Component
public interface SystemUserRoleMapper {
    /**
     * 通过用户ID删除用户和角色关联
     *
     * @param userId 用户ID
     * @return 结果
     */
    int deleteUserRoleByUserId(String userId);

    /**
     * 批量删除用户和角色关联
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteUserRole(String[] ids);

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    int countUserRoleByRoleId(String roleId);

    /**
     * 批量新增用户角色信息
     *
     * @param userRoleList 用户角色列表
     * @return 结果
     */
    int batchUserRole(List<SystemUserRole> userRoleList);

    /**
     * 删除用户和角色关联信息
     *
     * @param userRole 用户和角色关联信息
     * @return 结果
     */
    int deleteUserRoleInfo(SystemUserRole userRole);

    /**
     * 批量取消授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要删除的用户数据ID
     * @return 结果
     */
    int deleteUserRoleInfos(@Param("roleId") String roleId, @Param("userIds") String[] userIds);

    /**
     * 更改用户角色
     * @param roleId
     * @param userId
     */
    void updateRoleByUser(String roleId, String userId);

    /**
     * 查看权限是否存在
     * @param userId
     * @param roleId
     * @return
     */
    Integer existRole(String userId, String roleId);
}
