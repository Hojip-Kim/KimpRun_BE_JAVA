package kimp.chat.service;

import kimp.chat.dto.request.DeleteAuthChatRequest;
import kimp.chat.dto.response.ChatLogResponseDto;
import kimp.chat.dto.vo.DeleteAnonChatMessage;
import kimp.chat.entity.Chat;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ChatService {

    public Page<ChatLogResponseDto> getChatMessages(int page, int size);

    public void softDeleteAnonMessage(String kimprunToken, DeleteAnonChatMessage deleteChatMessage);

    public void softDeleteAuthMessage(Long userId, DeleteAuthChatRequest deleteChatMessage);

    public void softDeleteAdminRole(DeleteAuthChatRequest deleteChatMessage);
}
