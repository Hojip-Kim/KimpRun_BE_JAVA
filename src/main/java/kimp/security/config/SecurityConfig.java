package kimp.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kimp.exception.response.ApiResponse;
import kimp.security.filter.AnonymousCookieGuardFilter;
import kimp.security.filter.CustomAuthenticationFilter;
import kimp.security.hanlder.CustomLogoutSuccessHandler;
import kimp.security.hanlder.OAuth2AuthenticationSuccessHandler;
import kimp.security.user.service.CustomOAuth2UserService;
import kimp.security.user.service.CustomUserDetailService;
import kimp.user.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.firewall.RequestRejectedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    private final CustomUserDetailService UserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final CustomOAuth2UserService oauth2memberService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final MemberService memberService;
    @Value("${app.cookie.domain}")
    public String cookieDomain;   // prod: "kimprun.com", dev/local: 빈 값


    public SecurityConfig(CustomUserDetailService customUserDetailservice, PasswordEncoder passwordEncoder, CustomOAuth2UserService customOAuth2memberService, OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandlerImpl, ClientRegistrationRepository clientRegistrationRepository, MemberService memberService) {
        this.UserDetailsService = customUserDetailservice;
        this.passwordEncoder = passwordEncoder;
        this.oauth2memberService = customOAuth2memberService;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandlerImpl;
        this.memberService = memberService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AnonymousCookieGuardFilter cookieGuardFilter) throws Exception {
        AuthenticationManager authenticationManager = authenticationManager(passwordEncoder);

        // CustomAuthenticationFilter 생성 및 설정
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager, memberService);
        customAuthenticationFilter.setFilterProcessesUrl("/login");

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf
                        .csrfTokenRepository(createCsrfTokenRepository())
                        // WebSocket 연결 자체는 CSRF에서 제외하지만, STOMP 메시지는 CsrfChannelInterceptor에서 검증
                        .ignoringRequestMatchers("/ws/**", "/batch/**")
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/csrf/token").permitAll()
                        .requestMatchers(HttpMethod.POST,"/login", "/user/sign-up", "/user/email", "/user/email/verify", "/user/email/new", "/batch/cmc/sync", "/logout", "/declaration").permitAll()
                        .requestMatchers(HttpMethod.POST, "/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/anonymous/member/nickname").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/user/password").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/chat/anon").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/**").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/oauth2/authorization")
                        )
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri("/login/oauth2/code/*"))
                        .userInfoEndpoint(memberInfo -> memberInfo
                                .userService(oauth2memberService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler((request, response, exception) -> {
                            log.error("OAuth 로그인 실패: ", exception);
                            String origin = request.getHeader("Origin");
                            String redirectUrl = (origin != null && origin.startsWith("http://localhost:3000")) 
                                ? "http://localhost:3000/login?error=true"
                                : "https://kimprun.com/login?error=true";
                            response.sendRedirect(redirectUrl);
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(new CustomLogoutSuccessHandler())
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(customAuthenticationEntryPoint())
                )
                .addFilterAt(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(cookieGuardFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CSRF 토큰 리포지토리 생성 (쿠키 경로를 /로 설정하여 JavaScript 접근 허용)
     */
    private CsrfTokenRepository createCsrfTokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookiePath("/"); // JavaScript에서 접근 가능하도록 루트 경로로 변경
        repository.setCookieName("XSRF-TOKEN");
        repository.setHeaderName("X-XSRF-TOKEN");
        repository.setParameterName("_csrf");
        repository.setCookieMaxAge(-1); // 세션 쿠키
        repository.setSecure(false); // HTTPS가 아닌 환경에서도 작동

        return repository;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "https://kimp-run-fe-jvmn-dev.vercel.app", 
            "http://localhost:3000",
            "http://127.0.0.1:3000"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(UserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authProvider);
    }

    /**
     * Custom AuthenticationEntryPoint - /login 경로는 OAuth2 리다이렉트에서 제외
     */
    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            String requestUri = request.getRequestURI();
            log.info("requestUri : {}" , requestUri);
            
            // /login 경로에 대해서는 OAuth2 리다이렉트를 하지 않고 401 응답만 반환
            if ("/login".equals(requestUri)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"result\":\"failure\",\"message\":\"Authentication required for login endpoint\"}");
                response.getWriter().flush();
                return;
            }
            
            // 다른 경로에 대해서는 OAuth2 authorization endpoint로 리다이렉트
            String redirectUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "") + 
                                "/oauth2/authorization/google";
            response.sendRedirect(redirectUrl);
        };
    }

    // cookie가 변조되어 문제가 발생하면 requestRejectedHanlder 발생. 이에따른 적절한 대처 후 response
    @Bean
    public RequestRejectedHandler requestRejectedHandler(ObjectMapper mapper) {
        return (request, response, ex) -> {
            if (response.isCommitted()) return;

            // 문제 쿠키 제거
            ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from("kimprun-token", "")
                    .httpOnly(true)
                    .sameSite("Lax")
                    .path("/")
                    .maxAge(0); // 즉시 삭제

            // prod환경에서 cookie domain이 있음. 이 경우 secure true설정, dev에선 false설정
            if (cookieDomain != null && !cookieDomain.isBlank()) {
                cookieBuilder.domain(cookieDomain).secure(true);
            } else {
                cookieBuilder.secure(false); // http 로컬 개발
            }

            response.addHeader(HttpHeaders.SET_COOKIE, cookieBuilder.build().toString());

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-store");

            ApiResponse body = ApiResponse.error(
                    HttpStatus.BAD_REQUEST.value(),
                    "INVALID_COOKIE",
                    "Malformed or disallowed cookie was sent."
            );

            mapper.writeValue(response.getWriter(), body);
            response.getWriter().flush();
        };
    }
}
