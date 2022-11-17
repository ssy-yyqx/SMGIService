package cn.htht.service.platform.portal.manager.service.impl;

import cn.htht.service.platform.portal.common.AbstractBaseService;
import cn.htht.service.platform.portal.constant.ModuleConstant;
import cn.htht.service.platform.portal.entity.dto.ContentDto;
import cn.htht.service.platform.portal.entity.dto.PageHeaderDto;
import cn.htht.service.platform.portal.entity.manager.Module;
import cn.htht.service.platform.portal.entity.manager.*;
import cn.htht.service.platform.portal.entity.user.DocEntity;
import cn.htht.service.platform.portal.entity.vo.ContentVo;
import cn.htht.service.platform.portal.entity.vo.ServiceDataVo;
import cn.htht.service.platform.portal.manager.mapper.*;
import cn.htht.service.platform.portal.manager.service.ModuleService;
import cn.htht.service.platform.portal.utils.utils.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName ModuleServiceImpl
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Service
public class ModuleServiceImpl extends AbstractBaseService<Module> implements ModuleService {

    @Autowired
    private ModuleMapper moduleMapper;

    @Autowired
    private SingleTemplateMapper singleTemplateMapper;

    @Autowired
    private InfoTemplateMapper infoTemplateMapper;

    @Autowired
    private ServiceTemplateMapper serviceTemplateMapper;

    @Autowired
    private DataTemplateMapper dataTemplateMapper;

    @Autowired
    private ApplyTemplateMapper applyTemplateMapper;

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;

    private static Map<Integer, String> TEMPLATE_MAP = new HashMap<>();

    static {
        TEMPLATE_MAP.put(ModuleConstant.SINGLE_TEMPLATE, ModuleConstant.SINGLE_TEMPLATE_TYPE);
        TEMPLATE_MAP.put(ModuleConstant.INFO_TEMPLATE, ModuleConstant.INFO_TEMPLATE_TYPE);
        TEMPLATE_MAP.put(ModuleConstant.SERVICE_TEMPLATE, ModuleConstant.SERVICE_TEMPLATE_TYPE);
        TEMPLATE_MAP.put(ModuleConstant.DATA_TEMPLATE, ModuleConstant.DATA_TEMPLATE_TYPE);
    }


    @Override
    public List<Module> buildModuleTree(String isShow) {
        List<Module> modules = moduleMapper.getModuleList(isShow);
        List<Module> resultList = buildTree(modules, "");
        return resultList;
    }

    @Override
    public List<Integer> selectExistModule(String moduleName, String router) {
        return moduleMapper.selectExistModule(moduleName, router);
    }

    @Override
    public Integer getMaxSortInModule(String parentModuleId) {
        if (StringUtils.isNotBlank(parentModuleId)) {
            return moduleMapper.getMaxSortInParentModule(parentModuleId);
        } else {
            return moduleMapper.getMaxSortInModule();
        }

    }

    @Override
    public void deleteModule(String id) {
        // 删除栏目
        moduleMapper.deleteByPrimaryKey(id);
    }

    @Override
    public boolean haveChild(String id) {
        return moduleMapper.selectCountByParentId(id) > 0;
    }

    @Override
    @Transactional
    public void updateSort(Module module, String sort) {
        moduleMapper.updateSort(module.getId(), sort);
        if (sort.equals("up")) {
            moduleMapper.updateSortChange(module.getParentModuleId(), module.getId(), module.getSort(), module.getSort() - 1);
        }
        if (sort.equals("down")) {
            moduleMapper.updateSortChange(module.getParentModuleId(), module.getId(), module.getSort(), module.getSort() + 1);
        }
    }

    @Override
    public List<Module> getModuleList(String id) {
        return moduleMapper.getModuleListByParentId(id);
    }

    @Override
    public void updateIsShow(String id, Integer isShow) {
        moduleMapper.updateIsShow(id, isShow);
    }

    /**
     * 获取栏目下的内容
     *
     * @param moduleId
     * @return
     */
    @Override
    public Template getContent(String moduleId) {
        Module module = moduleMapper.getById(moduleId);
        if (module.getModuleType() != ModuleConstant.MODULE_CONTENT) {
            // 如果不是最终节点则没有内容
            return null;
        }
        if (module.getTemplateType() == ModuleConstant.SINGLE_TEMPLATE) {
            // 简单单页面模板
            SingleTemplate singleTemplate = singleTemplateMapper.getTemplateByModuleId(moduleId);
            return singleTemplate == null ? new SingleTemplate() : singleTemplate;
        }
        if (module.getTemplateType() == ModuleConstant.INFO_TEMPLATE) {
            InfoTemplate infoTemplate = infoTemplateMapper.getTemplateByModuleId(moduleId);
            return infoTemplate;
        }
        if (module.getTemplateType() == ModuleConstant.SERVICE_TEMPLATE) {
            ServiceTemplate serviceTemplate = serviceTemplateMapper.getTemplateByModuleId(moduleId);
            return serviceTemplate;
        }
        if (module.getTemplateType() == ModuleConstant.DATA_TEMPLATE) {
            DataTemplate dataTemplate = dataTemplateMapper.getTemplateByModuleId(moduleId);
            return dataTemplate;
        }
        return new Template();
    }

