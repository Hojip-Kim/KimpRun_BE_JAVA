package kimp.auth.service.serviceImpl;

import kimp.auth.dto.CheckAuthResponseDto;
import kimp.auth.service.AuthService;
import kimp.member.util.NicknameGeneratorUtils;
import kimp.user.dto.UserWithIdNameEmailDto;
import org.springframework.stereotype.Service;


@Service
public class SessionAuthServiceImpl implements AuthService {

    private final NicknameGeneratorUtils nicknameGeneratorUtils;

    public SessionAuthServiceImpl(NicknameGeneratorUtils nicknameGeneratorUtils) {
        this.nicknameGeneratorUtils = nicknameGeneratorUtils;
    }

    @Override
    public CheckAuthResponseDto checkAuthStatus(kimp.security.user.CustomUserDetails member) {
        String memberEmail = member.getEmail();
        String memberName = member.getUsername();
        String UserRole = member.getRole().name();
        return new CheckAuthResponseDto(true, new UserWithIdNameEmailDto(memberEmail, memberName, UserRole));

    }
}
