package kimp.auth.service.serviceImpl;

import kimp.auth.dto.OauthProcessDTO;
import kimp.auth.service.OAuth2Service;
import kimp.auth.service.OAuth2TokenRefreshService;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.user.dto.UserCopyDto;
import kimp.user.dto.request.CreateUserDTO;
import kimp.user.entity.Member;
import kimp.user.enums.Oauth;
import kimp.user.service.MemberService;
import kimp.user.util.NicknameGeneratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class OAuth2ServiceImpl implements OAuth2Service {

    private final MemberService memberService;
    private final NicknameGeneratorUtils nicknameGeneratorUtils;
    private final OAuth2TokenRefreshService tokenRefreshService;

    public OAuth2ServiceImpl(MemberService memberService, NicknameGeneratorUtils nicknameGeneratorUtils, OAuth2TokenRefreshService tokenRefreshService) {
        this.memberService = memberService;
        this.nicknameGeneratorUtils = nicknameGeneratorUtils;
        this.tokenRefreshService = tokenRefreshService;
    }

    @Override
    @Transactional
    public UserCopyDto processOAuth2member(OauthProcessDTO oauthProcessDTO) {
        Map<String, Object> attributes = oauthProcessDTO.getOauth2User().getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String providerId = (String) attributes.get("sub");
        String nickName = nicknameGeneratorUtils.createRandomNickname();

        log.info("OAuth2 attributes: {}", attributes);

        String password = UUID.randomUUID().toString();

        // 1. 이메일로 회원 조회
        Member member = memberService.getmemberByEmail(email);
        
        // 2. Provider ID로 이미 다른 계정에 연결된 OAuth가 있는지 확인
        Member existingOAuthMember = memberService.getMemberByOAuthProviderId(Oauth.GOOGLE.name(), providerId);
        
        if (existingOAuthMember != null && member != null && !existingOAuthMember.getId().equals(member.getId())) {
            // 같은 Provider ID가 다른 계정에 이미 연결되어 있는 경우
            log.warn("OAuth Provider ID 중복 - Provider ID: {}, 기존 회원 ID: {}, 새 회원 ID: {}", 
                     providerId, existingOAuthMember.getId(), member.getId());
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, 
                                     "이 OAuth 계정은 이미 다른 회원에게 연결되어 있습니다.", 
                                     HttpStatus.CONFLICT, "OAuth2ServiceImpl.processOAuth2member");
        }
        
        if (existingOAuthMember != null && member == null) {
            // Provider ID로 찾은 회원이 있지만 이메일로는 찾지 못한 경우 (이메일 변경된 경우)
            log.info("OAuth Provider ID로 기존 회원 발견 - Provider ID: {}, Member ID: {}", providerId, existingOAuthMember.getId());
            member = existingOAuthMember;
        }

        if(member == null){
            // member가 없을경우 회원가입 진행
            CreateUserDTO createUserDTO = new CreateUserDTO(nickName, email, password);

            // 현재 google oauth만 제공하므로 google로 설정
            createUserDTO.setOauth(Oauth.GOOGLE);
            createUserDTO.setProviderId(providerId);
            createUserDTO.setAccessToken(oauthProcessDTO.getAccessToken());
            if(oauthProcessDTO.getRefreshToken() != null){
                createUserDTO.setRefreshToken(oauthProcessDTO.getRefreshToken());
            }
            if(oauthProcessDTO.getTokenType() != null){
                createUserDTO.setTokenType(oauthProcessDTO.getTokenType());
            }
            if(oauthProcessDTO.getExpiresIn() != null){
                createUserDTO.setExpiresIn(oauthProcessDTO.getExpiresIn());
            }
            if(oauthProcessDTO.getScope() != null){
                createUserDTO.setScope(oauthProcessDTO.getScope());
            }

            member = memberService.createMember(createUserDTO);
            member.setName(name);
        } else {
            // 기존 회원이 있을 경우 OAuth 정보 연결 또는 업데이트
            log.info("기존 회원에 OAuth 정보 연결 - Email: {}, Provider: {}", email, Oauth.GOOGLE.name());
            
            member = memberService.attachOAuthToMember(
                member,
                Oauth.GOOGLE.name(),
                providerId,
                oauthProcessDTO.getAccessToken(),
                oauthProcessDTO.getRefreshToken(),
                oauthProcessDTO.getTokenType(),
                oauthProcessDTO.getExpiresIn(),
                oauthProcessDTO.getScope()
            );
            
            // 기존 토큰이 만료되었거나 곧 만료될 경우 갱신 시도
            if (member.getOauth() != null && tokenRefreshService.isTokenExpiringSoon(member.getOauth(), 60)) {
                log.info("토큰이 곧 만료됨. 갱신 시도 - Member ID: {}", member.getId());
                tokenRefreshService.refreshToken(member.getOauth());
            }
        }

        return new UserCopyDto(member.getId(), member.getEmail(), member.getPassword(), member.getNickname(), member.getRole().getRoleName());
    }

}
