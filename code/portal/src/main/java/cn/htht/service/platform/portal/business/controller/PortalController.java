package cn.htht.service.platform.portal.business.controller;

import cn.htht.service.platform.portal.common.bean.ResponseEntity;
import cn.htht.service.platform.portal.component.web.service.SysLoginService;
import cn.htht.service.platform.portal.component.web.service.TokenService;
import cn.htht.service.platform.portal.constant.Constants;
import cn.htht.service.platform.portal.constant.ModuleConstant;
import cn.htht.service.platform.portal.constant.UserConstants;
import cn.htht.service.platform.portal.constant.WeatherConstant;
import cn.htht.service.platform.portal.entity.dto.WeatherDto;
import cn.htht.service.platform.portal.entity.manager.Content;
import cn.htht.service.platform.portal.entity.manager.ListTab;
import cn.htht.service.platform.portal.entity.manager.Module;
import cn.htht.service.platform.portal.entity.manager.NormalTab;
import cn.htht.service.platform.portal.entity.manager.ServiceTemplate;
import cn.htht.service.platform.portal.entity.manager.TabContent;
import cn.htht.service.platform.portal.entity.manager.TabInfo;
import cn.htht.service.platform.portal.entity.system.LoginBody;
import cn.htht.service.platform.portal.entity.system.LoginUser;
import cn.htht.service.platform.portal.entity.system.SystemUser;
import cn.htht.service.platform.portal.manager.service.ContentService;
import cn.htht.service.platform.portal.manager.service.DataTemplateService;
import cn.htht.service.platform.portal.manager.service.InfoTemplateService;
import cn.htht.service.platform.portal.manager.service.ModuleService;
import cn.htht.service.platform.portal.business.service.PortalOverviewService;
import cn.htht.service.platform.portal.manager.service.ServiceNormalService;
import cn.htht.service.platform.portal.manager.service.ServiceTabInfoService;
import cn.htht.service.platform.portal.manager.service.ServiceTabListService;
import cn.htht.service.platform.portal.manager.service.ServiceTemplateService;
import cn.htht.service.platform.portal.system.service.ISystemUserService;
import cn.htht.service.platform.portal.utils.annotation.IsLogin;
import cn.htht.service.platform.portal.utils.utils.ServletUtils;
import cn.htht.service.platform.portal.utils.utils.StringUtils;
import cn.htht.service.platform.portal.utils.utils.ip.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName PortalController
 * @Description 首页接口controller
 * @Author Ken
 * @Date DATE{TIME}
 **/
