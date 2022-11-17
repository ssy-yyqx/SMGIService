package cn.htht.service.platform.portal.manager.service;

import cn.htht.service.platform.portal.common.BaseService;
import cn.htht.service.platform.portal.entity.manager.NormalTab;

import java.util.List;

public interface ServiceNormalService extends BaseService<NormalTab>{

    List<NormalTab> getAsNormalTabByTemplateId(String id);
}
