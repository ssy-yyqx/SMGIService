package cn.htht.service.platform.portal.common;

import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName BaseEntity
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@ToString
public class BaseEntity {

    private String id;

    private Long createTime;

    private Long updateTime;

    private String remark;

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
