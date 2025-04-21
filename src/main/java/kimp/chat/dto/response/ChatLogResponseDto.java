package kimp.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChatLogResponseDto {
    private String chatID;
    private String content;
    @JsonProperty("authenticated")
    private String authenticated;

}
