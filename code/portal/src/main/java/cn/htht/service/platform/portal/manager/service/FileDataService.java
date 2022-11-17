package cn.htht.service.platform.portal.manager.service;

import cn.htht.service.platform.portal.common.BaseService;
import cn.htht.service.platform.portal.entity.manager.FileData;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public interface FileDataService extends BaseService<FileData> {
    /**
     * 上传文件
     * @param multipartFile 文件实体
     * @param fileType 文件类型
     * @param fileData 文件数据实体
     * @return
     */
    String upload(MultipartFile multipartFile, Integer fileType, FileData fileData);

    /**
     * 获取文件流
     * @param fileData 文件数据实体类
     * @return
     */
    void getFileStream(FileData fileData, HttpServletResponse response) throws IOException;

    /**
     * 下载文件
     * @param fileData 文件数据
     * @param request 请求头
     * @param response 响应头
     */
    void downloadFile(FileData fileData, HttpServletRequest request, HttpServletResponse response);

    /**
     * 删除文件
     * @param dataId 文件di
     */
    void deleteData(String dataId);
}
