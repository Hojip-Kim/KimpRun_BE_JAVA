package kimp.scrap.dto.internal.bithumb.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kimp.exchange.dto.internal.response.NoticeServerResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class BithumbNoticeResponseDto extends NoticeServerResponseDto {
    @JsonProperty("category")
    private String category;
    @JsonProperty("is_new")
    private Boolean isNew;
}
