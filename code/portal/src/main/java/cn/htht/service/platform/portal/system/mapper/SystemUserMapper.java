package cn.htht.service.platform.portal.system.mapper;

import cn.htht.service.platform.portal.entity.system.SystemUser;
import cn.htht.service.platform.portal.entity.vo.UserVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户表 数据层
 *
 * @author htht
 */
@Component
public interface SystemUserMapper {
    /**
     * 根据条件分页查询用户列表
     *
     * @param SystemUser 用户信息
     * @return 用户信息集合信息
     */
    List<SystemUser> selectUserList(SystemUser SystemUser);

    /**
     * 通过用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象信息
     */
    SystemUser selectUserByUserName(String username);

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    SystemUser selectUserById(String userId);

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

//    /**
//     * 修改用户头像
//     *
//     * @param username 用户名
//     * @param avatar 头像地址
//     * @return 结果
//     */
//    int updateUserAvatar(@Param("username") String username, @Param("avatar") String avatar);

    /**
     * 重置用户密码
     *
     * @param username 用户名
     * @param password 密码
     * @return 结果
     */
    int resetUserPwd(@Param("username") String username, @Param("password") String password);

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

    /**
     * 校验用户名称是否唯一
     *
     * @param username 用户名称
     * @return 结果
     */
    int checkUserNameUnique(String username);

    /**
     * 校验手机号码是否唯一
     *
     * @param phoneNumber 手机号码
     * @return 结果
     */
    SystemUser checkPhoneUnique(String phoneNumber);

    /**
     * 校验email是否唯一
     *
     * @param email 用户邮箱
     * @return 结果
     */
    SystemUser checkEmailUnique(String email);

    /**
     * 根据用户名查询用户列表
     *
     * @param username
     * @return
     */
    List<UserVo> selectUserListByUsername(String username);
}
