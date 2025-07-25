package kimp.exchange.dto.common.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class NoticeServerResponseDto {
    @JsonProperty("date")
    private Integer count;
    @JsonProperty("link")
    private String link;
    @JsonProperty("title")
    private String title;
}
