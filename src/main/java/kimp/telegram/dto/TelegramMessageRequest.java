package kimp.telegram.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelegramMessageRequest {
    
    @JsonProperty("chat_id")
    private String chatId;
    
    private String text;
    
    @JsonProperty("parse_mode")
    private String parseMode = "Markdown";
}