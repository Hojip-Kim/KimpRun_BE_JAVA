package kimp.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ChatLogResponseDto {
    @JsonProperty("chatId")
    private String chatID;
    @JsonProperty("content")
    private String content;
    @JsonProperty("authenticated")
    private Boolean authenticated;
    @JsonProperty("uuid")
    private String uuid;
    @JsonProperty("userIp")
    private String userIp;
    @JsonProperty("registedAt")
    private LocalDateTime registedAt;

    public ChatLogResponseDto(String chatID, String content, Boolean authenticated, String uuid, String userIp, LocalDateTime registedAt ) {
        this.chatID = chatID;
        this.content = content;
        this.authenticated = authenticated;
        this.uuid = uuid;
        this.userIp = userIp;
        this.registedAt = registedAt;
    }
}
