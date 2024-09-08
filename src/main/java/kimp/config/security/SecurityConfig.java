package kimp.config.security;

import kimp.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final UserService userService;

    @Value("${custom.secure.paths}")
    private String[] securePaths;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    UserAuthenticationFailureHandler getFailureHandler() {
        return new UserAuthenticationFailureHandler();
    }

    /*
    * @TODO
    *   request matcher 환경변수설정
    * */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        System.out.println(removeParentheses(permissionPoint));
        httpSecurity
                .httpBasic(basic -> basic.disable())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(req -> req
//                        .requestMatchers(securePaths)
                        .requestMatchers("/","/member", "/member/register","/member/login", "/member/email-auth", "/user", "/user/**", "/upbit","/upbit/**", "/binance", "/binance/**", "/upbitSSE", "/upbitSSE/**", "/websocket", "/websocket/**", "/market","/market/**", "market/first/**", "market/first/data", "/chat/**","/chat/allLog", "/chat/test", "/chatService/**", "/chatService")
                        .permitAll()
                .anyRequest()
                .authenticated())
                .formLogin(form -> form.loginPage("/member/login").failureHandler(getFailureHandler()).permitAll())
                .logout(logout -> logout.permitAll());


    return httpSecurity.build();
    }

    private static String removeParentheses(String text){
        text.replace("{", "");
        text.replace("}", "");

        System.out.println(text);
        return text;
    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        configuration.addAllowedOrigin("*");
//        configuration.addAllowedHeader("*");
//        configuration.addAllowedMethod("*");
//        configuration.setAllowCredentials(true);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user =
                User.withDefaultPasswordEncoder()
                        .username("user")
                        .password("password")
                        .roles("USER")
                        .build();

        return new InMemoryUserDetailsManager(user);
    }
}
