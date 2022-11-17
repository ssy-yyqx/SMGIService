package cn.htht.service.platform.portal.entity.manager;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Id;

/**
 * @ClassName TabInfo
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
public class TabInfo {

    @Id
    private String id;

    /**
     * 页签id
     */
    private String tabId;

    /**
     * 内容标题
     */
    private String titleName;

    /**
     * 内容具体信息
     */
    private String infoContent;

}
