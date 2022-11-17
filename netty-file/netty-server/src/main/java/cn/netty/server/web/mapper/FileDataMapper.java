package cn.netty.server.web.mapper;

import cn.netty.server.web.entity.FileData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @ClassName FileDataMapper
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Component
@Mapper
public interface FileDataMapper extends BaseMapper<FileData> {
}
