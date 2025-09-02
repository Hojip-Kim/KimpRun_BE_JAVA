package kimp.community.dto.board.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class BoardLikeRequest {
    private Long boardId;

    public BoardLikeRequest(Long boardId) {
        this.boardId = boardId;
    }
}
