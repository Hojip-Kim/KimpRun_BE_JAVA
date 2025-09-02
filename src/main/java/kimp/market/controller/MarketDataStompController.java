package kimp.market.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.market.dto.coin.common.market.*;
import kimp.market.dto.market.response.websocket.MultipleMarketDataResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@Slf4j
public class MarketDataStompController {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public MarketDataStompController(SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    private Map<String, BinanceDto> binanceHashMap = new ConcurrentHashMap<>();
    private Map<String, UpbitDto> upbitHashMap = new ConcurrentHashMap<>();
    private Map<String, CoinoneDto> coinoneHashMap = new ConcurrentHashMap<>();
    private Map<String, BithumbDto> bithumbHashMap = new ConcurrentHashMap<>();

    public void inputDataToHashMap(MarketDto dto) {
        Class<?> dtoClass = dto.getClass();

        switch (dtoClass.getSimpleName()) {
            case "UpbitDto":
                upbitHashMap.put(dto.getToken(), (UpbitDto) dto);
                break;
            case "CoinoneDto":
                coinoneHashMap.put(dto.getToken(), (CoinoneDto) dto);
                break;
            case "BithumbDto":
                bithumbHashMap.put(dto.getToken(), (BithumbDto) dto);
                break;
            case "BinanceDto":
                binanceHashMap.put(dto.getToken(), (BinanceDto) dto);
                break;
            default:
                log.warn("Unknown Dto : {}", dtoClass.getSimpleName());
                break;
        }
    }

    @Scheduled(fixedRate = 1000, scheduler = "marketDataTaskScheduler")
    public void sendMarketData() {
        try {
            List<UpbitDto> upbitDtoList = new ArrayList<>();
            List<CoinoneDto> coinoneDtoList = new ArrayList<>();
            List<BithumbDto> bithumbDtoList = new ArrayList<>();
            List<BinanceDto> binanceDtoList = new ArrayList<>();

            for(UpbitDto upbitDto : upbitHashMap.values()) {
                upbitDtoList.add(upbitDto);
            }
            for(CoinoneDto coinoneDto : coinoneHashMap.values()) {
                coinoneDtoList.add(coinoneDto);
            }
            for(BithumbDto bithumbDto : bithumbHashMap.values()) {
                bithumbDtoList.add(bithumbDto);
            }
            for(BinanceDto binanceDto : binanceHashMap.values()) {
                binanceDtoList.add(binanceDto);
            }
            
            MultipleMarketDataResponseDto responseDto = new MultipleMarketDataResponseDto(
                    upbitDtoList, binanceDtoList, coinoneDtoList, bithumbDtoList
            );

            // STOMP 브로커를 통해 /topic/marketData로 데이터 전송
            messagingTemplate.convertAndSend("/topic/marketData", responseDto);

            clearAllHashMaps();

        } catch (Exception e) {
            log.error("STOMP 마켓 데이터 전송 실패: {}", e.getMessage(), e);
        }
    }

    private void clearAllHashMaps() {
        upbitHashMap.clear();
        binanceHashMap.clear();
        coinoneHashMap.clear();
        bithumbHashMap.clear();
    }
}