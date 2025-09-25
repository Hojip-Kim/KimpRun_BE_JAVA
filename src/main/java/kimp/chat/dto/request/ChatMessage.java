package kimp.chat.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ChatMessage {

    @JsonProperty("ping")
    private Boolean ping;
    @JsonProperty("chatId")
    private String chatID;
    @JsonProperty("content")
    private String content;
    @JsonProperty("authenticated")
    private Boolean authenticated;
    @JsonProperty("userIp")
    private String userIp;
    @JsonProperty("registedAt")
    private LocalDateTime registedAt;
    @JsonProperty("uuid")
    private String uuid;
    @JsonProperty("memberId")
    private Long memberId;

    public ChatMessage(boolean ping, String chatId, String content, Boolean authenticated, String userIp, String uuid) {
        this.ping = ping;
        this.chatID = chatId;
        this.content = content;
        this.authenticated = authenticated;
        this.userIp = userIp;
        this.uuid = uuid;
    }

    public Boolean isPing() {
        return ping;
    }

    public void setIp(String userIp) {
        this.userIp = userIp;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
    }

    public void setCreatedAt(LocalDateTime registedAt) {
        this.registedAt = registedAt;
    }
}
