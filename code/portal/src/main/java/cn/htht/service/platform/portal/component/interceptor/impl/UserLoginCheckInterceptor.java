package cn.htht.service.platform.portal.component.interceptor.impl;

import cn.htht.service.platform.portal.component.interceptor.LoginCheckInterceptor;
import cn.htht.service.platform.portal.component.web.service.TokenService;
import cn.htht.service.platform.portal.constant.UserConstants;
import cn.htht.service.platform.portal.entity.system.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName UserLoginCheckInterceptor
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Component
public class UserLoginCheckInterceptor extends LoginCheckInterceptor {

    @Autowired
    private TokenService tokenService;

    @Override
    public boolean isLogin(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser != null && loginUser.getUser() != null
                && loginUser.getUser().getStatus().equals(UserConstants.IS_NO)
                && loginUser.getUser().getDelFlag().equals(UserConstants.IS_NO)) {
            return true;
        } else {
            return false;
        }
    }
}
