package cn.htht.service.platform.portal.component.aspectj;


import cn.htht.service.platform.portal.component.manager.AsyncManager;
import cn.htht.service.platform.portal.component.manager.factory.AsyncFactory;
import cn.htht.service.platform.portal.component.web.service.TokenService;
import cn.htht.service.platform.portal.utils.annotation.Log;
import cn.htht.service.platform.portal.entity.system.LoginUser;
import cn.htht.service.platform.portal.utils.enums.BusinessStatus;
import cn.htht.service.platform.portal.utils.enums.HttpMethod;
import cn.htht.service.platform.portal.utils.utils.ServletUtils;
import cn.htht.service.platform.portal.utils.utils.StringUtils;
import cn.htht.service.platform.portal.utils.utils.ip.IpUtils;
import cn.htht.service.platform.portal.utils.utils.spring.SpringUtils;
import cn.htht.service.platform.portal.entity.system.SystemLog;
import cn.htht.service.platform.portal.utils.utils.uuid.IdUtils;
import com.alibaba.fastjson.JSON;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * 操作日志记录处理
 *
 * @author htht
 */
@Aspect
@Component
public class LogAspect {
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    // 配置织入点
    @Pointcut("@annotation(cn.htht.service.platform.portal.utils.annotation.Log)")
    public void logPointCut() {
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "logPointCut()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        handleLog(joinPoint, null, jsonResult);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, final Exception e, Object jsonResult) {
        try {
            // 获得注解
            Log controllerLog = getAnnotationLog(joinPoint);
            if (controllerLog == null) {
                return;
            }

            // 获取当前的用户
            LoginUser loginUser = SpringUtils.getBean(TokenService.class).getLoginUser(ServletUtils.getRequest());

            if (loginUser == null) {
                return;
            }

            // *========数据库日志=========*//
            SystemLog systemLog = new SystemLog();
            systemLog.setId(IdUtils.fastSimpleUUID());
            systemLog.setStatus(BusinessStatus.SUCCESS.ordinal() + 1);
            // 请求的地址
            String ip = IpUtils.getIpAddress(ServletUtils.getRequest());
            systemLog.setIpAddr(ip);
            // 返回参数
            systemLog.setJsonResult(JSON.toJSONString(jsonResult));

            if (loginUser != null) {
                systemLog.setOperateUserId(loginUser.getUserId());
                systemLog.setOperateUsername(loginUser.getUsername());
                systemLog.setOperateRealName(loginUser.getUser().getRealName());
                StringBuffer id = new StringBuffer();
                StringBuffer name = new StringBuffer();
                loginUser.getUser().getRoleList().forEach(systemRole -> {
                    id.append(systemRole.getId()).append(",");
                    name.append(systemRole.getRoleName()).append(",");
                });
                systemLog.setOperateRoleId(id.substring(0, id.length() - 1));
                systemLog.setOperateRoleName(name.substring(0, name.length() - 1));
            }

            if (e != null) {
                systemLog.setStatus(BusinessStatus.FAIL.ordinal() + 1);
                systemLog.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 1000));
            }
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            systemLog.setMethod(className + "." + methodName + "()");
            // 设置请求方式
            systemLog.setRequestMethod(ServletUtils.getRequest().getMethod());
            // 处理设置注解上的参数
            getControllerMethodDescription(joinPoint, controllerLog, systemLog);
            // 保存数据库
            AsyncManager.me().execute(AsyncFactory.recordSystemLog(systemLog));
        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("==前置通知异常==");
            log.error("异常信息:{}", exp.getMessage());
            exp.printStackTrace();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param log       日志
     * @param systemLog 操作日志
     * @throws Exception
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, Log log, SystemLog systemLog) throws Exception {
        // 设置action动作
        systemLog.setOperateType(log.businessType().ordinal());
        // 设置标题
        systemLog.setModuleName(log.module());
        // 设置操作人类别
        systemLog.setOperateType(log.operatorType().ordinal());
        // 是否需要保存request，参数和值
        if (log.isSaveRequestData()) {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(joinPoint, systemLog);
        }
    }

    /**
     * 获取请求的参数，放到log中
     *
     * @param systemLog 操作日志
     * @throws Exception 异常
     */
    private void setRequestValue(JoinPoint joinPoint, SystemLog systemLog) throws Exception {
        String requestMethod = systemLog.getRequestMethod();
        if (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod)) {
            String params = argsArrayToString(joinPoint.getArgs());
            systemLog.setOperateParam(StringUtils.substring(params, 0, 1000));
        } else {
            Map<?, ?> paramsMap = (Map<?, ?>) ServletUtils.getRequest().getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            systemLog.setOperateParam(StringUtils.substring(paramsMap.toString(), 0, 1000));
        }
    }

    /**
     * 是否存在注解，如果存在就获取
     */
    private Log getAnnotationLog(JoinPoint joinPoint) throws Exception {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        if (method != null) {
            return method.getAnnotation(Log.class);
        }
        return null;
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray) {
        String params = "";
        if (paramsArray != null && paramsArray.length > 0) {
            for (int i = 0; i < paramsArray.length; i++) {
                if (!isFilterObject(paramsArray[i])) {
                    Object jsonObj = JSON.toJSON(paramsArray[i]);
                    params += jsonObj.toString() + " ";
                }
            }
        }
        return params.trim();
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Iterator iter = collection.iterator(); iter.hasNext(); ) {
                return iter.next() instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Iterator iter = map.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iter.next();
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }
}
