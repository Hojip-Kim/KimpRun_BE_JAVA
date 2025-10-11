package kimp.cmc.component;

import kimp.cmc.dto.internal.coin.*;
import kimp.cmc.dto.internal.exchange.CmcExchangeDto;
import kimp.cmc.dto.internal.exchange.CmcExchangeApiStatusDto;
import kimp.cmc.dto.internal.exchange.CmcExchangeDetailMapDto;
import kimp.common.ratelimit.DistributedRateLimiter;
import kimp.common.ratelimit.RateLimitResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;

import java.util.List;

@Component
@Slf4j
@Qualifier("cmc")
public class CoinMarketCapComponent {

    private final RestClient coinMarketCapClient;
    private final DistributedRateLimiter distributedRateLimiter;
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

    public CoinMarketCapComponent(RestClient coinMarketCapClient, DistributedRateLimiter rateLimiter) {
        this.coinMarketCapClient = coinMarketCapClient;
        this.distributedRateLimiter = rateLimiter;
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
        
        // Rate Limit 대기 후 재시도 로직
        waitForRateLimitAvailability("getCoinMapFromCMC");
        
        String url = String.format(cmcCoinMapUrl, start, limit);
        
        try {
            CmcApiResponseDto<CmcExchangeApiStatusDto, List<CmcCoinMapDataDto>> cmcResponse = coinMarketCapClient.get()
                    .uri(url)
                    .headers(headers -> headers.addAll(getCMCHeaders()))
                    .retrieve()
                    .body(new ParameterizedTypeReference<CmcApiResponseDto<CmcExchangeApiStatusDto, List<CmcCoinMapDataDto>>>() {});

            return cmcResponse.getData();
        } catch (Exception e) {
            log.error("CMC Coin Map 조회 실패: {} - start: {}, limit: {}", e.getMessage(), start, limit);
            throw e;
        }
    }

    // 5000개 호출시 per credit : 25
    // 1, 5000
    // 코인 인포중, 숫자와 관련된 데이터들을 받아옴
    public List<CmcApiDataDto> getLatestCoinInfoFromCMC(int start, int limit){
        log.info("CoinMarketCap 최신 데이터 가져오기 시작");
        
        // Rate Limit 대기 후 재시도 로직
        waitForRateLimitAvailability("getLatestCoinInfoFromCMC");
        
        String url = String.format(cmcLatestUrl, start, limit);
        
        try {
            CmcApiResponseDto<CmcCoinApiStatusDto, List<CmcApiDataDto>> cmcResponse = coinMarketCapClient.get()
                    .uri(url)
                    .headers(headers -> headers.addAll(getCMCHeaders()))
                    .retrieve()
                    .body(new ParameterizedTypeReference<CmcApiResponseDto<CmcCoinApiStatusDto, List<CmcApiDataDto>>>() {});

            return cmcResponse.getData();
        } catch (Exception e) {
            log.error("CMC Latest 조회 실패: {} - start: {}, limit: {}", e.getMessage(), start, limit);
            throw e;
        }
    }

    // 최대 100개까지만 가능 - per credit : 1
    // 코인의 id를 List의 형태로 넣어주고, coinMarketCap의 api를통해 정보를 가져옵니다.
    public CmcCoinInfoDataMapDto getCmcCoinInfos(List<Integer> cmcCoinIds){
        // Rate Limit 대기 후 재시도 로직
        waitForRateLimitAvailability("getCmcCoinInfos");
        
        String sequenceMainnetCmcIds = cmcCoinIds.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse("");
        String url = String.format(cmcCoinInfoUrl, sequenceMainnetCmcIds);

        try {
            CmcApiResponseDto<CmcExchangeApiStatusDto, CmcCoinInfoDataMapDto> cmcResponse = coinMarketCapClient.get()
                    .uri(url)
                    .headers(headers -> headers.addAll(getCMCHeaders()))
                    .retrieve()
                    .body(new ParameterizedTypeReference<CmcApiResponseDto<CmcExchangeApiStatusDto, CmcCoinInfoDataMapDto>>() {});

            return cmcResponse.getData();
        } catch (Exception e) {
            log.error("CMC 코인 정보 조회 실패: {} - IDs: {}", e.getMessage(), sequenceMainnetCmcIds);
            // 빈 응답 반환하여 애플리케이션 시작 중단 방지
            return new CmcCoinInfoDataMapDto();
        }
    }

