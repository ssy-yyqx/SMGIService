package cn.htht.service.platform.portal.utils.handler;

/**
 * 对象过滤接口
 *
 * @param <T>
 */
public interface ObjectFilter<T> {
    /**
     * 实现自定义过滤方法
     *
     * @param t
     * @return
     */
    boolean filter(T t);
}
