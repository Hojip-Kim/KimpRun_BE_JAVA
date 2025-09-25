package kimp.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.chat.dto.vo.SaveChatMessage;
import kimp.chat.dto.request.ChatMessage;
import kimp.chat.dto.response.ChatMessageResponse;
import kimp.chat.service.ChatStompService;
import kimp.config.redis.RedisMessageBrokerConfig;
import kimp.config.redis.handler.RedisMessageHandler;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.util.IpMaskUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@Controller
@Slf4j
public class ChatStompController {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatStompService chatStompService;
    private final RedisTemplate<String, Object> chatRedisTemplate;
    private final RedisMessageHandler redisMessageHandler;

    public ChatStompController(ObjectMapper objectMapper, 
                              SimpMessagingTemplate messagingTemplate, 
                              ChatStompService chatStompService,
                              @Qualifier("chatRedisTemplate") RedisTemplate<String, Object> chatRedisTemplate,
                              RedisMessageHandler redisMessageHandler) {
        this.objectMapper = objectMapper;
        this.messagingTemplate = messagingTemplate;
        this.chatStompService = chatStompService;
        this.chatRedisTemplate = chatRedisTemplate;
        this.redisMessageHandler = redisMessageHandler;
    }

    /**
     * 채팅 메시지 처리 - 비동기 최적화 버전
     * 
     * 1. 바로 브로드캐스트: 사용자에게 실시간 응답 제공
     * 2. 비동기 저장: DB 저장을 별도 스레드에서 처리하여 처리 속도 향상
     */
    @MessageMapping("/chat")
    public void handleChatMessage(ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor, Principal principal) throws Exception {
        
        if (chatMessage == null) {
            throw new KimprunException(KimprunExceptionEnum.WEBSOCKET_SESSION_EXCEPTION, "Chat message is null", HttpStatus.BAD_REQUEST, "ChatStompController.handleChatMessage");
        }
        
        // ping 메시지인 경우 - 바로 브로드캐스트
        if(chatMessage.isPing()) {
            messagingTemplate.convertAndSend("/topic/chat", chatMessage);
            return;
        }

        // IP와 쿠키 정보 추출 (HandshakeInterceptor에서 설정한 세션 속성 활용)
        String userIp = extractClientIpFromSession(headerAccessor);
        String cookiePayload = extractKimprunTokenFromSession(headerAccessor);
        String randomUUID = UUID.randomUUID().toString();
        
        // chat 메시지인 경우 - 저장용 VO 생성
        SaveChatMessage saveChatMessageVo = new SaveChatMessage(
            chatMessage.isPing(),
            chatMessage.getChatID(),
            chatMessage.getContent(),
            chatMessage.getAuthenticated(),
            userIp,
            cookiePayload,   // uuid 필드에 저장 (cookiePayload)
            randomUUID,      // inherienceId 필드에 저장 (randomUUID - 채팅 식별용 고유 id)
            false, chatMessage.getMemberId()
        );

        // 1. 메시지 응답 객체 생성
        ChatMessageResponse chatMessageResponse = saveChatMessageVo.toResponse();
        chatMessageResponse.setIp(IpMaskUtil.mask(chatMessageResponse.getUserIp()));
        
        // 2. 로컬 브로드캐스트 (현재 서버에 연결된 클라이언트에게)
        messagingTemplate.convertAndSend("/topic/chat", chatMessageResponse);
        
        // 3. Redis Pub/Sub으로 다른 서버 인스턴스에 메시지 발행
        publishToRedis(chatMessageResponse);
        
        // 4. 비동기 DB 저장 (별도 스레드에서 처리)
        boolean saveSuccess = chatStompService.saveMessageAsync(saveChatMessageVo);
        if (!saveSuccess) {
            log.error("채팅 메시지 큐 추가 실패 - UUID: {}, 채팅ID: {}", 
                randomUUID, chatMessage.getChatID());
        } else {
            log.debug("채팅 메시지 큐 추가 성공 - UUID: {}", randomUUID);
        }
    }

    /**
     * HandshakeInterceptor에서 설정한 세션 속성에서 클라이언트 IP 추출
     */
    private String extractClientIpFromSession(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        
        String clientIp = (String) sessionAttributes.get("clientIp");
        if (clientIp != null) {
            log.debug("IP 추출 완료: {}", clientIp);
            return clientIp;
        }
        
        log.warn("IP를 찾을 수 없음.");
        return extractClientIpFallback(headerAccessor);
    }

    /**
     * HandshakeInterceptor에서 설정한 세션 속성에서 kimprun-token 추출
     */
    private String extractKimprunTokenFromSession(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        
        String kimprunToken = (String) sessionAttributes.get("kimprun-token");
        if (kimprunToken != null) {
            log.debug("세션에서 kimprun-token 추출 완료 (마스킹됨): {}", maskToken(kimprunToken));
            return kimprunToken;
        }
        
        log.debug("세션 속성에서 kimprun-token을 찾을 수 없음");
        return null;
    }

    /**
     * 세션 속성에서 IP를 찾지 못한 경우 폴백 방식으로 IP 추출
     */
    private String extractClientIpFallback(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        
        // WebSocket 헤더에서 IP 추출 시도
        String ip = (String) sessionAttributes.get("X-Forwarded-For");
        
        if (ip == null) {
            // 다른 IP 관련 헤더들도 체크
            ip = (String) sessionAttributes.get("X-Real-IP");
        }

        return ip != null ? ip : "unknown";
    }

    /**
     * 로깅용 토큰 마스킹
     */
    private String maskToken(String token) {
        if (token == null || token.length() < 8) {
            return "***";
        }
        return token.substring(0, 4) + "***" + token.substring(token.length() - 4);
    }
    
    /**
     * Redis Pub/Sub으로 메시지 발행
     * 다른 서버 인스턴스에 메시지를 전파하여 분산 환경에서 채팅 동기화
     */
    private void publishToRedis(ChatMessageResponse chatMessageResponse) {
        try {
            // 메시지 래퍼 생성 (인스턴스 ID 포함)
            RedisMessageHandler.ChatMessageWrapper wrapper = new RedisMessageHandler.ChatMessageWrapper(
                redisMessageHandler.getInstanceId(),
                chatMessageResponse
            );
            
            // Redis 채널에 메시지 발행
            chatRedisTemplate.convertAndSend(RedisMessageBrokerConfig.CHAT_CHANNEL, wrapper);
            
            log.debug("Redis 메시지 발행 완료 - 채널: {}, 채팅ID: {}, 인스턴스ID: {}", 
                    RedisMessageBrokerConfig.CHAT_CHANNEL, 
                    chatMessageResponse.getChatID(),
                    redisMessageHandler.getInstanceId());
                    
        } catch (Exception e) {
            log.error("Redis 메시지 발행 실패 - 채팅ID: {}", chatMessageResponse.getChatID(), e);
            // Redis 발행 실패해도 로컬 브로드캐스트는 이미 완료되었으므로 서비스는 계속됨
        }
    }
}