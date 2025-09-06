package kimp.security.user;

import kimp.user.dto.UserCopyDto;
import kimp.user.enums.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

@Getter
public class CustomUserDetails implements UserDetails, OAuth2User {

    private final Long memberId;
    private final String username;
    private final String email;
    private final String password;
    private final Set<GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    public CustomUserDetails(UserCopyDto memberCopyDto) {
        this(memberCopyDto, null);
    }
    
    public CustomUserDetails(UserCopyDto memberCopyDto, Map<String, Object> attributes) {
        this.memberId = memberCopyDto.getId();
        this.username = memberCopyDto.getNickname();
        this.email = memberCopyDto.getEmail();
        this.password = memberCopyDto.getPassword();
        UserRole role = memberCopyDto.getRole();
        String roleName = (role == null) ? "USER" : role.name();
        this.authorities = Collections.unmodifiableSet(
                new HashSet<>(Collections.singletonList(new SimpleGrantedAuthority(roleName)))
        );
        this.attributes = (attributes == null) ? null : Collections.unmodifiableMap(new HashMap<>(attributes));
    }
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId(){
        return this.memberId;
    }


    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
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

    @Override
    public String getName() {
        return this.username;
    }
}
