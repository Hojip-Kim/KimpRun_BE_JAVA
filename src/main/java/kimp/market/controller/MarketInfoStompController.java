package kimp.market.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.notice.dto.response.NoticeDto;
import kimp.market.dto.market.response.websocket.InfoResponseDto;
import kimp.market.dto.market.response.websocket.MarketWebsocketResponseDto;
import kimp.market.dto.market.response.websocket.UserWebsocketResponseDto;
import kimp.market.dto.internal.marketInfo.MarketInfoWebsocketDto;
import kimp.market.service.MarketInfoService;
import kimp.telegram.service.TelegramService;
import kimp.websocket.service.WebSocketUserTracker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
@Slf4j
public class MarketInfoStompController {

    private final MarketInfoService marketInfoService;
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketUserTracker webSocketUserTracker;
    private final ObjectMapper objectMapper;
    private final TelegramService telegramService;

    public MarketInfoStompController(MarketInfoService marketInfoService, SimpMessagingTemplate messagingTemplate, WebSocketUserTracker webSocketUserTracker, ObjectMapper objectMapper, TelegramService telegramService) {
        this.marketInfoService = marketInfoService;
        this.messagingTemplate = messagingTemplate;
        this.webSocketUserTracker = webSocketUserTracker;
        this.objectMapper = objectMapper;
        this.telegramService = telegramService;
    }

    public void sendNewNotice(NoticeDto noticeDto) throws IOException {
        MarketInfoWebsocketDto<NoticeDto> noticeData = new MarketInfoWebsocketDto<NoticeDto>("notice", noticeDto);
        
        // STOMP 브로커를 통해 /topic/marketInfo/notice로 공지사항 전송
        messagingTemplate.convertAndSend("/topic/marketInfo/notice", noticeData);
        
        // 텔레그램 채널로도 공지사항 전송
        try {
//            telegramService.sendNoticeMessage(noticeDto);
        } catch (Exception e) {
            log.error("텔레그램 알림 전송 실패: {} - {} (오류: {})", 
                    noticeDto.getExchangeType().name(), noticeDto.getTitle(), e.getMessage());
        }
    }

    @Scheduled(fixedRate = 5000)
    public void sendMarketInfo() throws Exception {
        try {
            double dollarData = this.marketInfoService.getDollarKRW();
            double tetherData = this.marketInfoService.getTetherKRW();

            // WebSocket 연결된 사용자 수 계산
            int userCount = webSocketUserTracker.getUserCount();
            
            MarketWebsocketResponseDto marketWebsocketResponseDto = new MarketWebsocketResponseDto(dollarData, tetherData);
            UserWebsocketResponseDto userWebsocketResponseDto = new UserWebsocketResponseDto(userCount);

            MarketInfoWebsocketDto<InfoResponseDto> marketWebsocketResponseData = new MarketInfoWebsocketDto<InfoResponseDto>("market", new InfoResponseDto(userWebsocketResponseDto, marketWebsocketResponseDto));

            // STOMP 브로커를 통해 /topic/marketInfo로 마켓 정보 전송
            messagingTemplate.convertAndSend("/topic/marketInfo", marketWebsocketResponseData);

        } catch (Exception e) {
            log.error("STOMP 마켓 정보 전송 실패: {}", e.getMessage(), e);
        }
    }
}