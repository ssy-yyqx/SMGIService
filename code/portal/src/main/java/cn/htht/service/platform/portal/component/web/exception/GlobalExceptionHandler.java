package cn.htht.service.platform.portal.component.web.exception;


import cn.htht.service.platform.portal.common.bean.ResponseEntity;
import cn.htht.service.platform.portal.constant.HttpStatus;
import cn.htht.service.platform.portal.utils.exception.BaseException;
import cn.htht.service.platform.portal.utils.exception.CustomException;
import cn.htht.service.platform.portal.utils.exception.DemoModeException;
import cn.htht.service.platform.portal.utils.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Objects;

/**
 * 全局异常处理器
 *
 * @author htht
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 基础异常
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity baseException(BaseException e) {
        return ResponseEntity.error(e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity businessException(CustomException e) {
        if (StringUtils.isNull(e.getCode())) {
            return ResponseEntity.error(e.getMessage());
        }
        return ResponseEntity.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity handlerNoFoundException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.error(HttpStatus.NOT_FOUND, "路径不存在，请检查路径是否正确");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity handleAuthorizationException(AccessDeniedException e) {
        log.error(e.getMessage());
        return ResponseEntity.error(HttpStatus.FORBIDDEN, "没有权限，请联系管理员授权");
    }

    @ExceptionHandler(AccountExpiredException.class)
    public ResponseEntity handleAccountExpiredException(AccountExpiredException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.error(e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.error(e.getMessage());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity validatedBindException(BindException e) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.error(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object validExceptionHandler(MethodArgumentNotValidException e) {
//        log.error(e.getMessage(), e);
        String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        return ResponseEntity.error(message);
    }

    /**
     * 演示模式异常
     */
    @ExceptionHandler(DemoModeException.class)
    public ResponseEntity demoModeException(DemoModeException e) {
        return ResponseEntity.error("演示模式，不允许操作");
    }
}
