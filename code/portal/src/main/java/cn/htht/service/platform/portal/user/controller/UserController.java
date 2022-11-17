package cn.htht.service.platform.portal.user.controller;

import cn.htht.service.platform.portal.common.BaseController;
import cn.htht.service.platform.portal.common.bean.ResponseEntity;
import cn.htht.service.platform.portal.component.web.service.PermissionService;
import cn.htht.service.platform.portal.component.web.service.TokenService;
import cn.htht.service.platform.portal.entity.manager.Module;
import cn.htht.service.platform.portal.entity.system.LoginUser;
import cn.htht.service.platform.portal.entity.system.SystemUser;
import cn.htht.service.platform.portal.entity.user.UserAppointment;
import cn.htht.service.platform.portal.entity.user.UserCollection;
import cn.htht.service.platform.portal.entity.user.UserSupport;
import cn.htht.service.platform.portal.manager.mapper.ModuleMapper;
import cn.htht.service.platform.portal.system.service.ISystemUserService;
import cn.htht.service.platform.portal.user.service.UserAppointmentService;
import cn.htht.service.platform.portal.user.service.UserCollectionService;
import cn.htht.service.platform.portal.user.service.UserOnlineDealRecordService;
import cn.htht.service.platform.portal.user.service.UserSupportService;
import cn.htht.service.platform.portal.utils.annotation.IsLogin;
import cn.htht.service.platform.portal.utils.annotation.Log;
import cn.htht.service.platform.portal.utils.enums.BusinessType;
import cn.htht.service.platform.portal.utils.utils.SecurityUtils;
import cn.htht.service.platform.portal.utils.utils.ServletUtils;
import cn.htht.service.platform.portal.utils.utils.uuid.IdUtils;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName UserController
 * @Description 用户个人中心
 * @Author Ken
 * @Date DATE{TIME}
 **/
