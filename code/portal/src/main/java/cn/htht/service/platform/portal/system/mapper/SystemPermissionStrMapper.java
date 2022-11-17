package cn.htht.service.platform.portal.system.mapper;

import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.system.SystemPermissionStr;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 菜单表 数据层
 *
 * @author htht
 */
@Component
public interface SystemPermissionStrMapper extends BasicMapper<SystemPermissionStr> {

    int insertStr(SystemPermissionStr systemPermissionStr);

    List<SystemPermissionStr> selectAllPermission();
}
