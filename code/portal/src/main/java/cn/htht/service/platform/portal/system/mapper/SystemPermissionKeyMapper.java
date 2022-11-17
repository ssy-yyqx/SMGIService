package cn.htht.service.platform.portal.system.mapper;

import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.system.SystemPermissionKey;
import org.apache.ibatis.annotations.Mapper;


/**
 * 菜单表 数据层
 *
 * @author htht
 */
@Mapper
public interface SystemPermissionKeyMapper extends BasicMapper<SystemPermissionKey> {

    int insertKeys(SystemPermissionKey systemPermissionKey);
}
