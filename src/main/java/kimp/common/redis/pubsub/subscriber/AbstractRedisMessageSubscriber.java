package kimp.common.redis.pubsub.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * Redis Pub/Sub 메시지 구독 추상 클래스
 *
 * Template Method 패턴을 사용하여 공통 로직 제공
 * 1. Redis 메시지 수신
 * 2. JSON 역직렬화
 * 3. 도메인별 처리 위임 (handleMessage)
 */
@Slf4j
public abstract class AbstractRedisMessageSubscriber<T> implements RedisMessageSubscriber<T>, MessageListener {

    protected final ObjectMapper objectMapper;

    protected AbstractRedisMessageSubscriber(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        log.info("{} 초기화 - 구독 채널: {}", getClass().getSimpleName(), getChannelPattern());
    }

    /**
     * Redis MessageListener 인터페이스 구현
     * Redis에서 메시지 수신 시 자동 호출
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(message.getChannel());
            String body = new String(message.getBody());

            log.debug("Redis 메시지 수신: channel={}", channel);

            // JSON → 객체 역직렬화
            T messageObject = objectMapper.readValue(body, getMessageType());

            // 도메인별 처리 위임
            handleMessage(channel, messageObject);

        } catch (Exception e) {
            log.error("Redis 메시지 처리 실패: {}", e.getMessage(), e);
        }
    }

    /**
     * 하위 클래스에서 구현할 메시지 처리 메서드
     *
     * @param channel 메시지가 발행된 채널명
     * @param message 역직렬화된 메시지 객체
     */
    @Override
    public abstract void handleMessage(String channel, T message);

    /**
     * 구독할 채널 패턴 (하위 클래스에서 구현)
     */
    @Override
    public abstract String getChannelPattern();

    /**
     * 메시지 타입 (하위 클래스에서 구현)
     */
    @Override
    public abstract Class<T> getMessageType();
}
