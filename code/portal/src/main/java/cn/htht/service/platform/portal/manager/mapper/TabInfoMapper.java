package cn.htht.service.platform.portal.manager.mapper;

import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.manager.TabInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TabInfoMapper extends BasicMapper<TabInfo> {

    List<TabInfo> getAsTabInfoByTemplateId(String id2);
}
