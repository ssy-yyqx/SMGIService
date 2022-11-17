package cn.htht.service.platform.portal.manager.service;

import cn.htht.service.platform.portal.common.BaseService;
import cn.htht.service.platform.portal.entity.dto.ContentDto;
import cn.htht.service.platform.portal.entity.manager.Content;
import cn.htht.service.platform.portal.entity.user.DocEntity;

import java.util.List;


/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/18
 */
public interface ContentService extends BaseService<Content> {
    /**
     * 根据ID获取详情
     * */
    Content getContentDetails(String contentId);

    /**
     * 获取所有内容
     * @return
     */
    List<DocEntity> selectAllContent();
}
