package cn.netty.server.web.service;

import cn.netty.server.web.entity.FileData;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface FileDataService extends IService<FileData> {

    Map<String, List<FileData>> listFile(String fileName);

    /**
     * 判断是否存在相同的md5
     * @param identifier
     * @return
     */
    boolean isFileDataExist(String identifier);

    /**
     * 根据MD5查询数据
     * @param identifier
     * @return
     */
    FileData getFileDataByMd5(String identifier);
}
