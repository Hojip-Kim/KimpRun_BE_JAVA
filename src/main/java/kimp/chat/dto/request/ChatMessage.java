package kimp.chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ChatMessage {

    private boolean ping;
    private String chatID;
    private String content;
    private String authenticated;

    public ChatMessage(boolean ping, String chatId, String content, String authenticated) {
        this.ping = ping;
        this.chatID = chatId;
        this.content = content;
        this.authenticated = authenticated;
    }
}
