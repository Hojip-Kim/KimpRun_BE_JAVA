package kimp.market.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.market.components.impl.market.Coinone;
import kimp.market.dto.coin.common.market.CoinoneDto;
import kimp.market.dto.market.response.MarketDataList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class CoinoneWebsocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final Coinone coinone;

    public CoinoneWebsocketHandler(ObjectMapper objectMapper, Coinone coinone) {
        this.objectMapper = objectMapper;
        this.coinone = coinone;
    }

    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private Map<String, CoinoneDto> dataHashMap = new ConcurrentHashMap<>();

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

    // 코인원은 다중 티커 웹소켓 지원을 하지 않으므로 폴링작업을 통한 데이터 갱신
    @Scheduled(fixedRate = 3000)
    public void inputDataToHashMap(){
        MarketDataList<CoinoneDto> coinoneMarketDataList = coinone.getMarketDataList();
        List<CoinoneDto> coinoneDtoList = coinoneMarketDataList.getMarketDataList();

        for(CoinoneDto dto : coinoneDtoList){
            String key = dto.getToken();
            dataHashMap.put(key, dto);
        }
    }

    @Scheduled(fixedRate = 3000)
    public void sendMessageToAll() throws Exception {
        try {
            String mapToJson = objectMapper.writeValueAsString(dataHashMap);
            log.info(String.valueOf(dataHashMap.size()));
            TextMessage textMessage = new TextMessage(mapToJson);
            for (WebSocketSession session : sessions.values()) {
                if (session.isOpen()) {
                    session.sendMessage(textMessage);
                }
            }
            dataHashMap.clear();
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

}
