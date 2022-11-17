package cn.htht.service.platform.portal.manager.service.impl;

import cn.htht.service.platform.portal.common.AbstractBaseService;
import cn.htht.service.platform.portal.entity.dto.ContentDto;
import cn.htht.service.platform.portal.entity.manager.ApplyTemplate;
import cn.htht.service.platform.portal.entity.manager.Content;
import cn.htht.service.platform.portal.entity.manager.Module;
import cn.htht.service.platform.portal.entity.manager.Template;
import cn.htht.service.platform.portal.entity.system.LoginUser;
import cn.htht.service.platform.portal.manager.mapper.ApplyTemplateMapper;
import cn.htht.service.platform.portal.manager.service.ApplyTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName ServiceTemplateServiceImpl
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Service
public class ApplyTemplateServiceImpl extends AbstractBaseService<ApplyTemplate> implements ApplyTemplateService {

    @Autowired
    private ApplyTemplateMapper applyTemplateMapper;


    @Override
    public void insertTemplate(Template template, Content content) {
    }

    @Override
    public String updateContent(ContentDto contentDto) {
        return null;
    }

    @Override
    public void deleteContent(String templateId) {
        applyTemplateMapper.deleteById(templateId);
    }

    @Override
    public ApplyTemplate getByModuleId(String moduleId) {
        return applyTemplateMapper.getTemplateByModuleId(moduleId);
    }

    @Override
    @Transactional
    public int updateApplyTemplate(ContentDto contentDto, Module module, LoginUser loginUser) {
        ApplyTemplate applyTemplate = applyTemplateMapper.getTemplateByModuleId(contentDto.getRedirectModuleId());
        applyTemplate.setHandleType(contentDto.getHandleType());
        applyTemplate.setGuideFileId(contentDto.getGuideFileId());
        applyTemplate.setRedirectModuleId(module.getId());
        applyTemplate.setRedirectUrl(module.getRouter());
        applyTemplate.setUpdateTime(System.currentTimeMillis());
        applyTemplate.setUpdateBy(loginUser.getUsername());
        return applyTemplateMapper.updateByPrimaryKey(applyTemplate);
    }

}
