package cn.htht.service.platform.portal.system.service.impl;

import cn.htht.service.platform.portal.entity.system.SystemRole;
import cn.htht.service.platform.portal.entity.system.SystemUser;
import cn.htht.service.platform.portal.entity.vo.UserVo;
import cn.htht.service.platform.portal.system.mapper.SystemRoleMapper;
import cn.htht.service.platform.portal.system.mapper.SystemUserMapper;
import cn.htht.service.platform.portal.system.mapper.SystemUserRoleMapper;
import cn.htht.service.platform.portal.constant.UserConstants;
import cn.htht.service.platform.portal.utils.exception.CustomException;
import cn.htht.service.platform.portal.utils.utils.SecurityUtils;
import cn.htht.service.platform.portal.utils.utils.StringUtils;
import cn.htht.service.platform.portal.entity.system.SystemUserRole;
import cn.htht.service.platform.portal.system.service.ISystemConfigService;
import cn.htht.service.platform.portal.system.service.ISystemUserService;
import cn.htht.service.platform.portal.utils.utils.uuid.IdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户 业务层处理
 *
 * @author htht
 */
@Service
public class SystemUserServiceImpl implements ISystemUserService {

    private static final Logger log = LoggerFactory.getLogger(SystemUserServiceImpl.class);

    @Autowired
    private SystemUserMapper userMapper;

    @Autowired
    private SystemRoleMapper roleMapper;

    @Autowired
    private SystemUserRoleMapper userRoleMapper;

    @Autowired
    private ISystemConfigService configService;

    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<SystemUser> selectUserList(SystemUser user) {
        return userMapper.selectUserList(user);
    }

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SystemUser selectUserByUserName(String userName) {
        return userMapper.selectUserByUserName(userName);
    }

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SystemUser selectUserById(String userId) {
        return userMapper.selectUserById(userId);
    }


    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    @Override
    public String checkUserNameUnique(String userName) {
        int count = userMapper.checkUserNameUnique(userName);
        if (count > 0) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkPhoneUnique(SystemUser user) {
        String userId = StringUtils.isNull(user.getId()) ? "" : user.getId();
        SystemUser info = userMapper.checkPhoneUnique(user.getPhoneNumber());
        if (StringUtils.isNotNull(info) && info.getId().equals(userId)) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkEmailUnique(SystemUser user) {
        String userId = StringUtils.isNull(user.getId()) ? "" : user.getId();
        SystemUser info = userMapper.checkEmailUnique(user.getEmail());
        if (StringUtils.isNotNull(info) && info.getId().equals(userId)) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    @Override
    public void checkUserAllowed(SystemUser user) {
        if (StringUtils.isNotNull(user.getId()) && user.getIsAdmin() == 1) {
            throw new CustomException("不允许操作超级管理员用户");
        }
    }


    @Override
    @Transactional
    public int insertNormalUser(SystemUser user) {
        user.setId(IdUtils.fastSimpleUUID());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setDelFlag(UserConstants.IS_NO);
        user.setIsAdmin(UserConstants.IS_NO);
        user.setStatus(UserConstants.IS_NO);
        user.setRoleIds(new String[] {UserConstants.NORMAL_USER_ROLE_ID});
        // 新增用户信息
        int rows = userMapper.insertUser(user);
        // 新增用户与角色管理
        insertUserRole(user);
        return rows;
    }

    @Override
    public List<UserVo> selectUserListByUsername(String username) {
        return userMapper.selectUserListByUsername(username);
    }

    @Override
    public void updateUserRole(String userId, String roleId) {
        if (userRoleMapper.existRole(userId, roleId) == 0) {
            SystemUserRole ur = new SystemUserRole();
            ur.setId(IdUtils.fastSimpleUUID());
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            List<SystemUserRole> list = new ArrayList<SystemUserRole>();
            list.add(ur);
            userRoleMapper.batchUserRole(list);
            return;
        }
        userRoleMapper.updateRoleByUser(roleId, userId);
    }

    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertUser(SystemUser user) {
        user.setId(IdUtils.fastSimpleUUID());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setDelFlag(UserConstants.IS_NO);
        // 新增用户信息
        int rows = userMapper.insertUser(user);

        // 新增用户与角色管理
        insertUserRole(user);
        return rows;
    }

    /**
     * 修改保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateUser(SystemUser user) {
        String userId = user.getId();
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 新增用户与角色管理
        insertUserRole(user);
        return userMapper.updateUser(user);
    }

    /**
     * 重置用户密码
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int resetPwd(SystemUser user) {
        return userMapper.updateUser(user);
    }

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    @Override
    public int resetUserPwd(String userName, String password) {
        return userMapper.resetUserPwd(userName, password);
    }

    /**
     * 新增用户角色信息
     *
     * @param user 用户对象
     */
    public void insertUserRole(SystemUser user) {
        String[] roles = user.getRoleIds();
        if (StringUtils.isNotNull(roles)) {
            // 新增用户与角色管理
            List<SystemUserRole> list = new ArrayList<SystemUserRole>();
            for (String roleId : roles) {
                SystemUserRole ur = new SystemUserRole();
                ur.setId(IdUtils.fastSimpleUUID());
                ur.setUserId(user.getId());
                ur.setRoleId(roleId);
                list.add(ur);
            }
            if (list.size() > 0) {
                userRoleMapper.batchUserRole(list);
            }
        }
    }

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserById(String userId) {
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        return userMapper.deleteUserById(userId);
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserByIds(String[] userIds) {
        for (String userId : userIds) {
            SystemUser systemUser = userMapper.selectUserById(userId);
            checkUserAllowed(systemUser);
        }
        // 删除用户与角色关联
        userRoleMapper.deleteUserRole(userIds);
        return userMapper.deleteUserByIds(userIds);
    }
}
