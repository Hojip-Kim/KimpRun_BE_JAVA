package kimp.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.chat.dto.request.ChatMessage;
import kimp.chat.service.ChatWebsocketService;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.util.IpMaskUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;

@Component
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatWebsocketService chatWebsocketService;
    private final ObjectMapper objectMapper;

    public ChatWebSocketHandler(ChatWebsocketService chatWebsocketService, ObjectMapper objectMapper) {
        this.chatWebsocketService = chatWebsocketService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if(session.isOpen()) {
            chatWebsocketService.sessionInput(session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        chatWebsocketService.sessionClose(session);
        super.afterConnectionClosed(session, status);
    }

    // message receive역할
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        if(message.getPayloadLength() == 0){
            throw new KimprunException(KimprunExceptionEnum.WEBSOCKET_SESSION_EXCEPTION, "WebSocket message payload is empty", HttpStatus.BAD_REQUEST, "ChatWebSocketHandler.handleTextMessage");
        }
        
        // 메시지를 받은 경우 처리 로직 (필요시)
        String payload = message.getPayload();
        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
        
        // IP와 쿠키 정보 추출
        String userIp = extractClientIp(session);
        String cookiePayload = extractKimprunTokenCookie(session);

        // ping 메시지인경우
        if(chatMessage.isPing()) {
            session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(chatMessage)));
        }else {
            // chat 메시지인경우 - IP와 쿠키 정보를 포함한 새로운 ChatMessage 생성
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
            chatWebsocketService.broadcastChat(session, enrichedChatMessage);
        }
    }

    private String extractClientIp(WebSocketSession session) {
        HttpHeaders httpHeaders = session.getHandshakeHeaders();
        // WebSocket 헤더에서 IP 추출
        String ip = httpHeaders.getFirst("X-Forwarded-For");

        if(ip == null && session.getRemoteAddress() != null){
            ip = session.getRemoteAddress().getAddress().getHostAddress();
        }

        return ip != null ? ip : "unknown";
    }

    private String extractKimprunTokenCookie(WebSocketSession session) {
        HttpHeaders httpHeaders = session.getHandshakeHeaders();
        List<String> cookies = httpHeaders.get(HttpHeaders.COOKIE);
        if(cookies == null || cookies.isEmpty()){
            return null;
        }else {
            for (String header : cookies) {
                for (String part : header.split(";")) {
                    String[] kv = part.trim().split("=", 2);
                    if (kv.length == 2 && "kimprun-token".equals(kv[0])) return kv[1];
                }
            }
        }
        return null;
    }
}
