package cn.htht.service.platform.portal.manager.mapper;

import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.manager.FileData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileDataMapper extends BasicMapper<FileData> {

    /**
     * 插入数据
     * */
    int insertFile(FileData attachmentFile);

    /**
     * 查询数据
     * */
    List<FileData> selectAllFile(FileData attachmentFile);

    /**
     * 删除数据
     * */
    int deleteFileById(String id);

    /**
     * 批量删除数据
     * */
    int deleteFileByIds(String[] ids);

    /**
     * 更新数据
     * */
    int updateFile(FileData attachmentFile);
}
