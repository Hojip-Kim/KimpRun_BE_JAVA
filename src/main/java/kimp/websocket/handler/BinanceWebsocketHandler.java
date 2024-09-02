package kimp.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.websocket.dto.response.BinanceStreamDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Component
@Slf4j
public class BinanceWebsocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;

    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private Map<String, BinanceStreamDto> dataHashMap = new ConcurrentHashMap<>();

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

    public void inputDataToHashMap(BinanceStreamDto dto){
        String key = dto.getToken();
        dataHashMap.put(key, dto);
    }


    @Async
    @Scheduled(fixedRate = 3000)
    public void sendMessageToAll() throws Exception {
        try {
            String mapToJson = objectMapper.writeValueAsString(dataHashMap);
            log.info(String.valueOf(dataHashMap.size()));
            dataHashMap.clear();
            for (WebSocketSession session : sessions.values()) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(mapToJson));
                }
            }

        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

}
