package cn.htht.service.platform.portal.manager.service.impl;

import cn.htht.service.platform.portal.common.AbstractBaseService;
import cn.htht.service.platform.portal.entity.dto.ContentDto;
import cn.htht.service.platform.portal.entity.manager.Content;
import cn.htht.service.platform.portal.entity.manager.InfoTemplate;
import cn.htht.service.platform.portal.entity.manager.Module;
import cn.htht.service.platform.portal.entity.manager.Template;
import cn.htht.service.platform.portal.manager.mapper.ContentMapper;
import cn.htht.service.platform.portal.manager.mapper.InfoTemplateMapper;
import cn.htht.service.platform.portal.manager.service.FileDataService;
import cn.htht.service.platform.portal.manager.service.InfoTemplateService;
import cn.htht.service.platform.portal.manager.service.ModuleService;
import cn.htht.service.platform.portal.utils.utils.StringUtils;
import cn.htht.service.platform.portal.utils.utils.uuid.IdUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName InfoTemplateServiceImpl
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Service
public class InfoTemplateServiceImpl extends AbstractBaseService<InfoTemplate> implements InfoTemplateService {

    @Autowired
    private InfoTemplateMapper infoTemplateMapper;

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private FileDataService fileDataService;

    @Override
    public void insertTemplate(Template template, Content content) {
        // 需要看该栏目下是否存在该模板，如果存在则不用插入模板，直接插入内容
        InfoTemplate infoTemplate = infoTemplateMapper.getTemplateByModuleId(template.getModuleId());
        if (infoTemplate == null) {
            template.setId(IdUtils.fastSimpleUUID());
            infoTemplate = new InfoTemplate(template);
            infoTemplateMapper.insert(infoTemplate);
        }
        content.setTemplateId(infoTemplate.getId());
        contentMapper.insert(content);
    }

    @Override
    public String updateContent(ContentDto contentDto) {
        Content content = contentMapper.selectByPrimaryKey(contentDto.getContentId());
        if (content == null) {
            return "error:修改内容不存在";
        }
        try {
            // 删除被替换的文件
            if (StringUtils.isNotBlank(content.getDataId()) && StringUtils.isNotBlank(contentDto.getDataId()) && !content.getDataId().equals(contentDto.getDataId())) {
                fileDataService.deleteData(content.getDataId());
            }
            // 删除别替换的图片
            if (StringUtils.isNotBlank(content.getImageUrl()) && StringUtils.isNotBlank(contentDto.getImageUrl())  && !content.getImageUrl().equals(contentDto.getImageUrl())) {
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
        InfoTemplate infoTemplate = infoTemplateMapper.getTemplateByContentId(contentId);
        if (infoTemplate == null || infoTemplate.getContentList() == null || infoTemplate.getContentList().size() == 0) {
            return;
        }
        if (infoTemplate.getContentList().size() == 1 && infoTemplate.getContentList().get(0).getId().equals(contentId)) {
            // 如果只剩一个则要把template也删掉
            Content content = infoTemplate.getContentList().get(0);
            if (content == null) {
                return;
            }
            contentMapper.delete(content);
            fileDataService.deleteData(content.getDataId());
            fileDataService.deleteData(content.getImageUrl().substring(content.getImageUrl().indexOf("show/") + 5));
            // 删除template
            infoTemplateMapper.delete(infoTemplate);
        } else if (infoTemplate.getContentList().size() > 1) {
            // 只删掉content
            List<Content> contents = infoTemplate.getContentList().stream().filter(content -> content.getId().equals(contentId)).collect(Collectors.toList());
            Content content = contents.get(0);
            if (content == null) {
                return;
            }
            contentMapper.delete(content);
            fileDataService.deleteData(content.getDataId());
            fileDataService.deleteData(content.getImageUrl().substring(content.getImageUrl().indexOf("show/") + 5));
        } else {
            return;
        }
    }

    @Override
    public PageInfo<Content> contentPageList(Module module, int pageSize, int pageNumber) {
        PageHelper.startPage(pageNumber, pageSize);
        List<Content> contentList = infoTemplateMapper.page(module.getId());
        return new PageInfo<>(contentList);
    }
}
