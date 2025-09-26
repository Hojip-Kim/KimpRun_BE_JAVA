package kimp.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisDistributedLockConfig {
    
    @Value("${spring.application.name}")
    private String applicationName;
    
    @Value("${server.port}")
    private String serverPort;
    
    @Bean
    public RedisTemplate<String, Object> distributedLockRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // String 직렬화 설정
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericToStringSerializer<>(Object.class));
        
        template.afterPropertiesSet();
        return template;
    }
    
    /**
     * 현재 서버 인스턴스 고유 ID 생성
     */
    @Bean
    public String serverInstanceId() {
        String hostName = System.getenv("HOSTNAME"); // 쿠버네티스 파드 이름
        if (hostName == null) {
            hostName = System.getProperty("user.name", "unknown");
        }
        return applicationName + ":" + hostName + ":" + serverPort;
    }
}