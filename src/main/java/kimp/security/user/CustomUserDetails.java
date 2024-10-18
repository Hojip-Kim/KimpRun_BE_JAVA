package kimp.security.user;

import kimp.user.dto.UserCopyDto;
import kimp.user.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final UserCopyDto userCopyDto;

    public CustomUserDetails(UserCopyDto userCopyDto) {
        this.userCopyDto = userCopyDto;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 설정
        UserRole role = userCopyDto.getRole();

        Collection<GrantedAuthority> authorities = new ArrayList<>();

        if(role == null){
            return Collections.emptyList();
        }

        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));

        return authorities;
    }

    public Long getId(){
        return userCopyDto.getId();
    }

    // user의 email이 null이면 null반환, 그렇지않으면 email 반환
    public String getEmail() {
        return userCopyDto.getEmail() == null ? null : userCopyDto.getEmail();
    }

    @Override
    public String getPassword() {
        return userCopyDto.getPassword();
    }

    @Override
    public String getUsername() {
        return userCopyDto.getLoginId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
