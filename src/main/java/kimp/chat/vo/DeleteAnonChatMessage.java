package kimp.chat.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DeleteAnonChatMessage {
    private String inherenceId;

    public DeleteAnonChatMessage(String inherenceId, String kimprunTokenUuId) {
        this.inherenceId = inherenceId;
    }
}
