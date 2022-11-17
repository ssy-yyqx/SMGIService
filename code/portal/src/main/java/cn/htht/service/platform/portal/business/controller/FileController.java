package cn.htht.service.platform.portal.business.controller;

import cn.htht.service.platform.portal.common.BaseController;
import cn.htht.service.platform.portal.common.bean.ResponseEntity;
import cn.htht.service.platform.portal.component.web.service.TokenService;
import cn.htht.service.platform.portal.constant.ModuleConstant;
import cn.htht.service.platform.portal.entity.manager.FileData;
import cn.htht.service.platform.portal.entity.system.SystemUser;
import cn.htht.service.platform.portal.manager.service.FileDataService;
import cn.htht.service.platform.portal.manager.service.impl.FileDataServiceImpl;
import cn.htht.service.platform.portal.utils.annotation.IsLogin;
import cn.htht.service.platform.portal.utils.annotation.Log;
import cn.htht.service.platform.portal.utils.enums.BusinessType;
import cn.htht.service.platform.portal.utils.utils.MinioClientUtils;
import cn.htht.service.platform.portal.utils.utils.StringUtils;
import cn.htht.service.platform.portal.utils.utils.uuid.IdUtils;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName FileController
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@RestController
@RequestMapping("file")
@Api(tags = "文件管理接口")
public class FileController extends BaseController {

    @Value("${file.show.url.pic}")
    private String accessUrl;

    @Autowired
    private FileDataService fileDataService;

    @Autowired
    private TokenService tokenService;

    @ApiOperation("图片查看")
    @GetMapping("/show/{id}")
    public void showPicture(@ApiParam(name = "id", value = "文件id") @PathVariable(value = "id") String id, HttpServletResponse response) {
        FileData fileData = fileDataService.selectById(id);
        try {
            //从minio获取到文件流
            fileDataService.getFileStream(fileData, response);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation("文件下载")
    @GetMapping("/download/{id}")
    public void download(@PathVariable("id") @ApiParam(name = "id", value = "文件id") String id, HttpServletRequest request, HttpServletResponse response) {
        FileData fileData = fileDataService.selectById(id);
        fileDataService.downloadFile(fileData, request, response);
    }

    /**
     * 图片上传接口
     *
     * @param multipartFile
     * @return
     */
    @RequestMapping(value = "/upload/pic", method = RequestMethod.POST)
    @ApiOperation("图片上传")
    @ResponseBody
    public Map<String, String> uploadPic(@ApiParam(name = "upload", value = "图片文件")@RequestParam(name = "upload") MultipartFile multipartFile, @ApiParam(name = "ckCsrfToken", value = "图片token") @RequestParam(name = "ckCsrfToken", required = false) String ckCsrfToken, HttpServletRequest request) {
        Map<String, String> resultMap = Maps.newConcurrentMap();
        if (multipartFile.getSize() > 500 * 1024 * 1024) {
            resultMap.put("error", "文件超过500MB，暂不支持");
            return resultMap;
        }
        FileData fileData = new FileData();
        fileData.setId(IdUtils.fastSimpleUUID());
        fileData.setFileType(ModuleConstant.FILE_TYPE_PICTURE);
        fileData.setCreateTime(System.currentTimeMillis());
        fileData.setCreateBy(ckCsrfToken);
        String uploadPath = fileDataService.upload(multipartFile, ModuleConstant.FILE_TYPE_PICTURE, fileData);
        if (!uploadPath.contains("success")) {
            resultMap.put("error", uploadPath);
            return resultMap;
        }
        resultMap.put("uploaded", "1");
        resultMap.put("url", accessUrl + fileData.getId());
        return resultMap;
    }

}
