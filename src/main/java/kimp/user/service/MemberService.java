package kimp.user.service;

import kimp.user.dto.UserCopyDto;
import kimp.user.dto.UserDto;
import kimp.user.dto.UserWithIdNameEmailDto;
import kimp.user.dto.request.*;
import kimp.user.entity.Member;
import kimp.user.enums.UserRole;
import kimp.user.enums.Oauth;
import kimp.user.vo.*;

public interface MemberService {

    public UserDto createMember(CreateMemberVo vo);

    public Member createMemberEntity(CreateUserDTO request);

    public Member getmemberByEmail(String email);

    public Member getMemberByOAuthProviderId(String provider, String providerId);

    public String sendEmailVerifyCode(SendEmailVerifyCodeVo vo);

    public UserCopyDto createCopyUserDtoByEmail(String email);

    public Boolean verifyCode(VerifyEmailCodeVo vo);

    public UserDto getmemberById(GetMemberByIdVo vo);

    public Member getMemberEntityById(Long id);

    public Boolean isFirstLogin(Member member);

    public Boolean isEqualIpBeforeLogin(Member member, String ip);

    public void setMemberIP(Member member, String ip);

    public UserDto updateMember(UpdateMemberPasswordVo vo);

    public UserWithIdNameEmailDto updateNickname(UpdateMemberNicknameVo vo);

    public Boolean deActivateMember(DeActivateMemberVo vo);

    public Boolean deleteMember(DeleteMemberVo vo);

    public UserDto convertUserToUserDto(Member member);

    public UserDto grantRole(UpdateUserRoleVo vo);

    public Member attachOAuthToMember(Member member, String provider, String providerId,
                                     String accessToken, String refreshToken, String tokenType,
                                     Long expiresIn, String scope);

    public Boolean updatePassword(ResetPasswordVo vo);

    /**
     * 로그인용 최적화된 Member 조회 (모든 연관 엔티티를 한 번에 fetch)
     */
    public Member getMemberByEmailOptimized(String email);
}
