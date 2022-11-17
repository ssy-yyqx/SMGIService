package cn.htht.service.platform.portal.entity.system;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Id;

/**
 * @ClassName SystemPermissionKey
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
public class SystemPermissionKey {

    @Id
    private String id;
    /**
     * 权限key
     */
    private String permKey;

    /**
     * 路由（保留字段）
     */
    private String router;

}
