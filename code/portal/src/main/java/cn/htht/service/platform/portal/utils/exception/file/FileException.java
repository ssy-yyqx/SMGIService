package cn.htht.service.platform.portal.utils.exception.file;

import cn.htht.service.platform.portal.utils.exception.BaseException;

/**
 * 文件信息异常类
 *
 * @author htht
 */
public class FileException extends BaseException {
    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args) {
        super("file", code, args, null);
    }

}
