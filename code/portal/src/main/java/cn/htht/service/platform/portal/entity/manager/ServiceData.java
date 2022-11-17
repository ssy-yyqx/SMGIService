package cn.htht.service.platform.portal.entity.manager;

import cn.htht.service.platform.portal.common.BaseServiceEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Table;

/**
 * @ClassName ServiceData
 * @Description 数据服务实体类
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
@Table(name = "t_service_data")
public class ServiceData extends BaseServiceEntity {
    /**
     * 服务模板id
     */
    private String dataTemplateId;

    /**
     * 拇指图路径
     */
    private String thumbImageId;

    /**
     * 数据包路径
     */
    private String dataId;

    /**
     * 数据大小（MB，KB，GB)
     */
    private String dataSize;

    /**
     * 数据名称
     */
    private String dataName;

    /**
     * 数据阐述
     */
    private String describe;
}
