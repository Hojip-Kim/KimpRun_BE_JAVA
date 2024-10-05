package kimp.chat.service;

import kimp.chat.dto.response.ChatLogResponseDto;
import kimp.chat.entity.Chat;

import java.util.List;

public interface ChatService {

    public Chat createChat(String chatID, String content);

    public List<Chat> getChatMessages(int page, int size);

    public List<ChatLogResponseDto> convertChatLogToDto(List<Chat> chatList);
}
