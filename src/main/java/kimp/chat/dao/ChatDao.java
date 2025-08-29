package kimp.chat.dao;

import kimp.chat.entity.Chat;
import org.springframework.data.domain.Page;

public interface ChatDao {

    public Chat insertChat(String nickname, String content, Boolean authenticated, String userIp, String cookiePayload, String inherenceId, Boolean isDeleted, Long memberId);

    public Chat findByInherenceId(String inherenceId);

    public Page<Chat> getAllChats(int page, int size);

}
