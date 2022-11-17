package cn.htht.service.platform.portal.entity.manager;

import lombok.Data;
import lombok.ToString;

/**
 * @ClassName NormalTab
 * @Description 普通形式页签
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
public class NormalTab extends TabContent {

    /**
     * 页签标题
     */
    private String tabName;

    /**
     * 页签内容
     */
    private String content;

    /**
     * 相关链接
     */
    private String referenceLink;
}
