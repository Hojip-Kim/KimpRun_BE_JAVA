package kimp.user.service;

import kimp.user.dto.UserCopyDto;
import kimp.user.dto.UserDto;
import kimp.user.dto.UserWithIdNameEmailDto;
import kimp.user.dto.request.*;
import kimp.user.entity.Member;
import kimp.user.enums.UserRole;
import kimp.user.enums.Oauth;

public interface MemberService {

    public UserDto createMember(CreateUserDTO request);

    public Member createMemberEntity(CreateUserDTO request);

    public Member getmemberByEmail(String email);

    public Member getMemberByOAuthProviderId(String provider, String providerId);

    public String sendEmailVerifyCode(String email);

    public UserCopyDto createCopyUserDtoByEmail(String email);

    public Boolean verifyCode(String email, String code);

    public UserDto getmemberById(Long id);

    public Member getMemberEntityById(Long id);

    public Boolean isFirstLogin(Member member);

    public Boolean isEqualIpBeforeLogin(Member member, String ip);

    public void setMemberIP(Member member, String ip);

    public UserDto updateMember(Long id, UpdateUserPasswordDTO UpdateUserPasswordDTO);

    public UserWithIdNameEmailDto updateNickname(Long id, UpdateUserNicknameDTO UpdateUserNicknameDTO);

    public Boolean deActivateMember(Long id, DeActivateUserDTO deleteUserDTO);

    public Boolean deleteMember(DeleteUserDTO DeleteUserDTO);

    public UserDto convertUserToUserDto(Member member);

    public UserDto grantRole(Long memberId, UserRole grantRole);

    public Member attachOAuthToMember(Member member, String provider, String providerId, 
                                     String accessToken, String refreshToken, String tokenType, 
                                     Long expiresIn, String scope);

    public Boolean updatePassword(UpdateUserPasswordRequest request);
    
    /**
     * 로그인용 최적화된 Member 조회 (모든 연관 엔티티를 한 번에 fetch)
     */
    public Member getMemberByEmailOptimized(String email);
}
