package cn.netty.server.utils;

import java.util.*;

/**
 * @ClassName MapUtils
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
public class MapUtils<K, V> {

    public void setMapList(Map<K, List<V>> map, K k, V v) {
        if (map.containsKey(k)) {
            map.get(k).add(v);
        } else {
            map.put(k, Arrays.asList(v));
        }
    }
}
