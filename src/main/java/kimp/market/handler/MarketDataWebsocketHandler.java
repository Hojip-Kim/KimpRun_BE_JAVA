package kimp.market.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.market.dto.coin.common.market.*;
import kimp.market.dto.market.response.websocket.MultipleMarketDataResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class MarketDataWebsocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;

    public MarketDataWebsocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private Map<String, BinanceDto> binanceHashMap = new ConcurrentHashMap<>();
    private Map<String, UpbitDto> upbitHashMap = new ConcurrentHashMap<>();
    private Map<String, CoinoneDto> coinoneHashMap = new ConcurrentHashMap<>();
    private Map<String, BithumbDto> bithumbHashMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info(session.getId() + " established");
        sessions.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info(session.getId() + " closed");
        sessions.remove(session.getId());
        super.afterConnectionClosed(session, status);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 메시지를 받은 경우 처리 로직 (필요시)
    }

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


    @Async
    @Scheduled(fixedRate = 3000)
    public void sendMessageToAll() {
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

            String finalJson = objectMapper.writeValueAsString(responseDto);
            TextMessage textMessage = new TextMessage(finalJson);

            for (WebSocketSession session : sessions.values()) {
                if (session.isOpen()) {
                    session.sendMessage(textMessage);
                }
            }

            clearAllHashMaps();

        } catch (Exception e) {
            log.error("WebSocket 메시지 전송 실패: {}", e.getMessage(), e);
        }
    }

    private void clearAllHashMaps() {
        upbitHashMap.clear();
        binanceHashMap.clear();
        coinoneHashMap.clear();
        bithumbHashMap.clear();
    }
}
