package kimp.chat.dao;

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

    public Chat insertChat(String nickname, String content, String authenticated) {
        Chat chat = new Chat(nickname, content, authenticated);

        return chatRepository.insert(chat);
    }

    public List<Chat> getAllChats(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("registed_at").descending());
        List<Chat> chatMessages = chatRepository.findAllByOrderByRegistedAtAsc(pageable);
        if(chatMessages == null || chatMessages.isEmpty()){
            throw new IllegalArgumentException("Not found any chats");
        }

        Collections.reverse(chatMessages);
        return chatMessages;
    }
}
