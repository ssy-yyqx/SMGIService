package cn.htht.service.platform.portal.manager.controller;

import cn.htht.service.platform.portal.constant.ModuleConstant;
import cn.htht.service.platform.portal.entity.manager.ApplyTemplate;
import cn.htht.service.platform.portal.entity.manager.Content;
import cn.htht.service.platform.portal.entity.vo.ContentVo;
import cn.htht.service.platform.portal.manager.service.ApplyTemplateService;
import cn.htht.service.platform.portal.manager.service.ModuleService;
import cn.htht.service.platform.portal.common.BaseController;
import cn.htht.service.platform.portal.common.bean.ResponseEntity;
import cn.htht.service.platform.portal.component.web.service.TokenService;
import cn.htht.service.platform.portal.entity.manager.Module;
import cn.htht.service.platform.portal.entity.system.LoginUser;
import cn.htht.service.platform.portal.utils.annotation.IsLogin;
import cn.htht.service.platform.portal.utils.annotation.Log;
import cn.htht.service.platform.portal.utils.enums.BusinessType;
import cn.htht.service.platform.portal.utils.utils.ServletUtils;
import cn.htht.service.platform.portal.utils.utils.StringUtils;
import cn.htht.service.platform.portal.utils.utils.uuid.IdUtils;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @ClassName ModuleController
 * @Description
 * @Author Ken
 * @Date DATE{TIME}
 **/
@RestController
@Api(tags = "栏目管理")
@RequestMapping("/module")
public class ModuleController extends BaseController {


    @Autowired
    private ModuleService moduleService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ApplyTemplateService applyTemplateService;

    @ApiOperation("获取栏目树")
    @GetMapping("/tree")
    public ResponseEntity getModuleTree() {
        List<Module> moduleList = moduleService.buildModuleTree("");
        return ResponseEntity.success(moduleList);
    }

    @ApiOperation("获取栏目列表")
    @GetMapping("/list")
    public ResponseEntity list(@RequestParam(value = "parentId") @ApiParam(name = "parentId", value = "上级栏目id") String id) {
        List<Module> moduleList = moduleService.getModuleList(id);
        return ResponseEntity.success(moduleList);
    }


