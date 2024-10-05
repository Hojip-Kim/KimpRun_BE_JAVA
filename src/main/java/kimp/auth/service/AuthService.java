package kimp.auth.service;

import kimp.auth.dto.CheckAuthResponseDto;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {

    public CheckAuthResponseDto checkAuthStatus(UserDetails userDetails);

}
