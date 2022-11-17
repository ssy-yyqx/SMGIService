package cn.htht.service.platform.portal.manager.mapper;

import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.manager.DataTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/26
 */
@Mapper
public interface DataTemplateMapper extends BasicMapper<DataTemplate> {
    /**
     * 按moduleId查询
     * */
    DataTemplate getTemplateByModuleId(@Param("moduleId") String moduleId);
}
