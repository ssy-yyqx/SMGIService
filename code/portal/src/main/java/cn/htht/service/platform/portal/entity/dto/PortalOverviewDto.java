package cn.htht.service.platform.portal.entity.dto;

import cn.htht.service.platform.portal.entity.business.PortalOverview;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/12
 */
@Data
@ToString
@AllArgsConstructor
public class PortalOverviewDto {
    private String centerOverview;
    private List<PortalOverview> newList;
    private List<PortalOverview> noticeList;
    private List<PortalOverview> centerList;
}
