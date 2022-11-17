package cn.htht.service.platform.portal.utils.exception.user;

/**
 * 验证码错误异常类
 *
 * @author htht
 */
public class CaptchaException extends UserException {
    private static final long serialVersionUID = 1L;

    public CaptchaException() {
        super("user.jcaptcha.error", null);
    }
}
