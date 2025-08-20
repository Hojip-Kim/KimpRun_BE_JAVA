package kimp.chat.dao.impl;

import kimp.chat.dao.ChatDao;
import kimp.chat.entity.Chat;
import kimp.chat.repository.ChatRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;


@Repository
public class ChatDaoImpl implements ChatDao {

    private final ChatRepository chatRepository;

    public ChatDaoImpl(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

//    public Chat insertChat(String nickname, String content, String authenticated) {
//        Chat chat = new Chat(nickname, content, authenticated);
//
//        return chatRepository.insert(chat);
//    }

    @Override
    public Chat insertChat(String nickname, String content, Boolean authenticated, String userIp, String cookiePayload) {
        Chat chat = new Chat(nickname, content, authenticated, userIp, cookiePayload);

        return chatRepository.insert(chat);
    }

    @Override
    public Page<Chat> getAllChats(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("registed_at").descending());
        Page<Chat> chatMessages = chatRepository.findAllByOrderByRegistedAtDesc(pageable);
        if(chatMessages == null || chatMessages.isEmpty()){
            throw new IllegalArgumentException("Not found any chats");
        }

        return chatMessages;
    }
}
