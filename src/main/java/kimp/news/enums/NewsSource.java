package kimp.news.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 뉴스 소스 열거형
 * 각 뉴스 제공 업체를 나타냄
 */
@Getter
@RequiredArgsConstructor
public enum NewsSource {

    BLOOMING_BIT("BloomingBit", "블루밍비트"),
    COINNESS("Coinness", "코인니스");

    private final String code;
    private final String description;

    /**
     * 코드 값으로 NewsSource 찾기
     * @param code 뉴스 소스 코드
     * @return NewsSource enum
     */
    public static NewsSource fromCode(String code) {
        for (NewsSource source : values()) {
            if (source.getCode().equals(code)) {
                return source;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 뉴스 소스입니다: " + code);
    }
}
