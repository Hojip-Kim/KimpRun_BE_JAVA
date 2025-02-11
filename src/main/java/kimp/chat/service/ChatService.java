package kimp.chat.service;

import kimp.chat.dto.response.ChatLogResponseDto;
import kimp.chat.entity.Chat;

import java.util.List;

public interface ChatService {

    public List<Chat> getChatMessages(int page, int size);

    public List<ChatLogResponseDto> convertChatLogToDto(List<Chat> chatList);
}
