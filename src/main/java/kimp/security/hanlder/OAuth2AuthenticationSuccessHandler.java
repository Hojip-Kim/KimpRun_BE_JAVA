package kimp.security.hanlder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kimp.user.entity.Member;
import kimp.user.service.member.MemberService;
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
import java.net.URL;

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

        // Refresh token 추출 및 저장
        updateRefreshTokenIfAvailable(oauth2Authentication);

        // Security Context 설정
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(context, request, response);

        // 환경에 따른 프론트엔드 URL 결정
        String targetUrl = determineTargetUrl(request);

        log.info("OAuth2 로그인 후 리디렉션 - URL: {}", targetUrl);

        // 프론트엔드로 리디렉션
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void updateRefreshTokenIfAvailable(OAuth2AuthenticationToken authentication) {
        try {
            String registrationId = authentication.getAuthorizedClientRegistrationId();
            String principalName = authentication.getName();

            OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(registrationId, principalName);
            
            if (authorizedClient != null) {

                OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();
                
                if (refreshToken != null) {

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
                Object emailObj = authentication.getPrincipal().getAttributes().get("email");
                if (emailObj != null) {
                    String email = emailObj.toString();
                }
            }
        } catch (Exception e) {
            log.error("❌ Refresh token 업데이트 중 오류 발생", e);
        }
    }

    private String determineTargetUrl(HttpServletRequest request) {
        // Referer 또는 Origin 헤더에서 요청이 온 도메인 확인
        String referer = request.getHeader("Referer");
        String origin = request.getHeader("Origin");

        // Origin이나 Referer에서 도메인 추출
        String requestOrigin = origin != null ? origin :
                              (referer != null ? extractOriginFromReferer(referer) : null);

        log.info("OAuth2 요청 출처 - Origin: {}, Referer: {}", origin, referer);

        // 로컬 개발 환경 (localhost, www.localhost, 127.0.0.1 등 모든 로컬 주소)
        if (requestOrigin != null && (requestOrigin.contains("localhost") || requestOrigin.contains("127.0.0.1"))) {
            return requestOrigin + "?login=success";
        }

        // kimprun.com 도메인 (www, api 등 서브도메인 포함)
        if (requestOrigin != null && requestOrigin.contains("kimprun.com")) {
            return requestOrigin + "?login=success";
        }

        // 프로덕션 환경 확인 (기본값)
        boolean isProduction = environment.acceptsProfiles(Profiles.of("prod"));
        String baseUrl = isProduction ? frontendProdUrl : frontendDevUrl;
        String targetUrl = baseUrl + "?login=success";

        log.info("환경 구분 - Production: {}, Target URL: {}", isProduction, targetUrl);

        return targetUrl;
    }

    private String extractOriginFromReferer(String referer) {
        try {
            URL url = new java.net.URL(referer);
            return url.getProtocol() + "://" + url.getHost() + (url.getPort() != -1 && url.getPort() != 80 && url.getPort() != 443 ? ":" + url.getPort() : "");
        } catch (Exception e) {
            log.warn("Referer URL 파싱 실패: {}", referer);
            return null;
        }
    }
}