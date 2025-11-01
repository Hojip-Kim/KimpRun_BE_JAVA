package kimp.common.redis.pubsub.publisher;

/**
 * Redis Pub/Sub 메시지 발행 인터페이스
 *
 * 단일 책임 원칙(SRP)에 따라 Redis 메시지 발행 기능만 담당
 */
public interface RedisMessagePublisher {

    /**
     * 특정 채널로 메시지 발행
     *
     * @param channel Redis 채널명 (예: "chat:messages", "notice:upbit")
     * @param message 발행할 메시지 객체 (JSON으로 직렬화됨)
     */
    void publish(String channel, Object message);

    /**
     * 특정 채널로 메시지 발행 (현재 서버 인스턴스 ID 포함)
     * 중복 처리 방지를 위해 발행 서버 식별자를 함께 전송
     *
     * @param channel Redis 채널명
     * @param message 발행할 메시지 객체
     * @param includeInstanceId true면 instanceId를 메시지에 포함
     */
    void publish(String channel, Object message, boolean includeInstanceId);

    /**
     * 현재 서버 인스턴스 ID 반환
     *
     * @return 서버 인스턴스 고유 식별자
     */
    String getInstanceId();
}
