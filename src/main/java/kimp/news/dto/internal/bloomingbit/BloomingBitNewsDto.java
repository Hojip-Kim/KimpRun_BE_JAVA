package kimp.news.dto.internal.bloomingbit;

import com.fasterxml.jackson.annotation.JsonProperty;
import kimp.news.dto.internal.NewsSourceDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * BloomingBit 뉴스 API 응답 DTO
 * BloomingBit 암호화폐 뉴스 API로부터 받는 뉴스 데이터
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BloomingBitNewsDto implements NewsSourceDto {

    /** 뉴스 고유 시퀀스 ID (BloomingBit에서 제공) */
    @JsonProperty("seq")
    private Long seq;

    /** 뉴스 타입 (예: 속보, 일반기사) */
    @JsonProperty("newsType")
    private String newsType;

    /** 뉴스 지역 (예: 국내, 해외) */
    @JsonProperty("region")
    private String region;

    /** 뉴스 제목 */
    @JsonProperty("title")
    private String title;

    /** 플레인 텍스트 형식의 뉴스 본문 */
    @JsonProperty("plainTextContent")
    private String plainTextContent;

    /** 마크다운 형식의 뉴스 본문 */
    @JsonProperty("markdownContent")
    private String markdownContent;

    /** 썸네일 이미지 URL */
    @JsonProperty("thumbnail")
    private String thumbnail;

    /** 감정 분석 결과 (예: POSITIVE, NEGATIVE, NEUTRAL) */
    @JsonProperty("sentiment")
    private String sentiment;

    /** 뉴스 요약 목록 */
    @JsonProperty("summaryList")
    private List<String> summaryList;

    /** 뉴스 인사이트 목록 (시장 영향 분석 등) */
    @JsonProperty("insightList")
    private List<String> insightList;

    /** 뉴스 키워드 목록 (예: 비트코인, 이더리움) */
    @JsonProperty("keywordList")
    private List<String> keywordList;

    /** 원본 뉴스 URL */
    @JsonProperty("sourceUrl")
    private String sourceUrl;

    /** 뉴스 생성 시각 (epoch 밀리초) */
    @JsonProperty("createEpoch")
    private Long createEpoch;

    /** 뉴스 수정 시각 (epoch 밀리초) */
    @JsonProperty("updateEpoch")
    private Long updateEpoch;

    /** 변화값 (시세 관련 변화량) */
    @JsonProperty("change")
    private Integer change;

    /** 신규 뉴스 여부 */
    @JsonProperty("isNew")
    private Boolean isNew;

    /** 헤드라인 뉴스 여부 */
    @JsonProperty("headline")
    private Boolean headline;

    // NewsSourceDto 인터페이스 구현 메소드
    @Override
    public Long getSourceSequenceId() {
        return this.seq;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getSourceUrl() {
        return this.sourceUrl;
    }

    @Override
    public Long getCreateEpochMillis() {
        return this.createEpoch;
    }
}