    // 5000개 호출시 per credit : 1
    // 최대 limit 5000까지만 가능
    public List<CmcExchangeDto> getExchangeMap(int start, int limit){
        log.info("CoinMarketCap Exchange Map 데이터 가져오기 시작");
        
        // Rate Limit 대기 후 재시도 로직
        waitForRateLimitAvailability("getExchangeMap");
        
        String url = String.format(cmcExchangeMapUrl, start, limit);
        
        try {
            CmcApiResponseDto<CmcExchangeApiStatusDto, List<CmcExchangeDto>> cmcResponse = coinMarketCapClient.get()
                    .uri(url)
                    .headers(headers -> headers.addAll(getCMCHeaders()))
                    .retrieve()
                    .body(new ParameterizedTypeReference<CmcApiResponseDto<CmcExchangeApiStatusDto, List<CmcExchangeDto>>>() {});

            return cmcResponse.getData();
        } catch (Exception e) {
            log.error("CMC Exchange Map 조회 실패: {} - start: {}, limit: {}", e.getMessage(), start, limit);
            throw e;
        }
    }

    // 최대 exchangeId 100개까지 가능 - per credit : 1
    public CmcExchangeDetailMapDto getExchangeInfo(List<Integer> exchangeIds){
        // Rate Limit 대기 후 재시도 로직
        waitForRateLimitAvailability("getExchangeInfo");
        
        String sequenceExchangeIds = exchangeIds.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse("");
        String url = String.format(cmcExchangeInfoUrl, sequenceExchangeIds);
        
        try {
            CmcApiResponseDto<CmcExchangeApiStatusDto, CmcExchangeDetailMapDto> cmcResponse = coinMarketCapClient.get()
                    .uri(url)
                    .headers(headers -> headers.addAll(getCMCHeaders()))
                    .retrieve()
                    .body(new ParameterizedTypeReference<CmcApiResponseDto<CmcExchangeApiStatusDto, CmcExchangeDetailMapDto>>() {});

            return cmcResponse.getData();
        } catch (Exception e) {
            log.error("CMC Exchange Info 조회 실패: {} - IDs: {}", e.getMessage(), sequenceExchangeIds);
            throw e;
        }
    }
    
    /**
     * Rate Limit 윈도우의 남은 시간 계산
     * 
     * @param windowSeconds 윈도우 크기 (초)
     * @return 남은 시간 (밀리초)
     */
    private long calculateRemainingWindowTime(int windowSeconds) {
        // 현재 시간을 윈도우 크기로 나눈 나머지로 윈도우 내 경과 시간 계산
        long currentTimeMillis = Instant.now().toEpochMilli();
        long windowMillis = windowSeconds * 1000L;
        long elapsedInWindow = currentTimeMillis % windowMillis;
        long remainingMillis = windowMillis - elapsedInWindow;
        
        log.debug("Rate Limit 윈도우 남은 시간 계산 - 윈도우: {}초, 경과: {}ms, 남은: {}ms ({}초)", 
            windowSeconds, elapsedInWindow, remainingMillis, remainingMillis / 1000);
        
        return remainingMillis;
    }
    
    /**
     * Rate Limit 가용성 대기 메서드
     * 제한에 걸리면 윈도우가 리셋될 때까지 대기 후 재시도
     * 
     * @param methodName 호출하는 메서드명
     */
    private void waitForRateLimitAvailability(String methodName) {
        int maxRetries = 5; // 최대 5번 재시도
        int windowSeconds = 60; // CMC API 대기시간 60초
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            RateLimitResult rateLimitResult = distributedRateLimiter.tryAcquireCmcApiLimit();
            
            if (rateLimitResult.isAllowed()) {
                return;
            }
            
            // Rate Limit 초과 시 대기
            long waitTime = calculateRemainingWindowTime(windowSeconds);
            // 최소 5초, 최대 65초 대기
            waitTime = Math.max(5000, Math.min(waitTime + 5000, 65000));
            
            log.warn("CMC API Rate Limit 초과 - 메서드: {}, 시도: {}/{}, {}초 대기 후 재시도", 
                methodName, attempt, maxRetries, waitTime / 1000);
            
            if (attempt == maxRetries) {
                log.error("CMC API Rate Limit 최대 재시도 횟수 초과 - 메서드: {}", methodName);
                throw new RuntimeException("CMC API Rate Limit exceeded after " + maxRetries + " attempts");
            }
            
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Rate Limit 대기 중 인터럽트 발생", e);
            }
        }
    }
}
