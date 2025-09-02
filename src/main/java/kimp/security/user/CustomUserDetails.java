package kimp.security.user;

import kimp.user.dao.MemberDao;
import kimp.user.dto.UserCopyDto;
import kimp.user.entity.Member;
import kimp.user.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomUserDetails implements UserDetails, OAuth2User {

    private final UserCopyDto memberCopyDto;
    private Map<String, Object> attributes;
    private final MemberDao memberDao;

    public CustomUserDetails(UserCopyDto memberCopyDto, MemberDao memberDao) {
        this.memberCopyDto = memberCopyDto;
        this.memberDao = memberDao;
    }
    
    public CustomUserDetails(UserCopyDto memberCopyDto, Map<String, Object> attributes, MemberDao memberDao) {
        this.memberCopyDto = memberCopyDto;
        this.attributes = attributes;
        this.memberDao = memberDao;
    }
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 설정
        UserRole role = memberCopyDto.getRole();

        Collection<GrantedAuthority> authorities = new ArrayList<>();

        if(role == null){
            return Collections.emptyList();
        }

        authorities.add(new SimpleGrantedAuthority(role.name()));

        return authorities;
    }

    public UserRole getRole() {
        return memberCopyDto.getRole();
    }

    public Long getId(){
        return memberCopyDto.getId();
    }

    // member의 email이 null이면 null반환, 그렇지않으면 email 반환
    public String getEmail() {
        return memberCopyDto.getEmail() == null ? null : memberCopyDto.getEmail();
    }

    @Override
    public String getPassword() {
        return memberCopyDto.getPassword();
    }

    @Override
    public String getUsername() {
        if (memberDao != null) {
            Member currentMember = memberDao.findMemberById(memberCopyDto.getId());
            if (currentMember != null && currentMember.getNickname() != null) {
                return currentMember.getNickname();
            }
        }
        return memberCopyDto.getNickname();
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
        if (memberDao != null) {
            Member currentMember = memberDao.findMemberById(memberCopyDto.getId());
            if (currentMember != null && currentMember.getNickname() != null) {
                return currentMember.getNickname();
            }
        }
        return memberCopyDto.getNickname();
    }
}
