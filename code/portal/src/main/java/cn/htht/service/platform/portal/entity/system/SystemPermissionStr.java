package cn.htht.service.platform.portal.entity.system;

import cn.htht.service.platform.portal.common.BaseEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName SystemMenu
 * @Description 系统菜单表
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
public class SystemPermissionStr {


    /**
     * id
     */
    @Id
    private String id;

    /**
     * 菜单名称
     */
    private String permName;

    @Transient
    private List<SystemPermissionKey> systemPermissionKeyList;

}
