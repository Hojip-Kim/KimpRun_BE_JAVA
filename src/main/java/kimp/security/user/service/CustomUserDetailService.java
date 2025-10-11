package kimp.security.user.service;

import kimp.security.user.CustomUserDetails;
import kimp.user.dto.internal.UserCopyDto;
import kimp.user.entity.Member;
import kimp.user.service.member.MemberService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class CustomUserDetailService implements UserDetailsService {
    private final MemberService memberService;

    public CustomUserDetailService(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        // 최적화된 쿼리로 모든 연관 엔티티를 한 번에 조회
        Member member = memberService.getMemberByEmailOptimized(email);
        
        if(member != null) {
            UserCopyDto userDto = new UserCopyDto(
                member.getId(), 
                member.getEmail(), 
                member.getPassword(), 
                member.getNickname(), 
                member.getRole().getRoleName()
            );
            return new CustomUserDetails(userDto);
        } else {
            throw new UsernameNotFoundException(email);
        }
    }
}
