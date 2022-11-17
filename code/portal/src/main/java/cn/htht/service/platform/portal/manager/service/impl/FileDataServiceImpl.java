package cn.htht.service.platform.portal.manager.service.impl;

import cn.htht.service.platform.portal.common.AbstractBaseService;
import cn.htht.service.platform.portal.entity.manager.FileData;
import cn.htht.service.platform.portal.manager.mapper.FileDataMapper;
import cn.htht.service.platform.portal.manager.service.FileDataService;
import cn.htht.service.platform.portal.utils.utils.DownloadUtils;
import cn.htht.service.platform.portal.utils.utils.MinioClientUtils;
import cn.htht.service.platform.portal.utils.utils.file.FileUtils;
import cn.htht.service.platform.portal.utils.utils.uuid.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName FileDataServiceImpl
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Service
public class FileDataServiceImpl extends AbstractBaseService<FileData> implements FileDataService {


    private final FileDataMapper fileDataMapper;

    private final MinioClientUtils minioClientUtils;

    @Autowired
    public FileDataServiceImpl(FileDataMapper fileDataMapper, MinioClientUtils minioClientUtils) {
        this.fileDataMapper = fileDataMapper;
        this.minioClientUtils = minioClientUtils;
    }

    public final static Map<Integer, String> BUCKET_NAME_MAP = new HashMap<>();
    static {
        BUCKET_NAME_MAP.put(1, "picture");
        BUCKET_NAME_MAP.put(2, "geomap");
        BUCKET_NAME_MAP.put(3, "word");
        BUCKET_NAME_MAP.put(4, "program");
        BUCKET_NAME_MAP.put(5, "other");
    }

    @Override
    public String upload(MultipartFile multipartFile, Integer fileType, FileData fileData) {
        String fileName = multipartFile.getOriginalFilename();
        String bucketName = BUCKET_NAME_MAP.get(fileType);
        String objectName = fileData.getId() + File.separator + IdUtils.fastSimpleUUID() + "." + FileUtils.suffix(fileName);
        try {
            InputStream inputStream = multipartFile.getInputStream();
            minioClientUtils.uploadMinio(bucketName, objectName, inputStream, multipartFile.getContentType());
        } catch (IOException e) {
            e.printStackTrace();
            return "上传minio出现问题：" + e.getMessage();
        }
        fileData.setExt(FileUtils.suffix(fileName));
        fileData.setFileName(fileName);
        fileData.setFileType(fileType);
        fileData.setBucketName(bucketName);
        fileData.setObjectName(objectName);
        fileDataMapper.insertFile(fileData);
        return "success:" + fileData.getId();
    }

    @Override
    public void getFileStream(FileData fileData, HttpServletResponse response) throws IOException {
        minioClientUtils.showPicture(fileData.getObjectName(), fileData.getBucketName(), response);
    }

    @Override
    public void downloadFile(FileData fileData, HttpServletRequest request, HttpServletResponse response) {
        InputStream stream = minioClientUtils.downloadFile(fileData.getBucketName(), fileData.getObjectName());
        DownloadUtils downloadUtils = new DownloadUtils();
        downloadUtils.downloadFileStream(stream, fileData.getFileName(), request, response);
    }

    @Override
    public void deleteData(String dataId) {
        FileData fileData = fileDataMapper.selectByPrimaryKey(dataId);
        if (fileData == null) {
            return;
        }
        minioClientUtils.deletePartFile(fileData.getBucketName(), fileData.getObjectName());
        fileDataMapper.deleteFileById(dataId);
    }

}
