package kimp.common.redis.pubsub.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Redis Pub/Sub 메시지 발행 구현체
 *
 * JSON 직렬화를 사용하여 객체를 Redis 채널로 발행
 */
@Component
@Slf4j
public class RedisMessagePublisherImpl implements RedisMessagePublisher {

    private final RedisTemplate<String, Object> redisPubSubTemplate;
    private final ObjectMapper objectMapper;
    private final String instanceId;

    public RedisMessagePublisherImpl(
            @Qualifier("redisPubSubTemplate") RedisTemplate<String, Object> redisPubSubTemplate,
            ObjectMapper objectMapper) {
        this.redisPubSubTemplate = redisPubSubTemplate;
        this.objectMapper = objectMapper;
        this.instanceId = generateInstanceId();
        log.info("RedisMessagePublisher 초기화 - Instance ID: {}", instanceId);
    }

    @Override
    public void publish(String channel, Object message) {
        publish(channel, message, false);
    }

    @Override
    public void publish(String channel, Object message, boolean includeInstanceId) {
        try {
            Object payload = message;

            // 인스턴스 ID 포함 옵션
            if (includeInstanceId) {
                payload = wrapWithInstanceId(message);
            }

            // Redis Pub/Sub으로 발행
            redisPubSubTemplate.convertAndSend(channel, payload);
        } catch (Exception e) {
            log.error("Redis 메시지 발행 실패: channel={}, error={}", channel, e.getMessage(), e);
        }
    }

    @Override
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * 메시지를 인스턴스 ID와 함께 래핑
     */
    private Map<String, Object> wrapWithInstanceId(Object message) {
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("instanceId", instanceId);
        wrapper.put("message", message);
        return wrapper;
    }

    /**
     * 서버 인스턴스 고유 ID 생성
     * Kubernetes Pod명 또는 로컬 환경 식별자
     */
    private String generateInstanceId() {
        String hostname = System.getenv("HOSTNAME"); // Kubernetes Pod명
        if (hostname != null && !hostname.isEmpty()) {
            return hostname;
        }
        // 로컬 환경
        return "local-" + System.currentTimeMillis();
    }
}
