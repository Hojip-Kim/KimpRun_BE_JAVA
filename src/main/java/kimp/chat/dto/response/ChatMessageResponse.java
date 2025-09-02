package kimp.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kimp.chat.dto.request.ChatMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ChatMessageResponse extends ChatMessage {
    @JsonProperty("inherenceId")
    private String chatInherenceId;
    @JsonProperty("isDeleted")
    private Boolean isDeleted;
    @JsonProperty("memberId")
    private Long memberId;


    public ChatMessageResponse(boolean ping, String chatId, String content, Boolean authenticated, String userIp, String uuid, String chatInherenceId, Boolean isDeleted, Long memberId) {
        super(ping, chatId, content, authenticated, userIp, uuid);
        this.chatInherenceId = chatInherenceId;
        this.isDeleted = isDeleted;
        this.memberId = memberId;
    }

    public void setIp(String ip) {
        super.setIp(ip);
    }

}
