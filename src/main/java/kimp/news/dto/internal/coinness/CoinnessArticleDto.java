package kimp.news.dto.internal.coinness;

import com.fasterxml.jackson.annotation.JsonProperty;
import kimp.news.dto.internal.NewsSourceDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Coinness 일반 기사 API 응답 DTO
 * Coinness의 일반 뉴스 기사 데이터
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoinnessArticleDto implements NewsSourceDto {

    /** 뉴스 고유 ID (Coinness에서 제공) */
    @JsonProperty("id")
    private Long id;

    /** 뉴스 제목 */
    @JsonProperty("title")
    private String title;

    /** 뉴스 원본 링크 URL */
    @JsonProperty("link")
    private String link;

    /** 썸네일 이미지 URL */
    @JsonProperty("thumbnailImage")
    private String thumbnailImage;

    /** 본문 이미지 URL */
    @JsonProperty("contentImage")
    private String contentImage;

    /** 발행 시각 (ISO 8601 형식) */
    @JsonProperty("publishAt")
    private String publishAt;

    /** 뉴스 요약 설명 */
    @JsonProperty("description")
    private String description;

    /** 조회수 */
    @JsonProperty("view")
    private Integer view;

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
        return this.link;
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
