package kimp.chat.service;

import kimp.chat.dto.request.ChatMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public interface ChatWebsocketService {

    public void sessionInput(WebSocketSession webSocketSession);

    public void sessionClose(WebSocketSession session);

    public void broadcastChat(WebSocketSession webSocketSession, ChatMessage chatMessage) throws IOException;

    public void saveMessage(ChatMessage chatMessage);
}
