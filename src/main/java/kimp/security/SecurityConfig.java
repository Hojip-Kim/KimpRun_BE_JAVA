package kimp.security;

import kimp.security.user.CustomAuthenticationFilter;
import kimp.security.user.service.CustomUserDetailService;
import kimp.user.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;

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

    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationConfiguration authConfig) throws Exception {
        // AuthenticationManager를 AuthenticationConfiguration을 통해 가져옴
        AuthenticationManager authenticationManager = authConfig.getAuthenticationManager();

        // CustomAuthenticationFilter 생성 및 설정
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager);
        customAuthenticationFilter.setFilterProcessesUrl("/login");


        http
                // CORS 설정 적용
                .cors(c -> {
                            CorsConfigurationSource source = request -> {
                                CorsConfiguration config = new CorsConfiguration();
                                config.setAllowCredentials(true);
                                config.setAllowedOrigins(Arrays.asList("http://localhost:3000",
                                        "kimp-run-fe-jvmn-ffjqw3bd2-hojip-kims-projects.vercel.app",
                                        "kimp-run-fe-jvmn.vercel.app",
                                        "kimp-run-fe-jvmn-dev.vercel.app",
                                        "2f68-2001-2d8-ef42-eff1-a49a-9d39-ce9f-f068.ngrok-free.app"));
                                config.setAllowedMethods(Arrays.asList("HEAD", "POST", "GET", "DELETE", "PUT", "OPTIONS"));
                                config.setAllowedHeaders(Arrays.asList("*"));
                                return config;
                            };
                            c.configurationSource(source);
                        }
                    )
                // CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                // 세션 관리 설정
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                // 요청 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/login",
                                "/user/sign-up",
                                "/user",
                                "/upbit/**",
                                "/binance/**",
                                "/websocket/**",
                                "/market/**",
                                "/chat/**",
                                "/chatService/**",
                                "/category/**",
                                "/error"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // 기본 폼로그인 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                // 로그아웃 비활성화
                .logout(AbstractHttpConfigurer::disable)
                // CustomAuthenticationFilter를 필터 체인에 추가
                .addFilterAt(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    // AuthenticationManager를 직접 빈으로 등록하지 않음
    // AuthenticationConfiguration을 통해 주입받음
//    @Bean
//    public AuthenticationManager authenticationManager() {
//        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//        daoAuthenticationProvider.setUserDetailsService(customUserDetailService);
//        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
//        return new ProviderManager(daoAuthenticationProvider);
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
