package cn.htht.service.platform.portal.manager.service.impl;

import cn.htht.service.platform.portal.common.AbstractBaseService;
import cn.htht.service.platform.portal.entity.manager.NormalTab;
import cn.htht.service.platform.portal.manager.mapper.NormalTabMapper;
import cn.htht.service.platform.portal.manager.service.ServiceNormalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ServiceNormalServiceImpl extends AbstractBaseService<NormalTab> implements ServiceNormalService {

    @Autowired
    private NormalTabMapper normalTabMapper;

    @Override
    public List<NormalTab> getAsNormalTabByTemplateId(String templateId) {
        return normalTabMapper.getAsTemplateByTemplateId(templateId);
    }
}
