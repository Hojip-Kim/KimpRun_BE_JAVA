package kimp.market.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.exchange.dto.notice.NoticeDto;
import kimp.market.dto.market.response.websocket.InfoResponseDto;
import kimp.market.dto.market.response.websocket.MarketWebsocketResponseDto;
import kimp.market.dto.market.response.websocket.UserWebsocketResponseDto;
import kimp.market.dto.marketInfo.common.MarketInfoWebsocketDto;
import kimp.market.service.MarketInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
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

    public void sendNewNotice(NoticeDto noticeDto) throws IOException {
        MarketInfoWebsocketDto<NoticeDto> noticeData = new MarketInfoWebsocketDto<NoticeDto>("notice", noticeDto);
        String notice = objectMapper.writeValueAsString(noticeData);

        TextMessage textMessage = new TextMessage(notice);
        for(WebSocketSession session : sessions.values()){
            if(session.isOpen()){
                session.sendMessage(textMessage);
            }
        }
    }

    @Scheduled(fixedRate = 5000)
    public void sendMessageToAll() throws Exception {
        try {
            double dollarData = this.marketInfoService.getDollarKRW();
            double tetherData = this.marketInfoService.getTetherKRW();

            int userCount = sessions.size();
            MarketWebsocketResponseDto marketWebsocketResponseDto = new MarketWebsocketResponseDto(dollarData, tetherData);
            UserWebsocketResponseDto userWebsocketResponseDto = new UserWebsocketResponseDto(userCount);

            MarketInfoWebsocketDto<InfoResponseDto> marketWebsocketResponseData = new MarketInfoWebsocketDto<InfoResponseDto>("market",new InfoResponseDto(userWebsocketResponseDto, marketWebsocketResponseDto));

            String responseInfo = objectMapper.writeValueAsString(marketWebsocketResponseData);
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
