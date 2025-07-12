package kimp.cmc.dto.common.coin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CmcApiResponseDto<S, T> {

    @JsonProperty("status")
    private S status;
    @JsonProperty("data")
    private T data;

}
