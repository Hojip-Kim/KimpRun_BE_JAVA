package kimp.chat.service;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public interface ChatWebsocketService {

    public void sessionInput(WebSocketSession webSocketSession);

    public void sessionClose(WebSocketSession session);

    public void broadcastChat(WebSocketSession webSocketSession, TextMessage textMessage) throws IOException;

    public void saveMessage(WebSocketSession webSocketSession, TextMessage textMessage);
}
