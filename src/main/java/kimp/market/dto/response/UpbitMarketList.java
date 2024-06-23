package kimp.market.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpbitMarketList {
    @JsonProperty("marketList")
    private List<String> marketList;

    public List<String> getMarketList() {
        return marketList;
    }
}
