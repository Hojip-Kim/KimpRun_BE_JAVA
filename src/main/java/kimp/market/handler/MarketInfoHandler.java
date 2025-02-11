package kimp.market.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.market.dto.response.InfoResponseDto;
import kimp.market.service.MarketInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class MarketInfoHandler extends TextWebSocketHandler {

    private final MarketInfoService marketInfoService;

    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private ObjectMapper objectMapper;

    public MarketInfoHandler(MarketInfoService marketInfoService, ObjectMapper objectMapper) {
        this.marketInfoService = marketInfoService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info(session.getId() + " MarketInfoHandler established");
        sessions.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info(session.getId() + "MarketInfoHandler closed");
        sessions.remove(session.getId());
        super.afterConnectionClosed(session, status);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    }

    @Scheduled(fixedRate = 5000)
    public void sendMessageToAll() throws Exception {
        try {
            double dollarData = this.marketInfoService.getDollarKRW();
            double tetherData = this.marketInfoService.getTetherKRW();
            int sessionSize = sessions.size();

            InfoResponseDto responseDto = new InfoResponseDto(sessionSize, dollarData, tetherData);
            String responseInfo = objectMapper.writeValueAsString(responseDto);
            TextMessage textMessage = new TextMessage(responseInfo);
            for (WebSocketSession session : sessions.values()) {
                if (session.isOpen()) {
                    session.sendMessage(textMessage);
                }
            }

        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

}
