package kimp.community.dto.board.response;

import kimp.community.dto.comment.response.ResponseCommentDto;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class BoardWithCommentResponseDto extends BoardResponseDto{

    List<ResponseCommentDto> comments;

    public BoardWithCommentResponseDto(Long boardId, Long userId, String userNickName, String title, String content, Integer boardViewsCount, Integer boardLikesCount, LocalDateTime createdAt, LocalDateTime updatedAt, List<ResponseCommentDto> comments) {
        super(boardId, userId, userNickName, title, content, boardViewsCount, boardLikesCount, createdAt, updatedAt);
        this.comments = comments;
    }
}
