package cn.htht.service.platform.portal.manager.mapper;

import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.manager.ListTab;
import cn.htht.service.platform.portal.entity.manager.NormalTab;
import cn.htht.service.platform.portal.entity.manager.ServiceTemplate;
import cn.htht.service.platform.portal.entity.manager.TabInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ServiceTemplateMapper extends BasicMapper<ServiceTemplate> {

    ServiceTemplate getTemplateByModuleId(String moduleId);

    ServiceTemplate getAsTemplateByModuleId(String moduleId);

    void deleteById(String templateId);
}



