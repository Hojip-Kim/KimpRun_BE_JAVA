package kimp.user.service;

import kimp.user.dto.UserCopyDto;
import kimp.user.dto.UserDto;
import kimp.user.dto.UserWithIdNameEmailDto;
import kimp.user.dto.request.*;
import kimp.user.entity.Member;
import kimp.user.enums.UserRole;
import kimp.user.enums.Oauth;

public interface MemberService {

    public Member createMember(CreateUserDTO request);

    public Member getmemberByEmail(String email);

    public Member getMemberByOAuthProviderId(String provider, String providerId);

    public String sendEmailVerifyCode(String email);

    public UserCopyDto createCopyUserDtoByEmail(String email);

    public Boolean verifyCode(String email, String code);

    public Member getmemberById(Long id);

    public Boolean isFirstLogin(Member member);

    public Boolean isEqualIpBeforeLogin(Member member, String ip);

    public void setMemberIP(Member member, String ip);

    public Member updateMember(Long id, UpdateUserPasswordDTO UpdateUserPasswordDTO);

    public Member updateNickname(Long id, UpdateUserNicknameDTO UpdateUserNicknameDTO);

    public Boolean deActivateMember(Long id, DeActivateUserDTO deleteUserDTO);

    public Boolean deleteMember(DeleteUserDTO DeleteUserDTO);

    public UserDto convertUserToUserDto(Member member);

    public Member grantRole(Long memberId, UserRole grantRole);

    public Member attachOAuthToMember(Member member, String provider, String providerId, 
                                     String accessToken, String refreshToken, String tokenType, 
                                     Long expiresIn, String scope);

    public Boolean updatePassword(UpdateUserPasswordRequest request);

    // DTO 반환 메소드들 (Controller용)
    public UserDto createMemberDto(CreateUserDTO request);
    
    public UserDto getMemberDtoById(Long id);
    
    public UserDto updateMemberDto(Long id, UpdateUserPasswordDTO updateUserPasswordDTO);
    
    public UserWithIdNameEmailDto updateNicknameDto(Long id, UpdateUserNicknameDTO updateUserNicknameDTO);
    
    public UserDto grantRoleDto(Long memberId, UserRole grantRole);
}
