package cn.htht.service.platform.portal.manager.mapper;

import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.manager.ApplyTemplate;
import cn.htht.service.platform.portal.entity.manager.ServiceTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApplyTemplateMapper extends BasicMapper<ApplyTemplate> {

    ApplyTemplate getTemplateByModuleId(String moduleId);

    ApplyTemplate getAsTemplateByModuleId(String moduleId);

    void deleteById(String templateId);

    void updateRouter(@Param("router") String router, @Param("redirectModuleId") String redirectModuleId, @Param("moduleId") String moduleId);

}



