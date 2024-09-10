package kimp.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChatLogResponseDto {
    private String chatID;
    private String content;

}
