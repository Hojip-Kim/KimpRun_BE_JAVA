package kimp.chat.dao;

import kimp.chat.entity.Chat;
import org.springframework.data.domain.Page;

public interface ChatDao {

    public Chat insertChat(String nickname, String content, Boolean authenticated, String userIp, String cookiePayload);

    public Page<Chat> getAllChats(int page, int size);
}
