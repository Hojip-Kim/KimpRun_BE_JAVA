package kimp.exchange.dto.common.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class ExchangeNoticeResponseWrappingDto {
    @JsonProperty("count")
    private Integer count;
    @JsonProperty("exchange")
    private String exchange;
    @JsonProperty("results")
    private List<NoticeServerResponseDto> notices;
    @JsonProperty("success")
    private Boolean success;
    @JsonProperty("total_found")
    private String totalFound;
    @JsonProperty("url")
    private String url;
}
