package kimp.chat.service.serviceImpl;

import kimp.chat.dao.ChatDao;
import kimp.chat.dto.request.DeleteAuthChatRequest;
import kimp.chat.dto.response.ChatLogResponseDto;
import kimp.chat.dto.vo.DeleteAnonChatMessage;
import kimp.chat.entity.Chat;
import kimp.chat.repository.ChatRepository;
import kimp.chat.service.ChatService;

import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.util.IpMaskUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatDao chatDao;
    private final ChatRepository chatRepository;

    public ChatServiceImpl(ChatDao chatDao, ChatRepository chatRepository){
        this.chatDao = chatDao;
        this.chatRepository = chatRepository;
    }

    @Override
    public Page<ChatLogResponseDto> getChatMessages(int page, int size) {
        Page<Chat> chats = chatDao.getAllChats(page, size);
        if (chats == null || chats.isEmpty()) {
            throw new KimprunException(
                    KimprunExceptionEnum.INTERNAL_SERVER_ERROR,
                    "message가 비어있습니다.",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "ChatServiceImpl.getChatMessages"
            );
        }

        // 페이지의 콘텐츠만 ASC로 재정렬
        List<Chat> contentAsc = new ArrayList<>(chats.getContent());
        contentAsc.sort(Comparator.comparing(Chat::getRegistedAt)); // ASC

        // DTO 매핑
        List<ChatLogResponseDto> dtos = contentAsc.stream()
                .map(chat -> new ChatLogResponseDto(
                        chat.getId(),
                        chat.getChatID(),
                        chat.getContent(),
                        chat.getAuthenticated(),
                        chat.getCookiePayload(),
                        IpMaskUtil.mask(chat.getUserIp()),
                        chat.getRegistedAt(),
                        chat.getInherenceId(),
                        chat.getUserId()
                ))
                .toList();

        return new PageImpl<>(dtos, chats.getPageable(), chats.getTotalElements());
    }


    @Override
    public void softDeleteAnonMessage(String kimprunToken, DeleteAnonChatMessage deleteChatMessage) {
        String inherenceId = deleteChatMessage.getInherenceId();

        Chat foundChat = chatDao.findByInherenceId(inherenceId);
        if(!foundChat.getCookiePayload().equals(kimprunToken)){
            throw new KimprunException(KimprunExceptionEnum.INVALID_REQUEST_EXCEPTION, "Not matched kimprun-token with chat",  HttpStatus.BAD_REQUEST, "ChatStompServiceImpl.softDeleteAnonMessage");
        }

        foundChat.softDeleteChat();
        chatRepository.save(foundChat);
    }

    @Override
    public void softDeleteAuthMessage(Long userId, DeleteAuthChatRequest deleteChatMessage) {
        Chat foundByInherenceId = chatDao.findByInherenceId(deleteChatMessage.getInherenceId());
        if(!foundByInherenceId.getUserId().equals(userId)){
            throw new KimprunException(KimprunExceptionEnum.INVALID_REQUEST_EXCEPTION, "찾은 chat의 작성 유저 : " + foundByInherenceId.getUserId() + "요청한 유저 : " + userId, HttpStatus.BAD_REQUEST, "ChatStompServiceImpl.softDeleteAuthMessage");
        }
        foundByInherenceId.softDeleteChat();
        chatRepository.save(foundByInherenceId);
    }

    @Override
    public void softDeleteAdminRole(DeleteAuthChatRequest deleteChatMessage) {
        Chat foundChat = chatDao.findByInherenceId(deleteChatMessage.getInherenceId());
        foundChat.softDeleteChat();
        chatRepository.save(foundChat);
    }
}
