package kimp.market.dto.market.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class CoinoneTicker extends Ticker{

    // 정상 반환 시 success, 에러 코드 반환 시 error
    @JsonProperty("result")
    private String result;

    // error 발생 시 에러코드 반환, 성공인 경우 0 반환
    @JsonProperty("error_code")
    private String errorCode;

    // 반환 시점의 서버 시간 (ms)
    @JsonProperty("server_time")
    private Long serverTime;

    // 마켓 기준 통화로 거래 가능한 종목 목록
    @JsonProperty("tickers")
    private List<CoinoneTickerInfo> tickers;

    public CoinoneTicker(String result, String errorCode, Long serverTime, List<CoinoneTickerInfo> tickers) {
        this.result = result;
        this.errorCode = errorCode;
        this.serverTime = serverTime;
        this.tickers = tickers;
    }
}
