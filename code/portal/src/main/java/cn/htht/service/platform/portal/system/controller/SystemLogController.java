package cn.htht.service.platform.portal.system.controller;

import cn.htht.service.platform.portal.common.BaseController;
import cn.htht.service.platform.portal.common.bean.ResponseEntity;
import cn.htht.service.platform.portal.entity.system.SystemLog;
import cn.htht.service.platform.portal.system.service.ISystemLogService;
import cn.htht.service.platform.portal.utils.annotation.IsLogin;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/19
 */
@Api(tags = "日志管理")
@RestController
@RequestMapping("/system/log")
public class SystemLogController extends BaseController {
    @Autowired
    ISystemLogService iSystemLogService;

    @PostMapping("/insert")
    @IsLogin
    public ResponseEntity insertLog(@RequestBody SystemLog systemLog){
        return ResponseEntity.success(iSystemLogService.insertSystemLog(systemLog));
    }


    @ApiOperation("查询日志")
    @GetMapping("/list")
    @IsLogin
    public ResponseEntity selectLogList(@RequestParam(value = "pageSize") @ApiParam(name = "pageSize", value = "每页多少条") Integer pageSize, @RequestParam(value = "pageNumber") @ApiParam(name = "pageNumber", value = "页码") Integer pageNumber, @RequestParam(value = "keyword", required = false) @ApiParam(name = "keyword", value = "关键词") String keyword){
        PageInfo<SystemLog> systemLogList = iSystemLogService.selectSystemLogList(pageSize, pageNumber, keyword);
        return ResponseEntity.success(systemLogList);
    }

    @ApiOperation("根据id查询日志")
    @GetMapping("/select/{id}")
    @IsLogin
    public ResponseEntity selectLogById(@PathVariable String id) {
        return ResponseEntity.success(iSystemLogService.selectSystemLogById(id));
    }

}
