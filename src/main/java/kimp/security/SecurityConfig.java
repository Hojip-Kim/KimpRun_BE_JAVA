package kimp.security;

import kimp.security.user.CustomAuthenticationFilter;
import kimp.security.user.CustomLogoutSuccessHandler;
import kimp.security.user.service.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailService customUserDetailService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(CustomUserDetailService customUserDetailService, PasswordEncoder passwordEncoder) {
        this.customUserDetailService = customUserDetailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        AuthenticationManager authenticationManager = authenticationManager(passwordEncoder);

        // CustomAuthenticationFilter 생성 및 설정
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager);
        customAuthenticationFilter.setFilterProcessesUrl("/login");


        http
                // CORS 설정 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                // 세션 관리 설정
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                )
                // 요청 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers(HttpMethod.POST, "/login", "/user/sign-up").permitAll()
                                .requestMatchers(HttpMethod.POST, "/**").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/**").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/**").authenticated()
                                .anyRequest().permitAll()
                )
                // 기본 폼로그인 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                .logout((logout) -> logout.logoutUrl("/logout")
                        .logoutSuccessHandler(new CustomLogoutSuccessHandler())
                        .invalidateHttpSession(true) // 세션 무효화
                        .deleteCookies("JSESSIONID")
                )
                // CustomAuthenticationFilter를 필터 체인에 추가
                .addFilterAt(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://kimp-run-fe-jvmn-dev.vercel.app", "http://localhost", "http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailService);
        authProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authProvider);
    }

}
