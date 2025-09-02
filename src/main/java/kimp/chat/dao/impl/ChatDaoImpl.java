package kimp.chat.dao.impl;

import kimp.chat.dao.ChatDao;
import kimp.chat.entity.Chat;
import kimp.chat.repository.ChatRepository;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


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
    public Chat insertChat(String nickname, String content, Boolean authenticated, String userIp, String cookiePayload, String inherenceId, Boolean isDeleted, Long memberId) {
        Chat chat = new Chat(nickname, content, authenticated, userIp, cookiePayload, inherenceId, isDeleted);
        chat.setUser(memberId);

        return chatRepository.insert(chat);
    }

    @Override
    public Chat findByInherenceId(String inherenceId) {
        Optional<Chat> chat = chatRepository.findOneByInherenceId(inherenceId);

        if(!chat.isPresent()) {
            throw new KimprunException(KimprunExceptionEnum.REQUEST_ACCEPTED, "Not have matched chat data with InherenceId : " + inherenceId, HttpStatus.ACCEPTED, "ChatDaoImpl.findByInherenceId");
        }

        return chat.get();
    }

    @Override
    public Page<Chat> getAllChats(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("registed_at").descending());
        // isDeleted가 false인것만 가져옴 (softDelete)
        Page<Chat> chatMessages = chatRepository.findAllByIsDeletedFalseOrderByRegistedAtDesc(pageable);
        if(chatMessages == null || chatMessages.isEmpty()){
            throw new IllegalArgumentException("Not found any chats");
        }

        return chatMessages;
    }

    @Override
    public Page<Chat> getAllChatsWithBlocked(int page, int size, List<Long> blockedMemberIds, List<String> blockedGuestUuids) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("registed_at").descending());
        
        Page<Chat> chatMessages = chatRepository.findAllByIsDeletedFalseAndNotBlockedOrderByRegistedAtDesc(
            blockedMemberIds != null ? blockedMemberIds : List.of(),
            blockedGuestUuids != null ? blockedGuestUuids : List.of(),
            pageable
        );
        
        if(chatMessages == null || chatMessages.isEmpty()){
            throw new IllegalArgumentException("Not found any chats");
        }

        return chatMessages;
    }

}
