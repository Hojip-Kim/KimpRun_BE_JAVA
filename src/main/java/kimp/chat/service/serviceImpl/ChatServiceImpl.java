package kimp.chat.service.serviceImpl;

import kimp.chat.dao.ChatDao;
import kimp.chat.dto.request.DeleteAuthChatRequest;
import kimp.chat.dto.response.ChatLogResponseDto;
import kimp.chat.dto.vo.DeleteAnonChatMessage;
import kimp.chat.entity.Chat;
import kimp.chat.repository.ChatRepository;
import kimp.chat.service.ChatService;
import kimp.chat.service.ChatTrackingService;

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
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatDao chatDao;
    private final ChatRepository chatRepository;
    private final ChatTrackingService chatTrackingService;

    public ChatServiceImpl(ChatDao chatDao, ChatRepository chatRepository, ChatTrackingService chatTrackingService){
        this.chatDao = chatDao;
        this.chatRepository = chatRepository;
        this.chatTrackingService = chatTrackingService;
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

        // N+1 문제 방지를 위한 닉네임 배치 조회
        List<Long> memberIds = contentAsc.stream()
                .filter(chat -> chat.getAuthenticated() && chat.getUserId() != null)
                .map(Chat::getUserId)
                .distinct()
                .toList();
                
        List<String> uuids = contentAsc.stream()
                .filter(chat -> !chat.getAuthenticated() && chat.getCookiePayload() != null)
                .map(Chat::getCookiePayload)
                .distinct()
                .toList();
                
        Map<Long, String> memberNicknames = chatTrackingService.getNicknamesByMemberIds(memberIds);
        Map<String, String> guestNicknames = chatTrackingService.getNicknamesByUuids(uuids);
        
        // DTO 매핑
        List<ChatLogResponseDto> dtos = contentAsc.stream()
                .map(chat -> {
                    String nickname = null;
                    if (chat.getAuthenticated() && chat.getUserId() != null) {
                        nickname = memberNicknames.get(chat.getUserId());
                    } else if (!chat.getAuthenticated() && chat.getCookiePayload() != null) {
                        nickname = guestNicknames.get(chat.getCookiePayload());
                    }
                    
                    return new ChatLogResponseDto(
                            chat.getId(),
                            chat.getChatID(),
                            chat.getContent(),
                            chat.getAuthenticated(),
                            chat.getCookiePayload(),
                            IpMaskUtil.mask(chat.getUserIp()),
                            chat.getRegistedAt(),
                            chat.getInherenceId(),
                            chat.getUserId(),
                            nickname
                    );
                })
                .toList();

        return new PageImpl<>(dtos, chats.getPageable(), chats.getTotalElements());
    }

    @Override
    public Page<ChatLogResponseDto> getChatMessagesWithBlocked(int page, int size, List<String> blockedMembers, List<String> blockedGuests) {
        List<Long> blockedMemberIds = null;
        List<String> blockedGuestUuids = null;
        
        if (blockedMembers != null && !blockedMembers.isEmpty()) {
            blockedMemberIds = blockedMembers.stream()
                .filter(id -> id != null && !id.trim().isEmpty())
                .map(Long::valueOf)
                .collect(Collectors.toList());
        }
        
        if (blockedGuests != null && !blockedGuests.isEmpty()) {
            blockedGuestUuids = blockedGuests.stream()
                .filter(uuid -> uuid != null && !uuid.trim().isEmpty())
                .collect(Collectors.toList());
        }
        
        Page<Chat> chats = chatDao.getAllChatsWithBlocked(page, size, blockedMemberIds, blockedGuestUuids);
        if (chats == null || chats.isEmpty()) {
            throw new KimprunException(
                    KimprunExceptionEnum.INTERNAL_SERVER_ERROR,
                    "message가 비어있습니다.",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "ChatServiceImpl.getChatMessagesWithBlocked"
            );
        }

        List<Chat> contentAsc = new ArrayList<>(chats.getContent());
        contentAsc.sort(Comparator.comparing(Chat::getRegistedAt));

        // N+1 문제 방지를 위한 닉네임 배치 조회
        List<Long> memberIds = contentAsc.stream()
                .filter(chat -> chat.getAuthenticated() && chat.getUserId() != null)
                .map(Chat::getUserId)
                .distinct()
                .toList();
                
        List<String> uuids = contentAsc.stream()
                .filter(chat -> !chat.getAuthenticated() && chat.getCookiePayload() != null)
                .map(Chat::getCookiePayload)
                .distinct()
                .toList();
                
        Map<Long, String> memberNicknames = chatTrackingService.getNicknamesByMemberIds(memberIds);
        Map<String, String> guestNicknames = chatTrackingService.getNicknamesByUuids(uuids);

        List<ChatLogResponseDto> dtos = contentAsc.stream()
                .map(chat -> {
                    String nickname = null;
                    if (chat.getAuthenticated() && chat.getUserId() != null) {
                        nickname = memberNicknames.get(chat.getUserId());
                    } else if (!chat.getAuthenticated() && chat.getCookiePayload() != null) {
                        nickname = guestNicknames.get(chat.getCookiePayload());
                    }
                    
                    return new ChatLogResponseDto(
                            chat.getId(),
                            chat.getChatID(),
                            chat.getContent(),
                            chat.getAuthenticated(),
                            chat.getCookiePayload(),
                            IpMaskUtil.mask(chat.getUserIp()),
                            chat.getRegistedAt(),
                            chat.getInherenceId(),
                            chat.getUserId(),
                            nickname
                    );
                })
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
