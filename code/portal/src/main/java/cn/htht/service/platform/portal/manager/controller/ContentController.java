package cn.htht.service.platform.portal.manager.controller;

import cn.htht.service.platform.portal.common.BaseController;
import cn.htht.service.platform.portal.common.bean.ResponseEntity;
import cn.htht.service.platform.portal.component.web.service.TokenService;
import cn.htht.service.platform.portal.constant.ModuleConstant;
import cn.htht.service.platform.portal.entity.dto.ContentDto;
import cn.htht.service.platform.portal.entity.manager.Module;
import cn.htht.service.platform.portal.entity.manager.*;
import cn.htht.service.platform.portal.entity.system.LoginUser;
import cn.htht.service.platform.portal.entity.system.SystemUser;
import cn.htht.service.platform.portal.entity.user.DocEntity;
import cn.htht.service.platform.portal.manager.service.*;
import cn.htht.service.platform.portal.utils.annotation.IsLogin;
import cn.htht.service.platform.portal.utils.annotation.Log;
import cn.htht.service.platform.portal.utils.enums.BusinessType;
import cn.htht.service.platform.portal.utils.utils.ServletUtils;
import cn.htht.service.platform.portal.utils.utils.StringUtils;
import cn.htht.service.platform.portal.utils.utils.file.FileMD5Utils;
import cn.htht.service.platform.portal.utils.utils.file.FileUtils;
import cn.htht.service.platform.portal.utils.utils.uuid.IdUtils;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @ClassName ContentController
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Api(tags = "内容管理")
@RequestMapping("/content")
@RestController
@Slf4j
public class ContentController extends BaseController {


    @Value("${file.show.url.pic}")
    private String accessUrl;

    @Value("${file.show.url.file}")
    private String downloadUrl;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private InfoTemplateService infoTemplateService;

    @Autowired
    private SingleTemplateService singleTemplateService;

    @Autowired
    private ServiceTemplateService serviceTemplateService;

    @Autowired
    private ApplyTemplateService applyTemplateService;

    @Autowired
    private ServiceNormalService serviceNormalService;

    @Autowired
    private ServiceTabListService serviceTabListService;

    @Autowired
    private ServiceTabInfoService serviceTabInfoService;

    @Autowired
    private DataTemplateService dataTemplateService;

    @Autowired
    private FileDataService fileDataService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;

    private ResponseEntity validateModule(Module module) {
        if (module == null) {
            return ResponseEntity.error("添加该栏目不存在");
        }
        if (module.getModuleType() == ModuleConstant.MODULE_MENU) {
            return ResponseEntity.error("该栏目为分类节点，不能添加内容");
        }
        if (module.getModuleType() == ModuleConstant.HOME_TEMPLATE) {
            return ResponseEntity.error("首页不需要添加内容");
        }
        return null;
    }

    private Template createTemplate(ContentDto contentDto) {
        Template template = new Template();
        template.setId(IdUtils.fastSimpleUUID());
        template.setModuleId(contentDto.getModuleId());
        if (StringUtils.isBlank(contentDto.getSearchKey())) {
            template.setSearchKey(contentDto.getTitle());
        } else {
            template.setSearchKey(contentDto.getSearchKey());
        }
        return template;
    }

    private Content createContent(ContentDto contentDto, String username, String idUtils) {
        Content content = new Content();
        content.setId(idUtils);
        content.setCreateBy(username);
        content.setCreateTime(System.currentTimeMillis());
        content.setTitle(contentDto.getTitle());
        content.setContent(contentDto.getContent());
        content.setImageUrl(contentDto.getImageUrl());
        content.setDataId(contentDto.getDataId());
        return content;
    }

