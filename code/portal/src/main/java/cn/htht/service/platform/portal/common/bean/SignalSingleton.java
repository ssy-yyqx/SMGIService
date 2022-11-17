package cn.htht.service.platform.portal.common.bean;

/**
 * @ClassName SignalSingleton
 * @Description 多线程标识符单例实体，用于获取序列号以及标志
 * @Author Ken
 * @Date DATE{TIME}
 **/
public class SignalSingleton {

    private SignalSingleton() {}

    private static class SingletonHelper {
        private static final SignalSingleton INSTANCE = new SignalSingleton();
    }

    public static final SignalSingleton getInstance() {
        return  SingletonHelper.INSTANCE;
    }

}
