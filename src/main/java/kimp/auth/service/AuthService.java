package kimp.auth.service;

import kimp.auth.dto.response.LoginMemberResponseDto;
import kimp.auth.vo.CheckAuthStatusVo;

public interface AuthService {

    public LoginMemberResponseDto checkAuthStatus(CheckAuthStatusVo vo);

}
