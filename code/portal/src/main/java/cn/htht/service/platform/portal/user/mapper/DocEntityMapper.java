package cn.htht.service.platform.portal.user.mapper;

import cn.htht.service.platform.portal.entity.user.DocEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DocEntityMapper extends ElasticsearchRepository<DocEntity,String> {
}
