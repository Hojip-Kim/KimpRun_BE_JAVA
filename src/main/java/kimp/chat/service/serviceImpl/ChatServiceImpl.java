package kimp.chat.service.serviceImpl;

import kimp.chat.dao.ChatDao;
import kimp.chat.dto.request.ChatMessage;
import kimp.chat.dto.response.ChatLogResponseDto;
import kimp.chat.entity.Chat;
import kimp.chat.service.ChatService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatDao chatDao;

    public ChatServiceImpl(ChatDao chatDao){
        this.chatDao = chatDao;
    }

    @Override
    public List<Chat> getChatMessages(int page, int size) {
        return chatDao.getAllChats(page, size);
    }

    @Override
    public List<ChatLogResponseDto> convertChatLogToDto(List<Chat> chatList){

        List<ChatLogResponseDto> responseDtos = chatList.stream()
                .map(chat -> new ChatLogResponseDto(chat.getChatID(), chat.getContent(), chat.getAuthenticated()))
                .collect(Collectors.toList());

        return responseDtos;
    }



}
