package cn.htht.service.platform.portal.component.config;

import cn.htht.service.platform.portal.component.interceptor.impl.UserLoginCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/22
 */
@Configuration
@Component
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    UserLoginCheckInterceptor userLoginCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userLoginCheckInterceptor).addPathPatterns("/**").excludePathPatterns("/*/login");
    }
}
