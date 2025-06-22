package kimp.market.dto.market.common;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpbitMarketNameData {
    @JsonProperty("market")
    private String market;
    @JsonProperty("korean_name")
    private String korean_name;
    @JsonProperty("english_name")
    private String english_name;

    public String getMarket() {
        return market;
    }

    public String getKorean_name() {
        return korean_name;
    }

    public String getEnglish_name() {
        return english_name;
    }
}
