package kimp.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.web.csrf.CsrfChannelInterceptor;


@Configuration
@EnableWebSocketSecurity
public class WebsocketSecurityConfig {

    @Bean
    AuthorizationManager<Message<?>> messageAuthorizationManager(
            MessageMatcherDelegatingAuthorizationManager.Builder messages) {

        messages.simpTypeMatchers(
                SimpMessageType.CONNECT,
                SimpMessageType.HEARTBEAT,
                SimpMessageType.DISCONNECT,
                SimpMessageType.OTHER
        ).permitAll();

        messages.nullDestMatcher().permitAll();
        messages.simpSubscribeDestMatchers("/topic/**", "/queue/**", "/user/**").permitAll();
        messages.simpMessageDestMatchers("/app/**").permitAll();
        messages.anyMessage().permitAll();

        return messages.build();
    }

    @Bean
    @Primary
    public CsrfChannelInterceptor csrfChannelInterceptor() {
        return new CsrfChannelInterceptor();
    }



}
