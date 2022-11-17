package cn.htht.service.platform.portal.component.security.handle;

import cn.htht.service.platform.portal.component.manager.AsyncManager;
import cn.htht.service.platform.portal.component.manager.factory.AsyncFactory;
import cn.htht.service.platform.portal.component.web.service.TokenService;
import cn.htht.service.platform.portal.common.bean.ResponseEntity;
import cn.htht.service.platform.portal.constant.Constants;
import cn.htht.service.platform.portal.constant.HttpStatus;
import cn.htht.service.platform.portal.entity.system.LoginUser;
import cn.htht.service.platform.portal.utils.utils.ServletUtils;
import cn.htht.service.platform.portal.utils.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义退出处理类 返回成功
 *
 * @author htht
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    @Autowired
    private TokenService tokenService;

    /**
     * 退出处理
     *
     * @return
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser)) {
            String userName = loginUser.getUsername();
            // 删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());
            // 记录用户退出日志
            AsyncManager.me().execute(AsyncFactory.recordLoginlog(userName, Constants.LOGIN_SUCCESS, "退出成功", 2));
        }
        ServletUtils.renderString(response, JSON.toJSONString(ResponseEntity.error(HttpStatus.SUCCESS, "退出成功")));
    }
}
