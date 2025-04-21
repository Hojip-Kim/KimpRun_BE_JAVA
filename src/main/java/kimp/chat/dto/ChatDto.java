package kimp.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatDto {

    private String chatID;
    private String content;
    private String authenticated;

    public ChatDto(String chatID, String content, String authenticated) {
        this.chatID = chatID;
        this.content = content;
        this.authenticated = authenticated;
    }
}
