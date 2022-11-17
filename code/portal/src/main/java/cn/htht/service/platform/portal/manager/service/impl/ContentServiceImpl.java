package cn.htht.service.platform.portal.manager.service.impl;

import cn.htht.service.platform.portal.common.AbstractBaseService;
import cn.htht.service.platform.portal.entity.dto.ContentDto;
import cn.htht.service.platform.portal.entity.manager.Content;
import cn.htht.service.platform.portal.entity.user.DocEntity;
import cn.htht.service.platform.portal.manager.mapper.ContentMapper;
import cn.htht.service.platform.portal.manager.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/18
 */
@Service
public class ContentServiceImpl extends AbstractBaseService<Content> implements ContentService {
    @Autowired
    private ContentMapper contentMapper;
    /**
     * 根据ID获取详情
     *
     * @param contentId
     */
    @Override
    public Content getContentDetails(String contentId) {
        return contentMapper.selectContentById(contentId);
    }

    @Override
    public List<DocEntity> selectAllContent() {
        return contentMapper.selectAllContent();
    }
}
