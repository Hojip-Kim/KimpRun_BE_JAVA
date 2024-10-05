package kimp.auth.service.serviceImpl;

import kimp.auth.dto.CheckAuthResponseDto;
import kimp.auth.service.AuthService;
import kimp.security.user.CustomUserDetails;
import kimp.user.dto.UserWithIdNameEmailDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class SessionAuthServiceImpl implements AuthService {


    @Override
    public CheckAuthResponseDto checkAuthStatus(UserDetails userDetails) {

        CustomUserDetails user = (CustomUserDetails) userDetails;

        UserWithIdNameEmailDto userWithIdNameEmailDto = new UserWithIdNameEmailDto(user.getId(), user.getEmail(), user.getUsername());

        CheckAuthResponseDto checkAuthResponseDto = new CheckAuthResponseDto(userWithIdNameEmailDto);

        return checkAuthResponseDto;
    }
}
