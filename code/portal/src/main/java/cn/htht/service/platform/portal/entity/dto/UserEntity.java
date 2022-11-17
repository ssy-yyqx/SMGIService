package cn.htht.service.platform.portal.entity.dto;

import cn.htht.service.platform.portal.entity.system.SystemUser;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @ClassName UserEntity
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
public class UserEntity extends SystemUser implements UserDetails {

    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    // 用户所有权限
    private List<GrantedAuthority> authorities = new ArrayList<>();

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
