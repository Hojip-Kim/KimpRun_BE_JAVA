package kimp.security.user.service;

import kimp.security.user.CustomUserDetails;
import kimp.user.dto.UserCopyDto;
import kimp.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomUserDetailService implements UserDetailsService {
    private final UserService userService;

    public CustomUserDetailService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        UserCopyDto userDto = userService.createCopyUserDtoByLoginId(loginId);

        if(userDto != null) {
            return new CustomUserDetails(userDto);
        }else{
            throw new UsernameNotFoundException(loginId);
        }
    }
}
