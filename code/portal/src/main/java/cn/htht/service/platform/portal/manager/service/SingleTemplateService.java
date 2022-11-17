package cn.htht.service.platform.portal.manager.service;

import cn.htht.service.platform.portal.common.BaseService;
import cn.htht.service.platform.portal.entity.dto.ContentDto;
import cn.htht.service.platform.portal.entity.manager.SingleTemplate;

public interface SingleTemplateService extends BaseService<SingleTemplate>, TemplateService{

    SingleTemplate getByModuleId(String moduleId);

}
