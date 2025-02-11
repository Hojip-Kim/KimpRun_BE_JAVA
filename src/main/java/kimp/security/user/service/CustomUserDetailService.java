package kimp.security.user.service;

import kimp.security.user.CustomUserDetails;
import kimp.user.dto.UserCopyDto;
import kimp.user.service.MemberService;
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
        UserCopyDto UserDto = memberService.createCopyUserDtoByEmail(email);

        if(UserDto != null) {
            return new CustomUserDetails(UserDto);
        }else{
            throw new UsernameNotFoundException(email);
        }
    }
}
