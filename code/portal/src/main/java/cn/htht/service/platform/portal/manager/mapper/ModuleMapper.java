package cn.htht.service.platform.portal.manager.mapper;

import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.manager.Content;
import cn.htht.service.platform.portal.entity.manager.Module;
import cn.htht.service.platform.portal.entity.vo.ContentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface ModuleMapper extends BasicMapper<Module> {

    List<Module> getModuleList(@Param("isShow") String isShow);

    List<Integer> selectExistModule(@Param("moduleName") String moduleName, @Param("router") String router);

    Integer getMaxSortInParentModule(@Param("parentModuleId") String parentModuleId);

    Integer getMaxSortInModule();

    Integer selectCountByParentId(@Param("id") String id);

    void updateSort(@Param("id") String id, @Param("sort") String sort);

    List<Module> getModuleListByParentId(@Param("id") String id);

    void updateIsShow(@Param("id") String id, @Param("isShow") Integer isShow);

    Module getById(@Param("moduleId") String moduleId);

    void updateSortChange(@Param("parentModuleId") String parentModuleId, @Param("moduleId")String moduleId, @Param("oldSort") Integer oldSort, @Param("newSort") Integer newSort);

    List<Module> getParentModule();

    Map<String, Long> getHeaderInfo(@Param("moduleId") String moduleId, @Param("userId") String userId);

    List<Module> getModuleApplyTemplateList(@Param("type") Integer type);
}
