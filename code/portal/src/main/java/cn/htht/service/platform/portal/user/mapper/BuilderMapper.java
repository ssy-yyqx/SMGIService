package cn.htht.service.platform.portal.user.mapper;

import cn.htht.service.platform.portal.entity.user.Builder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BuilderMapper extends ElasticsearchRepository<Builder,Long> {
}
