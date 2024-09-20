package kimp.security;

import kimp.security.user.CustomAuthenticationFilter;
import kimp.security.user.service.CustomUserDetailService;
import kimp.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final UserService userService;
    private final CustomUserDetailService customUserDetailService;

    public SecurityConfig(UserService userService, CustomUserDetailService customUserDetailService) {
        this.userService = userService;
        this.customUserDetailService = customUserDetailService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        // SecurityContextRepository 설정
        SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

        // CustomAuthenticationFilter 생성 및 설정
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager);
        customAuthenticationFilter.setFilterProcessesUrl("/login");
        customAuthenticationFilter.setSecurityContextRepository(securityContextRepository);

        // CORS 설정
        http.cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of(
                            "http://localhost:8080",
                            "http://localhost:3000",
                            "http://127.0.0.1:3000",
                            "http://127.0.0.1:8080",
                            "localhost",
                            "https://kimp-run-fe-jvmn-ffjqw3bd2-hojip-kims-projects.vercel.app",
                            "https://kimp-run-fe-jvmn.vercel.app",
                            "https://2f68-2001-2d8-ef42-eff1-a49a-9d39-ce9f-f068.ngrok-free.app"
                    ));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                // CSRF 비활성화
                .csrf(csrf -> csrf.disable())
                // 요청 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/login",
                                "/user/sign-up",
                                "/user",
                                "/upbit",
                                "/upbit/**",
                                "/binance",
                                "/binance/**",
                                "/websocket",
                                "/websocket/**",
                                "/market",
                                "/market/**",
                                "market/first/**",
                                "market/first/data",
                                "/chat/**",
                                "/chat/allLog",
                                "/chat/test",
                                "/chatService/**",
                                "/chatService",
                                "/category",
                                "/category/**",
                                "/error" // /error 추가
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // 세션 관리 설정
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                // CustomAuthenticationFilter를 필터 체인에 추가
                .addFilterAt(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 기본 폼 로그인 비활성화
                .formLogin(form -> form.disable());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(customUserDetailService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailService(userService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
