package cn.htht.service.platform.portal.manager.mapper;

import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.manager.ListTab;
import cn.htht.service.platform.portal.entity.manager.TabInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ListTabMapper extends BasicMapper<ListTab> {

    void insertListTab(@Param("list") List<ListTab> listTabs);

    void insertTabInfo(@Param("list") List<TabInfo> tabInfoList);

    void deleteInfoListByTemplate(@Param("templateId") String id);

    void deleteByTemplate(@Param("templateId") String id);

    List<ListTab> getAsListTabByTemplateId(String id);

}
