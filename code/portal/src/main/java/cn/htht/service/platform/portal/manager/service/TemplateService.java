package cn.htht.service.platform.portal.manager.service;

import cn.htht.service.platform.portal.entity.dto.ContentDto;
import cn.htht.service.platform.portal.entity.manager.Content;
import cn.htht.service.platform.portal.entity.manager.Template;

public interface TemplateService {

    /**
     * 添加模板
     * @param template 模板
     * @param content 内容
     * @return
     */
    void insertTemplate(Template template, Content content);

    /**
     * 修改模板内容
     * @param contentDto
     * @return
     */
    String updateContent(ContentDto contentDto);

    /**
     * 删除模板内容
     * @param contentId
     */
    void deleteContent(String contentId);

}
