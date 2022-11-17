package cn.htht.service.platform.portal.component.web.service;

import cn.htht.service.platform.portal.entity.system.SystemUser;
import cn.htht.service.platform.portal.entity.system.LoginUser;
import cn.htht.service.platform.portal.utils.enums.UserStatus;
import cn.htht.service.platform.portal.utils.exception.BaseException;
import cn.htht.service.platform.portal.utils.utils.StringUtils;
import cn.htht.service.platform.portal.system.service.ISystemUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户验证处理
 *
 * @author htht
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private ISystemUserService userService;

    @Autowired
    private SysPermissionService permissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemUser user = userService.selectUserByUserName(username);
        if (StringUtils.isNull(user)) {
            log.info("登录用户：{} 不存在.", username);
            throw new UsernameNotFoundException("登录用户：" + username + " 不存在");
        } else if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
            log.info("登录用户：{} 已被删除.", username);
            throw new BaseException("对不起，您的账号：" + username + " 已被删除");
        } else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new BaseException("对不起，您的账号：" + username + " 已停用");
        }

        return createLoginUser(user);
    }

    public UserDetails createLoginUser(SystemUser user) {
        return new LoginUser(user, permissionService.getPermissionKeyByUser(user));
    }
}
