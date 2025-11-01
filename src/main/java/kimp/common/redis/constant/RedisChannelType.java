package kimp.common.redis.constant;

import kimp.market.Enum.MarketType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Redis Pub/Sub 채널 타입 정의
 *
 * Redis Pub/Sub에서 사용하는 모든 채널을 중앙에서 관리
 * 1. 채널명 오타 방지
 * 2. 채널 네이밍 일관성 유지
 * 3. Publisher와 Subscriber 간 채널 불일치 방지
 * 4. 채널 문서화
 */
@Getter
@RequiredArgsConstructor
public enum RedisChannelType {

    // ===== 공지사항 관련 채널 =====
    /**
     * 공지사항 채널
     * - Pattern: notice:{marketType}
     * - 예시: notice:upbit, notice:binance, notice:bithumb, notice:coinone
     * - 용도: 새로운 공지사항 발생 시 분산 서버 간 이벤트 전파
     */
    NOTICE("notice:%s"),

    /**
     * 공지사항 채널 패턴 (구독용)
     * - Pattern: notice:*
     * - 용도: 모든 거래소의 공지사항 구독
     */
    NOTICE_ALL("notice:*"),

    // ===== 채팅 관련 채널 =====
    /**
     * 채팅 메시지 채널
     * - Channel: kimprun:chat:messages
     * - 용도: 분산 서버 간 채팅 메시지 동기화
     */
    CHAT_MESSAGES("kimprun:chat:messages");

    private final String channelPattern;

    /**
     * 파라미터 없이 채널명 생성
     *
     * @return Redis 채널명
     */
    public String getChannel() {
        return channelPattern;
    }

    /**
     * 단일 파라미터로 채널명 생성
     *
     * @param param 파라미터
     * @return Redis 채널명
     */
    public String getChannel(String param) {
        return String.format(channelPattern, param);
    }

    /**
     * MarketType으로 채널명 생성
     *
     * @param marketType 거래소 타입
     * @return Redis 채널명
     */
    public String getChannel(MarketType marketType) {
        return String.format(channelPattern, marketType.name().toLowerCase());
    }

    /**
     * 복수 파라미터로 채널명 생성
     *
     * @param params 파라미터들
     * @return Redis 채널명
     */
    public String getChannel(Object... params) {
        return String.format(channelPattern, params);
    }
}
