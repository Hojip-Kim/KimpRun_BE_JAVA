package kimp.common.redis.constant;

import kimp.market.Enum.MarketType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Redis 키 타입 정의
 *
 * Redis에서 사용하는 모든 키를 중앙에서 관리하여
 * 1. 오타 방지
 * 2. 키 네이밍 일관성 유지
 * 3. 키 변경 시 영향 범위 파악 용이
 * 4. 키 문서화
 */
@Getter
@RequiredArgsConstructor
public enum RedisKeyType {

    // ===== 공지사항 관련 =====
    /**
     * 공지사항 URL 캐시 (Sorted Set)
     * - Key: notices:{marketType}
     * - Value: 공지사항 URL
     * - Score: Unix timestamp (공지사항 날짜)
     */
    NOTICES("notices:%s"),

    // ===== 뉴스 관련 =====
    /**
     * 코인니스 속보 뉴스 ID 캐시 (SET)
     * - Key: news:coinness:breaking:ids
     * - Value: 뉴스 ID
     */
    NEWS_COINNESS_BREAKING_IDS("news:coinness:breaking:ids"),

    /**
     * 코인니스 기사 뉴스 ID 캐시 (SET)
     * - Key: news:coinness:articles:ids
     * - Value: 뉴스 ID
     */
    NEWS_COINNESS_ARTICLES_IDS("news:coinness:articles:ids"),

    /**
     * 블루밍비트 뉴스 SEQ 캐시 (SET)
     * - Key: news:bloomingbit:seqs
     * - Value: 뉴스 SEQ
     */
    NEWS_BLOOMINGBIT_SEQS("news:bloomingbit:seqs"),

    /**
     * 뉴스 목록 캐시 (String - JSON)
     * - Key: news:list:{params}
     * - Value: 뉴스 목록 JSON
     * - TTL: 설정된 시간
     */
    NEWS_LIST("news:list:%s"),

    /**
     * 뉴스 상세 캐시 (String - JSON)
     * - Key: news:detail:{id}
     * - Value: 뉴스 상세 JSON
     * - TTL: 설정된 시간
     */
    NEWS_DETAIL("news:detail:%s"),

    /**
     * 뉴스 소스별 조회 캐시 (String - JSON)
     * - Key: news:source:{newsSource}:{sourceSequenceId}
     * - Value: 뉴스 상세 JSON
     * - TTL: 설정된 시간
     */
    NEWS_SOURCE("news:source:%s:%s"),

    // ===== 사용자 인증 관련 =====
    /**
     * 이메일 인증 코드 (String)
     * - Key: {email}
     * - Value: 인증 코드
     * - TTL: 5분
     */
    EMAIL_VERIFICATION("%s"),

    // ===== 세션 관련 =====
    /**
     * Spring Session 키 (자동 생성)
     * - Key: spring:session:sessions:{sessionId}
     * - Spring Session에서 자동 관리
     */
    SPRING_SESSION("spring:session:sessions:%s");

    private final String keyPattern;

    /**
     * 파라미터 없이 키 생성
     *
     * @return Redis 키
     */
    public String getKey() {
        return keyPattern;
    }

    /**
     * 단일 파라미터로 키 생성
     *
     * @param param 파라미터
     * @return Redis 키
     */
    public String getKey(String param) {
        return String.format(keyPattern, param);
    }

    /**
     * MarketType으로 키 생성
     *
     * @param marketType 거래소 타입
     * @return Redis 키
     */
    public String getKey(MarketType marketType) {
        return String.format(keyPattern, marketType.name().toLowerCase());
    }

    /**
     * 복수 파라미터로 키 생성
     *
     * @param params 파라미터들
     * @return Redis 키
     */
    public String getKey(Object... params) {
        return String.format(keyPattern, params);
    }
}
