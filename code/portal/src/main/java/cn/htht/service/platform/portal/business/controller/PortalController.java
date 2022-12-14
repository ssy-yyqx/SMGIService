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
 * @Description ????????????controller
 * @Author Ken
 * @Date DATE{TIME}
 **/
@RestController
@Api(tags = "????????????")
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

    @ApiOperation("????????????")
    @GetMapping("index")
    public ResponseEntity index() {
        return ResponseEntity.success();
    }

    @ApiOperation("??????????????????")
    @GetMapping("/overview")
    public ResponseEntity getPortalOverview(){
        return ResponseEntity.success(portalOverviewService.getPortalOverview());
    }

    @ApiOperation("????????????")
    @PutMapping("register")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "?????????", required = true, paramType = "query"),
            @ApiImplicitParam(name = "realName", value = "????????????", required = true, paramType = "query"),
            @ApiImplicitParam(name = "password", value = "??????", required = true, defaultValue = "", paramType = "query"),
            @ApiImplicitParam(name = "company", value = "??????", paramType = "query"),
            @ApiImplicitParam(name = "email", value = "??????", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "phoneNumber", value = "??????", required = true, paramType = "query")
    })
    public ResponseEntity register(@ApiIgnore @Validated @RequestBody SystemUser user) {
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUsername()))) {
            return ResponseEntity.error("????????????'" + user.getUsername() + "'??????????????????????????????");
        } else if (StringUtils.isNotEmpty(user.getPhoneNumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return ResponseEntity.error("????????????'" + user.getUsername() + "'??????????????????????????????");
        } else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return ResponseEntity.error("????????????'" + user.getUsername() + "'??????????????????????????????");
        }
        int i = userService.insertNormalUser(user);
        return (i > 0 ? ResponseEntity.success("????????????") : ResponseEntity.error("????????????"));
    }



    /**
     * ????????????
     *
     * @param loginBody ????????????
     * @return ??????
     */
    @PostMapping("/login")
    @ApiOperation("????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "?????????", required = true, paramType = "query"),
            @ApiImplicitParam(name = "password", value = "??????", defaultValue = "", paramType = "query")
    })
    public ResponseEntity login(@RequestBody @ApiIgnore LoginBody loginBody) {
        ResponseEntity responseEntity = ResponseEntity.success();
        // ????????????
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid(), false);
        responseEntity.put(Constants.TOKEN, token);
        return responseEntity;
    }

    /**
     * ??????????????????
     * @param request
     * @return
     */
    @GetMapping("/loginStatus")
    @ApiOperation("??????????????????")
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

    @ApiOperation("??????????????????")
    @GetMapping("/module")
    public ResponseEntity module() {
        return ResponseEntity.success(moduleService.buildModuleTree("isShow"));
    }


    @ApiOperation("?????????????????????")
    @GetMapping("module/single/page/{moduleId}")
    public ResponseEntity show(@PathVariable("moduleId") String moduleId) {
        return ResponseEntity.success(moduleService.getContent(moduleId));
    }

    @ApiOperation("?????????????????????")
    @GetMapping("module/info/page/")
    public ResponseEntity show(@RequestParam(value = "moduleId") @ApiParam(name = "moduleId", value = "?????? ID") String moduleId, @RequestParam(value = "pageSize") @ApiParam(name = "pageSize", value = "???????????????") Integer pageSize, @RequestParam(value = "pageNumber") @ApiParam(name = "pageNumber", value = "??????") Integer pageNumber) {
        Module module = moduleService.selectById(moduleId);
        if (module.getTemplateType() != ModuleConstant.INFO_TEMPLATE) {
            return ResponseEntity.error("????????????????????????");
        }
        PageInfo<Content> contentList = infoTemplateService.contentPageList(module, pageSize, pageNumber);
        return ResponseEntity.success(contentList);
    }

    @ApiOperation("??????????????????????????????")
    @GetMapping("module/data/page/{moduleId}")
    public ResponseEntity showDataPage(@PathVariable("moduleId") String moduleId) {
        Module module = moduleService.selectById(moduleId);
        if (module.getTemplateType() != ModuleConstant.DATA_TEMPLATE) {
            return ResponseEntity.error("????????????????????????");
        }
        return ResponseEntity.success(dataTemplateService.getDataTemplateByModuleId(moduleId));
    }

    @ApiOperation("??????????????????????????????????????????")
    @GetMapping("module/data/page/list")
    public ResponseEntity showDataPageList(@RequestParam(value = "templateId") @ApiParam(name = "templateId", value = "??????ID") String templateId, @RequestParam(value = "pageSize") @ApiParam(name = "pageSize", value = "???????????????") Integer pageSize, @RequestParam(value = "pageNumber") @ApiParam(name = "pageNumber", value = "??????") Integer pageNumber) {
        return ResponseEntity.success(dataTemplateService.getServiceDataListByTemplateId(templateId, pageSize, pageNumber));
    }

    @ApiOperation("????????????????????????")
    @GetMapping("module/service/{moduleId}")
    public ResponseEntity queryBusiness(@PathVariable("moduleId") String moduleId) {
        List<TabContent> tabContentList = new ArrayList<>();
        ServiceTemplate serviceTemplate = serviceTemplateService.getByModuleId(moduleId);
        if (serviceTemplate == null) {
            return ResponseEntity.success(null);
        }
        //??????????????????
        List<NormalTab> normalTabList = serviceNormalService.getAsNormalTabByTemplateId(serviceTemplate.getId());
        //???listtab??????????????????
        List<ListTab> listTabList = serviceTabListService.getAsListTabByTemplateId(serviceTemplate.getId());//-->id2

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

    @ApiOperation("??????????????????")
    @GetMapping("/content/{contentId}")
    public ResponseEntity getContentDetails(@PathVariable("contentId") String contentId){
        return ResponseEntity.success(contentService.getContentDetails(contentId));
    }

    @ApiOperation("?????????????????????????????????????????????")
    @GetMapping("module/header/{moduleId}")
    private ResponseEntity getHeaderInfo(@PathVariable("moduleId") String moduleId, HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        String userId = "";
        if (loginUser != null) {
            userId = loginUser.getUserId();
        }
        return ResponseEntity.success(moduleService.getHeaderInfo(moduleId, StringUtils.isBlank(userId) ? "" : userId));
    }

    @ApiOperation("????????????????????????????????????")
    @GetMapping("/weather")
    public ResponseEntity weather() {
        // ??????????????????IP
        String ipAddress = IpUtils.getIpAddress(ServletUtils.getRequest());
        String address = AddressUtils.getRealAddressByIP(ipAddress);
        if (address.equals("??????IP")) {
            // ????????????????????????IP?????????????????????IP????????????IP
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
        String cityName = city.substring(0, city.indexOf("???"));
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
