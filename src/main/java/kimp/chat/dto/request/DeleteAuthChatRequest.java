package kimp.chat.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeleteAuthChatRequest {
    @JsonProperty("inherenceId")
    private String inherenceId;

    public DeleteAuthChatRequest(String inherenceId) {
        this.inherenceId = inherenceId;
    }
}
