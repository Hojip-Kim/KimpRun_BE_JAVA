package kimp.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.chat.dto.request.ChatMessage;
import kimp.chat.service.ChatWebsocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatWebsocketService chatWebsocketService;

    public ChatWebSocketHandler(ChatWebsocketService chatWebsocketService) {
        this.chatWebsocketService = chatWebsocketService;
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
        if(message.getPayloadLength() == 0 || message == null){
            throw new IllegalArgumentException("Not have text message contents");
        }
        // 메시지를 받은 경우 처리 로직 (필요시)
        String payload = message.getPayload();
        ObjectMapper mapper = new ObjectMapper();
        ChatMessage chatMessage = mapper.readValue(payload, ChatMessage.class);


        chatWebsocketService.saveMessage(chatMessage);
        chatWebsocketService.broadcastChat(session, chatMessage);
    }

    // TODO : websocket time-out 방지용 ping-pong logic
    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        super.handlePongMessage(session, message);
    }
}
