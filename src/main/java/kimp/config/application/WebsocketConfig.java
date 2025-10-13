package kimp.config.application;

import kimp.chat.service.ChatTrackingService;
import kimp.websocket.interceptor.ChatHandshakeInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Arrays;


@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${environment.websocket.allowOrigins}")
    private String allowOrigins;
    private final ChatTrackingService chatTrackingService;

    public WebsocketConfig(ChatTrackingService chatTrackingService) {
        this.chatTrackingService = chatTrackingService;
    }

    // STOMP 하트비트를 위한 TaskScheduler Bean
    @Bean
    public TaskScheduler heartBeatScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("wss-heartbeat-");
        scheduler.initialize();
        return scheduler;
    }

    // stomp 엔드포인트 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("allowOrigins : " + allowOrigins);

        String[] origins = Arrays.stream(allowOrigins.split(","))
                .map(String::trim)
                .toArray(String[]::new);

        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(origins)
                .addInterceptors(
                    new HttpSessionHandshakeInterceptor(), // HTTP 세션 정보 전달
                    new ChatHandshakeInterceptor(chatTrackingService)         // 커스텀 핸드셰이크 인터셉터
                );
    }

    // 인메모리 브로커 활성화 설정
    // prefix 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue", "/user")
                .setHeartbeatValue(new long[]{10000, 10000}) // 10초 하트비트
                .setTaskScheduler(heartBeatScheduler()); // TaskScheduler 설정
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(1024 * 1024 * 10) // 10메가
                .setSendBufferSizeLimit(1024 * 1024 * 10) // 10메가
                .setSendTimeLimit(30_000) // 30초로 증가
                .setTimeToFirstMessage(30_000); // 첫 메시지 대기 시간 30초
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Spring Security 기본 CSRF 검증 사용 (자동 등록됨)
    }

//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        log.info("allowOrigins : " + allowOrigins);
//        registry
//                .addHandler(marketDataWebsocketHandler, "/marketData")
//                .setAllowedOrigins(allowOrigins);
//        registry
//                .addHandler(chatWebSocketHandler, "/chatService")
//                .setAllowedOrigins(allowOrigins);
//        registry
//                .addHandler(marketInfoHandler, "/marketInfo")
//                .setAllowedOrigins(allowOrigins);
//
//    }
}