    @ApiOperation("单页面内容添加")
    @PutMapping("/add/single")
    @IsLogin
    @Log(module = "内容管理", businessType = BusinessType.INSERT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "栏目id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "title", value = "页面标题", required = true, paramType = "query"),
            @ApiImplicitParam(name = "content", value = "页面内容", paramType = "query"),
            @ApiImplicitParam(name = "searchKey", value = "搜索关键字", paramType = "query"),
            @ApiImplicitParam(name = "imageUrl", value = "页面图片", paramType = "query"),
            @ApiImplicitParam(name = "dataId", value = "文件id", paramType = "query")
    })
    public ResponseEntity addSingle(@ApiIgnore @RequestBody ContentDto contentDtoDto) {
        Module module = moduleService.selectById(contentDtoDto.getModuleId());
        ResponseEntity errorResponse = validateModule(module);
        if (errorResponse != null) {
            return errorResponse;
        }
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        String id = IdUtils.fastSimpleUUID();
        Content content = createContent(contentDtoDto, loginUser.getUsername(), id);
        Template template = createTemplate(contentDtoDto);
        if (StringUtils.isBlank(contentDtoDto.getTitle())) {
            return ResponseEntity.error("标题不能为空");
        }
        if (StringUtils.isBlank(contentDtoDto.getContent())) {
            return ResponseEntity.error("内容不能为空");
        }
        SingleTemplate singleTemplate = singleTemplateService.getByModuleId(contentDtoDto.getModuleId());
        if (singleTemplate != null) {
            return ResponseEntity.error("该栏目类型为单页面，已存在内容，无法新增");
        }
        singleTemplateService.insertTemplate(template, content);
        String serviceTemplateType = ModuleConstant.SINGLE_TEMPLATE_TYPE;
        DocEntity docEntity = moduleService.setDocEntity(id, module, content, contentDtoDto, serviceTemplateType);
        try {
            elasticsearchTemplate.save(docEntity);
        } catch (Exception e) {
            log.info("单页面内容添加ES失败 ：{}", contentDtoDto.getContentId());
            e.printStackTrace();
        }

        return ResponseEntity.success("添加成功");
    }

    @ApiOperation("数据页面内容添加")
    @PutMapping("/add/data")
    @Log(module = "内容管理", businessType = BusinessType.INSERT)
    @IsLogin
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "栏目id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "title", value = "页面标题", paramType = "query"),
            @ApiImplicitParam(name = "content", value = "页面内容", paramType = "query"),
            @ApiImplicitParam(name = "searchKey", value = "搜索关键字", paramType = "query"),
            @ApiImplicitParam(name = "imageUrl", value = "页面图片", paramType = "query"),
            @ApiImplicitParam(name = "handleType", value = "办理类型 1、在线办理 2、预约线下", paramType = "query"),
            @ApiImplicitParam(name = "redirectModuleId", value = "跳转栏目id", paramType = "query"),
            @ApiImplicitParam(name = "redirectUrl", value = "跳转栏目url", paramType = "query"),
            @ApiImplicitParam(name = "guideFileUrl", value = "办理指南文件下载路径", paramType = "query")
    })
    public ResponseEntity addData(@ApiIgnore @RequestBody ContentDto contentDto) {
        Module module = moduleService.selectById(contentDto.getModuleId());
        ResponseEntity errorResponse = validateModule(module);
        if (errorResponse != null) {
            return errorResponse;
        }
        String idUtils = IdUtils.fastSimpleUUID();
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        Content content = createContent(contentDto, loginUser.getUsername(), idUtils);
        Template template = createTemplate(contentDto);
        contentDto.setContentId(idUtils);
        dataTemplateService.insertDataTemplate(template, contentDto);

        String idType = contentDto.getModuleId();
        String serviceTemplateType = ModuleConstant.DATA_TEMPLATE_TYPE;
        DocEntity docEntity = moduleService.setDocEntity(idType, module, content, contentDto, serviceTemplateType);
        try {
            elasticsearchTemplate.save(docEntity);
        } catch (Exception e) {
            log.info("数据页面内容添加ES失败 ：{}", contentDto.getModuleId());
            e.printStackTrace();
        }

        return ResponseEntity.success("添加成功");
    }


    @ApiOperation("业务内容添加")
    @PutMapping("/add/service")
    @Log(module = "内容管理", businessType = BusinessType.INSERT)
    @IsLogin
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "栏目id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "searchKey", value = "搜索关键字", paramType = "query"),
            @ApiImplicitParam(name = "imageUrl", value = "页面图片", paramType = "query"),
            @ApiImplicitParam(name = "handleType", value = "办理类型 1、在线办理 2、预约线下 3、无办理类型", paramType = "query"),
            @ApiImplicitParam(name = "redirectModuleId", value = "跳转栏目id", paramType = "query"),
            @ApiImplicitParam(name = "redirectUrl", value = "跳转栏目url", paramType = "query"),
            @ApiImplicitParam(name = "guideFileId", value = "办理指南文件id", paramType = "query"),
            @ApiImplicitParam(name = "tabContent", value = "页签json：[" +
                    "  // 普通页签" +
                    "  {" +
                    "   \"contentType\": 1" +
                    "   \"tabType\": 1" +
                    "   \"tabName\": \"服务简介\"" +
                    "   \"content\": \"内容\"" +
                    "  }," +
                    "  {" +
                    "   \"contentType\": 2" +
                    "   \"tabType\": 1" +
                    "   \"tabName\": \"接口说明\"" +
                    "   \"content\": \"内容\"" +
                    "  }," +
                    "  // 列表页签" +
                    "  {" +
                    "   \"contentType\": 3" +
                    "   \"tabType\": 2" +
                    "   \"tabName\": \"常见问题\"" +
                    "   \"tableInfoList\": [" +
                    "    {" +
                    "     \"titleName\": \"问题名称1\"" +
                    "     \"infoContent\": \"问题答案1\"" +
                    "    }," +
                    "    {" +
                    "     \"titleName\": \"问题名称2\"" +
                    "     \"infoContent\": \"问题答案2\"" +
                    "    }" +
                    "    ...." +
                    "   ]" +
                    "  }," +
                    "  {" +
                    "   \"contentType\": 4" +
                    "   \"tabType\": 2" +
                    "   \"tabName\": \"使用案例\"" +
                    "   \"tableInfoList\": [" +
                    "    {" +
                    "     \"titleName\": \"案例1\"" +
                    "     \"infoContent\": String" +
                    "    }," +
                    "    {" +
                    "     \"titleName\": \"案例2\"" +
                    "     \"infoContent\": String" +
                    "    }" +
                    "    ...." +
                    "   ]" +
                    "  }" +
                    "  ..." +
                    " ]", paramType = "query")
    })
    public ResponseEntity addService(@ApiIgnore @RequestBody ContentDto contentDto) {
        Module module = moduleService.selectById(contentDto.getModuleId());
        ServiceTemplate serviceTemplate = serviceTemplateService.getByModuleId(contentDto.getModuleId());
        if (serviceTemplate != null) {
            return ResponseEntity.error("栏目下已有内容，不能再添加新的内容");
        }
        ResponseEntity errorResponse = validateModule(module);
        if (errorResponse != null) {
            return errorResponse;
        }
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        Template template = createTemplate(contentDto);
        serviceTemplateService.insertServiceTemplate(template, contentDto, loginUser);
        applyTemplateService.updateApplyTemplate(contentDto, module, loginUser);

        String idType = contentDto.getModuleId();
        String serviceTemplateType = ModuleConstant.SERVICE_TEMPLATE_TYPE;
        Content content = createContent(contentDto, loginUser.getUsername(), contentDto.getModuleId());
        DocEntity docEntity = moduleService.setDocEntity(idType, module, content, contentDto, serviceTemplateType);
        try {
            elasticsearchTemplate.save(docEntity);
        } catch (Exception e) {
            log.info("业务内容添加ES失败 ：{}", contentDto.getModuleId());
            e.printStackTrace();
        }

        return ResponseEntity.success("添加成功");
    }

    @ApiOperation("信息页面内容添加")
    @PutMapping("/add/info")
    @Log(module = "内容管理", businessType = BusinessType.INSERT)
    @IsLogin
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "栏目id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "title", value = "页面标题", paramType = "query"),
            @ApiImplicitParam(name = "content", value = "页面内容", paramType = "query"),
            @ApiImplicitParam(name = "searchKey", value = "搜索关键字", paramType = "query"),
            @ApiImplicitParam(name = "imageUrl", value = "页面图片", paramType = "query")
    })
    public ResponseEntity addInfo(@ApiIgnore @RequestBody ContentDto contentDto) {
        Module module = moduleService.selectById(contentDto.getModuleId());
        ResponseEntity errorResponse = validateModule(module);
        if (errorResponse != null) {
            return errorResponse;
        }
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        Template template = createTemplate(contentDto);
        String id = IdUtils.fastSimpleUUID();
        Content content = createContent(contentDto, loginUser.getUsername(), id);
        infoTemplateService.insertTemplate(template, content);
        String serviceTemplateType = ModuleConstant.INFO_TEMPLATE_TYPE;
        DocEntity docEntity = moduleService.setDocEntity(id, module, content, contentDto, serviceTemplateType);
        try {
            elasticsearchTemplate.save(docEntity);
        } catch (Exception e) {
            log.info("信息页面内容添加ES失败 ：{}", contentDto.getContentId());
            e.printStackTrace();
        }
        return ResponseEntity.success("添加成功");
    }


    @ApiOperation("单页面内容查看")
    @GetMapping("/single/page/{moduleId}")
    @IsLogin
    public ResponseEntity show(@PathVariable("moduleId") String moduleId) {
        return ResponseEntity.success(moduleService.getContent(moduleId));
    }

    @ApiOperation("信息页面内容查看")
    @GetMapping("info/page/")
    @IsLogin
    public ResponseEntity show(@RequestParam(value = "moduleId") @ApiParam(name = "moduleId", value = "模块ID") String moduleId, @RequestParam(value = "pageSize") @ApiParam(name = "pageSize", value = "每页多少条") Integer pageSize, @RequestParam(value = "pageNumber") @ApiParam(name = "pageNumber", value = "页码") Integer pageNumber) {
        Module module = moduleService.selectById(moduleId);
        if (module.getTemplateType() != ModuleConstant.INFO_TEMPLATE) {
            return ResponseEntity.error("页面模板类型错误");
        }
        PageInfo<Content> contentList = infoTemplateService.contentPageList(module, pageSize, pageNumber);
        return ResponseEntity.success(contentList);
    }

    @ApiOperation("数据业务页面内容查看")
    @GetMapping("data/page/{moduleId}")
    @IsLogin
    public ResponseEntity showDataPage(@PathVariable("moduleId") String moduleId) {
        Module module = moduleService.selectById(moduleId);
        if (module.getTemplateType() != ModuleConstant.DATA_TEMPLATE) {
            return ResponseEntity.error("页面模板类型错误");
        }
        return ResponseEntity.success(dataTemplateService.getDataTemplateByModuleId(moduleId));
    }

    @ApiOperation("业务页面内容数据列表查看")
    @GetMapping("data/page/list")
    @IsLogin
    public ResponseEntity showDataPageList(@RequestParam(value = "templateId") @ApiParam(name = "templateId", value = "模板ID") String templateId, @RequestParam(value = "pageSize") @ApiParam(name = "pageSize", value = "每页多少条") Integer pageSize, @RequestParam(value = "pageNumber") @ApiParam(name = "pageNumber", value = "页码") Integer pageNumber) {
        return ResponseEntity.success(dataTemplateService.getServiceDataListByTemplateId(templateId, pageSize, pageNumber));
    }

    @IsLogin
    @ApiOperation("业务页面信息查询")
    @GetMapping("service/{moduleId}")
    public ResponseEntity queryBusiness(@PathVariable("moduleId") String moduleId) {
        List<TabContent> tabContentList = new ArrayList<>();
        ServiceTemplate serviceTemplate = serviceTemplateService.getByModuleId(moduleId);
        if (serviceTemplate == null) {
            return ResponseEntity.success(null);
        }
        //普通页签列表
        List<NormalTab> normalTabList = serviceNormalService.getAsNormalTabByTemplateId(serviceTemplate.getId());
        //带listtab页签信息列表
        List<ListTab> listTabList = serviceTabListService.getAsListTabByTemplateId(serviceTemplate.getId());

        for (int i = 0; i < listTabList.size(); i++) {
            ListTab listTab = listTabList.get(i);
            String tabId = listTab.getId();
            //TabInfo信息列表
            List<TabInfo> tabInfoList = serviceTabInfoService.getAsTabInfoByTemplateId(tabId);
            listTab.setTabInfoList(tabInfoList);
        }

        tabContentList.addAll(normalTabList);
        tabContentList.addAll(listTabList);
        serviceTemplate.setTabList(tabContentList);
        return ResponseEntity.success(serviceTemplate);
    }

    @IsLogin
    @ApiOperation("特色服务页面查询")
    @GetMapping("apply/{moduleId}")
    public ResponseEntity queryApply(@PathVariable("moduleId") String moduleId) {
        ApplyTemplate applyTemplate = applyTemplateService.getByModuleId(moduleId);
        if (applyTemplate == null) {
            return ResponseEntity.success(null);
        }
        return ResponseEntity.success(applyTemplate);
    }


    @ApiOperation("编辑单页面内容")
    @PostMapping("/edit/single")
    @Log(module = "内容管理", businessType = BusinessType.UPDATE)
    @IsLogin
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contentId", value = "内容id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "title", value = "页面内容", required = true, paramType = "query"),
            @ApiImplicitParam(name = "content", value = "页面内容", required = true, paramType = "query"),
            @ApiImplicitParam(name = "imageUrl", value = "页面图片", paramType = "query")
    })
    public ResponseEntity editSingle(@ApiIgnore @RequestBody ContentDto contentDto) throws IOException {
        Module module = moduleService.selectById(contentDto.getModuleId());
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        Content content = createContent(contentDto, loginUser.getUsername(), contentDto.getContentId());
        String msg = singleTemplateService.updateContent(contentDto);
        String serviceType = ModuleConstant.SINGLE_TEMPLATE_TYPE;
        if (msg.contains("success")) {
            moduleService.updateEs(contentDto, module, content, serviceType);
            return ResponseEntity.success("修改成功");
        } else {
            return ResponseEntity.error(msg.replaceAll("error:", ""));
        }
    }



    @ApiOperation("编辑信息页面内容")
    @PostMapping("/edit/info")
    @Log(module = "内容管理", businessType = BusinessType.UPDATE)
    @IsLogin
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contentId", value = "内容id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "title", value = "页面内容", required = true, paramType = "query"),
            @ApiImplicitParam(name = "content", value = "页面内容", required = true, paramType = "query"),
            @ApiImplicitParam(name = "imageUrl", value = "页面图片", paramType = "query"),
            @ApiImplicitParam(name = "dataId", value = "页面图片", paramType = "query")
    })
    public ResponseEntity editInfo(@ApiIgnore @RequestBody ContentDto contentDto) {
        Module module = moduleService.selectById(contentDto.getModuleId());
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        Content content = createContent(contentDto, loginUser.getUsername(), contentDto.getContentId());
        String msg = infoTemplateService.updateContent(contentDto);
        String serviceType = ModuleConstant.INFO_TEMPLATE_TYPE;
        if (msg.contains("success")) {
            moduleService.updateEs(contentDto, module, content, serviceType);
            return ResponseEntity.success("修改成功");
        } else {
            return ResponseEntity.error(msg.replaceAll("error:", ""));
        }
    }

    @ApiOperation("业务内容修改")
    @PostMapping("/edit/service")
    @Log(module = "内容管理", businessType = BusinessType.UPDATE)
    @IsLogin
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "栏目id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "contentId", value = "serviceTemplateId 模板id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "imageUrl", value = "页面图片", paramType = "query"),
            @ApiImplicitParam(name = "handleType", value = "办理类型 1、在线办理 2、预约线下 3、无办理类型", paramType = "query"),
            @ApiImplicitParam(name = "redirectModuleId", value = "跳转栏目id", paramType = "query"),
            @ApiImplicitParam(name = "redirectUrl", value = "跳转栏目url", paramType = "query"),
            @ApiImplicitParam(name = "guideFileUrl", value = "办理指南文件下载路径", paramType = "query"),
            @ApiImplicitParam(name = "tabContent", value = "页签json：[" +
                    "  // 普通页签" +
                    "  {" +
                    "   \"contentType\": 1" +
                    "   \"tabType\": 1" +
                    "   \"tabName\": \"服务简介\"" +
                    "   \"content\": \"内容\"" +
                    "  }," +
                    "  {" +
                    "   \"contentType\": 2" +
                    "   \"tabType\": 1" +
                    "   \"tabName\": \"接口说明\"" +
                    "   \"content\": \"内容\"" +
                    "  }," +
                    "  // 列表页签" +
                    "  {" +
                    "   \"contentType\": 3" +
                    "   \"tabType\": 2" +
                    "   \"tabName\": \"常见问题\"" +
                    "   \"tableInfoList\": [" +
                    "    {" +
                    "     \"titleName\": \"问题名称1\"" +
                    "     \"infoContent\": \"问题答案1\"" +
                    "    }," +
                    "    {" +
                    "     \"titleName\": \"问题名称2\"" +
                    "     \"infoContent\": \"问题答案2\"" +
                    "    }" +
                    "    ...." +
                    "   ]" +
                    "  }," +
                    "  {" +
                    "   \"contentType\": 4" +
                    "   \"tabType\": 2" +
                    "   \"tabName\": \"使用案例\"" +
                    "   \"tableInfoList\": [" +
                    "    {" +
                    "     \"titleName\": \"案例1\"" +
                    "     \"infoContent\": String" +
                    "    }," +
                    "    {" +
                    "     \"titleName\": \"案例2\"" +
                    "     \"infoContent\": String" +
                    "    }" +
                    "    ...." +
                    "   ]" +
                    "  }" +
                    "  ..." +
                    " ]", paramType = "query")
    })
    @Transactional
    public ResponseEntity editService(@ApiIgnore @RequestBody ContentDto contentDto) {
        Module module = moduleService.selectById(contentDto.getModuleId());
        ResponseEntity errorResponse = validateModule(module);
        if (errorResponse != null) {
            return errorResponse;
        }
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        if (contentDto.getHandleType() == 2 || contentDto.getHandleType() == 1) {
            if (contentDto.getRedirectModuleId() == null) {
                return ResponseEntity.error("办理跳转栏目为空，请选择");
            }
            applyTemplateService.updateApplyTemplate(contentDto, module, loginUser);
        }
        ServiceTemplate serviceTemplate = serviceTemplateService.selectById(contentDto.getContentId());
        if (contentDto.getHandleType() == 3) {
            ApplyTemplate applyTemplate = applyTemplateService.selectById(serviceTemplate.getRedirectModuleId());
            applyTemplate.setRedirectUrl("");
            applyTemplate.setRedirectModuleId("");
            applyTemplate.setGuideFileId("");
            applyTemplateService.update(applyTemplate);
        }
        if (!serviceTemplate.getGuideFileId().equals(contentDto.getGuideFileId())) {
            fileDataService.deleteData(serviceTemplate.getGuideFileId());
        }
        serviceTemplateService.updateServiceTemplate(serviceTemplate, contentDto, loginUser);

        Content content = createContent(contentDto, loginUser.getUsername(), contentDto.getContentId());
        String serviceType = ModuleConstant.SERVICE_TEMPLATE_TYPE;
        moduleService.updateEs(contentDto, module, content, serviceType);
        return ResponseEntity.success("添加成功");
    }

    @ApiOperation("删除内容")
    @DeleteMapping("/del")
    @Log(module = "内容管理", businessType = BusinessType.UPDATE)
    @IsLogin
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "栏目id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "templateId", value = "模板id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "contentId", value = "内容id", required = true, paramType = "query")
    })
    public ResponseEntity del(@RequestBody @ApiIgnore JSONObject jsonObject) {
        String contentId = jsonObject.getString("contentId");
        String templateId = jsonObject.getString("templateId");
        String moduleId = jsonObject.getString("moduleId");
        Module module = moduleService.selectById(moduleId);
        if (module.getTemplateType() == ModuleConstant.SINGLE_TEMPLATE) {
            // 单页面直接删除
            singleTemplateService.deleteContent(contentId);
            elasticsearchTemplate.delete(elasticsearchTemplate.get(contentId, DocEntity.class));
        }
        if (module.getTemplateType() == ModuleConstant.INFO_TEMPLATE) {
            // 信息页面要进行判断
            infoTemplateService.deleteContent(contentId);
            elasticsearchTemplate.delete(elasticsearchTemplate.get(contentId, DocEntity.class));
        }

        if (module.getTemplateType() == ModuleConstant.SERVICE_TEMPLATE) {
            serviceTemplateService.deleteContent(templateId);
            elasticsearchTemplate.delete(elasticsearchTemplate.get(moduleId, DocEntity.class));
        }

        if (module.getTemplateType() == ModuleConstant.APPLICATION_TEMPLATE) {
            applyTemplateService.deleteContent(templateId);
            elasticsearchTemplate.delete(elasticsearchTemplate.get(moduleId, DocEntity.class));
        }
        // TODO 业务服务再具体分析

        return ResponseEntity.success("删除成功");
    }

    /**
     * 文件上传接口
     *
     * @param multipartFile
     * @return
     */
    @RequestMapping(value = "/upload/pic", method = RequestMethod.POST)
    @ApiOperation("图片上传")
    @Log(module = "内容管理", businessType = BusinessType.INSERT)
    @IsLogin
    public ResponseEntity uploadPic(MultipartFile multipartFile, @ApiParam(name = "fileTitle", value = "图片标题") @RequestParam(name = "fileTitle", required = false) String fileTitle, HttpServletRequest request) {
        if (multipartFile.getSize() > 500 * 1024 * 1024) {
            return ResponseEntity.error("文件超过500MB，暂不支持");
        }
        FileData fileData = new FileData();
        fileData.setId(IdUtils.fastSimpleUUID());
        fileData.setFileType(ModuleConstant.FILE_TYPE_PICTURE);
        if (StringUtils.isNotBlank(fileTitle)) {
            fileData.setFileTitle(fileTitle);
        } else {
            fileData.setFileTitle("");
        }
        fileData.setFileTitle(fileTitle);
        fileData.setCreateTime(System.currentTimeMillis());
        SystemUser user = tokenService.getLoginUser(request).getUser();
        fileData.setCreateBy(user.getUsername());
        String uploadPath = fileDataService.upload(multipartFile, ModuleConstant.FILE_TYPE_PICTURE, fileData);
        if (!uploadPath.contains("success")) {
            return ResponseEntity.error(uploadPath);
        }
        return ResponseEntity.success(accessUrl + fileData.getId());
    }

    /**
     * 文件上传接口
     *
     * @param multipartFile
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ApiOperation("数据上传")
    @Log(module = "内容管理", businessType = BusinessType.INSERT)
    @IsLogin
    public ResponseEntity upload(MultipartFile multipartFile,
                                 @ApiParam(name = "fileTitle", value = "文件标题") @RequestParam(name = "fileTitle", required = false) String fileTitle,
                                 @ApiParam(name = "fileType", value = "文件类型 1、图片 2、地图数据 3、文档信息 4、程序文件 5、其他") @RequestParam(name = "fileType") Integer fileType) {
        FileData fileData = new FileData();
        fileData.setId(IdUtils.fastSimpleUUID());
        fileData.setFileType(fileType);
        fileData.setMd5(FileMD5Utils.getFileMD5String(multipartFile));
        if (StringUtils.isNotBlank(fileTitle)) {
            fileData.setFileTitle(fileTitle);
        } else {
            fileData.setFileTitle("");
        }
        fileData.setCreateTime(System.currentTimeMillis());
        fileData.setCreateBy(tokenService.getLoginUser(ServletUtils.getRequest()).getUsername());
        String uploadPath = fileDataService.upload(multipartFile, fileType, fileData);
        if (!uploadPath.contains("success")) {
            return ResponseEntity.error(uploadPath);
        }
        return ResponseEntity.success(uploadPath.substring(uploadPath.indexOf(":") + 1));
    }

    /**
     * 数据文件上传接口
     *
     * @param multipartFile
     * @return
     */
    @RequestMapping(value = "/uploadGuide", method = RequestMethod.POST)
    @ApiOperation("办理指南文件上传")
    @Log(module = "内容管理", businessType = BusinessType.INSERT)
    @IsLogin
    public ResponseEntity dataUpload(MultipartFile multipartFile) {
        FileData fileData = new FileData();
        fileData.setId(IdUtils.fastSimpleUUID());
        fileData.setFileType(3);
        fileData.setCreateTime(System.currentTimeMillis());
        fileData.setCreateBy(tokenService.getLoginUser(ServletUtils.getRequest()).getUsername());
        String uploadPath = fileDataService.upload(multipartFile, 3, fileData);
        if (!uploadPath.contains("success")) {
            return ResponseEntity.error(uploadPath);
        }
        return ResponseEntity.success(fileData.getId());
    }

    /**
     * 根据办理类型查找特色服务列表
     *
     * @param type
     * @return
     */
    @ApiOperation("获取不同类型的特色服务")
    @GetMapping("/apply/{type}")
    public ResponseEntity getApplyTemplateList(@PathVariable("type") @ApiParam(name = "handleType", value = "办理类型 1、在线办理 2、线下预约") Integer type) {
        List<Module> applyModuleList = moduleService.getListByType(type);
        return ResponseEntity.success(applyModuleList);
    }
}
