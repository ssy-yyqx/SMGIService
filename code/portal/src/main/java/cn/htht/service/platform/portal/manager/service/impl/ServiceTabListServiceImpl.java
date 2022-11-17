package cn.htht.service.platform.portal.manager.service.impl;

import cn.htht.service.platform.portal.common.AbstractBaseService;
import cn.htht.service.platform.portal.entity.manager.ListTab;
import cn.htht.service.platform.portal.manager.mapper.ListTabMapper;
import cn.htht.service.platform.portal.manager.service.ServiceTabListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceTabListServiceImpl extends AbstractBaseService<ListTab> implements ServiceTabListService {

    @Autowired
    private ListTabMapper listTabMapper;
    @Override
    public List<ListTab> getAsListTabByTemplateId(String templateId) {
        return listTabMapper.getAsListTabByTemplateId(templateId);
    }
}
