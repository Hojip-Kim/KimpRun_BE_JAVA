package kimp.security;

import kimp.security.user.service.CustomUserDetailService;
import kimp.user.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    /*
    * @TODO
    *   request matcher 환경변수설정
    * */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .httpBasic(basic -> basic.disable())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/login","/register","/member", "/member/register","/member/login", "/member/email-auth", "/user", "/user/**","/user/sign-up","/user/redirect", "/upbit","/upbit/**", "/binance", "/binance/**", "/websocket", "/websocket/**", "/market","/market/**", "market/first/**", "market/first/data", "/chat/**","/chat/allLog", "/chat/test", "/chatService/**", "/chatService", "/category", "/category/**")
                        .permitAll()
                .anyRequest()
                .authenticated())
                .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/user/redirect", true).permitAll())
                .logout(logout -> logout.logoutUrl("/logout").permitAll());


    return httpSecurity.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:3000"); // 허용할 출처
        config.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        config.addAllowedHeader("*"); // 모든 헤더 허용
        config.setAllowCredentials(true); // 자격증명 포함 (세션, 쿠키 등)
        config.addExposedHeader("Location"); // 리다이렉트 헤더 노출

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
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
