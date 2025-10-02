package kimp.chat.dto.vo;

import kimp.chat.dto.response.ChatMessageResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SaveChatMessage {
    private Boolean ping;
    private String chatID;
    private String content;
    private Boolean authenticated;
    private String userIp;
    private String uuid;
    private String inherienceId;
    private Boolean isDeleted;
    private Long memberId;

    public SaveChatMessage(Boolean ping, String chatID, String content, Boolean authenticated, String userIp, String uuid, String inherienceId, Boolean isDeleted, Long memberId) {
        this.ping = ping;
        this.chatID = chatID;
        this.content = content;
        this.authenticated = authenticated;
        this.userIp = userIp;
        this.uuid = uuid;
        this.inherienceId = inherienceId;
        this.isDeleted = isDeleted;
        this.memberId = memberId;
    }

    public ChatMessageResponse toResponse() {
        return ChatMessageResponse.builder()
                .ping(getPing())
                .chatId(getChatID())
                .content(getContent())
                .authenticated(getAuthenticated())
                .userIp(getUserIp())
                .uuid(getUuid())
                .chatInherenceId(getInherienceId())
                .isDeleted(getIsDeleted())
                .memberId(getMemberId())
                .build();
    }

}
