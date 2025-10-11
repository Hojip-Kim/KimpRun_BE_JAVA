package kimp.news.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 뉴스 응답 DTO
 * 클라이언트에게 반환되는 뉴스 데이터
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponseDto {

    /** 뉴스 ID */
    private Long id;

    /** 뉴스 소스 (예: "BloomingBit", "Coinness") */
    private String newsSource;

    /** 뉴스 제목 */
    private String title;

    /** 썸네일 이미지 URL */
    private String thumbnail;

    /** 짧은 내용 (리스트용 요약) */
    private String shortContent;

    /** 원본 뉴스 URL */
    private String sourceUrl;

    /** 뉴스 생성 시각 (epoch 밀리초) */
    private Long createEpochMillis;

    /** 레코드 생성 시각 */
    private LocalDateTime createdAt;

    // 상세 조회 시 추가 필드들

    /** 뉴스 타입 (예: 속보, 일반기사) */
    private String newsType;

    /** 뉴스 지역 (예: 국내, 해외) */
    private String region;

    /** 감정 분석 결과 (예: POSITIVE, NEGATIVE, NEUTRAL) */
    private String sentiment;

    /** 신규 뉴스 여부 */
    private Boolean isNew;

    /** 헤드라인 뉴스 여부 */
    private Boolean isHeadline;

    /** 뉴스 키워드 목록 (예: 비트코인, 이더리움) */
    private List<String> keywords;

    /** 뉴스 요약 목록 (핵심 내용 요약) */
    private List<String> summaries;

    /** 뉴스 인사이트 목록 (시장 영향 분석, 전문가 의견 등) */
    private List<String> insights;
}
