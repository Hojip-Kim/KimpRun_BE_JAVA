package kimp.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.chat.dto.request.ChatMessage;
import kimp.chat.service.ChatWebsocketService;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.util.IpMaskUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class ChatStompController {

    private final ChatWebsocketService chatWebsocketService;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatStompController(ChatWebsocketService chatWebsocketService, ObjectMapper objectMapper, SimpMessagingTemplate messagingTemplate) {
        this.chatWebsocketService = chatWebsocketService;
        this.objectMapper = objectMapper;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat")
    public void handleChatMessage(ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor, Principal principal) throws Exception {
        
        if (chatMessage == null) {
            throw new KimprunException(KimprunExceptionEnum.WEBSOCKET_SESSION_EXCEPTION, "Chat message is null", HttpStatus.BAD_REQUEST, "ChatStompController.handleChatMessage");
        }
        
        // IP와 쿠키 정보 추출
        String userIp = extractClientIp(headerAccessor);
        String cookiePayload = extractKimprunTokenCookie(headerAccessor);

        // ping 메시지인 경우
        if(chatMessage.isPing()) {
            messagingTemplate.convertAndSend("/topic/chat", chatMessage);
        } else {
            // chat 메시지인 경우 - IP와 쿠키 정보를 포함한 새로운 ChatMessage 생성
            ChatMessage enrichedChatMessage = new ChatMessage(
                chatMessage.isPing(),
                chatMessage.getChatID(),
                chatMessage.getContent(),
                chatMessage.getAuthenticated(),
                userIp,
                cookiePayload
            );
            
            chatWebsocketService.saveMessage(enrichedChatMessage);
            enrichedChatMessage.setIp(IpMaskUtil.mask(enrichedChatMessage.getUserIp()));
            
            // 모든 구독자에게 브로드캐스트
            messagingTemplate.convertAndSend("/topic/chat", enrichedChatMessage);
        }
    }

    private String extractClientIp(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        
        // WebSocket 헤더에서 IP 추출 시도
        String ip = (String) sessionAttributes.get("X-Forwarded-For");
        
        if (ip == null) {
            // 다른 IP 관련 헤더들도 체크
            ip = (String) sessionAttributes.get("X-Real-IP");
        }
        
        if (ip == null) {
            // 세션에서 직접 추출 시도
            ip = (String) sessionAttributes.get("clientIp");
        }

        return ip != null ? ip : "unknown";
    }

    private String extractKimprunTokenCookie(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        
        // 쿠키에서 kimprun-token 추출
        String cookieHeader = (String) sessionAttributes.get("cookie");
        
        if (cookieHeader != null) {
            for (String part : cookieHeader.split(";")) {
                String[] kv = part.trim().split("=", 2);
                if (kv.length == 2 && "kimprun-token".equals(kv[0])) {
                    return kv[1];
                }
            }
        }
        
        return null;
    }
}