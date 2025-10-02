package kimp.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ChatLogResponseDto {
    @JsonProperty
    private String id;
    @JsonProperty("chatId")
    private String chatId;
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
    @JsonProperty("inherenceId")
    private String inherenceId;
    // 삭제된 채팅메시지는 아예 가질않으니 isDeleted는 없어도 됨.
    @JsonProperty("memberId")
    private Long memberId;
    @JsonProperty("nickname")
    private String nickname;
}
