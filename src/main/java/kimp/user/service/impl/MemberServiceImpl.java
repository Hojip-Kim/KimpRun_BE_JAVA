package kimp.user.service.impl;

import kimp.member.util.NicknameGeneratorUtils;
import kimp.user.dao.MemberDao;
import kimp.user.dto.UserCopyDto;
import kimp.user.dto.UserDto;
import kimp.user.dto.request.CreateUserDTO;
import kimp.user.dto.request.DeleteUserDTO;
import kimp.user.dto.request.UpdateUserNicknameDTO;
import kimp.user.dto.request.UpdateUserPasswordDTO;
import kimp.user.entity.Member;
import kimp.user.enums.UserRole;
import kimp.user.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberDao memberDao;
    private final PasswordEncoder passwordEncoder;
    private final NicknameGeneratorUtils nicknameGeneratorUtils;

    public MemberServiceImpl(MemberDao memberDao, PasswordEncoder passwordEncoder, NicknameGeneratorUtils nicknameGeneratorUtils){
        this.memberDao = memberDao;
        this.passwordEncoder = passwordEncoder;
        this.nicknameGeneratorUtils = nicknameGeneratorUtils;
    }

    @Override
    @Transactional
    public Member createMember(CreateUserDTO request) {
        try {
            log.info("유저 생성 시작 - Email: {}, Nickname: {}", request.getEmail(), request.getNickname());

            Member member = null;
            String nickname;

            if (request.getNickname() == null || request.getNickname().trim().isEmpty()) {
                nickname = nicknameGeneratorUtils.createRandomNickname();
                log.info("랜덤 닉네임 생성: {}", nickname);
            } else {
                nickname = request.getNickname();
            }

            member = memberDao.createMember(
                    request.getEmail(),
                    nickname,
                    passwordEncoder.encode(request.getPassword())
            );

            log.info("유저 생성 완료: {}", member);
            return member;

        } catch (Exception e) {
            log.error("유저 생성 중 오류 발생", e);
            throw new RuntimeException("유저 생성 실패: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Member updateNickname(Long id, UpdateUserNicknameDTO UpdateUserNicknameDTO){
        Member member = memberDao.findMemberById(id);

        return member.updateNickname(UpdateUserNicknameDTO.getNickname());
    }


    @Override
    public Member getmemberByEmail(String email) {

        Member member = memberDao.findMemberByEmail(email);

        return member;
    }

    // 외부 서비스에서 호출하는 메소드
    // 외부 서비스에서 객체 변경 방지를 위한 dto화
    @Override
    public UserCopyDto createCopyUserDtoByEmail(String email) {
        Member member = memberDao.findMemberByEmail(email);
        if(member != null) {
            return new UserCopyDto(member.getId(), member.getEmail(), member.getPassword(), member.getNickname(), member.getRole());
        }else{
            return null;
        }
    }

    @Override
    public Member getmemberById(Long id) {
        Member member = memberDao.findMemberById(id);
        return member;
    }

    @Override
    @Transactional
    public Member updateMember(Long id, UpdateUserPasswordDTO UpdateUserPasswordDTO) {
        Member member = memberDao.findMemberById(id);
        boolean isMatched = isMatchedPassword(UpdateUserPasswordDTO.getOldPassword(), member.getPassword());
        if(!isMatched){
            throw new IllegalArgumentException("password does not match");
        }
        memberDao.updateMember(member, passwordEncoder.encode(UpdateUserPasswordDTO.getNewPassword()));

        return member;
    }

    @Override
    public Boolean deletemember(Long id, DeleteUserDTO DeleteUserDTO) {
        Member member = memberDao.findMemberById(id);
        boolean isMatched = isMatchedPassword(DeleteUserDTO.getPassword(), member.getPassword());
        if(!isMatched){
            throw new IllegalArgumentException("password does not match");
        }
        Boolean isDeleted = memberDao.deletemember(id);
        return isDeleted;
    }

    @Override
    public UserDto convertUserToUserDto(Member member) {

        return new UserDto(member.getEmail(), member.getNickname(), member.getRole());
    }

    @Override
    public Boolean isMatchedPassword(String password, String hashedPassword) {
        return passwordEncoder.matches(password, hashedPassword);
    }

    @Override
    @Transactional
    public Member grantRole(Long memberId, UserRole grantRole) {
        Member member = memberDao.findMemberById(memberId);
        member.grantRole(grantRole);
        return member;
    }

}