@RestController
@Api(tags = "门户访问")
@RequestMapping("/portal")
public class PortalController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISystemUserService userService;

    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private PortalOverviewService portalOverviewService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private InfoTemplateService infoTemplateService;

    @Autowired
    private DataTemplateService dataTemplateService;

    @Autowired
    private ServiceTemplateService serviceTemplateService;

    @Autowired
    private ServiceNormalService serviceNormalService;

    @Autowired
    private ServiceTabListService serviceTabListService;

    @Autowired
    private ServiceTabInfoService serviceTabInfoService;

    @ApiOperation("门户首页")
    @GetMapping("index")
    public ResponseEntity index() {
        return ResponseEntity.success();
    }

    @ApiOperation("首页概览获取")
    @GetMapping("/overview")
    public ResponseEntity getPortalOverview(){
        return ResponseEntity.success(portalOverviewService.getPortalOverview());
    }

    @ApiOperation("注册用户")
    @PutMapping("register")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, paramType = "query"),
            @ApiImplicitParam(name = "realName", value = "真实姓名", required = true, paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, defaultValue = "", paramType = "query"),
            @ApiImplicitParam(name = "company", value = "单位", paramType = "query"),
            @ApiImplicitParam(name = "email", value = "邮箱", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "phoneNumber", value = "电话", required = true, paramType = "query")
    })
    public ResponseEntity register(@ApiIgnore @Validated @RequestBody SystemUser user) {
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUsername()))) {
            return ResponseEntity.error("注册用户'" + user.getUsername() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhoneNumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return ResponseEntity.error("注册用户'" + user.getUsername() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return ResponseEntity.error("注册用户'" + user.getUsername() + "'失败，邮箱账号已存在");
        }
        int i = userService.insertNormalUser(user);
        return (i > 0 ? ResponseEntity.success("注册成功") : ResponseEntity.error("注册失败"));
    }



    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", defaultValue = "", paramType = "query")
    })
    public ResponseEntity login(@RequestBody @ApiIgnore LoginBody loginBody) {
        ResponseEntity responseEntity = ResponseEntity.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid(), false);
        responseEntity.put(Constants.TOKEN, token);
        return responseEntity;
    }

    /**
     * 登录状态查询
     * @param request
     * @return
     */
    @GetMapping("/loginStatus")
    @ApiOperation("用户登录状态")
    public ResponseEntity logionStatus(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        ResponseEntity responseEntity = ResponseEntity.success();
        if (loginUser == null) {
            responseEntity.put("loginStatus", Boolean.FALSE);
        } else {
            responseEntity.put("loginStatus", Boolean.TRUE);
        }
        return  responseEntity;
    }

    @ApiOperation("获取栏目列表")
    @GetMapping("/module")
    public ResponseEntity module() {
        return ResponseEntity.success(moduleService.buildModuleTree("isShow"));
    }


    @ApiOperation("单页面内容查看")
    @GetMapping("module/single/page/{moduleId}")
    public ResponseEntity show(@PathVariable("moduleId") String moduleId) {
        return ResponseEntity.success(moduleService.getContent(moduleId));
    }

    @ApiOperation("信息面内容查看")
    @GetMapping("module/info/page/")
    public ResponseEntity show(@RequestParam(value = "moduleId") @ApiParam(name = "moduleId", value = "模块 ID") String moduleId, @RequestParam(value = "pageSize") @ApiParam(name = "pageSize", value = "每页多少条") Integer pageSize, @RequestParam(value = "pageNumber") @ApiParam(name = "pageNumber", value = "页码") Integer pageNumber) {
        Module module = moduleService.selectById(moduleId);
        if (module.getTemplateType() != ModuleConstant.INFO_TEMPLATE) {
            return ResponseEntity.error("页面模板类型错误");
        }
        PageInfo<Content> contentList = infoTemplateService.contentPageList(module, pageSize, pageNumber);
        return ResponseEntity.success(contentList);
    }

    @ApiOperation("数据业务页面内容查看")
    @GetMapping("module/data/page/{moduleId}")
    public ResponseEntity showDataPage(@PathVariable("moduleId") String moduleId) {
        Module module = moduleService.selectById(moduleId);
        if (module.getTemplateType() != ModuleConstant.DATA_TEMPLATE) {
            return ResponseEntity.error("页面模板类型错误");
        }
        return ResponseEntity.success(dataTemplateService.getDataTemplateByModuleId(moduleId));
    }

    @ApiOperation("数据业务页面内容数据列表查看")
    @GetMapping("module/data/page/list")
    public ResponseEntity showDataPageList(@RequestParam(value = "templateId") @ApiParam(name = "templateId", value = "模板ID") String templateId, @RequestParam(value = "pageSize") @ApiParam(name = "pageSize", value = "每页多少条") Integer pageSize, @RequestParam(value = "pageNumber") @ApiParam(name = "pageNumber", value = "页码") Integer pageNumber) {
        return ResponseEntity.success(dataTemplateService.getServiceDataListByTemplateId(templateId, pageSize, pageNumber));
    }

    @ApiOperation("业务页面信息查询")
    @GetMapping("module/service/{moduleId}")
    public ResponseEntity queryBusiness(@PathVariable("moduleId") String moduleId) {
        List<TabContent> tabContentList = new ArrayList<>();
        ServiceTemplate serviceTemplate = serviceTemplateService.getByModuleId(moduleId);
        if (serviceTemplate == null) {
            return ResponseEntity.success(null);
        }
        //普通页签列表
        List<NormalTab> normalTabList = serviceNormalService.getAsNormalTabByTemplateId(serviceTemplate.getId());
        //带listtab页签信息列表
        List<ListTab> listTabList = serviceTabListService.getAsListTabByTemplateId(serviceTemplate.getId());//-->id2

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

    @ApiOperation("获取内容详情")
    @GetMapping("/content/{contentId}")
    public ResponseEntity getContentDetails(@PathVariable("contentId") String contentId){
        return ResponseEntity.success(contentService.getContentDetails(contentId));
    }

    @ApiOperation("获取栏目的服务页面头部信息数据")
    @GetMapping("module/header/{moduleId}")
    private ResponseEntity getHeaderInfo(@PathVariable("moduleId") String moduleId, HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        String userId = "";
        if (loginUser != null) {
            userId = loginUser.getUserId();
        }
        return ResponseEntity.success(moduleService.getHeaderInfo(moduleId, StringUtils.isBlank(userId) ? "" : userId));
    }

    @ApiOperation("获取天气预报信息查询接口")
    @GetMapping("/weather")
    public ResponseEntity weather() {
        // 获取到访问者IP
        String ipAddress = IpUtils.getIpAddress(ServletUtils.getRequest());
        String address = AddressUtils.getRealAddressByIP(ipAddress);
        if (address.equals("内网IP")) {
            // 如果返回的是内网IP，则查找服务器IP对应外网IP
            ipAddress = AddressUtils.getIP();
            try {
                DbSearcher searcher = new DbSearcher(new DbConfig());
                searcher.btreeSearch(ipAddress);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (DbMakerConfigException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        DataBlock dataBlock = null;
        try {
            DbSearcher searcher = new DbSearcher(new DbConfig());
            dataBlock = searcher.btreeSearch(ipAddress);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DbMakerConfigException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String city = dataBlock.getRegion().split("\\|")[3];
        String cityName = city.substring(0, city.indexOf("市"));
        String cityCode = AddressUtils.getCityCode(cityName);
        Map<String, String> weather = AddressUtils.getWeather(cityCode);
        JSONArray weatherArr = JSONArray.parseArray(weather.get("temperatureList"));
        List<WeatherDto> weatherDtoList = switchWeatherJson(weatherArr, cityName, weather.get("currentTemperature"));
        return ResponseEntity.success(weatherDtoList);
    }

    private List<WeatherDto> switchWeatherJson(JSONArray weatherArr, String cityName, String currentTemperature) {
        List<WeatherDto> weatherDtoList = new ArrayList<>();
        for (int i = 0; i < weatherArr.size(); i++) {
            JSONObject jsonObject = weatherArr.getJSONObject(i);
            WeatherDto weatherDto = new WeatherDto();
            weatherDto.setCity(cityName);
            weatherDto.setCurrentTemperature(currentTemperature);
            weatherDto.setName(jsonObject.get("type").toString());
            weatherDto.setDate(jsonObject.get("ymd").toString().substring(5));
            String temperature = jsonObject.get("low").toString().substring(3) + "~" + jsonObject.get("high").toString().substring(3);
            weatherDto.setTemperature(temperature);
            weatherDto.setWeather(WeatherConstant.weatherMap.get(jsonObject.get("type").toString()));
            weatherDto.setWindAspect(jsonObject.get("fx").toString());
            weatherDto.setWindLevel(jsonObject.get("fl").toString());
            weatherDtoList.add(weatherDto);
            if (weatherDtoList.size() == 7) {
                break;
            }
        }
        return weatherDtoList;
    }
}
