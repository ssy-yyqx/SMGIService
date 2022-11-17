package cn.htht.service.platform.portal.utils.exception.user;

import cn.htht.service.platform.portal.utils.exception.BaseException;

/**
 * 用户信息异常类
 *
 * @author htht
 */
public class UserException extends BaseException {
    private static final long serialVersionUID = 1L;

    public UserException(String code, Object[] args) {
        super("user", code, args, null);
    }
}
