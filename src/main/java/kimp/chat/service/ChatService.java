package kimp.chat.service;

import kimp.chat.dto.ChatLogDTO;
import kimp.chat.entity.Chat;

import java.util.List;

public interface ChatService {

    public Chat createChat(String chatID, String content);

    public List<ChatLogDTO> getChatMessages(int page, int size);
}
