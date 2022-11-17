package cn.htht.service.platform.portal.entity.business;

import cn.htht.service.platform.portal.common.BaseServiceEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PortalOverview extends BaseServiceEntity {
    /**
     * 对应模板路由标识
     * */
    private String router;
    /**
     * 标题
     * */
    private String title;
    /**
     * 页面缩略图
     */
    private String imageUrl;
    /**
     * 模块名称
     * */
    private String moduleName;
}
