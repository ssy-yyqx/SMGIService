package cn.htht.service.platform.portal.system.mapper;

import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.system.SystemPermissionStrKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 菜单表 数据层
 *
 * @author htht
 */
@Mapper
public interface SystemPermissionStrKeyMapper extends BasicMapper<SystemPermissionStrKey> {

    void insertBatch(@Param("list") List<SystemPermissionStrKey> list);
}
