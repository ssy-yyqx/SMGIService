package cn.htht.service.platform.portal.system.service;


import cn.htht.service.platform.portal.entity.system.SystemRole;
import cn.htht.service.platform.portal.entity.system.SystemUser;
import cn.htht.service.platform.portal.entity.vo.UserVo;

import java.util.List;

/**
 * 用户 业务层
 *
 * @author htht
 */
public interface ISystemUserService {
    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    List<SystemUser> selectUserList(SystemUser user);

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    SystemUser selectUserByUserName(String userName);

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    SystemUser selectUserById(String userId);



    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    String checkUserNameUnique(String userName);

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    String checkPhoneUnique(SystemUser user);

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    String checkEmailUnique(SystemUser user);

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    void checkUserAllowed(SystemUser user);

    /**
     * 新增用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    int insertUser(SystemUser user);

    /**
     * 修改用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    int updateUser(SystemUser user);

    /**
     * 重置用户密码
     *
     * @param user 用户信息
     * @return 结果
     */
    int resetPwd(SystemUser user);

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    int resetUserPwd(String userName, String password);

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    int deleteUserById(String userId);

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    int deleteUserByIds(String[] userIds);


    int insertNormalUser(SystemUser user);

    /**
     * 查询用户列表
     * @param username
     * @return
     */
    List<UserVo> selectUserListByUsername(String username);

    /**
     * 修改用户角色
     * @param userId 用户
     * @param roleId 角色
     */
    void updateUserRole(String userId, String roleId);
}
