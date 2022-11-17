package cn.htht.service.platform.portal.manager.mapper;

import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.manager.NormalTab;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

import java.util.List;


@Mapper
public interface NormalTabMapper extends BasicMapper<NormalTab> {

    void insertNormalTab(@Param("list") List<NormalTab> normalTabs);

    void deleteByTemplate(@Param("templateId") String id);

    List<NormalTab> getAsTemplateByTemplateId(String id);
}
