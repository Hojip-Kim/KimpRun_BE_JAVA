package kimp.chat.service.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.chat.dao.ChatDao;
import kimp.chat.dto.ChatDto;
import kimp.chat.dto.request.ChatMessage;
import kimp.chat.service.ChatWebsocketService;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import org.springframework.http.HttpStatus;
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
        if(sessions.containsKey(webSocketSession.getId())){
            throw new KimprunException(KimprunExceptionEnum.WEBSOCKET_SESSION_EXCEPTION, "Session already exists in map: " + webSocketSession.getId(), HttpStatus.BAD_REQUEST, "ChatWebsocketServiceImpl.sessionInput");
        }

        sessions.put(webSocketSession.getId(), webSocketSession);

    }

    @Override
    public void sessionClose(WebSocketSession session){
        if(!sessions.containsKey(session.getId())){
            throw new KimprunException(KimprunExceptionEnum.WEBSOCKET_SESSION_EXCEPTION, "Session not found in map: " + session.getId(), HttpStatus.BAD_REQUEST, "ChatWebsocketServiceImpl.sessionClose");
        }

        WebSocketSession removeSession = sessions.remove(session.getId());

        if(removeSession == null){
            throw new KimprunException(KimprunExceptionEnum.WEBSOCKET_SESSION_EXCEPTION, "Failed to remove session from map", HttpStatus.INTERNAL_SERVER_ERROR, "ChatWebsocketServiceImpl.sessionClose");
        }
    }

    @Override
    public void broadcastChat(WebSocketSession webSocketSession, ChatMessage chatMessage) throws IOException {
        ChatDto chatDto = new ChatDto(chatMessage.getChatID(), chatMessage.getContent(), chatMessage.getAuthenticated());

        String chatDtoJson = objectMapper.writeValueAsString(chatDto);

        if(chatDtoJson.isEmpty()){
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, "Chat DTO JSON is empty", HttpStatus.INTERNAL_SERVER_ERROR, "ChatWebsocketServiceImpl.broadcastChat");
        }
        TextMessage newText = new TextMessage(chatDtoJson);

        for(WebSocketSession session : sessions.values()){
            if(session.isOpen()){
                session.sendMessage(newText);
            }
        }
    }

    // member session을 통해 find by name의형태로 찾아서 메시지 저장
    @Override
    public void saveMessage(ChatMessage chatMessage) {
        if(chatMessage.getContent().length() == 0){
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Chat message content cannot be empty", HttpStatus.BAD_REQUEST, "ChatWebsocketServiceImpl.saveMessage");
        }
        chatDao.insertChat(chatMessage.getChatID(), chatMessage.getContent(), chatMessage.getAuthenticated());
    }
}
