package kimp.chat.service.serviceImpl;

import kimp.chat.dao.impl.ChatDaoImpl;
import kimp.chat.dto.response.ChatLogResponseDto;
import kimp.chat.entity.Chat;
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

    private final ChatDaoImpl chatDao;

    public ChatServiceImpl(ChatDaoImpl chatDao){
        this.chatDao = chatDao;
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
                        chat.getChatID(),
                        chat.getContent(),
                        chat.getAuthenticated(),
                        chat.getCookiePayload(),
                        IpMaskUtil.mask(chat.getUserIp()),
                        chat.getRegistedAt()
                ))
                .toList();

        return new PageImpl<>(dtos, chats.getPageable(), chats.getTotalElements());
    }
}
