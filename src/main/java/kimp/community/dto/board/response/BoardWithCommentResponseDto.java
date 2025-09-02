package kimp.community.dto.board.response;

import kimp.community.dto.comment.response.ResponseCommentDto;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class BoardWithCommentResponseDto extends BoardResponseDto{

    List<ResponseCommentDto> comments;

    public BoardWithCommentResponseDto(Long boardId, Long memberId,Long categoryId,String categoryName, String memberNickName, String title, String content, Integer boardViewsCount, Integer boardLikesCount, LocalDateTime createdAt, LocalDateTime updatedAt, List<ResponseCommentDto> comments, Integer commentsCount, Boolean isPin) {
        super(boardId, memberId,categoryId,categoryName, memberNickName, title, content, boardViewsCount, boardLikesCount, createdAt, updatedAt, commentsCount, isPin);
        this.comments = comments;
    }
}
