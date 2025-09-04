package kimp.auth.service;

import kimp.auth.dto.LoginMemberResponseDto;
import kimp.security.user.CustomUserDetails;

public interface AuthService {

    public LoginMemberResponseDto checkAuthStatus(Long memberId);

}
