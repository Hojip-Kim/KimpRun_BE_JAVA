package kimp.news.dto.internal.coinness;

import com.fasterxml.jackson.annotation.JsonProperty;
import kimp.news.dto.internal.NewsSourceDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Coinness 속보 뉴스 API 응답 DTO
 * Coinness의 실시간 속보 뉴스 데이터
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoinnessBreakingNewsDto implements NewsSourceDto {

    /** 원본 코드 목록 (뉴스 출처 코드) */
    @JsonProperty("originCodes")
    private List<String> originCodes;

    /** 뉴스 고유 ID (Coinness에서 제공) */
    @JsonProperty("id")
    private Long id;

    /** 카테고리 ID */
    @JsonProperty("categoryId")
    private Integer categoryId;

    /** 뉴스 제목 */
    @JsonProperty("title")
    private String title;

    /** 뉴스 내용 */
    @JsonProperty("content")
    private String content;

    /** 썸네일 이미지 URL */
    @JsonProperty("thumbnailImage")
    private String thumbnailImage;

    /** 본문 이미지 URL */
    @JsonProperty("contentImage")
    private String contentImage;

    /** 빠른 주문 코드 (거래 관련) */
    @JsonProperty("quickOrderCode")
    private String quickOrderCode;

    /** 관련 링크 URL */
    @JsonProperty("link")
    private String link;

    /** 링크 제목 */
    @JsonProperty("linkTitle")
    private String linkTitle;

    /** 강세 지표 (Bull 수치) */
    @JsonProperty("bull")
    private Integer bull;

    /** 약세 지표 (Bear 수치) */
    @JsonProperty("bear")
    private Integer bear;

    /** 강세 투표 수 */
    @JsonProperty("bullCount")
    private Integer bullCount;

    /** 약세 투표 수 */
    @JsonProperty("bearCount")
    private Integer bearCount;

    /** 인용 수 */
    @JsonProperty("quoteCount")
    private Integer quoteCount;

    /** 강세 여부 (true: 강세, false: 약세) */
    @JsonProperty("isBull")
    private Boolean isBull;

    /** 중요 뉴스 여부 */
    @JsonProperty("isImportant")
    private Boolean isImportant;

    /** 관찰 대상 여부 */
    @JsonProperty("isObserve")
    private Boolean isObserve;

    /** 발행 시각 (ISO 8601 형식) */
    @JsonProperty("publishAt")
    private String publishAt;

    /** 댓글 수 */
    @JsonProperty("commentCount")
    private Integer commentCount;

    /** 번역 여부 */
    @JsonProperty("isTranslated")
    private Boolean isTranslated;

    // NewsSourceDto 인터페이스 구현
    @Override
    public Long getSourceSequenceId() {
        return this.id;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getSourceUrl() {
        return this.link != null ? this.link : "https://coinness.com/news/" + this.id;
    }

    @Override
    public Long getCreateEpochMillis() {
        // publishAt을 파싱하여 epoch milliseconds로 변환
        try {
            if (publishAt != null) {
                ZonedDateTime zdt = ZonedDateTime.parse(publishAt);
                return zdt.toInstant().toEpochMilli();
            }
        } catch (Exception e) {
            // 파싱 실패 시 현재 시간 반환
        }
        return System.currentTimeMillis();
    }
}
