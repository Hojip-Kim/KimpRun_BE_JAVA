package kimp.chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DeleteAnonChatRequest {
    private String inherenceId;
    private String kimprunTokenUuId;

    public DeleteAnonChatRequest(String inherenceId, String kimprunTokenUuId) {
        this.inherenceId = inherenceId;
        this.kimprunTokenUuId = kimprunTokenUuId;
    }
}
