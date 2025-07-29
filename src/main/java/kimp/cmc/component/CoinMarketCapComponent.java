package kimp.cmc.component;

import kimp.cmc.dto.common.coin.*;
import kimp.cmc.dto.common.exchange.CmcExchangeDto;
import kimp.cmc.dto.common.exchange.CmcExchangeApiStatusDto;
import kimp.cmc.dto.common.exchange.CmcExchangeDetailMapDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@Slf4j
@Qualifier("cmc")
public class CoinMarketCapComponent {

    private final RestClient restClient;
    private final CmcApiRateLimiter rateLimiter;
    @Value("${cmc.api.key}")
    private String cmcApiKey;

    @Value("${cmc.api.coinmap_url}")
    private String cmcCoinMapUrl;
    @Value("${cmc.api.latest_url}")
    private String cmcLatestUrl;
    @Value("${cmc.api.coin_info_url}")
    private String cmcCoinInfoUrl;
    @Value("${cmc.api.exchange_map_url}")
    private String cmcExchangeMapUrl;
    @Value("${cmc.api.exchange_info_url}")
    private String cmcExchangeInfoUrl;

    public CoinMarketCapComponent(RestClient restClient, CmcApiRateLimiter rateLimiter) {
        this.restClient = restClient;
        this.rateLimiter = rateLimiter;
    }

    private HttpHeaders getCMCHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-CMC_PRO_API_KEY", cmcApiKey);
        return headers;
    }

    // 대부분 start가 파라미터로 들어가있는것은 2까지만 순회 돌면 됨.

    // 5000개 호출시 per credit : 1
    // 1, 5000
    public List<CmcCoinMapDataDto> getCoinMapFromCMC(int start, int limit) {
        log.info("CoinMarketCap Coin Map 데이터 가져오기 시작");
        
        // Rate limit 체크
        if (!rateLimiter.tryAcquire()) {
            log.error("Rate limit exceeded, cannot proceed with API call");
            throw new RuntimeException("CMC API rate limit exceeded");
        }
        
        String url = String.format(cmcCoinMapUrl, start, limit);
        CmcApiResponseDto<CmcExchangeApiStatusDto, List<CmcCoinMapDataDto>> cmcResponse = restClient.get()
                .uri(url)
                .headers(headers -> headers.addAll(getCMCHeaders()))
                .retrieve()
                .body(new ParameterizedTypeReference<CmcApiResponseDto<CmcExchangeApiStatusDto, List<CmcCoinMapDataDto>>>() {});

        return cmcResponse.getData();
    }

    // 5000개 호출시 per credit : 25
    // 1, 5000
    // 코인 인포중, 숫자와 관련된 데이터들을 받아옴
    public List<CmcApiDataDto> getLatestCoinInfoFromCMC(int start, int limit){
        log.info("CoinMarketCap 최신 데이터 가져오기 시작");
        
        // Rate limit 체크
        if (!rateLimiter.tryAcquire()) {
            log.error("Rate limit exceeded, cannot proceed with API call");
            throw new RuntimeException("CMC API rate limit exceeded");
        }
        
        String url = String.format(cmcLatestUrl, start, limit);
        CmcApiResponseDto<CmcCoinApiStatusDto, List<CmcApiDataDto>> cmcResponse = restClient.get()
                .uri(url)
                .headers(headers -> headers.addAll(getCMCHeaders()))
                .retrieve()
                .body(new ParameterizedTypeReference<CmcApiResponseDto<CmcCoinApiStatusDto, List<CmcApiDataDto>>>() {});

        return cmcResponse.getData();
    }

    // 최대 100개까지만 가능 - per credit : 1
    // 코인의 id를 List의 형태로 넣어주고, coinMarketCap의 api를통해 정보를 가져옵니다.
    public CmcCoinInfoDataMapDto getCmcCoinInfos(List<Integer> cmcCoinIds){
        // Rate limit 체크
        if (!rateLimiter.tryAcquire()) {
            log.error("Rate limit exceeded, cannot proceed with API call");
            throw new RuntimeException("CMC API rate limit exceeded");
        }
        
        String sequenceMainnetCmcIds = cmcCoinIds.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse("");
        String url = String.format(cmcCoinInfoUrl, sequenceMainnetCmcIds);

        CmcApiResponseDto<CmcExchangeApiStatusDto, CmcCoinInfoDataMapDto> cmcResponse = restClient.get()
                .uri(url)
                .headers(headers -> headers.addAll(getCMCHeaders()))
                .retrieve()
                .body(new ParameterizedTypeReference<CmcApiResponseDto<CmcExchangeApiStatusDto, CmcCoinInfoDataMapDto>>() {});

        return cmcResponse.getData();
    }

    // 5000개 호출시 per credit : 1
    // 최대 limit 5000까지만 가능
    public List<CmcExchangeDto> getExchangeMap(int start, int limit){
        log.info("CoinMarketCap Exchange Map 데이터 가져오기 시작");
        
        // Rate limit 체크
        if (!rateLimiter.tryAcquire()) {
            log.error("Rate limit exceeded, cannot proceed with API call");
            throw new RuntimeException("CMC API rate limit exceeded");
        }
        
        String url = String.format(cmcExchangeMapUrl, start, limit);
        CmcApiResponseDto<CmcExchangeApiStatusDto, List<CmcExchangeDto>> cmcResponse = restClient.get()
                .uri(url)
                .headers(headers -> headers.addAll(getCMCHeaders()))
                .retrieve()
                .body(new ParameterizedTypeReference<CmcApiResponseDto<CmcExchangeApiStatusDto, List<CmcExchangeDto>>>() {});

        return cmcResponse.getData();
    }

    // 최대 exchangeId 100개까지 가능 - per credit : 1
    public CmcExchangeDetailMapDto getExchangeInfo(List<Integer> exchangeIds){
        // Rate limit 체크
        if (!rateLimiter.tryAcquire()) {
            log.error("Rate limit exceeded, cannot proceed with API call");
            throw new RuntimeException("CMC API rate limit exceeded");
        }
        
        String sequenceExchangeIds = exchangeIds.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse("");
        String url = String.format(cmcExchangeInfoUrl, sequenceExchangeIds);
        CmcApiResponseDto<CmcExchangeApiStatusDto, CmcExchangeDetailMapDto> cmcResponse = restClient.get()
                .uri(url)
                .headers(headers -> headers.addAll(getCMCHeaders()))
                .retrieve()
                .body(new ParameterizedTypeReference<CmcApiResponseDto<CmcExchangeApiStatusDto, CmcExchangeDetailMapDto>>() {});

        return cmcResponse.getData();
    }
}
