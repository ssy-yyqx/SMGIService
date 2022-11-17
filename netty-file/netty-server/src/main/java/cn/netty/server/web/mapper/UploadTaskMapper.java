package cn.netty.server.web.mapper;

import cn.netty.server.web.entity.UploadTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @ClassName UploadTaskMapper.java
 * @Description 类描述
 * @Author YangZiJie
 * @Version 1.0.0
 * @Date 2022年07月25日
 */
@Mapper
@Component
public interface UploadTaskMapper extends BaseMapper<UploadTask> {
}
