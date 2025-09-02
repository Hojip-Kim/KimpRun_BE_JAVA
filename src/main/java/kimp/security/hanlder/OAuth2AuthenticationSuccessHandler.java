package kimp.security.hanlder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kimp.user.entity.Member;
import kimp.user.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${spring.frontend.dev-url}")
    private String frontendDevUrl;

    @Value("${spring.frontend.prod-url}")
    private String frontendProdUrl;

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    private final Environment environment;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final MemberService memberService;

    public OAuth2AuthenticationSuccessHandler(Environment environment, OAuth2AuthorizedClientService authorizedClientService, MemberService memberService) {
        this.environment = environment;
        this.authorizedClientService = authorizedClientService;
        this.memberService = memberService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauth2Authentication = (OAuth2AuthenticationToken) authentication;
        log.info("OAuth2 로그인 성공 - username: {}", oauth2Authentication.getName());

        // Refresh token 추출 및 저장
        updateRefreshTokenIfAvailable(oauth2Authentication);

        // Security Context 설정
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(context, request, response);

        // 환경에 따른 프론트엔드 URL 결정
        String targetUrl = determineTargetUrl();
        
        log.info("OAuth2 로그인 후 리디렉션 - URL: {}", targetUrl);
        
        // 프론트엔드로 리디렉션
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void updateRefreshTokenIfAvailable(OAuth2AuthenticationToken authentication) {
        try {
            String registrationId = authentication.getAuthorizedClientRegistrationId();
            String principalName = authentication.getName();
            
            log.info("🔍 Refresh token 조회 시도 - Registration: {}, Principal: {}", registrationId, principalName);
            
            OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(registrationId, principalName);
            
            if (authorizedClient != null) {
                log.info("✅ OAuth2AuthorizedClient 발견");
                log.info("📋 AccessToken: {}", authorizedClient.getAccessToken() != null ? "존재" : "null");
                log.info("📋 RefreshToken: {}", authorizedClient.getRefreshToken() != null ? "존재" : "null");
                
                OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();
                
                if (refreshToken != null) {
                    log.info("🎉 Refresh token 발견! 값: {}", refreshToken.getTokenValue().substring(0, 10) + "...");
                    
                    // 사용자 정보에서 이메일 추출
                    Object emailObj = authentication.getPrincipal().getAttributes().get("email");
                    if (emailObj != null) {
                        String email = emailObj.toString();
                        Member member = memberService.getmemberByEmail(email);
                        
                        if (member != null && member.getOauth() != null) {
                            // 기존 OAuth 정보로 업데이트 (refresh token만 추가)
                            memberService.attachOAuthToMember(
                                member,
                                member.getOauth().getProvider(),
                                member.getOauth().getProviderId(),
                                member.getOauth().getAccessToken(),
                                refreshToken.getTokenValue(), // 새로운 refresh token
                                member.getOauth().getTokenType(),
                                member.getOauth().getExpiresIn(),
                                member.getOauth().getScope()
                            );
                            log.info("💾 Refresh token 저장 완료 - Member ID: {}", member.getId());
                        } else {
                            log.warn("⚠️ Member 또는 OAuth 정보가 없음 - Email: {}", email);
                        }
                    }
                } else {
                    log.warn("❌ Refresh token이 null입니다");
                }
            } else {
                log.warn("❌ OAuth2AuthorizedClient가 null입니다");
                
                // 대안: 모든 저장된 클라이언트 확인
                log.info("🔍 대안 방법: Principal attributes 확인");
                Object emailObj = authentication.getPrincipal().getAttributes().get("email");
                if (emailObj != null) {
                    String email = emailObj.toString();
                    log.info("📧 이메일: {}", email);
                }
            }
        } catch (Exception e) {
            log.error("❌ Refresh token 업데이트 중 오류 발생", e);
        }
    }

    private String determineTargetUrl() {
        // 프로덕션 환경 확인 (prod 프로파일 또는 특정 조건)
        boolean isProduction = environment.acceptsProfiles(Profiles.of("prod"));

        String baseUrl = isProduction ? frontendProdUrl : frontendDevUrl;
        String targetUrl = baseUrl + "?login=success";
        
        log.info("환경 구분 - Production: {}, Target URL: {}", isProduction, targetUrl);
        
        return targetUrl;
    }
}