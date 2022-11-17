package cn.htht.service.platform.portal.component.web.service;

import cn.htht.service.platform.portal.component.manager.AsyncManager;
import cn.htht.service.platform.portal.component.manager.factory.AsyncFactory;
import cn.htht.service.platform.portal.constant.Constants;
import cn.htht.service.platform.portal.entity.system.LoginUser;
import cn.htht.service.platform.portal.entity.system.SystemRole;
import cn.htht.service.platform.portal.utils.core.redis.RedisCache;
import cn.htht.service.platform.portal.utils.exception.BaseException;
import cn.htht.service.platform.portal.utils.exception.CustomException;
import cn.htht.service.platform.portal.utils.exception.user.UserPasswordNotMatchException;
import cn.htht.service.platform.portal.utils.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录校验方法
 *
 * @author htht
 */
@Component
public class SysLoginService {
    @Autowired
    private TokenService tokenService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public String login(String username, String password, String code, String uuid, boolean isManager) {
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        /* 验证码验证
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
            throw new CaptchaException();
        }*/
        // 用户验证
        Authentication authentication = null;
        try {
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                AsyncManager.me().execute(AsyncFactory.recordLoginlog(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLoginlog(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new CustomException(e.getMessage());
            }
        }
        AsyncManager.me().execute(AsyncFactory.recordLoginlog(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        if (isManager) {
            // 需要判断其是否有管理员角色
            List<SystemRole> roleList = loginUser.getUser().getRoleList().stream().filter(systemRole -> (systemRole.getRoleKey().equals("manager") || systemRole.getRoleKey().equals("admin"))).collect(Collectors.toList());
            if (roleList == null || roleList.size() < 1) {
                throw new BaseException("对不起，您的账号：" + username + " 无权访问后台");
            }
        }
        // 生成token
        return tokenService.createToken(loginUser);
    }
}
