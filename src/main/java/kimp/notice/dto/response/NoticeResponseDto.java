package kimp.notice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kimp.market.Enum.MarketType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class NoticeResponseDto {

    private String type = "notice";
    @JsonProperty("exchange_name")
    private MarketType exchangeName;
    private String absoluteUrl;
    private List<NoticeParsedData> noticeDataList;

    public NoticeResponseDto(MarketType exchangeName, String absoluteUrl, List<NoticeParsedData> noticeDataList) {
        this.exchangeName = exchangeName;
        this.absoluteUrl = absoluteUrl;
        this.noticeDataList = noticeDataList;
    }
}
