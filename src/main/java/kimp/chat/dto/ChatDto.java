package kimp.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatDto {

    private String chatId;
    private String content;
    private Boolean authenticated;
    private String userIp;
    private String uuid;
    private LocalDateTime registedAt;

    public ChatDto(String chatId, String content, Boolean authenticated, String userIp, String uuid, LocalDateTime registedAt) {
        this.chatId = chatId;
        this.content = content;
        this.authenticated = authenticated;
        this.userIp = userIp;
        this.uuid = uuid;
        this.registedAt = registedAt;
    }
}
