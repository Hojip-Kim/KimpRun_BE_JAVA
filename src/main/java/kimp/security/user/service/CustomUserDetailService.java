package kimp.security.user.service;

import kimp.security.user.CustomUserDetails;
import kimp.user.dto.UserCopyDto;
import kimp.user.entity.User;
import kimp.user.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailService implements UserDetailsService {
    private final UserService userService;

    public CustomUserDetailService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        UserCopyDto userDto = userService.createCopyUserDtoByLoginId(loginId);

        return new CustomUserDetails(new User(userDto.getLoginId(), userDto.getPassword()));
    }
}
