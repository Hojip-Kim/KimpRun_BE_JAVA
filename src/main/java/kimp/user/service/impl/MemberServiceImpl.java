package kimp.user.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kimp.member.util.NicknameGeneratorUtils;
import kimp.user.dao.BannedCountDao;
import kimp.user.dao.MemberDao;
import kimp.user.dao.MemberWithDrawDao;
import kimp.user.dao.UserAgentDao;
import kimp.user.dto.UserCopyDto;
import kimp.user.dto.UserDto;
import kimp.user.dto.request.*;
import kimp.user.entity.*;
import kimp.user.enums.UserRole;
import kimp.user.service.MemberService;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberDao memberDao;
    private final UserAgentDao userAgentDao;
    private final BannedCountDao bannedCountDao;
    private final MemberWithDrawDao memberWithDrawDao;
    private final PasswordEncoder passwordEncoder;
    private final NicknameGeneratorUtils nicknameGeneratorUtils;
    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;


    public MemberServiceImpl(MemberDao memberDao, UserAgentDao userAgentDao, BannedCountDao bannedCountDao, MemberWithDrawDao memberWithDrawDao, PasswordEncoder passwordEncoder, NicknameGeneratorUtils nicknameGeneratorUtils, JavaMailSender mailSender, StringRedisTemplate stringRedisTemplate){
        this.memberDao = memberDao;
        this.userAgentDao = userAgentDao;
        this.bannedCountDao = bannedCountDao;
        this.memberWithDrawDao = memberWithDrawDao;
        this.passwordEncoder = passwordEncoder;
        this.nicknameGeneratorUtils = nicknameGeneratorUtils;
        this.mailSender = mailSender;
        this.redisTemplate = stringRedisTemplate;
    }

    @Override
    public String sendEmailVerifyCode(String email) {
        if(email.isEmpty() || email == null){
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Email cannot be null or empty", HttpStatus.BAD_REQUEST, "MemberServiceImpl.sendEmailVerifyCode");
        }

        String verificationCode = generateVerificationCode();

        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(email);
            helper.setSubject("회원가입 인증 코드");
            helper.setText("<p>인증 코드: <strong>" + verificationCode + "</strong><p>", true);
            mailSender.send(mimeMessage);

            // redis 5분 제한시간 생성
            redisTemplate.opsForValue().set(email, verificationCode, 5, TimeUnit.MINUTES);


            return verificationCode;

        } catch (MessagingException e) {
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, "Failed to send email verification code", HttpStatus.INTERNAL_SERVER_ERROR, "MemberServiceImpl.sendEmailVerifyCode");
        }

    }

    // email verification code 정합성 유무 return
    @Override
    public Boolean verifyCode(String email, String code){
        String storedCode = redisTemplate.opsForValue().get(email);
        return storedCode != null && storedCode.equals(code);
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
            Oauth oauth = new Oauth();
            oauth.setMember(member);

            if(request.getOauth() != null){
                oauth.setProvider(request.getOauth().name())
                .setProviderId(request.getProviderId())
                .setAccessToken(passwordEncoder.encode(request.getAccessToken()));
            }

            UserAgent userAgent = userAgentDao.createUserAgent(member);
            MemberWithdraw memberWithdraw = memberWithDrawDao.createMemberWithDraw(member);
            BannedCount bannedCount = bannedCountDao.createBannedCount(userAgent);

            userAgent.setBannedCount(bannedCount);

            log.info("유저 생성 완료: {}", member);
            return member;

        } catch (Exception e) {
            log.error("유저 생성 중 오류 발생", e);
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, "Failed to create user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, "MemberServiceImpl.createMember");
        }
    }

    @Override
    @Transactional
    public Member updateNickname(Long id, UpdateUserNicknameDTO UpdateUserNicknameDTO){
        Member member = memberDao.findMemberById(id);

        return member.updateNickname(UpdateUserNicknameDTO.getNickname());
    }

    @Override
    @Transactional
    public Boolean deActivateMember(Long id, DeActivateUserDTO deleteUserDTO) {
        Member member = memberDao.findMemberById(id);

        if(passwordEncoder.matches(deleteUserDTO.getPassword(), member.getPassword())){
            member.deActivate();
            return true;
        }

        return false;
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
    public Boolean isFirstLogin(Member member){
        UserAgent ua = member.getMemberAgent();
        return ua.getIp() == null;
    }

    @Override
    public Boolean isEqualIpBeforeLogin(Member member, String ip){

        UserAgent ua = member.getMemberAgent();
        return ua.getIp().equals(ip);
    }

    @Override
    @Transactional
    public void setMemberIP(Member member, String ip){
        if(ip == null){
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "IP address cannot be null", HttpStatus.BAD_REQUEST, "MemberServiceImpl.setMemberIP");
        }
        log.info("ip : {}" , ip);
        Member foundMember = memberDao.findMemberById(member.getId());
        foundMember.getMemberAgent().setIp(ip);
    }

    @Override
    @Transactional
    public Member updateMember(Long id, UpdateUserPasswordDTO UpdateUserPasswordDTO) {
        Member member = memberDao.findMemberById(id);

        boolean isMatched = passwordEncoder.matches(UpdateUserPasswordDTO.getOldPassword(), member.getPassword());
        if(!isMatched){
            throw new KimprunException(KimprunExceptionEnum.AUTHENTICATION_REQUIRED_EXCEPTION, "Old password does not match", HttpStatus.UNAUTHORIZED, "MemberServiceImpl.updateMember");
        }
        memberDao.updateMember(member, passwordEncoder.encode(UpdateUserPasswordDTO.getNewPassword()));

        return member;
    }

    @Override
    public Boolean deleteMember(DeleteUserDTO deleteUserDTO) {
        Member member = memberDao.findMemberById(deleteUserDTO.getUserId());

        if(member == null){
            return false;
        }

        Boolean isDeleted = memberDao.deleteMember(deleteUserDTO.getUserId());
        return isDeleted;
    }

    @Override
    public UserDto convertUserToUserDto(Member member) {

        return new UserDto(member.getEmail(), member.getNickname(), member.getRole());
    }

    @Override
    @Transactional
    public Member grantRole(Long memberId, UserRole grantRole) {
        Member member = memberDao.findMemberById(memberId);
        member.grantRole(grantRole);
        return member;
    }

    private String generateVerificationCode(){
        Random random = new Random();

        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

}
