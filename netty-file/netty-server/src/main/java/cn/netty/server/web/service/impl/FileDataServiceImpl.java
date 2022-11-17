package cn.netty.server.web.service.impl;


import cn.netty.server.utils.MapUtils;
import cn.netty.server.utils.MinioClientUtils;
import cn.netty.server.web.entity.FileData;
import cn.netty.server.web.mapper.FileDataMapper;
import cn.netty.server.web.service.FileDataService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName FileDataServiceImpl
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Service
public class FileDataServiceImpl extends ServiceImpl<FileDataMapper, FileData> implements FileDataService {

    private final FileDataMapper fileDataMapper;
    private final MinioClientUtils minioClientUtils;

    public FileDataServiceImpl(FileDataMapper fileDataMapper, MinioClientUtils minioClientUtils) {
        this.fileDataMapper = fileDataMapper;
        this.minioClientUtils = minioClientUtils;
    }

    public boolean isFileDataExist(String identifier) {
        QueryWrapper<FileData> fileDataQueryWrapper = new QueryWrapper<>();
        fileDataQueryWrapper.eq("md5", identifier);
        return fileDataMapper.selectCount(fileDataQueryWrapper) > 0;
    }

    public FileData getFileDataByMd5(String identifier) {
        QueryWrapper<FileData> fileDataQueryWrapper = new QueryWrapper<>();
        fileDataQueryWrapper.eq("md5", identifier);
        return fileDataMapper.selectOne(fileDataQueryWrapper);
    }


    @Override
    public Map<String, List<FileData>> listFile(String fileName) {
        Map<String, List<FileData>> fileList = new HashMap<>();
        QueryWrapper<FileData> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("file_name", fileName);
        List<FileData> fileDataList = fileDataMapper.selectList(queryWrapper);
        for (FileData fd : fileDataList) {
            MapUtils<String, FileData> mapUtils = new MapUtils<>();
            mapUtils.setMapList(fileList, fd.getBucketName(), fd);
        }
        return fileList;
    }



}
