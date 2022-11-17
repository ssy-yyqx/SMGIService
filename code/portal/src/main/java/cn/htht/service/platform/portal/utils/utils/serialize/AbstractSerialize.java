package cn.htht.service.platform.portal.utils.utils.serialize;

/**
 * @ClassName AbstractSerialize
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
public abstract class AbstractSerialize {

    public abstract <T> byte[] serialize(T obj);

    public abstract <T> T deserialize(byte[] data, Class<T> clazz);

}