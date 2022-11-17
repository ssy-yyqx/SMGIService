package cn.htht.service.platform.portal.manager.service.impl;

import cn.htht.service.platform.portal.common.AbstractBaseService;
import cn.htht.service.platform.portal.entity.dto.ContentDto;
import cn.htht.service.platform.portal.entity.manager.DataTemplate;
import cn.htht.service.platform.portal.entity.manager.ServiceData;
import cn.htht.service.platform.portal.manager.mapper.DataTemplateMapper;
import cn.htht.service.platform.portal.manager.mapper.ServiceDataMapper;
import cn.htht.service.platform.portal.entity.manager.Template;
import cn.htht.service.platform.portal.manager.service.DataTemplateService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName DataTemplateServiceImpl
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Service
public class DataTemplateServiceImpl extends AbstractBaseService<DataTemplate> implements DataTemplateService {
    @Autowired
    private DataTemplateMapper dataTemplateMapper;

    @Autowired
    private ServiceDataMapper serviceDataMapper;
    /**
     * 查看数据业务内容
     *
     * @param moduleId
     */
    @Override
    public DataTemplate getDataTemplateByModuleId(String moduleId) {
        return dataTemplateMapper.getTemplateByModuleId(moduleId);
    }

    /**
     * 查看数据业务数据列表
     *
     * @param templateId
     * @param pageSize
     * @param pageNumber
     */
    @Override
    public PageInfo<ServiceData> getServiceDataListByTemplateId(String templateId, int pageSize, int pageNumber) {
        PageHelper.startPage(pageNumber, pageSize);
        List<ServiceData> serviceDataList = serviceDataMapper.selectServiceDataByTemplateId(templateId);
        return new PageInfo<>(serviceDataList);
    }

    @Override
    public void insertDataTemplate(Template template, ContentDto contentDto) {
        DataTemplate dataTemplate = new DataTemplate(template);
        dataTemplate.setId(contentDto.getContentId());
        dataTemplate.setTitle(contentDto.getTitle());
        dataTemplate.setImageUrl(contentDto.getImageUrl());
        dataTemplate.setContent(contentDto.getContent());
        dataTemplate.setHandleType(contentDto.getHandleType());
        dataTemplate.setGuideFileId(contentDto.getGuideFileId());
        dataTemplate.setRedirectUrl(contentDto.getRedirectUrl());
        dataTemplate.setRedirectModuleId(contentDto.getRedirectModuleId());
        dataTemplateMapper.insert(dataTemplate);
    }
}