    @Override
    public List<Module> getParentModule() {
        return moduleMapper.getParentModule();
    }

    @Override
    public boolean haveTemplate(String id) {
        Template content = getContent(id);
        if (content != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public PageInfo<ContentVo> getContentListByModule(Module module, int pageSize, int pageNumber) {
        List<Module> modules = moduleMapper.getModuleList("");
        Map<String, Set<String>> templateTypeMap = new HashMap<>();
        HashSet<String> infoSet = new HashSet<>();
        HashSet<String> singleSet = new HashSet<>();
        infoSet.add("");
        singleSet.add("");
        templateTypeMap.put(ModuleConstant.SINGLE_TEMPLATE_TYPE, singleSet);
        templateTypeMap.put(ModuleConstant.INFO_TEMPLATE_TYPE, infoSet);
        getModuleTypeMap(modules, module.getId(), templateTypeMap);
        PageHelper.startPage(pageNumber, pageSize);
        List<ContentVo> contentList = contentMapper.getContentByModuleId(templateTypeMap);
        return new PageInfo<>(contentList);
    }


    private void getModuleTypeMap(List<Module> modules, String pid, Map<String, Set<String>> templateTypeMap) {
        for (Module module : modules) {
            if (module.getParentModuleId() == null || (module.getParentModuleId() != null && module.getParentModuleId().equals(pid))) {
                if (templateTypeMap.containsKey(TEMPLATE_MAP.get(module.getTemplateType()))) {
                    Set<String> set = templateTypeMap.get(TEMPLATE_MAP.get(module.getTemplateType()));
                    set.add(module.getId());
                    templateTypeMap.put(TEMPLATE_MAP.get(module.getTemplateType()), set);
                } else {
                    Set<String> moduleIds = new HashSet<>();
                    moduleIds.add(module.getId());
                    templateTypeMap.put(TEMPLATE_MAP.get(module.getTemplateType()), moduleIds);
                }
                getModuleTypeMap(modules, module.getId(), templateTypeMap);
            }
        }
    }

    @Override
    public PageInfo<ServiceDataVo> getServiceDataByModuleId(Module module, int pageSize, int pageNumber) {
        List<Module> modules = moduleMapper.getModuleList("");
        Map<String, Set<String>> templateTypeMap = new HashMap<>();
        HashSet<String> serviceSet = new HashSet<>();
        HashSet<String> dataSet = new HashSet<>();
        serviceSet.add("");
        dataSet.add("");
        templateTypeMap.put(ModuleConstant.SERVICE_TEMPLATE_TYPE, serviceSet);
        templateTypeMap.put(ModuleConstant.DATA_TEMPLATE_TYPE, dataSet);
        // TODO
        getModuleTypeMap(modules, module.getId(), templateTypeMap);
        PageHelper.startPage(pageNumber, pageSize);
        List<ServiceDataVo> serviceDataList = contentMapper.getServiceDataByModuleId(templateTypeMap);
        return new PageInfo<>(serviceDataList);
    }

    private List<Module> buildTree(List<Module> modules, String pid) {
        List<Module> resultList = new ArrayList<>();
        if (modules == null || modules.size() == 0) {
            return null;
        }
        for (Module module : modules) {
            if (module.getParentModuleId() == null || (module.getParentModuleId() != null && module.getParentModuleId().equals(pid))) {
                resultList.add(module);
                module.setChildrenList(buildTree(modules, module.getId()));
            }
        }
        return resultList;
    }

    @Override
    public Map<String, String> getFirstRate(String moduleId) {
        String parentModuleId = "";
        String parentModuleName = "";
        Map<String, String> map = new HashMap();
        Module modules = null;
        for (; ; ) {
            modules = moduleMapper.getById(moduleId);
            parentModuleId = modules.getParentModuleId();
            if (StringUtils.isBlank(parentModuleId)) {
                break;
            }
            parentModuleId = modules.getParentModuleId();
            parentModuleName = modules.getParentModuleName();
            moduleId = parentModuleId;
        }
        map.put("parentModuleId", moduleId);
        map.put("parentModuleName", parentModuleName);
        //返回最顶层id name
        return map;
    }

    /**
     * Es Set值
     * @param idType
     * @param module
     * @param content
     * @param contentDtoDto
     * @param serviceTemplateType
     * @return
     */
    @Override
    public DocEntity setDocEntity(String idType, Module module, Content content, ContentDto contentDtoDto, String serviceTemplateType) {
        Map map = getFirstRate(contentDtoDto.getModuleId());
        DocEntity docEntity = new DocEntity();
        String title = "";
        if (StringUtils.equals(serviceTemplateType, ModuleConstant.SINGLE_TEMPLATE_TYPE) || StringUtils.equals(serviceTemplateType, ModuleConstant.INFO_TEMPLATE_TYPE)) {
            title = content.getTitle();
        }
        if (StringUtils.equals(serviceTemplateType, ModuleConstant.DATA_TEMPLATE_TYPE) || StringUtils.equals(serviceTemplateType, ModuleConstant.SERVICE_TEMPLATE_TYPE) ) {
            title = module.getModuleName();
        }
        docEntity.setId(idType);
        docEntity.setTitle(title);
        docEntity.setColumnType((String) map.get("parentModuleName"));
        docEntity.setFirstRateId((String) map.get("parentModuleId"));
        docEntity.setContentId(contentDtoDto.getContentId());
        docEntity.setModuleId(contentDtoDto.getModuleId());
        docEntity.setRouter(module.getRouter());
        docEntity.setModuleName(module.getModuleName());
        docEntity.setTempleType(String.valueOf(module.getTemplateType()));
        docEntity.setCreatTime(content.getCreateTime());
        docEntity.setCreatBy(content.getCreateBy());
        docEntity.setServiceTemplateType(serviceTemplateType);
        docEntity.setSysDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return docEntity;
    }

    @Override
    public void setDocEntity(List<DocEntity> docEntityList) {
        docEntityList.stream().forEach(docEntity -> {
            Map map = getFirstRate(docEntity.getModuleId());
            docEntity.setColumnType((String) map.get("parentModuleName"));
            docEntity.setFirstRateId((String) map.get("parentModuleId"));
        });
    }

    /**
     * 更新ES库
     *
     * @param contentDto
     * @param module
     * @param content
     */
    @Override
    public void updateEs(ContentDto contentDto, Module module, Content content, String serviceType) {
        //先删除此次contentId或者templateId的相关数据
        Object obj = elasticsearchTemplate.get(contentDto.getContentId(), DocEntity.class);
        String str = ObjectUtils.toString(obj, "");
        if (StringUtils.isNotBlank(str)) {
            elasticsearchTemplate.delete(obj);
            //添加此次contentId的相关新数据数据（相当于更新update）
            String idType = "";
            if (StringUtils.equals(serviceType, ModuleConstant.SINGLE_TEMPLATE_TYPE) || StringUtils.equals(serviceType, ModuleConstant.INFO_TEMPLATE_TYPE)) {
                idType = contentDto.getContentId();
            }
            if (StringUtils.equals(serviceType, ModuleConstant.DATA_TEMPLATE_TYPE) || StringUtils.equals(serviceType, ModuleConstant.SERVICE_TEMPLATE_TYPE)) {
                idType = contentDto.getModuleId();
            }
            String serviceTemplateType = serviceType;
            DocEntity docEntity = setDocEntity(idType, module, content, contentDto, serviceTemplateType);
            IndexQuery query = new IndexQuery();
            query.setObject(docEntity);
            IndexCoordinates indexCoordinateName = elasticsearchTemplate.getIndexCoordinatesFor(DocEntity.class);
            elasticsearchTemplate.index(query, indexCoordinateName);
        }
    }


    @Override
    public PageHeaderDto getHeaderInfo(String moduleId, String userId) {
        Map<String, Long> headerMap = moduleMapper.getHeaderInfo(moduleId, userId);
        Module module = moduleMapper.selectByPrimaryKey(moduleId);
        PageHeaderDto pageHeaderDto = new PageHeaderDto();
        pageHeaderDto.setModuleId(moduleId);
        pageHeaderDto.setUserId(userId);
        pageHeaderDto.setCollectCount(headerMap.get("collect_count"));
        pageHeaderDto.setCollect(headerMap.get("is_collect") > 0);
        pageHeaderDto.setSupportCount(headerMap.get("support_count"));
        pageHeaderDto.setSupport(headerMap.get("is_support") > 0);
        pageHeaderDto.setCommentCount(headerMap.get("comment_count"));
        if (module.getTemplateType().equals(ModuleConstant.DATA_TEMPLATE)) {
            // TODO 数据信息
        }
        if (module.getTemplateType().equals(ModuleConstant.SERVICE_TEMPLATE)) {
            ServiceTemplate serviceTemplate = serviceTemplateMapper.getTemplateByModuleId(moduleId);
            pageHeaderDto.setGuideFile(serviceTemplate.getGuideFileData());
            pageHeaderDto.setRedirectUrl(serviceTemplate.getRedirectUrl());
        }
        if (module.getTemplateType().equals(ModuleConstant.APPLICATION_TEMPLATE)) {
            ApplyTemplate applyTemplate = applyTemplateMapper.getTemplateByModuleId(moduleId);
            pageHeaderDto.setGuideFile(applyTemplate.getGuideFileData());
            pageHeaderDto.setRedirectUrl(applyTemplate.getRedirectUrl());
        }
        return pageHeaderDto;
    }

    @Override
    public List<Module> getListByType(Integer type) {
        List<Module> modules = moduleMapper.getModuleApplyTemplateList(type);
        List<Module> resultList = buildTree(modules, "");
        return resultList;
    }
}
