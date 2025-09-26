package kimp.telegram.service.impl;

import kimp.notice.dto.notice.NoticeDto;
import kimp.telegram.dto.TelegramMessageRequest;
import kimp.telegram.service.TelegramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class TelegramServiceImpl implements TelegramService {
    
    private final RestClient restClient;
    private final String botToken;
    private final String chatId;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public TelegramServiceImpl(RestClient.Builder restClientBuilder,
                              @Value("${telegram.bot.token}") String botToken,
                              @Value("${telegram.chat.id}") String chatId) {
        this.restClient = restClientBuilder
                .baseUrl("https://api.telegram.org")
                .build();
        this.botToken = botToken;
        this.chatId = chatId;
    }
    
    @Override
    public void sendNoticeMessage(NoticeDto noticeDto) {
        try {
            String message = formatNoticeMessage(noticeDto);
            
            TelegramMessageRequest request = new TelegramMessageRequest(chatId, message, "Markdown");
            
            String response = restClient.post()
                    .uri("/bot{botToken}/sendMessage", botToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(String.class);
            
            log.info("텔레그램 메시지 전송 성공: {} - {}", noticeDto.getExchangeType().name(), noticeDto.getTitle());
            log.debug("텔레그램 API 응답: {}", response);
            
        } catch (Exception e) {
            log.error("텔레그램 메시지 전송 실패: {} - {} (오류: {})", 
                    noticeDto.getExchangeType().name(), noticeDto.getTitle(), e.getMessage(), e);
        }
    }
    
    private String formatNoticeMessage(NoticeDto noticeDto) {
        String exchangeName = noticeDto.getExchangeType().name().toUpperCase();
        String title = noticeDto.getTitle();
        String link = noticeDto.getUrl();
        String date = noticeDto.getCreatedAt().format(DATE_FORMATTER);
        
        return String.format("""
                %s 거래소 공지
                [%s](%s)
                %s
                """, exchangeName, title, link, date);
    }
}