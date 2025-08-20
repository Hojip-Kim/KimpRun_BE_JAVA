package kimp.user.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kimp.member.util.NicknameGeneratorUtils;
import kimp.user.dao.BannedCountDao;
import kimp.user.dao.MemberDao;
import kimp.user.dao.MemberWithDrawDao;
import kimp.user.dao.ProfileDao;
import kimp.user.dao.UserAgentDao;
import kimp.user.service.MemberRoleService;
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

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberDao memberDao;
    private final UserAgentDao userAgentDao;
    private final BannedCountDao bannedCountDao;
    private final MemberWithDrawDao memberWithDrawDao;
    private final ProfileDao profileDao;
    private final MemberRoleService memberRoleService;
    private final PasswordEncoder passwordEncoder;
    private final NicknameGeneratorUtils nicknameGeneratorUtils;
    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;


    public MemberServiceImpl(MemberDao memberDao, UserAgentDao userAgentDao, BannedCountDao bannedCountDao, MemberWithDrawDao memberWithDrawDao, ProfileDao profileDao, MemberRoleService memberRoleService, PasswordEncoder passwordEncoder, NicknameGeneratorUtils nicknameGeneratorUtils, JavaMailSender mailSender, StringRedisTemplate stringRedisTemplate){
        this.memberDao = memberDao;
        this.userAgentDao = userAgentDao;
        this.bannedCountDao = bannedCountDao;
        this.memberWithDrawDao = memberWithDrawDao;
        this.profileDao = profileDao;
        this.memberRoleService = memberRoleService;
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

            // 기존 이메일로 가입된 사용자가 있는지 확인
            Member existingMember = memberDao.findMemberByEmail(request.getEmail());
            
            if (existingMember != null) {
                // 기존 사용자가 OAuth만 가입한 경우 (비밀번호가 없거나 OAuth 정보가 있는 경우)
                if (existingMember.getOauth() != null && request.getOauth() == null) {
                    log.info("기존 OAuth 사용자에게 비밀번호 추가 - Email: {}", request.getEmail());
                    return addPasswordToExistingOAuthMember(existingMember, request);
                } else {
                    // 이미 일반 회원가입이 완료된 경우
                    throw new KimprunException(KimprunExceptionEnum.DUPLICATE_EMAIL_EXCEPTION, 
                        "이미 가입된 이메일입니다.", HttpStatus.CONFLICT, "MemberServiceImpl.createMember");
                }
            }

            Member member = null;
            String nickname;

            if (request.getNickname() == null || request.getNickname().trim().isEmpty()) {
                nickname = nicknameGeneratorUtils.createRandomNickname();
            } else {
                nickname = request.getNickname();
            }

            MemberRole defaultRole = memberRoleService.getDefaultUserRole();
            
            member = memberDao.createMember(
                    request.getEmail(),
                    nickname,
                    passwordEncoder.encode(request.getPassword()),
                    defaultRole
            );
            Oauth oauth = new Oauth();
            oauth.setMember(member);

            if(request.getOauth() != null){
                LocalDateTime now = LocalDateTime.now();
                
                oauth.setProvider(request.getOauth().name())
                      .setProviderId(request.getProviderId())
                      .setAccessToken(request.getAccessToken());
                      
                if(request.getRefreshToken() != null) {
                    oauth.setRefreshToken(request.getRefreshToken());
                }
                if(request.getTokenType() != null) {
                    oauth.setTokenType(request.getTokenType());
                }
                if(request.getExpiresIn() != null) {
                    oauth.setExpiresIn(request.getExpiresIn())
                         .setExpiresAt(now.plusSeconds(request.getExpiresIn()));
                }
                if(request.getScope() != null) {
                    oauth.setScope(request.getScope());
                }
            }

            UserAgent userAgent = userAgentDao.createUserAgent(member);
            MemberWithdraw memberWithdraw = memberWithDrawDao.createMemberWithDraw(member);
            BannedCount bannedCount = bannedCountDao.createBannedCount(userAgent);
            Profile profile = profileDao.createDefaultProfile(member);

            userAgent.setBannedCount(bannedCount);

            log.info("유저 생성 완료: {}", member);
            return member;

        } catch (KimprunException e) {
            // KimprunException은 그대로 다시 던지기
            throw e;
        } catch (Exception e) {
            log.error("유저 생성 중 오류 발생", e);
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, "Failed to create user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, "MemberServiceImpl.createMember");
        }
    }
    
    private Member addPasswordToExistingOAuthMember(Member existingMember, CreateUserDTO request) {
        log.info("기존 OAuth 사용자에게 비밀번호 추가 - Member ID: {}", existingMember.getId());
        
        // 비밀번호 설정
        existingMember.updatePassword(passwordEncoder.encode(request.getPassword()));
        
        // 닉네임이 요청에 있는 경우 업데이트
        if (request.getNickname() != null && !request.getNickname().trim().isEmpty()) {
            if (!request.getNickname().equals(existingMember.getNickname())) {
                existingMember.updateNickname(request.getNickname());
            }
        }
        
        log.info("기존 OAuth 사용자에게 비밀번호 추가 완료 - Member ID: {}", existingMember.getId());
        return existingMember;
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

    @Override
    public Member getMemberByOAuthProviderId(String provider, String providerId) {
        return memberDao.findMemberByOAuthProviderId(provider, providerId);
    }

    // 외부 서비스에서 호출하는 메소드
    // 외부 서비스에서 객체 변경 방지를 위한 dto화
    @Override
    @Transactional
    public UserCopyDto createCopyUserDtoByEmail(String email) {
        Member member = memberDao.findMemberByEmail(email);
        if(member != null) {
            return new UserCopyDto(member.getId(), member.getEmail(), member.getPassword(), member.getNickname(), member.getRole().getRoleName());
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

        return new UserDto(member.getEmail(), member.getNickname(), member.getRole().getRoleName());
    }

    @Override
    @Transactional
    public Member grantRole(Long memberId, UserRole grantRole) {
        Member member = memberDao.findMemberById(memberId);
        MemberRole memberRole = memberRoleService.getRoleByName(grantRole);
        member.grantRole(memberRole);
        return member;
    }

    @Override
    @Transactional
    public Member attachOAuthToMember(Member member, String provider, String providerId, 
                                     String accessToken, String refreshToken, String tokenType, 
                                     Long expiresIn, String scope) {
        try {
            log.info("OAuth 정보 연결 시작 - Member ID: {}, Provider: {}", member.getId(), provider);
            
            // 기존 OAuth 정보가 있는지 확인
            kimp.user.entity.Oauth existingOAuth = member.getOauth();
            
            if (existingOAuth != null) {
                // 기존 OAuth 정보가 있으면 업데이트
                log.info("기존 OAuth 정보 업데이트 - Member ID: {}", member.getId());
                LocalDateTime now = LocalDateTime.now();
                
                existingOAuth.setProvider(provider)
                            .setProviderId(providerId)
                            .setAccessToken(accessToken);
                            
                if (refreshToken != null) {
                    existingOAuth.setRefreshToken(refreshToken);
                }
                if (tokenType != null) {
                    existingOAuth.setTokenType(tokenType);
                }
                if (expiresIn != null) {
                    existingOAuth.setExpiresIn(expiresIn)
                               .setExpiresAt(now.plusSeconds(expiresIn));
                }
                if (scope != null) {
                    existingOAuth.setScope(scope);
                }
            } else {
                // 새로운 OAuth 정보 생성
                log.info("새로운 OAuth 정보 생성 - Member ID: {}", member.getId());
                LocalDateTime now = LocalDateTime.now();
                
                kimp.user.entity.Oauth oauth = new kimp.user.entity.Oauth();
                oauth.setMember(member)
                     .setProvider(provider)
                     .setProviderId(providerId)
                     .setAccessToken(accessToken);
                     
                if (refreshToken != null) {
                    oauth.setRefreshToken(refreshToken);
                }
                if (tokenType != null) {
                    oauth.setTokenType(tokenType);
                }
                if (expiresIn != null) {
                    oauth.setExpiresIn(expiresIn)
                         .setExpiresAt(now.plusSeconds(expiresIn));
                }
                if (scope != null) {
                    oauth.setScope(scope);
                }
                
                member.setOauth(oauth);
            }
            
            log.info("OAuth 정보 연결 완료 - Member ID: {}", member.getId());
            return member;
            
        } catch (Exception e) {
            log.error("OAuth 정보 연결 중 오류 발생 - Member ID: {}", member.getId(), e);
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, 
                                     "Failed to attach OAuth to member: " + e.getMessage(), 
                                     HttpStatus.INTERNAL_SERVER_ERROR, "MemberServiceImpl.attachOAuthToMember");
        }
    }

    private String generateVerificationCode(){
        Random random = new Random();

        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

}
