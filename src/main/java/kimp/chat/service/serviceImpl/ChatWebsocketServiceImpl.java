package kimp.chat.service.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.chat.dao.daoImpl.ChatDao;
import kimp.chat.dto.ChatDto;
import kimp.chat.service.ChatWebsocketService;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatWebsocketServiceImpl implements ChatWebsocketService {
    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private final ChatDao chatDao;
    private final ObjectMapper objectMapper;

    public ChatWebsocketServiceImpl(ChatDao chatDao, ObjectMapper objectMapper) {
        this.chatDao = chatDao;
        this.objectMapper = objectMapper;
    }

    @Override
    public void sessionInput(WebSocketSession webSocketSession){
        WebSocketSession session =  sessions.put(webSocketSession.getId(), webSocketSession);

    }

    @Override
    public void sessionClose(WebSocketSession session){
        WebSocketSession removeSession = sessions.remove(session.getId());

        if(removeSession == null){
            throw new IllegalArgumentException("session Remove Failed");
        }
    }

    @Override
    public void broadcastChat(WebSocketSession webSocketSession, TextMessage textMessage) throws IOException {
        ChatDto chatDto = new ChatDto(webSocketSession.getId(), textMessage.getPayload());

        String chatDtoJson = objectMapper.writeValueAsString(chatDto);

        TextMessage newText = new TextMessage(chatDtoJson);
        for(WebSocketSession session : sessions.values()){
            if(session.isOpen()){
                session.sendMessage(newText);
            }
        }
    }

    // user session을 통해 find by name의형태로 찾아서 메시지 저장
    @Override
    public void saveMessage(WebSocketSession webSocketSession, TextMessage textMessage) {
        chatDao.insertChat(webSocketSession.getId(), textMessage.getPayload());
    }
}
