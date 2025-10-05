package kimp.news.dto.internal.bloomingbit;

import com.fasterxml.jackson.annotation.JsonProperty;
import kimp.news.dto.internal.NewsSourceDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BloomingBitNewsDto implements NewsSourceDto {

    @JsonProperty("seq")
    private Long seq;

    @JsonProperty("newsType")
    private String newsType;

    @JsonProperty("region")
    private String region;

    @JsonProperty("title")
    private String title;

    @JsonProperty("plainTextContent")
    private String plainTextContent;

    @JsonProperty("markdownContent")
    private String markdownContent;

    @JsonProperty("thumbnail")
    private String thumbnail;

    @JsonProperty("sentiment")
    private String sentiment;

    @JsonProperty("summaryList")
    private List<String> summaryList;

    @JsonProperty("insightList")
    private List<String> insightList;

    @JsonProperty("keywordList")
    private List<String> keywordList;

    @JsonProperty("sourceUrl")
    private String sourceUrl;

    @JsonProperty("createEpoch")
    private Long createEpoch;

    @JsonProperty("updateEpoch")
    private Long updateEpoch;

    @JsonProperty("change")
    private Integer change;

    @JsonProperty("isNew")
    private Boolean isNew;

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
