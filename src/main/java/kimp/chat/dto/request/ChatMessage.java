package kimp.chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ChatMessage {

    private String chatID;
    private String content;
    private String authenticated;

    public ChatMessage(String chatId, String content, String authenticated) {
        this.chatID = chatId;
        this.content = content;
        this.authenticated = authenticated;
    }
}
