package kimp.chat.dao.daoImpl;

import kimp.chat.entity.Chat;
import kimp.chat.repository.ChatRepository;
import org.springframework.stereotype.Repository;


@Repository
public class ChatDao {


    private final ChatRepository chatRepository;

    public ChatDao(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public Chat insertChat(String chatId, String content) {
        Chat chat = new Chat(chatId, content);

        return chatRepository.insert(chat);
    }
}
