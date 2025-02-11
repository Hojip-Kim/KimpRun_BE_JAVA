package kimp.auth.service;

import kimp.auth.dto.CheckAuthResponseDto;
import kimp.security.user.CustomUserDetails;

public interface AuthService {

    public CheckAuthResponseDto checkAuthStatus(CustomUserDetails member);

}
