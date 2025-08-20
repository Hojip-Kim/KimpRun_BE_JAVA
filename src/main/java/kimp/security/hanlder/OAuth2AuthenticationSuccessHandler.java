package kimp.security.hanlder;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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

    public OAuth2AuthenticationSuccessHandler(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauth2Authentication = (OAuth2AuthenticationToken) authentication;
        log.info("OAuth2 로그인 성공 - username: {}", oauth2Authentication.getName());

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

    private String determineTargetUrl() {
        // 프로덕션 환경 확인 (prod 프로파일 또는 특정 조건)
        boolean isProduction = environment.acceptsProfiles(Profiles.of("prod"));

        String baseUrl = isProduction ? frontendProdUrl : frontendDevUrl;
        String targetUrl = baseUrl + "?login=success";
        
        log.info("환경 구분 - Production: {}, Target URL: {}", isProduction, targetUrl);
        
        return targetUrl;
    }
}