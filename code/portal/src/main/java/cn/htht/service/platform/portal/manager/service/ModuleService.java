package cn.htht.service.platform.portal.manager.service;

import cn.htht.service.platform.portal.common.BaseService;
import cn.htht.service.platform.portal.entity.dto.ContentDto;
import cn.htht.service.platform.portal.entity.dto.PageHeaderDto;
import cn.htht.service.platform.portal.entity.manager.Content;
import cn.htht.service.platform.portal.entity.manager.Module;
import cn.htht.service.platform.portal.entity.manager.Template;
import cn.htht.service.platform.portal.entity.user.DocEntity;
import cn.htht.service.platform.portal.entity.vo.ContentVo;
import cn.htht.service.platform.portal.entity.vo.ServiceDataVo;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * @ClassName ModuleService
 * @Description 栏目管理模块
 * @Author Ken
 * @Date DATE{TIME}
 **/
public interface ModuleService extends BaseService<Module> {

    /**
     * 构建栏目树
     *
     * @return
     */
    List<Module> buildModuleTree(String isShow);


    /**
     * 查询是否存在相同的栏目名称或者路由
     *
     * @param moduleName 栏目名称
     * @param router     路由
     * @return List[int, int] 0表示不存在，>0 表示存在，第一个表示栏目名称存在个数， 第二个表示路由存在个数
     */
    List<Integer> selectExistModule(String moduleName, String router);

    /**
     * 获取上级目录下的最大排序
     *
     * @param parentModuleId
     * @return
     */
    Integer getMaxSortInModule(String parentModuleId);

    /**
     * 删除栏目
     *
     * @param id
     */
    void deleteModule(String id);

    /**
     * 是否有子节点
     *
     * @param id
     * @return
     */
    boolean haveChild(String id);

    /**
     * 修改栏目的排序
     *
     * @param module
     * @param sort
     */
    void updateSort(Module module, String sort);

    /**
     * 获取栏目列表
     *
     * @param id 栏目的上级id
     * @return
     */
    List<Module> getModuleList(String id);

    /**
     * 修改栏目显示状态
     *
     * @param id
     * @param isShow
     */
    void updateIsShow(String id, Integer isShow);

    /**
     * 获取栏目下的内容
     *
     * @param moduleId
     * @return
     */
    Template getContent(String moduleId);

    /**
     * 获取最上级的栏目
     *
     * @return
     */
    List<Module> getParentModule();

    /**
     * 判断是否有内容
     *
     * @param id 模板id
     * @return
     */
    boolean haveTemplate(String id);

    /**
     * 根据栏目获取其下层的所有内容
     *
     * @param module
     * @return
     */
    PageInfo<ContentVo> getContentListByModule(Module module, int pageSize, int pageNumber);


    PageInfo<ServiceDataVo> getServiceDataByModuleId(Module module, int pageSize, int pageNumber);


    /**
     * 获取最上级id
     *
     * @param moduleId
     * @return
     */
    Map<String, String> getFirstRate(String moduleId);

    /**
     * 获取页面内部头信息
     *
     * @param moduleId 栏目id
     * @param userId   用户id
     * @return
     */
    PageHeaderDto getHeaderInfo(String moduleId, String userId);

    List<Module> getListByType(Integer type);

    DocEntity setDocEntity(String idType, Module module, Content content, ContentDto contentDtoDto, String serviceTemplateType);

    void setDocEntity(List<DocEntity> docEntityList);

    void updateEs(ContentDto contentDto, Module module, Content content, String serviceType);
}
