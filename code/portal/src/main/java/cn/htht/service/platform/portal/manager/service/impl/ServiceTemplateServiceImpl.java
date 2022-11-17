package cn.htht.service.platform.portal.manager.service.impl;

import cn.htht.service.platform.portal.common.AbstractBaseService;
import cn.htht.service.platform.portal.entity.dto.ContentDto;
import cn.htht.service.platform.portal.entity.manager.*;
import cn.htht.service.platform.portal.entity.system.LoginUser;
import cn.htht.service.platform.portal.manager.mapper.ApplyTemplateMapper;
import cn.htht.service.platform.portal.manager.mapper.ListTabMapper;
import cn.htht.service.platform.portal.manager.mapper.NormalTabMapper;
import cn.htht.service.platform.portal.manager.mapper.ServiceTemplateMapper;
import cn.htht.service.platform.portal.manager.service.ServiceTemplateService;
import cn.htht.service.platform.portal.utils.exception.CustomException;
import cn.htht.service.platform.portal.utils.utils.uuid.IdUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ServiceTemplateServiceImpl
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Service
public class ServiceTemplateServiceImpl extends AbstractBaseService<ServiceTemplate> implements ServiceTemplateService {

    @Autowired
    private ServiceTemplateMapper serviceTemplateMapper;

    @Autowired
    private ListTabMapper listTabMapper;

    @Autowired
    private NormalTabMapper normalTabMapper;


    @Override
    public void insertTemplate(Template template, Content content) {
    }

    @Override
    public String updateContent(ContentDto contentDto) {
        return null;
    }

    @Override
    public void deleteContent(String templateId) {
        serviceTemplateMapper.deleteById(templateId);
        normalTabMapper.deleteByTemplate(templateId);
        listTabMapper.deleteInfoListByTemplate(templateId);
        listTabMapper.deleteByTemplate(templateId);
    }

    @Override
    public ServiceTemplate getByModuleId(String moduleId) {
        return serviceTemplateMapper.getTemplateByModuleId(moduleId);
    }

    @Override
    @Transactional
    public void insertServiceTemplate(Template template, ContentDto contentDto, LoginUser loginUser) {
        ServiceTemplate serviceTemplate = new ServiceTemplate(template);
        serviceTemplate.setServiceName(contentDto.getTitle());
        serviceTemplate.setImageUrl(contentDto.getImageUrl());
        serviceTemplate.setHandleType(contentDto.getHandleType());
        serviceTemplate.setRedirectUrl(contentDto.getRedirectUrl());
        serviceTemplate.setRedirectModuleId(contentDto.getRedirectModuleId());
        serviceTemplate.setGuideFileId(contentDto.getGuideFileId());
        serviceTemplate.setCreateTime(System.currentTimeMillis());
        serviceTemplate.setCreateBy(loginUser.getUsername());
        serviceTemplateMapper.insert(serviceTemplate);
        JSONArray tabContent = contentDto.getTabContent();
        insertOrUpdateTab(tabContent, serviceTemplate, false);

    }

    private void insertOrUpdateTab(JSONArray tabContent, ServiceTemplate serviceTemplate, boolean isUpdate) {
        if (isUpdate) {
            // 修改先清空数据，在进行插入
            normalTabMapper.deleteByTemplate(serviceTemplate.getId());
            listTabMapper.deleteInfoListByTemplate(serviceTemplate.getId());
            listTabMapper.deleteByTemplate(serviceTemplate.getId());
        }

        List<NormalTab> normalTabs = new ArrayList<>();
        List<ListTab> listTabs = new ArrayList<>();
        List<TabInfo> tabInfoList = new ArrayList<>();
        if (tabContent == null) {
            return;
        }
        if (tabContent.size() > 6) {
            throw new CustomException("页签个数过多，最多为6个");
        }
        for (int i = 0; i < tabContent.size(); i++) {
            JSONObject jsonObject = tabContent.getJSONObject(i);
            if (jsonObject.getInteger("tabType") == 1) {
                NormalTab normalTab = new NormalTab();
                normalTab.setId(IdUtils.fastSimpleUUID());
                normalTab.setServiceTemplateId(serviceTemplate.getId());
                normalTab.setContentType(jsonObject.getInteger("contentType"));
                normalTab.setTabName(jsonObject.getString("tabName"));
                normalTab.setContent(jsonObject.getString("content"));
                normalTab.setTabType(1);
                normalTab.setSort(i+1);
                normalTabs.add(normalTab);
            }
            if (jsonObject.getInteger("tabType") == 2) {
                ListTab listTab = new ListTab();
                listTab.setId(IdUtils.fastSimpleUUID());
                listTab.setServiceTemplateId(serviceTemplate.getId());
                listTab.setContentType(jsonObject.getInteger("contentType"));
                listTab.setTabName(jsonObject.getString("tabName"));
                listTab.setTabType(2);
                listTab.setSort(i+1);
                listTabs.add(listTab);
                JSONArray tableInfoList = jsonObject.getJSONArray("tableInfoList");
                if (tableInfoList == null) {
                    continue;
                }
                for (int j = 0; j < tableInfoList.size(); j++) {
                    JSONObject jo = tableInfoList.getJSONObject(j);
                    TabInfo tabInfo = new TabInfo();
                    tabInfo.setId(IdUtils.fastSimpleUUID());
                    tabInfo.setTabId(listTab.getId());
                    tabInfo.setTitleName(jo.getString("titleName"));
                    tabInfo.setInfoContent(jo.getString("infoContent"));
                    tabInfoList.add(tabInfo);
                }
            }
        }
        if (listTabs != null && listTabs.size() > 0) {
            listTabMapper.insertListTab(listTabs);
        }
        if (normalTabs != null && normalTabs.size() > 0) {
            normalTabMapper.insertNormalTab(normalTabs);
        }
        if (tabInfoList != null && tabInfoList.size() > 0) {
            listTabMapper.insertTabInfo(tabInfoList);
        }
    }

    @Override
    public void updateServiceTemplate(ServiceTemplate serviceTemplate, ContentDto contentDto, LoginUser loginUser) {
        serviceTemplate.setServiceName(contentDto.getTitle());
        serviceTemplate.setImageUrl(contentDto.getImageUrl());
        serviceTemplate.setHandleType(contentDto.getHandleType());
        serviceTemplate.setRedirectUrl(contentDto.getRedirectUrl());
        serviceTemplate.setRedirectModuleId(contentDto.getRedirectModuleId());
        serviceTemplate.setGuideFileId(contentDto.getGuideFileId());
        serviceTemplate.setUpdateBy(loginUser.getUsername());
        serviceTemplate.setUpdateTime(System.currentTimeMillis());
        serviceTemplateMapper.updateByPrimaryKey(serviceTemplate);
        JSONArray tabContent = contentDto.getTabContent();
        insertOrUpdateTab(tabContent, serviceTemplate, true);
    }

    @Override
    public ServiceTemplate getAsTemplateByModuleId(String moduleId) {
        return serviceTemplateMapper.getAsTemplateByModuleId(moduleId);
    }
}
