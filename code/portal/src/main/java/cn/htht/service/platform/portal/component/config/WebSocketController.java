package cn.htht.service.platform.portal.component.config;

import cn.htht.service.platform.portal.common.bean.ResponseEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/9/26
 */
@Api(tags = "客服")
@RequestMapping("/websocket")
@RestController
public class WebSocketController {

    @ApiOperation("判断用户是否已建立连接")
    @GetMapping("exist/{username}")
    public ResponseEntity isExist(@PathVariable String username){
        for (Map.Entry<String, WebSocketServer> m : WebSocketServer.getClients().entrySet()){
            if (m.getKey().equals(username)){
                return ResponseEntity.success(true);
            }
        }
        for (Map.Entry<String, WebSocketServer> m : WebSocketServer.getClientsAdmin().entrySet()){
            if (m.getKey().equals(username)){
                return ResponseEntity.success(true);
            }
        }
        return ResponseEntity.success(false);
    }


}
