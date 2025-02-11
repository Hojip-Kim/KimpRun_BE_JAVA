package kimp.user.service;

import kimp.user.dto.UserCopyDto;
import kimp.user.dto.UserDto;
import kimp.user.dto.request.*;
import kimp.user.entity.Member;
import kimp.user.enums.UserRole;

public interface MemberService {

    public Member createMember(CreateUserDTO request);

    public Member getmemberByEmail(String email);

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
}
