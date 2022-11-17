package cn.htht.service.platform.portal.common;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Id;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName BaseServiceEntity
 * @Description 业务公共实体类
 * @Author Ken
 * @Date DATE{TIME}
 **/
@ToString
public class BaseServiceEntity {
    /**
     * 主键
     */
    @Id
    private String id;

    /**
     * 检索关键字
     */
    private String searchKey;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long updateTime;

    private String remark;

    /**
     * 修改人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 请求参数
     */
    private Map<String, Object> params;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Map<String, Object> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