    @ApiOperation("添加栏目")
    @PutMapping("/add")
    @Log(module = "栏目管理", businessType = BusinessType.INSERT)
    @IsLogin
    // 添加权限验证 @PreAuthorize("@ss.hasPermi('')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleName", value = "栏目名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "searchKey", value = "搜索关键字", paramType = "query"),
            @ApiImplicitParam(name = "parentModuleId", value = "上级栏目id", paramType = "query"),
            @ApiImplicitParam(name = "templateType", value = "模板类型：1、首页 2、单页面 3、信息页面 4、业务页面 5、数据页面 6、特色服务", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "moduleType", value = "栏目节点类型：1、分类节点 2、最终节点", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "isShow", value = "是否显示 1、显示 2、隐藏", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "router", value = "路由标识", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isLink", value = "是否是外部链接 1 是 2 不是", paramType = "query"),
            @ApiImplicitParam(name = "linkUrl", value = "链接url", paramType = "query")
    })
    public ResponseEntity addModule(@RequestBody @ApiIgnore Module module, HttpServletRequest request) {
        ResponseEntity responseEntity = validateParam(module, false);
        if (responseEntity != null) {
            return responseEntity;
        }
        LoginUser loginUser = tokenService.getLoginUser(request);
        module.setId(IdUtils.fastSimpleUUID());
        module.setCreateBy(loginUser.getUsername());
        module.setCreateTime(System.currentTimeMillis());
        Integer sort = moduleService.getMaxSortInModule(module.getParentModuleId());
        if (sort != null) {
            module.setSort(sort + 1);
        } else {
            module.setSort(1);
        }
        if (StringUtils.isBlank(module.getRouterStr())) {
            module.setRouterStr(module.getRouter());
        }
        moduleService.insert(module);

        if (module.getTemplateType().equals(ModuleConstant.APPLICATION_TEMPLATE)) {
            //添加特色服务栏目时，直接创建模板
            ApplyTemplate applyTemplate = new ApplyTemplate();
            applyTemplate.setId(IdUtils.fastSimpleUUID());
            applyTemplate.setSearchKey(module.getModuleName());
            applyTemplate.setModuleId(module.getId());
            applyTemplate.setCreateTime(System.currentTimeMillis());
            applyTemplate.setCreateBy(loginUser.getUsername());
            applyTemplateService.insert(applyTemplate);
        }

        return ResponseEntity.success("添加成功");
    }


    @ApiOperation("修改栏目属性")
    @PostMapping("/edit")
    @Log(module = "栏目管理", businessType = BusinessType.UPDATE)
    // 添加权限验证 @PreAuthorize("@ss.hasPermi('')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "moduleName", value = "栏目名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "searchKey", value = "搜索关键字", defaultValue = "", paramType = "query"),
            @ApiImplicitParam(name = "parentModuleId", value = "上级栏目id", paramType = "query"),
            @ApiImplicitParam(name = "icon", value = "菜单图标", paramType = "query"),
            @ApiImplicitParam(name = "isShow", value = "是否显示 1、显示 2、隐藏", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isLink", value = "是否是外部链接 1 是 2 不是", paramType = "query"),
            @ApiImplicitParam(name = "linkUrl", value = "链接url", paramType = "query")
    })
    public ResponseEntity editModule(@RequestBody @ApiIgnore Module module, HttpServletRequest request) {
        Module updateModule = moduleService.selectById(module.getId());
        if (updateModule == null) {
            return ResponseEntity.error("修改栏目不存在");
        }
        ResponseEntity responseEntity = validateParam(module, true);
        if (responseEntity != null) {
            return responseEntity;
        }
        updateModule.setIcon(module.getIcon());
        updateModule.setModuleName(module.getModuleName());
        updateModule.setSearchKey(module.getSearchKey());
        updateModule.setParentModuleId(module.getParentModuleId());
        updateModule.setIsShow(module.getIsShow());
        updateModule.setIsLink(module.getIsLink());
        updateModule.setLinkUrl(module.getLinkUrl());
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        updateModule.setUpdateBy(loginUser.getUsername());
        updateModule.setUpdateTime(System.currentTimeMillis());
        moduleService.update(updateModule);
        return ResponseEntity.success("修改成功");
    }


    @PostMapping("/sort")
    @ApiOperation("设置排序")
    @Log(module = "栏目管理", businessType = BusinessType.UPDATE)
    @IsLogin
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "栏目id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序上移1 或 下移-1", required = true, paramType = "query", dataType = "Integer"),
    })
    public ResponseEntity sortModule(@RequestBody @ApiIgnore JSONObject jsonObject) {
        String moduleId = jsonObject.getString("moduleId");
        Module module = moduleService.selectById(moduleId);
        String sort = jsonObject.getString("sort");
        if (sort.equals("up") && module.getSort() == 1) {
            return ResponseEntity.error("该栏目已在最顶层");
        }
        if (sort.equals("down") && module.getSort() == moduleService.getMaxSortInModule(module.getParentModuleId())) {
            return ResponseEntity.error("该栏目已在最底层");
        }
        moduleService.updateSort(module, sort);
        return ResponseEntity.success("排序修改完成");
    }

    @PostMapping("/show")
    @ApiOperation("设置是否显示")
    @Log(module = "栏目管理", businessType = BusinessType.UPDATE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "栏目id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isShow", value = "是否显示 1、显示 2、隐藏", required = true, paramType = "query", dataType = "Integer"),
    })
    public ResponseEntity isShow(@RequestBody @ApiIgnore JSONObject jsonObject) {
        String moduleId = jsonObject.getString("moduleId");
        Integer isShow = jsonObject.getInteger("isShow");
        moduleService.updateIsShow(moduleId, isShow);
        return ResponseEntity.success("显示状态完成");
    }


    @ApiOperation("删除栏目")
    @DeleteMapping("del/{id}")
    @Log(module = "栏目管理", businessType = BusinessType.DELETE)
    // 添加权限验证 @PreAuthorize("@ss.hasPermi('')")
    public ResponseEntity delete(@PathVariable(value = "id") @ApiParam(name = "id", value = "上级栏目id") String id) {
        if (moduleService.haveChild(id)) {
            return ResponseEntity.success("该栏目下有子栏目，请删除子栏目节点");
        }
        if (moduleService.haveTemplate(id)) {
            return ResponseEntity.success("该栏目下有内容，请先删除下面的内容");
        }
        moduleService.deleteModule(id);
        return ResponseEntity.success("删除成功");
    }

    private ResponseEntity validateParam(Module module, boolean isUpdate) {
        if (StringUtils.isBlank(module.getModuleName())) {
            return ResponseEntity.error("栏目名称为空");
        }
        if (StringUtils.isBlank(module.getRouter())) {
            return ResponseEntity.error("路由标识为空");
        }
        if (module.getTemplateType() > 6 || module.getTemplateType() < 1) {
            return ResponseEntity.error("模板类型错误");
        }
        if (module.getModuleType() > 2 || module.getModuleType() < 1) {
            return ResponseEntity.error("栏目节点类型错误");
        }
        if (module.getIsShow() > 2 || module.getIsShow() < 1) {
            return ResponseEntity.error("显示类型错误");
        }
        if (!isUpdate) {
            List<Integer> existCount = moduleService.selectExistModule(module.getModuleName(), module.getRouter());
            if (existCount.get(0) > 0) {
                return ResponseEntity.error("栏目名称已存在");
            }
            if (existCount.get(1) > 0 ) {
                return ResponseEntity.error("路由标识已存在");
            }
        }
        if (StringUtils.isNotBlank(module.getParentModuleId())) {
            Module parentModule = moduleService.selectById(module.getParentModuleId());
            if (parentModule == null) {
                return ResponseEntity.error("上级栏目不存在");
            }
            module.setParentModuleName(parentModule.getModuleName());
            module.setRouterStr(parentModule.getRouterStr() + "-" + module.getRouter());
        } else {
            module.setParentModuleId("");
        }
        return null;
    }

}
