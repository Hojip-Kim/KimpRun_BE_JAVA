package kimp.chat.service;

import kimp.chat.dto.vo.SaveChatMessage;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;


public interface ChatStompService {

    public void handleWebSocketConnectListener(SessionConnectEvent event);

    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event);

    public void saveMessage(SaveChatMessage saveChatMessage);
    
    public boolean saveMessageAsync(SaveChatMessage saveChatMessage);
}
