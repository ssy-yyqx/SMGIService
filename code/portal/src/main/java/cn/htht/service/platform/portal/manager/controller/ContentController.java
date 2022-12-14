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
@Api(tags = "????????????")
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
            return ResponseEntity.error("????????????????????????");
        }
        if (module.getModuleType() == ModuleConstant.MODULE_MENU) {
            return ResponseEntity.error("?????????????????????????????????????????????");
        }
        if (module.getModuleType() == ModuleConstant.HOME_TEMPLATE) {
            return ResponseEntity.error("???????????????????????????");
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

    @ApiOperation("?????????????????????")
    @PutMapping("/add/single")
    @IsLogin
    @Log(module = "????????????", businessType = BusinessType.INSERT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "??????id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "title", value = "????????????", required = true, paramType = "query"),
            @ApiImplicitParam(name = "content", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "searchKey", value = "???????????????", paramType = "query"),
            @ApiImplicitParam(name = "imageUrl", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "dataId", value = "??????id", paramType = "query")
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
            return ResponseEntity.error("??????????????????");
        }
        if (StringUtils.isBlank(contentDtoDto.getContent())) {
            return ResponseEntity.error("??????????????????");
        }
        SingleTemplate singleTemplate = singleTemplateService.getByModuleId(contentDtoDto.getModuleId());
        if (singleTemplate != null) {
            return ResponseEntity.error("????????????????????????????????????????????????????????????");
        }
        singleTemplateService.insertTemplate(template, content);
        String serviceTemplateType = ModuleConstant.SINGLE_TEMPLATE_TYPE;
        DocEntity docEntity = moduleService.setDocEntity(id, module, content, contentDtoDto, serviceTemplateType);
        try {
            elasticsearchTemplate.save(docEntity);
        } catch (Exception e) {
            log.info("?????????????????????ES?????? ???{}", contentDtoDto.getContentId());
            e.printStackTrace();
        }

        return ResponseEntity.success("????????????");
    }

    @ApiOperation("????????????????????????")
    @PutMapping("/add/data")
    @Log(module = "????????????", businessType = BusinessType.INSERT)
    @IsLogin
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "??????id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "title", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "content", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "searchKey", value = "???????????????", paramType = "query"),
            @ApiImplicitParam(name = "imageUrl", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "handleType", value = "???????????? 1??????????????? 2???????????????", paramType = "query"),
            @ApiImplicitParam(name = "redirectModuleId", value = "????????????id", paramType = "query"),
            @ApiImplicitParam(name = "redirectUrl", value = "????????????url", paramType = "query"),
            @ApiImplicitParam(name = "guideFileUrl", value = "??????????????????????????????", paramType = "query")
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
            log.info("????????????????????????ES?????? ???{}", contentDto.getModuleId());
            e.printStackTrace();
        }

        return ResponseEntity.success("????????????");
    }


    @ApiOperation("??????????????????")
    @PutMapping("/add/service")
    @Log(module = "????????????", businessType = BusinessType.INSERT)
    @IsLogin
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "??????id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "searchKey", value = "???????????????", paramType = "query"),
            @ApiImplicitParam(name = "imageUrl", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "handleType", value = "???????????? 1??????????????? 2??????????????? 3??????????????????", paramType = "query"),
            @ApiImplicitParam(name = "redirectModuleId", value = "????????????id", paramType = "query"),
            @ApiImplicitParam(name = "redirectUrl", value = "????????????url", paramType = "query"),
            @ApiImplicitParam(name = "guideFileId", value = "??????????????????id", paramType = "query"),
            @ApiImplicitParam(name = "tabContent", value = "??????json???[" +
                    "  // ????????????" +
                    "  {" +
                    "   \"contentType\": 1" +
                    "   \"tabType\": 1" +
                    "   \"tabName\": \"????????????\"" +
                    "   \"content\": \"??????\"" +
                    "  }," +
                    "  {" +
                    "   \"contentType\": 2" +
                    "   \"tabType\": 1" +
                    "   \"tabName\": \"????????????\"" +
                    "   \"content\": \"??????\"" +
                    "  }," +
                    "  // ????????????" +
                    "  {" +
                    "   \"contentType\": 3" +
                    "   \"tabType\": 2" +
                    "   \"tabName\": \"????????????\"" +
                    "   \"tableInfoList\": [" +
                    "    {" +
                    "     \"titleName\": \"????????????1\"" +
                    "     \"infoContent\": \"????????????1\"" +
                    "    }," +
                    "    {" +
                    "     \"titleName\": \"????????????2\"" +
                    "     \"infoContent\": \"????????????2\"" +
                    "    }" +
                    "    ...." +
                    "   ]" +
                    "  }," +
                    "  {" +
                    "   \"contentType\": 4" +
                    "   \"tabType\": 2" +
                    "   \"tabName\": \"????????????\"" +
                    "   \"tableInfoList\": [" +
                    "    {" +
                    "     \"titleName\": \"??????1\"" +
                    "     \"infoContent\": String" +
                    "    }," +
                    "    {" +
                    "     \"titleName\": \"??????2\"" +
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
            return ResponseEntity.error("???????????????????????????????????????????????????");
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
            log.info("??????????????????ES?????? ???{}", contentDto.getModuleId());
            e.printStackTrace();
        }

        return ResponseEntity.success("????????????");
    }

    @ApiOperation("????????????????????????")
    @PutMapping("/add/info")
    @Log(module = "????????????", businessType = BusinessType.INSERT)
    @IsLogin
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "??????id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "title", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "content", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "searchKey", value = "???????????????", paramType = "query"),
            @ApiImplicitParam(name = "imageUrl", value = "????????????", paramType = "query")
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
            log.info("????????????????????????ES?????? ???{}", contentDto.getContentId());
            e.printStackTrace();
        }
        return ResponseEntity.success("????????????");
    }


    @ApiOperation("?????????????????????")
    @GetMapping("/single/page/{moduleId}")
    @IsLogin
    public ResponseEntity show(@PathVariable("moduleId") String moduleId) {
        return ResponseEntity.success(moduleService.getContent(moduleId));
    }

    @ApiOperation("????????????????????????")
    @GetMapping("info/page/")
    @IsLogin
    public ResponseEntity show(@RequestParam(value = "moduleId") @ApiParam(name = "moduleId", value = "??????ID") String moduleId, @RequestParam(value = "pageSize") @ApiParam(name = "pageSize", value = "???????????????") Integer pageSize, @RequestParam(value = "pageNumber") @ApiParam(name = "pageNumber", value = "??????") Integer pageNumber) {
        Module module = moduleService.selectById(moduleId);
        if (module.getTemplateType() != ModuleConstant.INFO_TEMPLATE) {
            return ResponseEntity.error("????????????????????????");
        }
        PageInfo<Content> contentList = infoTemplateService.contentPageList(module, pageSize, pageNumber);
        return ResponseEntity.success(contentList);
    }

    @ApiOperation("??????????????????????????????")
    @GetMapping("data/page/{moduleId}")
    @IsLogin
    public ResponseEntity showDataPage(@PathVariable("moduleId") String moduleId) {
        Module module = moduleService.selectById(moduleId);
        if (module.getTemplateType() != ModuleConstant.DATA_TEMPLATE) {
            return ResponseEntity.error("????????????????????????");
        }
        return ResponseEntity.success(dataTemplateService.getDataTemplateByModuleId(moduleId));
    }

    @ApiOperation("????????????????????????????????????")
    @GetMapping("data/page/list")
    @IsLogin
    public ResponseEntity showDataPageList(@RequestParam(value = "templateId") @ApiParam(name = "templateId", value = "??????ID") String templateId, @RequestParam(value = "pageSize") @ApiParam(name = "pageSize", value = "???????????????") Integer pageSize, @RequestParam(value = "pageNumber") @ApiParam(name = "pageNumber", value = "??????") Integer pageNumber) {
        return ResponseEntity.success(dataTemplateService.getServiceDataListByTemplateId(templateId, pageSize, pageNumber));
    }

    @IsLogin
    @ApiOperation("????????????????????????")
    @GetMapping("service/{moduleId}")
    public ResponseEntity queryBusiness(@PathVariable("moduleId") String moduleId) {
        List<TabContent> tabContentList = new ArrayList<>();
        ServiceTemplate serviceTemplate = serviceTemplateService.getByModuleId(moduleId);
        if (serviceTemplate == null) {
            return ResponseEntity.success(null);
        }
        //??????????????????
        List<NormalTab> normalTabList = serviceNormalService.getAsNormalTabByTemplateId(serviceTemplate.getId());
        //???listtab??????????????????
        List<ListTab> listTabList = serviceTabListService.getAsListTabByTemplateId(serviceTemplate.getId());

        for (int i = 0; i < listTabList.size(); i++) {
            ListTab listTab = listTabList.get(i);
            String tabId = listTab.getId();
            //TabInfo????????????
            List<TabInfo> tabInfoList = serviceTabInfoService.getAsTabInfoByTemplateId(tabId);
            listTab.setTabInfoList(tabInfoList);
        }

        tabContentList.addAll(normalTabList);
        tabContentList.addAll(listTabList);
        serviceTemplate.setTabList(tabContentList);
        return ResponseEntity.success(serviceTemplate);
    }

    @IsLogin
    @ApiOperation("????????????????????????")
    @GetMapping("apply/{moduleId}")
    public ResponseEntity queryApply(@PathVariable("moduleId") String moduleId) {
        ApplyTemplate applyTemplate = applyTemplateService.getByModuleId(moduleId);
        if (applyTemplate == null) {
            return ResponseEntity.success(null);
        }
        return ResponseEntity.success(applyTemplate);
    }


    @ApiOperation("?????????????????????")
    @PostMapping("/edit/single")
    @Log(module = "????????????", businessType = BusinessType.UPDATE)
    @IsLogin
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contentId", value = "??????id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "title", value = "????????????", required = true, paramType = "query"),
            @ApiImplicitParam(name = "content", value = "????????????", required = true, paramType = "query"),
            @ApiImplicitParam(name = "imageUrl", value = "????????????", paramType = "query")
    })
    public ResponseEntity editSingle(@ApiIgnore @RequestBody ContentDto contentDto) throws IOException {
        Module module = moduleService.selectById(contentDto.getModuleId());
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        Content content = createContent(contentDto, loginUser.getUsername(), contentDto.getContentId());
        String msg = singleTemplateService.updateContent(contentDto);
        String serviceType = ModuleConstant.SINGLE_TEMPLATE_TYPE;
        if (msg.contains("success")) {
            moduleService.updateEs(contentDto, module, content, serviceType);
            return ResponseEntity.success("????????????");
        } else {
            return ResponseEntity.error(msg.replaceAll("error:", ""));
        }
    }



    @ApiOperation("????????????????????????")
    @PostMapping("/edit/info")
    @Log(module = "????????????", businessType = BusinessType.UPDATE)
    @IsLogin
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contentId", value = "??????id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "title", value = "????????????", required = true, paramType = "query"),
            @ApiImplicitParam(name = "content", value = "????????????", required = true, paramType = "query"),
            @ApiImplicitParam(name = "imageUrl", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "dataId", value = "????????????", paramType = "query")
    })
    public ResponseEntity editInfo(@ApiIgnore @RequestBody ContentDto contentDto) {
        Module module = moduleService.selectById(contentDto.getModuleId());
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        Content content = createContent(contentDto, loginUser.getUsername(), contentDto.getContentId());
        String msg = infoTemplateService.updateContent(contentDto);
        String serviceType = ModuleConstant.INFO_TEMPLATE_TYPE;
        if (msg.contains("success")) {
            moduleService.updateEs(contentDto, module, content, serviceType);
            return ResponseEntity.success("????????????");
        } else {
            return ResponseEntity.error(msg.replaceAll("error:", ""));
        }
    }

    @ApiOperation("??????????????????")
    @PostMapping("/edit/service")
    @Log(module = "????????????", businessType = BusinessType.UPDATE)
    @IsLogin
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "??????id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "contentId", value = "serviceTemplateId ??????id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "imageUrl", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "handleType", value = "???????????? 1??????????????? 2??????????????? 3??????????????????", paramType = "query"),
            @ApiImplicitParam(name = "redirectModuleId", value = "????????????id", paramType = "query"),
            @ApiImplicitParam(name = "redirectUrl", value = "????????????url", paramType = "query"),
            @ApiImplicitParam(name = "guideFileUrl", value = "??????????????????????????????", paramType = "query"),
            @ApiImplicitParam(name = "tabContent", value = "??????json???[" +
                    "  // ????????????" +
                    "  {" +
                    "   \"contentType\": 1" +
                    "   \"tabType\": 1" +
                    "   \"tabName\": \"????????????\"" +
                    "   \"content\": \"??????\"" +
                    "  }," +
                    "  {" +
                    "   \"contentType\": 2" +
                    "   \"tabType\": 1" +
                    "   \"tabName\": \"????????????\"" +
                    "   \"content\": \"??????\"" +
                    "  }," +
                    "  // ????????????" +
                    "  {" +
                    "   \"contentType\": 3" +
                    "   \"tabType\": 2" +
                    "   \"tabName\": \"????????????\"" +
                    "   \"tableInfoList\": [" +
                    "    {" +
                    "     \"titleName\": \"????????????1\"" +
                    "     \"infoContent\": \"????????????1\"" +
                    "    }," +
                    "    {" +
                    "     \"titleName\": \"????????????2\"" +
                    "     \"infoContent\": \"????????????2\"" +
                    "    }" +
                    "    ...." +
                    "   ]" +
                    "  }," +
                    "  {" +
                    "   \"contentType\": 4" +
                    "   \"tabType\": 2" +
                    "   \"tabName\": \"????????????\"" +
                    "   \"tableInfoList\": [" +
                    "    {" +
                    "     \"titleName\": \"??????1\"" +
                    "     \"infoContent\": String" +
                    "    }," +
                    "    {" +
                    "     \"titleName\": \"??????2\"" +
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
                return ResponseEntity.error("????????????????????????????????????");
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
        return ResponseEntity.success("????????????");
    }

    @ApiOperation("????????????")
    @DeleteMapping("/del")
    @Log(module = "????????????", businessType = BusinessType.UPDATE)
    @IsLogin
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "??????id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "templateId", value = "??????id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "contentId", value = "??????id", required = true, paramType = "query")
    })
    public ResponseEntity del(@RequestBody @ApiIgnore JSONObject jsonObject) {
        String contentId = jsonObject.getString("contentId");
        String templateId = jsonObject.getString("templateId");
        String moduleId = jsonObject.getString("moduleId");
        Module module = moduleService.selectById(moduleId);
        if (module.getTemplateType() == ModuleConstant.SINGLE_TEMPLATE) {
            // ?????????????????????
            singleTemplateService.deleteContent(contentId);
            elasticsearchTemplate.delete(elasticsearchTemplate.get(contentId, DocEntity.class));
        }
        if (module.getTemplateType() == ModuleConstant.INFO_TEMPLATE) {
            // ???????????????????????????
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
        // TODO ???????????????????????????

        return ResponseEntity.success("????????????");
    }

    /**
     * ??????????????????
     *
     * @param multipartFile
     * @return
     */
    @RequestMapping(value = "/upload/pic", method = RequestMethod.POST)
    @ApiOperation("????????????")
    @Log(module = "????????????", businessType = BusinessType.INSERT)
    @IsLogin
    public ResponseEntity uploadPic(MultipartFile multipartFile, @ApiParam(name = "fileTitle", value = "????????????") @RequestParam(name = "fileTitle", required = false) String fileTitle, HttpServletRequest request) {
        if (multipartFile.getSize() > 500 * 1024 * 1024) {
            return ResponseEntity.error("????????????500MB???????????????");
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
     * ??????????????????
     *
     * @param multipartFile
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ApiOperation("????????????")
    @Log(module = "????????????", businessType = BusinessType.INSERT)
    @IsLogin
    public ResponseEntity upload(MultipartFile multipartFile,
                                 @ApiParam(name = "fileTitle", value = "????????????") @RequestParam(name = "fileTitle", required = false) String fileTitle,
                                 @ApiParam(name = "fileType", value = "???????????? 1????????? 2??????????????? 3??????????????? 4??????????????? 5?????????") @RequestParam(name = "fileType") Integer fileType) {
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
     * ????????????????????????
     *
     * @param multipartFile
     * @return
     */
    @RequestMapping(value = "/uploadGuide", method = RequestMethod.POST)
    @ApiOperation("????????????????????????")
    @Log(module = "????????????", businessType = BusinessType.INSERT)
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
     * ??????????????????????????????????????????
     *
     * @param type
     * @return
     */
    @ApiOperation("?????????????????????????????????")
    @GetMapping("/apply/{type}")
    public ResponseEntity getApplyTemplateList(@PathVariable("type") @ApiParam(name = "handleType", value = "???????????? 1??????????????? 2???????????????") Integer type) {
        List<Module> applyModuleList = moduleService.getListByType(type);
        return ResponseEntity.success(applyModuleList);
    }
}
