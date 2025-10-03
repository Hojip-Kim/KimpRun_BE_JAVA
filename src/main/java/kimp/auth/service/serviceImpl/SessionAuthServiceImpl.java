package kimp.auth.service.serviceImpl;

import kimp.auth.dto.response.LoginMemberResponseDto;
import kimp.auth.service.AuthService;
import kimp.auth.vo.CheckAuthStatusVo;
import kimp.user.dto.response.UserWithIdNameEmailDto;
import kimp.user.entity.Member;
import kimp.user.service.MemberService;
import kimp.user.util.NicknameGeneratorUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class SessionAuthServiceImpl implements AuthService {

    private final NicknameGeneratorUtils nicknameGeneratorUtils;
    private final MemberService memberService;

    public SessionAuthServiceImpl(NicknameGeneratorUtils nicknameGeneratorUtils, MemberService memberService) {
        this.nicknameGeneratorUtils = nicknameGeneratorUtils;
        this.memberService = memberService;
    }

    @Override
    @Transactional
    public LoginMemberResponseDto checkAuthStatus(CheckAuthStatusVo vo) {
        if(vo == null || vo.getMemberId() == null) {
            return null;
        }

        Member member = memberService.getMemberEntityById(vo.getMemberId());

        String memberEmail = member.getEmail();
        String memberNickname = member.getNickname();
        String UserRole = member.getRole().getRoleName().getName();
        Long memberIdFromEntity = member.getId();

        UserWithIdNameEmailDto userDto = UserWithIdNameEmailDto.builder()
                .email(memberEmail)
                .name(memberNickname)
                .role(UserRole)
                .memberId(memberIdFromEntity)
                .build();

        return LoginMemberResponseDto.builder()
                .isAuthenticated(true)
                .member(userDto)
                .uuid(null)
                .build();

    }
}
