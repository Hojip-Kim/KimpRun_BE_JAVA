package kimp.cmc.dto.common.coin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CmcApiQuoteDto {

    @JsonProperty("USD")
    private CmcUsdQuoteDto USD;
}
