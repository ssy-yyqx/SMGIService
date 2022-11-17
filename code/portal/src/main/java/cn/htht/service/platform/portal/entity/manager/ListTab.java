package cn.htht.service.platform.portal.entity.manager;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Transient;
import java.util.List;

/**
 * @ClassName ListTab
 * @Description 列表形式标签
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
public class ListTab extends TabContent {

    /**
     * 页签标题
     */
    private String tabName;

    @Transient
    private List<TabInfo> tabInfoList;
}
