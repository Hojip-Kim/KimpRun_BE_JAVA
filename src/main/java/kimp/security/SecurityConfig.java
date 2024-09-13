package kimp.security;

import kimp.security.user.service.CustomUserDetailService;
import kimp.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

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
                .httpBasic(basic -> basic.disable())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(req -> req
//                        .requestMatchers(securePaths)
                        .requestMatchers("/","/member", "/member/register","/member/login", "/member/email-auth", "/user", "/user/**", "/upbit","/upbit/**", "/binance", "/binance/**", "/upbitSSE", "/upbitSSE/**", "/websocket", "/websocket/**", "/market","/market/**", "market/first/**", "market/first/data", "/chat/**","/chat/allLog", "/chat/test", "/chatService/**", "/chatService", "/category", "/category/**")
                        .permitAll()
                .anyRequest()
                .authenticated())
                .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/home", true).permitAll())
                .logout(logout -> logout.logoutUrl("/logout").permitAll());


    return httpSecurity.build();
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
