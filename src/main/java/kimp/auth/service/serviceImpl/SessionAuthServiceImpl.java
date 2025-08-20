package kimp.auth.service.serviceImpl;

import kimp.auth.dto.LoginMemberResponseDto;
import kimp.auth.service.AuthService;
import kimp.member.util.NicknameGeneratorUtils;
import kimp.security.user.CustomUserDetails;
import kimp.user.dto.UserWithIdNameEmailDto;
import org.springframework.stereotype.Service;


@Service
public class SessionAuthServiceImpl implements AuthService {

    private final NicknameGeneratorUtils nicknameGeneratorUtils;

    public SessionAuthServiceImpl(NicknameGeneratorUtils nicknameGeneratorUtils) {
        this.nicknameGeneratorUtils = nicknameGeneratorUtils;
    }

    @Override
    public LoginMemberResponseDto checkAuthStatus(CustomUserDetails member) {
        if(member == null) {
            return null;
        }
        String memberEmail = member.getEmail();
        String memberName = member.getUsername();
        String UserRole = member.getRole().name();
        return new LoginMemberResponseDto(true, new UserWithIdNameEmailDto(memberEmail, memberName, UserRole), null);

    }
}
