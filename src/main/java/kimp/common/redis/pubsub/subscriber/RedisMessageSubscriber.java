package kimp.common.redis.pubsub.subscriber;

/**
 * Redis Pub/Sub 메시지 구독 인터페이스
 *
 * 단일 책임 원칙(SRP)에 따라 Redis 메시지 수신 및 처리 기능만 담당
 *
 * 구현 클래스는 handleMessage() 메서드를 오버라이드하여
 * 도메인별 메시지 처리 로직을 구현해야 함
 *
 * 사용 예시:
 * - ChatRedisSubscriber: 채팅 메시지 수신 → WebSocket 전송
 * - NoticeRedisSubscriber: 공지사항 수신 → WebSocket 전송
 */
public interface RedisMessageSubscriber<T> {

    /**
     * 구독한 채널에서 메시지 수신 시 호출
     *
     * @param channel 메시지가 발행된 채널명
     * @param message 수신한 메시지 객체
     */
    void handleMessage(String channel, T message);

    /**
     * 구독할 채널 패턴 반환
     *
     * @return Redis 채널 패턴 (예: "chat:*", "notice:*")
     */
    String getChannelPattern();

    /**
     * 메시지 타입 반환 (역직렬화용)
     *
     * @return 메시지 클래스 타입
     */
    Class<T> getMessageType();
}
