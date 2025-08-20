package kimp.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST,"/login", "/user/sign-up", "/user/email", "/user/email/verify", "/batch/cmc/sync", "/logout").permitAll()
                        .requestMatchers(HttpMethod.POST, "/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/**").authenticated()
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
                .addFilterAt(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(cookieGuardFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
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
