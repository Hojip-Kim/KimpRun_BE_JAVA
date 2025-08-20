package kimp.chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ChatMessage {

    private Boolean ping;
    private String chatID;
    private String content;
    private Boolean authenticated;
    private String userIp;
    private String uuid;

    public ChatMessage(boolean ping, String chatId, String content, Boolean authenticated) {
        this.ping = ping;
        this.chatID = chatId;
        this.content = content;
        this.authenticated = authenticated;
    }

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
}
