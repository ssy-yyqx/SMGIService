package cn.htht.service.platform.portal.manager.service.impl;

import cn.htht.service.platform.portal.common.AbstractBaseService;
import cn.htht.service.platform.portal.entity.dto.ContentDto;
import cn.htht.service.platform.portal.entity.manager.Content;
import cn.htht.service.platform.portal.entity.manager.FileData;
import cn.htht.service.platform.portal.entity.manager.SingleTemplate;
import cn.htht.service.platform.portal.entity.manager.Template;
import cn.htht.service.platform.portal.manager.mapper.ContentMapper;
import cn.htht.service.platform.portal.manager.mapper.SingleTemplateMapper;
import cn.htht.service.platform.portal.manager.service.FileDataService;
import cn.htht.service.platform.portal.manager.service.SingleTemplateService;
import cn.htht.service.platform.portal.utils.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName SingleTemplateServiceImpl
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Service
public class SingleTemplateServiceImpl extends AbstractBaseService<SingleTemplate> implements SingleTemplateService {

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private FileDataService fileDataService;

    @Autowired
    private SingleTemplateMapper singleTemplateMapper;

    @Override
    public void insertTemplate(Template template, Content content) {
        SingleTemplate singleTemplate = new SingleTemplate(template);
        singleTemplateMapper.insert(singleTemplate);
        content.setTemplateId(singleTemplate.getId());
        contentMapper.insert(content);
    }

    @Override
    public SingleTemplate getByModuleId(String moduleId) {
        return singleTemplateMapper.getTemplateByModuleId(moduleId);
    }

    @Override
    public String updateContent(ContentDto contentDto) {
        Content content = contentMapper.selectByPrimaryKey(contentDto.getContentId());
        if (content == null) {
            return "error:修改内容不存在";
        }
        try {
            // 删除被替换的文件
            if (!StringUtils.emptyIfNull(content.getDataId()).equals(StringUtils.emptyIfNull(contentDto.getDataId()))) {
                fileDataService.deleteData(content.getDataId());
            }
            // 删除别替换的图片
            if (!StringUtils.emptyIfNull(content.getImageUrl()).equals(StringUtils.emptyIfNull(contentDto.getDataId()))) {
                fileDataService.deleteData(content.getImageUrl().substring(content.getImageUrl().indexOf("show/") + 5));
            }
            contentMapper.updateContent(contentDto);
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
        return "success";
    }

    @Override
    public void deleteContent(String contentId) {
        Content content = contentMapper.selectByPrimaryKey(contentId);
        if (content == null) {
            return;
        }
        fileDataService.deleteData(content.getDataId());
        fileDataService.deleteData(content.getImageUrl().substring(content.getImageUrl().indexOf("show/") + 5));
        contentMapper.delete(content);
    }
}
