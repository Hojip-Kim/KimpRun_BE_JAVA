package kimp.chat.service;

import kimp.chat.dto.response.ChatLogResponseDto;
import kimp.chat.vo.DeleteAdminChatVo;
import kimp.chat.vo.DeleteAnonChatVo;
import kimp.chat.vo.DeleteAuthChatVo;
import kimp.chat.vo.GetChatMessagesVo;
import kimp.chat.vo.GetChatMessagesWithBlockedVo;
import org.springframework.data.domain.Page;

public interface ChatService {

    public Page<ChatLogResponseDto> getChatMessages(GetChatMessagesVo vo);

    public Page<ChatLogResponseDto> getChatMessagesWithBlocked(GetChatMessagesWithBlockedVo vo);

    public void softDeleteAnonMessage(DeleteAnonChatVo vo);

    public void softDeleteAuthMessage(DeleteAuthChatVo vo);

    public void softDeleteAdminRole(DeleteAdminChatVo vo);
}
