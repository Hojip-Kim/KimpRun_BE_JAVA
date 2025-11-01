package kimp.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.common.redis.constant.RedisChannelType;
import kimp.config.redis.handler.RedisMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis Pub/Sub를 이용한 분산 WebSocket 메시지 브로커 설정
 * 
 * 분산 서버 환경에서 여러 인스턴스 간 채팅 메시지 동기화를 위한 설정
 * - 각 서버 인스턴스는 Redis의 특정 채널을 구독
 * - 메시지 발행 시 모든 구독 서버로 전파
 * - 수신한 메시지를 해당 서버의 WebSocket 클라이언트에게 전달
 */
@Configuration
@Slf4j
public class RedisMessageBrokerConfig {
    
    /**
     * 채팅 메시지 전용 RedisTemplate 설정
     * JSON 직렬화를 사용하여 객체 전송
     */
    @Bean
    public RedisTemplate<String, Object> chatRedisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // JSON 직렬화 설정
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
        
        // Key는 String, Value는 JSON으로 직렬화
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        
        template.afterPropertiesSet();
        return template;
    }
    
    /**
     * Redis 메시지 리스너 컨테이너 (채팅용)
     * Redis pub/sub 메시지를 수신하고 처리
     */
    @Bean
    public RedisMessageListenerContainer chatRedisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter chatMessageListenerAdapter,
            ChannelTopic chatChannelTopic) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // 채팅 채널 구독 설정
        container.addMessageListener(chatMessageListenerAdapter, chatChannelTopic);

        return container;
    }
    
    /**
     * 채팅 메시지 리스너 어댑터
     * RedisMessageHandler의 handleMessage 메소드를 호출
     */
    @Bean
    public MessageListenerAdapter chatMessageListenerAdapter(RedisMessageHandler redisMessageHandler) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(redisMessageHandler, "handleMessage");
        adapter.setSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return adapter;
    }
    
    /**
     * 채팅 채널 토픽
     */
    @Bean
    public ChannelTopic chatChannelTopic() {
        return new ChannelTopic(RedisChannelType.CHAT_MESSAGES.getChannel());
    }
}