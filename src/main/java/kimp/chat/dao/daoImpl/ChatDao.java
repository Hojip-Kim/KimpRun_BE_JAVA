package kimp.chat.dao.daoImpl;

import kimp.chat.dto.response.ChatLogResponseDto;
import kimp.chat.entity.Chat;
import kimp.chat.repository.ChatRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;


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

    public List<ChatLogResponseDto> getAllChats(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("registed_at").descending());
        List<ChatLogResponseDto> chatMessages = chatRepository.findAllByOrderByRegisted_atAsc(pageable);
        if(chatMessages == null || chatMessages.isEmpty()){
            throw new IllegalArgumentException("Not found any chats");
        }

        Collections.reverse(chatMessages);
        return chatMessages;
    }
}
