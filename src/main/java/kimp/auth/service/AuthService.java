package kimp.auth.service;

import kimp.auth.dto.LoginMemberResponseDto;
import kimp.auth.vo.CheckAuthStatusVo;
import kimp.security.user.CustomUserDetails;

public interface AuthService {

    public LoginMemberResponseDto checkAuthStatus(CheckAuthStatusVo vo);

}