@RestController
@Api(tags = "用户个人中心")
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserCollectionService userCollectionService;

    @Autowired
    private UserSupportService userSupportService;

    @Autowired
    private UserOnlineDealRecordService userOnlineDealRecordService;

    @Autowired
    private UserAppointmentService userAppointmentService;

    @Autowired
    private ISystemUserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ModuleMapper moduleMapper;

    @Autowired
    private PermissionService permissionService;

    @ApiOperation("用户信息")
    @IsLogin
    @GetMapping("info")
    public ResponseEntity info(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SystemUser systemUser = userService.selectUserById(loginUser.getUserId());
        return ResponseEntity.success(systemUser);
    }

    @IsLogin
    @ApiOperation("更新用户信息")
    @Log(module = "用户个人中心", businessType = BusinessType.UPDATE)
    @PostMapping(value = "updateinfo")
    public ResponseEntity updateUserInfo(@RequestBody SystemUser systemUser) {
        return ResponseEntity.success(userService.updateUser(systemUser));
    }

    @ApiOperation("旧密码验证")
    @IsLogin
    @PostMapping(value = "checkpwd")
    public ResponseEntity checkPwd(@RequestBody JSONObject jsonObject,
                                   HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);

        String pwd = jsonObject.getString("password");
        boolean flag = SecurityUtils.matchesPassword(pwd, loginUser.getPassword());

        if (flag) {
            return ResponseEntity.success("旧密码验证成功！", flag);
        }
        return ResponseEntity.success("旧密码验证失败！", flag);
    }

    @IsLogin
    @ApiOperation("重置用户密码")
    @Log(module = "用户个人中心", businessType = BusinessType.UPDATE)
    @PostMapping("updatepwdinfo")
    public ResponseEntity updateUserInfo(@RequestBody HashMap data) {
        String username = (String) data.get("userid");
        String pwd = SecurityUtils.encryptPassword((String) data.get("passworld"));
        return ResponseEntity.success(userService.resetUserPwd(username, pwd));
    }

    @ApiOperation("在线办理列表")
    @IsLogin
    @GetMapping("onlineDeal/list")
    public ResponseEntity onlineDealList(HttpServletRequest request) {
        return ResponseEntity.success(userOnlineDealRecordService.getListByUserId(tokenService.getLoginUser(request).getUserId()));
    }

    @ApiOperation("申请预约列表")
    @GetMapping("appointment/list")
    @IsLogin
    public ResponseEntity getAppointmentList(@RequestParam(value = "keyword", required = false) @ApiParam(name = "keyword", value = "关键字") String keyword, @RequestParam(value = "status", required = false) @ApiParam(name = "status", value = "状态") Integer status, @RequestParam(value = "pageSize") @ApiParam(name = "pageSize", value = "每页多少条") Integer pageSize, @RequestParam(value = "pageNumber") @ApiParam(name = "pageNumber", value = "页码") Integer pageNumber) {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        return ResponseEntity.success(userAppointmentService.getAppointmentList(keyword, status, loginUser.getUserId(), pageSize, pageNumber));
    }

    @ApiOperation("我的审批列表")
    @GetMapping("examinet/list")
    @IsLogin
    //TODO 权限添加之后启用权限认证
    //@PreAuthorize("@ss.hasPermi('system:config:list')")
    public ResponseEntity getExaminetList(@RequestParam(value = "keyword", required = false) @ApiParam(name = "keyword", value = "关键字") String keyword, @RequestParam(value = "status", required = false) @ApiParam(name = "status", value = "状态") Integer status, @RequestParam(value = "pageSize") @ApiParam(name = "pageSize", value = "每页多少条") Integer pageSize, @RequestParam(value = "pageNumber") @ApiParam(name = "pageNumber", value = "页码") Integer pageNumber) {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        PageInfo<UserAppointment> getAppointmentList = userAppointmentService.getAppointmentList(keyword, status, loginUser.getUserId(), pageSize, pageNumber);
        return ResponseEntity.success(getAppointmentList);
    }

    @ApiOperation("审批通过状态操作")
    @PostMapping("appointment/updatepass")
    @Log(module = "用户个人中心", businessType = BusinessType.UPDATE)
    @IsLogin
    //TODO 权限添加之后启用权限认证
    //@PreAuthorize("@ss.hasPermi('system:config:list')")
    public ResponseEntity updateExaminetPassState(@RequestBody JSONObject data) {
        String appointmentId = data.getString("appointmentId");
        String detail = data.getString("detail");
        if (userAppointmentService.getAppointmentByaAppointmentId(appointmentId).getStatus().equals(1)) {
            return ResponseEntity.success(userAppointmentService.updatePassState(appointmentId, detail));
        }
        return ResponseEntity.success("该条记录已经审批！");
    }

    @ApiOperation("审批不通过状态操作")
    @PostMapping("appointment/updatenotpass")
    @Log(module = "用户个人中心", businessType = BusinessType.UPDATE)
    @IsLogin
    //TODO 权限添加之后启用权限认证
    //@PreAuthorize("@ss.hasPermi('system:config:list')")
    public ResponseEntity updateExaminetNotPassState(@RequestBody JSONObject data) {
        String appointmentId = data.getString("appointmentId");
        String detail = data.getString("detail");
        if (userAppointmentService.getAppointmentByaAppointmentId(appointmentId).getStatus().equals(1)) {
            return ResponseEntity.success(userAppointmentService.updateNotPassState(appointmentId, detail));
        }
        return ResponseEntity.success("该条记录已经审批！");
    }
    //TODO 权限添加之后打开一下代码
    //@ApiOperation("判断该用户是否具有审批权限")
    //@GetMapping("haspermi")
    //@IsLogin
    //public ResponseEntity hasDecideHasPermiOrNot() {
    //return ResponseEntity.success(permissionService.hasPermi("system:config:list"));
    //}

    @ApiOperation("撤销预约操作")
    @Log(module = "用户个人中心", businessType = BusinessType.UPDATE)
    @PutMapping("appointment/cancel/{appointmentId}")
    @IsLogin
    public ResponseEntity cancelAppointment(@PathVariable String appointmentId) {
        if (!userAppointmentService.getAppointmentByaAppointmentId(appointmentId).getStatus().equals(1)) {
            return ResponseEntity.error("申请当前状态不可撤销");
        }
        return ResponseEntity.success(userAppointmentService.cancelAppointment(appointmentId));
    }

    @ApiOperation("申请预约")
    @Log(module = "用户个人中心", businessType = BusinessType.INSERT)
    @PostMapping("appointment/appointment")
    @Transactional
    @IsLogin
    public ResponseEntity addAppointment(@RequestBody UserAppointment userAppointment) {
        Module module = moduleMapper.getById(userAppointment.getModuleId());
        if (module == null) {
            return ResponseEntity.error("模块不正确");
        }
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        userAppointment.setId(IdUtils.fastSimpleUUID());
        userAppointment.setApplyUserId(loginUser.getUserId());
        userAppointment.setApplyUsername(loginUser.getUsername());
        userAppointment.setModuleName(module.getModuleName());
        userAppointment.setSequenceCode(userAppointmentService.getMaxSequenceCode());
        return ResponseEntity.success(userAppointmentService.addAppointment(userAppointment));
    }

    @IsLogin
    @ApiOperation("用户收藏列表")
    @PostMapping("collection/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keys", value = "搜索关键字", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页容量", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, paramType = "query")
    })
    public ResponseEntity collectionList(@RequestBody @ApiIgnore JSONObject jsonObject) {
        PageHelper.startPage(jsonObject.getInteger("pageNumber"), jsonObject.getInteger("pageSize"));
        UserCollection userCollection = new UserCollection();
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        userCollection.setUserId(loginUser.getUserId());
        userCollection.setName(jsonObject.getString("keys"));
        PageInfo<UserCollection> pageInfo = new PageInfo(userCollectionService.getListByUser(userCollection));
        return ResponseEntity.success(pageInfo);
    }

    @IsLogin
    @ApiOperation("用户点赞列表")
    @PostMapping("support/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keys", value = "搜索关键字", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页容量", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, paramType = "query")
    })
    public ResponseEntity supportList(@RequestBody @ApiIgnore JSONObject jsonObject) {
        PageHelper.startPage(jsonObject.getInteger("pageNumber"), jsonObject.getInteger("pageSize"));
        UserSupport userSupport = new UserSupport();
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        userSupport.setUserId(loginUser.getUserId());
        userSupport.setName(jsonObject.getString("keys"));
        List<UserSupport> list = userSupportService.getListByUser(userSupport);
        PageInfo<UserSupport> pageInfo = new PageInfo(list);
        return ResponseEntity.success(pageInfo);
    }

}
