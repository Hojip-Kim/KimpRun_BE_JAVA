package kimp.chat.service;

import kimp.chat.entity.Chat;

public interface ChatService {

    public Chat createChat(String chatID, String content);
}
