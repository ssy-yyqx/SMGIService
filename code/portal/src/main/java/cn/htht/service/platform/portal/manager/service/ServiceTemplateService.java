package cn.htht.service.platform.portal.manager.service;

import cn.htht.service.platform.portal.common.BaseService;
import cn.htht.service.platform.portal.entity.dto.ContentDto;
import cn.htht.service.platform.portal.entity.manager.ApplyTemplate;
import cn.htht.service.platform.portal.entity.manager.ServiceTemplate;
import cn.htht.service.platform.portal.entity.manager.Template;
import cn.htht.service.platform.portal.entity.system.LoginUser;

public interface ServiceTemplateService extends BaseService<ServiceTemplate>, TemplateService {

    ServiceTemplate getByModuleId(String moduleId);

    void insertServiceTemplate(Template template, ContentDto contentDto, LoginUser loginUser);

    void updateServiceTemplate(ServiceTemplate serviceTemplate, ContentDto contentDto, LoginUser loginUser);

    ServiceTemplate getAsTemplateByModuleId(String moduleId);

}
