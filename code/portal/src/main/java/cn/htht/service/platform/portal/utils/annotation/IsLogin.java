package cn.htht.service.platform.portal.utils.annotation;

import java.lang.annotation.*;

/**
 * 判断当前是否处于登录状态
 */
@Inherited
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IsLogin {

}
