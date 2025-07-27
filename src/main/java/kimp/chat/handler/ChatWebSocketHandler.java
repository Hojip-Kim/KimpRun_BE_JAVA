package kimp.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.chat.dto.request.ChatMessage;
import kimp.chat.service.ChatWebsocketService;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

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
        // ping 메시지인경우
        if(chatMessage.isPing()) {
            session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(chatMessage)));
        }else {
            // chat 메시지인경우
            chatWebsocketService.saveMessage(chatMessage);
            chatWebsocketService.broadcastChat(session, chatMessage);
        }
    }
}
