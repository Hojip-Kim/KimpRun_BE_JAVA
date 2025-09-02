package kimp.auth.service.serviceImpl;

import kimp.auth.dto.LoginMemberResponseDto;
import kimp.auth.service.AuthService;
import kimp.security.user.CustomUserDetails;
import kimp.user.dto.UserWithIdNameEmailDto;
import kimp.user.util.NicknameGeneratorUtils;
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
        String memberNickname = member.getUsername();
        String UserRole = member.getRole().name();
        Long memberId = member.getId();
        return new LoginMemberResponseDto(true, new UserWithIdNameEmailDto(memberEmail, memberNickname, UserRole, memberId), null);

    }
}
