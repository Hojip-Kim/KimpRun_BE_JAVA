package kimp.chat.service;

import kimp.chat.dto.response.ChatLogResponseDto;
import kimp.chat.entity.Chat;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ChatService {

    public Page<ChatLogResponseDto> getChatMessages(int page, int size);
}
