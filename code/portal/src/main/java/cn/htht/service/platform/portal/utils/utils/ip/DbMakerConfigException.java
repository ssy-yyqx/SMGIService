package cn.htht.service.platform.portal.utils.utils.ip;

/**
 * @ClassName DbMakerConfigException
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
public class DbMakerConfigException extends Exception {
    private static final long serialVersionUID = 4495714680349884838L;

    public DbMakerConfigException(String info) {
        super(info);
    }

    public DbMakerConfigException(Throwable res) {
        super(res);
    }

    public DbMakerConfigException(String info, Throwable res) {
        super(info, res);
    }
}