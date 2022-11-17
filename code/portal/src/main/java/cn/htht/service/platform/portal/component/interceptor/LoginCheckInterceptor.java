package cn.htht.service.platform.portal.component.interceptor;

import cn.htht.service.platform.portal.common.bean.ResponseEntity;
import cn.htht.service.platform.portal.utils.annotation.IsLogin;
import cn.htht.service.platform.portal.utils.utils.ServletUtils;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @ClassName LoginCheckInterceptor
 * @Description 用户是否登录拦截器
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Component
public abstract class LoginCheckInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            IsLogin annotation = method.getAnnotation(IsLogin.class);
            if (annotation != null) {
                if (!this.isLogin(request)) {
                    ResponseEntity responseEntity = ResponseEntity.error(401, "用户未登录，请登录！");
                    ServletUtils.renderString(response, JSONObject.toJSONString(responseEntity));
                    return false;
                }
            }
            return true;
        } else {
            return super.preHandle(request, response, handler);
        }
    }

    /**
     * 验证用户是否登录
     *
     * @param request
     * @return
     */
    public abstract boolean isLogin(HttpServletRequest request);

}
