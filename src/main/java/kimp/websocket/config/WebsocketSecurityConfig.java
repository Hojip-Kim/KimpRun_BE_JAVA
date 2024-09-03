package kimp.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager.Builder;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;

@Configuration
@EnableWebSocketSecurity
public class WebsocketSecurityConfig {

    @Bean
    AuthorizationManager<Message<?>> messageAuthorizationManager(Builder messages) {
        messages.anyMessage().permitAll();
        return messages.build();
    }
}
